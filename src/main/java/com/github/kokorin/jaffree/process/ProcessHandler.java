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

    /**
     * Set extra {@link Runnable}s that must be executed in parallel with process
     *
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

        Process process = null;
        try {
            LOGGER.info("Starting process: {}", executable);
            process = new ProcessBuilder(command)
                    .start();

            return interactWithProcess(process);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start process.", e);
        } finally {
            if (process != null) {
                process.destroy();
                // Process must be destroyed before closing streams, can't use try-with-resources,
                // as resources are closing when leaving try block, before finally
                closeQuietly(process.getInputStream());
                closeQuietly(process.getOutputStream());
                closeQuietly(process.getErrorStream());
            }
        }
    }

    protected T interactWithProcess(Process process) {
        AtomicReference<T> resultRef = new AtomicReference<>();
        Executor executor = null;
        Integer status = null;
        Exception interrupted = null;

        try {
            waitForProcessOutput(process, 10_000);

            executor = startExecution(process, resultRef);

            LOGGER.info("Waiting for process to finish");
            status = process.waitFor();
            LOGGER.info("Process has finished with status: {}", status);

            waitForExecutorToStop(executor, 10_000);
        } catch (InterruptedException e) {
            LOGGER.warn("Process has been interrupted");
            interrupted = e;
        } finally {
            if (executor != null) {
                executor.stop();
            }
        }

        Exception exception = null;
        if (executor != null) {
            exception = executor.getException();
        }
        if (exception != null) {
            throw new RuntimeException("Failed to execute, exception appeared in one of helper threads", exception);
        }

        if (interrupted != null) {
            throw new RuntimeException("Failed to execute, was interrupted", interrupted);
        }

        if (!Integer.valueOf(0).equals(status)) {
            throw new RuntimeException("Process execution has ended with non-zero status: " + status);
        }

        T result = resultRef.get();
        if (result == null) {
            throw new RuntimeException("Process execution has ended with null result");
        }

        return result;
    }

    protected Executor startExecution(final Process process, final AtomicReference<T> resultReference) {
        Executor executor = new Executor(contextName);

        LOGGER.debug("Starting IO interaction with process");

        if (stdInWriter != null) {
            executor.execute("StdIn", new Runnable() {
                @Override
                public void run() {
                    // Explicitly close stdIn to notify process, that there will be no more data
                    try (OutputStream outputStream = process.getOutputStream()) {
                        stdInWriter.write(outputStream);
                    } catch (Exception e) {
                        throw new RuntimeException("Error while writing to Process", e);
                    }
                }
            });
        }

        if (stdErrReader != null) {
            executor.execute("StdErr", new Runnable() {
                @Override
                public void run() {
                    T errResult = stdErrReader.read(process.getErrorStream());
                    if (errResult != null) {
                        boolean set = resultReference.compareAndSet(null, errResult);
                        if (!set) {
                            LOGGER.warn("Ignored result of reading STD ERR: {}", errResult);
                        }
                    }
                }
            });
        }

        if (stdOutReader != null) {
            executor.execute("StdOut", new Runnable() {
                @Override
                public void run() {
                    T result = stdOutReader.read(process.getInputStream());
                    if (result != null) {
                        boolean set = resultReference.compareAndSet(null, result);
                        if (!set) {
                            LOGGER.warn("Ignored result of reading STD OUT: {}", result);
                        }
                    }
                }
            });
        }

        if (runnables != null) {
            for (int i = 0; i < runnables.size(); i++) {
                Runnable runnable = runnables.get(i);
                executor.execute("Runnable-" + i, runnable);
            }
        }

        return executor;
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


    private static void closeQuietly(Closeable toClose) {
        try {
            if (toClose != null) {
                toClose.close();
            }
        } catch (Exception e) {
            LOGGER.warn("Ignoring exception: " + e.getMessage());
        }
    }


    private static void waitForProcessOutput(Process process, long timeoutMillis) throws InterruptedException {
        LOGGER.debug("Waiting for Process output");

        long waitStarted = System.currentTimeMillis();

        try {
            do {
                if (System.currentTimeMillis() - waitStarted > timeoutMillis) {
                    LOGGER.warn("Process has no output in {} millis, won't wait longer", timeoutMillis);
                    break;
                }

                LOGGER.trace("Process has no output yet, will sleep");
                Thread.sleep(100);
            } while (process.getErrorStream().available() == 0
                    && process.getInputStream().available() == 0);
        } catch (IOException ignored) {

        }
    }

    private static void waitForExecutorToStart(Executor executor, long timeoutMillis) throws InterruptedException {
        LOGGER.debug("Waiting for Executor to start");

        long waitStarted = System.currentTimeMillis();
        while (!executor.isRunning()) {
            if (System.currentTimeMillis() - waitStarted > timeoutMillis) {
                LOGGER.warn("Executor hasn't started in {} millis, won't wait longer", timeoutMillis);
                break;
            }

            LOGGER.trace("Executor hasn't yet started, will sleep");
            Thread.sleep(100);
        }
    }

    private static void waitForExecutorToStop(Executor executor, long timeoutMillis) throws InterruptedException {
        LOGGER.debug("Waiting for Executor to stop");

        long waitStarted = System.currentTimeMillis();
        do {
            if (System.currentTimeMillis() - waitStarted > timeoutMillis) {
                LOGGER.warn("Executor hasn't stopped in {} millis, won't wait longer", timeoutMillis);
                break;
            }

            LOGGER.trace("Executor hasn't yet stopped, still running threads: {}", executor.getRunningThreadNames());
            Thread.sleep(100);
        } while (executor.isRunning());
    }
}
