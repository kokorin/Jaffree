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

package com.github.kokorin.jaffree.nut;

import com.github.kokorin.jaffree.Rational;
import static java.lang.Long.MIN_VALUE;
import static java.lang.Long.compare;

public class Util {
    private Util() {
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


    public static long convertTimestamp(long pts, Rational timeBaseFrom, Rational timeBaseTo) {
        long ln = timeBaseFrom.numerator * pts;
        long sn = timeBaseTo.denominator;
        long d1 = timeBaseFrom.denominator;
        long d2 = timeBaseTo.numerator;
        return (ln / d1 * sn + ln % d1 * sn / d1) / d2;
    }

    public static long toMillis(long pts, Rational timebase) {
        return 1000L * pts * timebase.numerator / timebase.denominator;
    }
}
