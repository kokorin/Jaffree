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
 * Represents ffmpeg filter chain.
 * <p>
 * Mainly this class exists to make ffmpeg filter graphs more readable for developers.
 *
 * @see <a href="https://ffmpeg.org/ffmpeg-filters.html">ffmpeg filters documentation</a>
 */
public class FilterChain {
    private final List<Filter> filters = new ArrayList<>();

    /**
     * Adds filter to filter chain.
     *
     * @param filter filter
     * @return this
     */
    public FilterChain addFilter(final Filter filter) {
        filters.add(filter);
        return this;
    }

    /**
     * Adds multiple filters to filter chain.
     *
     * @param filtersToAdd filters to add
     * @return this
     */
    public FilterChain addFilters(final List<? extends Filter> filtersToAdd) {
        filters.addAll(filtersToAdd);
        return this;
    }

    /**
     * Prints filter chain description according to ffmpeg filtergraph syntax.
     *
     * @return filter description
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#toc-Filtergraph-syntax-1">
     * filtergraph syntax</a>
     */
    public String getValue() {
        StringBuilder result = new StringBuilder();

        boolean first = true;
        for (Filter filter : filters) {
            if (!first) {
                result.append(",");
            }
            result.append(filter.getValue());
            first = false;
        }

        return result.toString();
    }

    /**
     * Create {@link FilterChain} from several filters.
     *
     * @param filters filters
     * @return FilterChain
     */
    public static FilterChain of(final Filter... filters) {
        return new FilterChain().addFilters(Arrays.asList(filters));
    }
}
