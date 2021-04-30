package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.ProbeData;

public interface TagAware {

    /**
     * Returns data section which holds all the data provided by ffprobe.
     * <p>
     * Use this method if you have to access properties which are not accessible through
     * other getters.
     *
     * @return probe data
     */
    ProbeData getProbeData();

    /**
     * Return tag string value by name
     * @param name tag name
     * @return tag value
     */
    default String getTag(String name) {
        return getProbeData().getSubDataString("tags", name);
    }

    /**
     * Return tag long value by name
     * @param name tag name
     * @return tag value
     */
    default Long getTagLong(String name) {
        return getProbeData().getSubDataLong("tags", name);
    }

    /**
     * Return tag integer value by name
     * @param name tag name
     * @return tag value
     */
    default Double getTagInteger(String name) {
        return getProbeData().getSubDataDouble("tags", name);
    }

    /**
     * Return tag double value by name
     * @param name tag name
     * @return tag value
     */
    default Double getTagDouble(String name) {
        return getProbeData().getSubDataDouble("tags", name);
    }

    /**
     * Return tag float value by name
     * @param name tag name
     * @return tag value
     */
    default Float getTagFloat(String name) {
        return getProbeData().getSubDataFloat("tags", name);
    }

}
