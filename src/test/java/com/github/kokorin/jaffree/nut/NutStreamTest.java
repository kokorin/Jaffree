package com.github.kokorin.jaffree.nut;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class NutStreamTest {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
    private final NutOutputStream output = new NutOutputStream(outputStream);

    @Test
    public void testReadWrite() throws Exception {
        output.writeValue(1);
        output.writeValue(356);
        output.writeValue(0x7ABCDEF012L);
        output.writeSignedValue(42);
        output.writeSignedValue(0);
        output.writeSignedValue(-142);
        output.writeLong(0x123ABCL);
        output.writeInt(0xABC123);
        output.writeByte(12);
        output.writeBytes(new byte[]{1, 2, 3});
        output.writeVariableBytes(new byte[]{5, 6, 7});
        output.writeCString("Jaffree");
        output.writeVariablesString("Test/\\Me");
        output.writeTimestamp(2, new Timestamp(1, 1100));
        output.close();

        NutInputStream input = new NutInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        long prevPosition = 0;
        Assert.assertEquals(1L, input.readValue());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertEquals(356L, input.readValue());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertEquals(0x7ABCDEF012L, input.readValue());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertEquals(42, input.readSignedValue());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertEquals(0, input.readSignedValue());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertEquals(-142, input.readSignedValue());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertEquals(0x123ABCL, input.readLong());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertEquals(0xABC123, input.readInt());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertEquals(12, input.readByte());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertArrayEquals(new byte[]{1, 2, 3}, input.readBytes(3));
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertArrayEquals(new byte[]{5, 6, 7}, input.readVariableBytes());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertEquals("Jaffree", input.readCString());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertEquals("Test/\\Me", input.readVariableString());
        prevPosition = assertIncreasedPosition(prevPosition, input);

        Assert.assertEquals(new Timestamp(1, 1100), input.readTimestamp(2));
        assertIncreasedPosition(prevPosition, input);
    }


    @Test
    public void checkNextByte() throws Exception {
        NutInputStream input = new NutInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        byte b = input.checkNextByte();

        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals("checkNextByte must not increase read position", b, input.checkNextByte());
        }
    }


    private static long assertIncreasedPosition(long prevPosition, NutInputStream input) {
        Assert.assertTrue(input.getPosition() > prevPosition);
        return input.getPosition();
    }

}