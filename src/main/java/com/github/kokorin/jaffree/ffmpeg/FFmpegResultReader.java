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

import com.github.kokorin.jaffree.JaffreeException;
import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.process.StdReader;
import com.github.kokorin.jaffree.util.LineIterator;
import com.github.kokorin.jaffree.util.LogMessage;
import com.github.kokorin.jaffree.util.LogMessageIterator;
import com.github.kokorin.jaffree.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * {@link FFmpegResultReader} reads ffmpeg stderr output, parses {@link FFmpegProgress} and
 * {@link FFmpegResult} and passes unparsed output to {@link OutputListener} (if provided).
 */
public class FFmpegResultReader implements StdReader<FFmpegResult> {
    private final OutputListener outputListener;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegResultReader.class);

    /**
     * Creates {@link FFmpegResultReader}.
     *
     * @param outputListener output listener
     */
    public FFmpegResultReader(final OutputListener outputListener) {
        this.outputListener = outputListener;
    }

    /**
     * Reads provided {@link InputStream} until it's depleted.
     * <p>
     * This method parses every line to detect ffmpeg log level. Lines with INFO log level are
     * additionally parsed to get ffmpeg result (which may be null because of too strict log level)
     *
     * @param stdOut input stream to read from
     * @return FFmpegResult if found or null
     * @throws JaffreeException if IOException appears or ffmpeg ends with error message.
     * @see FFmpeg#setLogLevel(LogLevel)
     */
    @SuppressWarnings("checkstyle:MissingSwitchDefault")
    @Override
    public FFmpegResult read(final InputStream stdOut) {
        LogMessageIterator logMessageIterator = new LogMessageIterator(
                new LineIterator(
                        new BufferedReader(new InputStreamReader(stdOut))
                )
        );

        FFmpegResult result = null;
        String errorMessage = null;

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

            if (logMessage.logLevel == LogLevel.INFO) {
                FFmpegResult possibleResult = ParseUtil.parseResult(logMessage.message);

                if (possibleResult != null) {
                    result = possibleResult;
                }
            }

            if (outputListener != null && logMessage.logLevel != null
                    && logMessage.logLevel.code() <= LogLevel.INFO.code()) {
                outputListener.onOutput(logMessage.message);
            }

            if (logMessage.logLevel != null
                    && logMessage.logLevel.code() <= LogLevel.ERROR.code()) {
                if (errorMessage == null) {
                    errorMessage = logMessage.message;
                } else {
                    errorMessage += "\n" + logMessage.message;
                }
            }
        }

        if (result != null) {
            if (errorMessage != null) {
                LOGGER.warn("One or more errors appeared during ffmpeg execution, "
                        + "ignoring since result is available");
            }
            return result;
        }

        if (errorMessage != null) {
            throw new JaffreeException("ffmpeg exited with message: " + errorMessage);
        }

        return new FFmpegResult(
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
