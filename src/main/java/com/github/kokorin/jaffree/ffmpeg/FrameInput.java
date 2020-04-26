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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <b>It's strongly recommended</b> to specify {@link #setFrameRate(Number)}  for video producing
 */
public class FrameInput extends TcpInput<FrameInput> implements Input {
    private boolean alpha;
    private boolean frameRateSet;
    private Long frameOrderingBufferMillis;

    private final FrameProducer producer;

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameInput.class);

    public FrameInput(FrameProducer producer) {
        super();
        this.producer = producer;
        setFormat("nut");
    }

    /**
     * Whether produced video stream should contain alpha channel
     *
     * @param alpha alpha
     * @return this
     */
    public FrameInput produceAlpha(boolean alpha) {
        this.alpha = alpha;
        return this;
    }

    /**
     * <b>It's strongly recommended</b> to specify videoFrameRate for video producing.
     * <p>
     * Otherwise conversion can be very slow (20-50 times slower) and even can result in corrupted video
     *
     * @param frameRate video frames per second
     * @return this
     */
    @Override
    public FrameInput setFrameRate(Number frameRate) {
        return super.setFrameRate(frameRate);
    }

    /**
     * <b>It's strongly recommended</b> to specify videoFrameRate for video producing.
     * <p>
     * Otherwise conversion can be very slow (20-50 times slower) and even can result in corrupted video
     *
     * @param streamSpecifier stream specifier
     * @param frameRate           video frames per second
     * @return this
     */
    @Override
    public FrameInput setFrameRate(String streamSpecifier, Number frameRate) {
        frameRateSet = true;
        return super.setFrameRate(streamSpecifier, frameRate);
    }

    public FrameInput setFrameOrderingBuffer(long bufferTime, TimeUnit unit) {
        return setFrameOrderingBuffer(unit.toMillis(bufferTime));
    }

    public FrameInput setFrameOrderingBuffer(long bufferTimeMillis) {
        frameOrderingBufferMillis = bufferTimeMillis;
        return this;
    }

    @Override
    protected Supplier supplier() {
        if (!frameRateSet) {
            LOGGER.warn("It's strongly recommended to specify video frame rate, " +
                    "otherwise video encoding may be slower (by 20-50 times) and may produce corrupted video");
        }
        return new NutFrameSupplier(producer, alpha, frameOrderingBufferMillis);
    }

    public static FrameInput withProducer(FrameProducer producer) {
        return new FrameInput(producer);
    }
}
