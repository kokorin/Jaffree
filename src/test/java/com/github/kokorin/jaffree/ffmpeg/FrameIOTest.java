package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.Option;
import com.github.kokorin.jaffree.StreamSpecifier;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.matroska.ExtraDocTypes;
import com.github.kokorin.jaffree.process.StdReader;
import org.apache.commons.io.IOUtils;
import org.ebml.io.FileDataSource;
import org.ebml.matroska.MatroskaFile;
import org.ebml.matroska.MatroskaFileTrack;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FrameIOTest {
    public static Path BIN;
    public static Path SAMPLES = Paths.get("target/samples");
    public static Path VIDEO_MP4 = SAMPLES.resolve("MPEG-4/video.mp4");
    public static Path VIDEO_MKV = SAMPLES.resolve("Matroska/atlantis405-test.mkv");

    static {
        ExtraDocTypes.init();
    }


    @BeforeClass
    public static void setUp() throws Exception {
        String ffmpegHome = System.getProperty("FFMPEG_BIN");
        if (ffmpegHome == null) {
            ffmpegHome = System.getenv("FFMPEG_BIN");
        }
        Assert.assertNotNull("Nor command line property, neither system variable FFMPEG_BIN is set up", ffmpegHome);
        BIN = Paths.get(ffmpegHome);

        Assert.assertTrue("Sample videos weren't found: " + SAMPLES.toAbsolutePath(), Files.exists(SAMPLES));

        ExtraDocTypes.init();
    }

    @Test
    public void testReadCompressed() throws Exception {
        MatroskaFile mkvFile = new MatroskaFile(new FileDataSource(VIDEO_MKV.toString()));
        mkvFile.readFile();

        MatroskaFileTrack[] tracks = mkvFile.getTrackList();
        Assert.assertNotNull(tracks);
        Assert.assertEquals(2, tracks.length);
    }

    @Test
    public void testReadCompressed2() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("test.mkv");

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(
                        UrlInput.fromPath(VIDEO_MP4)
                                .setDuration(1, TimeUnit.SECONDS)
                )
                .addOutput(
                        UrlOutput.toPath(outputPath)
                                .addCodec(StreamSpecifier.withType(StreamType.ALL_VIDEO), "h264")
                                .addCodec(StreamSpecifier.withType(StreamType.AUDIO), "ac3")
                )
                .execute();

        Assert.assertNotNull(result);

        MatroskaFile mkvFile = new MatroskaFile(new FileDataSource(outputPath.toString()));
        mkvFile.readFile();

        MatroskaFileTrack[] tracks = mkvFile.getTrackList();
        Assert.assertNotNull(tracks);
        Assert.assertEquals(2, tracks.length);
    }

    @Test
    @Ignore
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
                //ffmpeg.setStdErrReader(new LoggingStdReader<FFmpegResult>());
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
    public void testReadUncompressed() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");

        final AtomicLong frameCounter = new AtomicLong();
        FrameConsumer consumer = new FrameConsumer() {
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
                )
                .execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(frameCounter.get() > 0);
    }
}
