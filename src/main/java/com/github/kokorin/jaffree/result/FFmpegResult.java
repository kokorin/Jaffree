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

package com.github.kokorin.jaffree.result;

import com.github.kokorin.jaffree.SizeUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FFmpegResult {
    private final long videoSize;
    private final long audioSize;
    private final long subtitleSize;
    private final long otherStreamsSize;
    private final long globalHeadersSize;
    private final double muxingOverheadRatio;

    public FFmpegResult(long videoSize, long audioSize, long subtitleSize, long otherStreamsSize, long globalHeadersSize, double muxingOverheadRatio) {
        this.videoSize = videoSize;
        this.audioSize = audioSize;
        this.subtitleSize = subtitleSize;
        this.otherStreamsSize = otherStreamsSize;
        this.globalHeadersSize = globalHeadersSize;
        this.muxingOverheadRatio = muxingOverheadRatio;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public long getAudioSize() {
        return audioSize;
    }

    public long getSubtitleSize() {
        return subtitleSize;
    }

    public long getOtherStreamsSize() {
        return otherStreamsSize;
    }

    public long getGlobalHeadersSize() {
        return globalHeadersSize;
    }

    public double getMuxingOverheadRatio() {
        return muxingOverheadRatio;
    }

    // video:24326kB audio:1997kB subtitle:0kB other streams:0kB global headers:0kB muxing overhead: 0.532625%
    private static final String SIZE_PATTERN = "(\\d+)([kKmMgGibB]+)";
    private static final Pattern RESULT_PATTERN = Pattern.compile(
            "^video:\\s*" + SIZE_PATTERN + "\\s*" +
            "audio:\\s*" + SIZE_PATTERN + "\\s*" +
            "subtitle:\\s*" + SIZE_PATTERN + "\\s*" +
            "other streams:\\s*" + SIZE_PATTERN + "\\s*" +
            "global headers:\\s*" + SIZE_PATTERN + "\\s*" +
            "muxing overhead: ([\\.\\de\\+-]+)%\\s*$"
    );

    public static FFmpegResult fromString(String value) {
        if (value == null) {
            return null;
        }

        Matcher matcher = RESULT_PATTERN.matcher(value);
        if (!matcher.matches()) {
            return null;
        }

        long videoSize = Long.parseLong(matcher.group(1)) * parseSizeUnit(matcher.group(2)).multiplier();
        long audioSize = Long.parseLong(matcher.group(3)) * parseSizeUnit(matcher.group(4)).multiplier();
        long subtitleSize = Long.parseLong(matcher.group(5)) * parseSizeUnit(matcher.group(6)).multiplier();
        long otherStreamsSize = Long.parseLong(matcher.group(7)) * parseSizeUnit(matcher.group(8)).multiplier();
        long globalHeadersSize = Long.parseLong(matcher.group(9)) * parseSizeUnit(matcher.group(10)).multiplier();
        double muxingOverheadRatio = Double.parseDouble(matcher.group(11)) * 0.01;

        return new FFmpegResult(videoSize, audioSize, subtitleSize, otherStreamsSize, globalHeadersSize, muxingOverheadRatio);
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
