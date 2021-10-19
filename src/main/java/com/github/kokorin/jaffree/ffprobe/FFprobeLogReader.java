/*
 *    Copyright 2021 Denis Kokorin
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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.log.LogMessage;
import com.github.kokorin.jaffree.process.BaseStdReader;

/**
 * {@link FFprobeLogReader} reads ffprobe output (from stderr), parses logs and sends logs
 * to SLF4J with corresponding log level.
 */
public class FFprobeLogReader extends BaseStdReader<FFprobeResult> {
    /**
     * Does nothing as ffprobe prints logs and result in different output streams.
     *
     * @param logMessage log message
     * @return null
     */
    @Override
    protected FFprobeResult handleLogMessage(final LogMessage logMessage) {
        return null;
    }
}
