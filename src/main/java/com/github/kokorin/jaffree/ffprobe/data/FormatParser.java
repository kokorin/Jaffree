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

package com.github.kokorin.jaffree.ffprobe.data;

import java.io.InputStream;

/**
 * Represents ffprobe output format parser.
 */
public interface FormatParser {
    /**
     * Returns format name which is passed to ffprobe via <b>-print_format</b> argument.
     *
     * @return format name
     */
    String getFormatName();

    /**
     * Parses input stream.
     *
     * @param inputStream input stream
     * @return parsed Data
     */
    Data parse(InputStream inputStream);
}
