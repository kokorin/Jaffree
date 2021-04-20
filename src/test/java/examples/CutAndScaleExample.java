package examples;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.OutputListener;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import java.util.concurrent.TimeUnit;

public class CutAndScaleExample {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Exactly 2 arguments expected: path to source and destination media files");
            System.exit(1);
        }

        String pathToSrc = args[0];
        String pathToDst = args[1];

        FFmpeg.atPath()
                .addInput(
                        UrlInput.fromUrl(pathToSrc)
                                .setPosition(10, TimeUnit.SECONDS)
                                .setDuration(42, TimeUnit.SECONDS)
                )
                .setFilter(StreamType.VIDEO, "scale=160:-2")
                .setOverwriteOutput(true)
                .addArguments("-movflags", "faststart")
                .addOutput(
                        UrlOutput.toUrl(pathToDst)
                                .setPosition(10, TimeUnit.SECONDS)
                )
                .execute();
    }
}
