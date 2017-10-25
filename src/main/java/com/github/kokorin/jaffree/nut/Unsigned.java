package com.github.kokorin.jaffree.nut;

import static java.lang.Long.MIN_VALUE;
import static java.lang.Long.compare;

public class Unsigned {
    private Unsigned() {
    }

    /**
     * Method is a copy of {@link Long#compareUnsigned(long, long)},
     * which is available only since 1.8
     * <p>
     * Compares two {@code long} values numerically treating the values
     * as unsigned.
     *
     * @param x the first {@code long} to compare
     * @param y the second {@code long} to compare
     * @return the value {@code 0} if {@code x == y}; a value less
     * than {@code 0} if {@code x < y} as unsigned values; and
     * a value greater than {@code 0} if {@code x > y} as
     * unsigned values
     */
    public static int compareUnsigned(long x, long y) {
        return compare(x + MIN_VALUE, y + MIN_VALUE);
    }

}
