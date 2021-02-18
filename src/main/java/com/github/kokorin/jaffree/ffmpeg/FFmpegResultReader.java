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

import com.github.kokorin.jaffree.JaffreeException;
import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.process.StdReader;
import com.github.kokorin.jaffree.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link FFmpegResultReader} reads ffmpeg stderr output, parses {@link FFmpegProgress} and
 * {@link FFmpegResult} and passes unparsed output to {@link OutputListener} (if provided).
 */
public class FFmpegResultReader implements StdReader<FFmpegResult> {
    private final OutputListener outputListener;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegResultReader.class);

    /**
     * Creates {@link FFmpegResultReader}.
     *
     * @param outputListener output listener
     */
    public FFmpegResultReader(final OutputListener outputListener) {
        this.outputListener = outputListener;
    }

    /**
     * Reads provided {@link InputStream} until it's depleted.
     * <p>
     * This method parses every line to detect ffmpeg log level. Lines with INFO log level are
     * additionally parsed to get ffmpeg result (which may be null because of too strict log level)
     *
     * @param stdOut input stream to read from
     * @return FFmpegResult if found or null
     * @throws JaffreeException if IOException appears or ffmpeg ends with error message.
     * @see FFmpeg#setLogLevel(LogLevel)
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
                LogLevel logLevel = detectLogLevel(line);

                if (logLevel != null) {
                    switch (logLevel) {
                        case TRACE:
                            LOGGER.trace(line);
                            break;
                        case VERBOSE:
                        case DEBUG:
                            LOGGER.debug(line);
                            break;
                        case INFO:
                            LOGGER.info(line);
                            break;
                        case WARNING:
                            LOGGER.warn(line);
                            break;
                        case ERROR:
                        case FATAL:
                        case PANIC:
                        case QUIET:
                            LOGGER.error(line);
                            break;
                    }
                } else {
                    LOGGER.info(line);
                }

                if (logLevel == LogLevel.INFO) {
                    FFmpegResult possibleResult = parseResult(line);

                    if (possibleResult != null) {
                        result = possibleResult;
                        errorMessage = null;
                        continue;
                    }
                }

                if (logLevel == null && outputListener != null) {
                    outputListener.onOutput(line);
                    continue;
                }

                if (result != null) {
                    continue;
                }

                if (logLevel != null && logLevel.code() <= LogLevel.ERROR.code()) {
                    errorMessage = line;
                }
            }
        } catch (IOException e) {
            throw new JaffreeException("Exception while reading ffmpeg output", e);
        }

        if (errorMessage != null) {
            throw new JaffreeException("ffmpeg exited with message: " + errorMessage);
        }

        if (result != null) {
            return result;
        }

        return new FFmpegResult(
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    static LogLevel detectLogLevel(final String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        LogLevel result = detectLogLevel(line, 0);

        if (result == null) {
            int offset = line.indexOf('[', 1);
            if (offset != -1) {
                result = detectLogLevel(line, offset);
            }
        }

        return result;
    }

    private static LogLevel detectLogLevel(final String line, final int offset) {
        if (line.regionMatches(offset, "[info]", 0, 6)) {
            return LogLevel.INFO;
        }

        if (line.regionMatches(offset, "[verbose]", 0, 9)) {
            return LogLevel.VERBOSE;
        }

        if (line.regionMatches(offset, "[debug]", 0, 7)) {
            return LogLevel.DEBUG;
        }

        if (line.regionMatches(offset, "[warning]", 0, 9)) {
            return LogLevel.WARNING;
        }

        if (line.regionMatches(offset, "[error]", 0, 7)) {
            return LogLevel.ERROR;
        }

        if (line.regionMatches(offset, "[trace]", 0, 7)
                // before 2019-12-16 ffmpeg output trace as []
                // see https://github.com/FFmpeg/FFmpeg/commit/84db67894f9aec4aa0c8df67265019e0391c7572
                || line.regionMatches(offset, "[]", 0, 2)) {
            return LogLevel.TRACE;
        }

        if (line.regionMatches(offset, "[quiet]", 0, 7)) {
            return LogLevel.QUIET;
        }

        if (line.regionMatches(offset, "[panic]", 0, 7)) {
            return LogLevel.PANIC;
        }

        if (line.regionMatches(offset, "[fatal]", 0, 7)) {
            return LogLevel.FATAL;
        }

        return null;
    }

    static FFmpegResult parseResult(final String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        try {
            String valueWithoutSpaces = line
                    .replaceAll("other streams", "other_streams")
                    .replaceAll("global headers", "global_headers")
                    .replaceAll("muxing overhead", "muxing_overhead")
                    .replaceAll(":\\s+", ":");

            Map<String, String> map = parseKeyValues(valueWithoutSpaces, ":");

            Long videoSize = ParseUtil.parseSizeInBytes(map.get("video"));
            Long audioSize = ParseUtil.parseSizeInBytes(map.get("audio"));
            Long subtitleSize = ParseUtil.parseSizeInBytes(map.get("subtitle"));
            Long otherStreamsSize = ParseUtil.parseSizeInBytes(map.get("other_streams"));
            Long globalHeadersSize = ParseUtil.parseSizeInBytes(map.get("global_headers"));
            Double muxOverhead = ParseUtil.parseRatio(map.get("muxing_overhead"));

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

    private static boolean hasNonNull(final Object... items) {
        for (Object item : items) {
            if (item != null) {
                return true;
            }
        }

        return false;
    }
}
