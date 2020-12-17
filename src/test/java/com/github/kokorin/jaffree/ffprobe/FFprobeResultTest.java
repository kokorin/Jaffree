package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class FFprobeResultTest {
    
    @Test
    public void testChaptersWithDefaultFormat() throws Exception {
        FFprobeResult result;
        try (InputStream input = this.getClass().getResourceAsStream("./data/ffprobe_streams_and_chapters.out")) {
            result = new FFprobeResult(readData(input, StreamingFormatParsers.createDefault()));
        }
        verifyChaptersFFprobeResult(result);
        
    }
    
    @Test
    public void testChaptersWithFlatFormat() throws Exception {
        FFprobeResult result;
        try (InputStream input = this.getClass().getResourceAsStream("./data/ffprobe_streams_and_chapters.flat")) {
            result = new FFprobeResult(readData(input, StreamingFormatParsers.createFlat()));
        }
        verifyChaptersFFprobeResult(result);
    }
    
    public void verifyChaptersFFprobeResult(FFprobeResult result) {
        List<Stream> streams = result.getStreams();
        Assert.assertEquals(8, streams.size());
        
        Stream videoStream = streams.get(0);
        
        Assert.assertEquals(StreamType.VIDEO, videoStream.getCodecType());
        Assert.assertEquals("hevc", videoStream.getCodecName());
        Assert.assertEquals(1, videoStream.getDisposition().getDefault());
        videoStream.getTags();
        
        Stream audioStream = streams.get(1);
        Assert.assertEquals(StreamType.AUDIO, audioStream.getCodecType());
        Assert.assertEquals("aac", audioStream.getCodecName());
        
        List<Chapter> chapters = result.getChapters();
        Assert.assertEquals(5, chapters.size());
        Chapter chapter = chapters.get(0);
        Assert.assertEquals(1, chapter.getTags().size());
        Assert.assertEquals("Chapter 01", chapter.getTag("title"));
    }
    
    @Test
    public void testProgramsWithDefaultFormat() throws Exception {
        FFprobeResult result;
        try (InputStream input = this.getClass().getResourceAsStream("./data/ffprobe_programs.out")) {
            result = new FFprobeResult(readData(input, StreamingFormatParsers.createDefault()));
        }
        verifyProgramsFFprobeResult(result);
        
    }
    
    @Test
    public void testProgramsWithFlatFormat() throws Exception {
        FFprobeResult result;
        try (InputStream input = this.getClass().getResourceAsStream("./data/ffprobe_programs.flat")) {
            result = new FFprobeResult(readData(input, StreamingFormatParsers.createFlat()));
        }
        verifyProgramsFFprobeResult(result);
    }
    
    public void verifyProgramsFFprobeResult(FFprobeResult result) {
        List<Program> programs = result.getPrograms();
        Assert.assertEquals(3, programs.size());
        
        for (int i = 0; i < 3; i++) {
            Program program = programs.get(i);
            Assert.assertEquals("program " + i, i + 1, program.getProgramId());
            Assert.assertEquals("program " + i, i + 1, program.getProgramNum());
            Assert.assertEquals("program " + i, 2, program.getNbStreams());
            Assert.assertEquals("program " + i, "FFmpeg", program.getTag("service_provider"));
            List<Stream> streams = program.getStreams();
            Assert.assertEquals(2, streams.size());
            Stream video = streams.get(0);
            Assert.assertEquals(StreamType.VIDEO, video.getCodecType());
            Assert.assertEquals("mpeg2video", video.getCodecName());
            Stream audio = streams.get(1);
            Assert.assertEquals(StreamType.AUDIO, audio.getCodecType());
            Assert.assertEquals("mp2", audio.getCodecName());
        }
    }
    
    public static Data readData(InputStream input, StreamingFormatParser parser) throws IOException {
        BufferedReader lines = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = lines.readLine()) != null) {
            parser.pushLine(line);
        }
        
        return parser.getResult();
    }
}
