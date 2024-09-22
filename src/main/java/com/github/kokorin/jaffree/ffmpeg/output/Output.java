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

package com.github.kokorin.jaffree.ffmpeg.output;

import com.github.kokorin.jaffree.process.ProcessHelper;

import java.util.List;

/**
 * Interface for any ffmpeg output.
 * <p>
 * This interface should not be implemented by custom solutions.
 * It's better to use {@link BaseOutput}.
 *
 * @see BaseOutput
 */
public interface Output {

    /**
     * Build a list of command line arguments for this output.
     *
     * @return list of command line arguments
     */
    List<String> buildArguments();

    /**
     * Helper {@link ProcessHelper} which should be ran in dedicated thread.
     *
     * @return ProcessHelper, or null if no helper thread is needed
     */
    ProcessHelper helperThread();
}
