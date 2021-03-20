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

// TODO test properties
public class StreamDisposition {
    private final ProbeData probeData;

    public StreamDisposition(ProbeData probeData) {
        this.probeData = probeData;
    }

    public ProbeData getProbeData() {
        return probeData;
    }

    // TODO make boolean?
    public Integer getDefault() {
        return probeData.getInteger("default");
    }

    public Integer getDub() {
        return probeData.getInteger("dub");
    }

    public Integer getOriginal() {
        return probeData.getInteger("original");
    }

    public Integer getComment() {
        return probeData.getInteger("comment");
    }

    public Integer getLyrics() {
        return probeData.getInteger("lyrics");
    }

    public Integer getKaraoke() {
        return probeData.getInteger("karaoke");
    }

    public Integer getForced() {
        return probeData.getInteger("forced");
    }

    public Integer getHearingImpaired() {
        return probeData.getInteger("hearing_impaired");
    }

    public Integer getVisualImpaired() {
        return probeData.getInteger("visual_impaired");
    }

    public Integer getCleanEffects() {
        return probeData.getInteger("clean_effects");
    }

    public Integer getAttachedPic() {
        return probeData.getInteger("attached_pic");
    }

    public Integer getTimedThumbnails() {
        return probeData.getInteger("timed_thumbnails");
    }


}

