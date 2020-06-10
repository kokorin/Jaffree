package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.*;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.file.StandardOpenOption.*;

public class FFmpegTest {
    public static Path BIN;
    public static Path VIDEO_MP4 = Artifacts.getFFmpegSample("MPEG-4/video.mp4");
    public static Path SMALL_FLV = Artifacts.getFFmpegSample("FLV/zelda.flv");
    public static Path SMALL_MP4 = Artifacts.getFFmpegSample("MPEG-4/turn-on-off.mp4");
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
        Assert.assertTrue("Sample videos weren't found: " + SMALL_FLV.toAbsolutePath(), Files.exists(SMALL_FLV));
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
                .addInput(UrlInput.fromPath(SMALL_FLV))
                .addOutput(UrlOutput.toPath(outputPath))
                .setProgressListener(listener)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(counter.get() > 0);
    }

    @Test
    public void testProgress2() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("test.flv");

        final AtomicLong counter = new AtomicLong();

        ProgressListener listener = new ProgressListener() {
            @Override
            public void onProgress(FFmpegProgress progress) {
                counter.incrementAndGet();
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(SMALL_MP4))
                .addOutput(UrlOutput.toPath(outputPath))
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
    public void testStopping() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpeg ffmpeg = FFmpeg.atPath(BIN)
                .addInput(UrlInput
                        .fromPath(VIDEO_MP4)
                )
                .addOutput(UrlOutput.toPath(outputPath));

        Future<FFmpegResult> futureResult = ffmpeg.executeAsync();

        Thread.sleep(1_000);

        boolean cancelled = futureResult.cancel(true);
        Assert.assertTrue(cancelled);

        Thread.sleep(1_000);
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
                        .setSizeLimit(1, SizeUnit.MB)
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
    @Ignore("This test requires manual verification of result frames")
    public void testAlpha() throws Exception {
        // https://www.videezy.com/elements-and-effects/7213-animated-character-girl-biking-alpha-transparent
        Path videoWithAlpha = Artifacts.getSample(URI.create("https://static.videezy.com/system/protected/files/000/007/213/Biking_Girl_Alpha.mov?md5=zJB3WS6tzcdWmKjzHnSTLA&expires=1553233302"));

        FrameConsumer frameConsumer = new FrameConsumer() {
            @Override
            public void consumeStreams(List<com.github.kokorin.jaffree.ffmpeg.Stream> streams) {
               LOGGER.debug(streams + "");
            }

            @Override
            public void consume(Frame frame) {
                LOGGER.debug(frame + "");
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput
                        .fromPath(videoWithAlpha)
                        .setDuration(1_000)
                )
                .addOutput(FrameOutput
                        .withConsumerAlpha(frameConsumer)
                        .disableStream(StreamType.AUDIO)
                )
                .execute();

        Assert.assertNotNull(result);
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
        final AtomicBoolean loudnormReportFound = new AtomicBoolean();

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addArguments("-af", "loudnorm=I=-16:TP=-1.5:LRA=11:print_format=json")
                .addOutput(new NullOutput(false))
                .setOutputListener(new OutputListener() {
                    @Override
                    public boolean onOutput(String line) {
                        if (line.contains("loudnorm")) {
                            loudnormReportFound.set(true);
                        }
                        return loudnormReportFound.get();
                    }
                })
                .execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(loudnormReportFound.get());
    }

    @Test
    public void testPipeInput() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result;

        try (InputStream inputStream = Files.newInputStream(VIDEO_MP4)) {
            result = FFmpeg.atPath(BIN)
                    .addInput(PipeInput.pumpFrom(inputStream))
                    .addOutput(UrlOutput.toPath(outputPath))
                    .execute();
        }

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getVideoSize());

        Assert.assertTrue(getDuration(outputPath) > 10.);
    }

    @Test
    public void testPipeInputPartialRead() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result;

        try (InputStream inputStream = Files.newInputStream(VIDEO_MP4)) {
            result = FFmpeg.atPath(BIN)
                    .addInput(
                            PipeInput
                                    .pumpFrom(inputStream)
                                    .setDuration(15, TimeUnit.SECONDS)
                    )
                    .addOutput(UrlOutput.toPath(outputPath))
                    .execute();
        }

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getVideoSize());

        Assert.assertTrue(getDuration(outputPath) > 10.);
    }


    @Test
    public void testPipeInputAsync() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(PipeInput.withSupplier(new TcpInput.Supplier() {
                    @Override
                    public void supplyAndClose(final OutputStream out) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                try (InputStream inputStream = Files.newInputStream(VIDEO_MP4);
                                     Closeable toClose = out) {
                                    IOUtils.copyLarge(inputStream, out);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        };

                        Thread thread = new Thread(runnable, "Supplier");
                        thread.start();
                    }
                }))
                .addOutput(UrlOutput.toPath(outputPath))
                .execute();


        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getVideoSize());

        Assert.assertTrue(getDuration(outputPath) > 10.);
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
    public void testPipeOutputAsync() throws IOException {
        Path tempDir = Files.createTempDirectory("jaffree");
        final Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addOutput(PipeOutput.withConsumer(
                        new TcpOutput.Consumer() {
                            @Override
                            public void consumeAndClose(final InputStream in) {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        try (OutputStream outputStream = Files.newOutputStream(outputPath, CREATE);
                                             Closeable toClose = in) {
                                            IOUtils.copyLarge(in, outputStream);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                };

                                Thread thread = new Thread(runnable, "Consumer");
                                thread.start();
                            }
                        }
                ).setFormat("flv"))
                .setOverwriteOutput(true)
                .execute();

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
    public void testDesktopCapture() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path output = tempDir.resolve("desktop.mp4");
        LOGGER.debug("Will write to " + output);

        Rectangle area = new Rectangle(80,60,160,120);
        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(DesktopCaptureInput
                        .fromScreen()
                        .setArea(area)
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

}
