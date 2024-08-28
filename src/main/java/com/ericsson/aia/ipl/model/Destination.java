/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.aia.ipl.model;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Destination of the IntegrationPoint configuration. It represents the place from which consumers will read and also the place to where the producers
 * will write.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "destinationType", propOrder = { "properties", "events" })
public class Destination {

    @XmlElement(name = "property")
    private Property[] properties;

    @XmlElement(name = "eventId")
    private List<String> events;

    @XmlAttribute(name = "name", required = true)
    private String name;

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Destination)) {
            return false;
        }
        final Destination castOther = (Destination) other;
        return new EqualsBuilder().append(name, castOther.name).append(events, castOther.events).append(properties, castOther.properties).isEquals();
    }

    /**
     * Gets the events allowed for this destination. In case of an empty return, *all events* are allowed to be in this destination.
     *
     * @return the allowed events for this destination.
     */
    public List<String> getEvents() {
        return events;
    }

    /**
     * Gets the destination name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the properties that configure this destination.
     *
     * @return the properties
     */
    public Property[] getProperties() {
        return properties;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(events).append(properties).toHashCode();
    }

    /**
     * Checks if is event filter off.
     *
     * @return true, if is event filter off
     */
    public boolean isEventFilterOff() {
        return events == null || events.isEmpty();
    }

    /**
     * Checks if the event is whitelisted in the destination. This occurs in two ways: either there is no event defined in the destination or the
     * event is defined.
     *
     * @param eventId
     *            the event id
     * @return true, if successful
     */
    public boolean isEventWhitelisted(final String eventId) {
        return isEventFilterOff() || events.contains(eventId.trim());
    }

    /**
     * Sets the events for this destination. In case of an empty parameter, *all events* are allowed to be in this destination.
     *
     * @param events
     *            the allowed events for this destination.
     */
    public void setEvents(final List<String> events) {
        this.events = events;
    }

    /**
     * Sets the destination name.
     *
     * @param name
     *            the new name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the properties.
     *
     * @param properties
     *            the new properties
     */
    public void setProperties(final Property[] properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "Destination [name=" + name + ", events=" + events + ", properties=" + Arrays.toString(properties) + "]";
    }

}
