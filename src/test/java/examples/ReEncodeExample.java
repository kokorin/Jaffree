package examples;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegProgress;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.ProgressListener;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import java.util.concurrent.atomic.AtomicLong;

public class ReEncodeExample {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Exactly 2 arguments expected: path to source and destination media files");
            System.exit(1);
        }

        String pathToSrc = args[0];
        String pathToDst = args[1];

        final AtomicLong duration = new AtomicLong();
        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(pathToSrc))
                .setOverwriteOutput(true)
                .addOutput(new NullOutput())
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        duration.set(progress.getTimeMillis());
                    }
                })
                .execute();

        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(pathToSrc))
                .setOverwriteOutput(true)
                .addArguments("-movflags", "faststart")
                .addOutput(UrlOutput.toUrl(pathToDst))
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        double percents = 100. * progress.getTimeMillis() / duration.get();
                        System.out.println("Progress: " + percents + "%");
                    }
                })
                .execute();

    }
}
