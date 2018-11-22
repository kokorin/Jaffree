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

public class FFmpegResultReader implements StdReader<FFmpegResult> {
    private final ProgressListener progressListener;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegResultReader.class);

    public FFmpegResultReader(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public FFmpegResult read(InputStream stdOut) {
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

                if (line.startsWith("[")) {
                    // After encoding has ended ffmpeg adds extra codec-specific data like following
                    // [libx264 @ 00000000012057c0] frame I:17    Avg QP:19.81  size:  2020
                    continue;
                }

                if (result != null) {
                    continue;
                }
                errorMessage = line;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (errorMessage != null) {
            throw new RuntimeException(errorMessage);
        }

        return result;
    }

    static FFmpegProgress parseProgress(String value) {
        if (value == null) {
            return null;
        }

        try {
            Map<String, String> map = new HashMap<>();
            // Replace "frame=  495 fps= 89" with "frame=495 fps=89"
            value = value.replaceAll("= +", "=");
            for (String pair : value.split(" +")) {
                String[] nameAndValue = pair.split("=");

                if (nameAndValue.length != 2) {
                    continue;
                }

                map.put(nameAndValue[0], nameAndValue[1]);
            }

            long frame = parseLong(map.get("frame"), 0);
            double fps = parseDouble(map.get("fps"), Double.NaN);
            double q = parseDouble(map.get("q"), Double.NaN);

            long size = parseSizeInBytes(map.get("Lsize"));

            long time = 0;
            String timeStr = map.get("time");
            if (timeStr != null) {
                String[] timeParts = timeStr.split(":");
                if (timeParts.length == 3) {
                    long hours = parseLong(timeParts[0], 0);
                    long minutes = parseLong(timeParts[1], 0);
                    double seconds = parseDouble(timeParts[2], 0);
                    time = (long) (((hours * 60 + minutes) * 60 + seconds) * 1000);
                }
            }

            String bitrateStr = map.get("bitrate");
            String[] bitrateAndUnit = splitValueAndUnit(bitrateStr);
            double bitrate = Double.NaN;
            if (bitrateAndUnit[1].equals("kbits/s")) {
                bitrate = parseDouble(bitrateAndUnit[0], Double.NaN);
            }

            long dup = parseLong(map.get("dup"), 0);
            long drop = parseLong(map.get("drop"), 0);

            String speedStr = map.get("speed");
            if (speedStr != null && speedStr.endsWith("x")) {
                speedStr = speedStr.substring(0, speedStr.length() - 1);
            }
            double speed = parseDouble(speedStr, Double.NaN);

            if (frame != 0 || !Double.isNaN(fps) || !Double.isNaN(q) || size != 0
                    || time != 0 || !Double.isNaN(bitrate) || !Double.isNaN(speed)) {
                return new FFmpegProgress(frame, fps, q, size, time, dup, drop, bitrate, speed);
            }
        } catch (Exception e) {
            // suppress
        }

        return null;
    }


    static FFmpegResult parsResult(String value) {
        if (value == null) {
            return null;
        }

        value = value
                .replaceAll("other streams", "other_streams")
                .replaceAll("global headers", "global_headers")
                .replaceAll("muxing overhead", "muxing_overhead")
                .replaceAll(":\\s+", ":");
        try {
            Map<String, String> map = new HashMap<>();
            for (String keyValueStr : value.split("\\s+")) {
                String[] keyValuePair = keyValueStr.split(":");

                if (keyValuePair.length != 2) {
                    continue;
                }

                map.put(keyValuePair[0], keyValuePair[1]);
            }

            long videoSize = parseSizeInBytes(map.get("video"));
            long audioSize = parseSizeInBytes(map.get("audio"));
            long subtitleSize = parseSizeInBytes(map.get("subtitle"));
            long otherStreamsSize = parseSizeInBytes(map.get("other_streams"));
            long globalHeadersSize = parseSizeInBytes(map.get("global_headers"));

            String muxOverhead = map.get("muxing_overhead");
            if (muxOverhead != null && muxOverhead.endsWith("%")) {
                muxOverhead = muxOverhead.substring(0, muxOverhead.length() - 1);
            }
            double muxingOverheadRatio = parseDouble(muxOverhead, 0.) * 0.01;

            if (videoSize != 0 || audioSize != 0 || subtitleSize != 0 || otherStreamsSize != 0
                    || globalHeadersSize != 0 || muxingOverheadRatio != 0) {
                return new FFmpegResult(videoSize, audioSize, subtitleSize, otherStreamsSize, globalHeadersSize, muxingOverheadRatio);
            }
        } catch (Exception e) {
            // supress
        }

        return null;
    }


    private static long parseLong(String value, long defValue) {
        if (value != null && !value.isEmpty()) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                // Suppress
            }
        }

        return defValue;
    }

    private static double parseDouble(String value, double defValue) {
        if (value != null && !value.isEmpty()) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // Suppress
            }
        }

        return defValue;
    }

    private static long parseSizeInBytes(String value) {
        String[] sizeAndUnit = splitValueAndUnit(value);
        long parsedValue = parseLong(sizeAndUnit[0], 0);
        SizeUnit unit = parseSizeUnit(sizeAndUnit[1]);
        return unit.toBytes(parsedValue);
    }

    private static String[] splitValueAndUnit(String string) {
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

    private static SizeUnit parseSizeUnit(String value) {
        for (SizeUnit unit : SizeUnit.values()) {
            if (unit.name().equalsIgnoreCase(value)) {
                return unit;
            }
        }

        return SizeUnit.K;
    }
}
