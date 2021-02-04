/*
 *    Copyright  2019 Denis Kokorin
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
 * Extend this interface to analyze ffmpeg output
 */
public interface OutputListener {
    /**
     * Invoked on every line, which wasn't parsed by FFmpegResultReader.
     * <p>
     * Attention: this method is not thread safe and may be invoked in different thread.
     * Consider using synchronization.
     *
     * @param line of ffmpeg output, which is neither progress, nor result
     * @return whether input was successfully parsed and should not be treated as error message
     */
    // TODO return void
    boolean onOutput(String line);
}
