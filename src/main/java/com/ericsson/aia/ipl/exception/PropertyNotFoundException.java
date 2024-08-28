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

package com.ericsson.aia.ipl.exception;

/**
 * PropertyNotFoundException is an unchecked exception that's thrown when an IntegrationPoint Property could not be found.
 */
public class PropertyNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4599540318142638753L;

    /**
     * Instantiates a new PropertyNotFoundException.
     *
     * @param message
     *            the detail message.
     */
    public PropertyNotFoundException(final String message) {
        super(message);
    }
}
