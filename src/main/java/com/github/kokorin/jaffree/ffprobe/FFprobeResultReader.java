package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.Data;
import com.github.kokorin.jaffree.ffprobe.data.DataParser;
import com.github.kokorin.jaffree.ffprobe.data.LineIterator;
import com.github.kokorin.jaffree.process.StdReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class FFprobeResultReader implements StdReader<FFprobeResult> {
    @Override
    public FFprobeResult read(InputStream stdOut) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdOut));
        Iterator<String> lines = new LineIterator(reader);
        Data data = DataParser.parse(lines);

        return new FFprobeResult(data);
    }
}
