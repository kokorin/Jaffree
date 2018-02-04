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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlInput extends UrlInOut<UrlInput> implements Input {
    private String url;
    private Integer streamLoop;
    private boolean readAtFrameRate = false;
    //-itsoffset offset (input)
    //-dump_attachment[:stream_specifier] filename (input,per-stream)

    /**
     * @param url input file url
     * @return this
     */
    public UrlInput setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Set number of times input stream shall be looped. Loop 0 means no loop, loop -1 means infinite loop.
     * @param streamLoop
     * @return this
     */
    public UrlInput setStreamLoop(Integer streamLoop) {
        this.streamLoop = streamLoop;
        return this;
    }

    public UrlInput setReadAtFrameRate(boolean readAtFrameRate) {
        this.readAtFrameRate = readAtFrameRate;
        return this;
    }

    @Override
    public List<String> buildArguments() {
        List<String> result = new ArrayList<>();

        if (streamLoop != null) {
            result.addAll(Arrays.asList("-stream_loop", streamLoop.toString()));
        }

        if (readAtFrameRate) {
            result.add("-re");
        }

        result.addAll(buildCommonArguments());

        // must be the last option
        if (url != null) {
            result.addAll(Arrays.asList("-i", url));
        }

        return result;
    }

    public static UrlInput fromUrl(String url) {
        return new UrlInput().setUrl(url);
    }

    public static UrlInput fromPath(Path path) {
        return new UrlInput().setUrl(path.toString());
    }
}

