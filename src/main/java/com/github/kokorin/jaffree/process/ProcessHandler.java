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

package com.github.kokorin.jaffree.process;

import com.github.kokorin.jaffree.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProcessHandler<T> {
    private final Path executable;
    private StdWriter stdInWriter = null;
    private StdReader<T> stdOutReader = new LoggingStdReader<>();
    private StdReader<Void> stdErrReader = new LoggingStdReader<>();
    private boolean redirectErrToOut = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessHandler.class);

    public ProcessHandler(Path executable) {
        this.executable = executable;
    }

    public ProcessHandler<T> setStdInWriter(StdWriter stdInWriter) {
        this.stdInWriter = stdInWriter;
        return this;
    }

    public ProcessHandler<T> setStdOutReader(StdReader<T> stdOutReader) {
        this.stdOutReader = stdOutReader;
        return this;
    }

    public ProcessHandler<T> setStdErrReader(StdReader<Void> stdErrReader) {
        this.stdErrReader = stdErrReader;
        return this;
    }

    public  ProcessHandler<T> setRedirectErrToOut(boolean redirectErrToOut) {
        this.redirectErrToOut = redirectErrToOut;
        return this;
    }

    public T execute(List<Option> options) {
        List<String> command = buildCommand(executable, options);

        Process process;
        try {
            LOGGER.info("Starting process: {}", executable);
            process = new ProcessBuilder(command)
                    .redirectErrorStream(redirectErrToOut)
                    .start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start process.", e);
        }

        T result = null;
        final AtomicReference<Exception> exceptionRef = new AtomicReference<>();
        int status = 0;
        Thread stdInThread = null;
        Thread stdErrThread = null;

        LOGGER.debug("Reading of stdout and stderr");

        try (InputStream stdOut = process.getInputStream();
             final InputStream stdErr = process.getErrorStream();
             final OutputStream stdIn = process.getOutputStream()) {

            stdErrThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Exception exception = null;
                    try {
                        stdErrReader.read(stdErr);
                    } catch (Exception e) {
                        exception = e;
                    }
                    exceptionRef.set(exception);
                }
            }, "stderr reader");
            LOGGER.debug("Starting thread for reading stderr");
            stdErrThread.start();

            if (stdInWriter != null) {
                stdInThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        stdInWriter.write(stdIn);
                    }
                }, "stdin writer");
                LOGGER.debug("Starting thread for writing stdin");
                stdErrThread.start();
            }

            result = stdOutReader.read(stdOut);

            if (stdInThread != null) {
                LOGGER.debug("Waiting for thread to join current thread");
                stdInThread.join();
            }
            if (stdErrThread != null) {
                LOGGER.debug("Waiting for thread to join current thread");
                stdErrThread.join();
            }

            LOGGER.debug("Waiting process to finish");
            status = process.waitFor();
        } catch (Exception e) {
            if (stdInThread != null) {
                stdInThread.interrupt();
            }
            if (stdErrThread != null) {
                stdErrThread.interrupt();
            }
            if (process != null) {
                process.destroy();
            }
            throw new RuntimeException("Failed to handle process execution", e);
        }

        Exception exception = exceptionRef.get();
        if (status == 0) {
            if (exception != null) {
                LOGGER.warn("Process execution has ended successfully, but exception has been caught: ", exception);
            }
            return result;
        }

        if (result != null) {
            LOGGER.warn("Process has ended with non zero status: {}", status, exception);
            return result;
        }

        throw new RuntimeException("Process has ended with no ffprobe and non zero status: " + status, exception);
    }

    public static <T> ProcessHandler<T> forExecutable(Path executable) {
        return new ProcessHandler<>(executable);
    }

    protected static List<String> buildCommand(Path executable, List<Option> options) {
        LOGGER.debug("Constructing command");
        List<String> result = new ArrayList<>();

        result.add(executable.toString());

        for (Option option : options) {
            result.add(option.getName());
            if (option.getValue() != null) {
                result.add(option.getValue());
            }
        }

        if (LOGGER.isInfoEnabled()) {
            StringBuilder commandBuilder = new StringBuilder();
            boolean first = true;
            for (String argument : result) {
                if (!first) {
                    commandBuilder.append(" ");
                }
                String quote = argument.contains(" ") ? "\"" : "";
                commandBuilder.append(quote).append(argument).append(quote);
                first = false;
            }
            LOGGER.info("Command constructed:\n{}", commandBuilder.toString());
        }

        return result;
    }
}
