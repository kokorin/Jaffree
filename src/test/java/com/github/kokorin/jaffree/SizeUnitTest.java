package com.github.kokorin.jaffree;

import org.junit.Assert;
import org.junit.Test;

public class SizeUnitTest {

    @Test
    public void convertTo() {
        Assert.assertEquals(10_000, SizeUnit.GB.convertTo(10, SizeUnit.MB));
    }

    @Test
    public void toBytes() {
        Assert.assertEquals(1, SizeUnit.B.toBytes(1));
        Assert.assertEquals(1024, SizeUnit.KiB.toBytes(1));
    }
}