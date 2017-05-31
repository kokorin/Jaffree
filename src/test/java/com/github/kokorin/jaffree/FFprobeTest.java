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
    public static Path bin;
    public static Path samples = Paths.get("target/samples");
    public static Path videoMP4 = samples.resolve("MPEG-4/video.mp4");

    @BeforeClass
    public static void setUp() throws Exception {
        String ffmpegHome = System.getProperty("FFMPEG_BIN");
        if (ffmpegHome == null) {
            ffmpegHome = System.getenv("FFMPEG_BIN");
        }
        Assert.assertNotNull("Nor command line property, neither system variable FFMPEG_BIN is set up", ffmpegHome);
        bin = Paths.get(ffmpegHome);

        Assert.assertTrue("Sample videos weren't found: " + samples.toAbsolutePath(), Files.exists(samples));
    }

    //private boolean showData;

    @Test
    public void testShowDataWithShowStreams() throws Exception {
        FFprobeType result = FFprobe.atPath(bin)
                .setInputPath(videoMP4)
                .setShowData(true)
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams().getStream().get(0).getExtradata());
    }

    @Test
    public void testShowDataWithShowPackets() throws Exception {
        FFprobeType result = FFprobe.atPath(bin)
                .setInputPath(videoMP4)
                .setShowData(true)
                .setShowPackets(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPackets().getPacket().get(0).getData());
    }


    //private String showDataHash;

    @Test
    public void testShowDataHashWithShowStreams() throws Exception {
        FFprobeType result = FFprobe.atPath(bin)
                .setInputPath(videoMP4)
                .setShowDataHash("MD5")
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams().getStream().get(0).getExtradataHash());
    }

    @Test
    public void testShowDataHashWithShowPackets() throws Exception {
        FFprobeType result = FFprobe.atPath(bin)
                .setInputPath(videoMP4)
                .setShowDataHash("MD5")
                .setShowPackets(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPackets().getPacket().get(0).getDataHash());
    }
    //private boolean showError;
    //private boolean showFormat;
    //private String showFormatEntry;
    ////TODO extract type
    //private String showEntries;
    //private boolean showPackets;
    //private boolean showFrames;
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
        FFprobeType result = FFprobe.atPath(bin)
                .setInputPath(videoMP4)
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.getStreams().getStream().size());
    }

    @Test
    public void testSelectStreamWithShowStreams() throws Exception {
        FFprobeType result = FFprobe.atPath(bin)
                .setInputPath(videoMP4)
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
        FFprobeType result = FFprobe.atPath(bin)
                .setInputPath(videoMP4)
                .setShowPackets(true)
                .setSelectStreams(new StreamSpecifier(5))
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getPackets().getPacket().size());
    }



}
