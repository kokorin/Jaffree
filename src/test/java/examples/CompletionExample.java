package examples;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegProgress;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResultFuture;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.ProgressListener;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CompletionExample {
    public static void completionWithException(final FFmpeg ffmpeg) throws Exception {
        final AtomicBoolean stopped = new AtomicBoolean();
        ffmpeg.setProgressListener(
                new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        if (stopped.get()) {
                            throw new RuntimeException("Stopped with exception!");
                        }
                    }
                }
        );

        final AtomicReference<FFmpegResult> result = new AtomicReference<>();

        ffmpeg.executeAsync().toCompletableFuture().thenAccept(result::set).exceptionally(ex -> {
            System.out.println("Completion exception: " + ex);
            return null;
        });

        Thread.sleep(5_000);
        stopped.set(true);

        Thread.sleep(1_000);
        System.out.println(result.get());
    }

    public static void completionWithGracefulStop(final FFmpeg ffmpeg) throws Exception {
        final AtomicReference<FFmpegResult> result = new AtomicReference<>();

        FFmpegResultFuture future = ffmpeg.executeAsync();
        future.toCompletableFuture().thenAccept(result::set);

        Thread.sleep(5_000);
        future.graceStop();

        Thread.sleep(1_000);
        System.out.println(result.get());
    }

    public static void main(String[] args) throws Exception {
        FFmpeg ffmpeg;
        ffmpeg = createTestFFmpeg();
        completionWithException(ffmpeg);

        ffmpeg = createTestFFmpeg();
        completionWithGracefulStop(ffmpeg);
    }

    public static FFmpeg createTestFFmpeg() {
        return FFmpeg.atPath()
                .addInput(
                        UrlInput
                                .fromUrl("testsrc=duration=3600:size=1280x720:rate=30")
                                .setFormat("lavfi")
                )
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        //System.out.println(progress);
                    }
                })
                .addOutput(
                        new NullOutput()
                );
    }
}
