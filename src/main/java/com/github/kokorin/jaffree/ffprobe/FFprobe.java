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
import com.github.kokorin.jaffree.ffprobe.data.FormatParser;
import com.github.kokorin.jaffree.ffprobe.data.JsonFormatParser;
import com.github.kokorin.jaffree.ffprobe.input.ChannelInput;
import com.github.kokorin.jaffree.ffprobe.input.Input;
import com.github.kokorin.jaffree.ffprobe.input.PipeInput;
import com.github.kokorin.jaffree.ffprobe.input.UrlInput;
import com.github.kokorin.jaffree.process.ProcessHandler;
import com.github.kokorin.jaffree.process.ProcessHelper;
import com.github.kokorin.jaffree.process.StdReader;

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
public class FFprobe {
    private LogLevel logLevel = LogLevel.INFO;

    private String selectStreams;
    private boolean showData;
    private boolean showPrivateData = true;
    private String showDataHash;
    private boolean showFormat;
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

    private Long probeSize;
    private Long analyzeDuration;
    private Long fpsProbeSize;

    private final List<String> additionalArguments = new ArrayList<>();

    private String format;
    private Input input;

    private FormatParser formatParser = new JsonFormatParser();

    private final Path executable;

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
     * <b>Note:</b> this option overwrites any &quot;show...&quot; set before, so this method
     * should not be used together with any of {@link #setShowFormat(boolean)},
     * {@link #setShowFrames(boolean)}, {@link #setShowPackets(boolean)},
     * {@link #setShowStreams(boolean)}, {@link #setShowChapters(boolean)} or
     * {@link #setShowPrograms(boolean)}
     *
     * @param showEntries list entries syntax
     * @return this
     */
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
     * @param showLog decoder log level
     * @return this
     * @see #setShowFrames(boolean)
     */
    public FFprobe setShowLog(final LogLevel showLog) {
        this.showLog = showLog;
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
     * Force input file format. The format is normally auto detected for input files,
     * so this option is not needed in most cases.
     *
     * @param format format
     * @return this
     */
    public FFprobe setFormat(final String format) {
        this.format = format;
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
        return setInput(UrlInput.fromUrl(inputUriOrPath));
    }

    /**
     * Sets input to analyze with ffprobe.
     *
     * @param inputStream input stream to analyze
     * @return this
     */
    public FFprobe setInput(final InputStream inputStream) {
        return setInput(PipeInput.pumpFrom(inputStream));
    }

    /**
     * Sets input to analyze with ffprobe.
     *
     * @param inputStream input stream to analyze
     * @param bufferSize  buffer size to copy bytes from input stream
     * @return this
     */
    public FFprobe setInput(final InputStream inputStream, final int bufferSize) {
        return setInput(PipeInput.pumpFrom(inputStream, bufferSize));
    }

    /**
     * Sets input to analyze with ffprobe.
     *
     * @param inputChannel byte channel to analyze
     * @return this
     */
    public FFprobe setInput(final SeekableByteChannel inputChannel) {
        return setInput(ChannelInput.fromChannel(inputChannel));
    }

    /**
     * Sets input to analyze with ffprobe.
     *
     * @param inputChannel byte channel to analyze
     * @param bufferSize   buffer size to copy bytes from input stream
     * @return this
     */
    public FFprobe setInput(final SeekableByteChannel inputChannel, final int bufferSize) {
        return setInput(ChannelInput.fromChannel(inputChannel, bufferSize));
    }

    /**
     * Sets input to analyze with ffprobe.
     *
     * @param input input to analyze
     * @return this
     */
    public FFprobe setInput(final Input input) {
        this.input = input;
        return this;
    }

    /**
     * Sets ffprobe output format parser (and corresponding output format).
     * <p>
     * {@link JsonFormatParser} is used by default. It's possible to provide custom implementation.
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

    /**
     * Sets ffprobe logging level.
     * <p>
     * Note: for message to appear in SLF4J logging it's required to configure appropriate
     * log level for SLF4J.
     *
     * @param logLevel log level
     * @return this
     */
    public FFprobe setLogLevel(final LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    /**
     * Starts asynchronous ffprobe execution.
     *
     * @return ffprobe result future
     */
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
                .setStdOutReader(createStdOutReader(formatParser))
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

        // "level" is required for ffmpeg to add [loglevel] to output lines
        String logLevelArgument = "level";
        if (logLevel != null) {
            logLevelArgument += "+" + logLevel.name().toLowerCase();
        }
        result.addAll(Arrays.asList("-loglevel", logLevelArgument));


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

        if (format != null) {
            result.addAll(Arrays.asList("-f", format));
        }

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
     * @param formatParser format parser to use
     * @return this
     */
    @SuppressWarnings("checkstyle:HiddenField")
    protected StdReader<FFprobeResult> createStdOutReader(final FormatParser formatParser) {
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
        return new FFprobeLogReader();
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
