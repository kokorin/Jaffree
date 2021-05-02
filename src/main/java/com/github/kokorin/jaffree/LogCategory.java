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

package com.github.kokorin.jaffree;

/**
 * Log categories declared by ffmpeg as
 * <a href="https://github.com/FFmpeg/FFmpeg/blob/master/libavutil/log.h#L29">AVClassCategory</a>.
 */
public enum LogCategory {
    NA(0),
    INPUT(1),
    OUTPUT(2),
    MUXER(3),
    DEMUXER(4),
    ENCODER(5),
    DECODER(6),
    FILTER(7),
    BITSTREAM_FILTER(8),
    SWSCALER(9),
    SWRESAMPLER(10),
    DEVICE_VIDEO_OUTPUT(40),
    DEVICE_VIDEO_INPUT(41),
    DEVICE_AUDIO_OUTPUT(42),
    DEVICE_AUDIO_INPUT(43),
    DEVICE_OUTPUT(44),
    DEVICE_INPUT(45),
    NB(46);

    private final int code;

    LogCategory(final int code) {
        this.code = code;
    }

    /**
     * Returns LogCategory with specified code.
     *
     * @param code category code
     * @return LogCategory or null
     */
    public static LogCategory fromCode(final int code) {
        for (LogCategory category : values()) {
            if (category.code == code) {
                return category;
            }
        }
        return null;
    }
}
