package com.github.kokorin.jaffree.nut;

import java.util.Collections;
import java.util.List;

public class Info {
    public final long streamId;
    public final long chapterId;
    public final long chapterStart;
    public final long chapterLength;
    public final List<DataItem> metaData;

    public Info(long streamId, long chapterId, long chapterStart, long chapterLength, List<DataItem> metaData) {
        this.streamId = streamId;
        this.chapterId = chapterId;
        this.chapterStart = chapterStart;
        this.chapterLength = chapterLength;
        this.metaData = Collections.unmodifiableList(metaData);
    }
}
