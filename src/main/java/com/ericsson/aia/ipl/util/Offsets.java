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

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

/**
 * This class holds the start and end offsets for a batch
 */
public class Offsets {

    /**
     * Creates a null Offsets object that overwrites the isObjectNull() to return true
     */
    public static final Offsets NULL_OFFSETS = new Offsets(){
        @Override
        public boolean isObjectNull() {
            return true;
        }
    };
    private final Map<TopicPartition, OffsetAndMetadata> startOffsets = new HashMap<>();
    private final Map<TopicPartition, OffsetAndMetadata> endOffsets = new HashMap<>();

    public Map<TopicPartition, OffsetAndMetadata> getStartOffsets() {
        return startOffsets;
    }

    public Map<TopicPartition, OffsetAndMetadata> getEndOffsets() {
        return endOffsets;
    }


    /**
     * Check to see if Offset object is null
     * @return  false
     *                      - the offsets object is not null
     *
     */
    public boolean isObjectNull() {
        return false;
    }

    /**
     * Sets the start offset for a topic and partition. Can only be set once for a topic/partition. Subsequent calls are ignored.
     * @param topicPartition
     *                      - the topic and partition to which the offset applies
     * @param offset
     *                      - the offset to set
     */
    public void setStartOffset(final TopicPartition topicPartition, final long offset) {
        if (!startOffsets.containsKey(topicPartition)) {
            startOffsets.put(topicPartition, new OffsetAndMetadata(offset));
        }
    }

    /**
     * Sets the end offset for a topic and partition. Can be set multiple times
     * @param topicPartition
     *                      - the topic and partition to which the offset applies
     * @param offset
     *                      - the offset to set
     */
    public void setEndOffset(final TopicPartition topicPartition, final long offset) {
        endOffsets.put(topicPartition, new OffsetAndMetadata(offset));
    }

    @Override
    public String toString() {
        return "Offsets{startOffsets=" + startOffsets + ", endOffsets=" + endOffsets + '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof  Offsets) {
            final Offsets other = (Offsets) obj;
            return startOffsets.equals(other.startOffsets) && endOffsets.equals(other.endOffsets);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = startOffsets != null ? startOffsets.hashCode() : 0;
        result = 31 * result + (endOffsets != null ? endOffsets.hashCode() : 0);
        return result;
    }
}
