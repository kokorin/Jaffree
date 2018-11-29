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
        DSection section = data.getSection("PROGRAM_VERSION");
        if (section == null) {
            return null;
        }

        return new ProgramVersion(section);
    }

    public Format getFormat() {
        return null;
    }

    public Error getError() {
        DSection section = data.getSection("ERROR");
        if (section == null) {
            return null;
        }

        return new Error(section);
    }

    public List<LibraryVersion> getLibraryVersions() {
        return data.getSections("LIBRARY_VERSION", new Data.SectionConverter<LibraryVersion>() {
            @Override
            public LibraryVersion convert(DSection dSection) {
                return new LibraryVersion(dSection);
            }
        });
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
        return data.getSections("PROGRAM", new Data.SectionConverter<Program>() {
            @Override
            public Program convert(DSection dSection) {
                return new Program(dSection);
            }
        });
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
