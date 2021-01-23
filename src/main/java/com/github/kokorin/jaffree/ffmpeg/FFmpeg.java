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

package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.process.FFHelper;
import com.github.kokorin.jaffree.process.LoggingStdReader;
import com.github.kokorin.jaffree.process.ProcessHandler;
import com.github.kokorin.jaffree.process.StdReader;
import com.github.kokorin.jaffree.process.Stopper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * {@link FFmpeg} provides an ability to start & stop ffmpeg process and keep track of
 * encoding progress.
 */
//TODO add debug statements for all methods
public class FFmpeg {
    private final List<Input> inputs = new ArrayList<>();
    private final List<Output> outputs = new ArrayList<>();
    private final List<String> additionalArguments = new ArrayList<>();
    //TODO make it Boolean (non-primitive)
    private boolean overwriteOutput;
    private ProgressListener progressListener;
    private OutputListener outputListener;
    //-progress url (global)
    //-filter_threads nb_threads (global)
    //-debug_ts (global)
    private FilterGraph complexFilter;

    /**
     * A map with 0 or 1 filter per stream type. Type can be "a" (audio), "v" (video) or "" (plain 'filter')
     */
    private final Map<String, Object> filters = new HashMap<>();

    private LogLevel logLevel = null;
    private String contextName = null;

    private final Path executable;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpeg.class);

    /**
     * Creates {@link FFmpeg}.
     *
     * @param executable path to ffmpeg binary
     */
    public FFmpeg(final Path executable) {
        this.executable = executable;
    }

    /**
     * Adds arguments (provided by input parameter) to ffmpeg arguments list.
     * <p>
     * Note: the order matters.
     *
     * @param input input
     * @return this
     * @see Input
     * @see UrlInput
     * @see ChannelInput
     * @see FrameInput
     * @see PipeInput
     */
    public FFmpeg addInput(final Input input) {
        inputs.add(input);
        return this;
    }

    /**
     * Adds arguments (provided by output parameter) to ffmpeg arguments list.
     * <p>
     * Note: the order matters.
     *
     * @param output output
     * @return this
     * @see Output
     * @see UrlOutput
     * @see ChannelOutput
     * @see FrameOutput
     * @see PipeOutput
     */
    public FFmpeg addOutput(final Output output) {
        outputs.add(output);
        return this;
    }

    /**
     * Adds custom global argument to ffmpeg arguments list.
     * <p>
     * <b>Note:</b> if value contains spaces it <b>should not</b> be wrapped
     * with quotes. Also spaces <b>should not</b> be escaped with backslash
     *
     * @param argument argument
     * @return this
     */
    public FFmpeg addArgument(final String argument) {
        additionalArguments.add(argument);
        return this;
    }

    /**
     * Adds custom global arguments to ffmpeg arguments list.
     * <p>
     * <b>Note:</b> if value contains spaces it <b>should not</b> be wrapped
     * with quotes. Also spaces <b>should not</b> be escaped with backslash
     *
     * @param key   key argument
     * @param value value argument
     * @return this
     */
    public FFmpeg addArguments(final String key, final String value) {
        additionalArguments.addAll(Arrays.asList(key, value));
        return this;
    }

    /**
     * Adds complex filter graph to ffmpeg arugments list.
     * <p>
     * Complex filtergraphs are those which cannot be described as simply a linear processing chain
     * applied to one stream. This is the case, for example, when the graph has more than one input
     * and/or output, or when output stream type is different from input.
     *
     * @param graph complex filter graph
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#toc-Filtergraph-syntax-1">
     * Filtergraph syntax</a>
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Complex-filtergraphs">
     * Complex filtergraph</a>
     */
    // TODO overload with String parameter
    public FFmpeg setComplexFilter(final FilterGraph graph) {
        this.complexFilter = graph;
        return this;
    }

    /**
     * Sets the 'generic' filter value (equivalent to the "-filter" command-line parameter).
     *
     * @param filter a String describing the filter to apply
     * @return this
     *
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(String filter) {
        return setFilter("", filter);
    }

    /**
     * Sets the 'generic' filter value (equivalent to the "-filter" command-line parameter).
     *
     * @param filter a FilterGraph describing the filter to apply
     * @return this
     *
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(FilterGraph filter) {
        return setFilter("", filter.getValue());
    }

    /**
     * Sets a 'stream specific' filter value (equivalent to the "-av" / "-filter:a" or "-fv" / "-filter:v" command-line parameters).
     *
     * @param streamType  the stream type to apply this filter to (StreamType.AUDIO or StreamType.VIDEO)
     * @param filterGraph a graph describing the filters to apply
     * @return this
     *
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(StreamType streamType, FilterGraph filterGraph) {
        return setFilter(streamType.code(), filterGraph.getValue());
    }

    /**
     * Sets a 'stream specific' filter value (equivalent to the "-av" / "-filter:a" or "-fv" / "-filter:v" command-line parameters).
     *
     * @param streamType the stream type to apply this filter to (StreamType.AUDIO or StreamType.VIDEO)
     * @param filter     a String describing the filter to apply
     * @return this
     *
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(StreamType streamType, String filter) {
        return setFilter(streamType.code(), filter);
    }

    /**
     * Sets a 'stream specific' filter value (equivalent to the "-av" / "-filter:a" or "-fv" / "-filter:v" / "-filter" command-line parameters).
     *
     * @param streamSpecifier a String specifying to which stream this filter must be applied ("a" for audio, "v" "for video, or "" for generic 'filter')
     * @param filterGraph     a graph describing the filters to apply
     * @return this
     *
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(String streamSpecifier, FilterGraph filterGraph) {
        return setFilter(streamSpecifier, filterGraph.getValue());
    }

    /**
     * Sets a 'stream specific' filter value (equivalent to the "-av" / "-filter:a" or "-fv" / "-filter:v" / "-filter" command-line parameters).
     *
     * @param streamSpecifier a String specifying to which stream this filter must be applied ("a" for audio, "v" "for video, or "" for generic 'filter')
     * @param filter          a String describing the filter to apply
     * @return this
     *
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(String streamSpecifier, String filter) {
        // If a previous filter was set, warn that it is replaced by the new one
        final String previousFilter = (String) filters.get(streamSpecifier);
        if (previousFilter != null) {
            if (streamSpecifier.isEmpty()) {
                LOGGER.warn("Only one generic filter is supported. Ignoring previous filter '" + previousFilter + "'.");
            } else {
                LOGGER.error("Only one filter per stream is supported. Ignoring previous filter '" + previousFilter + "' for stream '" + streamSpecifier + "'.");
            }
        }
        // Store the new filter
        filters.put(streamSpecifier, filter);
        return this;
    }


    /**
     * Whether to overwrite output. False by default.
     * <p>
     * If overwriteOutput is false, ffmpeg will stop with an error if output file exists.
     *
     * @param overwriteOutput true to overwrite output
     * @return this
     */
    public FFmpeg setOverwriteOutput(final boolean overwriteOutput) {
        this.overwriteOutput = overwriteOutput;
        return this;
    }

    /**
     * Supply custom ProgressListener to receive progress events.
     * <p>
     * Usually ffmpeg reports encoding progress every second.
     *
     * @param progressListener progress listener
     * @return this
     */
    public FFmpeg setProgressListener(final ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    /**
     * Supply custom OutputListener to receive ffmpeg output.
     * <p>
     * Some ffmpeg filters cause extra output. Any line in ffmpeg output that doesn't represent
     * encoding progress or encoding result  will be passed to {@link OutputListener}
     *
     * @param outputListener output listener
     * @return this
     * @see FFmpegResultReader
     */
    public FFmpeg setOutputListener(final OutputListener outputListener) {
        this.outputListener = outputListener;
        return this;
    }

    /**
     * Sets ffmpeg logging level.
     * <p>
     * Note: for message to appear in SLF4J logging it's required to configure appropriate
     * log level for SLF4J.
     *
     * @param logLevel log level
     * @return this
     */
    public FFmpeg setLogLevel(final LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    /**
     * Set context name to prepend all log messages.
     * <p>
     * Makes logs more clear in case of multiple ffmpeg processes running simultaneously
     *
     * @param contextName context name
     * @return this
     */
    public FFmpeg setContextName(final String contextName) {
        this.contextName = contextName;
        return this;
    }

    /**
     * Starts synchronous ffmpeg execution.
     * <p>
     * Current thread is blocked until ffmpeg is finished.
     *
     * @return ffmpeg result
     */
    public FFmpegResult execute() {
        ProcessHandler<FFmpegResult> processHandler = createProcessHandler();
        return processHandler.execute();
    }

    /**
     * Starts asynchronous ffmpeg execution.
     * <p>
     *
     * @return ffmpeg result future
     */
    public FFmpegResultFuture executeAsync() {
        final ProcessHandler<FFmpegResult> processHandler = createProcessHandler();
        Stopper stopper = createStopper();
        processHandler.setStopper(stopper);

        FutureTask<FFmpegResult> resultFuture = new FutureTask<>(new Callable<FFmpegResult>() {
            @Override
            public FFmpegResult call() throws Exception {
                return processHandler.execute();
            }
        });

        Thread runner = new Thread(resultFuture, "FFmpeg-async-runner");
        runner.setDaemon(true);
        runner.start();

        return new FFmpegResultFuture(resultFuture, stopper);
    }

    protected ProcessHandler<FFmpegResult> createProcessHandler() {
        List<FFHelper> helpers = new ArrayList<>();

        for (Input input : inputs) {
            FFHelper helper = input.helperThread();
            if (helper != null) {
                helpers.add(helper);
            }
        }
        for (Output output : outputs) {
            FFHelper helper = output.helperThread();
            if (helper != null) {
                helpers.add(helper);
            }
        }

        return new ProcessHandler<FFmpegResult>(executable, contextName)
                .setStdErrReader(createStdErrReader())
                .setStdOutReader(createStdOutReader())
                .setHelpers(helpers)
                .setArguments(buildArguments());
    }

    protected Stopper createStopper() {
        return new FFmpegStopper();
    }

    /**
     * Creates {@link StdReader} which is used to read ffmpeg stderr.
     * <p>
     * Note: should be overridden wisely: otherwise {@link FFmpeg} may produce wrong result or
     * even produce an error.
     *
     * @return this
     */
    protected StdReader<FFmpegResult> createStdErrReader() {
        return new FFmpegResultReader(progressListener, outputListener);
    }

    /**
     * Creates {@link StdReader} which is used to read ffmpeg stderr.
     * <p>
     * Note: default implementation simply logs everything with SLF4J.
     *
     * @return this
     */
    protected StdReader<FFmpegResult> createStdOutReader() {
        return new LoggingStdReader<>();
    }

    /**
     * Constructs ffmpeg command line.
     * <p>
     * Arguments order is as follows:
     * <ol>
     *     <li>arguments for each {@link Input}</li>
     *     <li>global arguments</li>
     *     <li>arguments for each {@link Output}</li>
     * </ol>
     *
     * @return arguments list
     */
    protected List<String> buildArguments() {
        List<String> result = new ArrayList<>();

        if (logLevel != null) {
            if (progressListener != null && logLevel.code() < LogLevel.INFO.code()) {
                throw new RuntimeException("Specified log level " + logLevel
                        + " hides ffmpeg progress output");
            }
            result.addAll(Arrays.asList("-loglevel", Integer.toString(logLevel.code())));
        }

        for (Input input : inputs) {
            result.addAll(input.buildArguments());
        }

        if (overwriteOutput) {
            //Overwrite output files without asking.
            result.add("-y");
        } else {
            // Do not overwrite output files, and exit immediately if a specified output file
            // already exists.
            result.add("-n");
        }

        if (complexFilter != null) {
            result.addAll(Arrays.asList("-filter_complex", complexFilter.getValue()));
        }

        result.addAll(BaseInOut.toArguments("-filter", filters));

        result.addAll(additionalArguments);

        for (Output output : outputs) {
            result.addAll(output.buildArguments());
        }

        return result;
    }

    /**
     * Creates {@link FFmpeg}.
     * <p>
     * Note: directory with ffmpeg binaries must be in PATH environment variable.
     *
     * @return FFmpeg
     */
    public static FFmpeg atPath() {
        return atPath(null);
    }

    /**
     * Creates {@link FFmpeg}.
     *
     * @param pathToDir path to ffmpeg directory
     * @return FFmpeg
     */
    public static FFmpeg atPath(final Path pathToDir) {
        final Path executable;
        if (pathToDir != null) {
            executable = pathToDir.resolve("ffmpeg");
        } else {
            executable = Paths.get("ffmpeg");
        }

        return new FFmpeg(executable);
    }
}
