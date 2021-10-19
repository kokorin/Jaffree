/*
 *    Copyright 2021 Denis Kokorin
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
import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.log.LogMessage;
import com.github.kokorin.jaffree.log.LogMessageIterator;
import com.github.kokorin.jaffree.util.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * {@link BaseStdReader} reads std output, parses result and logs, and sends logs
 * to SLF4J with corresponding log level.
 *
 * @param <T> type of parsed result
 */
public abstract class BaseStdReader<T> implements StdReader<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStdReader.class);

    /**
     * Reads provided {@link InputStream} until it's depleted.
     * <p>
     * This method parses every line to detect ffmpeg log level. Lines with INFO log level are
     * additionally parsed for a result (which may be null because of too strict log level).
     *
     * @param stdOut input stream to read from
     * @return T if found or null
     * @throws JaffreeException if IOException appears or program ends with error message.
     * @see com.github.kokorin.jaffree.ffmpeg.FFmpeg#setLogLevel(LogLevel)
     * @see com.github.kokorin.jaffree.ffprobe.FFprobe#setLogLevel(LogLevel)
     */
    @SuppressWarnings("checkstyle:MissingSwitchDefault")
    @Override
    public T read(final InputStream stdOut) {
        LogMessageIterator logMessageIterator = new LogMessageIterator(
                new LineIterator(
                        new BufferedReader(new InputStreamReader(stdOut))
                )
        );

        T result = defaultResult();

        while (logMessageIterator.hasNext()) {
            LogMessage logMessage = logMessageIterator.next();

            if (logMessage.logLevel != null) {
                switch (logMessage.logLevel) {
                    case TRACE:
                        LOGGER.trace(logMessage.message);
                        break;
                    case VERBOSE:
                    case DEBUG:
                        LOGGER.debug(logMessage.message);
                        break;
                    case INFO:
                        LOGGER.info(logMessage.message);
                        break;
                    case WARNING:
                        LOGGER.warn(logMessage.message);
                        break;
                    case ERROR:
                    case FATAL:
                    case PANIC:
                    case QUIET:
                        LOGGER.error(logMessage.message);
                        break;
                }
            } else {
                LOGGER.info(logMessage.message);
            }

            T possibleResult = handleLogMessage(logMessage);
            if (possibleResult != null) {
                result = possibleResult;
            }
        }

        return result;
    }

    /**
     * @return default result
     */
    protected T defaultResult() {
        return null;
    }

    /**
     * Parses single ffmpeg/ffprobe log message.
     *
     * @param logMessage log message
     * @return parsed result or null
     */
    protected abstract T handleLogMessage(LogMessage logMessage);
}
