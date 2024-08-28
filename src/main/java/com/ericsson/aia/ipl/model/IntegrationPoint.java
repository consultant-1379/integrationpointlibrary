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

import static com.ericsson.aia.ipl.model.Property.PROPERTY_NON_EXISTENT;
import static com.ericsson.aia.ipl.util.Utils.standardizeName;

import java.util.Arrays;
import java.util.Properties;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ericsson.aia.ipl.util.Utils;

/**
 * IntegrationPoint contains the properties and configurations specific for that that IntegrationPoint type, like EventPublishers or EventSubscribers.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "properties", "destinations", "eventMap" })
@XmlRootElement(name = "IntegrationPoint")
public class IntegrationPoint {

    public static final IntegrationPoint INVALID_INTEGRATION_POINT = new IntegrationPoint();

    @XmlElement(name = "eventMap")
    protected EventMapType eventMap;

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "type", required = true)
    private IntegrationPointType type;

    @XmlElement(name = "property")
    private Property[] properties;

    @XmlElement(name = "destination", required = true)
    private Destination[] destinations;

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof IntegrationPoint)) {
            return false;
        }
        final IntegrationPoint castOther = (IntegrationPoint) other;
        return new EqualsBuilder().append(type, castOther.type).append(properties, castOther.properties).append(destinations, castOther.destinations)
                .append(eventMap, castOther.eventMap).isEquals();
    }

    /**
     * Gets the destinations.
     *
     * @return the destinations
     */
    public Destination[] getDestinations() {
        return destinations;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public Properties getProperties() {
        return Utils.toProperties(properties);
    }

    /**
     * Gets the type associated to this IntegrationPoint.
     *
     * @return the associated type.
     */
    public IntegrationPointType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(properties).append(destinations).append(eventMap).toHashCode();
    }

    /**
     * Sets the destinations.
     *
     * @param destinations
     *            the new destinations
     */
    public void setDestinations(final Destination[] destinations) {
        this.destinations = destinations;
    }

    /**
     * @param name
     *            the name to set
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

    /**
     * Sets the type associated to this IntegrationPoint.
     *
     * @param type
     *            the new type
     */
    public void setType(final IntegrationPointType type) {
        this.type = type;
    }

    /**
     * Gets the value of the eventMap property.
     *
     * @return possible object is {@link EventMapType }
     */
    public EventMapType getEventMap() {
        return eventMap;
    }

    /**
     * Sets the value of the eventMap property.
     *
     * @param eventMapType
     *     allowed object is {@link EventMapType }
     */
    public void setEventMap(final EventMapType eventMapType) {
        this.eventMap = eventMapType;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "IntegrationPoint [type=" + type + ", destinations=" + Arrays.toString(destinations) + ", properties=" + Arrays.toString(properties)
                + ", eventMap=" + eventMap + "]";
    }

    /**
     * Replaces the value of a property if this value is a placeholder.
     * This is to be used in case there is a property whose value can only be determined in runtime, for example.
     *
     * @param propertyName
     *            the name of the property.
     * @param propertyValue
     *            the value of the property.
     * @param placeholder
     *            the placeholder to be replaced.
     */
    public void replacePlaceHolderForValueInProperty(final String propertyName, final String propertyValue, final String placeholder) {
        final Property property = getProperty(propertyName);
        if (property != PROPERTY_NON_EXISTENT) {
            replaceValueIfPlaceholderIsPresent(property, propertyValue, placeholder);
        }
    }

    private void replaceValueIfPlaceholderIsPresent(final Property property, final String propertyValue, final String placeholder) {
        if (placeholder.equals(property.getValue())) {
            property.setValue(propertyValue);
        }
    }

    /**
     * Gets the property.
     *
     * @param propertyName
     *            the property name
     * @return the searched for property or Property.PROPERTY_NON_EXISTENT if the property is not found.
     */
    public Property getProperty(final String propertyName) {
        final String propertyNameStandardized = standardizeName(propertyName);
        if (getProperties() != null) {
            return searchForProperty(propertyNameStandardized);
        }
        return PROPERTY_NON_EXISTENT;
    }

    private Property searchForProperty(final String propertyName) {
        for (final Property property : properties) {
            final String name = standardizeName(property.getName());
            if (name.equals(propertyName)) {
                return property;
            }
        }
        return PROPERTY_NON_EXISTENT;
    }

}
