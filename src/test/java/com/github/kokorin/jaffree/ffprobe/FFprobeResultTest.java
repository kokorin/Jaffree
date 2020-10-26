package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.DefaultFormatParser;
import com.github.kokorin.jaffree.ffprobe.data.FlatFormatParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class FFprobeResultTest {

    @Test
    public void testWithDefaultFormat() throws Exception {
        FFprobeResult result;
        try (InputStream input = this.getClass().getResourceAsStream("./data/ffprobe_streams_and_chapters.out")) {
            result = new FFprobeResult(new DefaultFormatParser().parse(input));
        }
        verifyFFprobeResult(result);

    }

    @Test
    public void testWithFlatFormat() throws Exception {
        FFprobeResult result;
        try (InputStream input = this.getClass().getResourceAsStream("./data/ffprobe_streams_and_chapters.flat")) {
            result = new FFprobeResult(new FlatFormatParser().parse(input));
        }
        verifyFFprobeResult(result);

    }

    public void verifyFFprobeResult(FFprobeResult result) {
        List<Stream> streams = result.getStreams();
        Assert.assertEquals(8, streams.size());

        Stream videoStream = streams.get(0);

        Assert.assertEquals(StreamType.VIDEO, videoStream.getCodecType());
        Assert.assertEquals("hevc", videoStream.getCodecName());
        Assert.assertEquals(1, videoStream.getDisposition().getDefault());
        List<Tag> videoTags = videoStream.getTagList();

        Stream audioStream = streams.get(1);
        Assert.assertEquals(StreamType.AUDIO, audioStream.getCodecType());
        Assert.assertEquals("aac", audioStream.getCodecName());

        List<Chapter> chapters = result.getChapters();
        Assert.assertEquals(5, chapters.size());
        Chapter chapter = chapters.get(0);
        Assert.assertEquals(1, chapter.getTags().size());
        Assert.assertEquals("Chapter 01", chapter.getTag("title"));
    }
}