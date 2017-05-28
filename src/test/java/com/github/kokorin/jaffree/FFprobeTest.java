package com.github.kokorin.jaffree;

import com.github.kokorin.jaffree.ffprobe.xml.FFprobeType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FFprobeTest {
    @Test
    public void test1() throws Exception {
        Path bin = Paths.get("D:\\Projects\\ffmpeg\\bin");
        Path input = Paths.get("D:\\Projects\\Jaffree\\target\\samples\\MPEG-4\\video.mp4");
        //Path input = Paths.get("D:\\Видео\\Cinema\\День выборов (полн).avi")
        FFprobeType result = FFprobe.atPath(bin)
                .setInputPath(input)
                .execute();

        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.getStreams().getStream().size());
    }
}
