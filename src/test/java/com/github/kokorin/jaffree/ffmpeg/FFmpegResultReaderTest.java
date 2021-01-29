package com.github.kokorin.jaffree.ffmpeg;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class FFmpegResultReaderTest {
    @Test
    public void parseResult() throws Exception {
        String value = "video:1GB audio:2mB subtitle:3kiB other streams:4kB global headers:0kB muxing overhead: 1.285102%";
        FFmpegResult result = FFmpegResultReader.parseResult(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(1_000_000_000L, result.getVideoSize().longValue());
        Assert.assertEquals(2_000_000, result.getAudioSize().longValue());
        Assert.assertEquals(3 * 1024, result.getSubtitleSize().longValue());
        Assert.assertEquals(4_000, result.getOtherStreamsSize().longValue());
        Assert.assertEquals(0, result.getGlobalHeadersSize().longValue());
        Assert.assertEquals(0.01285102, result.getMuxingOverheadRatio(), 0.00000001);
    }

    @Test
    public void parseZeroResult() throws Exception {
        String value = "video:0kB audio:0kB subtitle:0kB other streams:0kB global headers:0kB muxing overhead: 0.000000%";
        FFmpegResult result = FFmpegResultReader.parseResult(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getVideoSize().longValue());
        Assert.assertEquals(0, result.getAudioSize().longValue());
        Assert.assertEquals(0, result.getSubtitleSize().longValue());
        Assert.assertEquals(0, result.getOtherStreamsSize().longValue());
        Assert.assertEquals(0, result.getGlobalHeadersSize().longValue());
        Assert.assertEquals(0, result.getMuxingOverheadRatio(), 0.00000001);
    }

    @Test
    public void parsResult2() throws Exception {
        String value = "video:1417kB audio:113kB subtitle:0kB other streams:0kB global headers:0kB muxing overhead: unknown";
        FFmpegResult result = FFmpegResultReader.parseResult(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(1_417_000L, result.getVideoSize().longValue());
        Assert.assertEquals(113_000L, result.getAudioSize().longValue());
        Assert.assertEquals(0L, result.getSubtitleSize().longValue());
        Assert.assertEquals(0L, result.getOtherStreamsSize().longValue());
        Assert.assertEquals(0, result.getGlobalHeadersSize().longValue());
        Assert.assertNull(result.getMuxingOverheadRatio());
    }


    @Test
    public void parseResultWhichDoesntContainResult() throws Exception {
        String value = "This= 5Random String : doesn't contain progre==55 info";
        FFmpegResult result = FFmpegResultReader.parseResult(value);

        Assert.assertNull(result);
    }
}