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
 * KafkaBrokerAddressesNotResolvedException is an unchecked exception that's thrown when it is not possible to resolve the Kafka broker addresses
 * during runtime.
 */
public class KafkaBrokerAddressesNotResolvedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new kafka broker addresses not resolved exception.
     *
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public KafkaBrokerAddressesNotResolvedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new kafka broker addresses not resolved exception.
     *
     * @param message
     *            the detail message
     */
    public KafkaBrokerAddressesNotResolvedException(final String message) {
        super(message);

    }

}
