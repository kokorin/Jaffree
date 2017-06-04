
package com.github.kokorin.jaffree.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for packetSideDataListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="packetSideDataListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="side_data" type="{http://www.ffmpeg.org/schema/ffprobe}packetSideDataType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "packetSideDataListType", propOrder = {
    "sideData"
})
public class PacketSideDataList {

    @XmlElement(name = "side_data", required = true)
    protected List<PacketSideData> sideData;

    /**
     * Gets the value of the sideData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sideData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSideData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PacketSideData }
     * 
     * 
     */
    public List<PacketSideData> getSideData() {
        if (sideData == null) {
            sideData = new ArrayList<PacketSideData>();
        }
        return this.sideData;
    }

}
