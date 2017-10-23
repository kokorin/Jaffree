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
import java.util.List;

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
    public void read() throws Exception {
        try (FileInputStream input = new FileInputStream(VIDEO_NUT.toFile())) {
            NutReader reader = new NutReader(new NutInputStream(input));

            MainHeader mainHeader = reader.getMainHeader();
            Assert.assertNotNull(mainHeader);
            Assert.assertTrue(mainHeader.majorVersion >= 3);

            List<StreamHeader> streamHeaders = reader.getStreamHeaders();
            Assert.assertEquals(StreamHeader.Type.VIDEO, streamHeaders.get(0).streamType);
            Assert.assertEquals(320, streamHeaders.get(0).video.width);
            Assert.assertEquals(240, streamHeaders.get(0).video.height);

            Assert.assertEquals(StreamHeader.Type.AUDIO, streamHeaders.get(1).streamType);
            Assert.assertEquals(2, streamHeaders.get(1).audio.channelCount);
            Assert.assertEquals(44100, streamHeaders.get(1).audio.samplerate.numerator);
            Assert.assertEquals(1, streamHeaders.get(1).audio.samplerate.denominator);
        }
    }
}