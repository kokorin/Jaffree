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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents ffmpeg filter graph.
 * <p>
 * Mainly this class exists to make ffmpeg filter graphs more readable for developers.
 *
 * @see <a href="https://ffmpeg.org/ffmpeg-filters.html">ffmpeg filters documentation</a>
 */
public class FilterGraph {
    private final List<FilterChain> chains = new ArrayList<>();

    /**
     * Adds filter chain to filter graph.
     *
     * @param chain filter chain
     * @return this
     */
    public FilterGraph addFilterChain(final FilterChain chain) {
        chains.add(chain);
        return this;
    }

    /**
     * Adds multiple filter chains to filter graph.
     *
     * @param chainsToAdd filter chains to add
     * @return this
     */
    public FilterGraph addFilterChains(final List<? extends FilterChain> chainsToAdd) {
        chains.addAll(chainsToAdd);
        return this;
    }

    /**
     * Prints filter graph description according to ffmpeg filtergraph syntax.
     *
     * @return filter description
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#toc-Filtergraph-syntax-1">
     * filtergraph syntax</a>
     */
    public String getValue() {
        StringBuilder result = new StringBuilder();

        boolean first = true;
        for (FilterChain chain : chains) {
            if (!first) {
                result.append(";");
            }
            result.append(chain.getValue());
            first = false;
        }

        return result.toString();
    }

    /**
     * Create {@link FilterGraph} from several filter chains.
     *
     * @param filterChains filter chains
     * @return FilterGraph
     */
    public static FilterGraph of(final FilterChain... filterChains) {
        return new FilterGraph().addFilterChains(Arrays.asList(filterChains));
    }
}
