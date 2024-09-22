package com.github.kokorin.jaffree;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.input.PipeInput;
import com.github.kokorin.jaffree.ffmpeg.input.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.output.UrlOutput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Artifacts {
    public static final Path VIDEO_MP4 = getMp4Artifact(180);
    public static final Path VIDEO_MKV = getMkvArtifact(180);
    public static final Path VIDEO_FLV = getFlvArtifact(180);
    public static final Path VIDEO_TS = getTsArtifact(180);
    public static final Path SMALL_FLV = getFlvArtifact(20);
    public static final Path SMALL_MP4 = getMp4Artifact(20);
    public static final Path VIDEO_WITH_PROGRAMS = getTsArtifactWithPrograms();
    public static final Path VIDEO_WITH_CHAPTERS = getMkvArtifactWithChapters();
    public static final Path VIDEO_WITH_SUBTITLES = getMkvArtifactWithSubtitles();
    public static final Path AUDIO_OPUS = getOpusArtifact(180);
    public static final Path VIDEO_MJPEG = getMjpegArtifact(20);
    public static final Path VIDEO_NUT = getNutArtifact(180);
    public static final Path VIDEO_NUT_WITH_CHAPTERS = getNutArtifactWithChapters();

    private static Path getMp4Artifact(int duration) {
        return getArtifact("640x480", 30, 44_100, "mp4", duration);
    }

    private static Path getFlvArtifact(int duration) {
        return getArtifact("640x480", 30, 44_100, "flv", duration);
    }

    private static Path getTsArtifact(int duration) {
        return getArtifact("640x480", 30, 44_100, "ts", duration);
    }

    private static synchronized Path getNutArtifact(int duration) {
        Path source = getMp4Artifact(duration);
        String filename = source.getFileName().toString().replace(".mp4", ".nut");
        Path result = getSamplePath(filename);

        if (!Files.exists(result)) {
            FFmpeg.atPath()
                    .addInput(UrlInput.fromPath(source))
                    .addOutput(UrlOutput
                            .toPath(result)
                            .copyAllCodecs()
                    )
                    .execute();
        }

        return result;
    }

    private static synchronized Path getNutArtifactWithChapters() {
        Path source = getMkvArtifactWithChapters();
        String filename = source.getFileName().toString().replace(".mkv", ".nut");
        Path result = getSamplePath(filename);

        if (!Files.exists(result)) {
            FFmpeg.atPath()
                    .addInput(UrlInput.fromPath(source))
                    .addOutput(UrlOutput
                            .toPath(result)
                            .copyAllCodecs()
                    )
                    .execute();
        }

        return result;
    }

    private static synchronized Path getMkvArtifactWithSubtitles() {
        int duration = 180;
        String filename = "subtitles_" + duration + "s.mkv";
        Path result = getSamplePath(filename);

        if (!Files.exists(result)) {
            Path source = getMkvArtifact(duration);

            StringBuilder subtitles = new StringBuilder();
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss,SSS");
            for (int i = 1; i < duration; i++) {
                subtitles
                        .append(i).append('\n')
                        .append(format.format(new Date(i * 1000L))).append(" --> ")
                        .append(format.format(new Date(i * 1000L - 100L))).append('\n')
                        .append(i).append('\n')
                        .append('\n');
            }

            FFmpeg.atPath()
                    .addInput(UrlInput.fromPath(source))
                    .addInput(PipeInput
                            .pumpFrom(new ByteArrayInputStream(subtitles.toString().getBytes()))
                            .setFormat("srt")
                    )
                    .addOutput(UrlOutput
                            .toPath(result)
                            .addMap(0, StreamType.VIDEO)
                            .addMap(0, StreamType.AUDIO)
                            .addMap(1, StreamType.SUBTITLE)
                            .copyCodec(StreamType.VIDEO)
                            .copyCodec(StreamType.AUDIO)
                    )
                    .execute();
        }

        return result;
    }

    private static synchronized Path getMkvArtifactWithChapters() {
        int duration = 180;
        String filename = "3chapters_" + duration + "s.mkv";
        Path result = getSamplePath(filename);

        if (!Files.exists(result)) {
            Path source = getMkvArtifact(duration);
            String metadata = ";FFMETADATA1\n" +
                    "[CHAPTER]\n" +
                    "TIMEBASE=1/1\n" +
                    "START=0\n" +
                    "END=60\n" +
                    "title=FirstChapter\n" +
                    "[CHAPTER]\n" +
                    "TIMEBASE=1/1\n" +
                    "START=60\n" +
                    "END=120\n" +
                    "title=Second Chapter\n" +
                    "[CHAPTER]\n" +
                    "TIMEBASE=1/1\n" +
                    "START=120\n" +
                    "END=180\n" +
                    "title=Final\n";

            FFmpeg.atPath()
                    .addInput(UrlInput.fromPath(source))
                    .addInput(PipeInput.pumpFrom(new ByteArrayInputStream(metadata.getBytes())))
                    .addOutput(UrlOutput
                            .toPath(result)
                            .addMap(0)
                            .addArguments("-map_metadata", "1")
                            .copyAllCodecs()
                    )
                    .execute();
        }

        return result;
    }

    private static Path getMkvArtifact(int duration) {
        return getArtifact("640x480", 30, 44_100, "mkv", duration);
    }

    private static synchronized Path getTsArtifactWithPrograms() {
        int duration = 180;
        String filename = "3programs_" + duration + "s.ts";
        Path result = getSamplePath(filename);

        if (!Files.exists(result)) {
            Path program1 = getArtifact("640x480", 30, 44_100, "ts", duration);
            Path program2 = getArtifact("320x240", 30, 44_100, "ts", duration);
            Path program3 = getArtifact("160x120", 30, 44_100, "ts", duration);

            FFmpeg.atPath()
                    .addInput(UrlInput.fromPath(program1))
                    .addInput(UrlInput.fromPath(program2))
                    .addInput(UrlInput.fromPath(program3))
                    .addOutput(UrlOutput
                            .toPath(result)
                            .addMap(0, StreamType.VIDEO)
                            .addMap(0, StreamType.AUDIO)
                            .addMap(1, StreamType.VIDEO)
                            .addMap(1, StreamType.AUDIO)
                            .addMap(2, StreamType.VIDEO)
                            .addMap(2, StreamType.AUDIO)
                            .copyAllCodecs()
                            .addProgram(1, "first_program", 0, 1)
                            .addProgram(2, "second program", 2, 3)
                            .addProgram(3, "3rdProgram", 4, 5)
                    )
                    .execute();
        }

        return result;
    }

    private static synchronized Path getOpusArtifact(int duration) {
        return getOpusArtifact(44_100, duration);
    }

    private static synchronized Path getOpusArtifact(int samplerate, int duration) {
        return getArtifact(null, 0, samplerate, "opus", duration);
    }

    private static synchronized Path getMjpegArtifact(int duration) {
        return getMjpegArtifact("160x120", 1, duration);
    }

    private static synchronized Path getMjpegArtifact(String resolution, int fps, int duration) {
        return getArtifact(resolution, fps, 0, "mjpeg", duration);
    }

    private static synchronized Path getArtifact(String resolution, Integer fps, int samplerate,
                                                 String format, int duration) {
        FFmpeg ffmpeg = FFmpeg.atPath();
        String filename = "";
        int cutDuration = 5;
        int extraDuration = duration + 2 * cutDuration;

        if (resolution != null) {
            filename += resolution + "_" + fps + "fps";

            ffmpeg
                    .addInput(
                            UrlInput
                                    .fromUrl(
                                            "testsrc=s=" + resolution + ":r=30:d=" + extraDuration
                                    )
                                    .setFormat("lavfi")
                    )
                    .addArguments("-preset", "ultrafast");


        }

        if (samplerate > 0) {
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
                                    .setFrameRate(fps)
                    )
                    .execute();
        }

        return result;
    }

    private static synchronized Path getSamplePath(String name) {
        try {
            Path artifacts = Paths.get(".artifacts");
            Files.createDirectories(artifacts);
            return artifacts.resolve(name);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get sample path", e);
        }
    }
}
