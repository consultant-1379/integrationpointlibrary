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

import java.util.Map;

/**
 * This class signals to clients the start and end points of a batch.
 * Offsets are committed if the end batch call succeeds without an exception
 * Attributes can be included for metadata
 */
public class BatchEvent {

    private final Map<String, Object> attributes;
    private final BatchEventType batchEventType;

    /**
     * This class signals to clients the start and end points of a batch.
     * Offsets are committed if the end batch call succeeds without an exception
     * Attributes can be included for metadata
     * @param batchEventType the type of batch signal, start or end
     * @param attributes additional attributes to go with the batch signal
     */
    public BatchEvent(final BatchEventType batchEventType, final Map<String, Object> attributes) {
        this.batchEventType = batchEventType;
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public BatchEventType getBatchEventType() {
        return batchEventType;
    }

    @Override
    public String toString() {
        return "BatchEvent [attributes=" + attributes + ", batchEventType=" + batchEventType + "]";
    }
}
