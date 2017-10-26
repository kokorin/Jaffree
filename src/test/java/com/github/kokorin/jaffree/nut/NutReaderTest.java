package com.github.kokorin.jaffree.nut;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NutReaderTest {

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
    public void checkNextByte() throws Exception {
        try (FileInputStream input = new FileInputStream(VIDEO_NUT.toFile())) {
            NutInputStream nutInputStream = new NutInputStream(input);
            byte b = nutInputStream.checkNextByte();

            for (int i = 0; i < 1000; i++) {
                Assert.assertEquals("checkNextByte must not increase read position", b ,nutInputStream.checkNextByte());
            }
        }
    }

    @Test
    public void read() throws Exception {
        try (FileInputStream input = new FileInputStream(VIDEO_NUT.toFile())) {
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

            Frame frame = reader.readFrame();
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