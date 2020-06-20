/*
 *    Copyright  2020 Denis Kokorin
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

import com.github.kokorin.jaffree.process.StdWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class StopStdWriter implements StdWriter {
    private volatile boolean stopped = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(StopStdWriter.class);

    @Override
    public synchronized void write(OutputStream stdIn) {
        try {
            while (!stopped) {
                wait(1_000);
            }
            stdIn.write('q');
            stdIn.flush();
        } catch (InterruptedException | IOException e) {
            LOGGER.info("Ignoring {}: {}", e.getClass(), e.getMessage());
        }
    }

    public synchronized void stop() {
        stopped = true;
        notifyAll();
    }
}
