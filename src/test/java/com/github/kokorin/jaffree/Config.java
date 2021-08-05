package com.github.kokorin.jaffree;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    public static final Path FFMPEG_BIN = initFfmegBin();

    private static Path initFfmegBin() {
        String ffmpegHome = System.getProperty("FFMPEG_BIN");
        if (ffmpegHome != null) {
            System.out.println("Using FFMPEG_BIN from system property: '" + ffmpegHome + "'");
            return Paths.get(ffmpegHome);
        }
        ffmpegHome = System.getenv("FFMPEG_BIN");
        if (ffmpegHome != null) {
            System.out.println("Using FFMPEG_BIN from environment variable: '" + ffmpegHome + "'");
            return Paths.get(ffmpegHome);
        } else {
            System.out.println("FFMPEG_BIN not configured: expecting to find ffmpeg on PATH");
            return null;
        }
    }
}
