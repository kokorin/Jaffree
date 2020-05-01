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

/**
 * {@link FFmpegResult} contains information about ffmpeg encoding result.
 */
public class FFmpegResult {
    private final Long videoSize;
    private final Long audioSize;
    private final Long subtitleSize;
    private final Long otherStreamsSize;
    private final Long globalHeadersSize;
    private final Double muxingOverheadRatio;

    /**
     * Creates {@link FFmpegResult}.
     *
     * @param videoSize           output video stream size in bytes
     * @param audioSize           output audio stream size in bytes
     * @param subtitleSize        output subtitles stream size in bytes
     * @param otherStreamsSize    output other streams size in bytes
     * @param globalHeadersSize   output global headers size in bytes
     * @param muxingOverheadRatio ratio of extra information size to output size
     */
    public FFmpegResult(final Long videoSize, final Long audioSize, final Long subtitleSize,
                        final Long otherStreamsSize, final Long globalHeadersSize,
                        final Double muxingOverheadRatio) {
        this.videoSize = videoSize;
        this.audioSize = audioSize;
        this.subtitleSize = subtitleSize;
        this.otherStreamsSize = otherStreamsSize;
        this.globalHeadersSize = globalHeadersSize;
        this.muxingOverheadRatio = muxingOverheadRatio;
    }

    /**
     * Note: value may be not exact.
     *
     * @return video stream size in bytes.
     */
    public Long getVideoSize() {
        return videoSize;
    }

    /**
     * Note: value may be not exact.
     *
     * @return audio stream size in bytes.
     */
    public Long getAudioSize() {
        return audioSize;
    }

    /**
     * Note: value may be not exact.
     *
     * @return subtitles stream size in bytes.
     */
    public Long getSubtitleSize() {
        return subtitleSize;
    }

    /**
     * Note: value may be not exact.
     *
     * @return output other streams size in bytes.
     */
    public Long getOtherStreamsSize() {
        return otherStreamsSize;
    }

    /**
     * Note: value may be not exact.
     *
     * @return output global headers size in bytes.
     */
    public Long getGlobalHeadersSize() {
        return globalHeadersSize;
    }

    /**
     * Note: value may be not exact.
     *
     * @return ratio of extra information size to output size.
     */
    public Double getMuxingOverheadRatio() {
        return muxingOverheadRatio;
    }
}
