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

import com.github.kokorin.jaffree.network.OutputStreamTcpNegotiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Allows to supply ffmpeg with audio & video frames constructed in Java.
 *
 * <b>It's strongly recommended</b> to specify {@link #setFrameRate(Number)} for video producing.
 *
 * @see FrameProducer
 */
public class FrameInput extends TcpInput<FrameInput> implements Input {
    private boolean alpha;
    private boolean frameRateSet;
    private Long frameOrderingBufferMillis;

    private final FrameProducer producer;

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameInput.class);

    /**
     * Creates {@link FrameInput} for {@link FFmpeg}.
     *
     * @param producer                  frame producer
     * @param alphaChannel
     * @param frameOrderingBufferMillis
     */
    public FrameInput(final FrameProducer producer, final boolean alphaChannel, final Long frameOrderingBufferMillis) {
        super(new OutputStreamTcpNegotiator(new NutFrameSupplier(producer, alphaChannel, frameOrderingBufferMillis)));
        this.producer = producer;
        setFormat("nut");
    }

    /**
     * Whether produced video stream should contain alpha channel.
     *
     * @param containsAlphaChannel alpha channel
     * @return this
     */
    // TODO rename method
    public FrameInput produceAlpha(final boolean containsAlphaChannel) {
        this.alpha = containsAlphaChannel;
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
        frameRateSet = true;
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
        frameRateSet = true;
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
        frameOrderingBufferMillis = bufferTimeMillis;
        return this;
    }

    /**
     * Creates {@link com.github.kokorin.jaffree.ffmpeg.TcpInput.Supplier} which is capable of
     * reading frames from {@link FrameProducer} and passing them to ffmpeg via TCP socket.
     *
     * @return bytes supplier
     */
    @Override
    protected Supplier supplier() {
        if (!frameRateSet) {
            LOGGER.warn("It's strongly recommended to specify video frame rate, "
                    + "otherwise video encoding may be slower (by 20-50 times) "
                    + "and may produce corrupted video");
        }
        return new NutFrameSupplier(producer, alpha, frameOrderingBufferMillis);
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
}
