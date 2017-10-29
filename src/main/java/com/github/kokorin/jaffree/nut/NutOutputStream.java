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

package com.github.kokorin.jaffree.nut;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

public class NutOutputStream implements AutoCloseable {
    private final OutputStream output;
    private final CRC32 crc32 = new CRC32();

    public NutOutputStream(OutputStream output) {
        if (!(output instanceof BufferedOutputStream)) {
            output = new BufferedOutputStream(output);
        }

        this.output = output;
    }

    public void writeValue(long value) throws IOException {
        value &= 0x7FFFFFFFFFFFFFFFL; // FIXME: Can only encode up to 63 bits ATM.
        int i;
        for (i = 7; ; i += 7) {
            if (value >> i == 0) {
                break;
            }
        }

        for (i -= 7; i > 0; i -= 7) {
            int b = (int) (0x80 | (value >> i));
            output.write(b);
            crc32.update(b);
        }

        int b = (int) (value & 0x7F);
        output.write(b);
        crc32.update(b);
    }

    public void writeSignedValue(long signed) throws IOException {
        long value;
        if (signed > 0) {
            value = signed << 1;
        } else {
            value = 1 - (signed << 1);
        }
        value--;
        writeValue(value);
    }

    public void writeLong(long value) throws IOException {
        for (int i = 7; i >= 0; i--) {
            int b = (int) ((value >> (8 * i)) & 0xFF);
            output.write(b);
            crc32.update(b);
        }
    }

    public void writeInt(long value) throws IOException {
        for (int i = 3; i >= 0; i--) {
            int b = (int) ((value >> (8 * i)) & 0xFF);
            output.write(b);
            crc32.update(b);
        }
    }

    public void writeByte(int value) throws IOException {
        output.write(value);
        crc32.update(value);
    }

    public void writeVariablesString(String data) throws IOException{
        writeVariableBytes(data.getBytes());
    }

    public void writeVariableBytes(byte[] data) throws IOException{
        writeValue(data.length);
        writeBytes(data);
    }


    public void writeTimestamp(int timeBaseCount, Timestamp timestamp) throws IOException {
        long value = timestamp.pts * timeBaseCount + timestamp.timebaseId;
        writeValue(value);
    }

    public void writeCString(String data) throws IOException{
        writeBytes(data.getBytes());
        writeByte(0);
    }

    public void writeBytes(byte[] data) throws IOException {
        output.write(data);
        crc32.update(data);
    }

    public void resetCrc32() {
        crc32.reset();
    }

    public void writeCrc32() throws IOException{
        writeValue(crc32.getValue());
    }

    @Override
    public void close() throws Exception {
        output.close();
    }
}
