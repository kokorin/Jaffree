# Jaffree

Jaffree stands for [Ja]va [ff]mpeg and [ff]probe [free] command line wrapper.

It integrates with ffmpeg via `java.lang.Process`.

```xml
<dependency>
    <groupId>com.github.kokorin.jaffree</groupId>
    <artifactId>jaffree</artifactId>
    <version>0.7.4</version>
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

Inspired by [ffmpeg-cli-wrapper](https://github.com/bramp/ffmpeg-cli-wrapper) by [Andrew Brampton](https://blog.bramp.net/)

# Examples

## Checking media streams with ffprobe

See whole example [here](/src/test/java/examples/ffprobe/ShowStreams.java).

```java
Path BIN = Paths.get("/path/to/ffmpeg_directory/");
Path VIDEO_MP4 = Paths.get("/path/to/video.mp4");

FFprobeResult result = FFprobe.atPath(BIN)
        .setInputPath(VIDEO_MP4)
        .setShowStreams(true)
        .execute();

for (Stream stream : probe.getStreams()) {
    //TODO analyze stream data
}
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
                .addCodec(null, "copy")
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
