/*
 *    Copyright  2018 Denis Kokorin
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

import com.github.kokorin.jaffree.ffprobe.data.Data;
import com.github.kokorin.jaffree.ffprobe.data.FormatParser;
import com.github.kokorin.jaffree.process.StdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class FFprobeResultReader implements StdReader<FFprobeResult> {

    private final FormatParser parser;

    private static Logger LOGGER = LoggerFactory.getLogger(FFprobeResultReader.class);

    public FFprobeResultReader(FormatParser parser) {
        this.parser = parser;
    }

    @Override
    public FFprobeResult read(InputStream stdOut) {
        Data data = parser.parse(stdOut);

        return new FFprobeResult(data);
    }
}
