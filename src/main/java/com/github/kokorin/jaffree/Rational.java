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
import java.math.BigInteger;
import java.util.Objects;

public class Rational extends Number implements Comparable<Rational> {
    public final long numerator;
    public final long denominator;

    public static final Rational ZERO = new Rational(0, 1);
    public static final Rational ONE = new Rational(1, 1);

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

    public Rational negate() {
        return new Rational(-numerator, denominator);
    }

    public Rational add(Number value) {
        Rational that = toRational(value);
        return new Rational(
                this.numerator * that.denominator + that.numerator * this.denominator,
                this.denominator * that.denominator
        );
    }

    public Rational subtract(Number value) {
        Rational that = toRational(value);
        return add(that.negate());
    }

    public Rational multiply(Number value) {
        Rational that = toRational(value);
        return new Rational(this.numerator * that.numerator, this.denominator * that.denominator);
    }

    public boolean lessThan(Number that) {
        return compareTo(toRational(that)) < 0;
    }

    public boolean lessThanOrEqual(Number that) {
        return compareTo(toRational(that)) <= 0;
    }

    public boolean greaterThan(Number that) {
        return compareTo(toRational(that)) > 0;
    }

    public boolean greaterThanOrEqual(Number that) {
        return compareTo(toRational(that)) >= 0;
    }

    public Rational simplify() {
        long gcd = gcd(Math.abs(numerator), denominator);
        return new Rational(numerator / gcd, denominator / gcd);
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

    public static Rational valueOf(long value) {
        return new Rational(value, 1L);
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

    private static Rational toRational(Number value) {
        if (value instanceof Rational) {
            return (Rational) value;
        }

        if (value instanceof Double || value instanceof Float) {
            return valueOf(value.doubleValue());
        }

        return valueOf(value.longValue());
    }

    private static long gcd(long a, long b) {
        BigInteger bigA = BigInteger.valueOf(a);
        BigInteger bigB = BigInteger.valueOf(b);
        return bigA.gcd(bigB).longValue();
    }
}
