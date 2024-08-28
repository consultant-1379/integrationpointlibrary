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

/**
 * Interface defining the event listening behavior. It is used to mark clients of the event subscribers, so they can receive the subscribed events to
 * further processing. Note: users of EventListener must be non-blocking.
 *
 * @param <V>
 *            the event.
 */
public interface EventListener<V> {

    /**
     * Method that will be called when there is an event to be consumed.
     *
     * @param event
     *            the event to be consumed.
     */
    void onEvent(V event);

}
