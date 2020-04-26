package examples.ffprobe;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegProgress;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.ProgressListener;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ShowStreams {
    private final String ffmpegBin;
    private final String video;

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowStreams.class);

    public ShowStreams(String ffmpegBin, String video) {
        this.ffmpegBin = ffmpegBin;
        this.video = video;
    }

    public void execute() {
        FFprobe ffprobe;
        if (ffmpegBin != null) {
            ffprobe = FFprobe.atPath(Paths.get(ffmpegBin));
        } else {
            ffprobe = FFprobe.atPath();
        }

        FFprobeResult result = ffprobe
                .setShowStreams(true)
                .setInput(video)
                .execute();

        for (Stream stream : result.getStreams()) {
            System.out.println("Stream " + stream.getIndex() + " type " + stream.getCodecType() + " duration " + stream.getDuration(TimeUnit.SECONDS));
        }

        FFmpeg ffmpeg;
        if (ffmpegBin != null) {
            ffmpeg = FFmpeg.atPath(Paths.get(ffmpegBin));
        } else {
            ffmpeg = FFmpeg.atPath();
        }

        final AtomicLong durationMillis = new AtomicLong();
        FFmpegResult fFmpegResult = ffmpeg
                .addInput(
                        UrlInput.fromUrl(video)
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

    public static void main(String[] args) {
        Iterator<String> argIter = Arrays.asList(args).iterator();

        String ffmpegBin = null;
        String video = null;

        while (argIter.hasNext()) {
            String argName = argIter.next();

            if (!argIter.hasNext()) {
                video = argName;
                break;
            }

            String argValue = argIter.next();

            if ("-ffmpeg_bin".equals(argName)) {
                ffmpegBin = argValue;
            }
        }

        new ShowStreams(ffmpegBin, video).execute();
    }
}
