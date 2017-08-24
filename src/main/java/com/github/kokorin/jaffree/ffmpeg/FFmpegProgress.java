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

import java.util.HashMap;
import java.util.Map;

public class FFmpegProgress {
    private final long frame;
    private final double fps;
    private final double q;
    private final long size;
    private final long time;
    private final long dup;
    private final long drop;
    private final double bitrate;
    private final double speed;

    public FFmpegProgress(long frame, double fps, double q, long size, long time, long dup, long drop, double bitrate, double speed) {
        this.frame = frame;
        this.fps = fps;
        this.q = q;
        this.size = size;
        this.time = time;
        this.dup = dup;
        this.drop = drop;
        this.bitrate = bitrate;
        this.speed = speed;
    }

    public long getFrame() {
        return frame;
    }

    public double getFps() {
        return fps;
    }

    public double getQ() {
        return q;
    }

    public long getSize() {
        return size;
    }

    /**
     * @return time in milliseconds
     */
    public long getTime() {
        return time;
    }

    public long getDup() {
        return dup;
    }

    public long getDrop() {
        return drop;
    }

    public double getBitrate() {
        return bitrate;
    }

    public double getSpeed() {
        return speed;
    }

    public static FFmpegProgress fromString(String value) {
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

            String[] sizeAndUnit = splitValueAndUnit(map.get("Lsize"));
            long size = parseLong(sizeAndUnit[0], 0) * parseSizeUnit(sizeAndUnit[1]).multiplier();

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
