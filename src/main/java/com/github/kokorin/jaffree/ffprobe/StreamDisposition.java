
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.Section;

public class StreamDisposition {
    private final Section section;

    public StreamDisposition(Section section) {
        this.section = section;
    }

    public int getDefault() {
        return section.getInteger("default");
    }

    public int getDub() {
        return section.getInteger("dub");
    }

    public int getOriginal() {
        return section.getInteger("original");
    }

    public int getComment() {
        return section.getInteger("comment");
    }

    public int getLyrics() {
        return section.getInteger("lyrics");
    }

    public int getKaraoke() {
        return section.getInteger("karaoke");
    }

    public int getForced() {
        return section.getInteger("forced");
    }

    public int getHearingImpaired() {
        return section.getInteger("hearing_impaired");
    }

    public int getVisualImpaired() {
        return section.getInteger("visual_impaired");
    }

    public int getCleanEffects() {
        return section.getInteger("clean_effects");
    }

    public int getAttachedPic() {
        return section.getInteger("attached_pic");
    }

    public int getTimedThumbnails() {
        return section.getInteger("timed_thumbnails");
    }


}

