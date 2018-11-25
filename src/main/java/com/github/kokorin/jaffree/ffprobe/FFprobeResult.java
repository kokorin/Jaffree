package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.Data;
import com.github.kokorin.jaffree.ffprobe.data.Section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FFprobeResult {
    private final Data data;

    public FFprobeResult(Data data) {
        this.data = data;
    }

    public ProgramVersion getProgramVersion() {
        return null;
    }

    public Format getFormat() {
        return null;
    }

    public Error getError() {
        return null;
    }

    public List<LibraryVersion> getLibraryVersions() {
        return Collections.emptyList();
    }

    public List<PixelFormat> getPixelFormats() {
        return Collections.emptyList();
    }

    public List<Packet> getPackets() {
        return Collections.emptyList();
    }

    public List<Object> getFrames() {
        return Collections.emptyList();
    }

    public List<Object> getPacketsAndFrames() {
        return Collections.emptyList();
    }

    public List<Program> getPrograms() {
        return Collections.emptyList();
    }

    public List<Stream> getStreams() {
        List<Stream> result = new ArrayList<>();
        for (Section section : data.getSections("STREAM")) {
            result.add(new Stream(section));
        }
        return result;
    }

    public List<Chapter> getChapters() {
        return Collections.emptyList();
    }
}
