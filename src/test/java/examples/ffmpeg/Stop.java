package examples.ffmpeg;

import com.github.kokorin.jaffree.ffmpeg.*;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Stop {
    public static void stopWithException(final FFmpeg ffmpeg) throws Exception {
        final AtomicBoolean stopped = new AtomicBoolean();
        ffmpeg.setProgressListener(
                new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        if (stopped.get()) {
                            throw new RuntimeException("Stooped with exception!");
                        }
                    }
                }
        );

        final AtomicReference<FFmpegResult> result = new AtomicReference<>();

        new Thread() {
            @Override
            public void run() {
                FFmpegResult r = ffmpeg.execute();
                result.set(r);
            }
        }.start();

        Thread.sleep(5_000);
        stopped.set(true);

        Thread.sleep(1_000);
        System.out.println(result.get());
    }

    public static void stopWithInterruption(final FFmpeg ffmpeg) throws Exception {
        final AtomicReference<FFmpegResult> result = new AtomicReference<>();

        Thread thread = new Thread() {
            @Override
            public void run() {
                FFmpegResult r = ffmpeg.execute();
                result.set(r);
            }
        };
        thread.start();

        Thread.sleep(5_000);
        thread.interrupt();

        Thread.sleep(1_000);
        System.out.println(result.get());
    }

    public static void stopWithFutureCancellation(final FFmpeg ffmpeg) throws Exception {
        Future<FFmpegResult> future = ffmpeg.executeAsync();

        Thread.sleep(5_000);
        // We must pass mayInterruptIfRunning true, otherwise process won't be interrupted
        future.cancel(true);

        Thread.sleep(1_000);
        System.out.println(future.get());
    }

    public static void main(String[] args) throws Exception {
        FFmpeg ffmpeg = createTestFFmpeg();
        stopWithException(ffmpeg);

        ffmpeg = createTestFFmpeg();
        stopWithInterruption(ffmpeg);

        ffmpeg = createTestFFmpeg();
        stopWithInterruption(ffmpeg);
    }

    public static FFmpeg createTestFFmpeg() {
        return FFmpeg.atPath()
                .addInput(
                        UrlInput
                                .fromUrl("testsrc=duration=3600:size=1280x720:rate=30")
                                .setFormat("lavfi")
                )
                .addOutput(
                        new NullOutput()
                );
    }
}
