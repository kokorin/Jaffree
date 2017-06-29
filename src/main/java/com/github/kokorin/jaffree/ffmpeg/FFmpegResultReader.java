package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.process.StdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FFmpegResultReader implements StdReader<FFmpegResult>{
    private final ProgressListener progressListener;

    private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegResultReader.class);

    public FFmpegResultReader(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public FFmpegResult read(InputStream stdOut) {
        //just read stdOut fully
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdOut));
        String line;
        FFmpegResult result = null;

        try {
            while ((line = reader.readLine()) != null) {
                LOGGER.info(line);
                if (progressListener != null) {
                    FFmpegProgress progress = FFmpegProgress.fromString(line);
                    if (progress != null) {
                        progressListener.onProgress(progress);
                        continue;
                    }
                }

                FFmpegResult possibleResult = FFmpegResult.fromString(line);

                if (possibleResult != null) {
                    result = possibleResult;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
