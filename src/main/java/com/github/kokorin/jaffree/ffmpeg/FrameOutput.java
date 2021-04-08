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

import com.github.kokorin.jaffree.JaffreeException;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.net.TcpNegotiator;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Allows to consume in Java audio &amp; video frames produced by ffmpeg.
 */
public class FrameOutput extends TcpOutput<FrameOutput> implements Output {

    /**
     * Create {@link FrameOutput} for {@link FFmpeg}
     *
     * @param frameReader frame reader
     * @param format      media format
     * @param videoCodec  video codec
     * @param pixelFormat pixel format
     * @param audioCodec  audio codec
     * @see NutFrameReader
     */
    protected FrameOutput(FrameReader frameReader, String format,
                          String videoCodec, String pixelFormat, String audioCodec) {
        super(new FrameOutputNegotiator(frameReader));
        super.setFormat(format);
        super.setCodec(StreamType.VIDEO.code(), videoCodec);
        super.setPixelFormat(null, pixelFormat);
        super.setCodec(StreamType.AUDIO.code(), audioCodec);
    }

    @Override
    public final FrameOutput setFormat(String format) {
        throw new JaffreeException("Format can't be changed");
    }

    @Override
    public final FrameOutput setCodec(String streamSpecifier, String codec) {
        throw new JaffreeException("Codec can't be changed");
    }

    @Override
    public final FrameOutput setPixelFormat(String streamSpecifier, String pixelFormat) {
        throw new JaffreeException("Pixel Format can't be changed");
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
        return withConsumer(consumer, ImageFormats.BGR24);
    }

    /**
     * Creates {@link FrameOutput} with alpha channel.
     *
     * @param consumer frame consumer
     * @return FrameOutput
     */
    public static FrameOutput withConsumerAlpha(final FrameConsumer consumer) {
        return withConsumer(consumer, ImageFormats.ABGR);
    }

    protected static FrameOutput withConsumer(final FrameConsumer consumer, final ImageFormat imageFormat) {
        return new FrameOutput(
                new NutFrameReader(consumer, imageFormat),
                "nut", "rawvideo", imageFormat.getPixelFormat(), "pcm_s32be"
        );
    }

    protected interface FrameReader {
        void read(InputStream inputStream) throws IOException;
    }

    @ThreadSafe
    protected static class FrameOutputNegotiator implements TcpNegotiator {
        private final FrameReader frameReader;

        public FrameOutputNegotiator(FrameReader frameReader) {
            this.frameReader = frameReader;
        }

        @Override
        public synchronized void negotiate(Socket socket) throws IOException {
            try (InputStream inputStream = socket.getInputStream()) {
                frameReader.read(inputStream);
            }
        }
    }
}
