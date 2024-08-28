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

package com.ericsson.aia.ipl.impl;

import java.util.Properties;

import com.ericsson.aia.ipl.*;
import com.ericsson.aia.ipl.model.Destination;
import com.ericsson.aia.ipl.model.IntegrationPoint;

/**
 * Concrete implementation of IntegrationPointBatchSubscriber based on {@link AbstractIntegrationPointSubscriber}
 *
 * @param <V>
 *           type of event for the event listener
 */
public class IntegrationPointBatchSubscriber<V> extends AbstractIntegrationPointSubscriber<V> {

    private final long batchMs;

    /**
     * Instantiates a new IntegrationPoint based event subscriber.
     *
     * @param integrationPoint the IntegrationPoint to configure this subscriber.
     * @param batchMs          the batch interval between commits
     */
    public IntegrationPointBatchSubscriber(final IntegrationPoint integrationPoint, final long batchMs) {
        super(integrationPoint);
        this.batchMs = batchMs;
    }

    @Override
    public KafkaSubscriber<V> getKafkaSubscriberRunnable(final Properties commonSubscriberProperties, final Destination destination,
                                                      final EventListener<V> eventListener) {
        final EventCollectionListener<V> eventCollectionListener = (EventCollectionListener<V>) eventListener;
        return new KafkaBatchSubscriberWrapper<>(commonSubscriberProperties, destination, eventCollectionListener, batchMs);
    }
}
