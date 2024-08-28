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

import static com.ericsson.aia.ipl.util.Utils.getPropertiesAsString;
import static com.ericsson.aia.ipl.util.ValidationUtils.validateIntegrationPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.EventPublisher;
import com.ericsson.aia.ipl.model.Destination;
import com.ericsson.aia.ipl.model.IntegrationPoint;

/**
 * Implementation of a IntegrationPoint based event publisher.
 *
 * @param <V>
 *            event type.
 */
public class IntegrationPointEventPublisher<V> implements EventPublisher<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationPointEventPublisher.class);
    private final List<KafkaPublisherWrapper<V>> publishers;

    /**
     * Instantiates a new IntegrationPoint based event publisher.
     *
     * @param integrationPoint
     *            the IntegrationPoint to configure this publisher.
     */
    public IntegrationPointEventPublisher(final IntegrationPoint integrationPoint) {
        validateIntegrationPoint(integrationPoint);
        this.publishers = new ArrayList<KafkaPublisherWrapper<V>>();
        final Properties commonPublisherProperties = integrationPoint.getProperties();
        initializePublishers(commonPublisherProperties, integrationPoint.getDestinations());
        LOGGER.info("New IntegrationPoint based event publisher created.");
    }

    private void initializePublishers(final Properties properties, final Destination[] destinations) {
        for (final Destination destination : destinations) {
            publishers.add(new KafkaPublisherWrapper<V>(properties, destination));
            LOGGER.info("KafkaPublisher created for topic '{}'.", destination);
            LOGGER.debug(getPropertiesAsString(properties));
        }
    }

    /**
     * Sends a record.
     *
     * @param eventId
     *            the event id.
     * @param event
     *            the event itself.
     */
    @Override
    public void sendRecord(final String eventId, final V event) {
        sendEvent(eventId, event, null);
    }

    /**
     * Sends a record.
     *
     * @param eventId
     *            the event id.
     * @param event
     *            the event itself.
     * @param partitionNumber
     *            the partition number.
     */
    @Override
    public void sendRecord(final String eventId, final V event, final Integer partitionNumber) {
        sendEvent(eventId, event, partitionNumber);
    }

    private void sendEvent(final String eventId, final V event, final Integer partitionNumber) {
        for (final KafkaPublisherWrapper<V> producer : publishers) {
            if (producer.canEventBeSent(eventId)) {
                producer.sendMessage(new ProducerRecord<String, V>(producer.getDestination().getName(), partitionNumber, eventId, event));
            }
        }
    }

    @Override
    public boolean canEventBePublished(final String eventId) {
        boolean canEventBePublished = false;

        for (final KafkaPublisherWrapper<V> producer : publishers) {
            if (producer.canEventBeSent(eventId)) {
                canEventBePublished = true;
                break;
            }
        }
        return canEventBePublished;
    }

    @Override
    public void close() {
        LOGGER.info("Closing all publishers ...");
        for (final KafkaPublisherWrapper<V> publisherWrapper : publishers) {
            publisherWrapper.close();
        }
    }
}
