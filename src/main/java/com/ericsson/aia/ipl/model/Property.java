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

import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Property to be used by the configuration. It can be applied to any case where there is need for a name representing a value.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "propertyType")
public class Property {

    public static final Property PROPERTY_NON_EXISTENT = new Property();

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "value", required = true)
    private String value;

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Property)) {
            return false;
        }
        final Property castOther = (Property) other;
        return new EqualsBuilder().append(name, castOther.name).append(value, castOther.value).isEquals();
    }

    /**
     * Gets the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value.
     *
     * @return the value.
     */
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(value).toHashCode();
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the value.
     *
     * @param value
     *            the new value.
     */
    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Property [name=" + name + ", value=" + value + "]";
    }

}
