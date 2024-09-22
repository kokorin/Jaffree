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

package com.github.kokorin.jaffree.ffmpeg.input;

import com.github.kokorin.jaffree.ffmpeg.io.Frame;
import com.github.kokorin.jaffree.ffmpeg.Stream;

import java.util.List;

/**
 * Allows custom implementation to produce streams and frames.
 *
 * @see FrameInput
 */
public interface FrameProducer {

    /**
     * Called once before any call to {@link #produce()}.
     *
     * @return streams
     */
    List<Stream> produceStreams();

    /**
     * Called repeatedly to get frames.
     * <p>
     * When there is no more frame, method should return {@code}null{@code} value.
     * <p>
     * Method must return video frames with {@link java.awt.image.BufferedImage BufferedImage}s
     * either in {@link java.awt.image.BufferedImage#TYPE_4BYTE_ABGR TYPE_4BYTE_ABGR},
     * or in {@link java.awt.image.BufferedImage#TYPE_3BYTE_BGR TYPE_3BYTE_BGR} formats
     *
     * @return Frame
     */
    Frame produce();
}
