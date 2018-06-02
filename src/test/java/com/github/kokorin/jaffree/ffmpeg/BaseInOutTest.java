package com.github.kokorin.jaffree.ffmpeg;

import org.junit.Assert;
import org.junit.Test;

public class BaseInOutTest {
    @Test
    public void testFormatDuration() throws Exception {
        Assert.assertEquals("123.456", BaseInOut.formatDuration(123_456));
        Assert.assertEquals("123.056", BaseInOut.formatDuration(123_056));
        Assert.assertEquals("123.050", BaseInOut.formatDuration(123_050));
        Assert.assertEquals("123.000", BaseInOut.formatDuration(123_000));

        Assert.assertEquals("-123.456", BaseInOut.formatDuration(-123_456));
        Assert.assertEquals("-123.056", BaseInOut.formatDuration(-123_056));
        Assert.assertEquals("-123.050", BaseInOut.formatDuration(-123_050));
        Assert.assertEquals("-123.000", BaseInOut.formatDuration(-123_000));

        Assert.assertEquals("1000.000", BaseInOut.formatDuration(1_000_000));
    }

}