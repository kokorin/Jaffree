package examples.ffmpeg;

import com.github.kokorin.jaffree.ffmpeg.ChannelInput;
import com.github.kokorin.jaffree.ffmpeg.ChannelOutput;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;

import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class UsingChannels {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Exactly 2 arguments expected: path to source and destination media files");
            System.exit(1);
        }

        Path pathToSrc = Paths.get(args[0]);
        Path pathToDst = Paths.get(args[1]);
        String filename = args[1];

        try (SeekableByteChannel inputChannel =
                     Files.newByteChannel(pathToSrc, StandardOpenOption.READ);
             SeekableByteChannel outputChannel =
                     Files.newByteChannel(pathToDst, StandardOpenOption.CREATE,
                             StandardOpenOption.WRITE, StandardOpenOption.READ,
                             StandardOpenOption.TRUNCATE_EXISTING)
        ) {
            FFmpeg.atPath()
                    .addInput(ChannelInput.fromChannel(inputChannel))
                    .addOutput(ChannelOutput.toChannel(filename, outputChannel))
                    .execute();
        }
    }
}
