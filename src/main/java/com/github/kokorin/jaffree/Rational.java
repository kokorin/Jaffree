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
import java.util.Objects;

public class Rational extends Number implements Comparable<Rational> {
    public final long numerator;
    public final long denominator;

    public Rational(long value) {
        this(value, 1L);
    }

    public Rational(long numerator, long denominator) {
        if (denominator <= 0) {
            throw new IllegalArgumentException("Denominator must be positive!");
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    @Override
    public int compareTo(Rational that) {
        return Long.compare(this.numerator * that.denominator, this.denominator * that.numerator);
    }

    @Override
    public int intValue() {
        return (int) longValue();
    }

    @Override
    public long longValue() {
        return numerator / denominator;
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        return 1. * numerator / denominator;
    }

    @Override
    public String toString() {
        if (denominator == 1) {
            return Long.toString(numerator);
        }

        return numerator + "/" + denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rational that = (Rational) o;
        return this.numerator * that.denominator == this.denominator * that.numerator;
    }

    @Override
    public int hashCode() {

        return Objects.hash(numerator, denominator);
    }

    public static Rational valueOf(double d) {
        BigDecimal bigDecimal = BigDecimal.valueOf(d);
        long numerator = bigDecimal.unscaledValue().longValue();
        long denominator = 1L;
        // pow(10L, bigDecimal.scale());
        for (int i = 0; i < bigDecimal.scale(); i++) {
            denominator *= 10L;
        }

        return new Rational(numerator, denominator);
    }

    public static Rational valueOf(String value) throws NumberFormatException {
        String[] parts = value.split("/", 2);

        try {
            long numerator;
            long denominator = 1L;
            numerator = Long.parseLong(parts[0]);
            if (parts.length == 2) {
                denominator = Long.parseLong(parts[1]);
            }
            return new Rational(numerator, denominator);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("For input string: \"" + value + "\"");
        }
    }
}
