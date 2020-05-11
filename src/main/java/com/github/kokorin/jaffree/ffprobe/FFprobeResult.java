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

/**
 * {@link FFprobeResult} contains information about ffprobe execution result.
 */
public class FFprobeResult {
    private final Data data;

    /**
     * Constructs {@link FFprobeResult} from parsed {@link Data}.
     *
     * @param data parsed ffprobe output
     */
    public FFprobeResult(final Data data) {
        this.data = data;
    }

    /**
     * This method allows direct access to parsed ffprobe output.
     * <p>
     * It can be used if {@link FFprobeResult} doesn't provide corresponding getter
     *
     * @return parsed ffprobe output
     */
    public Data getData() {
        return data;
    }

    /**
     * @return ffprobe version
     */
    // TODO: delete this?
    public ProgramVersion getProgramVersion() {
        DSection section = data.getSection("PROGRAM_VERSION");
        if (section == null) {
            return null;
        }

        return new ProgramVersion(section);
    }

    /**
     * @return format-related data
     * @see FFprobe#setShowFormat(boolean)
     */
    public Format getFormat() {
        DSection section = data.getSection("FORMAT");
        if (section == null) {
            return null;
        }

        return new Format(section);
    }

    /**
     * @return ffprobe errors
     */
    // TODO: delete this?
    public Error getError() {
        DSection section = data.getSection("ERROR");
        if (section == null) {
            return null;
        }

        return new Error(section);
    }

    /**
     * @return FF-library versions.
     */
    // TODO: delete this?
    public List<LibraryVersion> getLibraryVersions() {
        return data.getSections("LIBRARY_VERSION", new DSection.SectionConverter<LibraryVersion>() {
            @Override
            public LibraryVersion convert(final DSection dSection) {
                return new LibraryVersion(dSection);
            }
        });
    }

    /**
     * @return supported pixel formats
     */
    // TODO: delete this?
    public List<PixelFormat> getPixelFormats() {
        return data.getSections("PIXEL_FORMAT", new DSection.SectionConverter<PixelFormat>() {
            @Override
            public PixelFormat convert(final DSection dSection) {
                return new PixelFormat(dSection);
            }
        });
    }

    /**
     * @return parsed packets
     * @see FFprobe#setShowPackets(boolean)
     */
    public List<Packet> getPackets() {
        return data.getSections("PACKET", new DSection.SectionConverter<Packet>() {
            @Override
            public Packet convert(final DSection dSection) {
                return new Packet(dSection);
            }
        });
    }

    /**
     * @return parsed frames
     * @see FFprobe#setShowFrames(boolean)
     */
    public List<Frame> getFrames() {
        return data.getSections("FRAME", new DSection.SectionConverter<Frame>() {
            @Override
            public Frame convert(final DSection dSection) {
                return new Frame(dSection);
            }
        });
    }

    /**
     * @return parsed subtitles
     */
    public List<Subtitle> getSubtitles() {
        return data.getSections("SUBTITLE", new DSection.SectionConverter<Subtitle>() {
            @Override
            public Subtitle convert(final DSection dSection) {
                return new Subtitle(dSection);
            }
        });
    }

    /**
     * @return parsed programs
     * @see FFprobe#setShowPrograms(boolean)
     */
    public List<Program> getPrograms() {
        return data.getSections("PROGRAM", new DSection.SectionConverter<Program>() {
            @Override
            public Program convert(final DSection dSection) {
                return new Program(dSection);
            }
        });
    }

    /**
     * @return parsed streams
     * @see FFprobe#setShowStreams(boolean)
     */    public List<Stream> getStreams() {
        return data.getSections("STREAM", DSection.STREAM_CONVERTER);
    }

    /**
     * @return parsed chapters
     * @see FFprobe#setShowChapters(boolean)
     */
    public List<Chapter> getChapters() {
        return data.getSections("CHAPTER", new DSection.SectionConverter<Chapter>() {
            @Override
            public Chapter convert(final DSection dSection) {
                return new Chapter(dSection);
            }
        });
    }
}
