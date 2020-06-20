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

package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.process.LoggingStdReader;
import com.github.kokorin.jaffree.process.ProcessHandler;
import com.github.kokorin.jaffree.process.StdReader;
import com.github.kokorin.jaffree.process.StdWriter;
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
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class FFmpeg {
    private final List<Input> inputs = new ArrayList<>();
    private final List<Output> outputs = new ArrayList<>();
    private final List<String> additionalArguments = new ArrayList<>();
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
    private final Map<String,Object> filters = new HashMap<>();

    private LogLevel logLevel = null;
    private String contextName = null;

    private final Path executable;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpeg.class);

    public FFmpeg(Path executable) {
        this.executable = executable;
    }

    public FFmpeg addInput(Input input) {
        inputs.add(input);
        return this;
    }

    public FFmpeg addArgument(String argument) {
        additionalArguments.add(argument);
        return this;
    }

    public FFmpeg addArguments(String key, String value) {
        additionalArguments.addAll(Arrays.asList(key, value));
        return this;
    }

    public FFmpeg setComplexFilter(FilterGraph graph) {
        this.complexFilter = graph;
        return this;
    }

    /**
     * Sets the 'generic' filter value (equivalent to the "-filter" command-line parameter).
     *
     * @param filter a String describing the filter to apply
     * @return this
     */
    public FFmpeg setFilter(String filter) {
        return setFilter("", filter);
    }

    /**
     * Sets the 'generic' filter value (equivalent to the "-filter" command-line parameter).
     *
     * @param filter a FilterGraph describing the filter to apply
     * @return this
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
     */
    public FFmpeg setFilter(String streamSpecifier, String filter) {
        // If a previous filter was set, warn that it is replaced by the new one
        final String previousFilter = (String) filters.get(streamSpecifier);
        if (previousFilter != null) {
            if (streamSpecifier.isEmpty()) {
                LOGGER.warn("Only one generic filter is supported. Ignoring previous filter '" + previousFilter + "'.");
            }
            else {
                LOGGER.error("Only one filter per stream is supported. Ignoring previous filter '" + previousFilter + "' for stream '" + streamSpecifier + "'.");
            }
        }
        // Store the new filter
        filters.put(streamSpecifier, filter);
        return this;
    }

    public FFmpeg addOutput(Output output) {
        outputs.add(output);
        return this;
    }


    /**
     * Whether to overwrite or to stop. False by default.
     *
     * @param overwriteOutput true if forcibly overwrite, false if to stop
     * @return this
     */
    public FFmpeg setOverwriteOutput(boolean overwriteOutput) {
        this.overwriteOutput = overwriteOutput;
        return this;
    }

    /**
     * Supply custom ProgressListener to receive progress events
     * @param progressListener listener
     * @return this
     */
    public FFmpeg setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    /**
     * Supply custom OutputListener to receive ffmpeg output.
     * @param outputListener listener
     * @return this
     */
    public FFmpeg setOutputListener(OutputListener outputListener) {
        this.outputListener = outputListener;
        return this;
    }

    public FFmpeg setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    /**
     * Set context name to prepend all log messages. Makes logs more clear in case of multiple ffmpeg processes
     *
     * @param contextName context name
     * @return this
     */
    public FFmpeg setContextName(String contextName) {
        this.contextName = contextName;
        return this;
    }

    public FFmpegResult execute() {
        List<Runnable> helpers = new ArrayList<>();

        for (Input input : inputs) {
            Runnable helper = input.helperThread();
            if (helper != null) {
                helpers.add(helper);
            }
        }
        for (Output output : outputs) {
            Runnable helper = output.helperThread();
            if (helper != null) {
                helpers.add(helper);
            }
        }

        return new ProcessHandler<FFmpegResult>(executable, contextName)
                .setStdInWriter(createStdInWriter())
                .setStdErrReader(createStdErrReader())
                .setStdOutReader(createStdOutReader())
                .setRunnables(helpers)
                .setArguments(buildArguments())
                .execute();
    }

    /**
     * Runs ffmpeg in separate Thread.
     * <p>
     * <b>Note</b>: execution is started immediately, so invocation of <code>Future.cancel(false)</code> has no effect.
     * Use <code>Future.cancel(true)</code>
     *
     * @return ffmpeg result future
     */
    public Future<FFmpegResult> executeAsync() {

        List<Runnable> helpers = new ArrayList<>();

        for (Input input : inputs) {
            Runnable helper = input.helperThread();
            if (helper != null) {
                helpers.add(helper);
            }
        }
        for (Output output : outputs) {
            Runnable helper = output.helperThread();
            if (helper != null) {
                helpers.add(helper);
            }
        }

        final StopStdWriter stopStdWriter = new StopStdWriter();

        final ProcessHandler<FFmpegResult> processHandler = new ProcessHandler<FFmpegResult>(executable, contextName)
                .setStdInWriter(stopStdWriter)
                .setStdErrReader(createStdErrReader())
                .setStdOutReader(createStdOutReader())
                .setRunnables(helpers)
                .setArguments(buildArguments());

        Callable<FFmpegResult> callable = new Callable<FFmpegResult>() {
            @Override
            public FFmpegResult call() throws Exception {
                return processHandler.execute();
            }
        };

        final FutureTask<FFmpegResult> result = new FutureTask<FFmpegResult>(callable) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (!mayInterruptIfRunning) {
                    stopStdWriter.stop();
                    return true;
                }

                return super.cancel(mayInterruptIfRunning);
            }
        };

        Thread runner = new Thread(result, "FFmpeg-async-runner");
        runner.setDaemon(true);
        runner.start();

        return result;
    }

    protected StdWriter createStdInWriter() {
        return null;
    }

    protected StdReader<FFmpegResult> createStdErrReader() {
        return new FFmpegResultReader(progressListener, outputListener);
    }

    protected StdReader<FFmpegResult> createStdOutReader() {
        return new LoggingStdReader<>();
    }

    protected List<String> buildArguments() {
        List<String> result = new ArrayList<>();

        if (logLevel != null) {
            if (progressListener != null && logLevel.code() < LogLevel.INFO.code()) {
                throw new RuntimeException("Specified log level " + logLevel + " hides ffmpeg progress output");
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
            // Do not overwrite output files, and exit immediately if a specified output file already exists.
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

    public static FFmpeg atPath() {
        return atPath(null);
    }

    public static FFmpeg atPath(Path pathToDir) {
        final Path executable;
        if (pathToDir != null) {
            executable = pathToDir.resolve("ffmpeg");
        } else {
            executable = Paths.get("ffmpeg");
        }

        return new FFmpeg(executable);
    }
}
