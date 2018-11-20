package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.StreamType;
import org.junit.Assert;
import org.junit.Test;

public class StreamTypeAdapterTest {

    @Test
    public void unmarshal() throws Exception{
        Adapters.StreamTypeAdapter adapter = new Adapters.StreamTypeAdapter();

        Assert.assertNull(adapter.unmarshal(null));
        Assert.assertNull(adapter.unmarshal(""));

        Assert.assertEquals(StreamType.VIDEO, adapter.unmarshal("video"));
        Assert.assertEquals(StreamType.AUDIO, adapter.unmarshal("audio"));
        Assert.assertEquals(StreamType.DATA, adapter.unmarshal("data"));
        Assert.assertEquals(StreamType.SUBTITLE, adapter.unmarshal("subtitle"));
        Assert.assertEquals(StreamType.ATTACHMENT, adapter.unmarshal("attachment"));
    }
}