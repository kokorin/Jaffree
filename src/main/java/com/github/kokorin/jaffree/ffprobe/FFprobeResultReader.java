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
import com.github.kokorin.jaffree.ffprobe.data.DataParser;
import com.github.kokorin.jaffree.ffprobe.data.LineIterator;
import com.github.kokorin.jaffree.process.StdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class FFprobeResultReader implements StdReader<FFprobeResult> {
    private static Logger LOGGER = LoggerFactory.getLogger(FFprobeResultReader.class);

    @Override
    public FFprobeResult read(InputStream stdOut) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdOut));
        Iterator<String> lines = new LineIterator(reader);
        lines = new LoggingIterator(lines);
        Data data = DataParser.parse(lines);

        return new FFprobeResult(data);
    }

    private static class LoggingIterator implements Iterator<String> {
        private final Iterator<String> delegate;

        public LoggingIterator(Iterator<String> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public String next() {
            String next = delegate.next();
            LOGGER.debug(next);
            return next;
        }

        @Override
        public void remove() {
            delegate.remove();
        }
    }
}
