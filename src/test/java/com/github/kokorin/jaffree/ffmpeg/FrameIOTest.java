package com.github.kokorin.jaffree.ffmpeg;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FrameIOTest {
    public static Path BIN;
    public static Path SAMPLES = Paths.get("target/samples");
    public static Path VIDEO_MP4 = SAMPLES.resolve("MPEG-4/video.mp4");

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

    @Test
    public void dumpFrames() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        System.out.println("Will write to " + tempDir);

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
                    System.out.println(filename);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        UrlInput.fromPath(VIDEO_MP4)
                                .setDuration(1, TimeUnit.SECONDS)
                )
                .addOutput(
                        FrameOutput.withConsumer(consumer)
                                .extractVideo(true)
                                .extractAudio(false)
                )
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, trackCounter.get());
        Assert.assertTrue(frameCounter.get() > 10);
    }

    @Test
    public void createGif() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        Path output = tempDir.resolve("test.gif");
        System.out.println("Will write to " + tempDir);

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
                System.out.println("Creating frame " + frameCounter);

                BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D graphics = image.createGraphics();
                graphics.setPaint(new Color(frameCounter * 1.0f / frameCount, 0, 0));
                graphics.fillRect(0, 0, 320, 240);
                frameCounter++;

                return new Frame()
                        .setStreamId(0)
                        .setImage(image)
                        .setPts(frameCounter * 1000 / 10);
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
                                    System.out.println(event);
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
                        UrlInput.fromPath(VIDEO_MP4)
                                .setDuration(3, TimeUnit.SECONDS)
                )
                .addOutput(
                        FrameOutput.withConsumer(consumer)
                                .extractVideo(false)
                                .extractAudio(true)
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
        System.out.println("Will write to " + tempDir);

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
                    samples[i] = (int) (Integer.MAX_VALUE * Math.sin(300. * (timestamp + i * 100 / samples.length)));
                }
                frameCounter++;

                return new Frame()
                        .setStreamId(0)
                        .setPts(frameCounter * 4410)
                        .setSamples(samples);
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
    public void writeAndRead() {
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
            Frame frame = new Frame()
                    .setStreamId(track.getId())
                    .setPts(i * samplesPerFrame)
                    .setSamples(new int[samplesPerFrame]);
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

        NutFrameWriter writer = new NutFrameWriter(producer, false, 10_000);
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
        NutFrameReader reader = new NutFrameReader(consumer, false, 10_000);
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
            // TODO compare images
        }
    }
}
