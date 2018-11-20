package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.Rational;
import org.junit.Assert;
import org.junit.Test;

public class RatioAdapterTest {
    private final Adapters.RatioAdapter adapter = new Adapters.RatioAdapter();

    @Test
    public void unmarshal() throws Exception{
        Assert.assertNull(adapter.unmarshal(null));
        Assert.assertNull(adapter.unmarshal(""));
        Assert.assertNull(adapter.unmarshal("0:0"));
        Assert.assertNull(adapter.unmarshal("not_numeric"));

        Assert.assertEquals(new Rational(1, 1), adapter.unmarshal("1"));
        Assert.assertEquals(new Rational(1, 10), adapter.unmarshal("1:10"));
        Assert.assertEquals("1:10", adapter.marshal(new Rational(1, 10)));
    }
}