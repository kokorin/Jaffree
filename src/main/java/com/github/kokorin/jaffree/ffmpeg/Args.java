/*
 *    Copyright 2021 Denis Kokorin
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Command-line arguments utility.
 */
public final class Args {
    private Args() {
    }

    /**
     * Formats duration as fraction of second.
     *
     * @param durationMillis duration in milliseconds
     * @return duration in seconds with 3 digits after decimal separator
     */
    public static String formatDuration(final long durationMillis) {
        long divider = TimeUnit.SECONDS.toMillis(1);
        long seconds = durationMillis / divider;
        long millis = Math.abs(durationMillis) % divider;
        return String.format("%d.%03d", seconds, millis);
    }

    /**
     * Builds a list of arguments based on key and name-value pairs in the way accepted by
     * ffmpeg/ffprobe.
     *
     * @param key  key to combine arguments
     * @param args type-value pairs of arguments
     * @return list of arguments
     */
    public static List<String> toArguments(final String key, final Map<String, Object> args) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Object> arg : args.entrySet()) {
            String specifier = arg.getKey();
            Object valueObj = arg.getValue();
            String value = valueObj != null ? valueObj.toString() : null;

            if (value == null) {
                continue;
            }

            if (specifier == null || specifier.isEmpty()) {
                result.add(key);
                result.add(value);
                continue;
            }

            result.add(key + ":" + specifier);
            result.add(value);
        }

        return result;
    }
}
