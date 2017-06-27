package com.github.kokorin.jaffree;

import com.github.kokorin.jaffree.result.FFmpegResult;
import com.github.kokorin.jaffree.result.FFprobeResult;
import com.github.kokorin.jaffree.result.Stream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FFmpegTest {
    public static Path BIN;
    public static Path SAMPLES = Paths.get("target/samples");
    public static Path VIDEO_MP4 = SAMPLES.resolve("MPEG-4/video.mp4");
    public static Path SMALL_FLV = SAMPLES.resolve("FLV/zelda.flv");
    public static Path SMALL_MP4 = SAMPLES.resolve("MPEG-4/turn-on-off.mp4");
    public static Path ERROR_MP4 = SAMPLES.resolve("non_existent.mp4");
    public static Path TRANSPORT_VOB = SAMPLES.resolve("MPEG-VOB/transport-stream/capture.neimeng");

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
    public void testSimpleCopy() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addOutput(new Output()
                        .setUrl(outputPath.toString())
                        .addCodec(null, "copy"))
                .execute();

        Assert.assertNotNull(result);
    }

    @Test
    public void testOutputAdditionalOption() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("test.mp3");

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addOutput(new Output()
                        .setUrl(outputPath.toString())
                        .addCodec(StreamSpecifier.withType(StreamType.AUDIO), "mp3")
                        .addOption(new Option("-vn"))
                        .addOption(new Option("-id3v2_version", "3"))
                )
                .execute();

        Assert.assertNotNull(result);

        FFprobeResult probe = FFprobe.atPath(BIN)
                .setInputPath(outputPath)
                .setShowStreams(true)
                .setShowError(true)
                .execute();

        Assert.assertNotNull(probe);
        Assert.assertEquals(1, probe.getStreams().getStream().size());
        Assert.assertEquals("audio", probe.getStreams().getStream().get(0).getCodecType());
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
                .addOutput(new Output().setUrl(outputPath.toString()))
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
                .addOutput(new Output().setUrl(outputPath.toString()))
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
                .addOutput(new Output()
                        .setUrl(outputPath.toString())
                        .addCodec(null, "copy"))
                .execute();

        Assert.assertNotNull(result);

        double outputDuration = getDuration(outputPath);
        Assert.assertEquals(10.0, outputDuration, 0.1);
    }

    @Test
    public void testOutputPosition() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addOutput(new Output()
                        .setUrl(outputPath.toString())
                        .addCodec(null, "copy")
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
                .addOutput(new Output()
                        .setUrl(outputPath.toString())
                        .addCodec(null, "copy")
                        .setSizeLimit(1, SizeUnit.MB)
                )
                .execute();

        Assert.assertNotNull(result);

        long outputSize = Files.size(outputPath);
        Assert.assertTrue(outputSize > 900_000 * 8);
        Assert.assertTrue(outputSize < 1_100_000 * 8);
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
                .addOutput(new Output()
                        .setUrl(outputPath.toString())
                        .addCodec(null, "copy"))
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
                .addOutput(new Output()
                        .setUrl(outputPath.toString())
                        .addCodec(null, "copy"))
                .execute();

        Assert.assertNotNull(result);

        double outputDuration = getDuration(outputPath);

        Assert.assertEquals(7.0, outputDuration, 0.5);
    }

    @Test
    public void testMap() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve(VIDEO_MP4.getFileName());

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(UrlInput.fromPath(VIDEO_MP4))
                .addOutput(new Output()
                        .setUrl(outputPath.toString())
                        .addCodec(null, "copy")
                        .addMap(0, StreamSpecifier.withType(StreamType.AUDIO))
                        .addMap(0, StreamSpecifier.withType(StreamType.AUDIO))
                        .addMap(0, StreamSpecifier.withType(StreamType.ALL_VIDEO))
                )
                .execute();

        Assert.assertNotNull(result);

        FFprobeResult probe = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setShowError(true)
                .setInputPath(outputPath)
                .execute();
        Assert.assertNull(probe.getError());

        List<Stream> streamTypes = probe.getStreams().getStream();

        Assert.assertEquals(3, streamTypes.size());
        Assert.assertEquals("audio", streamTypes.get(0).getCodecType());
        Assert.assertEquals("audio", streamTypes.get(1).getCodecType());
        Assert.assertEquals("video", streamTypes.get(2).getCodecType());
    }

    private static double getDuration(Path path) {
        FFprobeResult probe = FFprobe.atPath(BIN)
                .setShowStreams(true)
                .setShowError(true)
                .setInputPath(path)
                .execute();
        Assert.assertNull(probe.getError());

        double result = 0.0;
        for (Stream stream : probe.getStreams().getStream()) {
            result = Math.max(result, stream.getDuration());
        }

        return result;
    }
}
