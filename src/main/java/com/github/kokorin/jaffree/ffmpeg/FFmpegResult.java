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
    private final Long videoSize;
    private final Long audioSize;
    private final Long subtitleSize;
    private final Long otherStreamsSize;
    private final Long globalHeadersSize;
    private final Double muxingOverheadRatio;

    public FFmpegResult(Long videoSize, Long audioSize, Long subtitleSize, Long otherStreamsSize, Long globalHeadersSize, Double muxingOverheadRatio) {
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
    public Long getVideoSize() {
        return videoSize;
    }

    /**
     * @return size in bytes
     */
    public Long getAudioSize() {
        return audioSize;
    }

    /**
     * @return size in bytes
     */
    public Long getSubtitleSize() {
        return subtitleSize;
    }

    /**
     * @return size in bytes
     */
    public Long getOtherStreamsSize() {
        return otherStreamsSize;
    }

    /**
     * @return size in bytes
     */
    public Long getGlobalHeadersSize() {
        return globalHeadersSize;
    }

    /**
     * @return value in range [0..1]
     */
    public Double getMuxingOverheadRatio() {
        return muxingOverheadRatio;
    }
}
