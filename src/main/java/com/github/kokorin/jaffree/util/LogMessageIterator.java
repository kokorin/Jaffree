package com.github.kokorin.jaffree.util;

import com.github.kokorin.jaffree.LogLevel;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LogMessageIterator implements Iterator<LogMessage> {
    private final Iterator<String> lineIterator;
    private String nextLine = null;
    private LogLevel nextLogLevel = null;

    public LogMessageIterator(Iterator<String> lineIterator) {
        this.lineIterator = lineIterator;
    }

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

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }
}
