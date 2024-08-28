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

import java.io.Closeable;
import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.model.Destination;
import com.ericsson.aia.ipl.util.ServiceProviderInstance;

/**
 * The Class KafkaPublisherWrapper uses the AIA Kafka API (com.ericsson.component.aia.common.transport) to publish events to Kafka.
 *
 * @param <V> the value type
 */
class KafkaPublisherWrapper<V> implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublisherWrapper.class);
    private static final long PUBLISHER_CLOSE_TIMEOUT_IN_SEC = 10l;
    private final KafkaProducer<String, V> producer;
    private final Destination destination;
    /**
     * Instantiates a new kafka producer wrapper.
     *
     * @param properties  the properties
     * @param destination the destination
     */
    KafkaPublisherWrapper(final Properties properties, final Destination destination) {
        properties.putAll(toProperties(destination.getProperties()));
        producer = ServiceProviderInstance.getInstance().getKafkaProducer(properties);
        this.destination = destination;
        LOGGER.debug("New KafkaPublisher created for topic '{}'.", destination.getName());

    }

    /**
     * Checks if this producer is allowed to send this event. Note: if no event was specified in the destination configuration, all events are
     * allowed.
     *
     * @param eventId the event id.
     * @return true, if the event can be sent by the producer.
     */
    public boolean canEventBeSent(final String eventId) {
        return destination.isEventWhitelisted(eventId);
    }

    /**
     * Send message.
     *
     * @param producerRecord to be sent
     */
    public void sendMessage(final ProducerRecord<String, V> producerRecord) {
        producer.send(producerRecord);
    }

    /**
     * Gets the destination (topic) associated to this KafkaPublisher wrapper.
     *
     * @return the destination.
     */
    public Destination getDestination() {
        return destination;
    }

    @Override
    public void close() {
        LOGGER.info("Closing KafkaPublisher for topic {}, will wait max {} seconds for producer to complete any pending requests ... ",
            destination.getName(), PUBLISHER_CLOSE_TIMEOUT_IN_SEC);
        producer.flush();
        producer.close(Duration.ofSeconds(PUBLISHER_CLOSE_TIMEOUT_IN_SEC));
    }
}
