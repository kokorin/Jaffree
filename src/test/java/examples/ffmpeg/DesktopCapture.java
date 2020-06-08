package examples.ffmpeg;

import com.github.kokorin.jaffree.ffmpeg.DesktopCaptureInput;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * This class demonstrates capturing the computer destktop and writing it to a file.
 * The default duration is 10 seconds and can be overridden with the -duration option
 * Sample use: DesktopCapture -ffmpeg_bin <path\to\ffmpeg\bin> -output desktop.mp4
 */
public class DesktopCapture {
    private final Path ffmpegBin;
    private final Long duration;
    private final Path output;
    private final int frameRate = 25;

    public DesktopCapture(Path ffmpegBin, Long duration, Path output) {
        this.ffmpegBin = ffmpegBin;
        this.duration = duration;
        this.output = output;
    }

    public void execute() {
        FFmpegResult result = FFmpeg.atPath(ffmpegBin)
                .addInput(DesktopCaptureInput
                        .fromScreen()
                        .setFrameRate(frameRate)
                )
                .addOutput(UrlOutput
                        .toPath(output)
                        .setDuration(duration, TimeUnit.SECONDS)
                )
                .setOverwriteOutput(true)
                .execute();
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options()
                .addOption("ffmpeg_bin", true, "FFmpeg binaries location")
                .addOption("duration", true, "Duration")
                .addOption("output", true, "Output");
        CommandLine commandLine = new DefaultParser().parse(options, args);

        new DesktopCapture(
                Paths.get(commandLine.getOptionValue("ffmpeg_bin")),
                Long.valueOf(commandLine.getOptionValue("duration", "10")),
                Paths.get(commandLine.getOptionValue("output"))
        ).execute();
    }
}
