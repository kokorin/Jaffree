# Jaffree

Jaffree stands for [Ja]va [ff]mpeg and [ff]probe [free] command line wrapper. Jaffree supports programmatic video production and consumption (with transparency)

It integrates with ffmpeg via `java.lang.Process`.

Inspired by [ffmpeg-cli-wrapper](https://github.com/bramp/ffmpeg-cli-wrapper)

## Tested with the help of [GitHub Actions](/kokorin/Jaffree/blob/master/.github/workflows/maven.yml) 

![Tests](https://github.com/kokorin/Jaffree/workflows/Tests/badge.svg)

**OS**: Ubuntu, MacOS, Windows

**JDK**: 7, 8, 11, 14

# Usage 

```xml
<dependency>
    <groupId>com.github.kokorin.jaffree</groupId>
    <artifactId>jaffree</artifactId>
    <version>0.9.5</version>
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

See whole example [here](/src/test/java/examples/ffprobe/ShowStreams.java).

```java
//path to ffmpeg directory or null (to use PATH env variable)
Path BIN = Paths.get("/path/to/ffmpeg_directory/");
Path VIDEO_MP4 = Paths.get("/path/to/video.mp4");


FFprobe ffprobe;
if (BIN != null) {
    ffprobe = FFprobe.atPath(BIN);
} else {
    ffprobe = FFprobe.atPath();
}

FFprobeResult result = ffprobe
        .setShowStreams(true)
        .setInput(VIDEO_MP4)
        .execute();

for (Stream stream : result.getStreams()) {
    System.out.println("Stream " + stream.getIndex() 
            + " type " + stream.getCodecType()
            + " duration " + stream.getDuration(TimeUnit.SECONDS));
}

FFmpeg ffmpeg;
if (BIN != null) {
    ffmpeg = FFmpeg.atPath(BIN);
} else {
    ffmpeg = FFmpeg.atPath();
}

//Sometimes ffprobe can't show exact duration, use ffmpeg trancoding to NULL output to get it
final AtomicLong durationMillis = new AtomicLong();
FFmpegResult fFmpegResult = ffmpeg
        .addInput(
                UrlInput.fromUrl(VIDEO_MP4)
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

See whole example [here](/src/test/java/examples/ffmpeg/ReEncode.java).

```java
Path BIN = Paths.get("/path/to/ffmpeg_directory/");
Path VIDEO_MP4 = Paths.get("/path/to/video.mp4");
Path OUTPUT_MP4 = Paths.get("/path/to/output.mp4");

ProgressListener listener = new ProgressListener() {
    @Override
    public void onProgress(FFmpegProgress progress) {
        //TODO handle progress data
    }
};


FFmpegResult result = FFmpeg.atPath(BIN)
        .addInput(UrlInput.fromPath(VIDEO_MP4))
        .addOutput(UrlOutput.toPath(outputPath)
                .copyAllCodecs()
        )
        // This is optional
        .setProgressListener(listener)
        .execute();
```

## Custom parsing of ffmpeg output

```java
FFmpegResult result = FFmpeg.atPath(BIN)
        .addInput(UrlInput.fromPath(VIDEO_MP4))
        .addArguments("-af", "loudnorm=I=-16:TP=-1.5:LRA=11:print_format=json")
        .addOutput(new NullOutput(false))
        .setOutputListener(new OutputListener() {
            private boolean loudnormReportStarted;
            @Override
            public boolean onOutput(String line) {
                if (line.contains("loudnornm")) {
                    loudnormReportStarted = true;
                    return true;
                }
                if (loudnormReportStarted) {
                    // TODO parse loudnorm JSON report
                }
                return loudnormReportStarted;
            }
        })
        .execute();

```

## Supplying and consuming data with SeekableByteChannel

Under the hood Jaffree uses tiny FTP server to interact with SeekableByteChannel

```java
FFprobeResult probe;
FFmpegResult result;

try (SeekableByteChannel channel = Files.newByteChannel(VIDEO_MP4, READ)) {
    probe = FFprobe.atPath(BIN)
            .setShowStreams(true)
            .setInput(channel)
            .execute();
}

try (SeekableByteChannel channel = Files.newByteChannel(VIDEO_MP4, READ)) {
    FFmpegResult result = FFmpeg.atPath(BIN)
            .addInput(
                    ChannelInput.fromChannel(VIDEO_MP4.getFileName().toString(), channel)
            )
            .addOutput(
                    UrlOutput.toPath(outputPath)
            )
            .execute();
}

try (SeekableByteChannel channel = Files.newByteChannel(outputPath, CREATE, WRITE, READ, TRUNCATE_EXISTING)) {
    FFmpegResult result = FFmpeg.atPath(BIN)
            .addInput(
                    UrlInput.fromPath(VIDEO_MP4)
            )
            .addOutput(
                    ChannelOutput.toChannel("channel.mp4", channel)
            )
            .execute();
}

```

## Supplying and consuming data with InputStream and OutputStream

**Notice** It's recommended to use `ChannelInput` & `ChannelOutput` since ffmpeg leverage seeking in input and 
requires seekable output for many formats.

Under the hood pipes are not OS pipes, but TCP Sockets. This allows much higher bandwidth.

```java
FFprobeResult probe;
FFmpegResult result;

try (InputStream inputStream = Files.newInputStream(VIDEO_MP4)) {
    probe = FFprobe.atPath(BIN)
            .setShowStreams(true)
            .setInput(inputStream)
            .execute();
}


try (InputStream inputStream = Files.newInputStream(VIDEO_MP4)) {
    result = FFmpeg.atPath(BIN)
            .addInput(PipeInput.pumpFrom(inputStream))
            .addOutput(UrlOutput.toPath(outputPath))
            .execute();
}

try (OutputStream outputStream = Files.newOutputStream(outputPath, StandardOpenOption.CREATE)) {
    result = FFmpeg.atPath(BIN)
            .addInput(UrlInput.fromPath(VIDEO_MP4))
            .addOutput(PipeOutput.pumpTo(outputStream).setFormat("flv"))
            .setOverwriteOutput(true)
            .execute();
}

```

## FFmpeg stop

See whole examples [here](/src/test/java/examples/ffmpeg/Stop.java).

### Grace stop

Start ffmpeg with FFmpeg#executeAsync and stop it with FFmpegResultFuture#graceStop (ffmpeg only).
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

* Throw an exception in ProgressListener (ffmpeg only)
```java
final AtomicBoolean stopped = new AtomicBoolean();
ffmpeg.setProgressListener(
        new ProgressListener() {
            @Override
            public void onProgress(FFmpegProgress progress) {
                if (stopped.get()) {
                    throw new RuntimeException("Stooped with exception!");
                }
            }
        }
);
```

* Start ffmpeg with FFmpeg#executeAsync and stop it with FFmpegResultFuture#forceStop (ffmpeg only)
```java
FFmpegResultFuture future = ffmpeg.executeAsync();

Thread.sleep(5_000);
future.forceStop();
```

* Start ffmpeg with FFmpeg#execute (or ffprobe with FFprobe#execute) and interrupt thread
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

## Complex filtergraph (mosaic video)

More details about this example can be found on ffmpeg wiki: [Create a mosaic out of several input videos](https://trac.ffmpeg.org/wiki/Create%20a%20mosaic%20out%20of%20several%20input%20videos)
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

## Programmatic video


### Producing video

Jaffree allows creation of video in pure java code.

See whole example [here](/src/test/java/examples/programmatic/ProduceGif.java).

```java
Path output = Paths.get("test.gif");

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
            return null;
        }
        System.out.println("Creating frame " + frameCounter);

        BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color(frameCounter * 1.0f / 30, 0, 0));
        graphics.fillRect(0, 0, 320, 240);

        Frame videoFrame = new Frame()
                .setStreamId(0)
                .setPts(frameCounter * 1000 / 10)
                .setImage(image);
        frameCounter++;

        return videoFrame;
    }
};

FFmpegResult result = FFmpeg.atPath(BIN)
        .addInput(
                FrameInput.withProducer(producer)
        )
        .addOutput(
                UrlOutput.toPath(output)
        )
        .execute();
```

Here is an output of the above example:

![example output](programmatic.gif)

Jaffree also allows producing of audio tracks, see BouncingBall [example](examples/src/main/java/BouncingBall.java) for more details.


### Consuming video

Jaffree allows consumption of video in the similar manner.

See whole example [here](/src/test/java/examples/programmatic/ExtractFrames.java).

```java
final Path tempDir = Files.createTempDirectory("jaffree");
System.out.println("Will write to " + tempDir);

final AtomicLong trackCounter = new AtomicLong();
final AtomicLong frameCounter = new AtomicLong();

FrameConsumer consumer = new FrameConsumer() {
    @Override
    public void consumeStreams(List<Stream> tracks) {
        trackCounter.set(tracks.size());
    }

    @Override
    public void consume(Frame frame) {
        if (frame == null) {
            return;
        }

        long n = frameCounter.incrementAndGet();
        String filename = String.format("frame%05d.png", n);
        try {
            ImageIO.write(frame.getImage(), "png", tempDir.resolve(filename).toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
};

FFmpegResult result = FFmpeg.atPath(BIN)
        .addInput(
                UrlInput.fromPath(VIDEO_MP4)
                        .setDuration(1, TimeUnit.SECONDS)
        )
        .addOutput(
                FrameOutput.withConsumer(consumer)
                        .extractVideo(true)
                        .extractAudio(false)
        )
        .execute();
```

### Programmatic mosaic video creation

Jaffree allows simultaneous reading from several sources (with one instance per every source and target).
You can find details in  Mosaic [example](/src/test/java/examples/programmatic/Mosaic.java).
