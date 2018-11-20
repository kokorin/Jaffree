package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.Rational;
import com.github.kokorin.jaffree.StreamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapters {
    private Adapters(){}

    public static class RationalAdapter extends XmlAdapter<String, Rational> {
        private static final Logger LOGGER = LoggerFactory.getLogger(RationalAdapter.class);
        @Override
        public Rational unmarshal(String v) {
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
        public String marshal(Rational v) {
            if (v == null) {
                return null;
            }
            return v.toString();
        }
    }

    public static class RatioAdapter extends XmlAdapter<String, Rational> {
        private static final Logger LOGGER = LoggerFactory.getLogger(RationalAdapter.class);
        @Override
        public Rational unmarshal(String v) {
            if (v == null || v.isEmpty() || v.equals("0:0")) {
                return null;
            }

            v = v.replace(':', '/');

            try {
                return Rational.valueOf(v);
            } catch (Exception e) {
                LOGGER.warn("Failed to parse rational number: " + v, e);
            }

            return null;
        }

        @Override
        public String marshal(Rational v) {
            if (v == null) {
                return null;
            }
            return v.toString().replace('/', ':');
        }
    }

    public static class StreamTypeAdapter extends XmlAdapter<String, StreamType> {
        private static final Logger LOGGER = LoggerFactory.getLogger(StreamTypeAdapter.class);
        @Override
        public StreamType unmarshal(String v) {
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
        public String marshal(StreamType v) {
            if (v == null) {
                return null;
            }
            return v.name().toLowerCase();
        }
    }
}
