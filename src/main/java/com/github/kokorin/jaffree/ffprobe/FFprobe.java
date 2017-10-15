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
import com.github.kokorin.jaffree.Option;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.process.LoggingStdReader;
import com.github.kokorin.jaffree.process.ProcessHandler;
import com.github.kokorin.jaffree.process.StdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXB;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FFprobe {

    // This final options are required for well-formatted xml output
    //
    // fully_qualified, q
    // If set to 1 specify if the output should be fully qualified. Default value is 0.
    // This is required for generating an XML file which can be validated through an XSD file.
    //
    // xsd_compliant, x
    // If set to 1 perform more checks for ensuring that the output is XSD compliant. Default value is 0.
    // This option automatically sets fully_qualified to 1.
    private final String printFormat = "xml=x=1:q=1";
    // Show private data, that is data depending on the format of the particular shown element.
    // This option is enabled by default, but you may need to disable it for specific uses,
    // for example when creating XSD-compliant XML output.
    private final boolean showPrivateData = false;

    private String selectStreams;
    private boolean showData;
    private String showDataHash;
    private boolean showError;
    private boolean showFormat;
    private String showFormatEntry;
    //TODO extract type
    private String showEntries;
    private boolean showPackets;
    private boolean showFrames;
    private LogLevel showLog;
    private boolean showStreams;
    private boolean showPrograms;
    private boolean showChapters;
    private boolean countFrames;
    private boolean countPackets;
    //TODO extract type
    private String readIntervals;

    private boolean showProgramVersion;
    private boolean showLibraryVersions;
    private boolean showVersions;
    private boolean showPixelFormats;
    private Path inputPath;

    private final Path executable;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFprobe.class);

    public FFprobe(Path executable) {
        this.executable = executable;
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
     * Show information about the error found when trying to probe the inputPath.
     *
     * @param showError
     * @return this
     */
    public FFprobe setShowError(boolean showError) {
        this.showError = showError;
        return this;
    }

    /**
     * Show information about the container format of the inputPath multimedia stream.
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
     * TODO: output with this option doesn't conform to ffprobe.xsd.
     * {@link Packet#streamIndex} can be absent in XML
     * {@link Stream#getIndex()} also can be absent in XML
     *
     * @param showEntries
     * @return this
     */
    public FFprobe setShowEntries(String showEntries) {
        this.showEntries = showEntries;
        return this;
    }

    /**
     * Show information about each packet contained in the inputPath multimedia stream.
     *
     * @param showPackets
     * @return this
     */
    public FFprobe setShowPackets(boolean showPackets) {
        this.showPackets = showPackets;
        return this;
    }

    /**
     * Show information about each frame and subtitle contained in the inputPath multimedia stream.
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
     * Show information about each media stream contained in the inputPath multimedia stream.
     *
     * @param showStreams
     * @return this
     */
    public FFprobe setShowStreams(boolean showStreams) {
        this.showStreams = showStreams;
        return this;
    }

    /**
     * Show information about programs and their streams contained in the inputPath multimedia stream.
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

    public FFprobe setInputPath(Path inputPath) {
        this.inputPath = inputPath;
        return this;
    }

    public FFprobeResult execute() {
        return new ProcessHandler<FFprobeResult>(executable, null)
                .setStdOutReader(createStdOutReader())
                .setStdErrReader(createStdErrReader())
                .execute(buildOptions());
    }

    protected List<Option> buildOptions() {
        List<Option> result = new ArrayList<>();

        // Force bitexact output, useful to produce output which is not dependent on the specific build.
        // -bitexact and -show_program_version or -show_library_versions options are incompatible
        boolean bitExact = !showProgramVersion && !showLibraryVersions && !showVersions;
        if (bitExact) {
            result.add(new Option("-bitexact"));
        }

        if (printFormat != null) {
            result.add(new Option("-print_format", printFormat));
        }

        //ffprobe.add(new Option("-hide_banner"));

        if (selectStreams != null) {
            result.add(new Option("-select_streams", selectStreams));
        }
        if (showData) {
            result.add(new Option("-show_data"));
        }
        if (showDataHash != null) {
            result.add(new Option("-show_data_hash", showDataHash));
        }
        if (showError) {
            result.add(new Option("-show_error"));
        }
        if (showFormat) {
            result.add(new Option("-show_format"));
        }
        if (showFormatEntry != null) {
            result.add(new Option("-show_format_entry", showFormatEntry));
        }
        if (showEntries != null) {
            result.add(new Option("-show_entries", showEntries));
        }
        if (showPackets) {
            result.add(new Option("-show_packets"));
        }
        if (showFrames) {
            result.add(new Option("-show_frames"));
        }
        if (showLog != null) {
            result.add(new Option("-show_log", Integer.toString(showLog.code())));
        }
        if (showStreams) {
            result.add(new Option("-show_streams"));
        }
        if (showPrograms) {
            result.add(new Option("-show_programs"));
        }
        if (showChapters) {
            result.add(new Option("-show_chapters"));
        }
        if (countFrames) {
            result.add(new Option("-count_frames"));
        }
        if (countPackets) {
            result.add(new Option("-count_packets"));
        }
        if (readIntervals != null) {
            result.add(new Option("-read_intervals", readIntervals));
        }
        if (showPrivateData) {
            result.add(new Option("-show_private_data"));
        } else {
            result.add(new Option("-noprivate"));
        }
        if (showProgramVersion) {
            result.add(new Option("-show_program_version"));
        }
        if (showLibraryVersions) {
            result.add(new Option("-show_library_versions"));
        }
        if (showVersions) {
            result.add(new Option("-show_versions"));
        }
        if (showPixelFormats) {
            result.add(new Option("-show_pixel_formats"));
        }

        if (inputPath != null) {
            result.add(new Option("-i", inputPath.toString()));
        }

        return result;
    }

    protected StdReader<FFprobeResult> createStdOutReader() {
        return new StdReader<FFprobeResult>() {
            @Override
            public FFprobeResult read(InputStream stdOut) {
                return JAXB.unmarshal(stdOut, FFprobeResult.class);
            }
        };
    }

    protected StdReader<FFprobeResult> createStdErrReader() {
        return new LoggingStdReader<>();
    }

    public static FFprobe atPath(Path pathToDir) {
        String os = System.getProperty("os.name");
        if (os == null) {
            throw new RuntimeException("Failed to detect OS");
        }

        Path executable;
        if (os.toLowerCase().contains("win")) {
            executable = pathToDir.resolve("ffprobe.exe");
        } else {
            executable = pathToDir.resolve("ffprobe");
        }

        return new FFprobe(executable);
    }

}
