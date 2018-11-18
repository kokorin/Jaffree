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

package com.github.kokorin.jaffree;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum SizeUnit {
    K(1_000L),
    M(1_000_000L),
    G(1_000_000_000L),

    Ki(1_024L),
    Mi(1_024L * 1_024),
    Gi(1_024L * 1_024 * 1_024),

    B(8L),
    KB(1_000L * 8),
    MB(1_000_000L * 8),
    GB(1_000_000_000L * 8),

    KiB(1_024L * 8),
    MiB(1_024L * 1_024 * 8),
    GiB(1_024L * 1_024 * 1_024 * 8),
    ;
    private long multiplier;

    SizeUnit(long multiplier) {
        this.multiplier = multiplier;
    }

    public long multiplier() {
        return multiplier;
    }

    public long convertTo(long value, SizeUnit unit) {
        return BigDecimal.valueOf(value)
                .multiply(BigDecimal.valueOf(this.multiplier))
                .divide(BigDecimal.valueOf(unit.multiplier), RoundingMode.CEILING)
                .longValue();
    }

    public long toBytes(long value) {
        return convertTo(value, B);
    }
}
