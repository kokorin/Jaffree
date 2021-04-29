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

/**
 * Different utility methods used in Nut format muxer and demuxer.
 */
public final class Util {
    private Util() {
    }

    /**
     * TODO replace with  {@link Long#compareUnsigned(long, long)}
     * <p>
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
    public static int compareUnsigned(final long x, final long y) {
        return compare(x + MIN_VALUE, y + MIN_VALUE);
    }


    /**
     * Converts passed in PTS from one timebase to another.
     * <p>
     * This method is equivalent to (ln*sn)/(d1*d2)
     *
     * @param pts          pts in timeBaseFrom
     * @param timeBaseFrom pts timebase
     * @param timeBaseTo   timebase to switch to
     * @return converted PTS
     */
    public static long convertTimestamp(final long pts, final Rational timeBaseFrom,
                                        final Rational timeBaseTo) {
        long ln = timeBaseFrom.getNumerator() * pts;
        long sn = timeBaseTo.getDenominator();
        long d1 = timeBaseFrom.getDenominator();
        long d2 = timeBaseTo.getNumerator();
        // This calculation MUST be done with unsigned 64 bit integers, and
        // is equivalent to (ln*sn)/(d1*d2) but this would require a 96 bit integer
        return (ln / d1 * sn + ln % d1 * sn / d1) / d2;
    }
}
