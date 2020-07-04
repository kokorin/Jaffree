/*
 *    Copyright  2020 Vicne
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package examples.ffmpeg;

import com.github.kokorin.jaffree.ffmpeg.CaptureInput;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

/**
 * This class demonstrates capturing the computer desktop and writing it to a file.
 * The default duration is 10 seconds and can be overridden with the -duration option
 * If the -center-only option is passed, only a 640x480 rectangle centered on the screen is grabbed
 * Sample use: DesktopCapture -ffmpeg_bin <path\to\ffmpeg\bin> -output <desktop.mp4>
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

    public void execute() throws Exception {
        //final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //Rectangle area = new Rectangle(screenSize.width / 2 - 320, screenSize.height / 2 - 240, 640, 480);

        FFmpegResult result = FFmpeg.atPath(ffmpegBin)
                .addInput(CaptureInput
                        .captureDesktop()
                        .setCaptureFrameRate(frameRate)
                        .setCaptureCursor(true)
                )
                .addOutput(UrlOutput
                        .toPath(output)
                        .addArguments("-preset", "ultrafast")
                        .setDuration(duration, TimeUnit.SECONDS)
                )
                .setOverwriteOutput(true)
                .execute();

        Path outputOptimized = output.resolveSibling("opt-" + output.getFileName());
        FFmpegResult optimizedResult = FFmpeg.atPath(ffmpegBin)
                .addInput(UrlInput.fromPath(output))
                .addOutput(UrlOutput.toPath(outputOptimized))
                .execute();

        Files.move(outputOptimized, output, StandardCopyOption.REPLACE_EXISTING);

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
