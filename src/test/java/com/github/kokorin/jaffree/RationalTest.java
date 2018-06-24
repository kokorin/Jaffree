package com.github.kokorin.jaffree;

import org.junit.Assert;
import org.junit.Test;

public class RationalTest {
    @Test
    public void equals() {
        Assert.assertEquals(new Rational(1L), new Rational(1L));
        Assert.assertEquals(new Rational(1L, 1L), new Rational(2L, 2L));
    }

    @Test
    public void compareTo() {
        Assert.assertEquals(0, new Rational(1L).compareTo(new Rational(1L)));
        Assert.assertEquals(0, new Rational(1L, 1L).compareTo(new Rational(2L, 2L)));
    }

    @Test
    public void valueOf() {
        Assert.assertEquals(new Rational(1L), Rational.valueOf(1.));
        Assert.assertEquals(new Rational(3_333_333_333_333_333L, 10_000_000_000_000_000L), Rational.valueOf(1. / 3));

        Assert.assertEquals(new Rational(1L), Rational.valueOf("1"));
        Assert.assertEquals(new Rational(1L), Rational.valueOf("1/1"));
        Assert.assertEquals(new Rational(1L, 10L), Rational.valueOf("1/10"));
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