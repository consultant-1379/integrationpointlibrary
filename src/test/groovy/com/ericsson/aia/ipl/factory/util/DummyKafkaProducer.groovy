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

package com.ericsson.aia.ipl.factory.util;

import java.util.*
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

/**
 * Fake Kafka producer for use in tests
 */
public class DummyKafkaProducer<K, V> extends KafkaProducer<K, V> {

    private static final Map<String, Object> CONFIGS = new HashMap<>()
    private final List<DummyKafkaConsumer> kafkaConsumers = new ArrayList<>()

    static {
        CONFIGS.put("bootstrap.servers", "localhost:9092")
        CONFIGS.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        CONFIGS.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    }

    public DummyKafkaProducer(final List<DummyKafkaConsumer<K, V>> kafkaConsumers) {
        super(CONFIGS)
        this.kafkaConsumers = kafkaConsumers
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K, V> record) {
        for (DummyKafkaConsumer kafkaConsumer : kafkaConsumers) {
            if (kafkaConsumer.subscribedTopics.contains(record.topic())) {
                kafkaConsumer.addRecord(record)
            }
        }
    }
}
