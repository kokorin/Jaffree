package com.github.kokorin.jaffree.result;

import org.junit.Assert;
import org.junit.Test;

public class FFmpegResultTest {
    @Test
    public void fromString() throws Exception {
        String value = "video:1GB audio:2mB subtitle:3kiB other streams:4kB global headers:0kB muxing overhead: 1.285102%";
        FFmpegResult result = FFmpegResult.fromString(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(8_000_000_000L, result.getVideoSize());
        Assert.assertEquals(16_000_000, result.getAudioSize());
        Assert.assertEquals(3 * 1024 * 8, result.getSubtitleSize());
        Assert.assertEquals(32_000, result.getOtherStreamsSize());
        Assert.assertEquals(0, result.getGlobalHeadersSize());
        Assert.assertEquals(0.01285102, result.getMuxingOverheadRatio(), 0.00000001);
    }

}