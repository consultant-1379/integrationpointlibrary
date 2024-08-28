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

package com.ericsson.aia.ipl.util;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;

/**
 * An instance of this class can be passed when subscribing to a topic
 * to know when a rebalance has happened during a poll
 */
public class RebalanceListener implements ConsumerRebalanceListener {

    private final AtomicBoolean batchCancelled = new AtomicBoolean();

    @Override
    public void onPartitionsRevoked(final Collection<TopicPartition> partitions) {
        batchCancelled.set(true);
    }

    @Override
    public void onPartitionsAssigned(final Collection<TopicPartition> partitions) {
    }

    public boolean isBatchCancelledAndReset() {
        return batchCancelled.getAndSet(false);
    }
}
