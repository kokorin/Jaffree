
package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DSection;

import java.util.List;

public class PixelFormat {
    private final DSection section;

    public PixelFormat(DSection section) {
        this.section = section;
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
