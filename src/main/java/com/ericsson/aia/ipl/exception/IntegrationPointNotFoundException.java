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
public class IntegrationPointNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4599540318142638753L;

    /**
     * Instantiates a new IntegrationPointNotFoundException.
     *
     * @param message
     *            the detail message.
     */
    public IntegrationPointNotFoundException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new IntegrationPointNotFoundException
     *
     * @param message
     *            the exception message.
     * @param exception
     *            the exception thrown.
     */
    public IntegrationPointNotFoundException(final String message, final Exception exception) {
        super(message, exception);
    }

}
