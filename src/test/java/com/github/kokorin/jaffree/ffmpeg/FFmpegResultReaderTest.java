package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.LogLevel;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class FFmpegResultReaderTest {

    @Test
    public void detectLogLevel() {
        Assert.assertEquals(LogLevel.INFO, FFmpegResultReader.detectLogLevel(
                "[info]"
        ));
        Assert.assertEquals(LogLevel.INFO, FFmpegResultReader.detectLogLevel(
                "[info] frame= 1759 fps=421 q=-1.0 Lsize=    3714kB time=00:00:58.59 bitrate= 519.2kbits/s speed=  14x"
        ));
        Assert.assertEquals(LogLevel.INFO, FFmpegResultReader.detectLogLevel(
                "[libx264 @ 0x5640c7caa580] [info] frame I:13    Avg QP:23.45  size: 11116"
        ));
        Assert.assertEquals(LogLevel.INFO, FFmpegResultReader.detectLogLevel(
                "[libx264 @ 0x5640c7caa580] [info]"
        ));

        Assert.assertEquals(LogLevel.VERBOSE, FFmpegResultReader.detectLogLevel(
                "[AVIOContext @ 0x5640c7c802c0] [verbose] Statistics: 6 seeks, 26 writeouts"
        ));

        Assert.assertEquals(LogLevel.DEBUG, FFmpegResultReader.detectLogLevel(
                "[debug] 4268 frames successfully decoded, 0 decoding errors"
        ));

        Assert.assertEquals(LogLevel.DEBUG, FFmpegResultReader.detectLogLevel(
                "[matroska @ 0x5640c7cb2000] [debug] stream 1 end duration = 58239"
        ));

        Assert.assertEquals(LogLevel.ERROR, FFmpegResultReader.detectLogLevel(
                "[error] .artifacts/MPEG-4/videosadfasdf.mp4: No such file or directory"
        ));

        Assert.assertEquals(LogLevel.TRACE, FFmpegResultReader.detectLogLevel(
                "[mov,mp4,m4a,3gp,3g2,mj2 @ 0x56288d084700] [] stream 3, sample 45, dts 5201270"
        ));

        Assert.assertEquals(LogLevel.TRACE, FFmpegResultReader.detectLogLevel(
                "[mov,mp4,m4a,3gp,3g2,mj2 @ 0x56288d084700] [trace] stream 3, sample 45, dts 5201270"
        ));

        for (LogLevel logLevel : LogLevel.values()) {
            Assert.assertEquals(logLevel, FFmpegResultReader.detectLogLevel("[" + logLevel.name().toLowerCase() + "]"));
        }

        for (LogLevel logLevel : LogLevel.values()) {
            Assert.assertNull(FFmpegResultReader.detectLogLevel("[" + logLevel.name().toLowerCase()));
        }

        Assert.assertNull(FFmpegResultReader.detectLogLevel("[mov,mp4,m4a,3gp,3g2,mj2 @ 0x56288d084700]"));
        Assert.assertNull(FFmpegResultReader.detectLogLevel("[mov,mp4,m4a,3gp,3g2,mj2 @ 0x56288d084700] [inf"));
    }

    @Test
    public void parsResult() throws Exception {
        String value = "video:1417kB audio:113kB subtitle:0kB other streams:0kB global headers:0kB muxing overhead: unknown";
        FFmpegResult result = FFmpegResultReader.parseResult(value);

        Assert.assertNotNull(result);
        Assert.assertEquals((Long) 1_417_000L, result.getVideoSize());
        Assert.assertEquals((Long) 113_000L, result.getAudioSize());
        Assert.assertEquals((Long) 0L, result.getSubtitleSize());
        Assert.assertEquals((Long) 0L, result.getOtherStreamsSize());
        Assert.assertEquals((Long) 0L, result.getGlobalHeadersSize());
        Assert.assertNull(result.getMuxingOverheadRatio());
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
    public void parseResultWhichDoesntContainResult() throws Exception {
        String value = "This= 5Random String : doesn't contain progre==55 info";
        FFmpegResult result = FFmpegResultReader.parseResult(value);

        Assert.assertNull(result);
    }
}