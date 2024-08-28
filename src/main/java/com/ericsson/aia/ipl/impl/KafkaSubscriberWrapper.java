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

import static com.ericsson.aia.ipl.util.Utils.toProperties;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.EventListener;
import com.ericsson.aia.ipl.model.Destination;
import com.ericsson.aia.ipl.util.ServiceProviderInstance;

/**
 * Wrapper around KafkaSubscriber uses the AIA Kafka API (com.ericsson.component.aia.common.transport) to subscribe to events from Kafka.
 *
 * @param <V>
 *            event type.
 */
class KafkaSubscriberWrapper<V> implements KafkaSubscriber<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSubscriberWrapper.class);
    private static final long TIME_OUT_IN_MILLISECONDS = 100L;

    private final KafkaConsumer<String, V> kafkaSubscriber;
    private final Destination destination;
    private final EventListener<V> eventListener;

    /**
     * Instantiates a new KafkaSubscriber wrapper.
     *
     * @param properties
     *            the IntegrationPoint properties for the consumer.
     * @param destination
     *            the topic this KafkaSubscriber will connect to.
     * @param eventListener
     *            the EventListener that will receive the consumed events.
     */
    KafkaSubscriberWrapper(final Properties properties, final Destination destination, final EventListener<V> eventListener) {
        properties.putAll(toProperties(destination.getProperties()));
        this.kafkaSubscriber = ServiceProviderInstance.getInstance().getKafkaConsumer(properties);
        this.kafkaSubscriber.subscribe(Arrays.asList(destination.getName()));
        this.destination = destination;
        this.eventListener = eventListener;
        LOGGER.debug("New KafkaSubscriber wrapper created for topic '{}'.", destination.getName());
    }

    /**
     * Start the KafkaSubscriber.
     */
    @Override
    public void run() {
        LOGGER.debug("KafkaSubscriber wrapper for topic '{}' commanded to start.", destination.getName());
        ConsumerRecords<String, V> records;
        while (true) {
            try {
                records = kafkaSubscriber.poll(TIME_OUT_IN_MILLISECONDS);
                process(records);
            } catch (final Exception exception) {
                LOGGER.error("KafkaSubscriver wrapper for topic '{}' provoked an exception. Logging the exception and keeping running.",
                        destination.getName(), exception);
            }
        }
    }

    private void process(final ConsumerRecords<String, V> records) {
        for (final ConsumerRecord<String, V> consumerRecord : records) {
            send(consumerRecord);
        }
    }

    private void send(final ConsumerRecord<String, V> consumerRecord) {
        if (destination.isEventWhitelisted(consumerRecord.key())) {
            eventListener.onEvent(consumerRecord.value());
        }
    }

    /**
     * Closes the KafkaSubscriber and stops its processing.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void close() throws IOException {
        LOGGER.info("Closing KafkaSubscriber wrapper for topic {}", destination.getName());
        if (kafkaSubscriber != null) {
            kafkaSubscriber.close();
        }
    }

}
