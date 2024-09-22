/*
 *    Copyright 2017-2021 Denis Kokorin
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

package com.github.kokorin.jaffree.ffmpeg.output;

import java.nio.file.Path;

/**
 * {@link Output} implementation which allows {@link String} to specify output location.
 */
public class UrlOutput extends BaseOutput<UrlOutput> implements Output {

    /**
     * Creates {@link UrlOutput}.
     * @param output output location
     */
    protected UrlOutput(final String output) {
        super(output);
    }

    /**
     * Creates {@link UrlOutput}.
     * @param output output location: path on filesystem, URL, etc
     * @return UrlOutput
     */
    public static UrlOutput toUrl(final String output) {
        return new UrlOutput(output);
    }

    /**
     * Creates {@link UrlOutput}.
     * @param path output location: path on filesystem
     * @return UrlOutput
     */
    public static UrlOutput toPath(final Path path) {
        return new UrlOutput(path.toString());
    }
}
