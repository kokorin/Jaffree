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
    private final int sampleRate = 44100;

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
                    .addInput(UrlInput
                            .fromUrl(input)
                            .setDuration(15_000))
                    .addOutput(FrameOutput
                            .withConsumer(frameIterator.getConsumer())
                            .addOption("-ac", "1")
                            .addOption("-ar", Integer.toString(sampleRate))
                    )
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
            // Millis to read frames ahead into Deques
            private final long readAheadMillis = 1000;
            private final long videoFrameDuration = 1000 / 25;
            private final long audioFrameDuration = 500;

            private final List<Deque<Frame>> videoQueues = new ArrayList<>(Collections.nCopies(frameIterators.size(), (Deque<Frame>) null));
            private final List<Deque<Frame>> audioQueues = new ArrayList<>(Collections.nCopies(frameIterators.size(), (Deque<Frame>) null));
            private final long[] audioSamplesRead = new long[frameIterators.size()];
            private long audioSamplesWritten = 0;
            private long timecode = 0;
            private long nextVideoFrameTimecode = 0;
            private long nextAudioFrameTimecode = 0;

            @Override
            public List<Stream> produceStreams() {
                return Arrays.asList(
                        new Stream()
                                .setId(0)
                                .setType(Stream.Type.VIDEO)
                                .setTimebase(1000L)
                                .setWidth(mosaicWidth)
                                .setHeight(mosaicHeight)                        ,
                        new Stream()
                                .setId(1)
                                .setType(Stream.Type.AUDIO)
                                .setTimebase((long)sampleRate)
                                .setChannels(1)
                                .setSampleRate(sampleRate)
                );
            }

            @Override
            public Frame produce() {
                fillFrameQueues();

                Frame result = null;
                if (nextVideoFrameTimecode <= timecode) {
                    result = produceVideoFrame();
                } else if (nextAudioFrameTimecode <= timecode) {
                    result = produceAudioFrame();
                }

                timecode = Math.min(nextVideoFrameTimecode, nextAudioFrameTimecode);

                return result;
            }

            public Frame produceVideoFrame() {
                Frame[] videoFrames = new Frame[frameIterators.size()];

                for (int i = 0; i < videoQueues.size(); i++) {
                    Deque<Frame> frameDeque = videoQueues.get(i);
                    Frame prevFrame = null;
                    while (!frameDeque.isEmpty()) {
                        Frame frame = frameDeque.pollFirst();
                        if (frame == null) {
                            break;
                        }

                        Stream stream = null;
                        for (Stream testStream : frameIterators.get(i).getTracks()) {
                            if (testStream.getId() == frame.getStreamId()) {
                                stream = testStream;
                                break;
                            }
                        }

                        long frameTs = 1000L * frame.getPts() / stream.getTimebase();
                        if (frameTs <= nextVideoFrameTimecode) {
                            prevFrame = frame;
                            continue;
                        }

                        videoFrames[i] = prevFrame;
                        frameDeque.addFirst(frame);
                        // If target FPS is bigger that source, we use the same source frame
                        // for several target frames
                        if (prevFrame != null) {
                            frameDeque.addFirst(prevFrame);
                        }
                        break;
                    }
                }

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

                Frame result = new Frame();
                result.setImage(mosaic);
                result.setPts(nextVideoFrameTimecode);
                result.setStreamId(0);

                nextVideoFrameTimecode += videoFrameDuration;

                return result;
            }

            // While producing audio frames we have to count written and read number of samples.
            // Frame can't have rational timecode in MKV.
            private Frame produceAudioFrame() {
                int[] samples = new int[(int) (sampleRate * audioFrameDuration / 1000)];

                for (int i = 0; i < audioQueues.size(); i++) {
                    Deque<Frame> deque = audioQueues.get(i);
                    // Video without audio
                    if (deque == null) {
                        continue;
                    }

                    while (!deque.isEmpty()) {
                        Frame frame = deque.pollFirst();
                        // ffmpeg resamples audio track and set channels to 1
                        int[] srcSamples = frame.getSamples();

                        // first sample to write into result
                        int firstSample = (int) (audioSamplesRead[i] - audioSamplesWritten);

                        // Source audio frame starts before target audio frame
                        if (firstSample >= 0) {
                            for (int j = 0; (j < srcSamples.length) && (j + firstSample < samples.length); j++) {
                                samples[j + firstSample] += srcSamples[j];
                            }
                        } else {
                            int absFirstSample = Math.abs(firstSample);
                            for (int j = 0; (j + absFirstSample < srcSamples.length) && (j < samples.length); j++) {
                                samples[j] += srcSamples[j + absFirstSample];
                            }
                        }

                        // Current target audio frame ends before source audio frame
                        if (audioSamplesRead[i] + srcSamples.length > audioSamplesWritten + samples.length) {
                            deque.addFirst(frame);
                            break;
                        }

                        audioSamplesRead[i] += srcSamples.length;
                    }
                }

                Frame result = new Frame();
                result.setSamples(samples);
                result.setPts(sampleRate * nextAudioFrameTimecode / 1000);
                result.setStreamId(1);

                audioSamplesWritten += samples.length;
                nextAudioFrameTimecode += audioFrameDuration;

                return result;
            }

            private void fillFrameQueues() {
                long readUpToTimecode = Math.max(nextVideoFrameTimecode, nextAudioFrameTimecode) + readAheadMillis;

                for (int i = 0; i < frameIterators.size(); i++) {
                    FrameIterator frameIterator = frameIterators.get(i);

                    while (frameIterator.hasNext()) {
                        Frame frame = frameIterator.next();
                        Stream stream = null;
                        for (Stream testStream : frameIterator.getTracks()) {
                            if (testStream.getId() == frame.getStreamId()) {
                                stream = testStream;
                                break;
                            }
                        }

                        if (stream.getType() == Stream.Type.VIDEO) {
                            Deque<Frame> videoQueue = videoQueues.get(i);
                            if (videoQueue == null) {
                                videoQueue = new LinkedList<>();
                                videoQueues.set(i, videoQueue);
                            }
                            videoQueue.addLast(frame);
                        } else if (stream.getType() == Stream.Type.AUDIO) {
                            Deque<Frame> audioQueue = audioQueues.get(i);
                            if (audioQueue == null) {
                                audioQueue = new LinkedList<>();
                                audioQueues.set(i, audioQueue);
                            }
                            audioQueue.addLast(frame);
                        }

                        long frameTs = 1000L * frame.getPts() / stream.getTimebase();
                        if (frameTs > readUpToTimecode) {
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
            LOGGER.error("Usage: java -cp examples.jar Mosaic -ffmpeg_bin </path/to/ffmpeg/bin>");
            return;
        }

        new Mosaic(ffmpegBin, inputs).execute();
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

        public FrameConsumer getConsumer() {
            return consumer;
        }

        public List<Stream> getTracks() {
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
