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

    /**
     * @return size in bytes
     */
    public long getVideoSize() {
        return videoSize;
    }

    /**
     * @return size in bytes
     */
    public long getAudioSize() {
        return audioSize;
    }

    /**
     * @return size in bytes
     */
    public long getSubtitleSize() {
        return subtitleSize;
    }

    /**
     * @return size in bytes
     */
    public long getOtherStreamsSize() {
        return otherStreamsSize;
    }

    /**
     * @return size in bytes
     */
    public long getGlobalHeadersSize() {
        return globalHeadersSize;
    }

    /**
     * @return value in range [0..1]
     */
    public double getMuxingOverheadRatio() {
        return muxingOverheadRatio;
    }
}
