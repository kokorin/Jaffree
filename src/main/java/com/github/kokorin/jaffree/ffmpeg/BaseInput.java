/*
 *    Copyright 2017-2021 Denis Kokorin
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

import com.github.kokorin.jaffree.process.ProcessHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class which handles all arguments for ffmpeg input.
 *
 * @param <T> self
 */
public abstract class BaseInput<T extends BaseInput<T>> extends BaseInOut<T> implements Input {
    // TODO: make input property final
    private final String input;
    private Integer streamLoop;
    private boolean readAtFrameRate = false;
    //-itsoffset offset (input)
    //-dump_attachment[:stream_specifier] filename (input,per-stream)

    /**
     * @param input path to file or URI
     */
    public BaseInput(String input) {
        this.input = input;
    }

    /**
     * Set number of times input stream shall be looped. Loop 0 means no loop,
     * loop -1 means infinite loop.
     *
     * @param streamLoop
     * @return this
     */
    @SuppressWarnings("checkstyle:hiddenfield")
    public T setStreamLoop(final Integer streamLoop) {
        this.streamLoop = streamLoop;
        return thisAsT();
    }

    /**
     * Read input at native frame rate. Mainly used to simulate a grab device, or live input stream
     * (e.g. when reading from a file).
     * <p>
     * Should not be used with actual grab devices or live input streams (where it can cause packet
     * loss).
     * <p>
     * By default ffmpeg attempts to read the input(s) as fast as possible. This option will
     * slow down the reading of the input(s) to the native frame rate of the input(s).
     * <p>
     * It is useful for real-time output (e.g. live streaming).
     *
     * @param readAtFrameRate whether or not to read at native frame rate
     * @return this
     */
    @SuppressWarnings("checkstyle:hiddenfield")
    public T setReadAtFrameRate(final boolean readAtFrameRate) {
        this.readAtFrameRate = readAtFrameRate;
        return thisAsT();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final List<String> buildArguments() {
        List<String> result = new ArrayList<>(super.buildArguments());

        if (streamLoop != null) {
            result.addAll(Arrays.asList("-stream_loop", streamLoop.toString()));
        }

        if (readAtFrameRate) {
            result.add("-re");
        }

        result.addAll(getAdditionalArguments());

        if (input == null) {
            throw new IllegalArgumentException("Input must be specified");
        }

        // must be the last option
        result.addAll(Arrays.asList("-i", input));

        return result;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    //TODO: remove and keep helperThread abstract?
    @Override
    public ProcessHelper helperThread() {
        return null;
    }
}

