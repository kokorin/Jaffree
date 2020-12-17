/*
 *    Copyright  2020 Alex Katlein
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

import com.github.kokorin.jaffree.ffprobe.data.StreamingFormatParser;
import com.github.kokorin.jaffree.process.LinesProcessHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

class FFprobeProcessHandler extends LinesProcessHandler<FFprobeResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FFprobeProcessHandler.class);
    
    private final StreamingFormatParser parser;
    
    public FFprobeProcessHandler(@NotNull StreamingFormatParser parser) {
        Objects.requireNonNull(parser, "parser must not be null");
        
        this.parser = parser;
    }
    
    @Override
    public void onStderrLine(@NotNull String line) {
        LOGGER.error(line);
        setException(new RuntimeException(line));
    }
    
    @Override
    public void onStdoutLine(@NotNull String line) {
        try {
            parser.pushLine(line);
        } catch (Exception x) {
            setException(x);
        }
    }
    
    @Override
    public void onExit() {
        try {
            setResult(new FFprobeResult(parser.getResult()));
        } catch (Exception x) {
            setException(x);
        }
    }
}
