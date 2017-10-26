package com.github.kokorin.jaffree.nut;

public class Info {
    public final int streamId;
    public final int chapterId;
    public final long chapterStart;
    public final long chapterLength;
    public final DataItem[] metaData;

    public Info(int streamId, int chapterId, long chapterStart, long chapterLength, DataItem[] metaData) {
        this.streamId = streamId;
        this.chapterId = chapterId;
        this.chapterStart = chapterStart;
        this.chapterLength = chapterLength;
        this.metaData = metaData;
    }
}
