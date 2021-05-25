/*
 *    Copyright 2017-2021 Denis Kokorin, Oded Arbel
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
import com.github.kokorin.jaffree.net.NegotiatingTcpServer;
import com.github.kokorin.jaffree.process.LoggingStdReader;
import com.github.kokorin.jaffree.process.ProcessHandler;
import com.github.kokorin.jaffree.process.ProcessHelper;
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
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * {@link FFmpeg} provides an ability to start &amp; stop ffmpeg process and keep track of
 * encoding progress.
 */
public class FFmpeg {
    private final List<Input> inputs = new ArrayList<>();
    private final List<Output> outputs = new ArrayList<>();
    private final List<String> additionalArguments = new ArrayList<>();
    private boolean overwriteOutput;
    private ProgressListener progressListener;
    private OutputListener outputListener;
    private String progress;
    //-filter_threads nb_threads (global)
    //-debug_ts (global)

    private String complexFilter;
    private final Map<String, Object> filters = new HashMap<>();

    private LogLevel logLevel = LogLevel.INFO;
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
     * @param complexFilter complex filter graph
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#toc-Filtergraph-syntax-1">
     * Filtergraph syntax</a>
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Complex-filtergraphs">
     * Complex filtergraph</a>
     */
    public FFmpeg setComplexFilter(final FilterGraph complexFilter) {
        return setComplexFilter(complexFilter.getValue());
    }

    /**
     * Adds complex filter graph to ffmpeg arguments list.
     * <p>
     * Complex filtergraphs are those which cannot be described as simply a linear processing chain
     * applied to one stream. This is the case, for example, when the graph has more than one input
     * and/or output, or when output stream type is different from input.
     *
     * @param complexFilter complex filter graph
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#toc-Filtergraph-syntax-1">
     * Filtergraph syntax</a>
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Complex-filtergraphs">
     * Complex filtergraph</a>
     */
    public FFmpeg setComplexFilter(final String complexFilter) {
        this.complexFilter = complexFilter;
        return this;
    }

    /**
     * Sets the 'generic' filter value (equivalent to the "-filter" command-line parameter).
     *
     * @param filter a FilterGraph describing the filter to apply
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(final FilterGraph filter) {
        return setFilter(filter.getValue());
    }

    /**
     * Sets the 'generic' filter value (equivalent to the "-filter" command-line parameter).
     *
     * @param filter a String describing the filter to apply
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(final String filter) {
        return setFilter((String) null, filter);
    }

    /**
     * Sets a 'stream specific' filter value (equivalent to the "-av" / "-filter:a" or "-fv" /
     * "-filter:v" command-line parameters).
     *
     * @param streamType  the stream type to apply this filter to
     * @param filterGraph a graph describing the filters to apply
     * @return this
     * @see <a href="http://ffmpeg.org/ffmpeg-all.html#toc-Stream-specifiers-1">Stream specifiers
     * </a>
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(final StreamType streamType, final FilterGraph filterGraph) {
        return setFilter(streamType, filterGraph.getValue());
    }

    /**
     * Sets a 'stream specific' filter value (equivalent to the "-av" / "-filter:a" or "-fv" /
     * "-filter:v" command-line parameters).
     *
     * @param streamType the stream type to apply this filter to
     * @param filter     a String describing the filter to apply
     * @return this
     * @see <a href="http://ffmpeg.org/ffmpeg-all.html#toc-Stream-specifiers-1">Stream specifiers
     * </a>
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(final StreamType streamType, final String filter) {
        return setFilter(streamType.code(), filter);
    }

    /**
     * Sets a 'stream specific' filter value (equivalent to the "-av" / "-filter:a" or "-fv" /
     * "-filter:v" / "-filter" command-line parameters).
     *
     * @param streamSpecifier a String specifying to which stream this filter must be applied
     *                        ("a" for audio, "v" "for video, or "" for generic 'filter')
     * @param filterGraph     a graph describing the filters to apply
     * @return this
     * @see <a href="http://ffmpeg.org/ffmpeg-all.html#toc-Stream-specifiers-1">Stream specifiers
     * </a>
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(final String streamSpecifier, final FilterGraph filterGraph) {
        return setFilter(streamSpecifier, filterGraph.getValue());
    }

    /**
     * Sets a 'stream specific' filter value (equivalent to the "-av" / "-filter:a" or "-fv" /
     * "-filter:v" / "-filter" command-line parameters).
     *
     * @param streamSpecifier a String specifying to which stream this filter must be applied
     *                        ("a" for audio, "v" "for video, or "" for generic 'filter')
     * @param filter          a String describing the filter to apply
     * @return this
     * @see <a href="http://ffmpeg.org/ffmpeg-all.html#toc-Stream-specifiers-1">Stream specifiers
     * </a>
     * @see <a href="https://ffmpeg.org/ffmpeg-all.html#Simple-filtergraphs">Simple filtergraphs</a>
     */
    public FFmpeg setFilter(final String streamSpecifier, final String filter) {
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
     * Send program-friendly progress information to url.
     * <p>
     * Progress information is written periodically and at the end of the encoding process. It is
     * made of "key=value" lines. key consists of only alphanumeric characters. The last key of
     * a sequence of progress information is always "progress".
     * <p>
     * This method is protected intentionally. One should use  {@link ProgressListener} to get
     * periodic progress reports.
     *
     * @param progress progress url
     * @see #setProgressListener(ProgressListener)
     */
    protected void setProgress(final String progress) {
        this.progress = progress;
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
        return createProcessHandler()
                .setStopper(createStopper())
                .execute();
    }

    /**
     * Starts asynchronous ffmpeg execution.
     *
     * @return ffmpeg result future
     */
    public FFmpegResultFuture executeAsync() {
        return executeAsync(new Executor() {
            @Override
            public void execute(final Runnable command) {
                Thread runner = new Thread(command, "FFmpeg-async-runner");
                runner.setDaemon(true);
                runner.start();
            }
        });
    }

    /**
     * Starts asynchronous ffmpeg execution, executed using the supplied Executor.
     *
     * @param executor the executor to use for asynchronous execution
     * @return ffmpeg result future
     */
    public FFmpegResultFuture executeAsync(final Executor executor) {
        final ProcessHandler<FFmpegResult> processHandler = createProcessHandler();
        Stopper stopper = createStopper();
        processHandler.setStopper(stopper);

        CompletableFuture<FFmpegResult> resultFuture = new CompletableFuture<FFmpegResult>() {
            @Override
            public boolean cancel(final boolean mayInterruptIfRunning) {
                if (mayInterruptIfRunning) {
                    stopper.forceStop();
                } else {
                    stopper.graceStop();
                }
                return completeExceptionally(new CancellationException());
            }
        };

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    resultFuture.complete(processHandler.execute());
                } catch (Throwable error) {
                    resultFuture.completeExceptionally(error);
                }
            }
        });

        return new FFmpegResultFuture(resultFuture, stopper);
    }

    /**
     * Creates {@link ProcessHandler} which executes ffmpeg command and starts specified
     * {@link ProcessHelper ProcessHelpers}.
     *
     * @return ProcessHandler
     */
    protected ProcessHandler<FFmpegResult> createProcessHandler() {
        List<ProcessHelper> helpers = new ArrayList<>();

        for (Input input : inputs) {
            ProcessHelper helper = input.helperThread();
            if (helper != null) {
                helpers.add(helper);
            }
        }
        for (Output output : outputs) {
            ProcessHelper helper = output.helperThread();
            if (helper != null) {
                helpers.add(helper);
            }
        }

        ProcessHelper progressHelper = createProgressHelper(progressListener);
        if (progressHelper != null) {
            helpers.add(progressHelper);
        }

        return new ProcessHandler<FFmpegResult>(executable, contextName)
                .setStdErrReader(createStdErrReader(outputListener))
                .setStdOutReader(createStdOutReader())
                .setHelpers(helpers)
                .setArguments(buildArguments());
    }

    /**
     * Creates {@link Stopper} which is used to stop ffmpeg gracefully and forcefully.
     *
     * @return Stopper
     */
    protected Stopper createStopper() {
        return new FFmpegStopper();
    }

    /**
     * Creates {@link StdReader} which is used to read ffmpeg stderr.
     * <p>
     * Note: should be overridden wisely: otherwise {@link FFmpeg} may produce wrong result or
     * even produce an error.
     *
     * @param listener output listener for non-progress-related ffmpeg output
     * @return this
     */
    protected StdReader<FFmpegResult> createStdErrReader(final OutputListener listener) {
        return new FFmpegResultReader(listener);
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
     * Creates {@link ProcessHelper} if required. It receives ffmpeg progress report, parses it
     * and passes to listener.
     *
     * @param listener progress listener
     * @return ProcessHelper, or null
     */
    protected ProcessHelper createProgressHelper(final ProgressListener listener) {
        NegotiatingTcpServer result = null;
        String progressReportUrl = null;

        if (listener != null) {
            result = NegotiatingTcpServer.onRandomPort(
                    new FFmpegProgressReader(listener)
            );
            progressReportUrl = "tcp://" + result.getAddressAndPort();
        }

        setProgress(progressReportUrl);

        return result;
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

        // "level" is required for ffmpeg to add [loglevel] to output lines
        String logLevelArgument = "level";
        if (logLevel != null) {
            logLevelArgument += "+" + logLevel.name().toLowerCase();
        }
        result.addAll(Arrays.asList("-loglevel", logLevelArgument));

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

        if (progress != null) {
            result.addAll(Arrays.asList("-progress", progress));
        } else {
            LOGGER.warn("ProgressListener isn't set, progress won't be reported");
        }

        if (complexFilter != null) {
            result.addAll(Arrays.asList("-filter_complex", complexFilter));
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
