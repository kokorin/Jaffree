/*
 *    Copyright  2018 Denis Kokorin
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

import com.github.kokorin.jaffree.ffprobe.data.DSection;

public class LibraryVersion {
    private final DSection section;

    public LibraryVersion(DSection section) {
        this.section = section;
    }

    public DSection getSection() {
        return section;
    }

    public String getName() {
        return section.getString("name");
    }

    public int getMajor() {
        return section.getInteger("major");
    }

    public int getMinor() {
        return section.getInteger("minor");
    }

    public int getMicro() {
        return section.getInteger("micro");
    }

    public int getVersion() {
        return section.getInteger("version");
    }

    public String getIdent() {
        return section.getString("ident");
    }
}
