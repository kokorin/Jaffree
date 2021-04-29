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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.net.PipeInputNegotiator;

import java.io.InputStream;

/**
 * {@link Input} implementation which passes {@link InputStream} to ffprobe as input.
 *
 * @see ChannelInput
 */
public class PipeInput extends TcpInput {
    private static final int DEFAULT_BUFFER_SIZE = 1_000_000;

    /**
     * Creates {@link PipeInput}.
     *
     * @param source     input stream to copy from
     * @param bufferSize buffer size to copy data
     */
    protected PipeInput(final InputStream source, final int bufferSize) {
        super(new PipeInputNegotiator(source, bufferSize));
    }

    /**
     * Creates {@link PipeInput}.
     *
     * @param source input stream to copy from
     * @return PipeInput
     */
    public static PipeInput pumpFrom(final InputStream source) {
        return pumpFrom(source, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Creates {@link PipeInput}.
     *
     * @param source     input stream to copy from
     * @param bufferSize buffer size to copy data
     * @return PipeInput
     */
    public static PipeInput pumpFrom(final InputStream source, final int bufferSize) {
        return new PipeInput(source, bufferSize);
    }
}
