/*
 *    Copyright 2017-2021 Denis Kokorin
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

/**
 * Represents rational numbers.
 * <p>
 * {@link Double} and {@link Float} can't represent every rational number without precision loss,
 * which is crucial for correct media representation.
 */
public final class Rational extends Number implements Comparable<Rational> {
    private final long numerator;
    private final long denominator;

    public static final Rational ZERO = new Rational(0, 1);
    public static final Rational ONE = new Rational(1, 1);

    /**
     * Create {@link Rational}.
     *
     * @param numerator   numerator
     * @param denominator denominator
     */
    public Rational(final long numerator, final long denominator) {
        if (denominator <= 0) {
            throw new IllegalArgumentException("Denominator must be positive!");
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * Returns this {@link Rational} numerator.
     *
     * @return numerator
     */
    public long getNumerator() {
        return numerator;
    }

    /**
     * Returns this {@link Rational} denominator.
     *
     * @return denominator
     */
    public long getDenominator() {
        return denominator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Rational that) {
        return Long.compare(this.numerator * that.denominator, this.denominator * that.numerator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int intValue() {
        return (int) longValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long longValue() {
        return numerator / denominator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double doubleValue() {
        return 1. * numerator / denominator;
    }

    /**
     * Returns a {@link Rational} whose value is {@code (-this)}.
     *
     * @return {@code -this}
     */
    public Rational negate() {
        return new Rational(-numerator, denominator);
    }

    /**
     * Returns a {@link Rational} whose value is {@code (this + value)}.
     *
     * @param value value to be added to this {@link Rational}.
     * @return {@code this + val}
     */
    public Rational add(final Number value) {
        Rational that = toRational(value);
        return new Rational(
                this.numerator * that.denominator + that.numerator * this.denominator,
                this.denominator * that.denominator
        );
    }

    /**
     * Returns a {@link Rational} whose value is {@code (this - value)}.
     *
     * @param value value to be subtracted from this {@link Rational}.
     * @return {@code this - val}
     */
    public Rational subtract(final Number value) {
        Rational that = toRational(value);
        return add(that.negate());
    }

    /**
     * Returns a {@link Rational} whose value is {@code (this * value)}.
     *
     * @param value value to be multiplied by this {@link Rational}.
     * @return {@code this * value}
     */
    public Rational multiply(final Number value) {
        Rational that = toRational(value);
        return new Rational(this.numerator * that.numerator, this.denominator * that.denominator);
    }

    /**
     * Returns a {@link Rational} whose value is {@code (this / value)}.
     *
     * @param value divisor
     * @return {@code this / value}
     */
    public Rational divide(final Number value) {
        return multiply(toRational(value).inverse());
    }

    /**
     * Returns a {@link Rational} whose value is {@code (1 / this)}.
     *
     * @return {@code 1 / this}
     */
    public Rational inverse() {
        long sign = numerator >= 0 ? 1 : -1;
        return new Rational(sign * denominator, Math.abs(numerator));
    }

    /**
     * Returns true if {@code (this < value)}.
     *
     * @param that value to be compared with this {@link Rational}.
     * @return {@code this < value}
     */
    public boolean lessThan(final Number that) {
        return compareTo(toRational(that)) < 0;
    }

    /**
     * Returns true if {@code (this <= value)}.
     *
     * @param that value to be compared with this {@link Rational}.
     * @return {@code this <= value}
     */
    public boolean lessThanOrEqual(final Number that) {
        return compareTo(toRational(that)) <= 0;
    }

    /**
     * Returns true if {@code (this > value)}.
     *
     * @param that value to be compared with this {@link Rational}.
     * @return {@code this > value}
     */
    public boolean greaterThan(final Number that) {
        return compareTo(toRational(that)) > 0;
    }

    /**
     * Returns true if {@code (this >= value)}.
     *
     * @param that value to be compared with this {@link Rational}.
     * @return {@code this >= value}
     */
    public boolean greaterThanOrEqual(final Number that) {
        return compareTo(toRational(that)) >= 0;
    }

    /**
     * Simplifies this {@link Rational} by dividing numerator and denominator by greatest common
     * divisor.
     * <p>
     * E.g. {@code 2/6 } is simplified to {@code 1/3 }
     *
     * @return simplified {@link Rational}
     */
    public Rational simplify() {
        long gcd = gcd(Math.abs(numerator), denominator);
        return new Rational(numerator / gcd, denominator / gcd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toString("/");
    }

    /**
     * Converts {@link Rational} to {@link String} using specified delimiter.
     *
     * @param delimiter delimiter
     * @return this converted to {@link String}
     */
    public String toString(final String delimiter) {
        if (denominator == 1) {
            return Long.toString(numerator);
        }

        return numerator + delimiter + denominator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rational that = (Rational) o;
        return this.numerator * that.denominator == this.denominator * that.numerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    /**
     * Returns a {@link Rational} whose value is equal to that of the specified {@code long}.
     *
     * @param value value of the {@link Rational} to return.
     * @return a {@link Rational} with the specified value.
     */
    public static Rational valueOf(final long value) {
        return new Rational(value, 1L);
    }

    /**
     * Returns a {@link Rational} whose value is equal to that of the specified {@code double}.
     *
     * @param value value of the {@link Rational} to return.
     * @return a {@link Rational} with the specified value.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public static Rational valueOf(final double value) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        long numerator = bigDecimal.unscaledValue().longValue();
        long denominator = 1L;
        int scale = bigDecimal.scale();
        while (scale > 0) {
            denominator *= 10L;
            scale--;
        }
        while (scale < 0) {
            numerator *= 10L;
            scale++;
        }

        return new Rational(numerator, denominator);
    }


    /**
     * Parses {@link Rational}.
     * <p>
     * Numerator and denominator are expected to be separated by / (slash) symbol.
     *
     * @param value value to parse
     * @return Rational value
     * @throws NumberFormatException if wrong format
     */
    public static Rational valueOf(final String value) throws NumberFormatException {
        return valueOf(value, "/");
    }

    /**
     * Parses {@link Rational}.
     * <p>
     * Numerator and denominator are expected to be separated by delimiter.
     *
     * @param value     value to parse
     * @param delimiter delimiter which separates numerator and denominator
     * @return Rational value
     * @throws NumberFormatException if wrong format
     */
    public static Rational valueOf(final String value, final String delimiter)
            throws NumberFormatException {
        String[] parts = value.split(delimiter, 2);

        try {
            long numerator = Long.parseLong(parts[0]);
            long denominator = 1L;
            if (parts.length == 2) {
                denominator = Long.parseLong(parts[1]);
            }
            return new Rational(numerator, denominator);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("For input string: \"" + value + "\"");
        }
    }

    private static Rational toRational(final Number value) {
        if (value instanceof Rational) {
            return (Rational) value;
        }

        if (value instanceof Double || value instanceof Float) {
            return valueOf(value.doubleValue());
        }

        return valueOf(value.longValue());
    }

    /**
     * Returns greatest common divisor.
     *
     * @param a a
     * @param b b
     * @return greatest common divisor
     */
    private static long gcd(final long a, final long b) {
        BigInteger bigA = BigInteger.valueOf(a);
        BigInteger bigB = BigInteger.valueOf(b);
        return bigA.gcd(bigB).longValue();
    }
}
