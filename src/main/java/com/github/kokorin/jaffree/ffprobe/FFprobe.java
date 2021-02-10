/*
 *    Copyright 2017-2021 Denis Kokorin
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
import com.github.kokorin.jaffree.process.ProcessHelper;
import com.github.kokorin.jaffree.process.LoggingStdReader;
import com.github.kokorin.jaffree.process.ProcessHandler;
import com.github.kokorin.jaffree.process.StdReader;
import com.github.kokorin.jaffree.process.ThrowingStdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * {@link FFprobe} provides an ability to execute ffprobe process.
 */
//TODO add debug statements for all methods
public class FFprobe {
    // TODO why final?
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

    // TODO: make it final?
    private Input input;

    private FormatParser formatParser = new FlatFormatParser();

    private final Path executable;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFprobe.class);

    /**
     * Creates {@link FFprobe}.
     *
     * @param executable path to ffprobe binary
     */
    public FFprobe(final Path executable) {
        this.executable = executable;
    }

    /**
     * Adds custom argument to ffprobe arguments list.
     * <p>
     * <b>Note:</b> if value contains spaces it <b>should not</b> be wrapped
     * with quotes. Also spaces <b>should not</b> be escaped with backslash
     *
     * @param argument argument
     * @return this
     */
    public FFprobe addArgument(final String argument) {
        additionalArguments.add(argument);
        return this;
    }

    /**
     * Adds custom arguments to ffprobe arguments list.
     * <p>
     * <b>Note:</b> if value contains spaces it <b>should not</b> be wrapped
     * with quotes. Also spaces <b>should not</b> be escaped with backslash
     *
     * @param key   key argument
     * @param value value argument
     * @return this
     */
    public FFprobe addArguments(final String key, final String value) {
        additionalArguments.addAll(Arrays.asList(key, value));
        return this;
    }

    /**
     * Select only the streams specified by stream_specifier.
     * <p>
     * This option affects only the options related to streams (e.g. show_streams,
     * show_packets, etc.).
     *
     * @param streamSpecifier stream specifier
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg.html#Stream-specifiers">
     * stream specifiers</a>
     * @see com.github.kokorin.jaffree.StreamSpecifier
     */
    public FFprobe setSelectStreams(final String streamSpecifier) {
        this.selectStreams = streamSpecifier;
        return this;
    }

    /**
     * Select only the streams of the specified type.
     *
     * @param streamType stream type
     * @return this
     */
    public FFprobe setSelectStreams(final StreamType streamType) {
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
     * @param showData true to show data
     * @return this
     */
    public FFprobe setShowData(final boolean showData) {
        this.showData = showData;
        return this;
    }

    /**
     * Show private data, that is data depending on the format of the particular shown element.
     * This option is enabled by default, but you may need to disable it for specific uses.
     *
     * @param showPrivateData true to show private data
     * @return this
     */
    public FFprobe setShowPrivateData(final boolean showPrivateData) {
        this.showPrivateData = showPrivateData;
        return this;
    }

    /**
     * Show a hash of payload data, for packets with -show_packets and for codec extradata
     * with -show_streams.
     *
     * @param algorithm algorithm to calculate hash
     * @return this
     */
    public FFprobe setShowDataHash(final String algorithm) {
        this.showDataHash = algorithm;
        return this;
    }

    /**
     * Show information about the container format of the input multimedia stream.
     *
     * @param showFormat true to show format
     * @return this
     */
    public FFprobe setShowFormat(final boolean showFormat) {
        this.showFormat = showFormat;
        return this;
    }

    /**
     * Like -show_format, but only prints the specified entry of the container format information,
     * rather than all.
     * <p>
     * This option may be given more than once, then all specified entries will be shown.
     *
     * @param showFormatEntry
     * @return this
     * @see #setShowEntries(String)
     * @deprecated This option is deprecated, use show_entries instead.
     */
    // TODO remove since with programmatic approach it's possible to get specific entries
    public FFprobe setShowFormatEntry(final String showFormatEntry) {
        this.showFormatEntry = showFormatEntry;
        return this;
    }

    /**
     * Set list of entries to show.
     * <p>
     * Entries are specified according to the following syntax. section_entries contains a list of
     * section entries separated by :.
     * Each section entry is composed by a section name (or unique name),
     * optionally followed by a list of entries local to that section, separated by ,.
     * <p>
     * Note that the order of specification of the local section entries is not honored in
     * the output, and the usual display order will be retained.
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
     * @param showEntries list entries syntax
     * @return this
     */
    // TODO remove since with programmatic approach it's possible to get specific entries
    public FFprobe setShowEntries(final String showEntries) {
        this.showEntries = showEntries;
        return this;
    }

    /**
     * Show information about each packet contained in the input multimedia stream.
     * <p>
     * Requires -show_streams
     *
     * @param showPackets true to show packets
     * @return this
     * @see #setShowStreams
     */
    public FFprobe setShowPackets(final boolean showPackets) {
        this.showPackets = showPackets;
        return this;
    }

    /**
     * Show information about each frame and subtitle contained in the input multimedia stream.
     *
     * @param showFrames true to show frames
     * @return this
     */
    public FFprobe setShowFrames(final boolean showFrames) {
        this.showFrames = showFrames;
        return this;
    }

    /**
     * Show logging information from the decoder about each frame according to the value
     * set in loglevel, (see -loglevel).
     * <p>
     * This option requires -show_frames.
     *
     * @param showLogLevel decoder log level
     * @return this
     * @see #setShowFrames(boolean)
     */
    public FFprobe setShowLog(final LogLevel showLogLevel) {
        this.showLog = showLogLevel;
        return this;
    }

    /**
     * Show information about each media stream contained in the input multimedia stream.
     *
     * @param showStreams true to show streams
     * @return this
     */
    public FFprobe setShowStreams(final boolean showStreams) {
        this.showStreams = showStreams;
        return this;
    }

    /**
     * Show information about programs and their streams contained in the input multimedia stream.
     *
     * @param showPrograms true to show programs
     * @return this
     */
    public FFprobe setShowPrograms(final boolean showPrograms) {
        this.showPrograms = showPrograms;
        return this;
    }

    /**
     * Show information about chapters stored in the format.
     *
     * @param showChapters true to show chapters
     * @return this
     */
    public FFprobe setShowChapters(final boolean showChapters) {
        this.showChapters = showChapters;
        return this;
    }

    /**
     * Count the number of frames per stream and report it in the corresponding stream section.
     * <p>
     * Requires -show_streams
     *
     * @param countFrames true to count frames
     * @return this
     * @see #setShowStreams
     */
    public FFprobe setCountFrames(final boolean countFrames) {
        this.countFrames = countFrames;
        return this;
    }

    /**
     * Count the number of packets per stream and report it in the corresponding stream section.
     * <p>
     * Requires -show_streams
     *
     * @param countPackets true to count packets
     * @return this
     */
    public FFprobe setCountPackets(final boolean countPackets) {
        this.countPackets = countPackets;
        return this;
    }

    /**
     * Read only the specified intervals. read_intervals must be a sequence of interval
     * specifications separated by ",".
     * <p>
     * ffprobe will seek to the interval starting point, and will continue reading from that.
     * <p>
     * The formal syntax is given by:
     * <p>
     * INTERVAL  ::= [START|+START_OFFSET][%[END|+END_OFFSET|+#NUMBER_OF_FRAMES]]
     * <p>
     * INTERVALS ::= INTERVAL[,INTERVALS]
     *
     * @param intervals interval specification
     * @return this
     */
    public FFprobe setReadIntervals(final String intervals) {
        this.readIntervals = intervals;
        return this;
    }

    /**
     * Show information related to program version.
     *
     * @param showProgramVersion true to show program version
     * @return this
     */
    //TODO remove
    public FFprobe setShowProgramVersion(final boolean showProgramVersion) {
        this.showProgramVersion = showProgramVersion;
        return this;
    }

    /**
     * Show information related to library versions.
     *
     * @param showLibraryVersions true to show library version
     * @return this
     * @deprecated not actually in use
     */
    @Deprecated
    //TODO remove this method
    public FFprobe setShowLibraryVersions(final boolean showLibraryVersions) {
        this.showLibraryVersions = showLibraryVersions;
        return this;
    }

    /**
     * Show information related to program and library versions.
     * <p>
     * This is the equivalent of setting both -show_program_version and -show_library_versions
     *
     * @param showVersions true to show version
     * @return this
     */
    //TODO remove
    public FFprobe setShowVersions(final boolean showVersions) {
        this.showVersions = showVersions;
        return this;
    }

    /**
     * Show information about all pixel formats supported by FFmpeg.
     *
     * @param showPixelFormats true to show pixel formats
     * @return this
     */
    //TODO remove
    public FFprobe setShowPixelFormats(final boolean showPixelFormats) {
        this.showPixelFormats = showPixelFormats;
        return this;
    }

    /**
     * Set probing size (from 32 to I64_MAX) (default 5e+006).
     *
     * @param probeSize prpobe size in bytes
     * @return this
     */
    public FFprobe setProbeSize(final Long probeSize) {
        this.probeSize = probeSize;
        return this;
    }

    /**
     * Specify how many microseconds are analyzed to probe the input (from 0 to I64_MAX).
     * (default 0).
     *
     * @param analyzeDurationMicros analyze duration micros
     * @return this
     */
    public FFprobe setAnalyzeDuration(final Long analyzeDurationMicros) {
        this.analyzeDuration = analyzeDurationMicros;
        return this;
    }

    /**
     * Specify how long to analyze to probe the input (from 0 to I64_MAX) (default 0).
     *
     * @param analyzeDurationInTimeUnit duration
     * @param timeUnit                  time unit
     * @return this
     */
    public FFprobe setAnalyzeDuration(final Number analyzeDurationInTimeUnit,
                                      final TimeUnit timeUnit) {
        long micros = (long) (analyzeDurationInTimeUnit.doubleValue() * timeUnit.toMicros(1));
        return setAnalyzeDuration(micros);
    }

    /**
     * Number of frames used to probe fps (from -1 to 2.14748e+009) (default -1).
     *
     * @param fpsProbeSize frames
     * @return this
     */
    public FFprobe setFpsProbeSize(final Long fpsProbeSize) {
        this.fpsProbeSize = fpsProbeSize;
        return this;
    }

    /**
     * Sets input to analyze with ffprobe.
     *
     * @param inputPath path to media file
     * @return this
     */
    public FFprobe setInput(final Path inputPath) {
        return setInput(inputPath.toString());
    }

    /**
     * Sets input to analyze with ffprobe.
     *
     * @param inputUriOrPath URI or path to media file
     * @return this
     */
    public FFprobe setInput(final String inputUriOrPath) {
        this.input = new UrlInput(inputUriOrPath);
        return this;
    }

    /**
     * Sets input to analyze with ffprobe.
     *
     * @param inputStream input stream to analyze
     * @return this
     */
    public FFprobe setInput(final InputStream inputStream) {
        this.input = PipeInput.pumpFrom(inputStream);
        return this;
    }

    /**
     * Sets input to analyze with ffprobe.
     *
     * @param inputStream input stream to analyze
     * @param bufferSize  buffer size to copy bytes from input stream
     * @return this
     */
    public FFprobe setInput(final InputStream inputStream, final int bufferSize) {
        this.input = PipeInput.pumpFrom(inputStream, bufferSize);
        return this;
    }

    /**
     * Sets input to analyze with ffprobe.
     *
     * @param inputChannel byte channel to analyze
     * @return this
     */
    //TODO add setInput(SeekableByteChannel, int) to allow custom buffer size
    public FFprobe setInput(final SeekableByteChannel inputChannel) {
        this.input = ChannelInput.fromChannel(inputChannel);
        return this;
    }

    /**
     * Sets ffprobe output format parser (and corresponding output format).
     * <p>
     * {@link FlatFormatParser} is used by default. It's possible to provide custom implementation.
     *
     * @param formatParser format parser
     * @return this
     */
    public FFprobe setFormatParser(final FormatParser formatParser) {
        if (formatParser == null) {
            throw new IllegalArgumentException("Parser must be non null");
        }

        this.formatParser = formatParser;
        return this;
    }

    public Future<FFprobeResult> executeAsync() {
        FutureTask<FFprobeResult> resultFuture = new FutureTask<>(
                new Callable<FFprobeResult>() {
                    @Override
                    public FFprobeResult call() throws Exception {
                        return execute();
                    }
                }
        );

        Thread runner = new Thread(resultFuture, "FFprobe-async-runner");
        runner.setDaemon(true);
        runner.start();

        return resultFuture;
    }

    /**
     * Starts synchronous ffprobe execution.
     * <p>
     * Current thread is blocked until ffprobe is finished.
     *
     * @return ffprobe result
     */
    public FFprobeResult execute() {
        List<ProcessHelper> helpers = new ArrayList<>();
        if (input != null) {
            ProcessHelper helper = input.helperThread();
            if (helper != null) {
                helpers.add(helper);
            }
        }

        return new ProcessHandler<FFprobeResult>(executable, null)
                .setStdOutReader(createStdOutReader())
                .setStdErrReader(createStdErrReader())
                .setHelpers(helpers)
                .setArguments(buildArguments())
                .execute();
    }

    /**
     * Constructs ffprobe command line.
     *
     * @return arguments list
     */
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

        result.addAll(Arrays.asList("-print_format", formatParser.getFormatName()));

        result.addAll(additionalArguments);

        if (input != null) {
            result.addAll(Arrays.asList("-i", input.getUrl()));
        }

        return result;
    }

    /**
     * Creates {@link StdReader} which is used to read ffprobe stdout.
     * <p>
     * Note: default implementation uses {@link FormatParser} to parse output.
     *
     * @return this
     */
    protected StdReader<FFprobeResult> createStdOutReader() {
        return new FFprobeResultReader(formatParser);
    }

    /**
     * Creates {@link StdReader} which is used to read ffprobe stderr.
     * <p>
     * Note: default implementation simply logs everything with SLF4J.
     *
     * @return this
     */
    protected StdReader<FFprobeResult> createStdErrReader() {
        // TODO check if we need below clause
        if (logLevel.code() > LogLevel.WARNING.code()) {
            return new LoggingStdReader<>();
        }

        return new ThrowingStdReader<>();
    }

    /**
     * Creates {@link FFprobe}.
     * <p>
     * Note: directory with ffprobe binaries must be in PATH environment variable.
     *
     * @return FFprobe
     */
    public static FFprobe atPath() {
        return atPath(null);
    }

    /**
     * Creates {@link FFprobe}.
     *
     * @param pathToDir path to ffprobe directory
     * @return FFprobe
     */
    public static FFprobe atPath(final Path pathToDir) {
        final Path executable;
        if (pathToDir != null) {
            executable = pathToDir.resolve("ffprobe");
        } else {
            executable = Paths.get("ffprobe");
        }

        return new FFprobe(executable);
    }

}
