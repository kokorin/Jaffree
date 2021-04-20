package examples;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.Frame;
import com.github.kokorin.jaffree.ffmpeg.FrameConsumer;
import com.github.kokorin.jaffree.ffmpeg.FrameInput;
import com.github.kokorin.jaffree.ffmpeg.FrameOutput;
import com.github.kokorin.jaffree.ffmpeg.FrameProducer;
import com.github.kokorin.jaffree.ffmpeg.ImageFormats;
import com.github.kokorin.jaffree.ffmpeg.Stream;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Note: for some reason ffmpeg 3.3.1 fails when using amix or amerge filters (in this example)
 * Use ffmpeg 4.0 at least, amix works (while amerge still fails)
 */
public class MosaicExample {
    private final Path ffmpegBin;
    private final List<String> inputs;
    private final int sampleRate = 44100;
    private final int frameRate = 25;

    public static final Logger LOGGER = LoggerFactory.getLogger(MosaicExample.class);

    public MosaicExample(String ffmpegBin, List<String> inputs) {
        this.ffmpegBin = Paths.get(ffmpegBin);
        this.inputs = inputs;
    }

    public void execute() {
        final List<FFmpegResult> results = new CopyOnWriteArrayList<>();
        List<FrameIterator> frameIterators = new ArrayList<>();

        for (int i = 0; i < inputs.size(); i++) {
            String input = inputs.get(i);

            boolean hasAudioStream = false;
            // We must test input for audio stream
            // Other corner-cases (no video stream, multiple audio stream are not considered)
            FFprobeResult probeResult = FFprobe.atPath(ffmpegBin)
                    .setShowStreams(true)
                    .setInput(Paths.get(input))
                    .execute();
            for (com.github.kokorin.jaffree.ffprobe.Stream stream : probeResult.getStreams()) {
                if (StreamType.AUDIO == stream.getCodecType()) {
                    hasAudioStream = true;
                    break;
                }
            }

            FrameIterator frameIterator = new FrameIterator();
            frameIterators.add(frameIterator);

            final FFmpeg ffmpeg = FFmpeg.atPath(ffmpegBin)
                    .addInput(UrlInput
                            .fromUrl(input)
                            .setDuration(15_000))
                    .addOutput(FrameOutput
                            .withConsumer(frameIterator.getConsumer())
                            .setFrameRate(frameRate)
                            .addArguments("-ac", "1")
                            .addArguments("-ar", Integer.toString(sampleRate))
                    )
                    .setContextName("input" + i);

            if (!hasAudioStream) {
                ffmpeg
                        .addInput(
                                UrlInput.fromUrl("anullsrc")
                                        .setFormat("lavfi")
                        )
                        .addArgument("-shortest");
            }

            Thread ffmpegThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    FFmpegResult result = ffmpeg.execute();
                    results.add(result);

                }
            }, "Reader-" + i + "-main");
            ffmpegThread.setDaemon(true);
            ffmpegThread.start();
        }

        FrameProducer frameProducer = produceMosaic(frameIterators);

        FFmpegResult mosaicResult = FFmpeg.atPath(ffmpegBin)
                .addInput(
                        FrameInput
                                .withProducer(frameProducer, ImageFormats.BGR24, 5_000L)
                                .setFrameRate(frameRate)
                )
                .setOverwriteOutput(true)
                .setLogLevel(LogLevel.TRACE)
                .addOutput(UrlOutput
                                .toUrl("mosaic.mp4")
                        //.addMap(0)
                )
                .addArguments("-filter_complex", "amix=inputs=" + inputs.size())
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
            private final long videoFrameDuration = 1000 / frameRate;

            private final Map<Integer, Deque<Frame>> audioQueues = new HashMap<>();
            private long timecode = 0;
            private final Frame[] nextVideoFrames = new Frame[frameIterators.size()];
            private long nextVideoFrameTimecode = 0;
            private long nextAudioFrameTimecode = 0;

            @Override
            public List<Stream> produceStreams() {
                List<Stream> streams = new ArrayList<>();
                streams.add(
                        new Stream()
                                .setId(0)
                                .setType(Stream.Type.VIDEO)
                                .setTimebase(1000L)
                                .setWidth(mosaicWidth)
                                .setHeight(mosaicHeight)
                );

                // We copy all audio streams to output and merge tham with amerge filter
                for (int i = 0; i < frameIterators.size(); i++) {
                    streams.add(
                            new Stream()
                                    .setId(1 + i)
                                    .setType(Stream.Type.AUDIO)
                                    .setTimebase((long) sampleRate)
                                    .setChannels(1)
                                    .setSampleRate(sampleRate)
                    );
                }

                return streams;
            }

            @Override
            public Frame produce() {
                Frame result = null;
                if (nextVideoFrameTimecode <= timecode) {
                    if (nextVideoFrameTimecode == 0) {
                        readNextVideoFrames(nextVideoFrameTimecode);
                    }

                    result = produceVideoFrame(nextVideoFrames);
                    // We have to read video frames ahead, otherwise we will have no audio frames read
                    readNextVideoFrames(nextVideoFrameTimecode);
                } else if (nextAudioFrameTimecode <= timecode) {
                    result = produceAudioFrame();
                }

                timecode = Math.min(nextVideoFrameTimecode, nextAudioFrameTimecode);

                return result;
            }

            public Frame produceVideoFrame(Frame[] videoFrames) {
                BufferedImage mosaic = new BufferedImage(mosaicWidth, mosaicHeight, BufferedImage.TYPE_3BYTE_BGR);
                Graphics mosaicGraphics = mosaic.getGraphics();
                mosaicGraphics.setColor(new Color(0));
                mosaicGraphics.fillRect(0, 0, mosaicWidth, mosaicHeight);

                boolean atLeastHasOneElement = false;

                for (int i = 0; i < videoFrames.length; i++) {
                    Frame videoFrame = videoFrames[i];
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
                            0, 0, element.getWidth(), element.getHeight(),
                            observer
                    );
                }

                if (!atLeastHasOneElement) {
                    return null;
                }

                Frame result = Frame.createVideoFrame(0, nextVideoFrameTimecode, mosaic);

                nextVideoFrameTimecode += videoFrameDuration;

                return result;
            }

            private Frame produceAudioFrame() {
                long minPts = Long.MAX_VALUE;
                long nextPts = Long.MAX_VALUE;
                int minI = -1;
                for (int i = 0; i < frameIterators.size(); i++) {
                    Deque<Frame> aQueue = audioQueues.get(i);
                    Frame frame = aQueue.peekFirst();
                    if (frame == null) {
                        continue;
                    }

                    long queuePts = frame.getPts();
                    if (queuePts < minPts) {
                        nextPts = minPts;
                        minPts = queuePts;
                        minI = i;
                        continue;
                    }
                    if (queuePts < nextPts) {
                        nextPts = queuePts;
                    }
                }

                if (minI == -1) {
                    return null;
                }

                Frame aFrame = audioQueues.get(minI).pollFirst();
                if (aFrame == null) {
                    return null;
                }
                aFrame = Frame.createAudioFrame(1 + minI, aFrame.getPts(), aFrame.getSamples());

                if (nextPts != Long.MAX_VALUE) {
                    nextAudioFrameTimecode = 1000L * nextPts / sampleRate;
                } else {
                    nextAudioFrameTimecode = nextVideoFrameTimecode;
                }

                return aFrame;
            }

            // All video input streams have forced frameRate, so we know that in every stream
            // frames will occur with the same timestamp
            private void readNextVideoFrames(long videoTs) {
                for (int i = 0; i < frameIterators.size(); i++) {
                    FrameIterator iter = frameIterators.get(i);

                    if (!iter.hasNext) {
                        nextVideoFrames[i] = null;
                    }

                    while (iter.hasNext) {
                        Frame frame = iter.next();
                        if (frame == null) {
                            break;
                        }

                        Stream stream = iter.getStream(frame.getStreamId());
                        if (stream == null) {
                            break;
                        }

                        switch (stream.getType()) {
                            case VIDEO:
                                nextVideoFrames[i] = frame;
                                break;
                            case AUDIO:
                                Deque<Frame> aQueue = audioQueues.get(i);
                                if (aQueue == null) {
                                    aQueue = new LinkedList<>();
                                    audioQueues.put(i, aQueue);
                                }
                                aQueue.addLast(frame);
                                break;
                        }
                        long frameTs = 1000L * frame.getPts() / stream.getTimebase();
                        if (frameTs >= videoTs) {
                            break;
                        }
                    }
                }
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

        if (ffmpegBin == null || inputs.isEmpty()) {
            System.err.println("Arguments: -ffmpeg_bin </path/to/ffmpeg/bin>");
            return;
        }

        new MosaicExample(ffmpegBin, inputs).execute();
    }

    public static class FrameIterator implements Iterator<Frame> {
        private volatile boolean hasNext = true;
        private volatile Frame next = null;
        private volatile List<Stream> tracks;

        private final FrameConsumer consumer = new FrameConsumer() {
            @Override
            public void consumeStreams(List<Stream> tracks) {
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

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        public FrameConsumer getConsumer() {
            return consumer;
        }

        public List<Stream> getTracks() {
            return tracks;
        }

        public Stream getStream(int id) {
            for (Stream stream : tracks) {
                if (stream.getId() == id) {
                    return stream;
                }
            }

            return null;
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
