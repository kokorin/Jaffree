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

import com.github.kokorin.jaffree.JaffreeException;
import com.github.kokorin.jaffree.net.TcpNegotiator;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Allows to supply ffmpeg with audio &amp; video frames constructed in Java.
 *
 * <b>It's strongly recommended</b> to specify {@link #setFrameRate(Number)} for video producing.
 *
 * @see FrameProducer
 */
public class FrameInput extends TcpInput<FrameInput> implements Input {
    private final FrameInputNegotiator negotiator;

    private static final int DEFAULT_FRAME_ORDERING_BUFFER_MILLIS = 200;
    private static final Logger LOGGER = LoggerFactory.getLogger(FrameInput.class);

    /**
     * Creates {@link FrameInput} for {@link FFmpeg}.
     *
     * @param frameWriter frame writer
     * @param format      media format
     * @see NutFrameWriter
     */
    protected FrameInput(final FrameWriter frameWriter, final String format) {
        this(new FrameInputNegotiator(frameWriter), format);
    }

    private FrameInput(final FrameInputNegotiator negotiator, final String format) {
        super(negotiator);
        this.negotiator = negotiator;
        super.setFormat(format);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>It's strongly recommended</b> to specify videoFrameRate for video producing.
     * <p>
     * Otherwise conversion can be very slow (20-50 times slower) and even can result in
     * corrupted video.
     */
    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public FrameInput setFrameRate(final Number frameRate) {
        return super.setFrameRate(frameRate);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>It's strongly recommended</b> to specify videoFrameRate for video producing.
     * <p>
     * Otherwise conversion can be very slow (20-50 times slower) and even can result in
     * corrupted video.
     */
    @Override
    public FrameInput setFrameRate(final String streamSpecifier, final Number frameRate) {
        negotiator.setFrameRateSet(true);
        return super.setFrameRate(streamSpecifier, frameRate);
    }

    /**
     * Format change is prohibited after {@link FrameInput} instantiation.
     *
     * @param format format
     * @return never returns
     * @throws JaffreeException always
     */
    @Override
    public final FrameInput setFormat(final String format) {
        throw new JaffreeException("Format can't be changed");
    }

    /**
     * Creates {@link FrameInput} with specified frame producer.
     * <p>
     * Note: frame producer should produce video frames in BGR24 format.
     *
     * @param producer frame producer
     * @return FrameInput
     * @see ImageFormats#BGR24
     */
    public static FrameInput withProducer(final FrameProducer producer) {
        return withProducer(producer, ImageFormats.BGR24);
    }

    /**
     * Creates {@link FrameInput} with specified frame producer with alpha channel.
     * <p>
     * Note: frame producer should produce video frames in ABGR format.
     *
     * @param producer frame producer
     * @return FrameInput
     * @see ImageFormats#ABGR
     */
    public static FrameInput withProducerAlpha(final FrameProducer producer) {
        return withProducer(producer, ImageFormats.ABGR);
    }

    /**
     * Creates {@link FrameInput} with specified frame producer and image format.
     *
     * @param producer    frame producer
     * @param imageFormat video frame image format
     * @return FrameInput
     * @see ImageFormats
     */
    public static FrameInput withProducer(final FrameProducer producer,
                                          final ImageFormat imageFormat) {
        return withProducer(producer, imageFormat, DEFAULT_FRAME_ORDERING_BUFFER_MILLIS);
    }

    /**
     * Creates {@link FrameInput} with specified frame producer, format and frame ordering buffer
     * <p>
     * Frame ordering buffer allows {@link FrameProducer} to produce frame without strict ordering
     * (which is required by NUT format).
     * <p>
     * <b>Note</b>: too long frame ordering buffer may cause {@link OutOfMemoryError} or
     * performance degradation.
     *
     * @param producer                  frame producer
     * @param imageFormat               video frame image format
     * @param frameOrderingBufferMillis frame ordering buffer milliseconds
     * @return FrameInput
     * @see ImageFormats
     */
    public static FrameInput withProducer(final FrameProducer producer,
                                          final ImageFormat imageFormat,
                                          final long frameOrderingBufferMillis) {
        return new FrameInput(
                new NutFrameWriter(producer, imageFormat, frameOrderingBufferMillis),
                "nut"
        );
    }

    protected interface FrameWriter {
        void write(OutputStream outputStream) throws IOException;
    }

    /**
     * {@link TcpNegotiator} implementation which uses {@link FrameWriter} to send bytes over
     * TCP connection.
     */
    @ThreadSafe
    protected static class FrameInputNegotiator implements TcpNegotiator {
        private final FrameWriter frameWriter;

        @GuardedBy("this")
        private boolean frameRateSet;

        public FrameInputNegotiator(final FrameWriter frameWriter) {
            this.frameWriter = frameWriter;
        }

        private synchronized void setFrameRateSet(final boolean frameRateSet) {
            this.frameRateSet = frameRateSet;
        }

        /**
         * Sends media over TCP connection.
         *
         * @param socket TCP socket
         * @throws IOException if any IO error
         */
        @Override
        public synchronized void negotiate(final Socket socket) throws IOException {
            if (!frameRateSet) {
                LOGGER.warn("It's strongly recommended to specify video frame rate, "
                        + "otherwise video encoding may be slower (by 20-50 times) "
                        + "and may produce corrupted video");
            }

            try (OutputStream output = socket.getOutputStream()) {
                frameWriter.write(output);
            }
        }
    }
}
