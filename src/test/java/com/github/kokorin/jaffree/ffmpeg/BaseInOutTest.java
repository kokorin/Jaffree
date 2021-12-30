package com.github.kokorin.jaffree.ffmpeg;

import org.junit.Assert;
import org.junit.Test;

import static com.github.kokorin.jaffree.ffmpeg.Args.formatDuration;

public class BaseInOutTest {
    @Test
    public void testFormatDuration() throws Exception {
        Assert.assertEquals("123.456", formatDuration(123_456));
        Assert.assertEquals("123.056", formatDuration(123_056));
        Assert.assertEquals("123.050", formatDuration(123_050));
        Assert.assertEquals("123.000", formatDuration(123_000));

        Assert.assertEquals("-123.456", formatDuration(-123_456));
        Assert.assertEquals("-123.056", formatDuration(-123_056));
        Assert.assertEquals("-123.050", formatDuration(-123_050));
        Assert.assertEquals("-123.000", formatDuration(-123_000));

        Assert.assertEquals("1000.000", formatDuration(1_000_000));
    }

}