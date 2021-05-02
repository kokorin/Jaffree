/*
 *    Copyright 2017 Denis Kokorin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.ffmpeg;

/**
 * Represents audio/video stream.
 *
 * @see FrameInput
 * @see FrameOutput
 */
public class Stream {
    private int id;
    private Type type;
    //TODO NUT format allows rational timebases,  e.g.
    // 29.97" is an approximation of 30000/1001
    // "23.976" is an approximation of 24000/1001
    private Long timebase;
    private Integer width;
    private Integer height;
    private Long sampleRate;
    private Integer channels;

    /**
     * Stream type.
     */
    public enum Type {
        VIDEO,
        AUDIO
    }

    /**
     * @return stream id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id stream id
     * @return this
     */
    public Stream setId(final int id) {
        this.id = id;
        return this;
    }

    /**
     * @return stream type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type stream type
     * @return this
     */
    public Stream setType(final Type type) {
        this.type = type;
        return this;
    }

    /**
     * @return stream timebase
     */
    public Long getTimebase() {
        return timebase;
    }

    /**
     * @param timebase stream timebase
     * @return this
     */
    public Stream setTimebase(final Long timebase) {
        this.timebase = timebase;
        return this;
    }

    /**
     * Return stream width for video stream  or null for other stream types.
     *
     * @return stream width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width stream width
     * @return this
     */
    public Stream setWidth(final int width) {
        this.width = width;
        return this;
    }

    /**
     * Return stream height for video stream  or null for other stream types.
     *
     * @return stream height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height stream width
     * @return this
     */
    public Stream setHeight(final int height) {
        this.height = height;
        return this;
    }

    /**
     * Sets both width and height.
     *
     * @param width  stream width
     * @param height stream height
     * @return this
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public Stream setResolution(final int width, final int height) {
        return setWidth(width).setHeight(height);
    }

    /**
     * Returns stream sample rate for audio stream or null for other stream types.
     *
     * @return sample rate
     */
    public Long getSampleRate() {
        return sampleRate;
    }

    /**
     * @param sampleRate sample rate
     * @return this
     */
    public Stream setSampleRate(final long sampleRate) {
        this.sampleRate = sampleRate;
        return this;
    }

    /**
     * Returns stream number of channels for audio stream or null for other stream types.
     *
     * @return number of channels
     */
    public Integer getChannels() {
        return channels;
    }

    /**
     * @param channels number of audio channels.
     * @return this
     */
    public Stream setChannels(final int channels) {
        this.channels = channels;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Stream{"
                + "id=" + id
                + ", type=" + type
                + '}';
    }
}
