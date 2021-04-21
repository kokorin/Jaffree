/*
 *    Copyright  2017 Denis Kokorin
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

    @Override
    public String toString() {
        return "Stream{" +
                "id=" + id +
                ", type=" + type +
                '}';
    }
}
