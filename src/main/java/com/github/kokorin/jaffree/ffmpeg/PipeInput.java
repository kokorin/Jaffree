/*
 *    Copyright  2019-2021 Denis Kokorin
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

import com.github.kokorin.jaffree.network.OutputStreamSupplier;
import com.github.kokorin.jaffree.network.OutputStreamTcpNegotiator;
import com.github.kokorin.jaffree.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

public class PipeInput extends TcpInput<PipeInput> implements Input {
    // TODO probably it's better for constructor to accept OutputStream, not OutputStreamSupplier
    public PipeInput(OutputStreamSupplier supplier) {
        super(new OutputStreamTcpNegotiator(supplier));
    }

    public static PipeInput withSupplier(OutputStreamSupplier supplier) {
        return new PipeInput(supplier);
    }

    public static PipeInput pumpFrom(InputStream source) {
        return pumpFrom(source, 1_000_000);
    }

    public static PipeInput pumpFrom(InputStream source, int bufferSize) {
        return new PipeInput(new PipeSupplier(source, bufferSize));
    }

    private static class PipeSupplier implements OutputStreamSupplier {
        private final InputStream source;
        private final int bufferSize;

        private static final Logger LOGGER = LoggerFactory.getLogger(PipeSupplier.class);


        public PipeSupplier(InputStream source, int bufferSize) {
            this.source = source;
            this.bufferSize = bufferSize;
        }

        @Override
        public void supply(OutputStream destination) throws IOException {
            try {
                IOUtil.copy(source, destination, bufferSize);
            } catch (SocketException e) {
                // client has no way to notify server that no more data is needed
                LOGGER.debug("Ignoring exception: " + e.getMessage());
            }
        }
    }
}
