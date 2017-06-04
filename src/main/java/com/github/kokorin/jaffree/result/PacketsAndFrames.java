
package com.github.kokorin.jaffree.result;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for packetsAndFramesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="packetsAndFramesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="packet" type="{http://www.ffmpeg.org/schema/ffprobe}packetType" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="frame" type="{http://www.ffmpeg.org/schema/ffprobe}frameType" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="subtitle" type="{http://www.ffmpeg.org/schema/ffprobe}subtitleType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "packetsAndFramesType", propOrder = {
    "packetOrFrameOrSubtitle"
})
public class PacketsAndFrames {

    @XmlElements({
        @XmlElement(name = "packet", type = Packet.class),
        @XmlElement(name = "frame", type = Frame.class),
        @XmlElement(name = "subtitle", type = Subtitle.class)
    })
    protected List<Object> packetOrFrameOrSubtitle;

    /**
     * Gets the value of the packetOrFrameOrSubtitle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the packetOrFrameOrSubtitle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPacketOrFrameOrSubtitle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Packet }
     * {@link Frame }
     * {@link Subtitle }
     * 
     * 
     */
    public List<Object> getPacketOrFrameOrSubtitle() {
        if (packetOrFrameOrSubtitle == null) {
            packetOrFrameOrSubtitle = new ArrayList<Object>();
        }
        return this.packetOrFrameOrSubtitle;
    }

}
