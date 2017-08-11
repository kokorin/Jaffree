package com.github.kokorin.jaffree.ffmpeg;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class FFmpegProgressTest {
    @Test
    public void fromStringWhenCopingCodecs() throws Exception {
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

    @Test
    public void fromStringWhenCopingCodecs2() throws Exception {
        String value = "frame=   33 fps=0.0 q=-1.0 Lsize=      71kB time=00:00:02.79 bitrate= 207.3kbits/s speed=11.9x   ";
        FFmpegProgress result = FFmpegProgress.fromString(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(33, result.getFrame());
        Assert.assertEquals(0.0, result.getFps(), 0.01);
        Assert.assertEquals(-1.0, result.getQ(), 0.01);
        Assert.assertEquals(71_000 * 8, result.getSize());
        Assert.assertEquals(2_790, result.getTime());
        Assert.assertEquals(207.3, result.getBitrate(), 0.01);
        Assert.assertEquals(11.9, result.getSpeed(), 0.1);
    }


    @Test
    public void fromStringWhenReencodingSmallVideo() throws Exception {
        String value = "frame=  358 fps=0.0 q=-1.0 Lsize=     443kB time=00:00:29.71 bitrate= 122.0kbits/s";
        FFmpegProgress result = FFmpegProgress.fromString(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(358, result.getFrame());
        Assert.assertEquals(0.0, result.getFps(), 0.01);
        Assert.assertEquals(-1.0, result.getQ(), 0.01);
        Assert.assertEquals(443_000 * 8, result.getSize());
        Assert.assertEquals(29_710, result.getTime());
        Assert.assertEquals(122.0, result.getBitrate(), 0.01);
        Assert.assertTrue(Double.isNaN(result.getSpeed()));
    }

    @Test
    @Ignore
    public void testFromStringWhenEncoding() throws Exception {
        String value = "frame=  184 fps=0.0 q=-1.0 Lsize=      38kB time=00:00:07.24 bitrate=  43.4kbits/s dup=73 drop=0 speed=19.5x";
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

    @Test
    @Ignore
    public void testFromStringWhenEncodingUnknownDuration() throws Exception {
        String value = "frame=  430 fps= 85 q=28.0 size=      46kB time=00:00:17.53 bitrate=  21.5kbits/s speed=3.47x";
        FFmpegProgress result = FFmpegProgress.fromString(value);

        Assert.assertNotNull(result);
    }

    @Test
    @Ignore
    public void testFromStringWhenEncodingUnknownDuration2() throws Exception {
        String value = "frame=  495 fps= 89 q=28.0 size=     124kB time=00:00:20.15 bitrate=  50.3kbits/s dup=1 drop=0 speed=3.63x";
        FFmpegProgress result = FFmpegProgress.fromString(value);

        Assert.assertNotNull(result);
    }
}