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

package com.github.kokorin.jaffree.ffprobe;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for streamDispositionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="streamDispositionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="default" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="dub" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="original" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="comment" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="lyrics" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="karaoke" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="forced" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="hearing_impaired" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="visual_impaired" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="clean_effects" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="attached_pic" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="timed_thumbnails" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "streamDispositionType")
public class StreamDisposition {

    @XmlAttribute(name = "default", required = true)
    protected int _default;
    @XmlAttribute(name = "dub", required = true)
    protected int dub;
    @XmlAttribute(name = "original", required = true)
    protected int original;
    @XmlAttribute(name = "comment", required = true)
    protected int comment;
    @XmlAttribute(name = "lyrics", required = true)
    protected int lyrics;
    @XmlAttribute(name = "karaoke", required = true)
    protected int karaoke;
    @XmlAttribute(name = "forced", required = true)
    protected int forced;
    @XmlAttribute(name = "hearing_impaired", required = true)
    protected int hearingImpaired;
    @XmlAttribute(name = "visual_impaired", required = true)
    protected int visualImpaired;
    @XmlAttribute(name = "clean_effects", required = true)
    protected int cleanEffects;
    @XmlAttribute(name = "attached_pic", required = true)
    protected int attachedPic;
    @XmlAttribute(name = "timed_thumbnails", required = true)
    protected int timedThumbnails;

    /**
     * Gets the value of the default property.
     * 
     */
    public int getDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     * 
     */
    public void setDefault(int value) {
        this._default = value;
    }

    /**
     * Gets the value of the dub property.
     * 
     */
    public int getDub() {
        return dub;
    }

    /**
     * Sets the value of the dub property.
     * 
     */
    public void setDub(int value) {
        this.dub = value;
    }

    /**
     * Gets the value of the original property.
     * 
     */
    public int getOriginal() {
        return original;
    }

    /**
     * Sets the value of the original property.
     * 
     */
    public void setOriginal(int value) {
        this.original = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     */
    public int getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     */
    public void setComment(int value) {
        this.comment = value;
    }

    /**
     * Gets the value of the lyrics property.
     * 
     */
    public int getLyrics() {
        return lyrics;
    }

    /**
     * Sets the value of the lyrics property.
     * 
     */
    public void setLyrics(int value) {
        this.lyrics = value;
    }

    /**
     * Gets the value of the karaoke property.
     * 
     */
    public int getKaraoke() {
        return karaoke;
    }

    /**
     * Sets the value of the karaoke property.
     * 
     */
    public void setKaraoke(int value) {
        this.karaoke = value;
    }

    /**
     * Gets the value of the forced property.
     * 
     */
    public int getForced() {
        return forced;
    }

    /**
     * Sets the value of the forced property.
     * 
     */
    public void setForced(int value) {
        this.forced = value;
    }

    /**
     * Gets the value of the hearingImpaired property.
     * 
     */
    public int getHearingImpaired() {
        return hearingImpaired;
    }

    /**
     * Sets the value of the hearingImpaired property.
     * 
     */
    public void setHearingImpaired(int value) {
        this.hearingImpaired = value;
    }

    /**
     * Gets the value of the visualImpaired property.
     * 
     */
    public int getVisualImpaired() {
        return visualImpaired;
    }

    /**
     * Sets the value of the visualImpaired property.
     * 
     */
    public void setVisualImpaired(int value) {
        this.visualImpaired = value;
    }

    /**
     * Gets the value of the cleanEffects property.
     * 
     */
    public int getCleanEffects() {
        return cleanEffects;
    }

    /**
     * Sets the value of the cleanEffects property.
     * 
     */
    public void setCleanEffects(int value) {
        this.cleanEffects = value;
    }

    /**
     * Gets the value of the attachedPic property.
     * 
     */
    public int getAttachedPic() {
        return attachedPic;
    }

    /**
     * Sets the value of the attachedPic property.
     * 
     */
    public void setAttachedPic(int value) {
        this.attachedPic = value;
    }

    /**
     * Gets the value of the timedThumbnails property.
     * 
     */
    public int getTimedThumbnails() {
        return timedThumbnails;
    }

    /**
     * Sets the value of the timedThumbnails property.
     * 
     */
    public void setTimedThumbnails(int value) {
        this.timedThumbnails = value;
    }

}
