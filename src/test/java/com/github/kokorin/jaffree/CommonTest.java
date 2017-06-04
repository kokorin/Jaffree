package com.github.kokorin.jaffree;

import org.junit.Assert;
import org.junit.Test;

public class CommonTest {
    @Test
    public void testFormatDuration() throws Exception {
        Assert.assertEquals("123.456", Common.formatDuration(123_456));
        Assert.assertEquals("123.056", Common.formatDuration(123_056));
        Assert.assertEquals("123.05", Common.formatDuration(123_050));
        Assert.assertEquals("123", Common.formatDuration(123_000));

        Assert.assertEquals("-123.456", Common.formatDuration(-123_456));
        Assert.assertEquals("-123.056", Common.formatDuration(-123_056));
        Assert.assertEquals("-123.05", Common.formatDuration(-123_050));
        Assert.assertEquals("-123", Common.formatDuration(-123_000));
    }

}