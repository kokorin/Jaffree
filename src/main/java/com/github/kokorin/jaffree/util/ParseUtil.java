/*
 *    Copyright  2021 Denis Kokorin, Cromefire_
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

package com.github.kokorin.jaffree.util;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses ffmpeg progress and result values.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public final class ParseUtil {
    private static final Pattern KBYTES_SUFFIX_PATTERN = Pattern.compile("\\d+(KiB|kB)");
    private static final String KBITS_PER_SECOND_SUFFIX = "kbits/s";
    private static final String SPEED_SUFFIX = "x";
    private static final String PERCENT_SUFFIX = "%";

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseUtil.class);

    private ParseUtil() {
    }

    /**
     * Parses long without exception.
     *
     * @param value string to parse
     * @return parsed long or null if value can't be parsed
     */
    public static Long parseLong(final String value) {
        if (value != null && !value.isEmpty() && !"N/A".equals(value)) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                LOGGER.warn("Failed to parse long number: {}", value, e);
            }
        }

        return null;
    }

    /**
     * Parses double without exception.
     *
     * @param value string to parse
     * @return parsed double or null if value can't be parsed
     */
    public static Double parseDouble(final String value) {
        if (value != null && !value.isEmpty() && !"N/A".equals(value)) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                LOGGER.warn("Failed to parse double number: {}", value, e);
            }
        }

        return null;
    }

    /**
     * Parses size in kilobytes without exception.
     *
     * @param value string to parse
     * @return parsed long or null if value can't be parsed
     */
    public static Long parseSizeInBytes(final String value) {
        Long result = parseSizeInKiloBytes(value);

        if (result == null) {
            return null;
        }

        return result * 1000;
    }

    /**
     * Parses size in kilobytes without exception.
     *
     * @param value string to parse
     * @return parsed long or null if value can't be parsed
     */
    public static Long parseSizeInKiloBytes(final String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        final String trimmedValue = value.trim();
        final Matcher matcher = KBYTES_SUFFIX_PATTERN.matcher(trimmedValue);

        return matcher.find() ? parseLongWithSuffix(trimmedValue, matcher.group(1)) : null;
    }

    /**
     * Parses encoding bitrate in kbits/s without exception.
     *
     * @param value string to parse
     * @return parsed double or null if value can't be parsed
     */
    public static Double parseBitrateInKBits(final String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        return parseDoubleWithSuffix(value.trim(), KBITS_PER_SECOND_SUFFIX);
    }

    /**
     * Parses encoding speed without exception.
     *
     * @param value string to parse
     * @return parsed double or null if value can't be parsed
     */
    public static Double parseSpeed(final String value) {
        return parseDoubleWithSuffix(value, SPEED_SUFFIX);
    }


    /**
     * Parses percents as ratio.
     * <p>
     * E.g. {@code parsePercentsAsRatio("10%")} will return {@code 0.1}
     *
     * @param value string to parse
     * @return parsed double or null if value can't be parsed
     */
    public static Double parsePercentsAsRatio(final String value) {
        Double percents = parseDoubleWithSuffix(value, PERCENT_SUFFIX);

        if (percents == null) {
            return null;
        }

        return percents / 100;
    }

    private static Long parseLongWithSuffix(final String value, final String suffix) {
        String numericValue = removeSuffix(value, suffix);

        return parseLong(numericValue);
    }

    private static Double parseDoubleWithSuffix(final String value, final String suffix) {
        String numericValue = removeSuffix(value, suffix);

        return parseDouble(numericValue);
    }

    private static String removeSuffix(final String value, final String suffix) {
        if (value == null || value.isEmpty() || !value.endsWith(suffix)) {
            return null;
        }

        return value.substring(0, value.length() - suffix.length());
    }

    /**
     * Parses log level in ffmpeg output.
     * <p>
     * Notice: printing loglevel in ffmpeg output should be enabled.
     *
     * @param line line of ffmpeg output
     * @return parsed log level or null
     * @see com.github.kokorin.jaffree.ffmpeg.FFmpeg#setLogLevel(LogLevel)
     */
    public static LogLevel parseLogLevel(final String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        LogLevel result = parseLogLevel(line, 0);

        if (result == null) {
            int offset = line.indexOf('[', 1);
            if (offset != -1) {
                result = parseLogLevel(line, offset);
                if (result == null) {
                    offset = line.indexOf('[', offset + 1);
                    result = parseLogLevel(line, offset);
                }
            }
        }

        return result;
    }

    private static LogLevel parseLogLevel(final String line, final int offset) {
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
                // https://github.com/FFmpeg/FFmpeg/commit/84db67894f9aec4aa0c8df67265019e0391c7572
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

    /**
     * Returns parsed {@link FFmpegResult} or null if passed in line contains no ffmpeg result.
     *
     * @param line ffmpeg output line
     * @return FFmpegResult, or null
     */
    public static FFmpegResult parseResult(final String line) {
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

            Long videoSize = parseSizeInBytes(map.get("video"));
            Long audioSize = parseSizeInBytes(map.get("audio"));
            Long subtitleSize = parseSizeInBytes(map.get("subtitle"));
            Long otherStreamsSize = parseSizeInBytes(map.get("other_streams"));
            Long globalHeadersSize = parseSizeInBytes(map.get("global_headers"));
            Double muxOverheadRatio = parsePercentsAsRatio(map.get("muxing_overhead"));

            if (videoSize != null || audioSize != null || subtitleSize != null
                    || otherStreamsSize != null || globalHeadersSize != null
                    || muxOverheadRatio != null) {
                return new FFmpegResult(videoSize, audioSize, subtitleSize, otherStreamsSize,
                        globalHeadersSize, muxOverheadRatio);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse result: {}", line, e);
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
}
