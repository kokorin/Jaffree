/*
 *    Copyright  2020-2021 Vicne, Denis Kokorin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package examples;

import com.github.kokorin.jaffree.ffmpeg.input.CaptureInput;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.input.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.output.UrlOutput;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

public class ScreenCaptureExample {

    public void execute() throws Exception {

    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Exactly 1 argument expected: path to output media file");
            System.exit(1);
        }

        Path pathToVideo = Paths.get(args[0]);

        FFmpeg.atPath()
                .addInput(CaptureInput
                        .captureDesktop()
                        .setCaptureFrameRate(30)
                        .setCaptureCursor(true)
                )
                .addOutput(UrlOutput
                        .toPath(pathToVideo)
                        // Record with ultrafast to lower CPU usage
                        .addArguments("-preset", "ultrafast")
                        .setDuration(30, TimeUnit.SECONDS)
                )
                .setOverwriteOutput(true)
                .execute();

        //Re-encode when record is completed to optimize file size
        Path pathToOptimized = pathToVideo.resolveSibling("optimized-" + pathToVideo.getFileName());
        FFmpeg.atPath()
                .addInput(UrlInput.fromPath(pathToVideo))
                .addOutput(UrlOutput.toPath(pathToOptimized))
                .execute();

        Files.move(pathToOptimized, pathToVideo, StandardCopyOption.REPLACE_EXISTING);
    }
}
