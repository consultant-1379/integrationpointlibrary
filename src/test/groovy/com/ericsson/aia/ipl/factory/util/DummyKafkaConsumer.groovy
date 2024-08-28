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

package com.ericsson.aia.ipl.factory.util

import java.util.Map.Entry
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

import org.apache.kafka.clients.consumer.*
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.PartitionInfo
import org.apache.kafka.common.TopicPartition

/**
 * The class DummyKafkaConsumer;
 * provides a configurable kafka consumer instance in test
 */
class DummyKafkaConsumer<K, V> extends KafkaConsumer<K, V> {

    private static final Map<String, Object> CONFIGS = new HashMap<>()
    private final List<ConsumerRecords<String, V>> consumerRecords = new CopyOnWriteArrayList<>()
    private final ConsumerRecords<String, V> emptyRecord = new ConsumerRecords<>(new HashMap<TopicPartition, List<ConsumerRecord>>())
    private final AtomicBoolean paused = new AtomicBoolean()
    private final List<Map<TopicPartition, OffsetAndMetadata>> committedOffsets = new ArrayList<>()
    private final Set<TopicPartition> assignment
    private final List<PartitionInfo> partitionsFor
    private List<ConsumerRebalanceListener> consumerRebalanceListeners = new ArrayList<>()
    private List<Boolean> revokePartitions = new ArrayList<>()

    private int callsToCommit = 0

    private final AtomicLong offset = new AtomicLong()
    private final Set<String> subscribedTopics = new HashSet<>()

    static {
        CONFIGS.put("bootstrap.servers", "localhost:9092")
        CONFIGS.put("group.id", "myGroupId")
        CONFIGS.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        CONFIGS.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        CONFIGS.put("auto.create.topics.enable", "true")
    }

    public DummyKafkaConsumer(final List<Map<TopicPartition, List<ConsumerRecord<String, V>>>> recordList) {
        super(CONFIGS)
        for (final Map<TopicPartition, List<ConsumerRecord<String, V>>> records : recordList) {
            consumerRecords.add(new ConsumerRecords<>(records))
        }
        partitionsFor = getPartitions(recordList)
        assignment = getAssignment(recordList)
    }

    private List<PartitionInfo> getPartitions(final List<Map<TopicPartition, List<ConsumerRecord<String, V>>>> recordList) {
        final List<PartitionInfo> partitionInfos = new ArrayList<>()
        final Set<TopicPartition> topicPartitions = new HashSet<>()
        for (final Map<TopicPartition, List<ConsumerRecord<String, V>>> records : recordList) {
            for (final TopicPartition topicPartition : records.keySet()) {
                if (!topicPartitions.contains(topicPartition)) {
                    topicPartitions.add(topicPartition)
                    partitionInfos.add(new PartitionInfo(topicPartition.topic(), topicPartition.partition(), null, null, null))
                }
            }
        }
        return partitionInfos
    }

    private Set<TopicPartition> getAssignment(final List<Map<TopicPartition, List<ConsumerRecord<String, V>>>> recordList) {
        final Set<TopicPartition> assignment = new HashSet<>()
        for (final Map<TopicPartition, List<ConsumerRecord<String, V>>> records : recordList) {
            assignment.addAll(records.keySet())
        }
        return assignment
    }

    @Override
    public ConsumerRecords<String, V> poll(final long timeout) {
        if (shouldRevoke()) {
            for (ConsumerRebalanceListener listener : consumerRebalanceListeners) {
                listener.onPartitionsRevoked(assignment)
                listener.onPartitionsAssigned(assignment)
            }
        }
        try {
            return consumerRecords.remove(0)
        } catch (final IndexOutOfBoundsException exception) {
        }
        Thread.sleep(timeout)
        return emptyRecord
    }

    private boolean shouldRevoke() {
        if (revokePartitions.size() > 1) {
            return revokePartitions.remove(0)
        } else if (revokePartitions.size() == 1) {
            return  revokePartitions.get(0)
        }
        return false
    }

    public void addRecord(final ProducerRecord<K, V> record) {
        def partition = (record.partition() == null) ? 1 : record.partition()
        def consumerRecord = new ConsumerRecord<K, V>(record.topic(), partition, offset.getAndIncrement(), record.key(), record.value())
        Map<TopicPartition, List<ConsumerRecord<K, V>>> recordMap = new HashMap<>()
        recordMap.put(new TopicPartition(record.topic(), partition), Arrays.asList(consumerRecord))
        ConsumerRecords<K, V> records = new ConsumerRecords<>(recordMap)
        consumerRecords.add(records)
    }

    @Override
    public void subscribe(final Collection<String> topics) {
        if (topics != null) {
            subscribedTopics.addAll(topics)
        }
    }

    @Override
    public void subscribe(final Collection<String> topics, ConsumerRebalanceListener listener) {
        this.consumerRebalanceListeners.add(listener)
        if (topics != null) {
            subscribedTopics.addAll(topics)
        }
    }

    public Set<TopicPartition> assignment() {
        return assignment
    }

    @Override
    public void commitSync() {
        callsToCommit++
    }

    @Override
    public void commitSync(final Map<TopicPartition, OffsetAndMetadata> offsets) {
        callsToCommit++
        committedOffsets.add(offsets)
    }

    @Override
    public void commitAsync(final Map<TopicPartition, OffsetAndMetadata> offsets, OffsetCommitCallback callback) {
        callsToCommit++
        committedOffsets.add(offsets)
    }

    @Override
    public List<PartitionInfo> partitionsFor(String topic) {
        return partitionsFor
    }

    @Override
    public void close() {
    }

    public void addRecords(final List<Map<TopicPartition, List<ConsumerRecord<String, V>>>> recordList) {
        for (final Map<TopicPartition, List<ConsumerRecord<String, V>>> records : recordList) {
            consumerRecords.add(new ConsumerRecords<>(records))
        }
    }
}
