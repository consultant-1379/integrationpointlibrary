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

package com.ericsson.aia.ipl.impl;

import java.util.*;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import com.ericsson.aia.ipl.BatchCommitter;

/**
 * This class is sent along with END_BATCH events so that the batch can be committed by the other handlers
 * @param <V> The data type coming from the Kafka bus
 */
public class KafkaBatchCommitter<V> implements BatchCommitter {

    private final KafkaBatchSubscriberWrapper<V> kafkaBatchSubscriberWrapper;
    private final Map<TopicPartition, OffsetAndMetadata> batchEndOffsets;
    /**
     * Instantiates a batch committer for Kafka batches. Takes the Kafka subscriber and the offsets to be committed as parameters
     * @param kafkaBatchSubscriberWrapper The kafka subscriber wrapper that was used for the batch. Passed in for callbacks
     * @param batchEndOffsets The end offsets of the batch
     */
    KafkaBatchCommitter(final KafkaBatchSubscriberWrapper<V> kafkaBatchSubscriberWrapper,
                        final Map<TopicPartition, OffsetAndMetadata> batchEndOffsets) {
        this.kafkaBatchSubscriberWrapper = kafkaBatchSubscriberWrapper;
        this.batchEndOffsets = batchEndOffsets;
    }

    /**
     * Commit the batchEndOffsets for this batch to Kafka
     */
    @Override
    public void commitBatchAsync() {
        this.kafkaBatchSubscriberWrapper.addOffsetsToCommit(batchEndOffsets);
    }
}
