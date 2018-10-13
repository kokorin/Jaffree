package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.Rational;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RationalAdapter extends XmlAdapter<String, Rational> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RationalAdapter.class);
    @Override
    public Rational unmarshal(String v) throws Exception {
        if (v == null || v.isEmpty() || v.equals("0/0")) {
            return null;
        }

        try {
            return Rational.valueOf(v);
        } catch (Exception e) {
            LOGGER.warn("Failed to parse rational number: " + v, e);
        }

        return null;
    }

    @Override
    public String marshal(Rational v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.toString();
    }
}
