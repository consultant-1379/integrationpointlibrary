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

package com.ericsson.aia.ipl;

import com.ericsson.aia.ipl.batch.BatchEvent;
import com.ericsson.aia.ipl.batch.RecordWrapper;

/**
 * Interface defining the event listening behavior.
 * Interface responsible for batching events.
 * It is used to mark clients of the event subscribers, so they can receive the subscribed events for further processing.
 * Note: users of EventListener must be non-blocking.
 * @param <V>
 *            the event.
 */
public interface EventCollectionListener<V> extends EventListener<V> {

    /**
     * Send an event along with meta data
     * @param record the record being sent
     */
    void onEvent(final RecordWrapper<V> record);

    /**
     * Method that indicates the start or end of a batch and can send additional attributes
     * @param batchEvent the signals for batch start and end along with additional attributes
     */
    void batchEvent(final BatchEvent batchEvent);

}
