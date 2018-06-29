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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProcessHandler<T> {
    private final Path executable;
    private final String contextName;
    private StdWriter stdInWriter = null;
    private StdReader<T> stdOutReader = new GobblingStdReader<>();
    private StdReader<T> stdErrReader = new GobblingStdReader<>();
    private List<Runnable> runnables = null;
    private boolean redirectErrToOut = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessHandler.class);

    public ProcessHandler(Path executable, String contextName) {
        this.executable = executable;
        this.contextName = contextName;
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

    /**
     * Set extra {@link Runnable}s that must be executed in parallel with process
     * @param runnables list
     * @return this
     */
    public ProcessHandler<T> setRunnables(List<Runnable> runnables) {
        this.runnables = runnables;
        return this;
    }

    public T execute(List<String> options) {
        List<String> command = new ArrayList<>();
        command.add(executable.toString());
        command.addAll(options);

        LOGGER.info("Command constructed:\n{}", joinArguments(command));

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
        final Executor<T> executor = new Executor<>(contextName);
        int status = -1;

        LOGGER.debug("Starting io interaction with process");

        try (final InputStream stdOut = process.getInputStream();
             final InputStream stdErr = process.getErrorStream();
             final OutputStream stdIn = process.getOutputStream()) {

            if (!redirectErrToOut) {
                executor.execute("StdErr", new Runnable(){
                    @Override
                    public void run() {
                        T errResult = stdErrReader.read(stdErr);
                        errResultRef.set(errResult);
                    }
                });
            }

            if (stdInWriter != null) {
                executor.execute("StdIn", new Runnable() {
                    @Override
                    public void run() {
                        // Explicitly close stdIn to notify process, that there will be no more data
                        try (Closeable toClose = stdIn) {
                            stdInWriter.write(stdIn);
                        } catch (Exception e) {
                            throw new RuntimeException("Error while writing to Process", e);
                        }
                    }
                });
            }

            if (stdOutReader != null) {
                executor.execute("StdOut", new Runnable() {
                    @Override
                    public void run() {
                        T result = stdOutReader.read(stdOut);
                        resultRef.set(result);
                    }
                });
            }

            if (runnables != null) {
                for (int i = 0; i < runnables.size(); i++) {
                    final Runnable runnable = runnables.get(i);
                    executor.execute("runnable-" + i, runnable);
                }
            }

            while (executor.isRunning() && !executor.isEceptionCaught()) {
                try {
                    LOGGER.trace("Waiting for stopping or threads finish");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    LOGGER.warn("Interrupted", e);
                }
            }

            if (!executor.isEceptionCaught()) {
                LOGGER.debug("Waiting for process to finish");
                status = process.waitFor();

                result = resultRef.get();
                if (result == null) {
                    result = errResultRef.get();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to handle process execution", e);
        } finally {
            executor.stop();
            LOGGER.info("Destroying process");
            process.destroy();
        }

        Exception exception = executor.getFirstException();
        if (result == null) {
            if (exception != null) {
                throw new RuntimeException("Failed to execute (no result)", exception);
            }

            throw new RuntimeException("Process execution has ended without result or exception, but status is " + status);
        }

        if (exception != null) {
            LOGGER.warn("Process execution has ended with result and with exception : ", exception);
        }
        if (status != 0) {
            LOGGER.warn("Process execution has ended with result, but status is {}", status);
        }

        return result;
    }

    protected static String joinArguments(List<String> arguments) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (String argument : arguments) {
            if (!first) {
                result.append(" ");
            }
            String quote = argument.contains(" ") ? "\"" : "";
            result.append(quote).append(argument).append(quote);
            first = false;
        }

        return result.toString();
    }
}
