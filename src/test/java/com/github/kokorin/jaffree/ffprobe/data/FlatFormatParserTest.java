package com.github.kokorin.jaffree.ffprobe.data;

import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FlatFormatParserTest {

    @Test
    public void parse() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("ffprobe_out.flat")){
            Data data = new FlatFormatParser().parse(input);
            Assert.assertNotNull(data);

            List<DSection> streams = data.getSections("STREAM");
            Assert.assertEquals(2, streams.size());
        }

    }

    @Test
    public void parseMultiline() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("ffprobe_multiline_out.flat")){
            Data data = new FlatFormatParser().parse(input);
            Assert.assertNotNull(data);

            List<DSection> streams = data.getSections("STREAM");
            Assert.assertEquals(2, streams.size());

            String description = data.getSection("format").getTag("tags").getString("description");

            int actualLines = description.split("\\n").length;
            Assert.assertTrue(actualLines > 10);
        }

    }

    @Test
    public void parseHttp() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("ffprobe_http_out.flat")){
            Data data = new FlatFormatParser().parse(input);
            Assert.assertNotNull(data);

            List<DSection> streams = data.getSections("STREAM");
            Assert.assertEquals(2, streams.size());

            String actual = data.getSection("format").getString("filename");
            String expected = "https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4?q1=v1&q2=v2";

            Assert.assertEquals(expected, actual);
        }

    }

    @Test
    public void parseWithRotate() throws IOException {
        try (InputStream input = getClass().getResourceAsStream("ffprobe_with_rotate.flat")){
            Data data = new FlatFormatParser().parse(input);
            Assert.assertNotNull(data);

            List<DSection> streams = data.getSections("STREAM");
            Assert.assertEquals(2, streams.size());

            DSection section = streams.get(0);
            Assert.assertNotNull(section);

            DTag dTag = section.getTag("TAGS");
            Assert.assertNotNull(dTag);

            String rotate = dTag.getString("rotate");
            Assert.assertEquals("90", rotate);

            FFprobeResult result = new FFprobeResult(data);
            Stream stream = result.getStreams().get(0);
            rotate = stream.getTag("rotate");
            Assert.assertEquals("90", rotate);
        }
    }
}