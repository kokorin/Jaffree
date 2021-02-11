/*
 *    Copyright  2017-2021 Denis Kokorin
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

import java.nio.file.Path;

/**
 * {@link Input} implementation which allows {@link String} to specify input location.
 */
public class UrlInput extends BaseInput<UrlInput> implements Input {

    /**
     * Creates {@link UrlInput}.
     * @param input input location
     */
    public UrlInput(String input) {
        super(input);
    }

    /**
     * Creates {@link UrlInput}.
     * @param input input location: path on filesystem, URL, etc
     * @return UrlInput
     */
    public static UrlInput fromUrl(final String input) {
        return new UrlInput(input);
    }

    /**
     * Creates {@link UrlInput}.
     * @param path input location: path on filesystem
     * @return UrlInput
     */
    public static UrlInput fromPath(Path path) {
        return new UrlInput(path.toString());
    }
}

