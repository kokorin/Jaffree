package com.github.kokorin.jaffree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterGraph {
    private final List<FilterChain> chains = new ArrayList<>();

    public FilterGraph addFilterChain(FilterChain chain) {
        chains.add(chain);
        return this;
    }

    public FilterGraph addFilterChains(List<FilterChain> chain) {
        chains.addAll(chain);
        return this;
    }

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

    public static FilterGraph of(FilterChain ...chains) {
        return new FilterGraph().addFilterChains(Arrays.asList(chains));
    }
}
