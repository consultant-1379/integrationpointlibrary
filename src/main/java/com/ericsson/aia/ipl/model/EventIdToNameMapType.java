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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * A mapping between the ID of an event, and it's name.
 * <p>
 * Java class for eventIdToNameMapType complex type.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventIdToNameMapType")
public class EventIdToNameMapType implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(name = "eventId", required = true)
    protected int eventId;

    @XmlAttribute(name = "eventName", required = true)
    protected String eventName;

    /**
     * Gets the value of the eventId property.
     *
     * @return the event id
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * Sets the value of the eventId property.
     *
     * @param eventId
     *     the eventId
     */
    public void setEventId(final int eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the value of the eventName property.
     *
     * @return possible object is {@link String }
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the value of the eventName property.
     *
     * @param eventName
     *     allowed object is {@link String }
     */
    public void setEventName(final String eventName) {
        this.eventName = eventName;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "EventIdToNameMapType{" + "eventId=" + eventId + ", eventName='" + eventName + '\'' + '}';
    }
}