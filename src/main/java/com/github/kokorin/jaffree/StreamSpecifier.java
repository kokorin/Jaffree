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
 *
 * @see <a href="https://ffmpeg.org/ffprobe.html#toc-Stream-specifiers-1">stream specifiers</a>
 */
public class StreamSpecifier {
    private StreamSpecifier() {
    }

    public static String withIndex(int index) {
        return Integer.toString(index);
    }

    public static String withType(StreamType type) {
        return type.code();
    }

    public static String withInputIndexAndStreamIndex(int inputIndex, int streamIndex) {
        return inputIndex + ":" + streamIndex;
    }

    public static String withInputIndexAndType(int inputIndex, StreamType type) {
        return inputIndex + ":" + type.code();
    }

    public static String withTypeAndIndex(StreamType type, int index) {
        return type.code() + ":" + index;
    }

    public static String withProgramId(int programId) {
        return "p:" + programId;
    }

    public static String withProgramIdAndStreamIndex(int programId, int index) {
        return "p:" + programId + ":" + index;
    }

    public static String withMetadataKey(String key) {
        return "m:" + key;
    }

    public static String withMetadataKeyAndValue(String key, String value) {
        return "m:" + key + ":" + value;
    }

    public static String withUsableConfiguration() {
        return "u";
    }
}
