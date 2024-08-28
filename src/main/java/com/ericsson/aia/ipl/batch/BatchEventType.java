/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.aia.ipl.batch;

/**
 * This enum defines the types of @BatchEvent that can be sent
 */
public enum BatchEventType {

    /**
     * event type indicating the start of a {@link BatchEvent}
     */
    START_BATCH,

    /**
     * event type indicating the end of a {@link BatchEvent}
     */
    END_BATCH,

    /**
     * event type indicating that a batch has been cancelled {@link BatchEvent}
     */
    CANCEL_BATCH
}
