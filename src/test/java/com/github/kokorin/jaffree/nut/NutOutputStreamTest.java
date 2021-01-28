package com.github.kokorin.jaffree.nut;

import org.apache.commons.io.output.ClosedOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class NutOutputStreamTest {
    private NutOutputStream closed = new NutOutputStream(new ClosedOutputStream());

    @Test(expected = IOException.class)
    public void writeValue() throws Exception {
        closed.writeValue(42);
        closed.flush();
    }

    @Test(expected = IOException.class)
    public void writeSignedValue() throws Exception {
        closed.writeSignedValue(42);
        closed.flush();
    }

    @Test(expected = IOException.class)
    public void writeLong() throws Exception {
        closed.writeLong(42);
        closed.flush();
    }

    @Test(expected = IOException.class)
    public void writeInt() throws Exception {
        closed.writeInt(42);
        closed.flush();
    }

    @Test(expected = IOException.class)
    public void writeByte() throws Exception {
        closed.writeByte(42);
        closed.flush();
    }

    @Test(expected = IOException.class)
    public void writeVariablesString() throws Exception {
        closed.writeVariablesString("42");
        closed.flush();
    }

    @Test(expected = IOException.class)
    public void writeVariableBytes() throws Exception {
        closed.writeVariableBytes(new byte[]{42});
        closed.flush();
    }

    @Test(expected = IOException.class)
    public void writeTimestamp() throws Exception {
        closed.writeTimestamp(42, new Timestamp(4, 2));
        closed.flush();
    }

    @Test(expected = IOException.class)
    public void writeCString() throws Exception {
        closed.writeCString("42");
        closed.flush();
    }

    @Test(expected = IOException.class)
    public void writeBytes() throws Exception {
        closed.writeBytes(new byte[]{42});
        closed.flush();
    }

    @Test
    public void resetCrc32() throws Exception {
        closed.resetCrc32();
    }

    @Test(expected = IOException.class)
    public void writeCrc32() throws Exception {
        closed.writeCrc32();
        closed.flush();
    }

    @Test
    public void getPosition() throws Exception {
        Assert.assertEquals(0, closed.getPosition());
    }

    @Test(expected = IOException.class)
    public void flush() throws Exception {
        closed.flush();
    }
}