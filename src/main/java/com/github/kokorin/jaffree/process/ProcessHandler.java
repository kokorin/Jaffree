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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ProcessHandler<T> {
    private final Path executable;
    private StdWriter stdInWriter = null;
    private StdReader<T> stdOutReader = new GobblingStdReader<>();
    private StdReader<T> stdErrReader = new GobblingStdReader<>();
    private boolean redirectErrToOut = false;
    private volatile boolean stopped = false;

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

    public ProcessHandler<T> setStdErrReader(StdReader<T> stdErrReader) {
        this.stdErrReader = stdErrReader;
        return this;
    }

    public ProcessHandler<T> setRedirectErrToOut(boolean redirectErrToOut) {
        this.redirectErrToOut = redirectErrToOut;
        return this;
    }

    public T execute(List<Option> options) {
        List<String> command = buildCommand(executable, options);

        final Process process;
        try {
            LOGGER.info("Starting process: {}", executable);
            process = new ProcessBuilder(command)
                    .redirectErrorStream(redirectErrToOut)
                    .start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start process.", e);
        }

        T result = null;
        final AtomicReference<T> resultRef = new AtomicReference<>();
        final AtomicReference<T> errResultRef = new AtomicReference<>();
        final AtomicReference<Exception> exceptionRef = new AtomicReference<>();
        final AtomicInteger workingThreadCount = new AtomicInteger();

        int status = -1;
        Thread stdInThread = null;
        Thread stdOutThread = null;
        Thread stdErrThread = null;

        LOGGER.debug("Starting io interaction with process");

        try (final InputStream stdOut = process.getInputStream();
             final InputStream stdErr = process.getErrorStream();
             final OutputStream stdIn = process.getOutputStream()) {

            if (!redirectErrToOut) {
                stdErrThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.debug("StdErr thread has started");
                        try {
                            T errResult = stdErrReader.read(stdErr);
                            errResultRef.set(errResult);
                        } catch (Exception e) {
                            exceptionRef.set(e);
                            stop();
                        }
                        LOGGER.debug("StdErr thread has finished");
                        workingThreadCount.decrementAndGet();
                    }
                }, "stderr reader");
                workingThreadCount.incrementAndGet();
                stdErrThread.start();
            }

            if (stdInWriter != null) {
                stdInThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.debug("StdIn thread has started");
                        try {
                            stdInWriter.write(stdIn);
                        } catch (Exception e) {
                            exceptionRef.set(e);
                            stop();
                        }
                        LOGGER.debug("StdIn thread has finished");
                        workingThreadCount.decrementAndGet();
                    }
                }, "stdin writer");
                workingThreadCount.incrementAndGet();
                stdErrThread.start();
            }

            stdOutThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    LOGGER.debug("StdOut thread has started");
                    try {
                        T result = stdOutReader.read(stdOut);
                        resultRef.set(result);
                    } catch (Exception e) {
                        exceptionRef.set(e);
                        stop();
                    }
                    LOGGER.debug("StdOut thread has finished");
                    workingThreadCount.decrementAndGet();
                }
            }, "stdout reader");
            workingThreadCount.incrementAndGet();
            stdOutThread.start();

            while (!stopped && workingThreadCount.get() != 0) {
                try {
                    LOGGER.debug("Waiting for stopping or threads finish");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    LOGGER.warn("Interrupted", e);
                }
            }

            if (!stopped) {
                LOGGER.debug("Waiting for process to finish");
                status = process.waitFor();

                result = resultRef.get();
                if (result == null) {
                    result = errResultRef.get();
                }
            } else {
                LOGGER.info("Destroying process");
                process.destroy();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to handle process execution", e);
        } finally {
            LOGGER.debug("Interrupting existing threads");
            if (stdInThread != null) {
                stdInThread.interrupt();
            }
            if (stdOutThread != null) {
                stdOutThread.interrupt();
            }
            if (stdErrThread != null) {
                stdOutThread.interrupt();
            }
            process.destroy();
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

        throw new RuntimeException("Process has ended with no result and non zero status: " + status, exception);
    }

    public void stop() {
        LOGGER.warn("Stop command has been received");
        stopped = true;
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
