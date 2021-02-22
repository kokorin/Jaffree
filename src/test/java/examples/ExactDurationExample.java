package examples;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegProgress;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.ProgressListener;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;

import java.util.concurrent.atomic.AtomicLong;

public class ExactDurationExample {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Exactly 1 argument expected: path to media file");
            System.exit(1);
        }

        String pathToVideo = args[0];

        final AtomicLong durationMillis = new AtomicLong();

        FFmpegResult ffmpegResult = FFmpeg.atPath()
                .addInput(
                        UrlInput.fromUrl(pathToVideo)
                )
                .addOutput(new NullOutput())
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        durationMillis.set(progress.getTimeMillis());
                    }
                })
                .execute();

        System.out.println("Exact duration: " + durationMillis.get() + " milliseconds");
    }
}
