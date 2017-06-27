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

package com.github.kokorin.jaffree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Executable<T> {
    private final Path executablePath;

    private static final Logger LOGGER = LoggerFactory.getLogger(Executable.class);

    public Executable(Path executablePath) {
        this.executablePath = executablePath;
    }

    public T execute() {
        LOGGER.debug("Building list of options");
        List<Option> options = buildOptions();

        LOGGER.debug("Constructing command");
        List<String> command = new ArrayList<>();

        command.add(executablePath.toString());

        for (Option option : options) {
            command.add(option.getName());
            if (option.getValue() != null) {
                command.add(option.getValue());
            }
        }

        if (LOGGER.isInfoEnabled()) {
            StringBuilder commandBuilder = new StringBuilder();
            boolean first = true;
            for (String argument : command) {
                if (!first) {
                    commandBuilder.append(" ");
                }
                String quote = argument.contains(" ") ? "\"" : "";
                commandBuilder.append(quote).append(argument).append(quote);
                first = false;
            }
            LOGGER.info("Command constructed:\n{}", commandBuilder.toString());
        }

        Process process;
        try {
            LOGGER.debug("Starting process");
            process = new ProcessBuilder(command)
                    .redirectErrorStream(isRedirectErrToStd())
                    .start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start process.", e);
        }

        T result = null;
        final AtomicReference<Exception> exceptionRef = new AtomicReference<>();
        int status = 0;
        Thread errorThread = null;
        Thread inThread = null;

        LOGGER.debug("Reading of stdout and stderr");

        try (InputStream stdOut = process.getInputStream();
             final InputStream stdErr = process.getErrorStream();
             final OutputStream stdIn = process.getOutputStream()) {
            if (!isRedirectErrToStd()) {
                errorThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Exception exception = null;
                        try {
                            parseStdErr(stdErr);
                        } catch (Exception e) {
                            exception = e;
                        }
                        exceptionRef.set(exception);
                    }
                }, "stderr reader");
                LOGGER.debug("Starting thread for reading stderr");
                errorThread.start();
            }

            if (isWriteToStdIn()) {
                inThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeStdIn(stdIn);
                    }
                }, "stdin writer");
                LOGGER.debug("Starting thread for writing stdin");
                errorThread.start();
            }

            result = parseStdOut(stdOut);

            if (inThread != null) {
                LOGGER.debug("Waiting for thread to join current thread");
                inThread.join();
            }
            if (errorThread != null) {
                LOGGER.debug("Waiting for thread to join current thread");
                errorThread.join();
            }

            LOGGER.debug("Waiting process to finish");
            status = process.waitFor();

            errorThread = null;
        } catch (Exception e) {
            if (inThread != null) {
                inThread.interrupt();
            }
            if (errorThread != null) {
                errorThread.interrupt();
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

        throw new RuntimeException("Process has ended with no result and non zero status: " + status, exception);
    }

    /**
     * Whether to combine stdOut and stdErr. May ease parsing of output.
     */
    protected boolean isRedirectErrToStd() {
        return false;
    }

    /**
     * Whether to write to stdIn.
     */
    protected boolean isWriteToStdIn() {
        return false;
    }

    protected abstract List<Option> buildOptions();

    /**
     * Parses standard output of process execution.
     * Note that implementation <b>must</b> read {@code}stdOut{@code} fully,
     * in order for {@code}Executable{@code} to work properly.
     * @param stdOut InputStream to parse
     * @return parsed entity
     */
    protected abstract T parseStdOut(InputStream stdOut);

    /**
     * Parses stderr output of process execution.
     * Note that implementation <b>must</b> read {@code}stdErr{@code} fully,
     * in order for {@code}Executable{@code} to work properly.
     * @param stdErr InputStream to parse
     * @throws Exception if execution of process has failed
     */
    protected abstract void parseStdErr(InputStream stdErr) throws Exception;

    protected void writeStdIn(OutputStream stdIn) {

    }
}
