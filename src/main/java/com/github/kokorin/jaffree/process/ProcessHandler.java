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

package com.github.kokorin.jaffree.process;

import com.github.kokorin.jaffree.JaffreeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProcessHandler<T> {
    private final Path executable;
    private final String contextName;
    private StdReader<T> stdOutReader = new GobblingStdReader<>();
    private StdReader<T> stdErrReader = new GobblingStdReader<>();
    private List<ProcessHelper> helpers = null;
    private Stopper stopper = null;
    private List<String> arguments = Collections.emptyList();

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessHandler.class);

    public ProcessHandler(Path executable, String contextName) {
        this.executable = executable;
        this.contextName = contextName;
    }

    public synchronized ProcessHandler<T> setStdOutReader(StdReader<T> stdOutReader) {
        this.stdOutReader = stdOutReader;
        return this;
    }

    public synchronized ProcessHandler<T> setStdErrReader(StdReader<T> stdErrReader) {
        this.stdErrReader = stdErrReader;
        return this;
    }

    /**
     * Set extra {@link Runnable}s that must be executed in parallel with process
     *
     * @param helpers list
     * @return this
     */
    public synchronized ProcessHandler<T> setHelpers(List<ProcessHelper> helpers) {
        this.helpers = helpers;
        return this;
    }

    public synchronized ProcessHandler<T> setStopper(Stopper stopper) {
        this.stopper = stopper;
        return this;
    }

    public synchronized ProcessHandler<T> setArguments(List<String> arguments) {
        this.arguments = arguments;
        return this;
    }

    public synchronized T execute() {
        try {
            List<String> command = new ArrayList<>();
            command.add(executable.toString());
            command.addAll(arguments);

            LOGGER.info("Command constructed:\n{}", joinArguments(command));

            Process process = null;
            try {
                LOGGER.info("Starting process: {}", executable);
                process = new ProcessBuilder(command)
                        .start();
                if (stopper != null) {
                    stopper.setProcess(process);
                }

                return interactWithProcess(process);
            } catch (IOException e) {
                throw new JaffreeException("Failed to start process.", e);
            } finally {
                if (process != null) {
                    // TODO on Windows process sometimes doesn't stop and keeps running
                    process.destroy();
                    // Process must be destroyed before closing streams, can't use try-with-resources,
                    // as resources are closing when leaving try block, before finally
                    closeQuietly(process.getInputStream());
                    closeQuietly(process.getOutputStream());
                    closeQuietly(process.getErrorStream());
                }
            }
        } finally {
            closeQuietly(helpers);
        }
    }

    protected T interactWithProcess(Process process) {
        AtomicReference<T> resultRef = new AtomicReference<>();
        Executor executor = null;
        Integer status = null;
        Exception interrupted = null;

        try {
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
            throw new JaffreeException("Failed to execute, exception appeared in one of helper threads", exception);
        }

        if (interrupted != null) {
            throw new JaffreeException("Failed to execute, was interrupted", interrupted);
        }

        if (!Integer.valueOf(0).equals(status)) {
            throw new JaffreeException("Process execution has ended with non-zero status: " + status);
        }

        T result = resultRef.get();
        if (result == null) {
            throw new JaffreeException("Process execution has ended with null result");
        }

        return result;
    }

    protected Executor startExecution(final Process process, final AtomicReference<T> resultReference) {
        Executor executor = new Executor(contextName);

        LOGGER.debug("Starting IO interaction with process");

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

        if (helpers != null) {
            for (int i = 0; i < helpers.size(); i++) {
                Runnable runnable = helpers.get(i);
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

    private static void closeQuietly(final Collection<? extends Closeable> toClose) {
        if (toClose == null) {
            return;
        }
        for (Closeable toCloseItem : toClose) {
            closeQuietly(toCloseItem);
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
