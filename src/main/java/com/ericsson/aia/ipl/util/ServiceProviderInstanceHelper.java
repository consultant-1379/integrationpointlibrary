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

package com.ericsson.aia.ipl.util;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

/**
 * The class ServiceProviderInstanceHelper;
 * provide an instance of kafka consumer, executor service and system time
 */
public class ServiceProviderInstanceHelper {

    /**
     * Returns a new kafka consumer with the given properties
     * In tests it returns a dummy kafka consumer
     * @param properties java properties
     * @param <V> The record type to consume
     * @return The kafka consumer
     */
    public <V> KafkaConsumer<String, V> getKafkaConsumer(final Properties properties) {
        return new KafkaConsumer<>(properties);
    }

    /**
     * Returns a new kafka producer with the given properties
     * In tests it returns a dummy kafka producer
     * @param properties java properties
     * @param <V> The record type to send
     * @return The kafka producer
     */
    public <V> KafkaProducer<String, V> getKafkaProducer(final Properties properties) {
        return new KafkaProducer<>(properties);
    }

    /**
     * Return an executor service with the given number of threads.
     * In tests it returns a reference to a dummy executor service
     * @param threadCount the number of threads in the thread pool
     * @return the executor service
     */
    public ExecutorService getNewFixedThreadPool(final int threadCount) {
        return Executors.newFixedThreadPool(threadCount);
    }

    /**
     * @return  the current system time
     */
    public long getTimeToPoll() {
        return System.currentTimeMillis();
    }
}
