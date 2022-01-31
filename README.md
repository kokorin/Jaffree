# Jaffree [![Sparkline](https://stars.medv.io/kokorin/Jaffree.svg)](https://stars.medv.io/kokorin/Jaffree)

Jaffree stands for JAva FFmpeg and FFprobe FREE command line wrapper. Jaffree supports programmatic video production and consumption (with transparency)

It integrates with ffmpeg via `java.lang.Process`.

Inspired by [ffmpeg-cli-wrapper](https://github.com/bramp/ffmpeg-cli-wrapper)

## Tested with the help of [GitHub Actions](https://github.com/kokorin/Jaffree/blob/master/.github/workflows/tests.yml) 

![Tests](https://github.com/kokorin/Jaffree/workflows/Tests/badge.svg)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=kokorin_Jaffree&metric=coverage)](https://sonarcloud.io/dashboard?id=kokorin_Jaffree)

**OS**: Ubuntu, MacOS, Windows

**JDK**: 8, 11, 17

# Usage 

[![Maven Central](https://img.shields.io/maven-central/v/com.github.kokorin.jaffree/jaffree.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.kokorin.jaffree%22%20AND%20a:%22jaffree%22)

```xml
<dependency>
    <groupId>com.github.kokorin.jaffree</groupId>
    <artifactId>jaffree</artifactId>
    <version>${jaffree.version}</version>
</dependency>

<!--
    You should also include slf4j into dependencies.
    This is done intentionally to allow changing of slf4j version.
  -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.25</version>
</dependency>
```

# Examples

## Checking media streams with ffprobe

See whole example [here](src/test/java/examples/ShowStreamsExample.java).

```java
FFprobeResult result = FFprobe.atPath()
    .setShowStreams(true)
    .setInput(pathToVideo)
    .execute();

for (Stream stream : result.getStreams()) {
    System.out.println("Stream #" + stream.getIndex()
        + " type: " + stream.getCodecType()
        + " duration: " + stream.getDuration() + " seconds");
}
```

## Detecting exact media file duration

Sometimes ffprobe can't show exact duration, use ffmpeg trancoding to NULL output to get it.

See whole example [here](src/test/java/examples/ExactDurationExample.java).

```java
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
```

## Re-encode and track progress

See whole example [here](src/test/java/examples/ReEncodeExample.java).

```java
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
```

## Cut and scale media file

Pay attention that arguments related to Input must be set at Input, not at FFmpeg.

See whole example [here](src/test/java/examples/CutAndScaleExample.java).

```java
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
```

## Custom parsing of ffmpeg output

See whole example [here](src/test/java/examples/ParsingOutputExample.java).

```java
// StringBuffer - because it's thread safe
final StringBuffer loudnormReport = new StringBuffer();

FFmpeg.atPath()
    .addInput(UrlInput.fromUrl(pathToVideo))
    .addArguments("-af", "loudnorm=I=-16:TP=-1.5:LRA=11:print_format=json")
    .addOutput(new NullOutput(false))
    .setOutputListener(new OutputListener() {
        @Override
        public void onOutput(String line) {
            loudnormReport.append(line);
        }
    })
    .execute();

System.out.println("Loudnorm report:\n" + loudnormReport);
```

## Supplying and consuming data with SeekableByteChannel

Ability to interact with SeekableByteChannel is one of the features, which distinct Jaffree from 
similar libraries. Under the hood Jaffree uses tiny FTP server to interact with SeekableByteChannel.

See whole example [here](src/test/java/examples/UsingChannelsExample.java).
```java
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
```

## Supplying and consuming data with InputStream and OutputStream

**Notice** It's recommended to use `ChannelInput` & `ChannelOutput` since ffmpeg leverage seeking in input and 
requires seekable output for many formats.

Under the hood pipes are not OS pipes, but TCP Sockets. This allows much higher bandwidth.

See whole example [here](src/test/java/examples/UsingStreamsExample.java).

```java
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
```

## Screen Capture

See whole example [here](src/test/java/examples/ScreenCaptureExample.java).

```java
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
```

## Produce Video in Pure Java Code

See whole example [here](src/test/java/examples/ProduceVideoExample.java). 
Check also more [advanced example](src/test/java/examples/BouncingBallExample.java) which produce 
both audio and video 

```java
FrameProducer producer = new FrameProducer() {
    private long frameCounter = 0;

    @Override
    public List<Stream> produceStreams() {
        return Collections.singletonList(new Stream()
                .setType(Stream.Type.VIDEO)
                .setTimebase(1000L)
                .setWidth(320)
                .setHeight(240)
        );
    }

    @Override
    public Frame produce() {
        if (frameCounter > 30) {
            return null; // return null when End of Stream is reached
        }

        BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color(frameCounter * 1.0f / 30, 0, 0));
        graphics.fillRect(0, 0, 320, 240);
        long pts = frameCounter * 1000 / 10; // Frame PTS in Stream Timebase
        Frame videoFrame = Frame.createVideoFrame(0, pts, image);
        frameCounter++;

        return videoFrame;
    }
};

FFmpeg.atPath()
    .addInput(FrameInput.withProducer(producer))
    .addOutput(UrlOutput.toUrl(pathToVideo))
    .execute();
```

Here is an output of the above example:

![example output](src/test/resources/examples/programmatic.gif)

### Consume Video in Pure Java Code

See whole example [here](src/test/java/examples/ExtractFramesExample.java).

```java
FFmpeg.atPath()
        .addInput(UrlInput
                .fromPath(pathToSrc)
        )
        .addOutput(FrameOutput
                .withConsumer(
                        new FrameConsumer() {
                            private long num = 1;

                            @Override
                            public void consumeStreams(List<Stream> streams) {
                                // All stream type except video are disabled. just ignore
                            }

                            @Override
                            public void consume(Frame frame) {
                                // End of Stream
                                if (frame == null) {
                                    return;
                                }

                                try {
                                    String filename = "frame_" + num++ + ".png";
                                    Path output = pathToDstDir.resolve(filename);
                                    ImageIO.write(frame.getImage(), "png", output.toFile());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                )
                // No more then 100 frames
                .setFrameCount(StreamType.VIDEO, 100L)
                // 1 frame every 10 seconds
                .setFrameRate(0.1)
                // Disable all streams except video
                .disableStream(StreamType.AUDIO)
                .disableStream(StreamType.SUBTITLE)
                .disableStream(StreamType.DATA)
        )
        .execute();
```

## Managing errors

Jaffree will raise exceptions when a fatal error that causes a non-zero exit code occurs.

In some cases an error can occur but FFmpeg manages to catch it and exit correctly. This can be a 
convenient case, although sometimes one would prefer an exception to be raised to Jaffree.

To do so, the [`-xerror`](https://ffmpeg.org/ffmpeg.html#Advanced-options) argument can be used to
tell FFmpeg to exit the process with an error status when an error occurs.

```java
FFmpeg.atPath()
      .addArgument("-xerror")
      // ...
```

Please see [Issue 276](https://github.com/kokorin/Jaffree/issues/276) for more details on an actual
usecase.

## FFmpeg stop

See whole examples [here](src/test/java/examples/StopExample.java).

### Grace stop

Start ffmpeg with `FFmpeg#executeAsync` and stop it with `FFmpegResultFuture#graceStop` (ffmpeg only).
This will pass `q` symbol to ffmpeg's stdin.

**Note** output media finalization may take some time - up to several seconds.

```java
FFmpegResultFuture future = ffmpeg.executeAsync();

Thread.sleep(5_000);
future.graceStop();
```

### Force stop

There are 3 ways to stop ffmpeg forcefully.

**Note**: ffmpeg may not (depending on output format) correctly finalize output. 
It's very likely that produced media will be corrupted with force stop.

* Throw an exception in `ProgressListener` (ffmpeg only)
```java
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
```

* Start ffmpeg with `FFmpeg#executeAsync` and stop it with `FFmpegResultFuture#forceStop` (ffmpeg only)
```java
FFmpegResultFuture future = ffmpeg.executeAsync();

Thread.sleep(5_000);
future.forceStop();
```

* Start ffmpeg with `FFmpeg#execute` (or ffprobe with `FFprobe#execute`) and interrupt thread
```java
Thread thread = new Thread() {
    @Override
    public void run() {
        ffmpeg.execute();
    }
};
thread.start();

Thread.sleep(5_000);
thread.interrupt();
```

## Java 8 Completion API

See whole examples [here](src/test/java/examples/CompletionExample.java).

```java
ffmpeg.executeAsync().toCompletableFuture()
    .thenAccept(res -> {
        // get the result of the operation when it is done
    })
    .exceptionally(ex -> {
        // handle exceptions produced during operation
    });
```

## Complex Filtergraph (mosaic video)

More details about this example can be found on ffmpeg wiki:
[Create a mosaic out of several input videos](https://trac.ffmpeg.org/wiki/Create%20a%20mosaic%20out%20of%20several%20input%20videos)

```java
FFmpegResult result = FFmpeg.atPath(BIN)
        .addInput(UrlInput.fromPath(VIDEO1_MP4).setDuration(10, TimeUnit.SECONDS))
        .addInput(UrlInput.fromPath(VIDEO2_MP4).setDuration(10, TimeUnit.SECONDS))
        .addInput(UrlInput.fromPath(VIDEO3_MP4).setDuration(10, TimeUnit.SECONDS))
        .addInput(UrlInput.fromPath(VIDEO4_MP4).setDuration(10, TimeUnit.SECONDS))

        .setComplexFilter(FilterGraph.of(
                FilterChain.of(
                        Filter.withName("nullsrc")
                                .addArgument("size", "640x480")
                                .addOutputLink("base")
                ),
                FilterChain.of(
                        Filter.fromInputLink(StreamSpecifier.withInputIndexAndType(0, StreamType.ALL_VIDEO))
                                .setName("setpts")
                                .addArgument("PTS-STARTPTS"),
                        Filter.withName("scale")
                                .addArgument("320x240")
                                .addOutputLink("upperleft")
                ),
                FilterChain.of(
                        Filter.fromInputLink(StreamSpecifier.withInputIndexAndType(1, StreamType.ALL_VIDEO))
                                .setName("setpts")
                                .addArgument("PTS-STARTPTS"),
                        Filter.withName("scale")
                                .addArgument("320x240")
                                .addOutputLink("upperright")
                ),
                FilterChain.of(
                        Filter.fromInputLink(StreamSpecifier.withInputIndexAndType(2, StreamType.ALL_VIDEO))
                                .setName("setpts")
                                .addArgument("PTS-STARTPTS"),
                        Filter.withName("scale")
                                .addArgument("320x240")
                                .addOutputLink("lowerleft")
                ),
                FilterChain.of(
                        Filter.fromInputLink(StreamSpecifier.withInputIndexAndType(3, StreamType.ALL_VIDEO))
                                .setName("setpts")
                                .addArgument("PTS-STARTPTS"),
                        Filter.withName("scale")
                                .addArgument("320x240")
                                .addOutputLink("lowerright")
                ),
                FilterChain.of(
                        Filter.fromInputLink("base")
                                .addInputLink("upperleft")
                                .setName("overlay")
                                .addArgument("shortest", "1")
                                .addOutputLink("tmp1")
                ),
                FilterChain.of(
                        Filter.fromInputLink("tmp1")
                                .addInputLink("upperright")
                                .setName("overlay")
                                //.addArgument("shortest", "1")
                                .addArgument("x", "320")
                                .addOutputLink("tmp2")
                ),
                FilterChain.of(
                        Filter.fromInputLink("tmp2")
                                .addInputLink("lowerleft")
                                .setName("overlay")
                                //.addArgument("shortest", "1")
                                .addArgument("y", "240")
                                .addOutputLink("tmp3")
                ),
                FilterChain.of(
                        Filter.fromInputLink("tmp3")
                                .addInputLink("lowerright")
                                .setName("overlay")
                                //.addArgument("shortest", "1")
                                .addArgument("x", "320")
                                .addArgument("y", "240")
                )
        ))

        .addOutput(UrlOutput.toPath(outputPath))
        .execute();
```

## Programmatic mosaic video creation

Jaffree allows simultaneous reading from several sources (with one instance per every source and target).
You can find details in  Mosaic [example](src/test/java/examples/MosaicExample.java).

# Build & configuration

## IntelliJ IDEA

This project uses [maven-git-versioning-extension](https://github.com/qoomon/maven-git-versioning-extension)
to set project version automatically.
Check its [intellij-setup](https://github.com/qoomon/maven-git-versioning-extension#intellij-setup) documentation.

## JDK

JDK8 is required to compile this project. JDK9 is required to compile this project with Java 9 module support.

### JDK8

```shell
mvn clean install
```

### JDK9

Maven profile `J9-module` enables two pass compilation:

1. Compile all sources with Java 9 target version (including `module-info.java`).
2. Recompile all sources (except `module-info.java`) with Java 8 target version.
   
After this all classes will have Java 8 bytecode (version 52), while `module-info.class`
will have Java 9 bytecode (version 53).

```shell
mvn clean install -PJ9-module
```
