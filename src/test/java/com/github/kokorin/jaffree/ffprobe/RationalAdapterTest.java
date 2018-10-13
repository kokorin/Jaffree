package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.Rational;
import org.junit.Assert;
import org.junit.Test;

public class RationalAdapterTest {
    private final RationalAdapter adapter = new RationalAdapter();

    @Test
    public void unmarshal() throws Exception{
        Assert.assertNull(adapter.unmarshal(null));
        Assert.assertNull(adapter.unmarshal(""));
        Assert.assertNull(adapter.unmarshal("0/0"));
        Assert.assertNull(adapter.unmarshal("not_numeric"));

        Assert.assertEquals(new Rational(1, 1), adapter.unmarshal("1"));
    }
}