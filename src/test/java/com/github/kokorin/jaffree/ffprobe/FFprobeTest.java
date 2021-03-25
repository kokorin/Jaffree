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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class FFprobeTest {
    private final FormatParser formatParser;

    public static Path BIN;
    public static Path VIDEO_MP4 = Artifacts.getMp4Artifact();
    public static Path VIDEO_WITH_PROGRAMS = Artifacts.getTsArtifactWithPrograms();
    public static Path VIDEO_WITH_CHAPTERS = Artifacts.getMkvArtifactWithChapters();
    public static Path VIDEO_WITH_SUBTITLES = Artifacts.getMkvArtifactWithSubtitles();
    public static Path AUDIO_OPUS = Artifacts.getOpusArtifact();
    public static Path VIDEO_MJPEG = Artifacts.getMjpegArtifact();

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
        assertNotNull("Nor command line property, neither system variable FFMPEG_BIN is set up", ffmpegHome);
        BIN = Paths.get(ffmpegHome);

        assertTrue("Sample videos weren't found: " + VIDEO_MP4.toAbsolutePath(), Files.exists(VIDEO_MP4));
        assertTrue("Sample videos weren't found: " + VIDEO_WITH_PROGRAMS.toAbsolutePath(), Files.exists(VIDEO_WITH_PROGRAMS));
        assertTrue("Sample videos weren't found: " + VIDEO_WITH_CHAPTERS.toAbsolutePath(), Files.exists(VIDEO_WITH_CHAPTERS));
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

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertFalse(result.getStreams().isEmpty());

        Stream stream = result.getStreams().get(0);
        assertNotNull(stream.getExtradata());
        assertEquals(Rational.valueOf(30L), stream.getAvgFrameRate());
    }

    // For this test to pass ffmpeg must be added to Operation System PATH environment variable
    @Test
    public void testEnvPath() throws Exception {
        FFprobeResult result = FFprobe.atPath()
                .setInput(VIDEO_MP4)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
    }

    @Test
    public void testShowDataWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowData(true)
                .setShowPackets(true)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getPackets());
        assertFalse(result.getPackets().isEmpty());
        assertNotNull(result.getPackets().get(0).getData());
        for (Packet packet : result.getPackets()) {
            assertNotNull(packet.getCodecType());
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

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertFalse(result.getStreams().isEmpty());
        assertNotNull(result.getStreams().get(0).getExtradataHash());
    }

    @Test
    public void testShowDataHashWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowDataHash("MD5")
                .setShowPackets(true)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getPackets());
        assertFalse(result.getPackets().isEmpty());
        for (Packet packet : result.getPackets()) {
            assertNotNull(packet.getCodecType());
            assertNotNull(packet.getDataHash());
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

        assertNotNull(result);
        Format format = result.getFormat();

        assertNotNull(format);
        assertNotNull(format.getFilename());
        assertNotNull(format.getNbStreams());
        assertNotNull(format.getNbPrograms());
        assertNotNull(format.getFormatName());
        assertNotNull(format.getFormatLongName());
        assertNotNull(format.getStartTime());
        assertNotNull(format.getDuration());
        assertNotNull(format.getSize());
        assertNotNull(format.getBitRate());
        assertNotNull(format.getProbeScore());
        assertEquals("isom", format.getTag("major_brand"));
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

        assertNotNull(result);

        assertNotNull(result.getPackets());
        assertTrue(result.getPackets().size() > 0);
        assertNotNull(result.getPackets().get(0).getPtsTime());
        assertNotNull(result.getPackets().get(0).getDurationTime());
        assertNotNull(result.getPackets().get(0).getStreamIndex());

        assertNotNull(result.getStreams());
        assertTrue(result.getStreams().size() > 0);
        assertNotNull(result.getStreams().get(0).getIndex());
        assertNotNull(result.getStreams().get(0).getCodecType());

    }

    //private boolean showFrames;

    @Test
    public void testShowFrames() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_WITH_SUBTITLES)
                .setShowFrames(true)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getFrames());
        assertFalse(result.getFrames().isEmpty());

        Set<StreamType> streamTypes = EnumSet.noneOf(StreamType.class);

        for (FrameSubtitle frameSubtitle : result.getFrames()) {

            if (frameSubtitle instanceof Subtitle) {
                Subtitle subtitle = (Subtitle) frameSubtitle;
                streamTypes.add(subtitle.getMediaType());
                assertNotNull(subtitle.getPts());
                assertNotNull(subtitle.getPtsTime());
                assertNotNull(subtitle.getFormat());
                assertNotNull(subtitle.getStartDisplayTime());
                assertNotNull(subtitle.getEndDisplayTime());
                assertNotNull(subtitle.getNumRects());
                continue;
            }

            assertTrue(frameSubtitle instanceof Frame);
            Frame frame = (Frame) frameSubtitle;
            streamTypes.add(frame.getMediaType());
            if (frame.getMediaType() == StreamType.VIDEO) {
                assertNotNull(frame.getWidth());
                assertNotNull(frame.getHeight());
                assertNotNull(frame.getSampleAspectRatio());
                assertNotNull(frame.getPixFmt());
            }
            if (frame.getMediaType() == StreamType.AUDIO) {
                assertNotNull(frame.getChannels());
                assertNotNull(frame.getChannelLayout());
                assertNotNull(frame.getNbSamples());
            }
        }

        assertThat(streamTypes, hasItems(StreamType.VIDEO, StreamType.AUDIO, StreamType.SUBTITLE));
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

        assertNotNull(result);
        assertNotNull(result.getFrames());
        assertFalse(result.getFrames().isEmpty());

        int framesWithLogs = 0;
        for (FrameSubtitle frameSubtitle : result.getFrames()) {
            Assert.assertTrue(frameSubtitle instanceof Frame);
            Frame frame = (Frame) frameSubtitle;

            if (frame.getLogs() != null && !frame.getLogs().isEmpty()) {
                framesWithLogs++;

                for (Log log : frame.getLogs()) {
                    assertNotNull(log.getLevel());
                    assertNotNull(log.getCategory());
                    assertNotNull(log.getContext());
                    assertNotNull(log.getMessage());
                }
            }
        }
        assertTrue(framesWithLogs > 1000);
    }

    //private boolean showStreams;

    @Test
    public void testShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowStreams(true)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertEquals(2, result.getStreams().size());

        Stream videoStream = result.getStreams().get(0);

        assertEquals(StreamType.VIDEO, videoStream.getCodecType());
        assertEquals("h264", videoStream.getCodecName());
        assertNotNull(videoStream.getIndex());
        assertEquals((Integer) 640, videoStream.getWidth());
        assertEquals((Integer) 480, videoStream.getHeight());
        assertNotNull(videoStream.getSampleAspectRatio());
        assertNotNull(videoStream.getDisplayAspectRatio());
        assertNotNull(videoStream.getStartPts());
        assertNotNull(videoStream.getTimeBase());
        assertNotNull(videoStream.getStartTime(TimeUnit.NANOSECONDS));
        assertEquals((Long) 180L, videoStream.getDuration(TimeUnit.SECONDS));
        assertEquals((Float) 180.f, videoStream.getDuration(), 0.01f);
        assertNotNull(videoStream.getBitRate());
        assertNotNull(videoStream.getNbFrames());
        assertNotNull(videoStream.getBitsPerRawSample());
        assertNotNull(videoStream.getPixFmt());
        assertNotNull(videoStream.getRFrameRate());
        assertNotNull(videoStream.getAvgFrameRate());
        assertNotNull(videoStream.getDisposition());
        assertEquals("VideoHandler", videoStream.getTag("handler_name"));

        Stream audioStream = result.getStreams().get(1);

        assertEquals(StreamType.AUDIO, audioStream.getCodecType());
        assertEquals((Integer) 1, audioStream.getIndex());
        assertEquals("aac", audioStream.getCodecName());
        assertNotNull(audioStream.getChannels());
        assertNotNull(audioStream.getChannelLayout());
        assertNotNull(audioStream.getSampleRate());
        assertNotNull(audioStream.getSampleFmt());

        StreamDisposition disposition = audioStream.getDisposition();
        assertNotNull(disposition);
        assertEquals((Integer) 1, disposition.getDefault());
        assertEquals((Integer) 0, disposition.getDub());
        assertEquals((Integer) 0, disposition.getOriginal());
        assertEquals((Integer) 0, disposition.getComment());
        assertEquals((Integer) 0, disposition.getLyrics());
        assertEquals((Integer) 0, disposition.getKaraoke());
        assertEquals((Integer) 0, disposition.getForced());
        assertEquals((Integer) 0, disposition.getHearingImpaired());
        assertEquals((Integer) 0, disposition.getVisualImpaired());
        assertEquals((Integer) 0, disposition.getCleanEffects());
        assertEquals((Integer) 0, disposition.getAttachedPic());
        assertEquals((Integer) 0, disposition.getTimedThumbnails());
    }

    @Test
    public void testSelectStreamWithShowStreams() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowStreams(true)
                .setSelectStreams(StreamType.VIDEO)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertEquals(1, result.getStreams().size());

        Stream stream = result.getStreams().get(0);
        assertEquals(StreamType.VIDEO, stream.getCodecType());
    }

    @Test
    public void testSelectStreamWithShowPackets() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MP4)
                .setShowPackets(true)
                .setSelectStreams(StreamSpecifier.withIndex(1))
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getPackets());
        assertTrue(result.getPackets().size() > 7000);
        assertNotNull(result.getPackets().get(0).getCodecType());
    }

    //private boolean showPrograms;

    @Test
    public void testShowPrograms() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_WITH_PROGRAMS)
                .setShowPrograms(true)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getPrograms());
        assertEquals(3, result.getPrograms().size());

        Program program1 = result.getPrograms().get(0);
        assertEquals("first_program", program1.getTag("service_name"));
        assertEquals((Integer) 1, program1.getProgramId());
        assertEquals((Integer) 1, program1.getProgramNum());
        assertEquals((Integer) 2, program1.getNbStreams());
        assertNotNull(program1.getPmtPid());
        assertNotNull(program1.getPcrPid());
        assertNotNull(program1.getStartPts());
        assertNotNull(program1.getStartTime());
        assertNotNull(program1.getEndPts());
        assertNotNull(program1.getEndTime());
        assertNotNull(program1.getStreams());
        assertEquals(2, program1.getStreams().size());

        Program program2 = result.getPrograms().get(1);
        assertEquals("second program", program2.getTag("service_name"));
        assertEquals((Integer) 2, program2.getProgramNum());
        assertEquals((Integer) 2, program2.getNbStreams());
        assertNotNull(program2.getStreams());
        assertEquals(2, program2.getStreams().size());

        Program program3 = result.getPrograms().get(2);
        assertEquals("3rdProgram", program3.getTag("service_name"));
        assertEquals((Integer) 3, program3.getProgramNum());
        assertEquals((Integer) 2, program3.getNbStreams());
        assertNotNull(program3.getStreams());
        assertEquals(2, program3.getStreams().size());
    }

    //private boolean showChapters;

    @Test
    public void testShowChapters() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_WITH_CHAPTERS)
                .setShowChapters(true)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getChapters());
        assertEquals(3, result.getChapters().size());

        Chapter chapter1 = result.getChapters().get(0);
        assertEquals(1, chapter1.getId());
        assertEquals("FirstChapter", chapter1.getTag("title"));
        assertEquals(new Rational(1L, 1_000_000_000L), chapter1.getTimeBase());
        assertEquals((Long) 0L, chapter1.getStart());
        assertEquals((Double) 0., chapter1.getStartTime(), 0.01);
        assertEquals((Long) 60_000_000_000L, chapter1.getEnd());
        assertEquals((Double) 60., chapter1.getEndTime(), 0.01);

        Chapter chapter2 = result.getChapters().get(1);
        assertEquals(2, chapter2.getId());
        assertEquals("Second Chapter", chapter2.getTag("title"));
        assertEquals((Long) 60_000_000_000L, chapter2.getStart());
        assertEquals((Double) 60., chapter2.getStartTime(), 0.01);

        Chapter chapter3 = result.getChapters().get(2);
        assertEquals(3, chapter3.getId());
        assertEquals("Final", chapter3.getTag("title"));
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

        assertNotNull(result);
        assertNotNull(result.getStreams());
        for (Stream stream : result.getStreams()) {
            assertTrue(stream.getNbFrames() > 0);
            assertTrue(stream.getNbReadFrames() > 0);
            assertTrue(stream.getNbReadPackets() > 0);
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

        assertNotNull(result);
        assertNotNull(result.getPackets());
        assertEquals(42, result.getPackets().size());
        for (Packet packet : result.getPackets()) {
            assertNotNull(packet.getCodecType());
        }
    }

    @Test
    public void testShowPacketsAndFrames() {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_WITH_SUBTITLES)
                .setShowPackets(true)
                .setShowFrames(true)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getPacketsAndFrames());
        assertTrue(result.getPacketsAndFrames().size() > 20_000);

        Set<Class<? extends PacketFrameSubtitle>> resultClasses = new HashSet<>();
        for (PacketFrameSubtitle pfs : result.getPacketsAndFrames()) {
            resultClasses.add(pfs.getClass());

            if (pfs instanceof Packet) {
                Packet packet = (Packet) pfs;

                assertNotNull(packet.getPts());
                assertNotNull(packet.getPtsTime());
                assertNotNull(packet.getCodecType());
                assertNotNull(packet.getDts());
                assertNotNull(packet.getDtsTime());
                assertNotNull(packet.getDuration());
                assertNotNull(packet.getDurationTime());
                assertNotNull(packet.getSize());
                assertNotNull(packet.getPos());
                assertNotNull(packet.getFlags());
                continue;
            }
            if (pfs instanceof Frame) {
                Frame frame = (Frame) pfs;

                assertNotNull(frame.getMediaType());
                assertNotNull(frame.getStreamIndex());
                assertNotNull(frame.getKeyFrame());
                assertNotNull(frame.getPktPts());
                assertNotNull(frame.getPktPtsTime());
                assertNotNull(frame.getPktDts());
                assertNotNull(frame.getPktDtsTime());
                assertNotNull(frame.getBestEffortTimestamp());
                assertNotNull(frame.getBestEffortTimestampTime());
                assertNotNull(frame.getPktDuration());
                assertNotNull(frame.getPktDurationTime());
                assertNotNull(frame.getPktPos());
                assertNotNull(frame.getPktSize());

                switch (frame.getMediaType()) {
                    case VIDEO:
                        assertNotNull(frame.getWidth());
                        assertNotNull(frame.getHeight());
                        assertNotNull(frame.getPixFmt());
                        assertNotNull(frame.getSampleAspectRatio());
                        assertNotNull(frame.getPictType());
                        assertNotNull(frame.getCodedPictureNumber());
                        assertNotNull(frame.getDisplayPictureNumber());
                        assertNotNull(frame.getInterlacedFrame());
                        assertNotNull(frame.getTopFieldFirst());
                        assertNotNull(frame.getRepeatPict());
                        break;
                    case AUDIO:
                        assertNotNull(frame.getSampleFmt());
                        assertNotNull(frame.getNbSamples());
                        assertNotNull(frame.getChannels());
                        assertNotNull(frame.getChannelLayout());
                        break;
                    default:
                        fail("Unexpected media type: " + frame.getMediaType());
                }
                continue;
            }
            if (pfs instanceof Subtitle) {
                Subtitle subtitle = (Subtitle) pfs;

                assertEquals(StreamType.SUBTITLE, subtitle.getMediaType());
                assertNotNull(subtitle.getPts());
                assertNotNull(subtitle.getPtsTime());
                assertNotNull(subtitle.getFormat());
                assertNotNull(subtitle.getStartDisplayTime());
                assertNotNull(subtitle.getEndDisplayTime());
                assertNotNull(subtitle.getNumRects());
                continue;
            }

            fail("Unexpected type: " + pfs);
        }

        assertThat(resultClasses, hasItems(Packet.class, Frame.class, Subtitle.class));
    }

    @Test
    public void testStreamSideDataListAttributes() throws Exception {
        FFprobeResult result;
        try (InputStream rotatedInput = FFprobeTest.class.getResourceAsStream("rotated.mp4")) {
            result = FFprobe.atPath(BIN)
                    .setInput(rotatedInput)
                    .setShowStreams(true)
                    .setFormatParser(formatParser)
                    .execute();
        }

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertEquals(1, result.getStreams().size());

        Stream stream = result.getStreams().get(0);
        assertNotNull(stream);

        assertNotNull(stream.getSideDataList());
        assertEquals(1, stream.getSideDataList().size());

        SideData sideData = stream.getSideDataList().get(0);
        assertNotNull(sideData);
        assertNotNull(sideData.getDisplayMatrix());
        assertEquals(3, sideData.getDisplayMatrix().trim().split("\n").length);
        assertNotNull(sideData.getRotation());
    }

    @Test
    public void testFrameSideDataListAttributes() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(VIDEO_MJPEG)
                .setShowFrames(true)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getFrames());
        assertFalse(result.getFrames().isEmpty());

        int sideDataCount = 0;
        for (FrameSubtitle frameSubtitle : result.getFrames()) {
            if (frameSubtitle instanceof Frame) {
                Frame frame = (Frame) frameSubtitle;
                assertNotNull(frame.getSideDataList());
                assertEquals(2, frame.getSideDataList().size());

                for (SideData sideData : frame.getSideDataList()) {
                    assertNotNull(sideData.getSideDataType());
                    sideDataCount++;
                }
            }
        }

        assertTrue(sideDataCount > 0);
    }

    @Test
    public void testPacketSideDataListAttributes() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setInput(AUDIO_OPUS)
                .setShowPackets(true)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getPackets());
        assertFalse(result.getPackets().isEmpty());

        int sideDataCount = 0;
        for (Packet packet : result.getPackets()) {
            if (packet.getSideDataList() == null) {
                continue;
            }
            sideDataCount++;
            for (SideData sideData : packet.getSideDataList()) {
                assertNotNull(sideData.getSideDataType());
                assertNotNull(sideData.getLong("skip_samples"));
                assertNotNull(sideData.getLong("discard_padding"));
                assertNotNull(sideData.getLong("skip_reason"));
                assertNotNull(sideData.getLong("discard_reason"));
            }
        }

        assertEquals(1, sideDataCount);
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

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertFalse(result.getStreams().isEmpty());
    }

    @Test
    public void testAnalyzeDuration() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setAnalyzeDuration(10_000_000L)
                .setInput(VIDEO_MP4)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertFalse(result.getStreams().isEmpty());
    }

    @Test
    public void testAnalyzeDuration2() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setAnalyzeDuration(10, TimeUnit.SECONDS)
                .setInput(VIDEO_MP4)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertFalse(result.getStreams().isEmpty());
    }

    @Test
    public void testFpsProbeSize() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setFpsProbeSize(100L)
                .setInput(VIDEO_MP4)
                .setFormatParser(formatParser)
                .execute();

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertFalse(result.getStreams().isEmpty());
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


        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertEquals(1, result.getStreams().size());

        Stream stream = result.getStreams().get(0);
        assertEquals(StreamType.VIDEO, stream.getCodecType());
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

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertFalse(result.getStreams().isEmpty());
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

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertFalse(result.getStreams().isEmpty());
    }

    @Test
    public void testAsyncExecution() throws Exception {
        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setInput(VIDEO_MP4)
                .setFormatParser(formatParser)
                .executeAsync()
                .get();

        assertNotNull(result);
        assertNotNull(result.getStreams());
        assertEquals(2, result.getStreams().size());

        Stream stream = result.getStreams().get(0);
        assertEquals(StreamType.VIDEO, stream.getCodecType());

        stream = result.getStreams().get(1);
        assertEquals(StreamType.AUDIO, stream.getCodecType());
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
