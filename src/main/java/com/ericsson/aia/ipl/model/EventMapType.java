/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.aia.ipl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A list of mappings.
 * <p>
 * Java class for eventMapType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventMapType", propOrder = { "mapping" })
public class EventMapType implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    protected List<EventIdToNameMapType> mapping;

    /**
     * Gets the value of the mapping property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be
     * present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the mapping property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMapping().add(newItem);
     * </pre>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link EventIdToNameMapType }
     *
     * @return the mapping
     */
    public List<EventIdToNameMapType> getMapping() {
        if (mapping == null) {
            mapping = new ArrayList<EventIdToNameMapType>();
        }
        return this.mapping;
    }
}