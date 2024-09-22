/*
 *    Copyright 2019-2021 Denis Kokorin
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

package com.github.kokorin.jaffree.ffmpeg.output;

import com.github.kokorin.jaffree.net.PipeOutputNegotiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

/**
 * {@link Output} implementation which passes ffmpeg output to {@link OutputStream}.
 * <p>
 * <b>Note</b> {@link OutputStream} is non seekable, so ffmpeg may not correctly finalize many
 * formats. Consider using {@link ChannelOutput}
 *
 * @see ChannelOutput
 */
public class PipeOutput extends TcpOutput<PipeOutput> implements Output {
    private static final int DEFAULT_BUFFER_SIZE = 1_000_000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PipeOutput.class);

    /**
     * Creates {@link PipeOutput}.
     *
     * @param destination output stream to copy to
     * @param bufferSize  buffer size to copy data
     */
    protected PipeOutput(final OutputStream destination, final int bufferSize) {
        this(new PipeOutputNegotiator(destination, bufferSize));
    }

    protected PipeOutput(final PipeOutputNegotiator negotiator) {
        super(negotiator);
        LOGGER.warn("It's recommended to use ChannelOutput since ffmpeg requires seekable output"
                + " for many formats");
    }


    /**
     * Creates {@link PipeOutput} with default buffer size.
     *
     * @param destination output stream to copy to
     * @return PipeOutput
     */
    public static PipeOutput pumpTo(final OutputStream destination) {
        return pumpTo(destination, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Creates {@link PipeOutput}.
     *
     * @param destination output stream to copy to
     * @param bufferSize  buffer size to copy data
     * @return PipeOutput
     */
    public static PipeOutput pumpTo(final OutputStream destination, final int bufferSize) {
        return new PipeOutput(destination, bufferSize);
    }

}
