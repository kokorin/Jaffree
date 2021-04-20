package examples;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.Frame;
import com.github.kokorin.jaffree.ffmpeg.FrameInput;
import com.github.kokorin.jaffree.ffmpeg.FrameProducer;
import com.github.kokorin.jaffree.ffmpeg.Stream;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ProduceVideoExample {

    public void execute() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Exactly 1 argument expected: path to media file");
            System.exit(1);
        }

        String pathToVideo = args[0];

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
                    return null; // return null when End of Stream is reached
                }

                BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D graphics = image.createGraphics();
                graphics.setPaint(new Color(frameCounter * 1.0f / 30, 0, 0));
                graphics.fillRect(0, 0, 320, 240);
                long pts = frameCounter * 1000 / 10; // Frame PTS in Stream Timebase
                Frame videoFrame = Frame.createVideoFrame(0, pts, image);
                frameCounter++;

                return videoFrame;
            }
        };

        FFmpeg.atPath()
                .addInput(FrameInput.withProducer(producer))
                .addOutput(UrlOutput.toUrl(pathToVideo))
                .execute();
    }
}
