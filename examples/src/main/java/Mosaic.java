import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffmpeg.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Mosaic {
    private final Path ffmpegBin;
    private final List<String> inputs;

    public static final Logger LOGGER = LoggerFactory.getLogger(Mosaic.class);

    public Mosaic(String ffmpegBin, List<String> inputs) {
        this.ffmpegBin = Paths.get(ffmpegBin);
        this.inputs = inputs;
    }

    public void execute() {
        final List<FFmpegResult> results = new CopyOnWriteArrayList<>();
        List<FrameIterator> frameIterators = new ArrayList<>();

        for (int i = 0; i < inputs.size(); i++) {
            String input = inputs.get(i);
            FrameIterator frameIterator = new FrameIterator();
            frameIterators.add(frameIterator);

            final FFmpeg ffmpeg = FFmpeg.atPath(ffmpegBin)
                    .addInput(UrlInput.fromUrl(input))
                    .addOutput(FrameOutput.withConsumer(frameIterator.getConsumer()))
                    .setContextName("input" + i);

            Thread ffmpegThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    FFmpegResult result = ffmpeg.execute();
                    results.add(result);

                }
            }, "FFmpeg");
            ffmpegThread.setDaemon(true);
            ffmpegThread.start();
        }

        FrameProducer frameProducer = produceMosaic(frameIterators);

        FFmpegResult mosaicResult = FFmpeg.atPath(ffmpegBin)
                .addInput(FrameInput.withProducer(frameProducer))
                .setOverwriteOutput(true)
                .addOutput(UrlOutput.toUrl("mosaic.mp4"))
                .setContextName("result")
                .execute();
    }

    private FrameProducer produceMosaic(final List<FrameIterator> frameIterators) {
        final int rows = (int) Math.round(Math.sqrt(frameIterators.size()));
        final int columns = (int) Math.ceil(1.0 * frameIterators.size() / rows);

        return new FrameProducer() {
            private final int elementWidth = 320;
            private final int elementHeight = 240;
            private final int mosaicWidth = columns * elementWidth;
            private final int mosaicHeight = rows * elementHeight;

            private final List<Deque<VideoFrame>> videoQueues = new ArrayList<>(Collections.nCopies(frameIterators.size(), (Deque<VideoFrame>)null));
            private final List<Deque<AudioFrame>> audioQueues = new ArrayList<>(Collections.nCopies(frameIterators.size(), (Deque<AudioFrame>)null));
            private long timecode = 0;
            private long nextVideoFrameTimecode = 0;
            private long nextAudioFrameTimecode = 0;

            @Override
            public List<Track> produceTracks() {
                return Collections.singletonList(
                        new Track()
                                .setType(Track.Type.VIDEO)
                                .setWidth(mosaicWidth)
                                .setHeight(mosaicHeight)
                                .setId(1)
                );
            }

            @Override
            public Frame produce() {
                fillFrameQueues();

                // TODO while audio frames aren't used
                for (Deque<AudioFrame> deque : audioQueues) {
                    deque.clear();
                }

                if (nextVideoFrameTimecode <= timecode) {
                    return produceVideoFrame();
                }
                if (nextAudioFrameTimecode <= timecode) {
                    return produceAudioFrame();
                }

                return null;
            }

            public VideoFrame produceVideoFrame() {
                VideoFrame[] videoFrames = new VideoFrame[frameIterators.size()];

                for (int i = 0; i < videoQueues.size(); i++) {
                    Deque<VideoFrame> frameDeque = videoQueues.get(i);
                    VideoFrame prevFrame = null;
                    while (!frameDeque.isEmpty()) {
                        VideoFrame frame = frameDeque.pollFirst();
                        if (frame == null) {
                            break;
                        }

                        if (frame.getTimecode() <= nextVideoFrameTimecode) {
                            prevFrame = frame;
                            continue;
                        }

                        videoFrames[i] = prevFrame;
                        frameDeque.addFirst(frame);

                        break;
                    }
                }

                BufferedImage mosaic = new BufferedImage(mosaicWidth, mosaicHeight, BufferedImage.TYPE_INT_RGB);
                Graphics mosaicGraphics = mosaic.getGraphics();
                mosaicGraphics.setColor(new Color(0));
                mosaicGraphics.fillRect(0, 0, mosaicWidth, mosaicHeight);

                boolean atLeastHasOneElement = false;

                for (int i = 0; i < videoFrames.length; i++) {
                    VideoFrame videoFrame = videoFrames[i];
                    if (videoFrame == null) {
                        continue;
                    }

                    atLeastHasOneElement = true;

                    BufferedImage element = videoFrame.getImage();
                    int row = i / columns;
                    int column = i % columns;

                    ImageObserver observer = new ImageObserver() {
                        @Override
                        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                            return false;
                        }
                    };

                    int dx1 = column * elementWidth;
                    int dy1 = row * elementHeight;
                    int dx2 = dx1 + elementWidth;
                    int dy2 = dy1 + elementHeight;

                    mosaicGraphics.drawImage(element,
                            dx1, dy1, dx2, dy2,
                            0, 0, elementWidth, elementHeight,
                            observer
                    );
                }

                if (!atLeastHasOneElement) {
                    return null;
                }

                VideoFrame result = new VideoFrame();
                result.setImage(mosaic);
                result.setTimecode(nextVideoFrameTimecode);
                result.setTrack(1);

                // 25 FPS
                nextVideoFrameTimecode += 40;
                timecode += 40;

                return result;
            }

            private Frame produceAudioFrame() {
                return null;
            }

            private void fillFrameQueues() {
                long readUpToTimecode = Math.max(nextVideoFrameTimecode, nextAudioFrameTimecode) + 500;
                for (int i = 0; i < frameIterators.size(); i++) {
                    FrameIterator frameIterator = frameIterators.get(i);

                    while (frameIterator.hasNext()) {
                        Frame frame = frameIterator.next();

                        if (frame instanceof VideoFrame) {
                            Deque<VideoFrame> videoQueue = videoQueues.get(i);
                            if (videoQueue == null) {
                                videoQueue = new LinkedList<>();
                                videoQueues.set(i, videoQueue);
                            }
                            videoQueue.addLast((VideoFrame) frame);
                        } else if (frame instanceof AudioFrame) {
                            Deque<AudioFrame> audioQueue = audioQueues.get(i);
                            if (audioQueue == null) {
                                audioQueue = new LinkedList<>();
                                audioQueues.set(i, audioQueue);
                            }
                            audioQueue.addLast((AudioFrame) frame);
                        }

                        if (frame.getTimecode() > readUpToTimecode) {
                            break;
                        }
                    }
                }
            }

            private int[] resample(int[] samples, long fromRate, long toRate) {
                if (fromRate == toRate) {
                    return samples;
                }

                long durationMillis = 1000 * samples.length / fromRate;
                int[] result = new int[(int) (toRate * durationMillis / 1000)];

                for (int i = 0; i < result.length; i++) {
                    double fromI = 1. * samples.length * i / result.length;
                    int left = (int) Math.floor(fromI);
                    int right = (int) Math.ceil(fromI);
                    double leftFactor = right - fromI;
                    double rightFactor = 1.0 - leftFactor;

                    result[i] = (int) (left * leftFactor + right * rightFactor);
                }

                return result;
            }
        };
    }

    public static void main(String[] args) {
        Iterator<String> argIter = Arrays.asList(args).iterator();

        String ffmpegBin = null;
        List<String> inputs = new ArrayList<>();

        while (argIter.hasNext()) {
            String arg = argIter.next();

            if ("-ffmpeg_bin".equals(arg)) {
                ffmpegBin = argIter.next();
            } else {
                inputs.add(arg);
            }
        }

        if (ffmpegBin == null) {
            LOGGER.error("Usage: java -cp examples.jar Mosaic -ffmpeg_bin </path/to/ffmpeg/bin>");
            return;
        }

        new Mosaic(ffmpegBin, inputs).execute();
    }

    public static class FrameIterator implements Iterator<Frame> {
        private volatile boolean hasNext = true;
        private volatile Frame next = null;
        private volatile List<Track> tracks;

        private final FrameConsumer consumer = new FrameConsumer() {
            @Override
            public void consumeTracks(List<Track> tracks) {
                FrameIterator.this.tracks = tracks;
            }

            @Override
            public void consume(Frame frame) {
                while (next != null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        LOGGER.warn("Exception while supplying frame", e);
                    }
                }

                hasNext = frame != null;
                next = frame;
            }
        };

        @Override
        public boolean hasNext() {
            waitForNextFrame();
            return hasNext;
        }

        @Override
        public Frame next() {
            waitForNextFrame();
            Frame result = next;
            next = null;
            return result;
        }

        public FrameConsumer getConsumer() {
            return consumer;
        }

        public List<Track> getTracks() {
            return tracks;
        }

        private void waitForNextFrame() {
            while (hasNext && next == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    LOGGER.warn("Exception while waiting for frame", e);
                }
            }
        }
    }
}
