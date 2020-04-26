package examples.programmatic;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.Frame;
import com.github.kokorin.jaffree.ffmpeg.FrameConsumer;
import com.github.kokorin.jaffree.ffmpeg.FrameOutput;
import com.github.kokorin.jaffree.ffmpeg.Stream;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExtractFrames {
    private final Path ffmpegBin;
    private final Path input;
    private final Path outputDir;
    private final Long frames;
    private final Long time;


    public ExtractFrames(Path ffmpegBin, Path input, Path outputDir, Long frames, Long time) {
        this.ffmpegBin = ffmpegBin;
        this.input = input;
        this.outputDir = outputDir;
        this.frames = frames;
        this.time = time;
    }

    public void execute() {
        FFmpegResult result = FFmpeg.atPath(ffmpegBin)
                .addInput(UrlInput
                        .fromPath(input)
                        .setPosition(time, TimeUnit.SECONDS)
                )
                .addOutput(FrameOutput
                        .withConsumer(
                                new FrameConsumer() {
                                    private long num = 1;

                                    @Override
                                    public void consumeStreams(List<Stream> streams) {
                                        // All stream type except video are disabled. just ignore
                                    }

                                    @Override
                                    public void consume(Frame frame) {
                                        // End of Stream
                                        if (frame == null) {
                                            return;
                                        }

                                        try {
                                            if (!Files.exists(outputDir)) {
                                                Files.createDirectories(outputDir);
                                            }

                                            String filename = "frame_" + num++ + ".png";
                                            Path output = outputDir.resolve(filename);
                                            ImageIO.write(frame.getImage(), "png", output.toFile());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        )
                        .setFrameCount(StreamType.VIDEO, frames)
                        .setFrameRate(1) // 1 frame per second
                        .disableStream(StreamType.AUDIO)
                        .disableStream(StreamType.SUBTITLE)
                        .disableStream(StreamType.DATA)
                )
                .execute();
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options()
                .addOption("ffmpeg_bin", true, "FFmpeg binaries location")
                .addOption("count", true, "Number of frames to extract")
                .addOption("time", true, "Time at which start frame extraction")
                .addOption("input", true, "Video input")
                .addOption("output", true, "Output Directory");
        CommandLine commandLine = new DefaultParser().parse(options, args);

        new ExtractFrames(
                Paths.get(commandLine.getOptionValue("ffmpeg_bin")),
                Paths.get(commandLine.getOptionValue("input")),
                Paths.get(commandLine.getOptionValue("output")),
                Long.valueOf(commandLine.getOptionValue("count", "1")),
                Long.valueOf(commandLine.getOptionValue("time", "5"))
        ).execute();
    }
}
