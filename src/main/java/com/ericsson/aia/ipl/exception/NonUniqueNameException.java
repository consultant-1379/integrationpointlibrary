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
 * IntegrationPointNotFoundException is an unchecked exception that's thrown when an IntegrationPoint could not be found.
 */
public class NonUniqueNameException extends RuntimeException {

    private static final long serialVersionUID = 4599540318142638753L;

    /**
     * Instantiates a new IntegrationPoint not found exception.
     *
     * @param message
     *            the detail message.
     */
    public NonUniqueNameException(final String message) {
        super(message);
    }

}
