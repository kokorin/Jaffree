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

import com.github.kokorin.jaffree.Option;
import com.github.kokorin.jaffree.process.ProcessHandler;
import com.github.kokorin.jaffree.process.StdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FFmpeg {
    private final List<Input> inputs = new ArrayList<>();
    private final List<Output> outputs = new ArrayList<>();
    private final List<Option> additionalOptions = new ArrayList<>();
    private boolean overwriteOutput;
    private ProgressListener progressListener;
    //-progress url (global)
    //-filter_threads nb_threads (global)
    //-debug_ts (global)
    private FilterGraph complexFilter;

    private StdReader<FFmpegResult> stdOutReader;
    private StdReader<FFmpegResult> stdErrReader;

    private final Path executable;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpeg.class);

    public FFmpeg(Path executable) {
        this.executable = executable;
    }

    public FFmpeg addInput(Input input) {
        inputs.add(input);
        return this;
    }

    public FFmpeg addOption(Option option) {
        additionalOptions.add(option);
        return this;
    }

    public FFmpeg setComplexFilter(FilterGraph graph) {
        this.complexFilter = graph;
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

    public FFmpeg setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public void setStdOutReader(StdReader<FFmpegResult> stdOutReader) {
        this.stdOutReader = stdOutReader;
    }

    public void setStdErrReader(StdReader<FFmpegResult> stdErrReader) {
        this.stdErrReader = stdErrReader;
    }

    public FFmpegResult execute() {
        for (Output output : outputs) {
            output.beforeExecute(this);
        }

        ProcessHandler<FFmpegResult> processHandler = ProcessHandler.<FFmpegResult>forExecutable(executable);

        if (stdErrReader != null) {
            processHandler.setStdErrReader(stdErrReader);
        }

        if (stdOutReader != null) {
            processHandler.setStdOutReader(stdOutReader);
        }

        return processHandler.execute(buildOptions());
    }

    protected List<Option> buildOptions() {
        List<Option> result = new ArrayList<>();

        for (Input input : inputs) {
            List<Option> inputOptions = input.buildOptions();
            if (inputOptions != null) {
                result.addAll(inputOptions);
            }
        }

        if (overwriteOutput) {
            //Overwrite output files without asking.
            result.add(new Option("-y"));
        } else {
            // Do not overwrite output files, and exit immediately if a specified output file already exists.
            result.add(new Option("-n"));
        }

        if (complexFilter != null) {
            result.add(new Option("-filter_complex", complexFilter.getValue()));
        }

        result.addAll(additionalOptions);

        for (Output output : outputs) {
            List<Option> outputOptions = output.buildOptions();
            if (outputOptions != null) {
                result.addAll(outputOptions);
            }
        }

        return result;
    }

    public static FFmpeg atPath(Path pathToDir) {
        String os = System.getProperty("os.name");
        if (os == null) {
            throw new RuntimeException("Failed to detect OS");
        }

        Path executable;
        if (os.toLowerCase().contains("win")) {
            executable = pathToDir.resolve("ffmpeg.exe");
        } else {
            executable = pathToDir.resolve("ffmpeg");
        }

        return new FFmpeg(executable);
    }
}
