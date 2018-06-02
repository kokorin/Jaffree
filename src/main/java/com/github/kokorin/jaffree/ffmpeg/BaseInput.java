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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseInput<T extends BaseInput & Input> extends BaseInOut<T> implements Input {
    private String input;
    private Integer streamLoop;
    private boolean readAtFrameRate = false;
    //-itsoffset offset (input)
    //-dump_attachment[:stream_specifier] filename (input,per-stream)

    /**
     * @param input input file input
     * @return this
     */
    public T setInput(String input) {
        this.input = input;
        return thisAsT();
    }

    /**
     * Set number of times input stream shall be looped. Loop 0 means no loop, loop -1 means infinite loop.
     * @param streamLoop
     * @return this
     */
    public BaseInput setStreamLoop(Integer streamLoop) {
        this.streamLoop = streamLoop;
        return this;
    }

    public BaseInput setReadAtFrameRate(boolean readAtFrameRate) {
        this.readAtFrameRate = readAtFrameRate;
        return this;
    }

    @Override
    public final List<String> buildArguments() {
        List<String> result = new ArrayList<>();

        if (streamLoop != null) {
            result.addAll(Arrays.asList("-stream_loop", streamLoop.toString()));
        }

        if (readAtFrameRate) {
            result.add("-re");
        }

        result.addAll(buildCommonArguments());

        if (input == null) {
            throw new IllegalArgumentException("Input must be specified");
        }

        // must be the last option
        result.addAll(Arrays.asList("-i", input));

        return result;
    }
}

