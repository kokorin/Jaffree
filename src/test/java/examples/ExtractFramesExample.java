package examples;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.Frame;
import com.github.kokorin.jaffree.ffmpeg.FrameConsumer;
import com.github.kokorin.jaffree.ffmpeg.FrameOutput;
import com.github.kokorin.jaffree.ffmpeg.Stream;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;

import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExtractFramesExample {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Exactly 2 arguments expected: path to source file"
                    + " and destination directory");
            System.exit(1);
        }

        Path pathToSrc = Paths.get(args[0]);
        final Path pathToDstDir = Paths.get(args[1]);

        FFmpeg.atPath()
                .addInput(UrlInput
                        .fromPath(pathToSrc)
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
                                            String filename = "frame_" + num++ + ".png";
                                            Path output = pathToDstDir.resolve(filename);
                                            ImageIO.write(frame.getImage(), "png", output.toFile());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        )
                        // No more then 100 frames
                        .setFrameCount(StreamType.VIDEO, 100L)
                        // 1 frame every 10 seconds
                        .setFrameRate(0.1)
                        // Disable all streams except video
                        .disableStream(StreamType.AUDIO)
                        .disableStream(StreamType.SUBTITLE)
                        .disableStream(StreamType.DATA)
                )
                .execute();
    }
}
