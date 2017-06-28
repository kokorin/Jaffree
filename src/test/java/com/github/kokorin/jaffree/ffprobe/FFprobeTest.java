package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.StreamSpecifier;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FFprobeTest {
    public static Path BIN;
    public static Path SAMPLES = Paths.get("target/samples");
    public static Path VIDEO_MP4 = SAMPLES.resolve("MPEG-4/video.mp4");
    public static Path ERROR_MP4 = SAMPLES.resolve("non_existent.mp4");
    public static Path TRANSPORT_VOB = SAMPLES.resolve("MPEG-VOB/transport-stream/capture.neimeng");

    @BeforeClass
    public static void setUp() throws Exception {
        String ffmpegHome = System.getProperty("FFMPEG_BIN");
        if (ffmpegHome == null) {
            ffmpegHome = System.getenv("FFMPEG_BIN");
        }
        Assert.assertNotNull("Nor command line property, neither system variable FFMPEG_BIN is set up", ffmpegHome);
        BIN = Paths.get(ffmpegHome);

        Assert.assertTrue("Sample videos weren't found: " + SAMPLES.toAbsolutePath(), Files.exists(SAMPLES));
    }

    //private boolean showData;

    @Test
    public void testShowDataWithShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowData(true)
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams().getStream().get(0).getExtradata());
    }

    @Test
    public void testShowDataWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowData(true)
                .setShowPackets(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPackets().getPacket().get(0).getData());
    }


    //private String showDataHash;

    @Test
    public void testShowDataHashWithShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowDataHash("MD5")
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams().getStream().get(0).getExtradataHash());
    }

    @Test
    public void testShowDataHashWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowDataHash("MD5")
                .setShowPackets(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPackets().getPacket().get(0).getDataHash());
    }

    //private boolean showError;

    @Test
    public void testShowError() throws Exception {

        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(ERROR_MP4)
                .setShowError(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getError());
    }


    //private boolean showFormat;

    @Test
    public void testShowFormat() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowFormat(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getFormat());
    }

    //private String showFormatEntry;
    //private String showEntries;

    @Test
    public void testShowEntries() throws Exception {

        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowEntries("packet=pts_time,duration_time,stream_index : stream=index,codec_type")
                .execute();

        Assert.assertNotNull(result);

        Assert.assertNotNull(result.getPackets());
        Assert.assertTrue(result.getPackets().getPacket().size() > 0);
        Assert.assertNotNull(result.getPackets().getPacket().get(0).getPtsTime());
        Assert.assertNotNull(result.getPackets().getPacket().get(0).getDurationTime());
        Assert.assertNotNull(result.getPackets().getPacket().get(0).getStreamIndex());

        Assert.assertNotNull(result.getStreams());
        Assert.assertTrue(result.getStreams().getStream().size() > 0);
        Assert.assertNotNull(result.getStreams().getStream().get(0).getIndex());
        Assert.assertNotNull(result.getStreams().getStream().get(0).getCodecType());

    }

    //private boolean showFrames;

    @Test
    public void testShowFrames() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowFrames(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getFrames());
        Assert.assertTrue(result.getFrames().getFrameOrSubtitle().size() > 0);
    }

    //private LogLevel showLog;

    @Test
    @Ignore("For some reason ffmpeg on ubuntu doesn't recognize -show_log option")
    public void testShowLog() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowFrames(true)
                .setShowLog(LogLevel.TRACE)
                .execute();

        Assert.assertNotNull(result);
        for (Object frameOrSubtitle : result.getFrames().getFrameOrSubtitle()) {
            if (frameOrSubtitle instanceof Frame) {
                Frame frame = (Frame) frameOrSubtitle;
                Assert.assertNotNull(frame.getLogs());
            } else {
                Assert.assertEquals(Subtitle.class, frameOrSubtitle.getClass());
            }
        }

    }

    //private boolean showStreams;

    @Test
    public void testShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.getStreams().getStream().size());
    }

    @Test
    public void testSelectStreamWithShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowStreams(true)
                .setSelectStreams(new StreamSpecifier("v"))
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getStreams().getStream().size());

        Stream stream = result.getStreams().getStream().get(0);
        Assert.assertEquals("video", stream.getCodecType());
    }

    @Test
    public void testSelectStreamWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowPackets(true)
                .setSelectStreams(StreamSpecifier.withIndex(5))
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getPackets().getPacket().size());
    }

    //private boolean showPrograms;

    @Test
    public void testShowPrograms() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(TRANSPORT_VOB)
                .setShowPrograms(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.getPrograms().getProgram().isEmpty());
    }

    //private boolean showChapters;

    @Test
    public void testShowChapters() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowChapters(true)
                .execute();

        Assert.assertNotNull(result);
        //TODO Find media file with chapters
        Assert.assertNotNull(result.getChapters());
    }

    //private boolean countFrames;
    //private boolean countPackets;

    @Test
    public void testCountFramesAndPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowStreams(true)
                .setCountFrames(true)
                .setCountPackets(true)
                .execute();

        Assert.assertNotNull(result);
        for (Stream stream : result.getStreams().getStream()) {
            Assert.assertTrue(stream.getNbFrames() > 0);
            Assert.assertTrue(stream.getNbReadPackets() > 0);
        }
    }

    //private String readIntervals;

    @Test
    public void testReadIntervals() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowPackets(true)
                .setReadIntervals("30%+#42")
                .execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.getPackets().getPacket().size() == 42);
    }

    //private boolean showProgramVersion;
    //private boolean showLibraryVersions;
    //private boolean showVersions;

    @Test
    public void testShowVersions() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowProgramVersion(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.getProgramVersion().getVersion().isEmpty());

        result = FFprobe.atPath(BIN)
                .setShowLibraryVersions(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.getLibraryVersions().getLibraryVersion().isEmpty());

        result = FFprobe.atPath(BIN)
                .setShowVersions(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.getProgramVersion().getVersion().isEmpty());
        Assert.assertFalse(result.getLibraryVersions().getLibraryVersion().isEmpty());
    }

    //private boolean showPixelFormats;
    @Test
    public void testShowPixelFormats() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowPixelFormats(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.getPixelFormats().getPixelFormat().isEmpty());
    }
}
