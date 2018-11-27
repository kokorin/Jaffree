
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DTag;

public class StreamDisposition {
    private final DTag tag;

    public StreamDisposition(DTag tag) {
        this.tag = tag;
    }

    public int getDefault() {
        return tag.getInteger("default");
    }

    public int getDub() {
        return tag.getInteger("dub");
    }

    public int getOriginal() {
        return tag.getInteger("original");
    }

    public int getComment() {
        return tag.getInteger("comment");
    }

    public int getLyrics() {
        return tag.getInteger("lyrics");
    }

    public int getKaraoke() {
        return tag.getInteger("karaoke");
    }

    public int getForced() {
        return tag.getInteger("forced");
    }

    public int getHearingImpaired() {
        return tag.getInteger("hearing_impaired");
    }

    public int getVisualImpaired() {
        return tag.getInteger("visual_impaired");
    }

    public int getCleanEffects() {
        return tag.getInteger("clean_effects");
    }

    public int getAttachedPic() {
        return tag.getInteger("attached_pic");
    }

    public int getTimedThumbnails() {
        return tag.getInteger("timed_thumbnails");
    }


}

