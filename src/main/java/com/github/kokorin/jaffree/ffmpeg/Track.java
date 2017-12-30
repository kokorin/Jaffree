package com.github.kokorin.jaffree.ffmpeg;

public class Track {
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

    public Track setId(int id) {
        this.id = id;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Track setType(Type type) {
        this.type = type;
        return this;
    }

    public Long getTimebase() {
        return timebase;
    }

    public Track setTimebase(Long timebase) {
        this.timebase = timebase;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public Track setWidth(int width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public Track setHeight(int height) {
        this.height = height;
        return this;
    }

    public Long getSampleRate() {
        return sampleRate;
    }

    public Track setSampleRate(long sampleRate) {
        this.sampleRate = sampleRate;
        return this;
    }

    public Integer getChannels() {
        return channels;
    }

    public Track setChannels(int channels) {
        this.channels = channels;
        return this;
    }
}
