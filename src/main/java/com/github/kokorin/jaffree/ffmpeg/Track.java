package com.github.kokorin.jaffree.ffmpeg;

public class Track {
    private int id;
    private String title;
    private Type type;
    private int width;
    private int height;
    private float samplingFreaquency;

    public int getId() {
        return id;
    }

    public Track setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Track setTitle(String title) {
        this.title = title;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Track setType(Type type) {
        this.type = type;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Track setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Track setHeight(int height) {
        this.height = height;
        return this;
    }

    public float getSamplingFreaquency() {
        return samplingFreaquency;
    }

    public Track setSamplingFreaquency(float samplingFreaquency) {
        this.samplingFreaquency = samplingFreaquency;
        return this;
    }

    public enum Type {
        VIDEO,
        AUDIO
    }
}
