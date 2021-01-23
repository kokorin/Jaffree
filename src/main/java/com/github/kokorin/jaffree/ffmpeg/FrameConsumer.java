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

package com.github.kokorin.jaffree.ffmpeg;

import java.util.List;

/**
 * Allows custom implementation to be notified about available streams and every frame.
 */
public interface FrameConsumer {

    /**
     * Called once before any call to {@link #consume(Frame)}.
     *
     * @param streams streams
     */
    void consumeStreams(List<Stream> streams);

    /**
     * Called for every frame in video file.
     * <p>
     * When there is no more frame this method is called one more time
     * with {@code}null{@code} to notify consumer about EOF.
     *
     * @param frame frame
     */
    void consume(Frame frame);
}
