package com.github.kokorin.jaffree;

import com.github.kokorin.jaffree.cli.StreamSpecifier;
import com.github.kokorin.jaffree.ffprobe.xml.FFprobeType;
import com.github.kokorin.jaffree.ffprobe.xml.StreamType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FFprobeTest {
    public static final Path bin = Paths.get("D:\\Projects\\ffmpeg\\bin");
    public static final Path input = Paths.get("D:\\Projects\\Jaffree\\target\\samples\\MPEG-4\\video.mp4");

    @Test
    public void testShowStreams() throws Exception {
        FFprobeType result = FFprobe.atPath(bin)
                .setInputPath(input)
                .setShowStreams(true)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.getStreams().getStream().size());
    }

    @Test
    public void testSelectStreamWithShowStreams() throws Exception {
        FFprobeType result = FFprobe.atPath(bin)
                .setInputPath(input)
                .setShowStreams(true)
                .setSelectStreams(new StreamSpecifier("v"))
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getStreams().getStream().size());

        StreamType stream = result.getStreams().getStream().get(0);
        Assert.assertEquals("video", stream.getCodecType());
    }

    @Test
    public void testSelectStreamWithShowPackets() throws Exception {
        FFprobeType result = FFprobe.atPath(bin)
                .setInputPath(input)
                .setShowPackets(true)
                .setSelectStreams(new StreamSpecifier(5))
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getPackets().getPacket().size());
    }
}
