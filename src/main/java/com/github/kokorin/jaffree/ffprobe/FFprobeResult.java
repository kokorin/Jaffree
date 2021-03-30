/*
 *    Copyright 2018-2021 Denis Kokorin
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

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.ProbeData;
import com.github.kokorin.jaffree.ffprobe.data.ProbeDataConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * {@link FFprobeResult} contains information about ffprobe execution result.
 */
public class FFprobeResult {
    private final ProbeData probeData;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFprobeResult.class);

    /**
     * Constructs {@link FFprobeResult} from parsed {@link ProbeData}.
     *
     * @param probeData parsed ffprobe output
     */
    public FFprobeResult(final ProbeData probeData) {
        this.probeData = probeData;
    }

    /**
     * This method allows direct access to parsed ffprobe output.
     * <p>
     * It can be used if {@link FFprobeResult} doesn't provide corresponding getter
     *
     * @return parsed ffprobe output
     */
    public ProbeData getData() {
        return probeData;
    }

    /**
     * @return format-related data
     * @see FFprobe#setShowFormat(boolean)
     */
    public Format getFormat() {
        return probeData.getSubData("format", new ProbeDataConverter<Format>() {
            @Override
            public Format convert(ProbeData probeData) {
                return new Format(probeData);
            }
        });
    }

    /**
     * @return parsed packets
     * @see FFprobe#setShowPackets(boolean)
     */
    public List<Packet> getPackets() {
        return probeData.getSubDataList("packets", new ProbeDataConverter<Packet>() {
            @Override
            public Packet convert(final ProbeData probeData) {
                return new Packet(probeData);
            }
        });
    }

    /**
     * @return parsed frames and subtitles
     * @see FFprobe#setShowFrames(boolean)
     * @see Frame
     * @see Subtitle
     */
    public List<FrameSubtitle> getFrames() {
        return probeData.getSubDataList("frames", new ProbeDataConverter<FrameSubtitle>() {
            @Override
            public FrameSubtitle convert(final ProbeData probeData) {
                StreamType streamType = probeData.getStreamType("media_type");
                if (streamType == StreamType.SUBTITLE) {
                    return new Subtitle(probeData);
                }
                return new Frame(probeData);
            }
        });
    }

    /**
     * @return parsed packets, frames and subtitles
     * @see FFprobe#setShowPackets(boolean)
     * @see FFprobe#setShowFrames(boolean)
     * @see Packet
     * @see Frame
     * @see Subtitle
     */
    public List<PacketFrameSubtitle> getPacketsAndFrames() {
        return probeData.getSubDataList("packets_and_frames", new ProbeDataConverter<PacketFrameSubtitle>() {
            @Override
            public PacketFrameSubtitle convert(final ProbeData probeData) {
                String type = probeData.getString("type");
                if (type == null) {
                    LOGGER.error("No type property found");
                    return null;
                }

                switch (type) {
                    case "packet":
                        return new Packet(probeData);
                    case "frame":
                        return new Frame(probeData);
                    case "subtitle":
                        return new Subtitle(probeData);
                    default:
                        LOGGER.error("Unknown type: " + type);
                        return null;
                }
            }
        });
    }

    /**
     * @return parsed programs
     * @see FFprobe#setShowPrograms(boolean)
     */
    public List<Program> getPrograms() {
        return probeData.getSubDataList("programs", new ProbeDataConverter<Program>() {
            @Override
            public Program convert(final ProbeData dSection) {
                return new Program(dSection);
            }
        });
    }

    /**
     * @return parsed streams
     * @see FFprobe#setShowStreams(boolean)
     */
    public List<Stream> getStreams() {
        return probeData.getSubDataList("streams", new ProbeDataConverter<Stream>() {
            @Override
            public Stream convert(final ProbeData probeData) {
                return new Stream(probeData);
            }
        });
    }

    /**
     * @return parsed chapters
     * @see FFprobe#setShowChapters(boolean)
     */
    public List<Chapter> getChapters() {
        return probeData.getSubDataList("chapters", new ProbeDataConverter<Chapter>() {
            @Override
            public Chapter convert(final ProbeData probeData) {
                return new Chapter(probeData);
            }
        });
    }
}
