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

package com.github.kokorin.jaffree.ffmpeg;

import com.github.kokorin.jaffree.StreamType;

/**
 * Implement {@link Filter} interface to provide custom ffmpeg filter.
 * @see <a href="https://ffmpeg.org/ffmpeg-filters.html">ffmpeg-filters</a>
 */
public interface Filter {
    /**
     * Creates {@link Filter} starting from specified stream type.
     *
     * @param streamType stream type
     * @return Filter
     */
    static GenericFilter fromInputLink(StreamType streamType) {
        return new GenericFilter().addInputLink(streamType);
    }

    /**
     * Creates {@link Filter} starting from link name or stream specifier.
     *
     * @param linkOrStreamSpecifier link name or stream specifier
     * @return this
     * @see <a href="https://ffmpeg.org/ffmpeg.html#Stream-specifiers">
     * stream specifiers</a>
     */
    static GenericFilter fromInputLink(String linkOrStreamSpecifier) {
        return new GenericFilter().addInputLink(linkOrStreamSpecifier);
    }

    /**
     * Creates {@link Filter} starting from filter name.
     *
     * @param name filter
     * @return Filter
     */
    static GenericFilter withName(String name) {
        return new GenericFilter().setName(name);
    }

    /**
     * Prints filter description according to ffmpeg filtergraph syntax.
     *
     * @return filter description
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#toc-Filtergraph-syntax-1">
     * filtergraph syntax</a>
     */
    String getValue();
}
