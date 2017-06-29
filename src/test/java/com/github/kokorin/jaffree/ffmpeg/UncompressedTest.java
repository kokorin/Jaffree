/*
 *    Copyright  2017 Denis Kokorin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.StreamSpecifier;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.matroska.ExtraDocTypes;
import org.ebml.io.FileDataSource;
import org.ebml.matroska.MatroskaFile;
import org.ebml.matroska.MatroskaFileTrack;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class UncompressedTest {
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
