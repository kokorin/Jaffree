package com.github.kokorin.jaffree.nut;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class NutStreamTest {

    @Test
    public void testReadWrite() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        NutOutputStream output = new NutOutputStream(outputStream);

        output.writeValue(1);
        output.writeValue(356);
        output.writeValue(0x7ABCDEF0123456L);
        output.writeSignedValue(42);
        output.writeSignedValue(0);
        output.writeSignedValue(-142);
        output.writeLong(0x123ABCL);
        output.writeInt(0xABC123);
        output.writeByte(12);
        output.writeBytes(new byte[]{1, 2, 3});
        output.writeVariableBytes(new byte[]{5, 6, 7});
        output.writeCString("");
        output.writeCString("Jaffree");
        output.writeVariablesString("");
        output.writeVariablesString("Test/\\Me");
        output.writeTimestamp(2, new Timestamp(1, 1100));
        output.writeBytes(new byte[1_000_000]);
        output.close();

        NutInputStream input = new NutInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        long position = 0;
        Assert.assertEquals(position, input.getPosition());

        Assert.assertEquals(1L, input.readValue());
        Assert.assertEquals(position += 1, input.getPosition());

        Assert.assertEquals(356L, input.readValue());
        Assert.assertEquals(position += 2, input.getPosition());

        Assert.assertEquals(0x7ABCDEF0123456L, input.readValue());
        Assert.assertEquals(position += 8, input.getPosition());

        Assert.assertEquals(42, input.readSignedValue());
        Assert.assertEquals(position += 1, input.getPosition());

        Assert.assertEquals(0, input.readSignedValue());
        Assert.assertEquals(position += 1, input.getPosition());

        Assert.assertEquals(-142, input.readSignedValue());
        Assert.assertEquals(position += 2, input.getPosition());

        Assert.assertEquals(0x123ABCL, input.readLong());
        Assert.assertEquals(position += 8, input.getPosition());

        Assert.assertEquals(0xABC123, input.readInt());
        Assert.assertEquals(position += 4, input.getPosition());

        Assert.assertEquals(12, input.readByte());
        Assert.assertEquals(position += 1, input.getPosition());

        Assert.assertArrayEquals(new byte[]{1, 2, 3}, input.readBytes(3));
        Assert.assertEquals(position += 3, input.getPosition());

        Assert.assertArrayEquals(new byte[]{5, 6, 7}, input.readVariableBytes());
        Assert.assertEquals(position += 4, input.getPosition());

        Assert.assertEquals("", input.readCString());
        Assert.assertEquals(position += 1, input.getPosition());

        Assert.assertEquals("Jaffree", input.readCString());
        Assert.assertEquals(position += 8, input.getPosition());

        Assert.assertEquals("", input.readVariableString());
        Assert.assertEquals(position += 1, input.getPosition());

        Assert.assertEquals("Test/\\Me", input.readVariableString());
        Assert.assertEquals(position += 9, input.getPosition());

        Assert.assertEquals(new Timestamp(1, 1100), input.readTimestamp(2));
        Assert.assertEquals(position += 2, input.getPosition());

        input.skipBytes(1_000_000);
        Assert.assertEquals(position + 1_000_000, input.getPosition());
    }


    @Test
    public void checkNextByte() throws Exception {
        NutInputStream input = new NutInputStream(new ByteArrayInputStream(new byte[]{42}));

        int nextByte = input.checkNextByte();
        Assert.assertEquals(42, nextByte);

        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals("checkNextByte must not increase read position", nextByte, input.checkNextByte());
        }

        Assert.assertEquals(42, input.readByte());

        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals("checkNextByte must return -1 if no more bytes are available", -1, input.checkNextByte());
        }
    }
}