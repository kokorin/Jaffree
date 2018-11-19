package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.StreamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StreamTypeAdapter extends XmlAdapter<String, StreamType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamTypeAdapter.class);
    @Override
    public StreamType unmarshal(String v) throws Exception {
        if (v == null || v.isEmpty()) {
            return null;
        }

        try {
            return StreamType.valueOf(v.toUpperCase());
        } catch (Exception e) {
            LOGGER.warn("Failed to parse rational number: " + v, e);
        }

        return null;
    }

    @Override
    public String marshal(StreamType v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.name().toLowerCase();
    }
}
