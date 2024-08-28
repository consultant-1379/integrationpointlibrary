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

package com.ericsson.aia.ipl;

import java.io.Closeable;

/**
 * Interface defining the behavior of event publishers based on the
 * IntegrationPoint configuration.
 *
 * @param <V>
 *            event type.
 */
public interface EventPublisher<V> extends Closeable {

    /**
     * Sends (publishes) record.
     *
     * @param eventId
     *            the event id.
     * @param event
     *            the event itself.
     */
    void sendRecord(String eventId, V event);

    /**
     * Sends (publishes) record to specified partition of the topic.
     *
     * @param eventId
     *            the event id.
     * @param event
     *            the event itself.
     * @param partitionNumber
     *            the specified partition of the topic to send event.
     */
    void sendRecord(String eventId, V event, Integer partitionNumber);

    /**
     * Checks if this producer is allowed to publish this event to a specified
     * topic.
     *
     * @param eventId
     *            the event id.
     * @return true if event can be sent.
     */
    boolean canEventBePublished(final String eventId);

}
