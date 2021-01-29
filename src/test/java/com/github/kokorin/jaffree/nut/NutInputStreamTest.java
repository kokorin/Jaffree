package com.github.kokorin.jaffree.nut;

import org.apache.commons.io.input.ClosedInputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;

public class NutInputStreamTest {
    private final NutInputStream closed = new NutInputStream(new ClosedInputStream());
    private final NutInputStream tooShortToReadValue = new NutInputStream(
            new ByteArrayInputStream(new byte[]{(byte) 0xFF})
    );
    private final NutInputStream tooShortToReadVarBytes = new NutInputStream(
            new ByteArrayInputStream(new byte[]{(byte) 0x4, (byte) 42})
    );

    @Rule
    public final Timeout timeout = Timeout.seconds(1);

    @Test
    public void getPosition() {
        Assert.assertEquals(0, closed.getPosition());
    }

    @Test(expected = EOFException.class)
    public void readValueFromClosed() throws IOException {
        closed.readValue();
    }

    @Test(expected = EOFException.class)
    public void readValueFromTooShort() throws IOException {
        tooShortToReadValue.readValue();
    }

    @Test(expected = EOFException.class)
    public void readSignedValueFromClosed() throws IOException {
        closed.readSignedValue();
    }

    @Test(expected = EOFException.class)
    public void readSignedValueFromTooShort() throws IOException {
        tooShortToReadValue.readSignedValue();
    }

    @Test(expected = EOFException.class)
    public void readLongFromClosed() throws IOException {
        closed.readLong();
    }

    @Test(expected = EOFException.class)
    public void readLongFromTooShort() throws IOException {
        tooShortToReadValue.readLong();
    }

    @Test(expected = EOFException.class)
    public void readIntFromClosed() throws IOException {
        closed.readInt();
    }

    @Test(expected = EOFException.class)
    public void readIntFromTooShort() throws IOException {
        tooShortToReadValue.readInt();
    }

    @Test(expected = EOFException.class)
    public void readByteFromClosed() throws IOException {
        closed.readByte();
    }

    @Test(expected = EOFException.class)
    public void readVariableStringFromClosed() throws IOException {
        closed.readVariableString();
    }

    @Test(expected = EOFException.class)
    public void readVariableStringFromTooShort() throws IOException {
        tooShortToReadValue.readVariableString();
    }

    @Test(expected = EOFException.class)
    public void readVariableStringFromTooShort2() throws IOException {
        tooShortToReadVarBytes.readVariableString();
    }

    @Test(expected = EOFException.class)
    public void readCStringFromClosed() throws IOException {
        closed.readCString();
    }

    @Test(expected = EOFException.class)
    public void readCStringFromTooShort() throws IOException {
        closed.readCString();
    }

    @Test(expected = EOFException.class)
    public void readVariableBytesFromClosed() throws IOException {
        closed.readVariableBytes();
    }

    @Test(expected = EOFException.class)
    public void readVariableBytesFromTooShort() throws IOException {
        tooShortToReadValue.readVariableBytes();
    }

    @Test(expected = EOFException.class)
    public void readVariableBytesFromTooShort2() throws IOException {
        tooShortToReadVarBytes.readVariableBytes();
    }

    @Test(expected = EOFException.class)
    public void readTimestampFromClosed() throws IOException {
        closed.readTimestamp(4);
    }

    @Test(expected = EOFException.class)
    public void readTimestampFromTooShort() throws IOException {
        tooShortToReadValue.readTimestamp(42);
    }

    @Test
    public void checkNextByte() throws IOException {
        closed.checkNextByte();
    }

    @Test
    public void hasMoreData() throws IOException {
        Assert.assertFalse(closed.hasMoreData());
        Assert.assertTrue(tooShortToReadValue.hasMoreData());
        Assert.assertTrue(tooShortToReadVarBytes.hasMoreData());
    }


    @Test(expected = EOFException.class)
    public void readBytesFromClosed() throws IOException {
        closed.readBytes(42);
    }


    @Test(expected = EOFException.class)
    public void readBytesFromTooShort() throws IOException {
        tooShortToReadValue.readBytes(42);
    }

    @Test(expected = EOFException.class)
    public void readBytesFromTooShort2() throws IOException {
        tooShortToReadVarBytes.readBytes(42);
    }

    @Test(expected = EOFException.class)
    public void skipBytesFromClosed() throws IOException {
        closed.skipBytes(42);
    }

    @Test(expected = EOFException.class)
    public void skipBytesFromTooShort() throws IOException {
        tooShortToReadValue.skipBytes(42);
    }

    @Test(expected = EOFException.class)
    public void skipBytesFromTooShort2() throws IOException {
        tooShortToReadVarBytes.skipBytes(42);
    }
}