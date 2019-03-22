package com.github.kokorin.jaffree;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Artifacts {

    public static Path getFFmpegSample(String relativeUrl) {
        URI uri = URI.create("http://samples.ffmpeg.org/").resolve(relativeUrl);
        return getSample(uri);
    }

    public static synchronized Path getSample(URI uri) {
        Path sample = getSamplePath(uri.getPath().substring(1));
        if (!Files.exists(sample)) {
            try (InputStream inputStream = uri.toURL().openStream()) {
                Files.createDirectories(sample.getParent());
                Files.copy(inputStream, sample, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                throw new RuntimeException("Failed to download", e);
            }
        }

        return sample;
    }

    public static synchronized Path getSamplePath(String name) {
        Path artifacts = Paths.get(".artifacts");
        return artifacts.resolve(name);
    }
}
