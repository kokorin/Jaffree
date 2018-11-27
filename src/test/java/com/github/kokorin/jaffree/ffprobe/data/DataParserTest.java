package com.github.kokorin.jaffree.ffprobe.data;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

public class DataParserTest {

    @Test
    public void parse() throws Exception {
        Data data = parseResource("ffprobe_streams.out");

        Assert.assertNotNull(data);

        List<DSection> streams = data.getSections("STREAM");
        Assert.assertEquals(2, streams.size());
    }

    public static Data parseResource(String name) throws Exception {
        try (InputStream input = DataParserTest.class.getResourceAsStream(name)){
            Iterator<String> lineIterator = IOUtils.lineIterator(input, StandardCharsets.UTF_8);
            return DataParser.parse(lineIterator);
        }
    }
}