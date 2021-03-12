package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.Artifacts;
import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.StackTraceMatcher;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import com.github.kokorin.jaffree.process.ProcessHelper;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class FFmpegTest {
    public static Path BIN;
    public static Path VIDEO_MP4 = Artifacts.getMp4Artifact();
    public static Path VIDEO_FLV = Artifacts.getFlvArtifact();
    public static Path SMALL_MP4 = Artifacts.getSmallFlvArtifact();
    public static Path ERROR_MP4 = Paths.get("non_existent.mp4");

    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegTest.class);

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
        Assert.assertTrue("Sample videos weren't found: " + VIDEO_FLV.toAbsolutePath(), Files.exists(VIDEO_FLV));
        Assert.assertTrue("Sample videos weren't found: " + SMALL_MP4.toAbsolutePath(), Files.exists(SMALL_MP4));
    }

    @Test
    public void testSimpleCopy() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addOutput(UrlOutput
                        .toPath(outputPath)
                        .copyAllCodecs())
                .execute();

        Assert.assertNotNull(result);
    }

    // For this test to pass ffmpeg must be added to Operation System PATH environment variable
    @Test
    public void testEnvPath() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath()
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addOutput(UrlOutput
                        .toPath(outputPath)
                        .copyAllCodecs())
                .execute();

        Assert.assertNotNull(result);
    }

    @Test
    public void testOutputAdditionalOption() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("test.mp3");

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addOutput(UrlOutput
                        .toPath(outputPath)
                        .setCodec(StreamType.AUDIO, "mp3")
                        .disableStream(StreamType.VIDEO)
                        .addArguments("-id3v2_version", "3")
                )
                .execute();

        Assert.assertNotNull(result);

        FFprobeResult probe = FFprobe.atPath(BIN)
                .setInput(outputPath)
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(probe);
        Assert.assertEquals(1, probe.getStreams().size());
        Assert.assertEquals(StreamType.AUDIO, probe.getStreams().get(0).getCodecType());
    }

    @Test
    public void testProgress() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("test.mkv");

        final AtomicLong counter = new AtomicLong();

        ProgressListener listener = new ProgressListener() {
            @Override
            public void onProgress(FFmpegProgress progress) {
                counter.incrementAndGet();
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_FLV))
                .addOutput(UrlOutput.toPath(outputPath))
                .setProgressListener(listener)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(counter.get() > 0);

        outputPath = tempDir.resolve("test.flv");
        counter.set(0L);

        result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(SMALL_MP4))
                .addOutput(UrlOutput.toPath(outputPath))
                .setProgressListener(listener)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(counter.get() > 0);
    }

    @Test
    public void testProgressWithErrorLogLevel() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("test.mkv");

        final AtomicLong counter = new AtomicLong();

        ProgressListener listener = new ProgressListener() {
            @Override
            public void onProgress(FFmpegProgress progress) {
                counter.incrementAndGet();
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_FLV))
                .addOutput(UrlOutput.toPath(outputPath))
                .setLogLevel(LogLevel.ERROR)
                .setProgressListener(listener)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(counter.get() > 0);
    }

    @Test
    public void testDuration() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput
                        .fromPath(VIDEO_MP4)
                        .setDuration(10, TimeUnit.SECONDS)
                )
                .addOutput(UrlOutput
                        .toPath(outputPath)
                        .copyAllCodecs())
                .execute();

        Assert.assertNotNull(result);

        double outputDuration = getDuration(outputPath);
        Assert.assertEquals(10.0, outputDuration, 0.1);

        result = FFmpeg.atPath(BIN)
                .addInput(UrlInput
                        .fromPath(VIDEO_MP4)
                        .setDuration(1. / 6., TimeUnit.MINUTES)
                )
                .setOverwriteOutput(true)
                .addOutput(UrlOutput
                        .toPath(outputPath)
                        .copyAllCodecs())
                .execute();

        Assert.assertNotNull(result);

        outputDuration = getDuration(outputPath);
        Assert.assertEquals(10.0, outputDuration, 0.1);
    }

    @Test
    public void testForceStopWithProgressListenerException() throws Exception {
        expectedException.expect(new StackTraceMatcher("Stop ffmpeg with ProgressListener Exception"));

        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        final long startedAtMillis = System.currentTimeMillis();
        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(FFmpegProgress progress) {
                System.out.println(progress);
                if (System.currentTimeMillis() - startedAtMillis > 5_000) {
                    throw new RuntimeException("Stop ffmpeg with ProgressListener Exception");
                }
            }
        };

        final FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput
                        .fromPath(VIDEO_MP4)
                        .setReadAtFrameRate(true)
                )
                .setProgressListener(progressListener)
                .addOutput(UrlOutput.toPath(outputPath))
                .execute();
    }

    @Test
    public void testForceStopWithThreadInterruption() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        final AtomicReference<FFmpegResult> result = new AtomicReference<>();
        final FFmpeg ffmpeg = FFmpeg.atPath(BIN)
                .addInput(UrlInput
                        .fromPath(VIDEO_MP4)
                        .setReadAtFrameRate(true)
                )
                .addOutput(UrlOutput.toPath(outputPath));

        final AtomicReference<Exception> executeException = new AtomicReference<>();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    FFmpegResult r = ffmpeg.execute();
                    result.set(r);
                } catch (Exception e) {
                    executeException.set(e);
                }
            }
        };
        thread.start();

        Thread.sleep(5_000);
        thread.interrupt();

        Thread.sleep(1_000);
        Assert.assertNull(result.get());
        Assert.assertTrue(Files.exists(outputPath));
        Assert.assertTrue(executeException.get() instanceof RuntimeException);
        Assert.assertEquals("Failed to execute, was interrupted", executeException.get().getMessage());
    }

    @Test
    public void testForceAsyncStop() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpeg ffmpeg = FFmpeg.atPath(BIN)
                .addInput(UrlInput
                        .fromPath(VIDEO_MP4)
                        .setReadAtFrameRate(true)
                )
                .addOutput(UrlOutput.toPath(outputPath));

        FFmpegResultFuture futureResult = ffmpeg.executeAsync();

        Thread.sleep(5_000);

        futureResult.forceStop();

        Thread.sleep(1_000);

        Assert.assertTrue(Files.exists(outputPath));
    }

    @Test
    public void testGraceAsyncStop() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        final AtomicReference<FFmpegResultFuture> futureRef = new AtomicReference<>();
        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(FFmpegProgress progress) {
                System.out.println(progress);
                if (progress.getTime(TimeUnit.SECONDS) >= 15) {
                    futureRef.get().graceStop();
                }
            }
        };

        FFmpeg ffmpeg = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .setProgressListener(progressListener)
                .addOutput(UrlOutput.toPath(outputPath));

        FFmpegResultFuture futureResult = ffmpeg.executeAsync();
        futureRef.set(futureResult);

        FFmpegResult encodingResult = futureResult.get(12, TimeUnit.SECONDS);
        Assert.assertNotNull(encodingResult);

        FFprobeResult probeResult = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setInput(outputPath)
                .execute();

        Assert.assertEquals(2, probeResult.getStreams().size());


        final AtomicReference<Long> durationRef = new AtomicReference<>();
        final ProgressListener progressDurationListener = new ProgressListener() {
            @Override
            public void onProgress(FFmpegProgress progress) {
                System.out.println(progress);
                durationRef.set(progress.getTime(TimeUnit.SECONDS));
            }
        };
        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(outputPath))
                .setProgressListener(progressDurationListener)
                .addOutput(new NullOutput())
                .execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(durationRef.get() >= 15);
    }

    @Test
    public void testOutputPosition() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addOutput(UrlOutput
                        .toPath(outputPath)
                        .copyAllCodecs()
                        .setOutputPosition(15, TimeUnit.SECONDS)
                )
                .execute();

        Assert.assertNotNull(result);

        double outputDuration = getDuration(outputPath);
        Assert.assertEquals(15.0, outputDuration, 0.1);
    }

    @Test
    public void testSizeLimit() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addOutput(UrlOutput
                        .toPath(outputPath)
                        .copyAllCodecs()
                        .setSizeLimit(1_000_000L)
                )
                .execute();

        Assert.assertNotNull(result);

        long outputSize = Files.size(outputPath);
        Assert.assertTrue(outputSize > 900_000);
        Assert.assertTrue(outputSize < 1_100_000);
    }

    @Test
    public void testPosition() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput
                        .fromPath(VIDEO_MP4)
                        .setPosition(10, TimeUnit.SECONDS)
                )
                .addOutput(UrlOutput
                        .toPath(outputPath)
                        .copyAllCodecs())
                .execute();

        Assert.assertNotNull(result);

        double inputDuration = getDuration(VIDEO_MP4);
        double outputDuration = getDuration(outputPath);

        Assert.assertEquals(inputDuration - 10, outputDuration, 0.5);
    }

    @Test
    public void testPositionNegative() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput
                        .fromPath(VIDEO_MP4)
                        .setPositionEof(-7, TimeUnit.SECONDS)
                )
                .addOutput(UrlOutput
                        .toPath(outputPath)
                        .copyAllCodecs())
                .execute();

        Assert.assertNotNull(result);

        double outputDuration = getDuration(outputPath);

        Assert.assertEquals(7.0, outputDuration, 0.5);
    }

    @Test
    public void testNullOutput() throws Exception {
        final AtomicLong time = new AtomicLong();

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput
                        .fromPath(VIDEO_MP4)
                )
                .addOutput(
                        new NullOutput()
                )
                .setOverwriteOutput(true)
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        time.set(progress.getTimeMillis());
                    }
                })
                .execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(time.get() > 165_000);
    }

    @Test
    public void testMap() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addOutput(UrlOutput
                        .toPath(outputPath)
                        .copyAllCodecs()
                        .addMap(0, StreamType.AUDIO)
                        .addMap(0, StreamType.AUDIO)
                        .addMap(0, StreamType.VIDEO)
                )
                .execute();

        Assert.assertNotNull(result);

        FFprobeResult probe = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setInput(outputPath)
                .execute();
        Assert.assertNull(probe.getError());

        List<Stream> streamTypes = probe.getStreams();

        Assert.assertEquals(3, streamTypes.size());
        Assert.assertEquals(StreamType.AUDIO, streamTypes.get(0).getCodecType());
        Assert.assertEquals(StreamType.AUDIO, streamTypes.get(1).getCodecType());
        Assert.assertEquals(StreamType.VIDEO, streamTypes.get(2).getCodecType());
    }

    @Test
    public void testExceptionIsThrownIfFfmpegExitsWithError() {
        expectedException.expect(new StackTraceMatcher("No such file or directory"));

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(ERROR_MP4))
                .addOutput(new NullOutput())
                .execute();
    }

    @Test
    public void testCustomOutputParsing() {
        // StringBuffer - because it's thread safe
        final AtomicReference<String> loudnormReport = new AtomicReference<>();

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addArguments("-af", "loudnorm=I=-16:TP=-1.5:LRA=11:print_format=json")
                .addOutput(new NullOutput(false))
                .setOutputListener(new OutputListener() {
                    @Override
                    public void onOutput(String message) {
                        if (message.contains("loudnorm")) {
                            loudnormReport.set(message);
                        }
                    }
                })
                .execute();

        Assert.assertNotNull(result);

        MatcherAssert.assertThat(loudnormReport.get(), AllOf.allOf(
                StringContains.containsString("input_i"),
                StringContains.containsString("input_tp"),
                StringContains.containsString("input_lra"),
                StringContains.containsString("input_thresh"),
                StringContains.containsString("output_i"),
                StringContains.containsString("output_tp"),
                StringContains.containsString("output_lra"),
                StringContains.containsString("output_thresh"),
                StringContains.containsString("normalization_type"),
                StringContains.containsString("target_offset")
        ));
    }

    @Test
    public void testCustomOutputParsing2() {
        // StringBuffer - because it's thread safe
        final StringBuffer idetReport = new StringBuffer();

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .setFilter(StreamType.VIDEO, "idet")
                .addOutput(
                        new NullOutput(false)
                                .setFrameCount(StreamType.VIDEO, 100L)
                )
                .setOutputListener(new OutputListener() {
                    @Override
                    public void onOutput(String line) {
                        if (line.startsWith("[Parsed_idet")) {
                            idetReport.append(line);
                        }
                    }
                })
                .execute();

        Assert.assertNotNull(result);

        MatcherAssert.assertThat(idetReport.toString(), AllOf.allOf(
                StringContains.containsString("Repeated Fields"),
                StringContains.containsString("Single frame detection"),
                StringContains.containsString("Multi frame detection")
        ));
    }

    @Test
    public void testPipeInput() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_FLV.getFileName());

        FFmpegResult result;

        try (InputStream inputStream = Files.newInputStream(VIDEO_FLV)) {
            result = FFmpeg.atPath(BIN)
                    .addInput(PipeInput.pumpFrom(inputStream))
                    .addOutput(UrlOutput.toPath(outputPath))
                    .execute();
        }

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getVideoSize());

        double expectedDuration = getExactDuration(VIDEO_FLV);
        double actualDuration = getExactDuration(outputPath);
        Assert.assertEquals(expectedDuration, actualDuration, 1.);
    }

    @Test
    public void testPipeInputPartialRead() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_FLV.getFileName());

        FFmpegResult result;

        try (InputStream inputStream = Files.newInputStream(VIDEO_FLV)) {
            result = FFmpeg.atPath(BIN)
                    .addInput(
                            PipeInput
                                    .pumpFrom(inputStream)
                                    .setDuration(15, TimeUnit.SECONDS)
                    )
                    .setLogLevel(LogLevel.VERBOSE)
                    .addOutput(UrlOutput.toPath(outputPath))
                    .execute();
        }

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getVideoSize());

        double actualDuration = getExactDuration(outputPath);
        Assert.assertEquals(15., actualDuration, 1.);
    }

    @Test
    public void testPipeOutput() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result;
        try (OutputStream outputStream = Files.newOutputStream(outputPath, CREATE)) {
            result = FFmpeg.atPath(BIN)
                    .addInput(UrlInput.fromPath(VIDEO_MP4))
                    .addOutput(PipeOutput.pumpTo(outputStream).setFormat("flv"))
                    .setOverwriteOutput(true)
                    .execute();
        }

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getVideoSize());

        Assert.assertTrue(getExactDuration(outputPath) > 10.);
    }

    @Test
    public void testChannelInput() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("channel.mp4");

        try (SeekableByteChannel channel = Files.newByteChannel(VIDEO_MP4, READ)) {
            FFmpegResult result = FFmpeg.atPath(BIN)
                    .addInput(
                            new ChannelInput("testChannelInput.mp4", channel)
                    )
                    .addOutput(
                            UrlOutput.toPath(outputPath)
                    )
                    .setLogLevel(LogLevel.DEBUG)
                    .execute();

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getVideoSize());
        }

        Assert.assertTrue(Files.exists(outputPath));
        Assert.assertTrue(Files.size(outputPath) > 1000);
    }

    @Test
    public void testChannelInputPartialRead() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("channel.mp4");

        try (SeekableByteChannel channel = Files.newByteChannel(VIDEO_MP4, READ)) {
            FFmpegResult result = FFmpeg.atPath(BIN)
                    .addInput(
                            new ChannelInput("testChannelInputPartialRead.mp4", channel)
                                    .setDuration(10, TimeUnit.SECONDS)
                    )
                    .addOutput(
                            UrlOutput.toPath(outputPath)
                    )
                    .setLogLevel(LogLevel.INFO)
                    .execute();

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getVideoSize());
        }

        Assert.assertTrue(Files.exists(outputPath));
        Assert.assertTrue(Files.size(outputPath) > 1000);
    }

    @Test
    public void testChannelInputSeek() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("frame.jpg");

        try (SeekableByteChannel channel = Files.newByteChannel(VIDEO_MP4, READ)) {
            FFmpegResult result = FFmpeg.atPath(BIN)
                    .addInput(
                            new ChannelInput("testChannelInputSeek.mp4", channel)
                                    .setPosition(1, TimeUnit.MINUTES)
                    )
                    .addOutput(
                            UrlOutput.toPath(outputPath)
                                    .setFrameCount(StreamType.VIDEO, 1L)
                    )
                    .setLogLevel(LogLevel.INFO)
                    .execute();

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getVideoSize());
        }

        Assert.assertTrue(Files.exists(outputPath));
        Assert.assertTrue(Files.size(outputPath) > 1000);
    }

    @Test
    public void testChannelOutput() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("channel.mp4");

        LOGGER.debug("Will write to " + outputPath);

        try (SeekableByteChannel channel = Files.newByteChannel(outputPath, CREATE, WRITE, READ, TRUNCATE_EXISTING)) {
            FFmpegResult result = FFmpeg.atPath(BIN)
                    .addInput(
                            UrlInput.fromPath(VIDEO_MP4)
                    )
                    .addOutput(
                            new ChannelOutput("channel.mp4", channel)
                    )
                    .setOverwriteOutput(true)
                    .setLogLevel(LogLevel.INFO)
                    .execute();

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getVideoSize());
        }

        Assert.assertTrue(Files.exists(outputPath));
        Assert.assertTrue(Files.size(outputPath) > 1000);
    }

    @Test
    public void testStreamFilters() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        LOGGER.debug("Will write to " + outputPath);

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .setFilter(StreamType.VIDEO, "crop=64:48:32:32")
                .setFilter(StreamType.AUDIO, "aecho=0.8:0.88:6:0.4")
                .addOutput(UrlOutput
                        .toPath(outputPath)
                )
                .execute();

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getVideoSize());
        Assert.assertTrue(Files.exists(outputPath));
        Assert.assertTrue(Files.size(outputPath) > 1000);
    }

    private static double getDuration(Path path) {
        FFprobeResult probe = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setInput(path)
                .execute();
        Assert.assertNull(probe.getError());

        double result = 0.0;
        for (Stream stream : probe.getStreams()) {
            result = Math.max(result, stream.getDuration());
        }

        return result;
    }

    private static double getExactDuration(Path path) {
        final AtomicReference<FFmpegProgress> progressRef = new AtomicReference<>();

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(path))
                .addOutput(new NullOutput())
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        progressRef.set(progress);
                    }
                })
                .execute();

        return progressRef.get().getTime(TimeUnit.SECONDS);
    }

    @Test
    @Ignore("This test requires a non-headless environment to work")
    public void testDesktopCapture() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path output = tempDir.resolve("desktop.mp4");
        LOGGER.debug("Will write to " + output);

        //Rectangle area = new Rectangle(80, 60, 160, 120);
        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(CaptureInput
                        .captureDesktop()
                        //.setArea(area)
                        .setFrameRate(10)
                )
                .addOutput(UrlOutput
                        .toPath(output)
                        .setDuration(10, TimeUnit.SECONDS)
                )
                .setOverwriteOutput(true)
                .execute();

        Assert.assertNotNull(result);


        FFprobeResult probe = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setInput(output)
                .execute();
        Assert.assertNull(probe.getError());

        List<Stream> streamTypes = probe.getStreams();

        Assert.assertEquals(1, streamTypes.size());

        final Stream stream0 = streamTypes.get(0);
        Assert.assertEquals(StreamType.VIDEO, stream0.getCodecType());
        Assert.assertEquals(10.0, stream0.getDuration(), 0.1);
        Assert.assertEquals(160L, (long) stream0.getWidth());
        Assert.assertEquals(120L, (long) stream0.getHeight());
    }

    @Test
    public void testHelperIsClosedAfterExecution() {
        final AtomicBoolean inputHelperClosed = new AtomicBoolean(false);
        final AtomicBoolean outputHelperClosed = new AtomicBoolean(false);

        class NotifyCloseHelper implements ProcessHelper {
            private final AtomicBoolean helperClosed;

            public NotifyCloseHelper(AtomicBoolean helperClosed) {
                this.helperClosed = helperClosed;
            }

            @Override
            public void close() throws IOException {
                helperClosed.set(true);
            }

            @Override
            public void run() {
            }
        }

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        new UrlInput(VIDEO_MP4.toString()) {
                            @Override
                            public ProcessHelper helperThread() {
                                return new NotifyCloseHelper(inputHelperClosed);
                            }
                        }
                )
                .addOutput(
                        new NullOutput() {
                            @Override
                            public ProcessHelper helperThread() {
                                return new NotifyCloseHelper(outputHelperClosed);
                            }
                        }
                )
                .execute();

        Assert.assertTrue(inputHelperClosed.get());
        Assert.assertTrue(outputHelperClosed.get());
    }
}
