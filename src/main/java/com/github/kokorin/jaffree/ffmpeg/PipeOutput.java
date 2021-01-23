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

package com.github.kokorin.jaffree.ffmpeg;

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
    private final PipeOutputNegotiator negotiator;

    private static final int DEFAULT_BUFFER_SIZE = 1_000_000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PipeOutput.class);

    public PipeOutput(OutputStream destination) {
        this(new PipeOutputNegotiator(destination));
    }

    protected PipeOutput(PipeOutputNegotiator negotiator) {
        super(negotiator);
        this.negotiator = negotiator;
        LOGGER.warn("It's recommended to use ChannelOutput since ffmpeg requires seekable output"
                + " for many formats");
    }

    public PipeOutput setBufferSize(int bufferSize) {
        negotiator.setBufferSize(bufferSize);
        return this;
    }

    public static PipeOutput pumpTo(OutputStream destination) {
        return new PipeOutput(destination);
    }

    public static PipeOutput pumpTo(OutputStream destination, int bufferSize) {
        return pumpTo(destination)
                .setBufferSize(bufferSize);
    }

}
