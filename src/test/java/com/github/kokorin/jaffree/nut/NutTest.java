package com.github.kokorin.jaffree.nut;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NutTest {

    public static Path BIN;
    public static Path SAMPLES = Paths.get("target/samples");
    public static Path VIDEO_MP4 = SAMPLES.resolve("MPEG-4/video.mp4");
    public static Path VIDEO_NUT = SAMPLES.resolve("video.nut");

    @BeforeClass
    public static void setUp() throws Exception {
        String ffmpegHome = System.getProperty("FFMPEG_BIN");
        if (ffmpegHome == null) {
            ffmpegHome = System.getenv("FFMPEG_BIN");
        }
        Assert.assertNotNull("Nor command line property, neither system variable FFMPEG_BIN is set up", ffmpegHome);
        BIN = Paths.get(ffmpegHome);

        Assert.assertTrue("Sample videos weren't found: " + SAMPLES.toAbsolutePath(), Files.exists(SAMPLES));

        if (!Files.exists(VIDEO_NUT)) {
            FFmpeg.atPath(BIN)
                    .addInput(UrlInput.fromPath(VIDEO_MP4))
                    .addOutput(UrlOutput.toPath(VIDEO_NUT).copyAllCodecs())
                    .execute();
        }

        Assert.assertTrue("NUT file hasn't been found: " + VIDEO_NUT.toAbsolutePath(), Files.exists(VIDEO_NUT));
    }

    @Test
    public void read() throws Exception {
        assertNutStructure(VIDEO_NUT);
    }

    @Test
    public void readWrite() throws Exception {
        Path outputPath = Files.createTempFile("output", ".nut");

        try (FileInputStream input = new FileInputStream(VIDEO_NUT.toFile());
             NutWriter writer = new NutWriter(new NutOutputStream(new FileOutputStream(outputPath.toFile())))) {
            NutReader reader = new NutReader(new NutInputStream(input));

            MainHeader mainHeader = reader.getMainHeader();
            StreamHeader[] streamHeaders = reader.getStreamHeaders();
            Info[] infos = reader.getInfos();

            writer.setMainHeader(mainHeader);
            writer.setStreamHeaders(streamHeaders);
            writer.setInfos(infos);

            NutFrame frame;
            while ((frame = reader.readFrame()) != null) {
                writer.writeFrame(frame);
            }
        }

        Assert.assertTrue(Files.exists(outputPath));
        Assert.assertTrue(Files.size(outputPath) > 1000);

        assertNutStructure(outputPath);

        FFprobeResult probe = FFprobe.atPath(BIN)
                .setInputPath(outputPath)
                .setShowError(true)
                .setCountFrames(true)
                .setShowLog(LogLevel.DEBUG)
                .execute();

        Assert.assertNotNull(probe);
        Assert.assertNull(probe.getError());

        // During this test you can see in console some warnings like the following:
        // [null @ 0000000000dca4e0] Application provided invalid, non monotonically increasing dts to muxer in stream 1: 7371776 >= 7371776
        // [null @ 0000000000dca4e0] Application provided invalid, non monotonically increasing dts to muxer in stream 0: 15050833 >= 15048033
        // This is because of EOR frames in NUT (the have the same timestamp as previous frames in the same stream), so it's OK

        FFmpegResult mpeg = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(outputPath))
                .addOutput(new NullOutput())
                .execute();
        Assert.assertNotNull(mpeg);
        Assert.assertTrue(mpeg.getVideoSize() > 100_000);
        Assert.assertTrue(mpeg.getAudioSize() > 10_000);
    }

    private static void assertNutStructure(Path nut) throws Exception {
        try (FileInputStream input = new FileInputStream(nut.toFile())) {
            NutReader reader = new NutReader(new NutInputStream(input));

            MainHeader mainHeader = reader.getMainHeader();
            Assert.assertNotNull(mainHeader);
            Assert.assertTrue(mainHeader.majorVersion >= 3);

            StreamHeader[] streamHeaders = reader.getStreamHeaders();
            Assert.assertEquals(StreamHeader.Type.VIDEO, streamHeaders[0].streamType);
            Assert.assertEquals(320, streamHeaders[0].video.width);
            Assert.assertEquals(240, streamHeaders[0].video.height);

            Assert.assertEquals(StreamHeader.Type.AUDIO, streamHeaders[1].streamType);
            Assert.assertEquals(2, streamHeaders[1].audio.channelCount);
            Assert.assertEquals(44100, streamHeaders[1].audio.samplerate.numerator);
            Assert.assertEquals(1, streamHeaders[1].audio.samplerate.denominator);

            NutFrame frame = reader.readFrame();
            Assert.assertNotNull(frame);

            long videoFrameCount = 0;
            long audioFrameCount = 0;
            do {
                if (frame.streamId == 0) {
                    videoFrameCount++;
                } else if (frame.streamId == 1) {
                    audioFrameCount++;
                } else {
                    Assert.fail("Unexpected streamId: " + frame.streamId);
                }
                frame = reader.readFrame();
            } while (frame != null);

            Assert.assertTrue(videoFrameCount > 300);
            Assert.assertTrue(audioFrameCount > 300);
        }

    }
}