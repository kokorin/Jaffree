/*
 *    Copyright  2021 Denis Kokorin
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

/**
 * Parses ffmpeg progress and result values.
 */
public class ParseUtil {

    private static final String KBYTES_SUFFIX = "kB";
    private static final String KBITS_PER_SECOND_SUFFIX = "kbits/s";
    private static final String SPEED_SUFFIX = "x";
    private static final String PERCENT_SUFFIX = "%";

    private ParseUtil() {
    }

    /**
     * Parses long without exception
     * @param value string to parse
     * @return parsed long or null if value can't be parsed
     */
    public static Long parseLong(final String value) {
        if (value != null && !value.isEmpty()) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                // Suppress
            }
        }

        return null;
    }

    /**
     * Parses double without exception
     * @param value string to parse
     * @return parsed double or null if value can't be parsed
     */
    public static Double parseDouble(final String value) {
        if (value != null && !value.isEmpty()) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // Suppress
            }
        }

        return null;
    }

    /**
     * Parses size in kilobytes without exception
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
     * Parses size in kilobytes without exception
     * @param value string to parse
     * @return parsed long or null if value can't be parsed
     */
    public static Long parseSizeInKiloBytes(final String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        return parseLongWithSuffix(value.trim(), KBYTES_SUFFIX);
    }

    /**
     * Parses encoding bitrate in kbits/s without exception
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
     * Parses encoding speed without exception
     * @param value string to parse
     * @return parsed double or null if value can't be parsed
     */
    // TODO probably too specific method, instead parseDoubleWithSuffix can be public
    public static Double parseSpeed(final String value) {
        return parseDoubleWithSuffix(value, SPEED_SUFFIX);
    }

    public static Double parseRatio(final String value) {
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
        if (value == null || value.isEmpty()) {
            return null;
        }

        if (!value.endsWith(suffix)) {
            return null;
        }

        return value.substring(0, value.length() - suffix.length());
    }
}
