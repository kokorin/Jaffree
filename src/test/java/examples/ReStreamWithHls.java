package examples;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReStreamWithHls {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("re-stream");

        Path liveStreamSimulation = dir.resolve("live_stream_simulation.mp4");

        if (!Files.exists(liveStreamSimulation)) {
            FFmpeg.atPath()
                    .addInput(
                            UrlInput.fromUrl("testsrc=s=1980x720:r=30:d=600")
                                    .setFormat("lavfi")
                    )
                    .addInput(
                            UrlInput.fromUrl("anoisesrc=r=44100:d=600:a=0.5")
                                    .setFormat("lavfi")
                    )
                    .addOutput(
                            UrlOutput.toPath(liveStreamSimulation)
                    )
                    .execute();
        }

        FFmpeg.atPath()
                .addInput(
                        UrlInput.fromPath(liveStreamSimulation)
                                .setReadAtFrameRate(true)
                )
                .addOutput(
                        UrlOutput.toPath(dir.resolve("index.m3u8"))
                                .setFrameRate(30)
                                // check all available options: ffmpeg -help muxer=hls
                                .setFormat("hls")
                                // enforce keyframe every 2s - see setFrameRate
                                .addArguments("-x264-params", "keyint=60")
                                .addArguments("-hls_list_size", "5")
                                .addArguments("-hls_delete_threshold", "5")
                                .addArguments("-hls_time", "2")
                                // TODO initialization FMP4 segment (init.mp4) is created at
                                // WORK directory, not at target directory
                                //.addArguments("-hls_segment_type", "1") // Fragmented MP4
                                .addArguments("-hls_flags", "delete_segments")
                )
                .setOverwriteOutput(true)
                .execute();
    }
}
