package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.FlatFormatParser;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class FFprobeResultTest {

    @Test
    public void testChaptersWithFlatFormat() throws Exception {
        FFprobeResult result;
        try (InputStream input = this.getClass().getResourceAsStream("./data/ffprobe_streams_and_chapters.flat")) {
            result = new FFprobeResult(new FlatFormatParser().parse(input));
        }
        verifyChaptersFFprobeResult(result);
    }

    public void verifyChaptersFFprobeResult(FFprobeResult result) {
        List<Stream> streams = result.getStreams();
        assertEquals(8, streams.size());

        Stream videoStream = streams.get(0);

        assertEquals(StreamType.VIDEO, videoStream.getCodecType());
        assertEquals("hevc", videoStream.getCodecName());
        assertEquals(Boolean.TRUE, videoStream.getDisposition().getDefault());

        Stream audioStream = streams.get(1);
        assertEquals(StreamType.AUDIO, audioStream.getCodecType());
        assertEquals("aac", audioStream.getCodecName());

        List<Chapter> chapters = result.getChapters();
        assertEquals(5, chapters.size());
        Chapter chapter = chapters.get(0);
        assertEquals("Chapter 01", chapter.getTag("title"));
    }

    @Test
    public void testProgramsWithFlatFormat() throws Exception {
        FFprobeResult result;
        try (InputStream input = this.getClass().getResourceAsStream("./data/ffprobe_programs.flat")) {
            result = new FFprobeResult(new FlatFormatParser().parse(input));
        }
        verifyProgramsFFprobeResult(result);
    }

    public void verifyProgramsFFprobeResult(FFprobeResult result) {
        List<Program> programs = result.getPrograms();
        assertEquals(3, programs.size());

        for (int i = 0; i < 3; i++) {
            Program program = programs.get(i);
            assertEquals("program " + i, (Integer) (i + 1), program.getProgramId());
            assertEquals("program " + i, (Integer) (i + 1), program.getProgramNum());
            assertEquals("program " + i, (Integer) 2, program.getNbStreams());
            assertEquals("program " + i, "FFmpeg", program.getTag("service_provider"));
            List<Stream> streams = program.getStreams();
            assertEquals(2, streams.size());
            Stream video = streams.get(0);
            assertEquals(StreamType.VIDEO, video.getCodecType());
            assertEquals("mpeg2video", video.getCodecName());
            Stream audio = streams.get(1);
            assertEquals(StreamType.AUDIO, audio.getCodecType());
            assertEquals("mp2", audio.getCodecName());
        }
    }
}