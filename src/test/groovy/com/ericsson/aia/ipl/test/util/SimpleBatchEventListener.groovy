/*
 * ------------------------------------------------------------------------------
 *  *******************************************************************************
 *  * COPYRIGHT Ericsson 2017
 *  *
 *  * The copyright to the computer program(s) herein is the property of
 *  * Ericsson Inc. The programs may be used and/or copied only with written
 *  * permission from Ericsson Inc. or in accordance with the terms and
 *  * conditions stipulated in the agreement/contract under which the
 *  * program(s) have been supplied.
 *  *******************************************************************************
 *  *----------------------------------------------------------------------------
 */

package com.ericsson.aia.ipl.test.util

import com.ericsson.aia.ipl.EventCollectionListener
import com.ericsson.aia.ipl.batch.BatchEvent
import com.ericsson.aia.ipl.batch.RecordWrapper

/**
 * A simple implementation of an EventListener, for generic test purposes.
 *
 * @param <V> the type of the event.
 */
class SimpleBatchEventListener<V> implements EventCollectionListener<V> {

    final List<V> events = new ArrayList<>()
    final List<BatchEvent> batchEvents = new ArrayList<>()

    @Override
    void onEvent(final V event) {
        events.add(event)
    }

    List<V> getEvents() {
        Collections.unmodifiableList(events)
    }

    @Override
    void onEvent(final RecordWrapper<V> event) {
        events.add(event.getRecord())
    }

    @Override
    void batchEvent(final BatchEvent batchEvent) {
        batchEvents.add(batchEvent)
    }
}
