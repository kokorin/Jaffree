/*
 *    Copyright  2017 Denis Kokorin
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

package com.github.kokorin.jaffree.nut;

import java.util.Objects;

public class Timestamp {
    public final int timebaseId;
    public final long pts;

    public Timestamp(int timebaseId, long pts) {
        this.timebaseId = timebaseId;
        this.pts = pts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timestamp timestamp = (Timestamp) o;
        return timebaseId == timestamp.timebaseId &&
                pts == timestamp.pts;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timebaseId, pts);
    }
}
