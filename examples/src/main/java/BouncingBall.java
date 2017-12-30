import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffmpeg.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BouncingBall {
    private final Path ffmpegBin;
    private final int width = 640;
    private final int height = 480;
    private final int fps = 24;
    private final long sampleRate = 44100;
    private final int ballRadius = (width + height) / 20;
    private final long duration = 30_000;
    private final int bounceFrequency = 250;
    private final long bounceDuration = 200;

    private static final Logger LOGGER = LoggerFactory.getLogger(BouncingBall.class);

    public BouncingBall(String ffmpegBin) {
        this.ffmpegBin = Paths.get(ffmpegBin);
    }

    public void execute() {
        Path output = Paths.get("bouncing_ball.mp4");

        FrameProducer frameProducer = new FrameProducer() {
            private int ballCenterX = width / 2;
            private int ballCenterY = height / 2;
            private int ballVelocityX = ballRadius * (ThreadLocalRandom.current().nextInt(10) + 1);
            private int ballVelocityY = ballRadius * (ThreadLocalRandom.current().nextInt(10) + 1);
            private long nextVideoTimecode = 0;
            private long nextAudioTimecode = 0;
            private long nextCollisionCheckTimecode = 0;
            private long collisionVideoTimecode = Integer.MAX_VALUE;
            private long collisionAudioTimecode = Integer.MIN_VALUE;
            private Color ballColor = new Color(255, 0, 0);

            @Override
            public List<Stream> produceStreams() {
                return Arrays.asList(
                        new Stream().setType(Stream.Type.VIDEO)
                                .setId(0)
                                .setTimebase(1000L)
                                .setWidth(width)
                                .setHeight(height),
                        new Stream().setType(Stream.Type.AUDIO)
                                .setId(1)
                                // Better to use the same value as sampleRate,
                                // but this was written before NUT replaced MKV
                                // and now I'm lazy to fix it
                                .setTimebase(1000L)
                                .setSampleRate(sampleRate)
                                .setChannels(1)
                );
            }

            @Override
            public Frame produce() {
                if (nextVideoTimecode >= duration && nextAudioTimecode >= duration) {
                    LOGGER.info("Finished");
                    return null;
                }

                //
                // collision detection
                //

                long maxTimecode = Math.min(nextVideoTimecode, nextAudioTimecode);
                boolean collisionDetected = false;

                if (nextCollisionCheckTimecode < maxTimecode) {
                    long deltaTime = maxTimecode - nextCollisionCheckTimecode;
                    ballCenterX += ballVelocityX * deltaTime / 1000;
                    ballCenterY += ballVelocityY * deltaTime / 1000;

                    if (ballCenterX <= ballRadius) {
                        ballCenterX = 2 * ballRadius - ballCenterX;
                        ballVelocityX *= -1;
                        collisionDetected = true;
                    } else if (ballCenterX + ballRadius > width) {
                        ballCenterX = 2 * (width - ballRadius) - ballCenterX;
                        ballVelocityX *= -1;
                        collisionDetected = true;
                    }

                    if (ballCenterY <= ballRadius) {
                        ballCenterY = 2 * ballRadius - ballCenterY;
                        ballVelocityY *= -1;
                        collisionDetected = true;
                    } else if (ballCenterY + ballRadius > height) {
                        ballCenterY = 2 * (height - ballRadius) - ballCenterY;
                        ballVelocityY *= -1;
                        collisionDetected = true;
                    }

                    nextCollisionCheckTimecode = maxTimecode;
                }

                if (collisionDetected) {
                    collisionVideoTimecode = collisionAudioTimecode = maxTimecode;
                }


                if (nextVideoTimecode <= nextAudioTimecode) {
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = image.createGraphics();
                    graphics.setPaint(new Color(0, 0, 0));
                    graphics.fillRect(0, 0, width, height);
                    graphics.setPaint(ballColor);
                    graphics.fillOval(ballCenterX - ballRadius, ballCenterY - ballRadius, ballRadius * 2, ballRadius * 2);

                    Frame videoFrame = new Frame()
                            .setStreamId(0)
                            .setPts(nextVideoTimecode)
                            .setImage(image);

                    if (collisionVideoTimecode <= nextVideoTimecode) {
                        Random random = new Random();
                        ballColor = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                        collisionVideoTimecode = Integer.MAX_VALUE;
                    }

                    nextVideoTimecode += 1000 / fps;
                    return videoFrame;
                }

                if (nextAudioTimecode <= nextVideoTimecode) {
                    int nSamples = (int) (sampleRate / fps);
                    int[] samples = new int[nSamples];

                    if (nextAudioTimecode < collisionAudioTimecode + bounceDuration) {
                        for (int i = 0; i < nSamples; i++) {
                            double tSeconds = nextAudioTimecode / 1000. + i * 1.0 / sampleRate;
                            double amp = 2_0000_000_000L * Math.sin(bounceFrequency * 2 * Math.PI * tSeconds);
                            samples[i] = (int) amp;
                        }
                    } else {
                        collisionAudioTimecode = Integer.MIN_VALUE;
                    }


                    Frame audioFrame = new Frame()
                            .setStreamId(1)
                            .setPts(nextAudioTimecode)
                            .setSamples(samples);

                    nextAudioTimecode += 1000 / fps;
                    return audioFrame;
                }

                return null;
            }
        };

        FFmpegResult result = FFmpeg.atPath(ffmpegBin)
                .addInput(
                        FrameInput.withProducer(frameProducer)
                )
                .setOverwriteOutput(true)
                .addOutput(
                        UrlOutput.toPath(output)
                )
                .execute();

        if (result != null) {
            LOGGER.info("Finished successfully: " + result);
        }
    }

    public static void main(String[] args) {
        Iterator<String> argIter = Arrays.asList(args).iterator();

        String ffmpegBin = null;

        while (argIter.hasNext()) {
            String argName = argIter.next();

            if (!argIter.hasNext()) {
                return;
            }

            String argValue = argIter.next();

            if ("-ffmpeg_bin".equals(argName)) {
                ffmpegBin = argValue;
            }
        }

        if (ffmpegBin == null) {
            LOGGER.error("Usage: java -jar BouncingBall.jar -ffmpeg_bin </path/to/ffmpeg/bin>");
            return;
        }

        new BouncingBall(ffmpegBin).execute();
    }

}
