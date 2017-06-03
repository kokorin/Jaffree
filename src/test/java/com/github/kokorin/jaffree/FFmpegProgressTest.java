package com.github.kokorin.jaffree;

import org.junit.Assert;
import org.junit.Test;

public class FFmpegProgressTest {
    @Test
    public void fromString() throws Exception {
        String value = "frame= 5012 fps=25.1 q=-1.0 Lsize=   26463kB time=00:02:47.20 bitrate=1296.6kbits/s speed=1.23e+003x";
        FFmpegProgress result = FFmpegProgress.fromString(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(5012, result.getFrame());
        Assert.assertEquals(25.1, result.getFps(), 0.01);
        Assert.assertEquals(-1.0, result.getQ(), 0.01);
        Assert.assertEquals(26_463_000 * 8, result.getSize());
        Assert.assertEquals(167_200, result.getTime());
        Assert.assertEquals(1296.6, result.getBitrate(), 0.01);
        Assert.assertEquals(1.23e+3, result.getSpeed(), 0.1);
    }

}