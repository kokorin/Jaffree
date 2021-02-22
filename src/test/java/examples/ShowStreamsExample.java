package examples;

import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;

import java.util.concurrent.TimeUnit;

public class ShowStreamsExample {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Exactly 1 argument expected: path to media file");
            System.exit(1);
        }

        String pathToVideo = args[0];

        FFprobeResult result = FFprobe.atPath()
                .setShowStreams(true)
                .setInput(pathToVideo)
                .execute();

        for (Stream stream : result.getStreams()) {
            System.out.println("Stream #" + stream.getIndex()
                    + " type: " + stream.getCodecType()
                    + " duration: " + stream.getDuration(TimeUnit.SECONDS) + " seconds");
        }

    }
}
