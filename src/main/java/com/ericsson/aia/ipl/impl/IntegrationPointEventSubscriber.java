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

package com.ericsson.aia.ipl.impl;

import java.util.Properties;

import com.ericsson.aia.ipl.EventListener;
import com.ericsson.aia.ipl.model.Destination;
import com.ericsson.aia.ipl.model.IntegrationPoint;

/**
 * Concrete Implementation of a IntegrationPointEventSubscriber based on {@link AbstractIntegrationPointSubscriber}.
 *
 * @param <V> type of event for the event listener
 */
public class IntegrationPointEventSubscriber<V> extends AbstractIntegrationPointSubscriber<V> {

    /**
     * Instantiates a new IntegrationPoint based event subscriber.
     *
     * @param integrationPoint the IntegrationPoint to configure this subscriber.
     */
    public IntegrationPointEventSubscriber(final IntegrationPoint integrationPoint) {
       super(integrationPoint);
    }


    @Override
    public KafkaSubscriber<V> getKafkaSubscriberRunnable(final Properties commonSubscriberProperties, final Destination destination,
                                                      final EventListener<V> eventListener) {
        return new KafkaSubscriberWrapper<>(commonSubscriberProperties, destination,
            eventListener);
    }
}
