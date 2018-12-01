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

public class Log {
    private final DSection section;

    public Log(DSection section) {
        this.section = section;
    }

    public DSection getSection() {
        return section;
    }

    public String getContext() {
        return section.getString("context");
    }

    public Integer getLevel() {
        return section.getInteger("level");
    }

    public Integer getCategory() {
        return section.getInteger("category");
    }

    public String getParentContext() {
        return section.getString("parent_context");
    }

    public Integer getParentCategory() {
        return section.getInteger("parent_category");
    }

    public String getMessage() {
        return section.getString("message");
    }
}
