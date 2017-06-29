package com.github.kokorin.jaffree.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class GobblingStdReader<T> implements StdReader<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GobblingStdReader.class);

    @Override
    public T read(InputStream stdOut) {
        byte[] bytes = new byte[1024];
        int result;

        try {
            do {
                result = stdOut.read(bytes);
                LOGGER.info("Read {} bytes", result);
            } while (result != -1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
