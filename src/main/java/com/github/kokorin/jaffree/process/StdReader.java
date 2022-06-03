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

package com.github.kokorin.jaffree.process;

import com.github.kokorin.jaffree.log.LogMessage;

import java.io.InputStream;
import java.util.List;

/**
 * Implement {@link StdReader} interface to parse program stdout or stderr streams.
 *
 * @param <T> std read result
 */
public interface StdReader<T> {
    /**
     * Reads program stdout or stderr and returns parsed result.
     *
     * @param stdOut input stream
     * @return parsed result
     */
    T read(InputStream stdOut);

    /**
     * Get the list of error messages produced by the running process.
     *
     * @return error messages
     */
    List<LogMessage> getErrorLogMessages();
}
