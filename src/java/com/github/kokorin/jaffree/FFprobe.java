package com.github.kokorin.jaffree;

import java.nio.file.Path;

public class FFprobe {
    private final Path path;

    protected FFprobe(Path path) {
        this.path = path;
    }

    public static FFprobe atPath(Path pathToDir) {
        return new FFprobe(pathToDir);
    }
}
