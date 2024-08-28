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

package com.ericsson.aia.ipl.test.util

import com.ericsson.aia.ipl.EventListener

/**
 * A simple implementation of an EventListener, for generic test purposes.
 *
 * @param <V> the type of the event.
 */
class SimpleEventListener<V> implements EventListener<V> {

    final List<V> events = new ArrayList<>()

    @Override
    void onEvent(final V event) {
        events.add(event)
    }

    List<V> getEvents() {
        Collections.unmodifiableList(events)
    }
}
