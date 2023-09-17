package examples;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.input.PipeInput;
import com.github.kokorin.jaffree.ffmpeg.output.PipeOutput;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class UsingStreamsExample {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Exactly 2 arguments expected: path to source and destination media files");
            System.exit(1);
        }

        Path pathToSrc = Paths.get(args[0]);
        Path pathToDst = Paths.get(args[1]);

        try (InputStream inputStream =
                     Files.newInputStream(pathToSrc);
             OutputStream outputStream =
                     Files.newOutputStream(pathToDst, StandardOpenOption.CREATE,
                             StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
        ) {
            FFmpeg.atPath()
                    .addInput(PipeInput.pumpFrom(inputStream))
                    .addOutput(
                            PipeOutput.pumpTo(outputStream)
                                    .setFormat("flv")
                    )
                    .execute();
        }
    }
}
