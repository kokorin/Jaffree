/*
 *    Copyright  2017-2021 Denis Kokorin
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
import com.github.kokorin.jaffree.net.TcpNegotiator;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Allows to consume in Java audio & video frames produced by ffmpeg.
 */
public class FrameOutput extends TcpOutput<FrameOutput> implements Output {
    private final FrameOutputNegotiator negotiator;

    private static final String PIXEL_FORMAT_ALPHA = "abgr";
    private static final String PIXEL_FORMAT_RGB = "bgr24";

    /**
     * Creates {@link FrameOutput}.
     *
     * @param consumer frame consumer
     */
    public FrameOutput(final FrameConsumer consumer) {
        this(new FrameOutputNegotiator(consumer));
    }

    protected FrameOutput(FrameOutputNegotiator negotiator) {
        super(negotiator);
        this.negotiator = negotiator;
        super.setFormat("nut");
        super.setCodec(StreamType.VIDEO.code(), "rawvideo");
        super.setCodec(StreamType.AUDIO.code(), "pcm_s32be");
        super.setPixelFormat(null, PIXEL_FORMAT_RGB);
    }

    /**
     * @param alpha true if video contains alpha channel and it should be extracted
     * @return this
     */
    public FrameOutput setAlpha(boolean alpha) {
        negotiator.setAlpha(alpha);
        String pixelFormat = alpha ? PIXEL_FORMAT_ALPHA : PIXEL_FORMAT_RGB;
        super.setPixelFormat(null, pixelFormat);
        return this;
    }

    @Override
    public final FrameOutput setFormat(String format) {
        throw new RuntimeException("Format can't be changed");
    }

    @Override
    public final FrameOutput setCodec(String streamSpecifier, String codec) {
        throw new RuntimeException("Codec can't be changed");
    }

    @Override
    public final FrameOutput setPixelFormat(String streamSpecifier, String pixelFormat) {
        throw new RuntimeException("Pixel Format can't be changed");
    }

    @Override
    public FrameOutput disableStream(StreamType streamType) {
        super.disableStream(streamType);

        // We have to reset codec and pixel format if video or audio output is disabled because
        // we set default values in constructor
        switch (streamType) {
            case VIDEO:
                super.setCodec(StreamType.VIDEO.code(), null);
                super.setPixelFormat(null, null);
                break;
            case AUDIO:
                super.setCodec(StreamType.AUDIO.code(), null);
                break;
            default:
        }

        return this;
    }

    /**
     * Creates {@link FrameOutput}.
     *
     * @param consumer frame consumer
     * @return FrameOutput
     */
    public static FrameOutput withConsumer(final FrameConsumer consumer) {
        return new FrameOutput(consumer);
    }

    /**
     * Creates {@link FrameOutput} with alpha channel.
     *
     * @param consumer frame consumer
     * @return FrameOutput
     */
    public static FrameOutput withConsumerAlpha(final FrameConsumer consumer) {
        return withConsumer(consumer)
                .setAlpha(true);
    }

    @ThreadSafe
    protected static class FrameOutputNegotiator implements TcpNegotiator {
        private final FrameConsumer consumer;
        @GuardedBy("this")
        private boolean alpha = false;

        public FrameOutputNegotiator(FrameConsumer consumer) {
            this.consumer = consumer;
        }

        public synchronized void setAlpha(boolean alpha) {
            this.alpha = alpha;
        }

        @Override
        public synchronized void negotiate(Socket socket) throws IOException {
            try (InputStream inputStream = socket.getInputStream()) {
                NutFrameReader frameReader = new NutFrameReader(consumer, alpha);
                frameReader.read(inputStream);
            }
        }
    }
}
