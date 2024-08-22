package com.github.kokorin.jaffree.util;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import org.junit.Assert;
import org.junit.Test;

public class ParseUtilTest {

    @Test
    public void parseLogLevel() {
        Assert.assertEquals(LogLevel.INFO, ParseUtil.parseLogLevel(
                "[info]"
        ));
        Assert.assertEquals(LogLevel.INFO, ParseUtil.parseLogLevel(
                "[info] frame= 1759 fps=421 q=-1.0 Lsize=    3714kB time=00:00:58.59 bitrate= 519.2kbits/s speed=  14x"
        ));
        Assert.assertEquals(LogLevel.INFO, ParseUtil.parseLogLevel(
                "[libx264 @ 0x5640c7caa580] [info] frame I:13    Avg QP:23.45  size: 11116"
        ));
        Assert.assertEquals(LogLevel.INFO, ParseUtil.parseLogLevel(
                "[libx264 @ 0x5640c7caa580] [info]"
        ));

        Assert.assertEquals(LogLevel.VERBOSE, ParseUtil.parseLogLevel(
                "[AVIOContext @ 0x5640c7c802c0] [verbose] Statistics: 6 seeks, 26 writeouts"
        ));

        Assert.assertEquals(LogLevel.DEBUG, ParseUtil.parseLogLevel(
                "[debug] 4268 frames successfully decoded, 0 decoding errors"
        ));

        Assert.assertEquals(LogLevel.DEBUG, ParseUtil.parseLogLevel(
                "[matroska @ 0x5640c7cb2000] [debug] stream 1 end duration = 58239"
        ));

        Assert.assertEquals(LogLevel.ERROR, ParseUtil.parseLogLevel(
                "[error] .artifacts/MPEG-4/videosadfasdf.mp4: No such file or directory"
        ));

        Assert.assertEquals(LogLevel.TRACE, ParseUtil.parseLogLevel(
                "[mov,mp4,m4a,3gp,3g2,mj2 @ 0x56288d084700] [] stream 3, sample 45, dts 5201270"
        ));

        Assert.assertEquals(LogLevel.TRACE, ParseUtil.parseLogLevel(
                "[mov,mp4,m4a,3gp,3g2,mj2 @ 0x56288d084700] [trace] stream 3, sample 45, dts 5201270"
        ));

        Assert.assertEquals(LogLevel.ERROR, ParseUtil.parseLogLevel(
                "[loudnorm @ 0x55c3e47a6e40] [Eval @ 0x7ffc5e716b40] [error] Undefined constant or missing '(' in 'pfnb'"
        ));

        for (LogLevel logLevel : LogLevel.values()) {
            Assert.assertEquals(logLevel, ParseUtil.parseLogLevel("[" + logLevel.name().toLowerCase() + "]"));
        }

        for (LogLevel logLevel : LogLevel.values()) {
            Assert.assertNull(ParseUtil.parseLogLevel("[" + logLevel.name().toLowerCase()));
        }

        Assert.assertNull(ParseUtil.parseLogLevel("[mov,mp4,m4a,3gp,3g2,mj2 @ 0x56288d084700]"));
        Assert.assertNull(ParseUtil.parseLogLevel("[mov,mp4,m4a,3gp,3g2,mj2 @ 0x56288d084700] [inf"));
    }

    @Test
    public void parseKibiByteFormats() {
        final Long oldFormat = ParseUtil.parseSizeInKibiBytes("2904kB");
        Assert.assertEquals(2904L, oldFormat.longValue());

        final Long newFormat = ParseUtil.parseSizeInKibiBytes("2904KiB");
        Assert.assertEquals(2904L, newFormat.longValue());
    }

    @Test
    public void parseResult() throws Exception {
        String value = "video:1417kB audio:113kB subtitle:0kB other streams:0kB global headers:0kB muxing overhead: unknown";
        FFmpegResult result = ParseUtil.parseResult(value);

        Assert.assertNotNull(result);
        Assert.assertEquals((Long) 1_451_008L, result.getVideoSize());
        Assert.assertEquals((Long) 115_712L, result.getAudioSize());
        Assert.assertEquals((Long) 0L, result.getSubtitleSize());
        Assert.assertEquals((Long) 0L, result.getOtherStreamsSize());
        Assert.assertEquals((Long) 0L, result.getGlobalHeadersSize());
        Assert.assertNull(result.getMuxingOverheadRatio());
    }

    @Test
    public void parseZeroResult() throws Exception {
        String value = "video:0kB audio:0kB subtitle:0kB other streams:0kB global headers:0kB muxing overhead: 0.000000%";
        FFmpegResult result = ParseUtil.parseResult(value);

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getVideoSize().longValue());
        Assert.assertEquals(0, result.getAudioSize().longValue());
        Assert.assertEquals(0, result.getSubtitleSize().longValue());
        Assert.assertEquals(0, result.getOtherStreamsSize().longValue());
        Assert.assertEquals(0, result.getGlobalHeadersSize().longValue());
        Assert.assertEquals(0, result.getMuxingOverheadRatio(), 0.00000001);
    }


    @Test
    public void parseResultWhichDoesntContainResult() throws Exception {
        String value = "This= 5Random String : doesn't contain progre==55 info";
        FFmpegResult result = ParseUtil.parseResult(value);

        Assert.assertNull(result);
    }
}