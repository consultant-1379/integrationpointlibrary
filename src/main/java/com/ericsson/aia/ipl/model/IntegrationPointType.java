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

/**
 * Enum defining the available types of IntegrationPoint.
 */
public enum IntegrationPointType {

    /** The subscriber. */
    SUBSCRIBER,

    /** The publisher. */
    PUBLISHER;

    private static final String EMPTY_TYPE = "";

    /**
     * Generates a String representation of the valid IntegrationPointTypes accepted by the component.
     *
     * @return the string representation.
     */
    public static String printValidTypes() {
        final String typeSeparator = ", ";
        String validTypes = EMPTY_TYPE;
        for (final IntegrationPointType type : IntegrationPointType.values()) {
            validTypes += typeSeparator + type.name();
        }
        final String formattedValidTypes = validTypes.substring(typeSeparator.length());
        return formattedValidTypes;
    }
}
