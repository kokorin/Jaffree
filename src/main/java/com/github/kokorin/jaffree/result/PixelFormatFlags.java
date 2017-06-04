
package com.github.kokorin.jaffree.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pixelFormatFlagsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pixelFormatFlagsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="big_endian" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="palette" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="bitstream" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="hwaccel" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="planar" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="rgb" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="pseudopal" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="alpha" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pixelFormatFlagsType")
public class PixelFormatFlags {

    @XmlAttribute(name = "big_endian", required = true)
    protected int bigEndian;
    @XmlAttribute(name = "palette", required = true)
    protected int palette;
    @XmlAttribute(name = "bitstream", required = true)
    protected int bitstream;
    @XmlAttribute(name = "hwaccel", required = true)
    protected int hwaccel;
    @XmlAttribute(name = "planar", required = true)
    protected int planar;
    @XmlAttribute(name = "rgb", required = true)
    protected int rgb;
    @XmlAttribute(name = "pseudopal", required = true)
    protected int pseudopal;
    @XmlAttribute(name = "alpha", required = true)
    protected int alpha;

    /**
     * Gets the value of the bigEndian property.
     * 
     */
    public int getBigEndian() {
        return bigEndian;
    }

    /**
     * Sets the value of the bigEndian property.
     * 
     */
    public void setBigEndian(int value) {
        this.bigEndian = value;
    }

    /**
     * Gets the value of the palette property.
     * 
     */
    public int getPalette() {
        return palette;
    }

    /**
     * Sets the value of the palette property.
     * 
     */
    public void setPalette(int value) {
        this.palette = value;
    }

    /**
     * Gets the value of the bitstream property.
     * 
     */
    public int getBitstream() {
        return bitstream;
    }

    /**
     * Sets the value of the bitstream property.
     * 
     */
    public void setBitstream(int value) {
        this.bitstream = value;
    }

    /**
     * Gets the value of the hwaccel property.
     * 
     */
    public int getHwaccel() {
        return hwaccel;
    }

    /**
     * Sets the value of the hwaccel property.
     * 
     */
    public void setHwaccel(int value) {
        this.hwaccel = value;
    }

    /**
     * Gets the value of the planar property.
     * 
     */
    public int getPlanar() {
        return planar;
    }

    /**
     * Sets the value of the planar property.
     * 
     */
    public void setPlanar(int value) {
        this.planar = value;
    }

    /**
     * Gets the value of the rgb property.
     * 
     */
    public int getRgb() {
        return rgb;
    }

    /**
     * Sets the value of the rgb property.
     * 
     */
    public void setRgb(int value) {
        this.rgb = value;
    }

    /**
     * Gets the value of the pseudopal property.
     * 
     */
    public int getPseudopal() {
        return pseudopal;
    }

    /**
     * Sets the value of the pseudopal property.
     * 
     */
    public void setPseudopal(int value) {
        this.pseudopal = value;
    }

    /**
     * Gets the value of the alpha property.
     * 
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * Sets the value of the alpha property.
     * 
     */
    public void setAlpha(int value) {
        this.alpha = value;
    }

}
