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
 * Interface defining the behavior of event subscribers based on the IntegrationPoint configuration.
 *
 * @param <V>
 *            type of event for the event listener.
 */
public interface EventSubscriber<V> extends Closeable {

    /**
     * Registers event listener to receive subscribed events.
     *
     * @param eventListener
     *            the event listener to be registered.
     */
    void registerEventListener(EventListener<V> eventListener);

}
