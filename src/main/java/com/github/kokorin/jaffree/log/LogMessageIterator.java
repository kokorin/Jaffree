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

package com.github.kokorin.jaffree.log;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.util.ParseUtil;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@link Iterator} implementation which can iterate over multiline ffprobe/ffmpeg log messages.
 */
public class LogMessageIterator implements Iterator<LogMessage> {
    private final Iterator<String> lineIterator;
    private String nextLine = null;
    private LogLevel nextLogLevel = null;

    /**
     * Create {@link LogMessageIterator}.
     *
     * @param lineIterator line iterator
     */
    public LogMessageIterator(final Iterator<String> lineIterator) {
        this.lineIterator = lineIterator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        if (nextLine != null) {
            return true;
        }

        if (!lineIterator.hasNext()) {
            return false;
        }

        nextLine = lineIterator.next();
        nextLogLevel = ParseUtil.parseLogLevel(nextLine);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogMessage next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more messages");
        }

        String message = nextLine;
        LogLevel logLevel = nextLogLevel;
        nextLine = null;
        nextLogLevel = null;

        String line;
        LogLevel lineLogLevel;
        while (lineIterator.hasNext()) {
            line = lineIterator.next();
            lineLogLevel = ParseUtil.parseLogLevel(line);

            if (lineLogLevel != null) {
                nextLine = line;
                nextLogLevel = lineLogLevel;
                break;
            }

            message += "\n" + line;
        }

        return new LogMessage(logLevel, message);
    }

    /**
     * Remove not supported.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }
}
