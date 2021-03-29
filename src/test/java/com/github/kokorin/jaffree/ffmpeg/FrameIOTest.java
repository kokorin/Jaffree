package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.Artifacts;
import com.github.kokorin.jaffree.StackTraceMatcher;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
    public void dumpFrames() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        LOGGER.debug("Will write to " + tempDir);

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

                long n = frameCounter.incrementAndGet();
                String filename = String.format("frame%05d.png", n);
                try {
                    boolean written = ImageIO.write(frame.getImage(), "png", tempDir.resolve(filename).toFile());
                    Assert.assertTrue(written);
                    LOGGER.debug(filename);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        UrlInput.fromPath(Artifacts.VIDEO_MP4)
                                .setDuration(1, TimeUnit.SECONDS)
                )
                .addOutput(
                        FrameOutput.withConsumer(consumer)
                                .disableStream(StreamType.AUDIO)
                )
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, trackCounter.get());
        Assert.assertTrue(frameCounter.get() > 10);
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
        Assert.assertEquals(1, trackCounter.get());
        Assert.assertEquals(42L, frameCounter.get());
    }

    @Test
    @Ignore
    // TODO unstable test
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

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        FrameInput.withProducer(producer)
                )
                .addOutput(
                        new NullOutput()
                )
                .execute();

        Assert.assertNotNull(result);
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

        Assert.assertEquals(4, probe.getStreams().size());
    }

    @Test
    public void createGif() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        Path output = tempDir.resolve("test.gif");
        LOGGER.debug("Will write to " + tempDir);

        FrameProducer producer = new FrameProducer() {
            private final int frameCount = 30;
            private long frameCounter = 0;

            @Override
            public List<Stream> produceStreams() {
                return Collections.singletonList(new Stream()
                        .setId(0)
                        .setType(Stream.Type.VIDEO)
                        .setTimebase(1_000L)
                        .setWidth(320)
                        .setHeight(240)
                );
            }

            @Override
            public Frame produce() {
                if (frameCounter > frameCount) {
                    return null;
                }
                LOGGER.debug("Creating frame " + frameCounter);

                BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D graphics = image.createGraphics();
                graphics.setPaint(new Color(frameCounter * 1.0f / frameCount, 0, 0));
                graphics.fillRect(0, 0, 320, 240);
                frameCounter++;

                return new Frame(0, frameCounter * 1000 / 10, image);
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        FrameInput.withProducer(producer)
                )
                .addOutput(
                        UrlOutput.toPath(output)
                )
                .execute();

        Assert.assertNotNull(result);
    }

    @Test
    @Ignore("This test plays sound via javax.sound")
    public void readAndPlayAudio() throws Exception {
        final AtomicLong trackCounter = new AtomicLong();
        final AtomicLong frameCounter = new AtomicLong();

        FrameConsumer consumer = new FrameConsumer() {
            SourceDataLine line = null;
            List<Stream> tracks;

            @Override
            public void consumeStreams(List<Stream> tracks) {
                trackCounter.set(tracks.size());
                this.tracks = tracks;

                for (Stream track : tracks) {
                    if (line == null && track.getType() == Stream.Type.AUDIO) {
                        AudioFormat audioFormat = new AudioFormat(track.getSampleRate(), 8, track.getChannels(), true, false);
                        try {
                            line = AudioSystem.getSourceDataLine(audioFormat);
                            line.addLineListener(new LineListener() {
                                @Override
                                public void update(LineEvent event) {
                                    LOGGER.debug(event.toString());
                                }
                            });
                            line.open(audioFormat);
                            line.start();
                        } catch (LineUnavailableException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            @Override
            public void consume(Frame frame) {
                if (tracks.get(frame.getStreamId()).getType() == Stream.Type.AUDIO) {
                    frameCounter.incrementAndGet();

                    int[] samples = frame.getSamples();
                    byte[] bytes = new byte[samples.length];

                    int coeff = Integer.MAX_VALUE / Byte.MAX_VALUE;

                    for (int i = 0; i < samples.length; i++) {
                        bytes[i] = (byte) (samples[i] / coeff);
                    }
                    line.write(bytes, 0, bytes.length);
                }
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        UrlInput.fromPath(Artifacts.VIDEO_MP4)
                                .setDuration(3, TimeUnit.SECONDS)
                )
                .addOutput(
                        FrameOutput.withConsumer(consumer)
                                .disableStream(StreamType.VIDEO)
                )
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(2, trackCounter.get());
        Assert.assertTrue(frameCounter.get() > 10);
    }

    @Test
    public void createMp3() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        Path output = tempDir.resolve("test.mp3");
        LOGGER.debug("Will write to " + tempDir);

        FrameProducer producer = new FrameProducer() {
            private long frameCounter = 0;

            @Override
            public List<Stream> produceStreams() {
                return Collections.singletonList(new Stream()
                        .setType(Stream.Type.AUDIO)
                        .setTimebase(44100L)
                        .setSampleRate(44100)
                        .setChannels(1)
                );
            }

            @Override
            public Frame produce() {
                if (frameCounter > 30) {
                    return null;
                }


                long timestamp = frameCounter * 1000 / 10;
                int[] samples = new int[4410];
                for (int i = 0; i < samples.length; i++) {
                    samples[i] = (int) (Integer.MAX_VALUE * Math.sin(300. * (timestamp + i * 100. / samples.length)));
                }
                frameCounter++;

                return new Frame(0, frameCounter * 4410, samples);
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        FrameInput.withProducer(producer)
                )
                .addOutput(
                        UrlOutput.toPath(output)
                )
                .execute();

        Assert.assertNotNull(result);
    }

    @Test
    public void writeAndRead() throws Exception {
        int sampleRate = 44100;
        int samplesPerFrame = 4410;
        final Stream track = new Stream()
                .setId(0)
                .setType(Stream.Type.AUDIO)
                .setSampleRate(sampleRate)
                .setTimebase((long) sampleRate)
                .setChannels(1);
        final List<Frame> frames = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 10; i++) {
            Frame frame = new Frame(track.getId(), i * samplesPerFrame, new int[samplesPerFrame]);
            frames.add(frame);
        }

        FrameProducer producer = new FrameProducer() {
            Iterator<Frame> frameIterator = frames.iterator();

            @Override
            public List<Stream> produceStreams() {
                return Collections.singletonList(track);
            }

            @Override
            public Frame produce() {
                if (frameIterator.hasNext()) {
                    return frameIterator.next();
                }
                return null;
            }
        };

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        NutFrameWriter writer = new NutFrameWriter(producer, false, null);
        writer.write(buffer);

        final List<Stream> actualTracks = new CopyOnWriteArrayList<>();
        final List<Frame> actualFrames = new CopyOnWriteArrayList<>();
        FrameConsumer consumer = new FrameConsumer() {
            @Override
            public void consumeStreams(List<Stream> tracks) {
                actualTracks.addAll(tracks);
            }

            @Override
            public void consume(Frame frame) {
                if (frame != null) {
                    actualFrames.add(frame);
                }
            }
        };

        ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray());
        NutFrameReader reader = new NutFrameReader(consumer, false);
        reader.read(input);

        Assert.assertEquals(1, actualTracks.size());
        Assert.assertEquals(track.getId(), actualTracks.get(0).getId());
        Assert.assertEquals(track.getType(), actualTracks.get(0).getType());
        Assert.assertEquals(track.getTimebase(), actualTracks.get(0).getTimebase());
        Assert.assertEquals(track.getSampleRate(), actualTracks.get(0).getSampleRate());
        Assert.assertEquals(track.getChannels(), actualTracks.get(0).getChannels());

        Assert.assertEquals(frames.size(), actualFrames.size());
        for (int i = 0; i < frames.size(); i++) {
            Frame frame = frames.get(i);
            Frame actualFrame = actualFrames.get(i);

            Assert.assertEquals(frame.getStreamId(), actualFrame.getStreamId());
            Assert.assertArrayEquals(frame.getSamples(), actualFrame.getSamples());
            // TODO: compare images
        }
    }

    public void testNutGenerationAndConsumption(final int duration, final int fps, final long timebase, final int width, final int height) throws Exception {
        Assert.assertEquals(0, timebase % fps);
        Path mp4Path = Files.createTempFile("highResolution", ".mp4");

        final AtomicReference<FFmpegProgress> progressRef = new AtomicReference<>();

        // TODO: convert outputPath to MP4 with ffmpeg and check how long will it take
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

                                Frame result = new Frame(0, frame * timebase / fps, image);
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

    @Test
    public void videoFramerateHighResolution() throws Exception {
        testNutGenerationAndConsumption(10, 25, 1000, 480, 240);
        testNutGenerationAndConsumption(10, 25, 1000, 1280, 720);
        testNutGenerationAndConsumption(10, 25, 1000, 2560, 1440);
    }

}
