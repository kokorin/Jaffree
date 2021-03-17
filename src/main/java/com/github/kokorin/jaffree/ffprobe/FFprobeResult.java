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

import com.github.kokorin.jaffree.ffprobe.data.ProbeData;
import com.github.kokorin.jaffree.ffprobe.data.ProbeDataConverter;

import java.util.List;

/**
 * {@link FFprobeResult} contains information about ffprobe execution result.
 */
public class FFprobeResult {
    private final ProbeData probeData;

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
     * @return parsed frames
     * @see FFprobe#setShowFrames(boolean)
     */
    public List<Frame> getFrames() {
        return probeData.getSubDataList("frames", new ProbeDataConverter<Frame>() {
            @Override
            public Frame convert(final ProbeData probeData) {
                return new Frame(probeData);
            }
        });
    }

    /**
     * @return parsed subtitles
     */
    public List<Subtitle> getSubtitles() {
        return probeData.getSubDataList("subtitle", new ProbeDataConverter<Subtitle>() {
            @Override
            public Subtitle convert(final ProbeData probeData) {
                return new Subtitle(probeData);
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
