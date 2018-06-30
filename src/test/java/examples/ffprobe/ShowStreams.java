package examples.ffprobe;

import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

public class ShowStreams {
    private final Path ffmpegBin;
    private final Path video;

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowStreams.class);

    public ShowStreams(Path ffmpegBin, Path video) {
        this.ffmpegBin = ffmpegBin;
        this.video = video;
    }

    public void execute() {
        FFprobeResult result = FFprobe.atPath(ffmpegBin)
                .setShowStreams(true)
                .setInputPath(video)
                .execute();

        for (Stream stream : result.getStreams()) {
            System.out.println("Stream " + stream.getIndex() + " type " + stream.getCodecType());
        }
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

        if (ffmpegBin == null || video == null) {
            LOGGER.error("Arguments: -ffmpeg_bin </path/to/ffmpeg/bin> <file>");
            return;
        }

        new ShowStreams(Paths.get(ffmpegBin), Paths.get(video)).execute();
    }
}
