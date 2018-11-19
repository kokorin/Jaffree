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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

/**
 * <b>It's strongly recommended</b> to specify {@link #setFrameRate(Number)}  for video producing
 */
public class FrameInput extends BaseInput<FrameInput> implements Input {
    private boolean alpha;
    private boolean frameRateSet;
    private Long frameOrderingBufferMillis;

    private final FrameProducer producer;
    private final ServerSocket serverSocket;

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameInput.class);

    public FrameInput(FrameProducer producer) {
        this.producer = producer;
        this.serverSocket = allocateSocket();
        setFormat("nut");
        setInput("tcp://127.0.0.1:" + serverSocket.getLocalPort());
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
     * @param value video frames per second
     * @return this
     */
    @Override
    public FrameInput setFrameRate(Number value) {
        return super.setFrameRate(value);
    }

    /**
     * <b>It's strongly recommended</b> to specify videoFrameRate for video producing.
     * <p>
     * Otherwise conversion can be very slow (20-50 times slower) and even can result in corrupted video
     * @param streamSpecifier stream specifier
     * @param value video frames per second
     * @return this
     */
    @Override
    public FrameInput setFrameRate(String streamSpecifier, Number value) {
        frameRateSet = true;
        return super.setFrameRate(streamSpecifier, value);
    }

    public FrameInput setFrameOrderingBuffer(long bufferTime, TimeUnit unit) {
        return setFrameOrderingBuffer(unit.toMillis(bufferTime));
    }

    public FrameInput setFrameOrderingBuffer(long bufferTimeMillis) {
        frameOrderingBufferMillis = bufferTimeMillis;
        return this;
    }

    Runnable createWriter() {
        if (!frameRateSet) {
            LOGGER.warn("It's strongly recommended to specify video frame rate, " +
                    "otherwise video encoding may be slower (by 20-50 times) and may produce corrupted video");
        }
        return new NutFrameWriter(producer, alpha, serverSocket, frameOrderingBufferMillis);
    }

    private static ServerSocket allocateSocket() {
        try {
            return new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
        } catch (IOException e) {
            throw new RuntimeException("Failed to allocate socket", e);
        }
    }

    public static FrameInput withProducer(FrameProducer producer) {
        return new FrameInput(producer);
    }
}
