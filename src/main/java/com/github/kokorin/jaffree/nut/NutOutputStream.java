package com.github.kokorin.jaffree.nut;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NutOutputStream implements AutoCloseable {
    private final OutputStream output;

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
        }
        output.write((int) (value & 0x7F));
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
            output.write((int) ((value >> (8 * i)) & 0xFF));
        }
    }

    public void writeInt(long value) throws IOException {
        for (int i = 3; i >= 0; i--) {
            output.write((int) ((value >> (8 * i)) & 0xFF));
        }
    }

    public void writeByte(int value) throws IOException {
        output.write(value);
    }

    public void writeVariablesString(String data) throws IOException{
        writeVariableBytes(data.getBytes());
    }

    public void writeVariableBytes(byte[] data) throws IOException{
        writeValue(data.length);
        writeBytes(data);
    }

    public void writeCString(String data) throws IOException{
        writeBytes(data.getBytes());
        writeByte(0);
    }

    public void writeBytes(byte[] data) throws IOException {
        output.write(data);
    }

    @Override
    public void close() throws Exception {
        output.close();
    }
}
