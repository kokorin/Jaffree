package com.github.kokorin.jaffree.cli;

public enum SizeUnit {
    K(1_000L),
    M(1_000_000L),
    G(1_000_000_000L),

    Ki(1_024),
    Mi(1_024 * 1_024),
    Gi(1_024 * 1_024 * 1_024),

    KB(1_000L * 8),
    MB(1_000_000L * 8),
    GB(1_000_000_000L * 8),

    KiB(1_024 * 8),
    MiB(1_024 * 1_024 * 8),
    GiB(1_024 * 1_024 * 1_024 * 8),
    ;
    private long multiplier;

    SizeUnit(long multiplier) {
        this.multiplier = multiplier;
    }
}
