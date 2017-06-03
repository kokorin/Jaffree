package com.github.kokorin.jaffree.cli;

import java.util.ArrayList;
import java.util.List;

public class Output extends Common <Output> {
    private String url;

    public Output setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public List<Option> buildOptions() {
        List<Option> result = new ArrayList<>();

        result.addAll(buildCommonOptions());

        // must be the last option
        result.add(new Option(url));

        return result;
    }
}
