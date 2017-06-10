package com.github.kokorin.jaffree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterChain {
    private final List<Filter> filters = new ArrayList<>();

    public FilterChain addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    public FilterChain addFilters(List<Filter> filters) {
        this.filters.addAll(filters);
        return this;
    }

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



    public static FilterChain of(Filter ...filters) {
        return new FilterChain().addFilters(Arrays.asList(filters));
    }
}
