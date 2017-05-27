package com.github.kokorin.jaffree.cli;

/**
 * Stream specifiers are used to precisely specify which stream(s) a given option belongs to.
 * @see <a href="https://ffmpeg.org/ffprobe.html#toc-Stream-specifiers-1">stream specifiers</a>
 */
public class StreamSpecifier {
    private int index;
    private String type;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * stream_type is one of following: ’v’ or ’V’ for video, ’a’ for audio, ’s’ for subtitle,
     * ’d’ for data, and ’t’ for attachments.
     * <p>
     * ’v’ matches all video streams, ’V’ only matches video streams which are not attached pictures,
     * video thumbnails or cover arts. If stream_index is given, then it matches stream number stream_index of this type.
     * Otherwise, it matches all streams of this type.
     *
     * @return
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
