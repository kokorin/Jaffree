package com.github.kokorin.jaffree.ffmpeg;

import org.junit.Assert;
import org.junit.Test;

public class FFmpegResultReaderTest {
    @Test
    public void parseResult() throws Exception {
        String value = "video:1GB audio:2mB subtitle:3kiB other streams:4kB global headers:0kB muxing overhead: 1.285102%";
        FFmpegResult result = FFmpegResultReader.parsResult(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(1_000_000_000L, result.getVideoSize());
        Assert.assertEquals(2_000_000, result.getAudioSize());
        Assert.assertEquals(3 * 1024, result.getSubtitleSize());
        Assert.assertEquals(4_000, result.getOtherStreamsSize());
        Assert.assertEquals(0, result.getGlobalHeadersSize());
        Assert.assertEquals(0.01285102, result.getMuxingOverheadRatio(), 0.00000001);
    }

    @Test
    public void parsResult2() throws Exception {
        String value = "video:1417kB audio:113kB subtitle:0kB other streams:0kB global headers:0kB muxing overhead: unknown";
        FFmpegResult result = FFmpegResultReader.parsResult(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(1_417_000L, result.getVideoSize());
        Assert.assertEquals(113_000L, result.getAudioSize());
        Assert.assertEquals(0L, result.getSubtitleSize());
        Assert.assertEquals(0L, result.getOtherStreamsSize());
        Assert.assertEquals(0, result.getGlobalHeadersSize());
        Assert.assertEquals(0, result.getMuxingOverheadRatio(), 0.0000001);
    }


    @Test
    public void parseResultWhichDoesntContainResult() throws Exception {
        String value = "This= 5Random String : doesn't contain progre==55 info";
        FFmpegResult result = FFmpegResultReader.parsResult(value);

        Assert.assertNull(result);
    }

    @Test
    public void parseProgressWhenCopingCodecs() throws Exception {
        String value = "frame= 5012 fps=25.1 q=-1.0 Lsize=   26463kB time=00:02:47.20 bitrate=1296.6kbits/s speed=1.23e+003x";
        FFmpegProgress result = FFmpegResultReader.parseProgress(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(5012, result.getFrame());
        Assert.assertEquals(25.1, result.getFps(), 0.01);
        Assert.assertEquals(-1.0, result.getQ(), 0.01);
        Assert.assertEquals(26_463_000, result.getSize());
        Assert.assertEquals(167_200, result.getTime());
        Assert.assertEquals(1296.6, result.getBitrate(), 0.01);
        Assert.assertEquals(1.23e+3, result.getSpeed(), 0.1);
    }

    @Test
    public void parseProgressWhenCopingCodecs2() throws Exception {
        String value = "frame=   33 fps=0.0 q=-1.0 Lsize=      71kB time=00:00:02.79 bitrate= 207.3kbits/s speed=11.9x   ";
        FFmpegProgress result = FFmpegResultReader.parseProgress(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(33, result.getFrame());
        Assert.assertEquals(0.0, result.getFps(), 0.01);
        Assert.assertEquals(-1.0, result.getQ(), 0.01);
        Assert.assertEquals(71_000, result.getSize());
        Assert.assertEquals(2_790, result.getTime());
        Assert.assertEquals(207.3, result.getBitrate(), 0.01);
        Assert.assertEquals(11.9, result.getSpeed(), 0.1);
    }


    @Test
    public void parseProgressWhenReencodingSmallVideo() throws Exception {
        String value = "frame=  358 fps=0.0 q=-1.0 Lsize=     443kB time=00:00:29.71 bitrate= 122.0kbits/s";
        FFmpegProgress result = FFmpegResultReader.parseProgress(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(358, result.getFrame());
        Assert.assertEquals(0.0, result.getFps(), 0.01);
        Assert.assertEquals(-1.0, result.getQ(), 0.01);
        Assert.assertEquals(443_000, result.getSize());
        Assert.assertEquals(29_710, result.getTime());
        Assert.assertEquals(122.0, result.getBitrate(), 0.01);
        Assert.assertTrue(Double.isNaN(result.getSpeed()));
    }

    @Test
    public void parseProgressWhenEncoding() throws Exception {
        String value = "frame=  184 fps=0.0 q=-1.0 Lsize=      38kB time=00:00:07.24 bitrate=  43.4kbits/s dup=73 drop=0 speed=19.5x";
        FFmpegProgress result = FFmpegResultReader.parseProgress(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(184, result.getFrame());
        Assert.assertEquals(0, result.getFps(), 0.01);
        Assert.assertEquals(-1.0, result.getQ(), 0.01);
        Assert.assertEquals(38_000, result.getSize());
        Assert.assertEquals(7_240, result.getTime());
        Assert.assertEquals(43.4, result.getBitrate(), 0.01);
        Assert.assertEquals(73, result.getDup());
        Assert.assertEquals(0, result.getDrop());
        Assert.assertEquals(19.5, result.getSpeed(), 0.1);
    }

    @Test
    public void parseProgressWhenEncodingUnknownDuration() throws Exception {
        String value = "frame=  430 fps= 85 q=28.0 size=      46kB time=00:00:17.53 bitrate=  21.5kbits/s speed=3.47x";
        FFmpegProgress result = FFmpegResultReader.parseProgress(value);

        Assert.assertNotNull(result);
    }

    @Test
    public void parseProgressWhenEncodingUnknownDuration2() throws Exception {
        String value = "frame=  495 fps= 89 q=28.0 size=     124kB time=00:00:20.15 bitrate=  50.3kbits/s dup=1 drop=0 speed=3.63x";
        FFmpegProgress result = FFmpegResultReader.parseProgress(value);

        Assert.assertNotNull(result);
    }

    @Test
    public void parseProgressWhenSizeAndBitrateAreNotAvailable() throws Exception {
        String value = "frame=15195 fps=819 q=-0.0 size=N/A time=00:10:07.80 bitrate=N/A";
        FFmpegProgress result = FFmpegResultReader.parseProgress(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getSize());
        Assert.assertTrue(Double.isNaN(result.getBitrate()));
    }

    @Test
    public void parseProgressWhichDoesntContainProgress() throws Exception {
        String value = "This= 5Random String doesn't contain progre==55 info";
        FFmpegProgress result = FFmpegResultReader.parseProgress(value);

        Assert.assertNull(result);
    }
}