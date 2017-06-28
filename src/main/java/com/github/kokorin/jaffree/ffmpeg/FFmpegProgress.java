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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FFmpegProgress {
    private final long frame;
    private final double fps;
    private final double q;
    private final long size;
    private final long time;
    private final double bitrate;
    private final double speed;

    public FFmpegProgress(long frame, double fps, double q, long size, long time, double bitrate, double speed) {
        this.frame = frame;
        this.fps = fps;
        this.q = q;
        this.size = size;
        this.time = time;
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

    public double getBitrate() {
        return bitrate;
    }

    public double getSpeed() {
        return speed;
    }

    // frame= 5012 fps=0.0 q=-1.0 Lsize=   26463kB time=00:02:47.20 bitrate=1296.6kbits/s speed=1.23e+003x
    private static final Pattern PROGRESS_PATTERN = Pattern.compile(
            "^frame=\\s*(\\d+)\\s*" +
            "fps=\\s*([.\\d]+)\\s*" +
            "q=\\s*([\\-.\\d]+)\\s*" +
            "Lsize=\\s*(\\d+)([kKmMgGibB]+)\\s*" +
            "time=\\s*(\\d+):(\\d+):([.\\d]+)\\s*" +
            "bitrate=\\s*([\\-.\\d]+)kbits/s\\s*" +
            "(?:speed=\\s*([.\\de+\\-]+)x\\s*$)?"
    );

    public static FFmpegProgress fromString(String value) {
        if (value == null) {
            return null;
        }

        Matcher matcher = PROGRESS_PATTERN.matcher(value);
        if (!matcher.matches()) {
            return null;
        }

        long frame = Long.parseLong(matcher.group(1));
        double fps = Double.parseDouble(matcher.group(2));
        double q = Double.parseDouble(matcher.group(3));
        long size = Long.parseLong(matcher.group(4)) * parseSizeUnit(matcher.group(5)).multiplier();
        long hours = Long.parseLong(matcher.group(6));
        long minutes = Long.parseLong(matcher.group(7));
        double seconds = Double.parseDouble(matcher.group(8));
        long time = (long) (((hours * 60 + minutes) * 60 + seconds) * 1000);
        double bitrate = Double.parseDouble(matcher.group(9));

        String speedStr = matcher.group(10);
        double speed = Double.NaN;
        if (speedStr != null && !speedStr.isEmpty()) {
            speed = Double.parseDouble(speedStr);
        }

        return new FFmpegProgress(frame, fps, q, size, time, bitrate, speed);
    }

    private static SizeUnit parseSizeUnit(String value) {
        for (SizeUnit unit : SizeUnit.values()) {
            if (unit.name().equalsIgnoreCase(value)) {
                return unit;
            }
        }

        throw new RuntimeException("Failed to parse size unit: " + value);
    }
}
