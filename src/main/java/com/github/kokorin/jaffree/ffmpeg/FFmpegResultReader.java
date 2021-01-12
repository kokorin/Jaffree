/*
 *    Copyright  2017 Denis Kokorin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.SizeUnit;
import com.github.kokorin.jaffree.process.StdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * {@link FFmpegResultReader} reads ffmpeg stderr output, parses {@link FFmpegProgress} and
 * {@link FFmpegResult} and passes unparsed output to {@link OutputListener} (if provided).
 */
public class FFmpegResultReader implements StdReader<FFmpegResult> {
    private final ProgressListener progressListener;
    private final OutputListener outputListener;

    private static final double PERCENTS_TO_RATIO_MULTIPLIER = 0.01;
    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegResultReader.class);

    /**
     * Creates {@link FFmpegResultReader}.
     *
     * @param progressListener progress listener
     * @param outputListener   output listener
     */
    public FFmpegResultReader(final ProgressListener progressListener,
                              final OutputListener outputListener) {
        this.progressListener = progressListener;
        this.outputListener = outputListener;
    }

    /**
     * Reads provided {@link InputStream} until it's depleted.
     * <p>
     * This method parses every line to check if it is progress report, ffmpeg result report,
     * output message or error message.
     *
     * @param stdOut input stream to read from
     * @return FFmpegResult if found
     * @throws RuntimeException if IOException appears or ffmpeg ends with error message.
     */
    @Override
    public FFmpegResult read(final InputStream stdOut) {
        //just read stdOut fully
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdOut));
        String errorMessage = null;

        String line;
        FFmpegResult result = null;

        try {
            while ((line = reader.readLine()) != null) {
                LOGGER.debug(line);
                FFmpegProgress progress = parseProgress(line);
                if (progress != null) {
                    if (progressListener != null) {
                        progressListener.onProgress(progress);
                    }
                    errorMessage = null;
                    continue;
                }

                FFmpegResult possibleResult = parsResult(line);

                if (possibleResult != null) {
                    result = possibleResult;
                    errorMessage = null;
                    continue;
                }

                if (outputListener != null) {
                    boolean errorLine = outputListener.onOutput(line);

                    if (!errorLine) {
                        continue;
                    }
                }

                if (result != null) {
                    continue;
                }

                errorMessage = line;
            }
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading ffmpeg output", e);
        }

        if (errorMessage != null) {
            throw new RuntimeException("ffmpeg exited with message: " + errorMessage);
        }

        return result;
    }

    static FFmpegProgress parseProgress(final String value) {
        if (value == null) {
            return null;
        }

        try {
            // Replace "frame=  495 fps= 89" with "frame=495 fps=89"
            String valueWithoutSpaces = value.replaceAll("= +", "=");
            Map<String, String> map = parseKeyValues(valueWithoutSpaces, "=");

            Long frame = parseLong(map.get("frame"));
            Double fps = parseDouble(map.get("fps"));
            Double q = parseDouble(map.get("q"));
            Long size = parseSizeInBytes(map.get("Lsize"));
            Long timeMillis = parseTimeInMillis(map.get("time"));
            Long dup = parseLong(map.get("dup"));
            Long drop = parseLong(map.get("drop"));
            Double bitrate = parseBitrateInKBits(map.get("bitrate"));
            Double speed = parseSpeed(map.get("speed"));

            if (hasNonNull(frame, fps, q, size, timeMillis, dup, drop, bitrate, speed)) {
                return new FFmpegProgress(
                        frame, fps, q, size, timeMillis, dup, drop, bitrate, speed
                );
            }
        } catch (Exception e) {
            // suppress
        }

        return null;
    }


    static FFmpegResult parsResult(final String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            String valueWithoutSpaces = value
                    .replaceAll("other streams", "other_streams")
                    .replaceAll("global headers", "global_headers")
                    .replaceAll("muxing overhead", "muxing_overhead")
                    .replaceAll(":\\s+", ":");

            Map<String, String> map = parseKeyValues(valueWithoutSpaces, ":");


            Long videoSize = parseSizeInBytes(map.get("video"));
            Long audioSize = parseSizeInBytes(map.get("audio"));
            Long subtitleSize = parseSizeInBytes(map.get("subtitle"));
            Long otherStreamsSize = parseSizeInBytes(map.get("other_streams"));
            Long globalHeadersSize = parseSizeInBytes(map.get("global_headers"));
            Double muxOverhead = parseRatio(map.get("muxing_overhead"));

            if (hasNonNull(videoSize, audioSize, subtitleSize, otherStreamsSize, globalHeadersSize,
                    muxOverhead)) {
                return new FFmpegResult(videoSize, audioSize, subtitleSize, otherStreamsSize,
                        globalHeadersSize, muxOverhead);
            }
        } catch (Exception e) {
            // supress
        }

        return null;
    }

    private static Map<String, String> parseKeyValues(final String value, final String separator) {
        Map<String, String> result = new HashMap<>();

        for (String pair : value.split("\\s+")) {
            String[] nameAndValue = pair.split(separator);

            if (nameAndValue.length != 2) {
                continue;
            }

            result.put(nameAndValue[0], nameAndValue[1]);
        }

        return result;
    }

    private static Long parseLong(final String value) {
        if (value != null && !value.isEmpty()) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                // Suppress
            }
        }

        return null;
    }

    private static Double parseDouble(final String value) {
        if (value != null && !value.isEmpty()) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // Suppress
            }
        }

        return null;
    }

    private static Long parseSizeInBytes(final String value) {
        return parseSize(value, SizeUnit.B);
    }

    private static Long parseSize(final String value, final SizeUnit unit) {
        String[] sizeAndUnit = splitValueAndUnit(value);
        Long parsedValue = parseLong(sizeAndUnit[0]);
        if (parsedValue == null) {
            return null;
        }

        SizeUnit valueUnit = parseSizeUnit(sizeAndUnit[1]);
        if (valueUnit == null) {
            return null;
        }

        return valueUnit.convertTo(parsedValue, unit);
    }

    private static Double parseBitrateInKBits(final String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        // TODO show warning if value ends not with kbits/s
        String numericValue = value.replace("kbits/s", "");

        return parseDouble(numericValue);
    }

    private static Double parseRatio(final String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String numericValue = value;
        double multiplier = 1;
        if (value.endsWith("%")) {
            numericValue = value.substring(0, value.length() - 1);
            multiplier = PERCENTS_TO_RATIO_MULTIPLIER;
        }

        Double valueDouble = parseDouble(numericValue);
        if (valueDouble == null) {
            return null;
        }

        return multiplier * valueDouble;
    }

    private static Long parseTimeInMillis(final String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        final int expectedParts = 3;
        String[] timeParts = value.split(":");
        if (timeParts.length != expectedParts) {
            return null;
        }

        Long hours = parseLong(timeParts[0]);
        Long minutes = parseLong(timeParts[1]);
        Double seconds = parseDouble(timeParts[2]);

        if (hours == null || minutes == null || seconds == null) {
            return null;
        }
        return TimeUnit.HOURS.toMillis(hours)
                + TimeUnit.MINUTES.toMillis(minutes)
                + (long) (TimeUnit.SECONDS.toMillis(1) * seconds);
    }

    private static Double parseSpeed(final String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String numericValue = value;
        if (value.endsWith("x")) {
            numericValue = value.substring(0, value.length() - 1);
        }

        return parseDouble(numericValue);
    }

    private static String[] splitValueAndUnit(final String string) {
        if (string == null) {
            return new String[]{"", ""};
        }

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if ((c < '0' || c > '9') && c != '.') {
                return new String[]{string.substring(0, i), string.substring(i)};
            }
        }
        return new String[]{string, ""};
    }

    private static SizeUnit parseSizeUnit(final String value) {
        for (SizeUnit unit : SizeUnit.values()) {
            if (unit.name().equalsIgnoreCase(value)) {
                return unit;
            }
        }

        return null;
    }

    private static boolean hasNonNull(final Object... items) {
        for (Object item : items) {
            if (item != null) {
                return true;
            }
        }

        return false;
    }
}
