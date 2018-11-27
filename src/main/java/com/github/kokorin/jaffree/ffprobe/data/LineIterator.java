package com.github.kokorin.jaffree.ffprobe.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LineIterator implements Iterator<String> {
    private final BufferedReader reader;
    private String nextLine = null;
    private boolean depleted = false;

    public LineIterator(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public boolean hasNext() {
        if (nextLine != null) {
            return true;
        }

        if (depleted) {
            return false;
        }

        try {
            nextLine = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Read failed", e);
        }

        if (nextLine == null) {
            depleted = true;
            return false;
        }

        return true;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more lines");
        }

        String result = nextLine;
        nextLine = null;
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Ramove not supported");
    }
}
