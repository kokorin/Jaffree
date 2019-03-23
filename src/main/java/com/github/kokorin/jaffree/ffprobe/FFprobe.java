/*
 *    Copyright  2017 Denis Kokorin
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

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.data.FlatFormatParser;
import com.github.kokorin.jaffree.ffprobe.data.FormatParser;
import com.github.kokorin.jaffree.process.LoggingStdReader;
import com.github.kokorin.jaffree.process.ProcessHandler;
import com.github.kokorin.jaffree.process.StdReader;
import com.github.kokorin.jaffree.process.ThrowingStdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FFprobe {
    private final LogLevel logLevel = LogLevel.ERROR;

    private String selectStreams;
    private boolean showData;
    private boolean showPrivateData = true;
    private String showDataHash;
    private boolean showFormat;
    private String showFormatEntry;
    private String showEntries;
    private boolean showPackets;
    private boolean showFrames;
    private LogLevel showLog;
    private boolean showStreams;
    private boolean showPrograms;
    private boolean showChapters;
    private boolean countFrames;
    private boolean countPackets;
    private String readIntervals;
    private boolean showProgramVersion;
    private boolean showLibraryVersions;
    private boolean showVersions;
    private boolean showPixelFormats;

    private Long probeSize;
    private Long analyzeDuration;
    private Long fpsProbeSize;

    private final List<String> additionalArguments = new ArrayList<>();

    private String input;

    private FormatParser parser = new FlatFormatParser();

    private final Path executable;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFprobe.class);

    public FFprobe(Path executable) {
        this.executable = executable;
    }

    public FFprobe addArgument(String argument) {
        additionalArguments.add(argument);
        return this;
    }

    public FFprobe addArguments(String key, String value) {
        additionalArguments.addAll(Arrays.asList(key, value));
        return this;
    }

    /**
     * Select only the streams specified by stream_specifier.
     * <p>
     * This option affects only the options related to streams (e.g. show_streams, show_packets, etc.).
     *
     * @param streamSpecifier
     * @return this
     */
    public FFprobe setSelectStreams(String streamSpecifier) {
        this.selectStreams = streamSpecifier;
        return this;
    }

    public FFprobe setSelectStreams(StreamType streamType) {
        this.selectStreams = streamType.code();
        return this;
    }

    /**
     * Show payload data, as a hexadecimal and ASCII dump.
     * <p>
     * Coupled with -show_packets, it will dump the packets’ data.
     * <p>
     * Coupled with -show_streams, it will dump the codec extradata.
     *
     * @param showData
     * @return this
     */
    public FFprobe setShowData(boolean showData) {
        this.showData = showData;
        return this;
    }

    /**
     * Show private data, that is data depending on the format of the particular shown element.
     * This option is enabled by default, but you may need to disable it for specific uses.
     *
     * @param showPrivateData show
     * @return this
     */
    public FFprobe setShowPrivateData(boolean showPrivateData) {
        this.showPrivateData = showPrivateData;
        return this;
    }

    /**
     * Show a hash of payload data, for packets with -show_packets and for codec extradata with -show_streams.
     *
     * @param algorithm
     * @return this
     */
    public FFprobe setShowDataHash(String algorithm) {
        this.showDataHash = algorithm;
        return this;
    }

    /**
     * Show information about the container format of the input multimedia stream.
     *
     * @param showFormat
     * @return this
     */
    public FFprobe setShowFormat(boolean showFormat) {
        this.showFormat = showFormat;
        return this;
    }

    /**
     * Like -show_format, but only prints the specified entry of the container format information, rather than all.
     * <p>
     * This option may be given more than once, then all specified entries will be shown.
     *
     * @param showFormatEntry
     * @return this
     * @see #setShowEntries(String)
     * @deprecated This option is deprecated, use show_entries instead.     *
     */
    public FFprobe setShowFormatEntry(String showFormatEntry) {
        this.showFormatEntry = showFormatEntry;
        return this;
    }

    /**
     * Set list of entries to show.
     * <p>
     * Entries are specified according to the following syntax. section_entries contains a list of section entries separated by :.
     * Each section entry is composed by a section name (or unique name),
     * optionally followed by a list of entries local to that section, separated by ,.
     * <p>
     * Note that the order of specification of the local section entries is not honored in the output,
     * and the usual display order will be retained.
     * <p>
     * The formal syntax is given by:
     * <p>
     * LOCAL_SECTION_ENTRIES ::= SECTION_ENTRY_NAME[,LOCAL_SECTION_ENTRIES]
     * <p>
     * SECTION_ENTRY         ::= SECTION_NAME[=[LOCAL_SECTION_ENTRIES]]
     * <p>
     * SECTION_ENTRIES       ::= SECTION_ENTRY[:SECTION_ENTRIES]
     * <p>
     * {@link Packet#getStreamIndex} can be absent in XML
     * {@link Stream#getIndex} also can be absent in XML
     *
     * @param showEntries
     * @return this
     */
    public FFprobe setShowEntries(String showEntries) {
        this.showEntries = showEntries;
        return this;
    }

    /**
     * Show information about each packet contained in the input multimedia stream.
     *
     * @param showPackets
     * @return this
     */
    public FFprobe setShowPackets(boolean showPackets) {
        this.showPackets = showPackets;
        return this;
    }

    /**
     * Show information about each frame and subtitle contained in the input multimedia stream.
     *
     * @param showFrames
     * @return this
     */
    public FFprobe setShowFrames(boolean showFrames) {
        this.showFrames = showFrames;
        return this;
    }

    /**
     * Show logging information from the decoder about each frame according to the value set in loglevel, (see -loglevel).
     * <p>
     * This option requires -show_frames.
     *
     * @param logLevel
     * @return this
     * @see #setShowFrames(boolean)
     */
    public FFprobe setShowLog(LogLevel logLevel) {
        this.showLog = logLevel;
        return this;
    }

    /**
     * Show information about each media stream contained in the input multimedia stream.
     *
     * @param showStreams
     * @return this
     */
    public FFprobe setShowStreams(boolean showStreams) {
        this.showStreams = showStreams;
        return this;
    }

    /**
     * Show information about programs and their streams contained in the input multimedia stream.
     *
     * @param showPrograms
     * @return this
     */
    public FFprobe setShowPrograms(boolean showPrograms) {
        this.showPrograms = showPrograms;
        return this;
    }

    /**
     * Show information about chapters stored in the format.
     *
     * @param showChapters
     * @return this
     */
    public FFprobe setShowChapters(boolean showChapters) {
        this.showChapters = showChapters;
        return this;
    }

    /**
     * Count the number of frames per stream and report it in the corresponding stream section.
     *
     * @param countFrames
     * @return this
     */
    public FFprobe setCountFrames(boolean countFrames) {
        this.countFrames = countFrames;
        return this;
    }

    /**
     * Count the number of packets per stream and report it in the corresponding stream section.
     *
     * @param countPackets
     * @return this
     */
    public FFprobe setCountPackets(boolean countPackets) {
        this.countPackets = countPackets;
        return this;
    }

    /**
     * Read only the specified intervals. read_intervals must be a sequence of interval specifications separated by ",".
     * <p>
     * ffprobe will seek to the interval starting point, and will continue reading from that.
     * <p>
     * The formal syntax is given by:
     * <p>
     * INTERVAL  ::= [START|+START_OFFSET][%[END|+END_OFFSET|+#NUMBER_OF_FRAMES]]
     * <p>
     * INTERVALS ::= INTERVAL[,INTERVALS]
     *
     * @param intervals
     * @return this
     */
    public FFprobe setReadIntervals(String intervals) {
        this.readIntervals = intervals;
        return this;
    }

    /**
     * Show information related to program version.
     *
     * @param showProgramVersion
     * @return this
     */
    public FFprobe setShowProgramVersion(boolean showProgramVersion) {
        this.showProgramVersion = showProgramVersion;
        return this;
    }

    /**
     * Show information related to library versions.
     *
     * @param showLibraryVersions
     * @return this
     */
    public FFprobe setShowLibraryVersions(boolean showLibraryVersions) {
        this.showLibraryVersions = showLibraryVersions;
        return this;
    }

    /**
     * Show information related to program and library versions.
     * <p>
     * This is the equivalent of setting both -show_program_version and -show_library_versions
     *
     * @param showVersions
     * @return
     */
    public FFprobe setShowVersions(boolean showVersions) {
        this.showVersions = showVersions;
        return this;
    }

    /**
     * Show information about all pixel formats supported by FFmpeg.
     *
     * @param showPixelFormats
     * @return this
     */
    public FFprobe setShowPixelFormats(boolean showPixelFormats) {
        this.showPixelFormats = showPixelFormats;
        return this;
    }

    /**
     * Set probing size (from 32 to I64_MAX) (default 5e+006)
     * @param probeSize
     * @return this
     */
    public FFprobe setProbeSize(Long probeSize) {
        this.probeSize = probeSize;
        return this;
    }

    /**
     * Specify how many microseconds are analyzed to probe the input (from 0 to I64_MAX) (default 0)
     * @param analyzeDurationMicros
     * @return this
     */
    public FFprobe setAnalyzeDuration(Long analyzeDurationMicros) {
        this.analyzeDuration = analyzeDurationMicros;
        return this;
    }

    /**
     * Specify how long to analyze to probe the input (from 0 to I64_MAX) (default 0)
     * @param analyzeDuration duration
     * @param timeUnit time unit
     * @return this
     */
    public FFprobe setAnalyzeDuration(Number analyzeDuration, TimeUnit timeUnit) {
        long micros = (long) (analyzeDuration.doubleValue() * timeUnit.toMicros(1));
        return setAnalyzeDuration(micros);
    }

    /**
     * Number of frames used to probe fps (from -1 to 2.14748e+009) (default -1)
     * @param fpsProbeSize frames
     * @return this
     */
    public FFprobe setFpsProbeSize(Long fpsProbeSize) {
        this.fpsProbeSize = fpsProbeSize;
        return this;
    }

    public FFprobe setInput(Path path) {
        this.input = path.toString();
        return this;
    }

    public FFprobe setInput(String input) {
        this.input = input;
        return this;
    }


    public FFprobe setFormatParser(FormatParser parser) {
        if (parser == null) {
            throw new IllegalArgumentException("Parser must be non null");
        }

        this.parser = parser;
        return this;
    }

    public FFprobeResult execute() {
        return new ProcessHandler<FFprobeResult>(executable, null)
                .setStdOutReader(createStdOutReader())
                .setStdErrReader(createStdErrReader())
                .execute(buildArguments());
    }

    protected List<String> buildArguments() {
        List<String> result = new ArrayList<>();

        if (logLevel != null) {
            result.addAll(Arrays.asList("-loglevel", Integer.toString(logLevel.code())));
        }

        if (selectStreams != null) {
            result.addAll(Arrays.asList("-select_streams", selectStreams));
        }
        if (showData) {
            result.add("-show_data");
        }
        if (showDataHash != null) {
            result.addAll(Arrays.asList("-show_data_hash", showDataHash));
        }
        if (showFormat) {
            result.add("-show_format");
        }
        if (showFormatEntry != null) {
            result.addAll(Arrays.asList("-show_format_entry", showFormatEntry));
        }
        if (showEntries != null) {
            result.addAll(Arrays.asList("-show_entries", showEntries));
        }
        if (showPackets) {
            result.add("-show_packets");
        }
        if (showFrames) {
            result.add("-show_frames");
        }
        if (showLog != null) {
            result.addAll(Arrays.asList("-show_log", Integer.toString(showLog.code())));
        }
        if (showStreams) {
            result.add("-show_streams");
        }
        if (showPrograms) {
            result.add("-show_programs");
        }
        if (showChapters) {
            result.add("-show_chapters");
        }
        if (countFrames) {
            result.add("-count_frames");
        }
        if (countPackets) {
            result.add("-count_packets");
        }
        if (readIntervals != null) {
            result.addAll(Arrays.asList("-read_intervals", readIntervals));
        }
        if (showPrivateData) {
            result.add("-show_private_data");
        } else {
            result.add("-noprivate");
        }
        if (showProgramVersion) {
            result.add("-show_program_version");
        }
        if (showLibraryVersions) {
            result.add("-show_library_versions");
        }
        if (showVersions) {
            result.add("-show_versions");
        }
        if (showPixelFormats) {
            result.add("-show_pixel_formats");
        }

        if (probeSize != null) {
            result.addAll(Arrays.asList("-probesize", probeSize.toString()));
        }
        if (analyzeDuration != null) {
            result.addAll(Arrays.asList("-analyzeduration", analyzeDuration.toString()));
        }
        if (fpsProbeSize != null) {
            result.addAll(Arrays.asList("-fpsprobesize", fpsProbeSize.toString()));
        }

        result.addAll(Arrays.asList("-print_format", parser.getFormatName()));

        result.addAll(additionalArguments);

        if (input != null) {
            result.addAll(Arrays.asList("-i", input));
        }

        return result;
    }

    protected StdReader<FFprobeResult> createStdOutReader() {
        return new FFprobeResultReader(parser);
    }

    protected StdReader<FFprobeResult> createStdErrReader() {
        if (logLevel.code() > LogLevel.WARNING.code()) {
            return new LoggingStdReader<>();
        }

        return new ThrowingStdReader<>();
    }

    public static FFprobe atPath() {
        return atPath(null);
    }

    public static FFprobe atPath(Path pathToDir) {
        final Path executable;
        if (pathToDir != null) {
            executable = pathToDir.resolve("ffprobe");
        } else {
            executable = Paths.get("ffprobe");
        }

        return new FFprobe(executable);
    }

}
