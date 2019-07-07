/*
 *    Copyright  2019 Denis Kokorin
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

import com.github.kokorin.jaffree.util.IOUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PipeInput extends TcpInput<PipeInput> implements Input {
    private final Supplier supplier;

    public PipeInput(Supplier supplier) {
        this.supplier = supplier;
    }

    @Override
    protected Supplier supplier() {
        return supplier;
    }

    public static PipeInput withSupplier(Supplier supplier) {
        return new PipeInput(supplier);
    }

    public static PipeInput pumpFrom(InputStream source) {
        return pumpFrom(source, 1_000_000);
    }

    public static PipeInput pumpFrom(InputStream source, int bufferSize) {
        return new PipeInput(new PipeSupplier(source, bufferSize));
    }

    private static class PipeSupplier implements Supplier {
        private final InputStream source;
        private final int bufferSize;

        public PipeSupplier(InputStream source, int bufferSize) {
            this.source = source;
            this.bufferSize = bufferSize;
        }

        @Override
        public void supplyAndClose(OutputStream destination) {
            try (Closeable toClose = destination) {
                IOUtil.copy(source, destination, bufferSize);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy data", e);
            }
        }
    }
}
