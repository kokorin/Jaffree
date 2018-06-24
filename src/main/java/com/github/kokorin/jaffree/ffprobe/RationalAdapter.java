package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.Rational;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RationalAdapter extends XmlAdapter<String, Rational> {
    @Override
    public Rational unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }

        return Rational.valueOf(v);
    }

    @Override
    public String marshal(Rational v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.toString();
    }
}
