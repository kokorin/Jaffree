package com.github.kokorin.jaffree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    public static final Path FFMPEG_BIN = getFfmpegBin();

    private static Path getFfmpegBin() {
        String ffmpegHome = System.getProperty("FFMPEG_BIN");
        if (ffmpegHome != null) {
            LOGGER.info("Using FFMPEG_BIN from system property: {}", ffmpegHome);
            return Paths.get(ffmpegHome);
        }

        ffmpegHome = System.getenv("FFMPEG_BIN");
        if (ffmpegHome != null) {
            LOGGER.info("Using FFMPEG_BIN from environment variable: {}", ffmpegHome);
            return Paths.get(ffmpegHome);
        }

        LOGGER.warn("FFMPEG_BIN not configured: expecting to find ffmpeg on PATH");
        return null;
    }
}
