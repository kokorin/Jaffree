package com.github.kokorin.jaffree.nut;

import com.github.kokorin.jaffree.Rational;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class UtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilTest.class);

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
        LOGGER.debug(Arrays.toString(bytes));
    }
}