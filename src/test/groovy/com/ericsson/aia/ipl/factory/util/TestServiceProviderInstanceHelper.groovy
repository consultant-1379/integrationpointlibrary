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

package com.ericsson.aia.ipl.factory.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer

import com.ericsson.aia.ipl.util.ServiceProviderInstanceHelper

/**
 * The class TestInstanceProviderHelper;
 */
class TestServiceProviderInstanceHelper extends ServiceProviderInstanceHelper {

    List<Long> timesToPoll = new ArrayList<>()
    boolean overrideConsumer = false

    List<Runnable> runnables = new ArrayList<>()
    List<DummyKafkaConsumer> kafkaConsumers = new ArrayList<>()

    KafkaConsumer kafkaConsumer

    Future runnableResult = [
        isDone : { true }
    ] as Future

    ExecutorService executorService = [
            submit : { a -> submitRunnable(a) }
    ] as ExecutorService

    Future submitRunnable(runnable) {
        runnables.add(runnable)
        runnableResult
    }

    @Override
    public <V> KafkaConsumer<String, V> getKafkaConsumer(final Properties properties) {
        if (!overrideConsumer) {
            KafkaConsumer kafkaConsumer = new DummyKafkaConsumer<>(Collections.emptyList())
            kafkaConsumers.add(kafkaConsumer)
            return kafkaConsumer
        }
        return kafkaConsumer
    }

    @Override
    public <V> KafkaProducer<String, V> getKafkaProducer(final Properties properties) {
        return new DummyKafkaProducer(kafkaConsumers)
    }

    public void setKafkaConsumer(final KafkaConsumer kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer
        overrideConsumer = true
    }

    @Override
    public long getTimeToPoll() {
        if (timesToPoll.isEmpty()) {
            return System.currentTimeMillis()
        } else if (timesToPoll.size() == 1) {
            return timesToPoll.get(0)
        }
        return timesToPoll.remove(0)
    }

    @Override
    public ExecutorService getNewFixedThreadPool(final int nThreads) {
        if (!overrideConsumer) {
            return Executors.newFixedThreadPool(nThreads)
        }
        return executorService
    }

    public List<Runnable> getRunnables() {
        return runnables
    }

    public void setTimesToPoll(List<Long> timesToPoll) {
        this.timesToPoll =  timesToPoll
    }
}
