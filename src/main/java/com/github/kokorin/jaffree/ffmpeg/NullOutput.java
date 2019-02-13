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

import com.github.kokorin.jaffree.OS;

/**
 * In some cases usage of ffprobe is not enough. Use this output when you don't care about ffmpeg output and
 * you only want to analyze input file.
 * <p>
 * Pay attention that it may be required also to set {@link FFmpeg#setOverwriteOutput(boolean)} to true
 */
public class NullOutput extends BaseOutput<NullOutput> implements Output {
    public NullOutput() {
        this(true);
    }

    public NullOutput(boolean copyCodecs) {
        if (copyCodecs) {
            copyAllCodecs();
        }
        setFormat("null");
        String output = OS.IS_WINDOWS ? "NUL" : "/dev/null";
        setOutput(output);
    }
}
