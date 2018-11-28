package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;
import com.github.kokorin.jaffree.ffprobe.data.Data;

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
        return data.getSections("PACKET", new Data.SectionConverter<Packet>() {
            @Override
            public Packet convert(DSection dSection) {
                return new Packet(dSection);
            }
        });
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
        return data.getSections("STREAM", new Data.SectionConverter<Stream>() {
            @Override
            public Stream convert(DSection dSection) {
                return new Stream(dSection);
            }
        });
    }

    public List<Chapter> getChapters() {
        return Collections.emptyList();
    }
}
