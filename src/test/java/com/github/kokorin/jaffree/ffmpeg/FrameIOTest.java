package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.Option;
import com.github.kokorin.jaffree.StreamSpecifier;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import com.github.kokorin.jaffree.matroska.ExtraDocTypes;
import com.github.kokorin.jaffree.process.StdReader;
import org.apache.commons.io.IOUtils;
import org.ebml.io.FileDataSource;
import org.ebml.matroska.MatroskaFile;
import org.ebml.matroska.MatroskaFileFrame;
import org.ebml.matroska.MatroskaFileTrack;
import org.ebml.matroska.MatroskaFileWriter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FrameIOTest {
    public static Path BIN;
    public static Path SAMPLES = Paths.get("target/samples");
    public static Path VIDEO_MP4 = SAMPLES.resolve("MPEG-4/video.mp4");
    public static Path VIDEO_MKV = SAMPLES.resolve("Matroska/atlantis405-test.mkv");

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
    public void testReadCompressed() throws Exception {
        MatroskaFile mkvFile = new MatroskaFile(new FileDataSource(VIDEO_MKV.toString()));
        mkvFile.readFile();

        MatroskaFileTrack[] fileTracks = mkvFile.getTrackList();
        Assert.assertNotNull(fileTracks);
        Assert.assertEquals(2, fileTracks.length);
    }


    @Test
    public void testJebml() throws Exception {
        try (FileInputStream inputStream = new FileInputStream(VIDEO_MKV.toFile())) {
            MatroskaFile mkvStream = new MatroskaFile(new InputStreamSource(inputStream));
            mkvStream.readFile();

            MatroskaFileTrack[] streamTracks = mkvStream.getTrackList();
            Assert.assertNotNull(streamTracks);
            Assert.assertEquals(2, streamTracks.length);
        }
    }

    @Test
    public void testJebmlDemuxAndMux() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        final Path output = tempDir.resolve("test.mkv");
        System.out.println(output);

        try (OutputStream out = new FileOutputStream(output.toFile())) {
            MatroskaFile mkvReader = new MatroskaFile(new FileDataSource(VIDEO_MKV.toString()));
            mkvReader.readFile();

            MatroskaFileWriter mkvWrtier = new MatroskaFileWriter(new OutputStreamWriter(out));

            for (MatroskaFileTrack track : mkvReader.getTrackList()) {
                MatroskaFileTrack write = new MatroskaFileTrack();
                write.setName(track.getName());
                write.setCodecID(track.getCodecID());
                write.setTrackNo(track.getTrackNo());
                write.setVideo(track.getVideo());
                write.setAudio(track.getAudio());
                write.setTrackType(track.getTrackType());

                mkvWrtier.addTrack(track);
            }

            for (MatroskaFileFrame frame = mkvReader.getNextFrame(); frame != null; frame = mkvReader.getNextFrame()) {
                MatroskaFileFrame write = new MatroskaFileFrame(frame);
                write.setTrackNo(write.getTrackNo());
                mkvWrtier.addFrame(write);
            }

            mkvWrtier.close();
        }

        Assert.assertTrue(Files.exists(output));

        FFprobeResult probe = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setShowError(true)
                .setInputPath(output)
                .execute();

        Assert.assertNotNull(probe);
        Assert.assertEquals(2, probe.getStreams().getStream().size());
        Stream stream1 = probe.getStreams().getStream().get(0);
        Stream stream2 = probe.getStreams().getStream().get(1);

        Assert.assertEquals("audio", stream1.getCodecType());
        Assert.assertEquals("video", stream2.getCodecType());
    }

    @Test
    @Ignore("It seems that std output differs from file output")
    public void testStdOutTheSameAsFileOut() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        final Path output = tempDir.resolve("test.mkv");

        FFmpegResult resultFile = FFmpeg.atPath(BIN)
                .addInput(
                        UrlInput.fromPath(VIDEO_MP4)
                                .setDuration(5, TimeUnit.SECONDS)
                )
                .addOutput(
                        UrlOutput.toPath(output)
                                .addCodec(StreamSpecifier.withType(StreamType.ALL_VIDEO), "rawvideo")
                                .addOption("-pix_fmt", "yuv420p")
                                .addOption("-an")
                )
                .execute();

        Assert.assertNotNull(resultFile);

        final StdReader<FFmpegResult> stdReader = new StdReader<FFmpegResult>() {
            @Override
            public FFmpegResult read(InputStream stdOut) {
                try (InputStream fileStream = new FileInputStream(output.toFile());) {
                    boolean equals = IOUtils.contentEquals(fileStream, stdOut);
                    if (!equals) {
                        throw new RuntimeException("File output isn't the same as std");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        Output compareOutput = new Output() {
            @Override
            public void beforeExecute(FFmpeg ffmpeg) {
                ffmpeg.setStdOutReader(stdReader);
            }

            @Override
            public List<Option> buildOptions() {
                return Arrays.asList(
                        new Option("-f", "matroska"),
                        new Option("-vcodec", "rawvideo"),
                        new Option("-pix_fmt", "yuv420p"),
                        new Option("-an"),
                        new Option("-")
                );
            }
        };

        FFmpegResult resultStdOut = FFmpeg.atPath(BIN)
                .addInput(
                        UrlInput.fromPath(VIDEO_MP4)
                                .setDuration(5, TimeUnit.SECONDS)
                )
                .addOutput(compareOutput)
                .execute();
    }

    @Test
    public void testReadUncompressedStreamDumpedToDisk() throws Exception {
        ExtraDocTypes.init();
        Path tempDir = Files.createTempDirectory("jaffree");
        final Path output = tempDir.resolve("test.mkv");
        System.out.println("Will write to " + output);

        final StdReader<FFmpegResult> stdToDiskReader = new StdReader<FFmpegResult>() {
            @Override
            public FFmpegResult read(InputStream stdOut) {
                try (OutputStream fileStream = new FileOutputStream(output.toFile())) {
                    IOUtils.copy(stdOut, fileStream);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to write output to disk", e);
                }
                return null;
            }
        };

        Output compareOutput = new Output() {
            @Override
            public void beforeExecute(FFmpeg ffmpeg) {
                ffmpeg.setStdOutReader(stdToDiskReader);
            }

            @Override
            public List<Option> buildOptions() {
                return Arrays.asList(
                        new Option("-f", "matroska"),
                        new Option("-vcodec", "rawvideo"),
                        new Option("-pix_fmt", "yuv420p"),
                        new Option("-acodec", "pcm_s32be"),
                        new Option("-")
                );
            }
        };

        FFmpegResult resultStdOut = FFmpeg.atPath(BIN)
                .addInput(
                        UrlInput.fromPath(VIDEO_MP4)
                                .setDuration(5, TimeUnit.SECONDS)
                )
                .addOutput(compareOutput)
                .execute();

        Assert.assertNotNull(resultStdOut);
        Assert.assertTrue(Files.exists(output));

        //Use JEBML with FileDataSource
        MatroskaFile mkvFile = new MatroskaFile(new FileDataSource(output.toString()));
        mkvFile.readFile();

        MatroskaFileTrack[] tracks = mkvFile.getTrackList();
        Assert.assertNotNull(tracks);
        Assert.assertEquals(2, tracks.length);

        //Use JEBML with InputStreamSource
        try (FileInputStream inputStream = new FileInputStream(output.toFile())) {
            MatroskaFile mkvStream = new MatroskaFile(new InputStreamSource(inputStream));
            mkvStream.readFile();

            MatroskaFileTrack[] streamTracks = mkvStream.getTrackList();
            Assert.assertNotNull(streamTracks);
            Assert.assertEquals(2, streamTracks.length);
        }
    }

    @Test
    public void testReadUncompressedMkvFromStdOut() throws Exception {
        final AtomicLong trackCounter = new AtomicLong();
        final AtomicLong frameCounter = new AtomicLong();
        FrameConsumer consumer = new FrameConsumer() {
            @Override
            public void consumeTracks(List<Track> tracks) {
                trackCounter.set(tracks.size());
            }

            @Override
            public void consume(Frame frame) {
                frameCounter.incrementAndGet();
            }
        };

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        UrlInput.fromPath(VIDEO_MP4)
                                .setDuration(5, TimeUnit.SECONDS)
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
    public void testReadUncompressedMkvFromStdOutAndSaveFrames() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        System.out.println("Will write to " + tempDir);

        final AtomicLong trackCounter = new AtomicLong();
        final AtomicLong frameCounter = new AtomicLong();
        FrameConsumer consumer = new FrameConsumer() {
            @Override
            public void consumeTracks(List<Track> tracks) {
                trackCounter.set(tracks.size());
            }

            @Override
            public void consume(Frame frame) {
                long n = frameCounter.incrementAndGet();
                if (frame instanceof VideoFrame) {
                    VideoFrame videoFrame = (VideoFrame) frame;
                    String filename = String.format("frame%05d.png", n);
                    try {
                        boolean written = ImageIO.write(videoFrame.getImage(), "png", tempDir.resolve(filename).toFile());
                        Assert.assertTrue(written);
                        System.out.println(filename);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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
    public void testWriteUncompressedMkvToDiskAndCompareColorSpace() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        Path expected = tempDir.resolve("expected.mkv");
        Path actual = tempDir.resolve("actual.mkv");

        System.out.println("Will write to " + tempDir);

        FrameProducer producer = new FrameProducer() {
            private long frameCounter = 0;

            @Override
            public List<Track> produceTracks() {
                return Collections.singletonList(new Track()
                        .setType(Track.Type.VIDEO)
                        .setWidth(320)
                        .setHeight(240)
                );
            }

            @Override
            public Frame produce() {
                if (frameCounter > 30) {
                    return null;
                }
                System.out.println("Creating frame " + frameCounter);

                VideoFrame frame = new VideoFrame();

                BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = image.createGraphics();
                graphics.setPaint(new Color(frameCounter * 1.0f / 30, 0, 0));
                graphics.fillRect(0, 0, 320, 240);

                frame.setImage(image);
                frame.setTimecode(frameCounter * 1000 / 10);
                frameCounter++;

                return frame;
            }
        };

        try (FileOutputStream outputStream = new FileOutputStream(actual.toFile())) {
            new FrameWriter(producer).write(outputStream);
        }

        Assert.assertTrue(Files.exists(actual));


        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        UrlInput.fromPath(VIDEO_MP4)
                                .setDuration(5, TimeUnit.SECONDS)
                )
                .addOutput(
                        UrlOutput.toPath(expected)
                                .addCodec(StreamSpecifier.withType(StreamType.ALL_VIDEO), "rawvideo")
                                .addOption("-pix_fmt", "yuv420p")
                                .addOption("-an")
                )
                .execute();

        Assert.assertNotNull(result);

        FFprobeResult expectedProbe = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setShowError(true)
                .setInputPath(expected)
                .execute();

        Assert.assertNotNull(expectedProbe);
        Stream expectedStream = expectedProbe.getStreams().getStream().get(0);

        //Use JEBML with FileDataSource
        MatroskaFile mkvFile = new MatroskaFile(new FileDataSource(expected.toString()));
        mkvFile.readFile();

        FFprobeResult actualProbe = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setShowError(true)
                .setInputPath(actual)
                .execute();

        Assert.assertNotNull(actualProbe);
        Stream actualStream = actualProbe.getStreams().getStream().get(0);

        Assert.assertEquals(expectedStream.getColorSpace(), actualStream.getColorSpace());

    }

    @Test
    public void testWriteUncompressedMkvToStdInAndSaveGif() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        Path output = tempDir.resolve("test.gif");
        System.out.println("Will write to " + tempDir);

        FrameProducer producer = new FrameProducer() {
            private long frameCounter = 0;

            @Override
            public List<Track> produceTracks() {
                return Collections.singletonList(new Track()
                        .setType(Track.Type.VIDEO)
                        .setWidth(320)
                        .setHeight(240)
                );
            }

            @Override
            public Frame produce() {
                if (frameCounter > 30) {
                    return null;
                }
                System.out.println("Creating frame " + frameCounter);

                VideoFrame frame = new VideoFrame();

                BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = image.createGraphics();
                graphics.setPaint(new Color(frameCounter * 1.0f / 30, 0, 0));
                graphics.fillRect(0, 0, 320, 240);

                frame.setImage(image);
                frame.setTimecode(frameCounter * 1000 / 10);
                frameCounter++;

                return frame;
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
    public void testReadAudioSamples() throws Exception {
        final AtomicLong trackCounter = new AtomicLong();
        final AtomicLong frameCounter = new AtomicLong();

        FrameConsumer consumer = new FrameConsumer() {
            SourceDataLine line = null;

            @Override
            public void consumeTracks(List<Track> tracks) {
                trackCounter.set(tracks.size());
                for (Track track : tracks) {
                    if (line == null && track.getType() == Track.Type.AUDIO) {
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
                if (frame instanceof AudioFrame) {
                    frameCounter.incrementAndGet();

                    AudioFrame audioFrame = (AudioFrame) frame;
                    //audioFrame.getDuration();
                    int[] samples = audioFrame.getSamples();
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
                                .extractVideo(true)
                                .extractAudio(true)
                )
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(2, trackCounter.get());
        Assert.assertTrue(frameCounter.get() > 10);
    }

    @Test
    public void testWriteAudioSamples() throws Exception {
        final Path tempDir = Files.createTempDirectory("jaffree");
        Path output = tempDir.resolve("test.mp3");
        System.out.println("Will write to " + tempDir);

        FrameProducer producer = new FrameProducer() {
            private long frameCounter = 0;

            @Override
            public List<Track> produceTracks() {
                return Collections.singletonList(new Track()
                        .setType(Track.Type.AUDIO)
                        .setSampleRate(44100)
                        .setChannels(1)
                );
            }

            @Override
            public Frame produce() {
                if (frameCounter > 30) {
                    return null;
                }
                System.out.println("Creating frame " + frameCounter);

                AudioFrame frame = new AudioFrame();

                long timecode = frameCounter * 1000 / 10;
                frame.setTimecode(timecode);
                int[] samples = new int[4410];
                for (int i = 0; i < samples.length; i++) {
                    samples[i] = (int)(Integer.MAX_VALUE * Math.sin(300. * (timecode + i * 100 / samples.length)));
                }
                frame.setSamples(samples);
                frame.setDuration(100);
                frameCounter++;

                return frame;
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
}
