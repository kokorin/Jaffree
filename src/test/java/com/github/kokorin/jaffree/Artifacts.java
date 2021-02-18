package com.github.kokorin.jaffree;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

public class Artifacts {

    public static Path getMp4Artifact() {
        return getArtifact("640x480", 30, 44_100, "mp4", 180);
    }

    public static Path getFlvArtifact() {
        return getArtifact("640x480", 30, 44_100, "flv", 180);
    }

    public static synchronized Path getArtifact(String resolution, int fps, int samplerate,
                                                String format, int duration) {
        FFmpeg ffmpeg = FFmpeg.atPath();
        String filename = "";
        int cutDuration = 5;
        int extraDuration = duration + 2 * cutDuration;
       // String pixelFormat = null;

        if (resolution != null) {
            filename += resolution + "_" + fps + "fps";

            ffmpeg
                    .addInput(
                            UrlInput
                                    .fromUrl(
                                            "testsrc=s=" + resolution + ":r=" + fps
                                                    + ":d=" + extraDuration
                                    )
                                    .setFormat("lavfi")
                    )
                    .addArguments("-preset", "ultrafast");

            //pixelFormat = "yuv420";
        }

        if (samplerate >= 0) {
            if (!filename.isEmpty()) {
                filename += "_";
            }
            filename += samplerate + "hz";
            ffmpeg.addInput(
                    UrlInput.fromUrl("anoisesrc=r=" + samplerate + ":d=" + extraDuration + ":a=0.5")
                            .setFormat("lavfi")
            );
        }

        filename += "_" + duration + "s." + format;

        Path result = getSamplePath(filename);

        if (!Files.exists(result)) {
            ffmpeg
                    .addOutput(
                            UrlOutput.toPath(result)
                                    .setPosition(cutDuration, TimeUnit.SECONDS)
                                    .setDuration(duration, TimeUnit.SECONDS)
                    )
                    .execute();
        }

        return result;
    }

    public static Path getFFmpegSample(String relativeUrl) {
        URI uri = URI.create("https://samples.ffmpeg.org/").resolve(relativeUrl);
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
