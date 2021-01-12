package examples.ffmpeg;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegProgress;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.ProgressListener;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

public class ReEncode {
    private final Path ffmpegBin;
    private final Path input;
    private final Path output;

    public ReEncode(Path ffmpegBin, Path input, Path output) {
        this.ffmpegBin = ffmpegBin;
        this.input = input;
        this.output = output;
    }

    public void execute() {
        // The most reliable way to get video duration
        // ffprobe for some formats can't detect duration
        final AtomicLong duration = new AtomicLong();
        final FFmpegResult nullResult = FFmpeg.atPath(ffmpegBin)
                .addInput(UrlInput.fromPath(input))
                .addOutput(new NullOutput())
                .setOverwriteOutput(true)
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        duration.set(progress.getTimeMillis());
                    }
                })
                .execute();

        ProgressListener listener = new ProgressListener() {
            private long lastReportTs = System.currentTimeMillis();

            @Override
            public void onProgress(FFmpegProgress progress) {
                long now = System.currentTimeMillis();
                if (lastReportTs + 1000 < now) {
                    long percent = 100 * progress.getTimeMillis() / duration.get();
                    System.out.println("Progress: " + percent + "%");
                }
            }
        };

        FFmpegResult result = FFmpeg.atPath(ffmpegBin)
                .addInput(UrlInput.fromPath(input))
                .addOutput(UrlOutput.toPath(output))
                .setProgressListener(listener)
                .setOverwriteOutput(true)
                .execute();
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options()
                .addOption("ffmpeg_bin", true, "FFmpeg binaries location")
                .addOption("input", true, "Input")
                .addOption("output", true, "Output");
        CommandLine commandLine = new DefaultParser().parse(options, args);

        new ReEncode(
                Paths.get(commandLine.getOptionValue("ffmpeg_bin")),
                Paths.get(commandLine.getOptionValue("input")),
                Paths.get(commandLine.getOptionValue("output"))
        ).execute();
    }
}
