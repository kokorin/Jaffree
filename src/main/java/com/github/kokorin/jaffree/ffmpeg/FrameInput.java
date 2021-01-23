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

import com.github.kokorin.jaffree.net.TcpNegotiator;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Allows to supply ffmpeg with audio & video frames constructed in Java.
 *
 * <b>It's strongly recommended</b> to specify {@link #setFrameRate(Number)} for video producing.
 *
 * @see FrameProducer
 */
public class FrameInput extends TcpInput<FrameInput> implements Input {
    private final FrameInputNegotiator negotiator;

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameInput.class);

    /**
     * Creates {@link FrameInput} for {@link FFmpeg}.
     *
     * @param producer frame producer
     */
    public FrameInput(final FrameProducer producer) {
        this(new FrameInputNegotiator(producer));
    }

    protected FrameInput(FrameInputNegotiator negotiator) {
        super(negotiator);
        this.negotiator = negotiator;
        super.setFormat("nut");
    }

    /**
     * Whether produced video stream should contain alpha channel.
     *
     * @param containsAlphaChannel alpha channel
     * @return this
     */
    // TODO rename method
    public FrameInput produceAlpha(final boolean containsAlphaChannel) {
        negotiator.setAlpha(containsAlphaChannel);
        return this;
    }

    /**
     * <b>It's strongly recommended</b> to specify videoFrameRate for video producing.
     * <p>
     * Otherwise conversion can be very slow (20-50 times slower) and even can result in
     * corrupted video.
     *
     * @param frameRate video frames per second
     * @return this
     */
    @Override
    public FrameInput setFrameRate(final Number frameRate) {
        negotiator.setFrameRateSet(true);
        return super.setFrameRate(frameRate);
    }

    /**
     * <b>It's strongly recommended</b> to specify videoFrameRate for video producing.
     * <p>
     * Otherwise conversion can be very slow (20-50 times slower) and even can result in
     * corrupted video.
     *
     * @param streamSpecifier stream specifier
     * @param frameRate       video frames per second
     * @return this
     */
    @Override
    public FrameInput setFrameRate(final String streamSpecifier, final Number frameRate) {
        negotiator.setFrameRateSet(true);
        return super.setFrameRate(streamSpecifier, frameRate);
    }

    /**
     * Sets frame ordering buffer while producing NUT video. Default is 200 ms.
     * <p>
     * Frame ordering buffer allows {@link FrameProducer} to produce frame without strict ordering
     * (which is required by NUT format).
     * <p>
     * Note: high values may cause OutOfMemoryError or performance degradation.
     *
     * @param bufferTime buffer time
     * @param unit       time unit
     * @return this
     */
    public FrameInput setFrameOrderingBuffer(final long bufferTime, final TimeUnit unit) {
        return setFrameOrderingBuffer(unit.toMillis(bufferTime));
    }

    /**
     * Sets frame ordering buffer while producing NUT video. Default is 200 ms.
     * <p>
     * Frame ordering buffer allows {@link FrameProducer} to produce frame without strict ordering
     * (which is required by NUT format).
     * <p>
     * Note: high values may cause OutOfMemoryError or performance degradation.
     *
     * @param bufferTimeMillis buffer time in milliseconds
     * @return this
     */
    public FrameInput setFrameOrderingBuffer(final long bufferTimeMillis) {
        negotiator.setFrameOrderingBufferMillis(bufferTimeMillis);
        return this;
    }

    @Override
    public final FrameInput setFormat(String format) {
        throw new RuntimeException("Format can't be changed");
    }

    /**
     * Creates {@link FrameInput} with {@link FrameProducer}.
     *
     * @param producer frame producer
     * @return FrameInput
     */
    public static FrameInput withProducer(final FrameProducer producer) {
        return new FrameInput(producer);
    }

    @ThreadSafe
    protected static class FrameInputNegotiator implements TcpNegotiator {
        private final FrameProducer producer;

        @GuardedBy("this")
        private boolean alpha;
        @GuardedBy("this")
        private boolean frameRateSet;
        @GuardedBy("this")
        private Long frameOrderingBufferMillis;

        public FrameInputNegotiator(FrameProducer producer) {
            this.producer = producer;
        }

        public synchronized void setAlpha(final boolean alpha) {
            this.alpha = alpha;
        }

        public synchronized void setFrameRateSet(final boolean frameRateSet) {
            this.frameRateSet = frameRateSet;
        }

        public synchronized void setFrameOrderingBufferMillis(
                final Long frameOrderingBufferMillis) {
            this.frameOrderingBufferMillis = frameOrderingBufferMillis;
        }

        @Override
        public synchronized void negotiate(final Socket socket) throws IOException {
            if (!frameRateSet) {
                LOGGER.warn("It's strongly recommended to specify video frame rate, "
                        + "otherwise video encoding may be slower (by 20-50 times) "
                        + "and may produce corrupted video");
            }

            NutFrameWriter supplier = new NutFrameWriter(producer, alpha, frameOrderingBufferMillis);
            try (OutputStream output = socket.getOutputStream()) {
                supplier.write(output);
            }
        }
    }
}
