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
 * InvalidUriException is an unchecked exception that's thrown when an invalid URI is used.
 */
public class InvalidUriException extends RuntimeException {

    private static final long serialVersionUID = -6347417855218293866L;

    /**
     * Instantiates a new invalid URI exception.
     *
     * @param message
     *            the detail message.
     */
    public InvalidUriException(final String message) {
        super(message);
    }

}
