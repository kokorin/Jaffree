package com.github.kokorin.jaffree;

import org.junit.Assert;
import org.junit.Test;

public class RationalTest {
    @Test
    public void equals() {
        Assert.assertEquals(Rational.valueOf(1L), Rational.valueOf(1L));
        Assert.assertEquals(new Rational(1L, 1L), new Rational(2L, 2L));
    }

    @Test
    public void compareTo() {
        Assert.assertEquals(0, Rational.valueOf(1L).compareTo(Rational.valueOf(1L)));
        Assert.assertEquals(0, new Rational(1L, 1L).compareTo(new Rational(2L, 2L)));
    }

    @Test
    public void valueOf() {
        Assert.assertEquals(Rational.valueOf(1L), Rational.valueOf(1.));
        Assert.assertEquals(new Rational(3_333_333_333_333_333L, 10_000_000_000_000_000L), Rational.valueOf(1. / 3));

        Assert.assertEquals(Rational.valueOf(1L), Rational.valueOf("1"));
        Assert.assertEquals(Rational.valueOf(1L), Rational.valueOf("1/1"));
        Assert.assertEquals(new Rational(1L, 10L), Rational.valueOf("1/10"));
    }

    @Test
    public void simplify() {
        Assert.assertEquals("1/3", new Rational(10, 30).simplify().toString());
        Assert.assertEquals("1/3", new Rational(3, 9).simplify().toString());
    }

    @Test
    public void multiply() {
        Assert.assertEquals(new Rational(1, 6), new Rational(1, 2).multiply(new Rational(1, 3)));
    }

    @Test
    public void add() {
        Assert.assertEquals(new Rational(1, 2), new Rational(1, 6).add(new Rational(1, 3)));
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