package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.Artifacts;
import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StackTraceMatcher;
import com.github.kokorin.jaffree.StreamSpecifier;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.DefaultFormatParser;
import com.github.kokorin.jaffree.ffprobe.data.FlatFormatParser;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
        boolean bitsPerSampleIsPresent = false;
        for (Stream stream : result.getStreams()) {
            if (stream.getCodecType() == StreamType.VIDEO) {

                Assert.assertNotNull(stream.getSampleAspectRatio());
                Assert.assertNotNull(stream.getDisplayAspectRatio());
                Assert.assertNotNull(stream.getStartTime(TimeUnit.NANOSECONDS));
                Assert.assertEquals(Long.valueOf(167L), stream.getDuration(TimeUnit.SECONDS));
                Assert.assertNotNull(stream.getBitRate());
                Assert.assertNotNull(stream.getMaxBitRate());
            }

            // TODO: find video sample for which ffprobe reports bits_per_raw_sample
            // Assert.assertNotNull(stream.getBitsPerRawSample());

            bitsPerSampleIsPresent |= stream.getBitsPerSample() != null;
        }

        Assert.assertTrue("bits per sample hasn't been found in any stream", bitsPerSampleIsPresent);
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
        //TODO: Find media file with chapters
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

    @Test
    public void testAdditionalArguments() {
        FFprobeResult result = FFprobe.atPath(BIN)
                // The same as .setShowStreams(true), just for testing
                .addArgument("-show_streams")
                .addArguments("-select_streams", "v")
                .setInput(VIDEO_MP4)
                .execute();


        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getStreams().size());

        Stream stream = result.getStreams().get(0);
        Assert.assertEquals(StreamType.VIDEO, stream.getCodecType());
    }

    @Test
    public void testDataFormat() throws Exception {
        FFprobeResult defaultResult = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setInput(VIDEO_MP4)
                .setFormatParser(new DefaultFormatParser())
                .execute();


        FFprobeResult flatResult = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setInput(VIDEO_MP4)
                .setFormatParser(new FlatFormatParser())
                .execute();

        compareByGetters("", defaultResult, flatResult);
    }

    @Test
    public void testInputStream() throws Exception {
        FFprobeResult result;

        try (InputStream inputStream = Files.newInputStream(VIDEO_MP4, StandardOpenOption.READ)) {
            result = FFprobe.atPath(BIN)
                    .setShowStreams(true)
                    .setInput(inputStream)
                    .setFormatParser(new DefaultFormatParser())
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
                    .setFormatParser(new DefaultFormatParser())
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
                .executeAsync()
                .get();

        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.getStreams().size());

        Stream stream = result.getStreams().get(0);
        Assert.assertEquals(StreamType.VIDEO, stream.getCodecType());

        stream = result.getStreams().get(2);
        Assert.assertEquals(StreamType.AUDIO, stream.getCodecType());
    }


    @Test
    public void testAsyncExecutionWithException() throws Exception {
        expectedException.expect(new StackTraceMatcher("No such file or directory"));

        FFprobeResult result = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setInput("non_existent.mp4")
                .executeAsync()
                .get();

        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.getStreams().size());

        Stream stream = result.getStreams().get(0);
        Assert.assertEquals(StreamType.VIDEO, stream.getCodecType());

        stream = result.getStreams().get(2);
        Assert.assertEquals(StreamType.AUDIO, stream.getCodecType());
    }

    private static List<? extends Class> noDeepCompare = Arrays.asList(
            int.class, short.class, long.class, float.class, double.class, boolean.class,
            Integer.class, Short.class, Long.class, Float.class, Double.class, Boolean.class,
            String.class
    );

    private static void compareByGetters(String context, Object o1, Object o2) throws Exception {
        if (Objects.equals(o1, o2)) {
            return;
        }

        if (o1 == null || o2 == null) {
            throw new AssertionFailedError(context + " null: " + o1 + " " + o2);
        }

        if (o1.getClass() != o2.getClass()) {
            throw new AssertionFailedError(context + " class: " + o1 + " " + o2);
        }

        Class clazz = o1.getClass();

        if (noDeepCompare.contains(clazz)) {
            throw new AssertionFailedError(context + " not equal: " + o1 + " " + o2);
        }

        if (o1 instanceof List) {
            List l1 = (List) o1;
            List l2 = (List) o2;

            if (l1.size() != l2.size()) {
                throw new AssertionFailedError(context + " size: " + o1 + " " + o2);
            }

            for (int i = 0; i < l1.size(); i++) {
                String subContext = context + "[" + i + "]";
                compareByGetters(subContext, l1.get(i), l2.get(i));
            }

            return;
        }

        if (clazz.getPackage().getName().startsWith("com.github.kokorin.jaffree")) {
            for (Method method : clazz.getMethods()) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    continue;
                }

                if (!method.getName().startsWith("get")) {
                    continue;
                }

                if (method.getParameterTypes().length > 0) {
                    continue;
                }

                String subContext = method.getName() + "()";
                if (!context.isEmpty()) {
                    subContext = context + "." + subContext;
                }

                Object s1 = method.invoke(o1);
                Object s2 = method.invoke(o2);

                compareByGetters(subContext, s1, s2);
            }


            return;
        }

        throw new AssertionFailedError("Don't know how to compare " + clazz);
    }
}
