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

import com.github.kokorin.jaffree.StreamType;

/**
 * Allows to consume in Java audio & video frames produced by ffmpeg.
 */
public class FrameOutput extends TcpOutput<FrameOutput> implements Output {
    private final FrameConsumer consumer;
    private final boolean alpha;

    /**
     * Creates {@link FrameOutput}.
     *
     * @param consumer frame consumer
     * @param alpha    alpha channel
     */
    public FrameOutput(final FrameConsumer consumer, final boolean alpha) {
        this.consumer = consumer;
        this.alpha = alpha;
        setFormat("nut");

        // default arguments
        setCodec(StreamType.VIDEO, "rawvideo");
        String pixelFormat = alpha ? "abgr" : "bgr24";
        setPixelFormat(pixelFormat);

        setCodec(StreamType.AUDIO, "pcm_s32be");
    }

    /**
     * Creates {@link com.github.kokorin.jaffree.ffmpeg.TcpOutput.Consumer} which is capable of
     * reading frames from ffmpeg via TCP socket and passing them to {@link FrameConsumer}.
     *
     * @return byte consumer
     */
    @Override
    protected Consumer consumer() {
        return new NutFrameConsumer(consumer, alpha);
    }

    /**
     * Creates {@link FrameOutput}.
     * @param consumer frame consumer
     * @return FrameOutput
     */
    public static FrameOutput withConsumer(final FrameConsumer consumer) {
        return new FrameOutput(consumer, false);
    }

    /**
     * Creates {@link FrameOutput} with alpha channel.
     * @param consumer frame consumer
     * @return FrameOutput
     */
    public static FrameOutput withConsumerAlpha(final FrameConsumer consumer) {
        return new FrameOutput(consumer, true);
    }
}
