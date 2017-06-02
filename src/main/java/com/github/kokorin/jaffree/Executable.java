package com.github.kokorin.jaffree;

import com.github.kokorin.jaffree.cli.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
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
            process = new ProcessBuilder(command).start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start process.", e);
        }

        T result = null;
        final AtomicReference<Exception> exceptionRef = new AtomicReference<>();
        int status = 0;
        Thread errorThread = null;

        LOGGER.debug("Reading of stdout and stderr");

        try (InputStream stdOut = process.getInputStream();
             final InputStream stdErr = process.getErrorStream()) {
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
            });
            LOGGER.debug("Starting thread for reading stderr");
            errorThread.start();

            result = parseStdOut(stdOut);
            errorThread.join();
            status = process.waitFor();
            errorThread = null;
        } catch (Exception e) {
            if (errorThread != null) {
                errorThread.interrupt();
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
}
