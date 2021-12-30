package com.github.kokorin.jaffree;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RationalTest {
    @Test
    public void equals() {
        assertEquals(Rational.valueOf(1L), Rational.valueOf(1L));
        assertEquals(new Rational(1L, 1L), new Rational(2L, 2L));
    }

    @Test
    public void compareTo() {
        assertEquals(0, Rational.valueOf(1L).compareTo(Rational.valueOf(1L)));
        assertEquals(0, new Rational(1L, 1L).compareTo(new Rational(2L, 2L)));
    }

    @Test
    public void valueOf() {
        assertEquals(Rational.ZERO, Rational.valueOf(0.));
        assertEquals(Rational.ONE, Rational.valueOf(1.));
        assertEquals(Rational.ONE.negate(), Rational.valueOf(-1.));
        assertEquals(new Rational(3_333_333_333_333_333L, 10_000_000_000_000_000L), Rational.valueOf(1. / 3));
        assertEquals(new Rational(1L, 10_000_000_000_000_000L), Rational.valueOf(1. / 10_000_000_000_000_000L));
        assertEquals(new Rational(10_000_000_000_000_000L, 1L), Rational.valueOf(10_000_000_000_000_000.));

        assertEquals(Rational.ONE, Rational.valueOf("1"));
        assertEquals(Rational.ONE, Rational.valueOf("1/1"));
        assertEquals(Rational.ONE, Rational.valueOf((Number) 1));
        assertEquals(Rational.ONE, Rational.valueOf((Number) 1L));
        assertEquals(Rational.ONE, Rational.valueOf((Number) 1.0));
        assertEquals(Rational.ONE, Rational.valueOf((Number) 1.0f));
        assertEquals(Rational.ONE.divide(10), Rational.valueOf("1/10"));
    }

    @Test
    public void inverse() {
        assertEquals(Rational.ONE, Rational.ONE.inverse());
        assertEquals(Rational.ONE.negate(), Rational.ONE.negate().inverse());
        assertEquals(new Rational(10L, 1L), (new Rational(1L, 10L)).inverse());
        assertEquals(new Rational(-10L, 1L), (new Rational(-1L, 10L)).inverse());
    }

    @Test
    public void divide() {
        assertEquals(Rational.ONE, Rational.ONE.divide(1));
        assertEquals(Rational.ONE.negate(), Rational.ONE.divide(-1));
        assertEquals(Rational.ONE.negate(), Rational.ONE.divide(Rational.ONE.negate()));
        assertEquals(Rational.ONE, (new Rational(10L, 1)).divide(new Rational(100L, 10L)));
        assertEquals(Rational.ONE, (new Rational(10L, 1)).divide(10));
        assertEquals(Rational.ZERO, Rational.ZERO.divide(1));
    }

    @Test
    public void simplify() {
        assertEquals("1/3", new Rational(10, 30).simplify().toString());
        assertEquals("1/3", new Rational(3, 9).simplify().toString());
    }

    @Test
    public void multiply() {
        assertEquals(new Rational(1, 6), new Rational(1, 2).multiply(new Rational(1, 3)));
    }

    @Test
    public void add() {
        assertEquals(new Rational(1, 2), new Rational(1, 6).add(new Rational(1, 3)));
    }

    @Test(expected = NumberFormatException.class)
    public void valueOfNFE_1() {
        Rational.valueOf("a");
    }

    @Test(expected = NumberFormatException.class)
    public void valueOfNFE_2() {
        Rational.valueOf("1/a");
    }

    @Test(expected = NumberFormatException.class)
    public void valueOfNFE_3() {
        Rational.valueOf("1/2/3");
    }
}