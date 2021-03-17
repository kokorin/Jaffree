package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.Artifacts;
import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StackTraceMatcher;
import com.github.kokorin.jaffree.StreamSpecifier;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.FlatFormatParser;
import com.github.kokorin.jaffree.ffprobe.data.FormatParser;
import com.github.kokorin.jaffree.ffprobe.data.JsonFormatParser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@RunWith(Parameterized.class)
public class FFprobeTest {
    private final FormatParser formatParser;

    public static Path BIN;
    public static Path VIDEO_MP4 = Artifacts.getMp4Artifact();
    public static Path VIDEO_WITH_PROGRAMS = Artifacts.getTsArtifactWithPrograms();
    public static Path VIDEO_WITH_CHAPTERS = Artifacts.getMkvArtifactWithChapters();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<? extends Object> data() {
        return Arrays.asList(new FlatFormatParser(), new JsonFormatParser());
    }

    public FFprobeTest(FormatParser formatParser) {
        this.formatParser = formatParser;
    }

    @BeforeClass
    public static void setUp() throws Exception {
        String ffmpegHome = System.getProperty("FFMPEG_BIN");
        if (ffmpegHome == null) {
            ffmpegHome = System.getenv("FFMPEG_BIN");
        }
        Assert.assertNotNull("Nor command line property, neither system variable FFMPEG_BIN is set up", ffmpegHome);
        BIN = Paths.get(ffmpegHome);

        Assert.assertTrue("Sample videos weren't found: " + VIDEO_MP4.toAbsolutePath(), Files.exists(VIDEO_MP4));
        Assert.assertTrue("Sample videos weren't found: " + VIDEO_WITH_PROGRAMS.toAbsolutePath(), Files.exists(VIDEO_WITH_PROGRAMS));
        Assert.assertTrue("Sample videos weren't found: " + VIDEO_WITH_CHAPTERS.toAbsolutePath(), Files.exists(VIDEO_WITH_CHAPTERS));
    }

    //private boolean showData;

    @Test
    public void testShowDataWithShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowData(true)
                .setShowStreams(true)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertFalse(result.getStreams().isEmpty());

        Stream stream = result.getStreams().get(0);
        Assert.assertNotNull(stream.getExtradata());
        Assert.assertEquals(Rational.valueOf(30L), stream.getAvgFrameRate());
    }

    // For this test to pass ffmpeg must be added to Operation System PATH environment variable
    @Test
    public void testEnvPath() throws Exception {
        FFprobeResult result = FFprobe.atPath()
                .setInput(VIDEO_MP4)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
    }

    @Test
    public void testShowDataWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowData(true)
                .setShowPackets(true)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPackets());
        Assert.assertFalse(result.getPackets().isEmpty());
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
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertFalse(result.getStreams().isEmpty());
        Assert.assertNotNull(result.getStreams().get(0).getExtradataHash());
    }

    @Test
    public void testShowDataHashWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowDataHash("MD5")
                .setShowPackets(true)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPackets());
        Assert.assertFalse(result.getPackets().isEmpty());
        for (Packet packet : result.getPackets()) {
            Assert.assertNotNull(packet.getCodecType());
            Assert.assertNotNull(packet.getDataHash());
        }
    }

    //private boolean showFormat;

    @Test
    public void testShowFormat() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowFormat(true)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getFormat());
        Assert.assertNotNull(result.getFormat().getFormatName());
        Assert.assertNotNull(result.getFormat().getFormatLongName());
    }

    //private String showFormatEntry;
    //private String showEntries;

    @Test
    public void testShowEntries() throws Exception {

        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowEntries("packet=pts_time,duration_time,stream_index : stream=index,codec_type")
                .setFormatParser(formatParser)
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
                .setFormatParser(formatParser)
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
    public void testShowLog() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowFrames(true)
                .setShowLog(LogLevel.TRACE)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getFrames());
        int framesWithLogs = 0;
        for (Frame frame : result.getFrames()) {
            if (frame.getLogs() != null && !frame.getLogs().isEmpty()) {
                framesWithLogs++;
            }
        }
        Assert.assertTrue(framesWithLogs > 1000);
    }

    //private boolean showStreams;

    @Test
    public void testShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowStreams(true)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertEquals(2, result.getStreams().size());

        Stream videoStream = result.getStreams().get(0);

        Assert.assertEquals(StreamType.VIDEO, videoStream.getCodecType());
        Assert.assertNotNull(videoStream.getSampleAspectRatio());
        Assert.assertNotNull(videoStream.getDisplayAspectRatio());
        Assert.assertNotNull(videoStream.getStartTime(TimeUnit.NANOSECONDS));
        Assert.assertEquals((Long) 180L, videoStream.getDuration(TimeUnit.SECONDS));
        Assert.assertNotNull(videoStream.getBitRate());
        Assert.assertNotNull(videoStream.getNbFrames());
        Assert.assertNotNull(videoStream.getBitsPerRawSample());
        Assert.assertNotNull(videoStream.getPixFmt());
    }

    @Test
    public void testSelectStreamWithShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowStreams(true)
                .setSelectStreams(StreamType.VIDEO)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertEquals(1, result.getStreams().size());

        Stream stream = result.getStreams().get(0);
        Assert.assertEquals(StreamType.VIDEO, stream.getCodecType());
    }

    @Test
    public void testSelectStreamWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowPackets(true)
                .setSelectStreams(StreamSpecifier.withIndex(1))
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPackets());
        Assert.assertTrue(result.getPackets().size() > 7000);
        Assert.assertNotNull(result.getPackets().get(0).getCodecType());
    }

    //private boolean showPrograms;

    @Test
    public void testShowPrograms() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_WITH_PROGRAMS)
                .setShowPrograms(true)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPrograms());
        Assert.assertEquals(3, result.getPrograms().size());

        Program program1 = result.getPrograms().get(0);
        Assert.assertEquals("first_program", program1.getTag("service_name"));
        Assert.assertEquals((Integer) 1, program1.getProgramId());
        Assert.assertEquals((Integer) 1, program1.getProgramNum());
        Assert.assertEquals((Integer) 2, program1.getNbStreams());
        Assert.assertNotNull(program1.getStreams());
        Assert.assertEquals(2, program1.getStreams().size());

        Program program2 = result.getPrograms().get(1);
        Assert.assertEquals("second program", program2.getTag("service_name"));
        Assert.assertEquals((Integer) 2, program2.getProgramNum());
        Assert.assertEquals((Integer) 2, program2.getNbStreams());
        Assert.assertNotNull(program2.getStreams());
        Assert.assertEquals(2, program2.getStreams().size());

        Program program3 = result.getPrograms().get(2);
        Assert.assertEquals("3rdProgram", program3.getTag("service_name"));
        Assert.assertEquals((Integer) 3, program3.getProgramNum());
        Assert.assertEquals((Integer) 2, program3.getNbStreams());
        Assert.assertNotNull(program3.getStreams());
        Assert.assertEquals(2, program3.getStreams().size());
    }

    //private boolean showChapters;

    @Test
    public void testShowChapters() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_WITH_CHAPTERS)
                .setShowChapters(true)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getChapters());
        Assert.assertEquals(3, result.getChapters().size());

        Chapter chapter1 = result.getChapters().get(0);
        Assert.assertEquals(1, chapter1.getId());
        Assert.assertEquals("FirstChapter", chapter1.getTag("title"));
        Assert.assertEquals(new Rational(1L, 1_000_000_000L), chapter1.getTimeBase());
        Assert.assertEquals((Long) 0L, chapter1.getStart());
        Assert.assertEquals((Double) 0., chapter1.getStartTime(), 0.01);
        Assert.assertEquals((Long)60_000_000_000L, chapter1.getEnd());
        Assert.assertEquals((Double) 60., chapter1.getEndTime(), 0.01);

        Chapter chapter2 = result.getChapters().get(1);
        Assert.assertEquals(2, chapter2.getId());
        Assert.assertEquals("Second Chapter", chapter2.getTag("title"));
        Assert.assertEquals((Long) 60_000_000_000L, chapter2.getStart());
        Assert.assertEquals((Double) 60., chapter2.getStartTime(), 0.01);

        Chapter chapter3 = result.getChapters().get(2);
        Assert.assertEquals(3, chapter3.getId());
        Assert.assertEquals("Final", chapter3.getTag("title"));
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
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        for (Stream stream : result.getStreams()) {
            Assert.assertTrue(stream.getNbFrames() > 0);
            Assert.assertTrue(stream.getNbReadFrames() > 0);
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
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPackets());
        Assert.assertEquals(42, result.getPackets().size());
        for (Packet packet : result.getPackets()) {
            Assert.assertNotNull(packet.getCodecType());
        }
    }

    @Test
    public void testShowSubtitles() {
        Assert.fail("No artifact with subtitles to check!");

        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowStreams(true)
                .setShowData(true)
                .setSelectStreams(StreamType.VIDEO)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getSubtitles());
        Assert.assertFalse(result.getSubtitles().isEmpty());
        for (Subtitle subtitle : result.getSubtitles()) {
            Assert.assertNotNull(subtitle.getStartDisplayTime());
            Assert.assertNotNull(subtitle.getFormat());
        }
    }

    @Test
    public void testSideListAttributes() throws Exception {
        Assert.fail("No artifact with side data to check!");

        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowStreams(true)
                .setShowData(true)
                .setSelectStreams(StreamType.VIDEO)
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());

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
                .setFormatParser(formatParser)
                .execute();
    }


    @Test
    public void testProbeSize() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setProbeSize(10_000_000L)
                .setInput(VIDEO_MP4)
                .setFormatParser(formatParser)
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
                .setFormatParser(formatParser)
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
                .setFormatParser(formatParser)
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
                .setFormatParser(formatParser)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertFalse(result.getStreams().isEmpty());
    }

    @Test
    public void testAdditionalArguments() {
        FFprobeResult result = FFprobe.atPath(BIN)
                // The same as .setShowStreams(true), just for testing
                .addArgument("-show_streams")
                .addArguments("-select_streams", "v")
                .setInput(VIDEO_MP4)
                .setFormatParser(formatParser)
                .execute();


        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertEquals(1, result.getStreams().size());

        Stream stream = result.getStreams().get(0);
        Assert.assertEquals(StreamType.VIDEO, stream.getCodecType());
    }

    @Test
    public void testInputStream() throws Exception {
        FFprobeResult result;

        try (InputStream inputStream = Files.newInputStream(VIDEO_MP4, StandardOpenOption.READ)) {
            result = FFprobe.atPath(BIN)
                    .setShowStreams(true)
                    .setInput(inputStream)
                    .setFormatParser(formatParser)
                    .execute();
        }

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertFalse(result.getStreams().isEmpty());
    }

    @Test
    public void testInputChannel() throws Exception {
        FFprobeResult result;

        try (SeekableByteChannel channel = Files.newByteChannel(VIDEO_MP4, StandardOpenOption.READ)) {
            result = FFprobe.atPath(BIN)
                    .setShowStreams(true)
                    .setInput(channel)
                    .setFormatParser(formatParser)
                    .execute();
        }

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertFalse(result.getStreams().isEmpty());
    }

    @Test
    public void testAsyncExecution() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setInput(VIDEO_MP4)
                .setFormatParser(formatParser)
                .executeAsync()
                .get();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getStreams());
        Assert.assertEquals(2, result.getStreams().size());

        Stream stream = result.getStreams().get(0);
        Assert.assertEquals(StreamType.VIDEO, stream.getCodecType());

        stream = result.getStreams().get(1);
        Assert.assertEquals(StreamType.AUDIO, stream.getCodecType());
    }


    @Test
    public void testAsyncExecutionWithException() throws Exception {
        expectedException.expect(new StackTraceMatcher("No such file or directory"));

        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setInput("non_existent.mp4")
                .setFormatParser(formatParser)
                .executeAsync()
                .get();
    }
}
