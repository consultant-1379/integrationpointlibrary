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
 * InvalidIntegrationPointException is thrown when an invalid IntegrationPoint is used.
 */
public class InvalidIntegrationPointException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * InvalidIntegrationPointException.
     *
     * @param message
     *            the exception message.
     */
    public InvalidIntegrationPointException(final String message) {
        super(message);

    }

    /**
     * InvalidIntegrationPointException.
     *
     * @param message
     *            the exception message.
     * @param exception
     *            the exception thrown.
     */
    public InvalidIntegrationPointException(final String message, final Exception exception) {
        super(message, exception);
    }

}
