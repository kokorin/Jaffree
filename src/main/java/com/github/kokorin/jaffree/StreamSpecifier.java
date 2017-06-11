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

package com.github.kokorin.jaffree;

/**
 * Stream specifiers are used to precisely specify which stream(s) a given option belongs to.
 * @see <a href="https://ffmpeg.org/ffprobe.html#toc-Stream-specifiers-1">stream specifiers</a>
 */
public class StreamSpecifier {
    private final String value;

    public StreamSpecifier(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StreamSpecifier withIndex(int index) {
        return new StreamSpecifier(Integer.toString(index));
    }

    public static StreamSpecifier withType(StreamType type) {
        return new StreamSpecifier(type.code());
    }

    public static StreamSpecifier withInputIndexAndStreamIndex(int inputIndex, int streamIndex) {
        return new StreamSpecifier(inputIndex + ":" + streamIndex);
    }

    public static StreamSpecifier withInputIndexAndType(int inputIndex, StreamType type) {
        return new StreamSpecifier(inputIndex + ":" + type.code());
    }

    public static StreamSpecifier withTypeAndIndex(StreamType type, int index) {
        return new StreamSpecifier(type.code() + ":" + index);
    }

    public static StreamSpecifier withProgramId(int programId) {
        return new StreamSpecifier("p:" + programId);
    }

    public static StreamSpecifier withProgramIdAndStreamIndex(int programId, int index) {
        return new StreamSpecifier("p:" + programId + ":" + index);
    }

    public static StreamSpecifier withMetadataKey(String key) {
        return new StreamSpecifier("m:" + key);
    }

    public static StreamSpecifier withMetadataKeyAndValue(String key, String value) {
        return new StreamSpecifier("m:" + key + ":" + value);
    }

    public static StreamSpecifier withUsableConfiguration() {
        return new StreamSpecifier("u");
    }
}
