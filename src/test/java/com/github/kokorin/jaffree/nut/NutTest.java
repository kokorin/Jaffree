package com.github.kokorin.jaffree.nut;

import com.github.kokorin.jaffree.Artifacts;
import com.github.kokorin.jaffree.Config;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;

public class NutTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NutTest.class);

    @Test
    public void read() throws Exception {
        assertNutStructure(Artifacts.VIDEO_NUT);
    }

    @Test
    public void readAndWrite() throws Exception {
        Path outputPath = Files.createTempFile("output", ".nut");

        try (NutInputStream inputStream = new NutInputStream(new FileInputStream(Artifacts.VIDEO_NUT.toFile()));
             NutOutputStream outputStream = new NutOutputStream(new FileOutputStream(outputPath.toFile()))) {
            NutReader reader = new NutReader(inputStream);
            NutWriter writer = new NutWriter(outputStream, 200);

            MainHeader mainHeader = reader.getMainHeader();
            StreamHeader[] streamHeaders = reader.getStreamHeaders();
            Info[] infos = reader.getInfos();

            writer.setMainHeader(mainHeader.streamCount, mainHeader.maxDistance, mainHeader.timeBases, mainHeader.frameCodes);
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

        // During this test you can see in console some warnings like the following:
        // [null @ 0000000000dca4e0] Application provided invalid, non monotonically increasing dts to muxer in stream 1: 7371776 >= 7371776
        // [null @ 0000000000dca4e0] Application provided invalid, non monotonically increasing dts to muxer in stream 0: 15050833 >= 15048033
        // This is because of EOR frames in NUT (the have the same timestamp as previous frames in the same stream), so it's OK

        FFmpegResult mpeg = FFmpeg.atPath(Config.FFMPEG_BIN)
                .addInput(UrlInput.fromPath(outputPath))
                .addOutput(new NullOutput())
                .execute();
        assertNotNull(mpeg);
        Assert.assertTrue(mpeg.getVideoSize() > 100_000);
        Assert.assertTrue(mpeg.getAudioSize() > 10_000);
    }

    @Test
    public void dumpNutHeaders() throws Exception {
        Path rawNut = Files.createTempFile("raw_video", ".nut");

        FFmpeg.atPath(Config.FFMPEG_BIN)
                .addInput(
                        UrlInput.fromPath(Artifacts.VIDEO_MP4)
                                .setDuration(1000)
                )
                .setOverwriteOutput(true)
                .addOutput(UrlOutput.toPath(rawNut)
                        .setFormat("nut")
                        .addArguments("-vcodec", "rawvideo")
                        .addArguments("-pix_fmt", "bgr24")
                        .addArguments("-acodec", "pcm_s32be")
                )
                .execute();

        try (FileInputStream input = new FileInputStream(rawNut.toFile())) {
            NutReader reader = new NutReader(new NutInputStream(input));
            reader.readFrame();

            assertNotNull(reader.getMainHeader());
            LOGGER.debug("-------");
            LOGGER.debug(reader.getMainHeader().toString());

            assertNotNull(reader.getInfos());
            LOGGER.debug("-------");
            for (Info info : reader.getInfos()) {
                LOGGER.debug(info.toString());
            }

            assertNotNull(reader.getStreamHeaders());
            LOGGER.debug("-------");
            for (StreamHeader streamHeader : reader.getStreamHeaders()) {
                LOGGER.debug(streamHeader.toString());
            }

            LOGGER.debug("-------");
            for (FrameCode fCode : reader.getMainHeader().frameCodes) {
                LOGGER.debug(fCode.toString());
            }
        }
    }

    private static void assertNutStructure(Path nut) throws Exception {
        try (FileInputStream input = new FileInputStream(nut.toFile())) {
            NutReader reader = new NutReader(new NutInputStream(input));

            MainHeader mainHeader = reader.getMainHeader();
            assertNotNull(mainHeader);
            Assert.assertTrue(mainHeader.majorVersion >= 3);

            StreamHeader[] streamHeaders = reader.getStreamHeaders();
            Assert.assertEquals(StreamHeader.Type.VIDEO, streamHeaders[0].streamType);
            Assert.assertEquals(640, streamHeaders[0].video.width);
            Assert.assertEquals(480, streamHeaders[0].video.height);

            Assert.assertEquals(StreamHeader.Type.AUDIO, streamHeaders[1].streamType);
            Assert.assertEquals(1, streamHeaders[1].audio.channelCount);
            Assert.assertEquals(44100, streamHeaders[1].audio.sampleRate.getNumerator());
            Assert.assertEquals(1, streamHeaders[1].audio.sampleRate.getDenominator());

            NutFrame frame = reader.readFrame();
            assertNotNull(frame);

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