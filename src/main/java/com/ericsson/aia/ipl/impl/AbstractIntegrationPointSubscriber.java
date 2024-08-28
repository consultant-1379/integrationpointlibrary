package com.ericsson.aia.ipl.impl;

import com.ericsson.aia.ipl.EventListener;
import com.ericsson.aia.ipl.EventSubscriber;
import com.ericsson.aia.ipl.model.Destination;
import com.ericsson.aia.ipl.model.IntegrationPoint;
import com.ericsson.aia.ipl.util.ServiceProviderInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ericsson.aia.ipl.util.Utils.getPropertiesAsString;
import static com.ericsson.aia.ipl.util.ValidationUtils.validateEventListener;
import static com.ericsson.aia.ipl.util.ValidationUtils.validateIntegrationPoint;

/**
 * Abstract Implementation of an IntegrationPoint based event subscriber.
 *
 * @param <V>
 *           type of event for the event listener
 */
public abstract class AbstractIntegrationPointSubscriber<V> implements EventSubscriber<V> {

    private static final long TERMINATION_TIME_OUT_IN_MILLISECOND = 10L;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final IntegrationPoint integrationPoint;
    private final ExecutorService destinationsExecutorService;
    private final List<KafkaSubscriber<V>> subscribers;

    /**
     * Instantiates a new IntegrationPoint based event subscriber.
     *
     * @param integrationPoint the IntegrationPoint to configure this subscriber.
     */
    public AbstractIntegrationPointSubscriber(final IntegrationPoint integrationPoint) {
        validateIntegrationPoint(integrationPoint);
        this.integrationPoint = integrationPoint;
        this.destinationsExecutorService = ServiceProviderInstance.getInstance().getNewFixedThreadPool(integrationPoint.getDestinations().length);
        this.subscribers = new ArrayList<>();
        LOGGER.info("New IntegrationPoint based event subscriber created.");
    }

    /**
     * Register the event listener to receive subscribed events.
     *
     * @param eventListener the event listener
     */
    @Override
    public void registerEventListener(final EventListener<V> eventListener) {
        validateEventListener(eventListener);
        startEventListener(eventListener);
        LOGGER.info("New event listener registered and started ({}).", eventListener);
    }

    /**
     * Start the event listener.
     */
    private void startEventListener(final EventListener<V> eventListener) {
        for (final Destination destination : integrationPoint.getDestinations()) {
            final Properties commonSubscriberProperties = integrationPoint.getProperties();
            final KafkaSubscriber<V> kafkaSubscriberWrapper = getKafkaSubscriberRunnable(commonSubscriberProperties, destination,
                eventListener);
            subscribers.add(kafkaSubscriberWrapper);
            destinationsExecutorService.submit(kafkaSubscriberWrapper);
            LOGGER.info("KafkaSubscriber created for topic '{}'.", destination);
            LOGGER.debug(getPropertiesAsString(commonSubscriberProperties));
        }
    }

    /**
     * @param commonSubscriberProperties common subscriber properties
     * @param destination                the destination {@link Destination}
     * @param eventListener              the eventListener {@link EventListener}
     * @return {@link KafkaSubscriber}
     */
    public abstract KafkaSubscriber<V> getKafkaSubscriberRunnable(final Properties commonSubscriberProperties, final Destination destination,
                                                               final EventListener<V> eventListener);

    @Override
    public void close() throws IOException {
        LOGGER.info("Closing all subscribers ... ");
        for (final KafkaSubscriber<V> kafkaSubscriberWrapper : subscribers) {
            kafkaSubscriberWrapper.close();
        }
        if (!isDown()) {
            destinationsExecutorService.shutdown();
            if (isSchedulerStopped()) {
                LOGGER.info("Executor has been successfully stopped.");
            } else {
                LOGGER.warn("Executor has failed to stop after {} ms.", TERMINATION_TIME_OUT_IN_MILLISECOND);
                LOGGER.info("Attempting to shutdown now ...");
                destinationsExecutorService.shutdownNow();
            }
        } else {
            LOGGER.info("Executor is already down. No need to stop.");
        }

    }

    private boolean isSchedulerStopped() {
        try {
            return destinationsExecutorService.awaitTermination(TERMINATION_TIME_OUT_IN_MILLISECOND, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException interruptedException) {
            LOGGER.error("Exception while checking if scheduler has stopped", interruptedException);
            return false;
        }
    }

    private boolean isDown() {
        return destinationsExecutorService == null || destinationsExecutorService.isShutdown();
    }

}
