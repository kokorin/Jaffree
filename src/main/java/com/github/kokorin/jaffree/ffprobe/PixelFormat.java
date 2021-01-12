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

import java.util.List;

@Deprecated
public class PixelFormat {
    private final DSection section;

    public PixelFormat(DSection section) {
        this.section = section;
    }

    public DSection getSection() {
        return section;
    }

    public PixelFormatFlags getFlags() {
        return new PixelFormatFlags(section.getTag("FLAGS"));
    }

    public List<PixelFormatComponent> getComponents() {
        return section.getSections("COMPONENT", new DSection.SectionConverter<PixelFormatComponent>() {
            @Override
            public PixelFormatComponent convert(DSection dSection) {
                return new PixelFormatComponent(dSection);
            }
        });
    }

    public String getName() {
        return section.getString("name");
    }

    public int getNbComponents() {
        return section.getInteger("nb_components");
    }

    public Integer getLog2ChromaW() {
        return section.getInteger("log2_chroma_w");
    }

    public Integer getLog2ChromaH() {
        return section.getInteger("log2_chroma_h");
    }

    public Integer getBitsPerPixel() {
        return section.getInteger("bits_per_pixel");
    }
}
