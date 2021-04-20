package examples;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.Frame;
import com.github.kokorin.jaffree.ffmpeg.FrameInput;
import com.github.kokorin.jaffree.ffmpeg.FrameProducer;
import com.github.kokorin.jaffree.ffmpeg.Stream;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BouncingBallExample {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private static final int FPS = 24;
    private static final long SAMPLE_RATE = 44100;
    private static final int BALL_RADIUS = (WIDTH + HEIGHT) / 20;
    private static final long DURATION = 30_000;
    private static final int BOUNCE_FREQUENCY = 250;
    private static final long BOUNCE_DURATION = 200;

    private static final Logger LOGGER = LoggerFactory.getLogger(BouncingBallExample.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Exactly 1 argument expected: path to output media file");
            System.exit(1);
        }

        Path pathToVideo = Paths.get(args[0]);

        FrameProducer frameProducer = new FrameProducer() {
            private int ballCenterX = WIDTH / 2;
            private int ballCenterY = HEIGHT / 2;
            private int ballVelocityX = BALL_RADIUS * (ThreadLocalRandom.current().nextInt(10) + 1);
            private int ballVelocityY = BALL_RADIUS * (ThreadLocalRandom.current().nextInt(10) + 1);
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
                                .setWidth(WIDTH)
                                .setHeight(HEIGHT),
                        new Stream().setType(Stream.Type.AUDIO)
                                .setId(1)
                                // Better to use the same value as sampleRate,
                                // but this was written before NUT replaced MKV
                                // and now I'm lazy to fix it
                                .setTimebase(1000L)
                                .setSampleRate(SAMPLE_RATE)
                                .setChannels(1)
                );
            }

            @Override
            public Frame produce() {
                if (nextVideoTimecode >= DURATION && nextAudioTimecode >= DURATION) {
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

                    if (ballCenterX <= BALL_RADIUS) {
                        ballCenterX = 2 * BALL_RADIUS - ballCenterX;
                        ballVelocityX *= -1;
                        collisionDetected = true;
                    } else if (ballCenterX + BALL_RADIUS > WIDTH) {
                        ballCenterX = 2 * (WIDTH - BALL_RADIUS) - ballCenterX;
                        ballVelocityX *= -1;
                        collisionDetected = true;
                    }

                    if (ballCenterY <= BALL_RADIUS) {
                        ballCenterY = 2 * BALL_RADIUS - ballCenterY;
                        ballVelocityY *= -1;
                        collisionDetected = true;
                    } else if (ballCenterY + BALL_RADIUS > HEIGHT) {
                        ballCenterY = 2 * (HEIGHT - BALL_RADIUS) - ballCenterY;
                        ballVelocityY *= -1;
                        collisionDetected = true;
                    }

                    nextCollisionCheckTimecode = maxTimecode;
                }

                if (collisionDetected) {
                    collisionVideoTimecode = collisionAudioTimecode = maxTimecode;
                }


                if (nextVideoTimecode <= nextAudioTimecode) {
                    BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
                    Graphics2D graphics = image.createGraphics();
                    graphics.setPaint(new Color(0, 0, 0));
                    graphics.fillRect(0, 0, WIDTH, HEIGHT);
                    graphics.setPaint(ballColor);
                    graphics.fillOval(ballCenterX - BALL_RADIUS, ballCenterY - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);

                    Frame videoFrame = Frame.createVideoFrame(0, nextVideoTimecode, image);

                    if (collisionVideoTimecode <= nextVideoTimecode) {
                        Random random = new Random();
                        ballColor = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                        collisionVideoTimecode = Integer.MAX_VALUE;
                    }

                    nextVideoTimecode += 1000 / FPS;
                    return videoFrame;
                }

                if (nextAudioTimecode <= nextVideoTimecode) {
                    int nSamples = (int) (SAMPLE_RATE / FPS);
                    int[] samples = new int[nSamples];

                    if (nextAudioTimecode < collisionAudioTimecode + BOUNCE_DURATION) {
                        for (int i = 0; i < nSamples; i++) {
                            double tSeconds = nextAudioTimecode / 1000. + i * 1.0 / SAMPLE_RATE;
                            double amp = 2_0000_000_000L * Math.sin(BOUNCE_FREQUENCY * 2 * Math.PI * tSeconds);
                            samples[i] = (int) amp;
                        }
                    } else {
                        collisionAudioTimecode = Integer.MIN_VALUE;
                    }


                    Frame audioFrame = Frame.createAudioFrame(1, nextAudioTimecode, samples);

                    nextAudioTimecode += 1000 / FPS;
                    return audioFrame;
                }

                return null;
            }
        };

        FFmpegResult result = FFmpeg.atPath()
                .addInput(
                        FrameInput.withProducer(frameProducer)
                )
                .setOverwriteOutput(true)
                .addOutput(
                        UrlOutput.toPath(pathToVideo)
                )
                .execute();

        if (result != null) {
            LOGGER.info("Finished successfully: " + result);
        }
    }

}
