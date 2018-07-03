package examples.programmatic;

import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffmpeg.Frame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ProduceGif {
    private final Path ffmpegBin;

    public ProduceGif(Path ffmpegBin) {
        this.ffmpegBin = ffmpegBin;
    }

    public void execute() {
        Path output = Paths.get("test.gif");

        FrameProducer producer = new FrameProducer() {
            private long frameCounter = 0;

            @Override
            public List<Stream> produceStreams() {
                return Collections.singletonList(new Stream()
                        .setType(Stream.Type.VIDEO)
                        .setTimebase(1000L)
                        .setWidth(320)
                        .setHeight(240)
                );
            }

            @Override
            public Frame produce() {
                if (frameCounter > 30) {
                    return null;
                }
                System.out.println("Creating frame " + frameCounter);

                BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D graphics = image.createGraphics();
                graphics.setPaint(new Color(frameCounter * 1.0f / 30, 0, 0));
                graphics.fillRect(0, 0, 320, 240);

                Frame videoFrame = new Frame()
                        .setStreamId(0)
                        .setPts(frameCounter * 1000 / 10)
                        .setImage(image);
                frameCounter++;

                return videoFrame;
            }
        };

        FFmpegResult result = FFmpeg.atPath(ffmpegBin)
                .addInput(
                        FrameInput.withProducer(producer)
                )
                .addOutput(
                        UrlOutput.toPath(output)
                )
                .execute();
    }

    public static void main(String[] args) {
        Iterator<String> argIter = Arrays.asList(args).iterator();

        String ffmpegBin = null;

        while (argIter.hasNext()) {
            String arg = argIter.next();

            if ("-ffmpeg_bin".equals(arg)) {
                ffmpegBin = argIter.next();
            }
        }

        if (ffmpegBin == null) {
            System.err.println("Arguments: -ffmpeg_bin </path/to/ffmpeg/bin>");
            return;
        }

        new ProduceGif(Paths.get(ffmpegBin)).execute();
    }
}
