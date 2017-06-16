# Jaffree
Jaffree stands for [Ja]va [ff]mpeg and [ff]probe [free] command line wrapper.

It integrates with ffmpeg via `java.lang.Process`.

```xml
<dependency>
    <groupId>com.github.kokorin.jaffree</groupId>
    <artifactId>jaffree</artifactId>
    <version>0.1</version>
</dependency>
```

# Examples

## Iterate over media streams

```java
Path BIN = Paths.get("/path/to/ffmpeg_directory/");
Path VIDEO_MP4 = Paths.get("/path/to/video.mp4");

FFprobeResult result = FFprobe.atPath(BIN)
        .setInputPath(VIDEO_MP4)
        .setShowStreams(true)
        .setShowError(true)
        .execute();

if (result.getError() != null) {
    //TODO handle ffprobe error message
    return;
}

for (Stream stream : probe.getStreams().getStream()) {
    //TODO analyze stream data
}
```

## Re-encode and track progress

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
        .addInput(Input.fromPath(VIDEO_MP4))
        .addOutput(Output.toPath(outputPath)
                .addCodec(null, "copy")
        )
        .setProgressListener(listener)
        .execute();
```

## Mosaic video (complex filtergraph) 

More details about this example can be found on ffmpeg wiki: [Create a mosaic out of several input videos](https://trac.ffmpeg.org/wiki/Create%20a%20mosaic%20out%20of%20several%20input%20videos)
```java
FFmpegResult result = FFmpeg.atPath(BIN)
        .addInput(Input.fromPath(VIDEO1_MP4).setDuration(10, TimeUnit.SECONDS))
        .addInput(Input.fromPath(VIDEO2_MP4).setDuration(10, TimeUnit.SECONDS))
        .addInput(Input.fromPath(VIDEO3_MP4).setDuration(10, TimeUnit.SECONDS))
        .addInput(Input.fromPath(VIDEO4_MP4).setDuration(10, TimeUnit.SECONDS))

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

        .addOutput(Output.toPath(outputPath))
        .execute();
```