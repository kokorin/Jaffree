/*
 *    Copyright 2017-2021 Denis Kokorin
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

import com.github.kokorin.jaffree.OS;

/**
 * FFmpeg Null output implementation.
 * <p>
 * The null muxer does not generate any output file.
 * The null muxer uses a wrapped frame so there is no muxing overhead (i.e. it can accept
 * any type of input codec, doesn't have to be rawvideo for instance).
 * <p>
 * Null output can be combined with codec copying to achieve fast &amp; exact file length detection.
 * <p>
 * It may be required also to set {@link FFmpeg#setOverwriteOutput(boolean)} to true.
 *
 * @see FFmpeg#setOverwriteOutput(boolean)
 * @see <a href="https://trac.ffmpeg.org/wiki/Null">ffmpeg Null</a>
 */
public class NullOutput extends BaseOutput<NullOutput> implements Output {
    /**
     * Creates {@link NullOutput} with codec copying.
     */
    public NullOutput() {
        this(true);
    }

    /**
     * Creates {@link NullOutput}.
     *
     * @param copyCodecs true to use copy codecs
     */
    public NullOutput(final boolean copyCodecs) {
        super(OS.IS_WINDOWS ? "NUL" : "/dev/null");

        if (copyCodecs) {
            copyAllCodecs();
        }
        setFormat("null");
    }
}
