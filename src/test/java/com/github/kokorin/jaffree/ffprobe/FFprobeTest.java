package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.*;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class FFprobeTest {
    public static Path BIN;
    public static Path VIDEO_MP4 = Artifacts.getFFmpegSample("MPEG-4/video.mp4");
    public static Path TRANSPORT_VOB = Artifacts.getFFmpegSample("MPEG-VOB/transport-stream/capture.neimeng");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUp() throws Exception {
        String ffmpegHome = System.getProperty("FFMPEG_BIN");
        if (ffmpegHome == null) {
            ffmpegHome = System.getenv("FFMPEG_BIN");
        }
        Assert.assertNotNull("Nor command line property, neither system variable FFMPEG_BIN is set up", ffmpegHome);
        BIN = Paths.get(ffmpegHome);

        Assert.assertTrue("Sample videos weren't found: " + VIDEO_MP4.toAbsolutePath(), Files.exists(VIDEO_MP4));
        Assert.assertTrue("Sample videos weren't found: " + TRANSPORT_VOB.toAbsolutePath(), Files.exists(TRANSPORT_VOB));
    }

    //private boolean showData;

    @Test
    public void testShowDataWithShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowData(true)
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);

        Stream stream = result.getStreams().get(0);
        Assert.assertNotNull(stream.getExtradata());
        Assert.assertEquals(Rational.valueOf("30000/1001"), stream.getAvgFrameRate());
    }

    // For this test to pass ffmpeg must be added to Operation System PATH environment variable
    @Test
    public void testEnvPath() throws Exception {
        FFprobeResult result = FFprobe.atPath()
                .setInput(VIDEO_MP4)
                .execute();

        Assert.assertNotNull(result);
    }

    @Test
    public void testShowDataWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowData(true)
                .setShowPackets(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPackets().get(0).getData());
        for (Packet packet : result.getPackets()) {
            Assert.assertNotNull(packet.getCodecType());
        }
    }


    //private String showDataHash;

    @Test
    public void testShowDataHashWithShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowDataHash("MD5")
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams().get(0).getExtradataHash());
    }

    @Test
    public void testShowDataHashWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowDataHash("MD5")
                .setShowPackets(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPackets().get(0).getDataHash());
        for (Packet packet : result.getPackets()) {
            Assert.assertNotNull(packet.getCodecType());
        }
    }

    //private boolean showFormat;

    @Test
    public void testShowFormat() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
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
                .setInput(VIDEO_MP4)
                .setShowEntries("packet=pts_time,duration_time,stream_index : stream=index,codec_type")
                .execute();

        Assert.assertNotNull(result);

        Assert.assertNotNull(result.getPackets());
        Assert.assertTrue(result.getPackets().size() > 0);
        Assert.assertNotNull(result.getPackets().get(0).getPtsTime());
        Assert.assertNotNull(result.getPackets().get(0).getDurationTime());
        Assert.assertNotNull(result.getPackets().get(0).getStreamIndex());

        Assert.assertNotNull(result.getStreams());
        Assert.assertTrue(result.getStreams().size() > 0);
        Assert.assertNotNull(result.getStreams().get(0).getIndex());
        Assert.assertNotNull(result.getStreams().get(0).getCodecType());

    }

    //private boolean showFrames;

    @Test
    public void testShowFrames() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowFrames(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getFrames());
        Assert.assertTrue(result.getFrames().size() > 0);

        for (Object frameOrSubtitle : result.getFrames()) {
            if (!(frameOrSubtitle instanceof Frame)) {
                continue;
            }

            Frame frame = (Frame) frameOrSubtitle;
            Assert.assertNotNull(frame.getMediaType());

            if (frame.getMediaType() == StreamType.VIDEO) {
                Assert.assertNotNull(frame.getSampleAspectRatio());
            }
        }
    }

    //private LogLevel showLog;

    @Test
    @Ignore("For some reason ffmpeg on ubuntu doesn't recognize -show_log option")
    public void testShowLog() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowFrames(true)
                .setShowLog(LogLevel.TRACE)
                .execute();

        Assert.assertNotNull(result);
        for (Object frameOrSubtitle : result.getFrames()) {
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
                .setInput(VIDEO_MP4)
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.getStreams().size());
        for (Stream stream : result.getStreams()) {
            if (stream.getCodecType() != StreamType.VIDEO) {
                continue;
            }

            Assert.assertNotNull(stream.getSampleAspectRatio());
            Assert.assertNotNull(stream.getDisplayAspectRatio());
            Assert.assertNotNull(stream.getStartTime(TimeUnit.NANOSECONDS));
            Assert.assertEquals(Long.valueOf(167L), stream.getDuration(TimeUnit.SECONDS));
        }
    }

    @Test
    public void testSelectStreamWithShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowStreams(true)
                .setSelectStreams(StreamType.VIDEO)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getStreams().size());

        Stream stream = result.getStreams().get(0);
        Assert.assertEquals(StreamType.VIDEO, stream.getCodecType());
    }

    @Test
    public void testSelectStreamWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowPackets(true)
                .setSelectStreams(StreamSpecifier.withIndex(5))
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getPackets().size());
        Assert.assertNotNull(result.getPackets().get(0).getCodecType());
    }

    //private boolean showPrograms;

    @Test
    public void testShowPrograms() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(TRANSPORT_VOB)
                .setShowPrograms(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.getPrograms().isEmpty());
    }

    //private boolean showChapters;

    @Test
    public void testShowChapters() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
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
                .setInput(VIDEO_MP4)
                .setShowStreams(true)
                .setCountFrames(true)
                .setCountPackets(true)
                .execute();

        Assert.assertNotNull(result);
        for (Stream stream : result.getStreams()) {
            Assert.assertTrue(stream.getNbFrames() > 0);
            Assert.assertTrue(stream.getNbReadPackets() > 0);
        }
    }

    //private String readIntervals;

    @Test
    public void testReadIntervals() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowPackets(true)
                .setReadIntervals("30%+#42")
                .execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.getPackets().size() == 42);
        for (Packet packet : result.getPackets()) {
            Assert.assertNotNull(packet.getCodecType());
        }
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
        Assert.assertFalse(result.getLibraryVersions().isEmpty());

        result = FFprobe.atPath(BIN)
                .setShowVersions(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.getProgramVersion().getVersion().isEmpty());
        Assert.assertFalse(result.getLibraryVersions().isEmpty());
    }

    //private boolean showPixelFormats;
    @Test
    public void testShowPixelFormats() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowPixelFormats(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.getPixelFormats().isEmpty());

        boolean hasComponents = false;
        for (PixelFormat format : result.getPixelFormats()) {
            if (format.getComponents().isEmpty()) {
                hasComponents = true;
            }
        }

        Assert.assertTrue(hasComponents);
    }

    @Test
    public void testSideListAttributes() throws Exception {
        Path video = Paths.get("VID_20180811_180157.mp4");
        // Test uses local file
        if (!Files.exists(video)) {
            return;
        }

        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(video)
                .setShowStreams(true)
                .setShowData(true)
                .setSelectStreams(StreamType.VIDEO)
                .execute();

        Assert.assertNotNull(result);

        Stream stream = result.getStreams().get(0);
        Assert.assertNotNull(stream);

        PacketSideData sideData = stream.getSideDataList().get(0);
        Assert.assertNotNull(sideData);
        Assert.assertNotNull(sideData.getDisplayMatrix());
        Assert.assertNotEquals("New lines must be kept by parser", -1, sideData.getDisplayMatrix().indexOf('\n'));
        Assert.assertNotNull(sideData.getRotation());
    }

    @Test
    public void testExceptionIsThrownIfFfprobeExitsWithError() {
        expectedException.expect(new StackTraceMatcher("No such file or directory"));

        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(Paths.get("nonexistent.mp4"))
                .execute();
    }


    @Test
    public void testProbeSize() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setProbeSize(10_000_000L)
                .setInput(VIDEO_MP4)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertFalse(result.getStreams().isEmpty());
    }

    @Test
    public void testAnalyzeDuration() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setAnalyzeDuration(10_000_000L)
                .setInput(VIDEO_MP4)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertFalse(result.getStreams().isEmpty());
    }

    @Test
    public void testAnalyzeDuration2() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setAnalyzeDuration(10, TimeUnit.SECONDS)
                .setInput(VIDEO_MP4)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertFalse(result.getStreams().isEmpty());
    }

    @Test
    public void testFpsProbeSize() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setFpsProbeSize(100L)
                .setInput(VIDEO_MP4)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertFalse(result.getStreams().isEmpty());
    }
}
