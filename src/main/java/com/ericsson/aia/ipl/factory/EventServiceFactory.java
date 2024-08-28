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

package com.ericsson.aia.ipl.factory;

import static com.ericsson.aia.ipl.model.Constants.*;
import static com.ericsson.aia.ipl.util.Utils.*;
import static com.ericsson.aia.ipl.util.ValidationUtils.*;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.EventPublisher;
import com.ericsson.aia.ipl.EventSubscriber;
import com.ericsson.aia.ipl.impl.ProcessMode;
import com.ericsson.aia.ipl.impl.IntegrationPointEventPublisher;
import com.ericsson.aia.ipl.model.IntegrationPoint;

/**
 * Factory responsible for creating EventPublisher and EventSubscriber types.
 *
 * @param <V>
 *            Event itself.
 */
public class EventServiceFactory<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceFactory.class);

    private final Map<String, EventPublisher<V>> eventPublisherLocalCache = new HashMap<>();
    private final Map<String, EventSubscriber<V>> eventSubscriberLocalCache = new HashMap<>();
    private final IntegrationPointFactory integrationPointFactory;
    private final ProcessMode processMode;
    private final long batchIntervalInMilliseconds;

    /**
     * Instantiates a new event-service factory for batch processing
     *
     * @param uri
     *            the URI where the IntegrationPoint definitions can be found.
     * @param processMode
     *            how the events should be processed, single or batch
     * @param batchIntervalInMilliseconds
     *            the batch interval between commits
     * @param pathForFileContainingKafkaBrokerAddresses
     *            the path of the file that contains the Kafka brokers addresses.
     */
    public EventServiceFactory(final String uri, final ProcessMode processMode, final long batchIntervalInMilliseconds,
                               final String pathForFileContainingKafkaBrokerAddresses) {
        validateUri(uri);
        this.integrationPointFactory = new IntegrationPointFactory(uri, pathForFileContainingKafkaBrokerAddresses);
        this.processMode = processMode;
        this.batchIntervalInMilliseconds = batchIntervalInMilliseconds;
    }

    /**
     * Instantiates a new event-service factory for continuous processing
     *
     * @param uri
     *            the URI where the IntegrationPoint definitions can be found.
     */
    public EventServiceFactory(final String uri) {
        this(uri, ProcessMode.CONTINUOUS, 0L, System.getProperty(KAFKA_BROKERS_ADDRESSES_FILE_PROPERTY_NAME, DEFAULT_KAFKA_BROKERS_ADDRESSES_FILE));
    }

    /**
     * Instantiates a new event-service factory for continuous processing
     *
     * @param uri
     *            the URI where the IntegrationPoint definitions can be found.
     * @param pathForFileContainingKafkaBrokerAddresses
     *            the path of the file that contains the Kafka brokers addresses.
     * @deprecated Constructor deprecated in favor of using System properties to set the values of the Kafka brokers addresses and their port. The
     *             System property name for the brokers addresses is defined by the constant
     *             com.ericsson.aia.ipl.model.Constants.KAFKA_BROKERS_ADDRESSES_FILE_PROPERTY_NAME. For the port value, the constant is
     *             com.ericsson.aia.ipl.model.Constants.KAFKA_BROKERS_PORT_PROPERTY_NAME. Example of usage:
     *             System.setProperty(KAFKA_BROKERS_ADDRESSES_FILE_PROPERTY_NAME, [custom location of file containing the Kafka brokers addresses]);
     *             System.setProperty(KAFKA_BROKERS_PORT_PROPERTY_NAME, [custom port to use])
     */
    @Deprecated
    public EventServiceFactory(final String uri, final String pathForFileContainingKafkaBrokerAddresses) {
        validateUri(uri);
        System.setProperty(KAFKA_BROKERS_ADDRESSES_FILE_PROPERTY_NAME, pathForFileContainingKafkaBrokerAddresses);
        this.integrationPointFactory = new IntegrationPointFactory(uri, pathForFileContainingKafkaBrokerAddresses);
        this.processMode = ProcessMode.CONTINUOUS;
        this.batchIntervalInMilliseconds = 0L;
    }

    /**
     * Creates an event publisher.
     *
     * @param integrationPointName
     *            the IntegrationPoint name to be used to configure the event publisher.
     * @return the event publisher.
     */
    public EventPublisher<V> createEventPublisher(final String integrationPointName) {
        validateIntegrationPointName(integrationPointName);
        final String sanitizedIntegrationPointName = standardizeName(integrationPointName);
        addEventPublisherToLocalCacheIfAbsent(integrationPointName, sanitizedIntegrationPointName);
        LOGGER.info("Returning publisher {} from local cache.", sanitizedIntegrationPointName);
        return eventPublisherLocalCache.get(sanitizedIntegrationPointName);
    }

    private void addEventPublisherToLocalCacheIfAbsent(final String integrationPointName, final String sanitizedIntegrationPointName) {
        if (eventPublisherLocalCache.get(sanitizedIntegrationPointName) == null) {
            final IntegrationPoint integrationPoint = integrationPointFactory.create(integrationPointName);
            final IntegrationPointEventPublisher<V> eventPublisher = new IntegrationPointEventPublisher<>(integrationPoint);
            eventPublisherLocalCache.put(sanitizedIntegrationPointName, eventPublisher);
            LOGGER.debug("Adding publisher {} to the local cache", integrationPointName);
        }
    }

    /**
     * Creates an event subscriber.
     *
     * @param integrationPointName
     *            the IntegrationPoint name to be used to configure the event subscriber.
     * @return the event consumer.
     */
    public EventSubscriber<V> createEventSubscriber(final String integrationPointName) {
        validateIntegrationPointName(integrationPointName);
        final String sanitizedIntegrationPointName = standardizeName(integrationPointName);
        addEventSubscriberToLocalCacheIfAbsent(integrationPointName, sanitizedIntegrationPointName);
        LOGGER.info("Returning subscriber {} from local cache.", sanitizedIntegrationPointName);
        return eventSubscriberLocalCache.get(sanitizedIntegrationPointName);
    }

    private void addEventSubscriberToLocalCacheIfAbsent(final String integrationPointName, final String sanitizedIntegrationPointName) {
        if (eventSubscriberLocalCache.get(sanitizedIntegrationPointName) == null) {
            final IntegrationPoint integrationPoint = integrationPointFactory.create(integrationPointName);
            final EventSubscriber<V> integrationPointEventSubscriber =
                    processMode.getEventSubscriber(integrationPoint, batchIntervalInMilliseconds);
            eventSubscriberLocalCache.put(sanitizedIntegrationPointName, integrationPointEventSubscriber);
            LOGGER.debug("Adding subscriber {} to the local cache", integrationPointName);
        }
    }
}
