package com.github.kokorin.jaffree.ffmpeg;

public class Stream {
    private int id;
    private Type type;
    private Long timebase;
    private Integer width;
    private Integer height;
    private Long sampleRate;
    private Integer channels;

    public enum Type {
        VIDEO,
        AUDIO
    }

    public int getId() {
        return id;
    }

    public Stream setId(int id) {
        this.id = id;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Stream setType(Type type) {
        this.type = type;
        return this;
    }

    public Long getTimebase() {
        return timebase;
    }

    public Stream setTimebase(Long timebase) {
        this.timebase = timebase;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public Stream setWidth(int width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public Stream setHeight(int height) {
        this.height = height;
        return this;
    }

    public Stream setResolution(int width, int height) {
        return setWidth(width).setHeight(height);
    }

    public Long getSampleRate() {
        return sampleRate;
    }

    public Stream setSampleRate(long sampleRate) {
        this.sampleRate = sampleRate;
        return this;
    }

    public Integer getChannels() {
        return channels;
    }

    public Stream setChannels(int channels) {
        this.channels = channels;
        return this;
    }
}
