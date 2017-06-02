package com.github.kokorin.jaffree;

import com.github.kokorin.jaffree.cli.StreamSpecifier;
import com.github.kokorin.jaffree.ffprobe.xml.FFprobeType;
import com.github.kokorin.jaffree.ffprobe.xml.StreamType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FFprobeTest {
    public static Path BIN;
    public static Path SAMPLES = Paths.get("target/samples");
    public static Path VIDEO_MP4 = SAMPLES.resolve("MPEG-4/video.mp4");
    public static Path ERROR_MP4 = SAMPLES.resolve("non_existent.mp4");

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
        FFprobeType result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowData(true)
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams().getStream().get(0).getExtradata());
    }

    @Test
    public void testShowDataWithShowPackets() throws Exception {
        FFprobeType result = FFprobe.atPath(BIN)
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
        FFprobeType result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowDataHash("MD5")
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams().getStream().get(0).getExtradataHash());
    }

    @Test
    public void testShowDataHashWithShowPackets() throws Exception {
        FFprobeType result = FFprobe.atPath(BIN)
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

        FFprobeType result = FFprobe.atPath(BIN)
                .setInputPath(ERROR_MP4)
                .setShowError(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getError());
    }


    //private boolean showFormat;

    @Test
    public void testShowFormat() throws Exception {
        FFprobeType result = FFprobe.atPath(BIN)
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

        FFprobeType result = FFprobe.atPath(BIN)
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
        FFprobeType result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowFrames(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getFrames());
        Assert.assertTrue(result.getFrames().getFrameOrSubtitle().size() > 0);
    }


    //private LogLevel showLog;
    //private boolean showStreams;
    //private boolean showPrograms;
    //private boolean showChapters;
    //private boolean countFrames;
    //private boolean countPackets;
    ////TODO extract type
    //private String readIntervals;
    //
    //private boolean showPrivateData;
    //private boolean showProgramVersion;
    //private boolean showLibraryVersions;
    //private boolean showVersions;
    //private boolean showPixelFormats;


    @Test
    public void testShowStreams() throws Exception {
        FFprobeType result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.getStreams().getStream().size());
    }

    @Test
    public void testSelectStreamWithShowStreams() throws Exception {
        FFprobeType result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowStreams(true)
                .setSelectStreams(new StreamSpecifier("v"))
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getStreams().getStream().size());

        StreamType stream = result.getStreams().getStream().get(0);
        Assert.assertEquals("video", stream.getCodecType());
    }

    @Test
    public void testSelectStreamWithShowPackets() throws Exception {
        FFprobeType result = FFprobe.atPath(BIN)
                .setInputPath(VIDEO_MP4)
                .setShowPackets(true)
                .setSelectStreams(new StreamSpecifier(5))
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getPackets().getPacket().size());
    }



}
