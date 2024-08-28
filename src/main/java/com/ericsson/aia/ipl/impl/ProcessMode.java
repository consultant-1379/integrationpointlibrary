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

import com.ericsson.aia.ipl.EventSubscriber;
import com.ericsson.aia.ipl.model.IntegrationPoint;

/**
 * How to process events
 */
public enum ProcessMode {

    /**
     * process events in batch mode - offset commits are done at end of batch
     *
     */
    BATCH {
        @Override
        public <V> EventSubscriber<V> getEventSubscriber(final IntegrationPoint integrationPoint,
                                                         final long batchMs) {
            return new IntegrationPointBatchSubscriber<>(integrationPoint, batchMs);
        }
    },

    /**
     * process events one at a time - offset commits are done at regular interval
     *
     */
    CONTINUOUS {
        @Override
        public <V> EventSubscriber<V> getEventSubscriber(final IntegrationPoint integrationPoint,
                                                         final long batchMs) {
            return new IntegrationPointEventSubscriber<>(integrationPoint);
        }
    };

    /**
     * abstract method to instantiate the correct integration point subscriber for the process mode
     * @param <V>
     *             type of event for the event listener
     * @param integrationPoint
     *            integrationPoint
     * @param batchMs
     *            the batch interval between commits
     * @return the IntegrationPointEventSubscriber
     */
    public abstract <V> EventSubscriber<V> getEventSubscriber(final IntegrationPoint integrationPoint,
                                                              final long batchMs);
}
