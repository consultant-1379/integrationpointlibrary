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

import static com.ericsson.aia.ipl.util.Utils.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.EventCollectionListener;
import com.ericsson.aia.ipl.batch.*;
import com.ericsson.aia.ipl.batch.util.BatchConstants;
import com.ericsson.aia.ipl.model.Destination;
import com.ericsson.aia.ipl.util.*;

/**
 * Wrapper around KafkaSubscriber uses the AIA Kafka API (com.ericsson.component.aia.common.transport) to subscribe to events from Kafka.
 *
 * @param <V> event type.
 */
public class KafkaBatchSubscriberWrapper<V> implements KafkaSubscriber<V> {

    private static final long WAIT_AFTER_FAILED_BATCH_MS = 2000L;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaBatchSubscriberWrapper.class);
    private static final long TIME_OUT = 100L;
    private static final String SUPPORTED_EVENTS = "supportedEvents";

    private final KafkaConsumer<String, V> kafkaSubscriber;
    private final Destination destination;
    private final EventCollectionListener<V> eventListener;
    private final long batchMs;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final RebalanceListener rebalanceListener = new RebalanceListener();

    private final Set<Map<TopicPartition, OffsetAndMetadata>> offsetsToCommit = Collections
            .newSetFromMap(new ConcurrentHashMap<Map<TopicPartition, OffsetAndMetadata>, Boolean>());
    private final OffsetCommitCallback offsetCommitCallback = new OffsetCommitCallback() {
        @Override public void onComplete(final Map<TopicPartition, OffsetAndMetadata> offsets, final Exception exception) {
            if (exception == null) {
                LOGGER.info("Commit finished for offsets {}", offsets);
            } else {
                LOGGER.error("Commit failed for offsets {}", offsets);
            }
        }
    };

    /**
     * Instantiates a new KafkaSubscriber wrapper.
     *
     * @param properties    the IntegrationPoint properties for the consumer.
     * @param destination   the topic this KafkaSubscriber will connect to.
     * @param eventListener the EventListener that will receive the consumed events.
     * @param batchMs       the batch interval between commits
     */
    public KafkaBatchSubscriberWrapper(final Properties properties, final Destination destination, final EventCollectionListener<V> eventListener,
                                       final long batchMs) {
        properties.putAll(toProperties(destination.getProperties()));
        addSupportedEvents(properties, destination.getEvents());
        this.kafkaSubscriber = ServiceProviderInstance.getInstance().getKafkaConsumer(properties);
        this.kafkaSubscriber.subscribe(Collections.singletonList(destination.getName()), rebalanceListener);
        this.destination = destination;
        this.eventListener = eventListener;
        this.batchMs = batchMs;
        LOGGER.debug("New KafkaSubscriber wrapper created for topic '{}'.", destination.getName());
    }

    private void addSupportedEvents(final Properties properties, final List<String> events) {
        if (events != null && !events.isEmpty()) {
            final List<Integer> supportedEvents = new ArrayList<>();
            for (final String event : events) {
                supportedEvents.add(Integer.parseInt(event));
            }
            properties.put(SUPPORTED_EVENTS, supportedEvents);
        }
    }

    /**
     * Start the KafkaSubscriber.
     */
    @Override
    public void run() {
        LOGGER.debug("KafkaSubscriber wrapper for topic '{}' commanded to start.", destination.getName());
        while (running.get()) {
            processBatch();
        }
    }

    private void processBatch() {
        try {
            final long endBatchTime = getEndBatchTime();
            LOGGER.debug("endBatchTime:{}", endBatchTime);
            final ConsumerRecords<String, V> records = pollToCheckPartitionAssignment();
            final Set<TopicPartition> assignment = this.kafkaSubscriber.assignment();
            final Offsets offsets = processBatch(endBatchTime, records, assignment);
            endBatch(assignment, offsets);
        } catch (final Exception exception) {
            LOGGER.error("An exception occurred while polling for records. Continuing", exception);
        }

    }

    private Offsets processBatch(final long endBatchTime, final ConsumerRecords<String, V> records, final Set<TopicPartition> assignment) {
        if (startBatch(assignment)) {
            LOGGER.debug("Start batch succeeded for topic {} with assignment {}", this.destination.getName(), assignment);
            return continueBatch(records, endBatchTime);
        } else {
            LOGGER.debug("Start batch failed for topic {} with assignment {}", this.destination.getName(), assignment);
            waitMillis(WAIT_AFTER_FAILED_BATCH_MS);
            return Offsets.NULL_OFFSETS;
        }
    }

    private ConsumerRecords<String, V> pollToCheckPartitionAssignment() {
        final ConsumerRecords<String, V> records = kafkaSubscriber.poll(TIME_OUT);
        LOGGER.debug("Got {} records for topic {}", records.count(), destination.getName());
        return records;
    }

    private boolean startBatch(final Collection<TopicPartition> topicPartitions) {
        try {
            if (!topicPartitions.isEmpty()) {
                eventListener.batchEvent(new BatchEvent(BatchEventType.START_BATCH, getStartAttributes(topicPartitions)));
                LOGGER.debug("Returning from start batch");
                return true;
            }
        } catch (final Exception exception) {
            LOGGER.error("An exception occurred during start batch", exception);
        }
        return false;
    }

    private void waitMillis(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException exception) {
            LOGGER.warn("Thread was interrupted", exception);
        }
    }

    private Offsets continueBatch(final ConsumerRecords<String, V> firstRecords, final long endBatchTime) {
        Offsets offsets = sendFirstRecords(firstRecords);
        while (isWithinTimeout(endBatchTime)) {
            try {
                rebalanceListener.isBatchCancelledAndReset();
                final ConsumerRecords<String, V> records = kafkaSubscriber.poll(TIME_OUT);
                if (rebalanceListener.isBatchCancelledAndReset()) {
                    cancelBatch();
                    LOGGER.debug("Batch is cancelled");
                    return Offsets.NULL_OFFSETS;
                } else {
                    offsets = sendRecords(offsets, records);
                    commitIfNecessary();
                }
            } catch (final Exception exception) {
                LOGGER.error("kafkaSubscriber wrapper for topic '{}' provoked an exception. Logging the exception and continuing.",
                        destination.getName(), exception);
            }
            LOGGER.debug("Partition offsets:{} Assignment:{}", offsets.getEndOffsets(), kafkaSubscriber.assignment());
        }
        return offsets;
    }

    private Offsets sendFirstRecords(final ConsumerRecords<String, V> firstRecords) {
        final Offsets offsets = new Offsets();
        sendRecords(offsets, firstRecords);
        return offsets;
    }

    private boolean isWithinTimeout(final long endBatchTime) {
        return ServiceProviderInstance.getInstance().getTime() < endBatchTime;
    }

    private Offsets sendRecords(final Offsets offsets, final ConsumerRecords<String, V> records) {
        Offsets updatedOffsets = offsets;
        final Set<TopicPartition> partitions = records.partitions();
        for (final TopicPartition topicPartition : partitions) {
            final List<ConsumerRecord<String, V>> consumerRecords = records.records(topicPartition);
            for (final ConsumerRecord<String, V> consumerRecord : consumerRecords) {
                send(consumerRecord);
            }
            updatedOffsets = updateOffsets(updatedOffsets, topicPartition, consumerRecords);
        }
        return updatedOffsets;
    }

    private Offsets updateOffsets(final Offsets offsets, final TopicPartition topicPartition, final List<ConsumerRecord<String, V>> consumerRecords) {
        if (!consumerRecords.isEmpty()) {
            offsets.setStartOffset(topicPartition, consumerRecords.get(0).offset());
            offsets.setEndOffset(topicPartition, consumerRecords.get(consumerRecords.size() - 1).offset());
        }
        return offsets;
    }

    private void commitIfNecessary() {
        try {
            if (!offsetsToCommit.isEmpty()) {
                final Iterator<Map<TopicPartition, OffsetAndMetadata>> iterator = offsetsToCommit.iterator();
                while (iterator.hasNext()) {
                    final Map<TopicPartition, OffsetAndMetadata> offsets = iterator.next();
                    iterator.remove();
                    if (!offsets.isEmpty()) {
                        this.kafkaSubscriber.commitAsync(offsets, offsetCommitCallback);
                    }
                }
            }
        } catch (final Exception exception) {
            LOGGER.error("Exception while trying to commit offsets", exception);
        }
    }

    /**
     * When the BatchCommitter is finished committing its batch it adds the end offsets to the set offsetsToCommit.
     * That allows the thread running this class to do the commit because the Kafka subscriber is not thread safe
     *
     * @param offsets the end offsets to commit
     */
    void addOffsetsToCommit(final Map<TopicPartition, OffsetAndMetadata> offsets) {
        this.offsetsToCommit.add(offsets);
    }

    private long getEndBatchTime() {
        final long now = ServiceProviderInstance.getInstance().getTime();
        return now + this.batchMs - (now % this.batchMs);
    }

    private void send(final ConsumerRecord<String, V> consumerRecord) {
        if (destination.isEventWhitelisted(consumerRecord.key()) && consumerRecord.value() != null) {
            eventListener.onEvent(new RecordWrapper<>(consumerRecord.value(), consumerRecord.key(), consumerRecord.topic()));
        }
    }

    private void endBatch(final Set<TopicPartition> assignment, final Offsets offsets) {
        try {
            if (!assignment.isEmpty() && !offsets.isObjectNull()) {
                LOGGER.debug("Ending batch");
                eventListener.batchEvent(new BatchEvent(BatchEventType.END_BATCH, getEndAttributes(offsets)));
                LOGGER.debug("Ended batch");
            }
        } catch (final Exception exception) {
            LOGGER.error("An exception was thrown during end batch.", exception);
        }
    }

    private void cancelBatch() {
        try {
            LOGGER.debug("Cancelling batch");
            eventListener.batchEvent(new BatchEvent(BatchEventType.CANCEL_BATCH, getCancelAttributes()));
            LOGGER.debug("Cancelled batch");
        } catch (final Exception exception) {
            LOGGER.error("An exception was thrown during cancel batch.", exception);
        }
    }

    private Map<String, Object> getStartAttributes(final Collection<TopicPartition> topicPartitions) {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(BatchConstants.TOPIC, this.destination.getName());
        attributes.put(BatchConstants.NUMBER_OF_PARTITIONS, getNumberOfPartitionsForTopic());
        attributes.put(BatchConstants.ASSIGNED_PARTITIONS, getAssignedPartitions(topicPartitions));
        return attributes;
    }

    private Integer getNumberOfPartitionsForTopic() {
        return this.kafkaSubscriber.partitionsFor(this.destination.getName()).size();
    }

    private Set<Integer> getAssignedPartitions(final Collection<TopicPartition> topicPartitions) {
        final Set<Integer> assignedPartitions = new HashSet<>();
        for (final TopicPartition topicPartition : topicPartitions) {
            assignedPartitions.add(topicPartition.partition());
        }
        return assignedPartitions;
    }

    private Map<String, Object> getEndAttributes(final Offsets offsets) {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(BatchConstants.COMMITTER, new KafkaBatchCommitter<>(this, offsets.getEndOffsets()));
        attributes.put(BatchConstants.TOPIC, this.destination.getName());
        attributes.put(BatchConstants.OFFSETS, offsets);
        return attributes;
    }

    private Map<String, Object> getCancelAttributes() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(BatchConstants.TOPIC, this.destination.getName());
        return attributes;
    }

    /**
     * Closes the kafkaSubscriber and stops its processing.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void close() throws IOException {
        LOGGER.info("Closing batch KafkaSubscriber wrapper for topic {}", destination.getName());
        running.set(false);
        if (kafkaSubscriber != null) {
            kafkaSubscriber.unsubscribe();
            kafkaSubscriber.close();
        }
    }
}
