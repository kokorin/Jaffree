/*
 *    Copyright 2018-2021 Denis Kokorin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.ProbeData;

/**
 * Stream disposition description.
 */
public class StreamDisposition {
    private final ProbeData probeData;

    /**
     * Creates {@link StreamDisposition} description based on provided ffprobe data.
     *
     * @param probeData ffprobe data
     */
    public StreamDisposition(final ProbeData probeData) {
        this.probeData = probeData;
    }

    /**
     * Returns data section which holds all the data provided by ffprobe.
     * <p>
     * Use this method if you have to access properties which are not accessible through
     * other getters.
     *
     * @return probe data
     */
    public ProbeData getProbeData() {
        return probeData;
    }

    /**
     * @return true if default
     */
    public Boolean getDefault() {
        return probeData.getBoolean("default");
    }

    /**
     * @return true if dub
     */
    public Boolean getDub() {
        return probeData.getBoolean("dub");
    }

    /**
     * @return true if original
     */
    public Boolean getOriginal() {
        return probeData.getBoolean("original");
    }

    /**
     * @return true if comment
     */
    public Boolean getComment() {
        return probeData.getBoolean("comment");
    }

    /**
     * @return true if lyrics
     */
    public Boolean getLyrics() {
        return probeData.getBoolean("lyrics");
    }

    /**
     * @return true if karaoke
     */
    public Boolean getKaraoke() {
        return probeData.getBoolean("karaoke");
    }

    /**
     * @return true if forced
     */
    public Boolean getForced() {
        return probeData.getBoolean("forced");
    }

    /**
     * @return true if hearing impaired
     */
    public Boolean getHearingImpaired() {
        return probeData.getBoolean("hearing_impaired");
    }

    /**
     * @return true if visual impaired
     */
    public Boolean getVisualImpaired() {
        return probeData.getBoolean("visual_impaired");
    }

    /**
     * @return true if clean effects
     */
    public Boolean getCleanEffects() {
        return probeData.getBoolean("clean_effects");
    }

    /**
     * @return true if attached pic
     */
    public Boolean getAttachedPic() {
        return probeData.getBoolean("attached_pic");
    }

    /**
     * @return true if timed thumbnails
     */
    public Boolean getTimedThumbnails() {
        return probeData.getBoolean("timed_thumbnails");
    }


}

