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
import java.util.concurrent.TimeUnit;

public class FFmpegFilterTest {

    public static Path BIN;
    public static Path SAMPLES = Paths.get("target/samples");
    public static Path VIDEO_MP4 = SAMPLES.resolve("MPEG-4/video.mp4");
    public static Path VIDEO2_MP4 = SAMPLES.resolve("MPEG-4/video2.mp4");
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

    /**
     * Test, that creates mosaic video from 4 sources
     * <p>
     * Note, that this example lacks audio filter.
     *
     * @throws Exception
     * @see <a href="https://trac.ffmpeg.org/wiki/Create%20a%20mosaic%20out%20of%20several%20input%20videos">mosaic</a>
     */
    @Test
    public void testMosaic() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("mosaic.mkv");

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(Input.fromPath(VIDEO_MP4).setDuration(10, TimeUnit.SECONDS))
                .addInput(Input.fromPath(SMALL_FLV).setDuration(10, TimeUnit.SECONDS))
                .addInput(Input.fromPath(SMALL_MP4).setDuration(10, TimeUnit.SECONDS))
                .addInput(Input.fromPath(VIDEO2_MP4).setDuration(10, TimeUnit.SECONDS))

                .setComplexFilter(FilterGraph.of(
                        FilterChain.of(
                                Filter.withName("nullsrc")
                                        .addArgument("size", "640x480")
                                        .addOutputLink("base")
                        ),
                        FilterChain.of(
                                Filter.fromInputLink(StreamSpecifier.withInputIndexAndType(0, StreamType.ALL_VIDEO))
                                        .setName("setpts")
                                        .addArgument("PTS-STARTPTS"),
                                Filter.withName("scale")
                                        .addArgument("320x240")
                                        .addOutputLink("upperleft")
                        ),
                        FilterChain.of(
                                Filter.fromInputLink(StreamSpecifier.withInputIndexAndType(1, StreamType.ALL_VIDEO))
                                        .setName("setpts")
                                        .addArgument("PTS-STARTPTS"),
                                Filter.withName("scale")
                                        .addArgument("320x240")
                                        .addOutputLink("upperright")
                        ),
                        FilterChain.of(
                                Filter.fromInputLink(StreamSpecifier.withInputIndexAndType(2, StreamType.ALL_VIDEO))
                                        .setName("setpts")
                                        .addArgument("PTS-STARTPTS"),
                                Filter.withName("scale")
                                        .addArgument("320x240")
                                        .addOutputLink("lowerleft")
                        ),
                        FilterChain.of(
                                Filter.fromInputLink(StreamSpecifier.withInputIndexAndType(3, StreamType.ALL_VIDEO))
                                        .setName("setpts")
                                        .addArgument("PTS-STARTPTS"),
                                Filter.withName("scale")
                                        .addArgument("320x240")
                                        .addOutputLink("lowerright")
                        ),
                        FilterChain.of(
                                Filter.fromInputLink("base")
                                        .addInputLink("upperleft")
                                        .setName("overlay")
                                        .addArgument("shortest", "1")
                                        .addOutputLink("tmp1")
                        ),
                        FilterChain.of(
                                Filter.fromInputLink("tmp1")
                                        .addInputLink("upperright")
                                        .setName("overlay")
                                        //.addArgument("shortest", "1")
                                        .addArgument("x", "320")
                                        .addOutputLink("tmp2")
                        ),
                        FilterChain.of(
                                Filter.fromInputLink("tmp2")
                                        .addInputLink("lowerleft")
                                        .setName("overlay")
                                        //.addArgument("shortest", "1")
                                        .addArgument("y", "240")
                                        .addOutputLink("tmp3")
                        ),
                        FilterChain.of(
                                Filter.fromInputLink("tmp3")
                                        .addInputLink("lowerright")
                                        .setName("overlay")
                                        //.addArgument("shortest", "1")
                                        .addArgument("x", "320")
                                        .addArgument("y", "240")
                        )
                ))

                .addOutput(Output.toPath(outputPath))
                .execute();

        Assert.assertNotNull(result);

        FFprobeResult probe = FFprobe.atPath(BIN)
                .setInputPath(outputPath)
                .setShowStreams(true)
                .setShowError(true)
                .execute();

        Assert.assertNotNull(probe);
        Assert.assertNull(probe.getError());

        int width = 0;
        int height = 0;

        for (Stream stream : probe.getStreams().getStream()) {
            if (stream.getWidth() != null) {
                width = Math.max(width, stream.getWidth());
            }
            if (stream.getHeight() != null) {
                height = Math.max(height, stream.getHeight());
            }
        }

        Assert.assertEquals(640, width);
        Assert.assertEquals(480, height);

    }


    /**
     * Concatenates 2 video with reencoding
     *
     * @throws Exception
     * @see <a href="Concatenate">Concatenate</a>
     */
    @Test
    public void testConcatWithReencode() throws Exception {
        Path tempDir = Files.createTempDirectory("jaffree");
        Path outputPath = tempDir.resolve("concat.mp4");

        FFmpegResult result = FFmpeg.atPath(BIN)
                .addInput(Input.fromPath(VIDEO_MP4).setDuration(5, TimeUnit.SECONDS))
                .addInput(Input.fromPath(VIDEO2_MP4).setPositionEof(-5, TimeUnit.SECONDS))

                .setComplexFilter(FilterGraph.of(
                        FilterChain.of(
                                Filter.fromInputLink(StreamSpecifier.withInputIndexAndType(0, StreamType.ALL_VIDEO))
                                        .addInputLink(StreamSpecifier.withInputIndexAndType(0, StreamType.AUDIO))
                                        .addInputLink(StreamSpecifier.withInputIndexAndType(1, StreamType.ALL_VIDEO))
                                        .addInputLink(StreamSpecifier.withInputIndexAndType(1, StreamType.AUDIO))
                                        .setName("concat")
                                        .addArgument("n", "2")
                                        .addArgument("v", "1")
                                        .addOutputLink("v")
                                        .addArgument("a", "1")
                                        .addOutputLink("a")
                        )
                ))

                //On ubuntu ffmpeg uses AAC encoder in experimental mode, this option allows using AAC
                .addOption(new Option("-strict", "-2"))

                .addOutput(Output.toPath(outputPath)
                        .addMap("v")
                        .addMap("a")
                )
                .execute();

        Assert.assertNotNull(result);

        FFprobeResult probe = FFprobe.atPath(BIN)
                .setInputPath(outputPath)
                .setShowStreams(true)
                .setShowError(true)
                .execute();

        double duration = 0.0;
        for (Stream stream : probe.getStreams().getStream()) {
            duration = Math.max(duration, stream.getDuration());
        }

        Assert.assertNotNull(probe);
        Assert.assertNull(probe.getError());
        Assert.assertEquals(10.0, duration, 0.1);
    }
}
