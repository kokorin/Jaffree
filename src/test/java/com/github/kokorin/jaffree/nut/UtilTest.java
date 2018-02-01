package com.github.kokorin.jaffree.nut;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class UtilTest {
    @Test
    public void convertTimestamp() throws Exception {
        Rational timebaseFrom = new Rational(1, 10);
        Rational timebaseTo = new Rational(1, 10);
        long result = Util.convertTimestamp(10, timebaseFrom, timebaseTo);
        Assert.assertEquals(10L, result);


        timebaseFrom = new Rational(1, 10);
        timebaseTo = new Rational(1, 1000);
        result = Util.convertTimestamp(10, timebaseFrom, timebaseTo);
        Assert.assertEquals(1000L, result);
    }

    @Test
    public void byteBuffer() {
        int v = 0x01020304;
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).asIntBuffer().put(v);
        System.out.println(Arrays.toString(bytes));
    }
}