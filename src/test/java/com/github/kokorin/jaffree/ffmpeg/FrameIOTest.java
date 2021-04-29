package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.Artifacts;
import com.github.kokorin.jaffree.StackTraceMatcher;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FrameIOTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static Path BIN;

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameIOTest.class);

    @BeforeClass
    public static void setUp() throws Exception {
        String ffmpegHome = System.getProperty("FFMPEG_BIN");
        if (ffmpegHome == null) {
            ffmpegHome = System.getenv("FFMPEG_BIN");
        }
        Assert.assertNotNull("Nor command line property, neither system variable FFMPEG_BIN is set up", ffmpegHome);
        BIN = Paths.get(ffmpegHome);
    }

    @Test
    public void countFrames() throws Exception {
        final AtomicLong trackCounter = new AtomicLong();
        final AtomicLong frameCounter = new AtomicLong();
        FrameConsumer consumer = new FrameConsumer() {

            @Override
            public void consumeStreams(List<Stream> tracks) {
                trackCounter.set(tracks.size());
            }

            @Override
            public void consume(Frame frame) {
                if (frame == null) {
                    return;
                }

                frameCounter.incrementAndGet();
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        UrlInput.fromPath(Artifacts.VIDEO_MP4)
                )
                .addOutput(
                        FrameOutput.withConsumer(consumer)
                                .disableStream(StreamType.AUDIO)
                                //.disableStream(StreamType.ATTACHMENT)
                                .disableStream(StreamType.DATA)
                                .disableStream(StreamType.SUBTITLE)
                                .setFrameCount(StreamType.VIDEO, 42L)
                )
                .execute();

        Assert.assertNotNull(result);
        assertEquals(1, trackCounter.get());
        assertEquals(42L, frameCounter.get());
    }

    @Test
    public void testStreamId() throws Exception {
        expectedException.expect(new StackTraceMatcher("Stream ids must start with 0 and increase by 1 subsequently"));

        FrameProducer producer = new FrameProducer() {

            @Override
            public List<Stream> produceStreams() {
                return Collections.singletonList(new Stream()
                        .setId(1)
                        .setType(Stream.Type.VIDEO)
                        .setTimebase(1_000L)
                        .setWidth(320)
                        .setHeight(240)
                );
            }

            @Override
            public Frame produce() {
                return null;
            }
        };

        NutFrameWriter writer = new NutFrameWriter(producer, ImageFormats.BGR24, 200);
        writer.write(new NullOutputStream());
    }

    @Test
    public void testMultipleStreams() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        Path output = tempDir.resolve("output.mp4");
        LOGGER.debug("Will write to " + output);

        FrameProducer producer = new FrameProducer() {
            private int frame = 0;

            @Override
            public List<Stream> produceStreams() {
                return Arrays.asList(
                        new Stream()
                                .setId(0)
                                .setType(Stream.Type.VIDEO)
                                .setTimebase(10L)
                                .setWidth(640)
                                .setHeight(480),
                        new Stream()
                                .setId(1)
                                .setType(Stream.Type.VIDEO)
                                .setTimebase(10L)
                                .setWidth(320)
                                .setHeight(240),
                        new Stream()
                                .setId(2)
                                .setType(Stream.Type.AUDIO)
                                .setTimebase(44_100L)
                                .setSampleRate(44_100)
                                .setChannels(1),
                        new Stream()
                                .setId(3)
                                .setType(Stream.Type.AUDIO)
                                .setTimebase(44_100L)
                                .setSampleRate(44_100)
                                .setChannels(1)
                );
            }

            @Override
            public Frame produce() {
                if (frame > 400) {
                    return null;
                }

                int type = frame % 4;
                long frameNumber = frame / 4;
                long pts = 0;
                BufferedImage image = null;
                int[] samples = null;
                switch (type) {
                    case 0:
                        image = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
                        pts = frameNumber;
                        break;
                    case 1:
                        image = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
                        pts = frameNumber;
                        break;
                    case 2:
                    case 3:
                        pts = 4410 * frameNumber;
                        samples = new int[4410 * 4];
                        break;
                }
                frame++;

                return new Frame(type, pts, image, samples);
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        FrameInput.withProducer(producer)
                                .setFrameRate(10)
                )
                .addOutput(
                        UrlOutput.toPath(output)
                                .addMap(0)
                )
                .execute();

        Assert.assertNotNull(result);

        FFprobeResult probe = FFprobe.atPath(BIN)
                .setInput(output)
                .setShowStreams(true)
                .execute();

        assertEquals(4, probe.getStreams().size());
    }

    @Test
    public void produceAndConsume() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        Path output = tempDir.resolve("output.mkv");

        int width = 720;
        int height = 480;
        int duration = 20;
        int sampleRate = 44100;
        int fps = 10;

        assertEquals(0, sampleRate % fps);

        BufferedImage flag = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = flag.createGraphics();
        graphics.setPaint(Color.WHITE);
        graphics.fillRect(0, 0, width, height / 3);
        graphics.setPaint(Color.BLUE);
        graphics.fillRect(0, height / 3, width, height / 3);
        graphics.setPaint(Color.RED);
        graphics.fillRect(0, 2 * height / 3, width, height / 3);

        int[] samples = new int[sampleRate / fps];

        FrameProducer frameProducer = new FrameProducer() {
            private int videoPts = 0;
            private int audioPts = 0;

            @Override
            public List<Stream> produceStreams() {
                return Arrays.asList(
                        new Stream()
                                .setId(0)
                                .setType(Stream.Type.VIDEO)
                                .setWidth(width)
                                .setHeight(height)
                                .setTimebase((long) fps),
                        new Stream()
                                .setId(1)
                                .setType(Stream.Type.AUDIO)
                                .setSampleRate(sampleRate)
                                .setChannels(1)
                                .setTimebase((long) fps)
                );
            }

            @Override
            public Frame produce() {
                if (videoPts > fps * duration) {
                    return null;
                }

                if (videoPts <= audioPts) {
                    return Frame.createVideoFrame(0, videoPts++, flag);
                }

                return Frame.createAudioFrame(1, audioPts++, samples);
            }
        };

        FFmpeg.atPath()
                .addInput(FrameInput
                        .withProducer(frameProducer)
                        .setFrameRate(fps)
                )
                .addOutput(UrlOutput
                        .toPath(output)
                        .setCodec(StreamType.VIDEO, "ffv1")
                )
                .execute();

        AtomicInteger videoCounter = new AtomicInteger();
        AtomicInteger sampleCounter = new AtomicInteger();

        FFmpeg.atPath()
                .addInput(UrlInput.fromPath(output))
                .addOutput(FrameOutput.withConsumer(new FrameConsumer() {
                    @Override
                    public void consumeStreams(List<Stream> streams) {
                        assertEquals(2, streams.size());

                        Stream video = streams.get(0);
                        assertEquals(0, video.getId());
                        assertEquals(Stream.Type.VIDEO, video.getType());
                        assertEquals((Integer) width, video.getWidth());
                        assertEquals((Integer) height, video.getHeight());

                        Stream audio = streams.get(1);
                        assertEquals(1, audio.getId());
                        assertEquals(Stream.Type.AUDIO, audio.getType());
                        assertEquals(Long.valueOf(sampleRate), audio.getSampleRate());
                        assertEquals((Integer) 1, audio.getChannels());
                    }

                    @Override
                    public void consume(Frame frame) {
                        if (frame == null) {
                            return;
                        }

                        if (frame.getStreamId() == 0) {
                            BufferedImage image = frame.getImage();
                            assertEquals(BufferedImage.TYPE_3BYTE_BGR, image.getType());

                            assertEquals(Color.WHITE, new Color(image.getRGB(1, 1)));
                            assertEquals(Color.BLUE, new Color(image.getRGB(width / 2, height / 2)));
                            assertEquals(Color.RED, new Color(image.getRGB(width - 1, height - 1)));

                            // fully compare every 10 frames to speed up test
                            if (videoCounter.get() % 10 == 0) {
                                for (int x = 0; x < width; x++) {
                                    for (int y = 0; y < height; y++) {
                                        assertEquals("x: " + x + ", y: " + y,
                                                flag.getRGB(x, y), image.getRGB(x, y));
                                    }
                                }
                            }

                            videoCounter.incrementAndGet();
                            return;
                        }

                        int[] samples = frame.getSamples();
                        assertNotNull(samples);
                        sampleCounter.addAndGet(samples.length);
                    }
                }))
                .execute();

        // total number of frames can differ from expected
        int expectedFrames = fps * duration;
        int actualFrames = videoCounter.get();
        assertTrue("expected: " + expectedFrames + ", actual: " + actualFrames,
                expectedFrames == actualFrames || expectedFrames + 1 == actualFrames);
        // total number of samples can differ in output file, assert the difference is less then 1%
        assertEquals(1., 1. * (sampleRate * duration) / sampleCounter.get(), 0.01);
    }


    @Test
    public void produceAndConsumeWithAlphaChannel() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        Path output = tempDir.resolve("output.mkv");

        int width = 480;
        int height = 480;
        int duration = 20;
        int sampleRate = 44100;
        int fps = 10;

        assertEquals(0, sampleRate % fps);

        BufferedImage redCross = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = redCross.createGraphics();
        graphics.setPaint(Color.RED);
        graphics.fillRect(width / 3, height / 4, width / 3, height / 2);
        graphics.fillRect(width / 4, height / 3, width / 2, height / 3);

        int[] samples = new int[sampleRate / fps];

        FrameProducer frameProducer = new FrameProducer() {
            private int videoPts = 0;
            private int audioPts = 0;

            @Override
            public List<Stream> produceStreams() {
                return Arrays.asList(
                        new Stream()
                                .setId(0)
                                .setType(Stream.Type.VIDEO)
                                .setWidth(width)
                                .setHeight(height)
                                .setTimebase((long) fps),
                        new Stream()
                                .setId(1)
                                .setType(Stream.Type.AUDIO)
                                .setSampleRate(sampleRate)
                                .setChannels(1)
                                .setTimebase((long) fps)
                );
            }

            @Override
            public Frame produce() {
                if (videoPts > fps * duration) {
                    return null;
                }

                if (videoPts <= audioPts) {
                    return Frame.createVideoFrame(0, videoPts++, redCross);
                }

                return Frame.createAudioFrame(1, audioPts++, samples);
            }
        };

        FFmpeg.atPath()
                .addInput(FrameInput
                        .withProducerAlpha(frameProducer)
                        .setFrameRate(fps)
                )
                .addOutput(UrlOutput
                        .toPath(output)
                        .setCodec(StreamType.VIDEO, "ffv1")
                )
                .execute();

        AtomicInteger videoCounter = new AtomicInteger();
        AtomicInteger sampleCounter = new AtomicInteger();

        FFmpeg.atPath()
                .addInput(UrlInput.fromPath(output))
                .addOutput(FrameOutput.withConsumerAlpha(new FrameConsumer() {
                    @Override
                    public void consumeStreams(List<Stream> streams) {
                        assertEquals(2, streams.size());

                        Stream video = streams.get(0);
                        assertEquals(0, video.getId());
                        assertEquals(Stream.Type.VIDEO, video.getType());
                        assertEquals((Integer) width, video.getWidth());
                        assertEquals((Integer) height, video.getHeight());

                        Stream audio = streams.get(1);
                        assertEquals(1, audio.getId());
                        assertEquals(Stream.Type.AUDIO, audio.getType());
                        assertEquals(Long.valueOf(sampleRate), audio.getSampleRate());
                        assertEquals((Integer) 1, audio.getChannels());
                    }

                    @Override
                    public void consume(Frame frame) {
                        if (frame == null) {
                            return;
                        }

                        if (frame.getStreamId() == 0) {
                            BufferedImage image = frame.getImage();
                            assertEquals(BufferedImage.TYPE_4BYTE_ABGR, image.getType());

                            assertEquals(0, image.getRGB(0, 0));
                            assertEquals(Color.RED.getRGB(), image.getRGB(width / 2, height / 2));

                            // fully compare every 10 frames to speed up test
                            if (videoCounter.get() % 10 == 0) {
                                for (int x = 0; x < width; x++) {
                                    for (int y = 0; y < height; y++) {
                                        assertEquals("x: " + x + ", y: " + y,
                                                redCross.getRGB(x, y), image.getRGB(x, y));
                                    }
                                }
                            }

                            videoCounter.incrementAndGet();
                            return;
                        }

                        int[] samples = frame.getSamples();
                        assertNotNull(samples);
                        sampleCounter.addAndGet(samples.length);
                    }
                }))
                .execute();


        // total number of frames can differ from expected
        int expectedFrames = fps * duration;
        int actualFrames = videoCounter.get();
        assertTrue("expected: " + expectedFrames + ", actual: " + actualFrames,
                expectedFrames == actualFrames || expectedFrames + 1 == actualFrames);
        // total number of samples can differ in output file, assert the difference is less then 1%
        assertEquals(1., 1. * (sampleRate * duration) / sampleCounter.get(), 0.01);
    }

    @Test
    public void videoFramerateHighResolution() throws Exception {
        testNutGenerationAndConsumption(10, 25, 1000, 480, 240);
        testNutGenerationAndConsumption(10, 25, 1000, 1280, 720);
        testNutGenerationAndConsumption(10, 25, 1000, 2560, 1440);
    }

    private void testNutGenerationAndConsumption(final int duration, final int fps, final long timebase, final int width, final int height) throws Exception {
        assertEquals(0, timebase % fps);
        Path mp4Path = Files.createTempFile("highResolution", ".mp4");

        final AtomicReference<FFmpegProgress> progressRef = new AtomicReference<>();

        FFmpeg.atPath(BIN)
                .addInput(FrameInput.withProducer(
                        new FrameProducer() {
                            private BufferedImage image;
                            private long frame = 0;
                            private long lastSecond = -1;

                            @Override
                            public List<Stream> produceStreams() {
                                return Arrays.asList(
                                        new Stream()
                                                .setId(0)
                                                .setType(Stream.Type.VIDEO)
                                                .setWidth(width)
                                                .setHeight(height)
                                                .setTimebase(timebase)
                                );
                            }

                            @Override
                            public Frame produce() {
                                if (frame > duration * fps) {
                                    return null;
                                }

                                long currentSecond = frame / fps;
                                if (lastSecond != currentSecond) {
                                    image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                                    Graphics2D g2d = image.createGraphics();
                                    g2d.setPaint(Color.white);
                                    g2d.setFont(new Font("Serif", Font.BOLD, height * 4 / 5));
                                    String s = currentSecond + "";
                                    FontMetrics fm = g2d.getFontMetrics();
                                    int x = (image.getWidth() - fm.stringWidth(s)) / 2;
                                    int y = height * 3 / 4;//image.getHeight() - (image.getHeight() - fm.getHeight()) / 2;
                                    g2d.drawString(s, x, y);
                                    g2d.dispose();

                                    lastSecond = currentSecond;
                                }

                                Frame result = Frame.createVideoFrame(0, frame * timebase / fps, image);
                                frame++;

                                return result;
                            }
                        })
                        .setFrameRate(fps)
                )
                .addOutput(UrlOutput.toPath(mp4Path))
                .setOverwriteOutput(true)
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        progressRef.set(progress);
                    }
                })
                .execute();

        Assert.assertNotNull(progressRef.get());
        // +1 frame for EOF
        int expectedFrames = duration * fps;
        Assert.assertTrue("duration=" + duration + ", fps=" + fps + ", timebase=" + timebase
                        + ", width=" + width + ", height=" + height + ", frames=" + progressRef.get().getFrame(),
                expectedFrames == progressRef.get().getFrame() || expectedFrames + 1 == progressRef.get().getFrame());
    }
}
