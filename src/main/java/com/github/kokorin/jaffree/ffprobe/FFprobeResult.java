/*
 *    Copyright  2018 Denis Kokorin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;
import com.github.kokorin.jaffree.ffprobe.data.Data;

import java.util.List;

public class FFprobeResult {
    private final Data data;

    public FFprobeResult(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    // TODO: delete this?
    public ProgramVersion getProgramVersion() {
        DSection section = data.getSection("PROGRAM_VERSION");
        if (section == null) {
            return null;
        }

        return new ProgramVersion(section);
    }

    public Format getFormat() {
        DSection section = data.getSection("FORMAT");
        if (section == null) {
            return null;
        }

        return new Format(section);
    }

    // TODO: delete this?
    public Error getError() {
        DSection section = data.getSection("ERROR");
        if (section == null) {
            return null;
        }

        return new Error(section);
    }

    // TODO: delete this?
    public List<LibraryVersion> getLibraryVersions() {
        return data.getSections("LIBRARY_VERSION", new DSection.SectionConverter<LibraryVersion>() {
            @Override
            public LibraryVersion convert(DSection dSection) {
                return new LibraryVersion(dSection);
            }
        });
    }

    // TODO: delete this?
    public List<PixelFormat> getPixelFormats() {
        return data.getSections("PIXEL_FORMAT", new DSection.SectionConverter<PixelFormat>() {
            @Override
            public PixelFormat convert(DSection dSection) {
                return new PixelFormat(dSection);
            }
        });
    }

    public List<Packet> getPackets() {
        return data.getSections("PACKET", new DSection.SectionConverter<Packet>() {
            @Override
            public Packet convert(DSection dSection) {
                return new Packet(dSection);
            }
        });
    }

    public List<Frame> getFrames() {
        return data.getSections("FRAME", new DSection.SectionConverter<Frame>() {
            @Override
            public Frame convert(DSection dSection) {
                return new Frame(dSection);
            }
        });
    }

    public List<Subtitle> getSubtitles() {
        return data.getSections("SUBTITLE", new DSection.SectionConverter<Subtitle>() {
            @Override
            public Subtitle convert(DSection dSection) {
                return new Subtitle(dSection);
            }
        });
    }

    public List<Program> getPrograms() {
        return data.getSections("PROGRAM", new DSection.SectionConverter<Program>() {
            @Override
            public Program convert(DSection dSection) {
                return new Program(dSection);
            }
        });
    }

    public List<Stream> getStreams() {
        return data.getSections("STREAM", DSection.STREAM_CONVERTER);
    }

    public List<Chapter> getChapters() {
        return data.getSections("CHAPTER", new DSection.SectionConverter<Chapter>() {
            @Override
            public Chapter convert(DSection dSection) {
                return new Chapter(dSection);
            }
        });
    }
}
