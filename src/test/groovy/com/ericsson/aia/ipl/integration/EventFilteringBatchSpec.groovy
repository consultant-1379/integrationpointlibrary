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
package com.ericsson.aia.ipl.integration

import static com.ericsson.aia.ipl.model.Constants.URI_DELIMITER
import static com.ericsson.aia.ipl.test.util.TestUtils.defineTestValueForModelRepoHome
import static com.ericsson.aia.ipl.test.util.TestUtils.defineTestValuesForKafkaBrokersAddressesModifier

import java.util.concurrent.TimeUnit

import com.ericsson.aia.ipl.EventCollectionListener
import com.ericsson.aia.ipl.EventListener
import com.ericsson.aia.ipl.EventPublisher
import com.ericsson.aia.ipl.EventSubscriber
import com.ericsson.aia.ipl.factory.EventServiceFactory
import com.ericsson.aia.ipl.factory.util.TestServiceProviderInstanceHelper
import com.ericsson.aia.ipl.impl.ProcessMode
import com.ericsson.aia.ipl.model.IntegrationPointCache
import com.ericsson.aia.ipl.model.UriSchemeType
import com.ericsson.aia.ipl.test.util.EmbeddedKafkaAndZookeeperServers
import com.ericsson.aia.ipl.test.util.SimpleBatchEventListener
import com.ericsson.aia.ipl.util.ServiceProviderInstance
import com.ericsson.aia.ipl.util.ServiceProviderInstanceHelper

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Timeout
import spock.lang.Title

@Stepwise
@Title("Integration test for sending and receiving events against a real Kafka broker.")
class EventFilteringBatchSpec extends Specification {

    static final String LOCAL_URI_SCHEME = UriSchemeType.LOCAL.getUriScheme()
    static final String MODEL_SERVICE_URI_SCHEME = UriSchemeType.MODEL.getUriScheme()
    static final String VALID_JSON_INTEGRATION_POINT_FILES_URI = LOCAL_URI_SCHEME + URI_DELIMITER + "src/test/resources/valid/batch/json/"
    static final String VALID_XML_INTEGRATION_POINT_FILES_URI = LOCAL_URI_SCHEME + URI_DELIMITER + "src/test/resources/valid/batch/xml/"
    static final String VALID_MODEL_SERVICE_URI = MODEL_SERVICE_URI_SCHEME + URI_DELIMITER
    static final String KAFKA_BROKER_ADDRESSES_FILE_FOR_TEST = "src/test/resources/global.properties.sample"
    static final String KAFKA_BROKER_PORT_FOR_TESTS = 59003
    static final long MILLISECONDS_TO_WAIT_BETWEEN_RETRIES = 300
    static final int SECONDS_TO_TIMEOUT = 60

    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    def setupSpec() {
        defineTestValueForModelRepoHome()
        defineTestValuesForKafkaBrokersAddressesModifier KAFKA_BROKER_ADDRESSES_FILE_FOR_TEST, KAFKA_BROKER_PORT_FOR_TESTS
    }

    def setup() {
        IntegrationPointCache.clear()
        ServiceProviderInstance.getInstance().setInstanceProviderHelper(new TestServiceProviderInstanceHelper<>())
    }

    def cleanup() {
        ServiceProviderInstance.getInstance().setInstanceProviderHelper(new ServiceProviderInstanceHelper())
    }

    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    def cleanupSpec() {
        ServiceProviderInstance.getInstance().setInstanceProviderHelper(new ServiceProviderInstanceHelper())
    }

    @Ignore  //Marked as ignored because of instability with the Embedded Kafka client. Discussions will take place to fix the instability
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    def "Should apply filtering to the sent and received events using Model URI" () {
        given: """an EventServiceFactory, a custom kafkaBrokersAddressesModifier for tests, an EventPublisher with a valid IntegrationPoint,
                  an EventSubscriber with a valid IntegrationPoint, and an EventListener"""
        EventServiceFactory factory = new EventServiceFactory(VALID_MODEL_SERVICE_URI, ProcessMode.BATCH, 5, KAFKA_BROKER_ADDRESSES_FILE_FOR_TEST)
        String senderIntegrationPointName = "EVENT_PRODUCING_INTEGRATION_POINT"
        String consumerIntegrationPointName = "EVENT_CONSUMING_INTEGRATION_POINT"
        EventPublisher<String> eventPublisher = factory.createEventPublisher(senderIntegrationPointName)
        EventSubscriber<String> eventSubscriber = factory.createEventSubscriber(consumerIntegrationPointName)
        EventListener<String> simpleEventListener = new SimpleBatchEventListener<String>()

        when: "registering the eventListener with the eventSubscriber that accepts only event ids '1', '2', '5' for decoded topic"
        eventSubscriber.registerEventListener simpleEventListener

        and: """sending event with ids '1', '2', '5', '6' with an eventPublisher that only sends event ids '1', '5', '6', '7' for decoded topic"""
        eventPublisher.sendRecord "1", "EventId 1 should be sent and received"
        eventPublisher.sendRecord "2", "EventId 2 should not be sent"
        eventPublisher.sendRecord "5", "EventId 5 should be sent and received"
        eventPublisher.sendRecord "6", "EventId 6 should be sent but not received"

        then: """both publisher and subscriber should respect the event filter, allowing only events with ids '1' and '5' to be received by the
                event listener"""
        int expectedNumberOfEvents = 2
        waitUntilAllExpectedEventsArrived simpleEventListener, expectedNumberOfEvents, MILLISECONDS_TO_WAIT_BETWEEN_RETRIES,
                SECONDS_TO_TIMEOUT
        simpleEventListener.getEvents().size() == expectedNumberOfEvents
        simpleEventListener.getEvents().contains("EventId 1 should be sent and received")
        simpleEventListener.getEvents().contains("EventId 5 should be sent and received")
        !simpleEventListener.getEvents().contains("EventId 2 should not be sent")
        !simpleEventListener.getEvents().contains("EventId 6 should be sent but not received")
    }

    @Ignore
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    def "Should apply filtering to the sent and received events in a complex scenario (using XML files)" () {
        given: "an EventServiceFactory"
        EventServiceFactory factory = new EventServiceFactory(VALID_XML_INTEGRATION_POINT_FILES_URI, ProcessMode.BATCH, 5, KAFKA_BROKER_ADDRESSES_FILE_FOR_TEST)

        and: """An EventSubscriber with valid IntegrationPoints for an all eventIds,
                an EventSubscriber with valid IntegrationPoints for odd numbered eventIds,
                an EventSubscriber with valid IntegrationPoints for even numbered eventIds,
                an EventPublisher with valid IntegrationPoints for odd numbered eventIds,
                an EventPublisher with valid IntegrationPoints for event numbered eventIds"""
        EventSubscriber<String> allEventIdsSubscriber = factory.createEventSubscriber("COMPLEX_TEST_ALL_NUMBER_EVENT_CONSUMING_INTEGRATION_POINT")
        EventSubscriber<String> oddEventIdsSubscriber = factory.createEventSubscriber("COMPLEX_TEST_ODD_NUMBER_EVENT_CONSUMING_INTEGRATION_POINT")
        EventSubscriber<String> evenEventIdsSubscriber = factory.createEventSubscriber("COMPLEX_TEST_EVEN_NUMBER_EVENT_CONSUMING_INTEGRATION_POINT")
        EventPublisher<String> oddEventIdsPublisher = factory.createEventPublisher("COMPLEX_TEST_ODD_NUMBER_PRODUCING_INTEGRATION_POINT")
        EventPublisher<String> evenEventIdsPublisher = factory.createEventPublisher("COMPLEX_TEST_EVEN_NUMBER_PRODUCING_INTEGRATION_POINT")

        and: "a event listener for each subscriber"
        EventListener<String> allEventIdsEventListener = new SimpleBatchEventListener<String>()
        EventListener<String> oddEventIdsEventListener = new SimpleBatchEventListener<String>()
        EventListener<String> evenEventIdsEventListener = new SimpleBatchEventListener<String>()

        and: "registering the eventListeners with the subscribers"
        allEventIdsSubscriber.registerEventListener allEventIdsEventListener
        oddEventIdsSubscriber.registerEventListener oddEventIdsEventListener
        evenEventIdsSubscriber.registerEventListener evenEventIdsEventListener

        when: """sending the same batch of events through the publishers, one of them only configured to send odd numbered event ids and the other,
                    just even numbered event ids"""
        sendBatchOfEvents oddEventIdsPublisher, "oddEventIdsSubscriber"
        sendBatchOfEvents evenEventIdsPublisher, "evenEventIdsSubscriber"

        then: "the all event Ids subscriber should receive all events that the publishers were allowed to send"
        int expectedOutcomeForAllEventIdsSubscriber = 12
        waitUntilAllExpectedEventsArrived allEventIdsEventListener, expectedOutcomeForAllEventIdsSubscriber, MILLISECONDS_TO_WAIT_BETWEEN_RETRIES,
                SECONDS_TO_TIMEOUT
        allEventIdsEventListener.getEvents().size() == expectedOutcomeForAllEventIdsSubscriber

        and: "the odd numbered event ids subscriber should receive only that kind of events"
        int expectedOutcomeForOddNumberedEventIdsSubscriber = 6
        waitUntilAllExpectedEventsArrived oddEventIdsEventListener, expectedOutcomeForOddNumberedEventIdsSubscriber,
                MILLISECONDS_TO_WAIT_BETWEEN_RETRIES, SECONDS_TO_TIMEOUT
        oddEventIdsEventListener.getEvents().size() == expectedOutcomeForOddNumberedEventIdsSubscriber
         oddEventIdsEventListener.getEvents().every { event ->
            (isEventIdOddOrEven(event.split()[0]) == "odd") && event.endsWith("oddEventIdsSubscriber")
        }

        and: "the even numbered event ids subscriber should receive only that kind of events"
        int expectedOutcomeForEvenNumberedEventIdsSubscriber = 6
        waitUntilAllExpectedEventsArrived evenEventIdsEventListener, expectedOutcomeForEvenNumberedEventIdsSubscriber,
                MILLISECONDS_TO_WAIT_BETWEEN_RETRIES, SECONDS_TO_TIMEOUT
        evenEventIdsEventListener.getEvents().size() == expectedOutcomeForEvenNumberedEventIdsSubscriber
        evenEventIdsEventListener.getEvents().every { event ->
            (isEventIdOddOrEven(event.split()[0]) == "even") && event.endsWith("evenEventIdsSubscriber")
        }
    }

    @Ignore
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    def "Should apply filtering to the sent and received events in a complex scenario (using JSON files)" () {
        given: "an EventServiceFactory"
        EventServiceFactory factory = new EventServiceFactory(VALID_JSON_INTEGRATION_POINT_FILES_URI, ProcessMode.BATCH, 5, KAFKA_BROKER_ADDRESSES_FILE_FOR_TEST)

        and: """An EventSubscriber with valid IntegrationPoints for an all eventIds,
                an EventSubscriber with valid IntegrationPoints for odd numbered eventIds,
                an EventSubscriber with valid IntegrationPoints for even numbered eventIds,
                an EventPublisher with valid IntegrationPoints for odd numbered eventIds,
                an EventPublisher with valid IntegrationPoints for event numbered eventIds"""
        EventSubscriber<String> allEventIdsSubscriber = factory.createEventSubscriber("COMPLEX_TEST_ALL_NUMBER_EVENT_CONSUMING_INTEGRATION_POINT")
        EventSubscriber<String> oddEventIdsSubscriber = factory.createEventSubscriber("COMPLEX_TEST_ODD_NUMBER_EVENT_CONSUMING_INTEGRATION_POINT")
        EventSubscriber<String> evenEventIdsSubscriber = factory.createEventSubscriber("COMPLEX_TEST_EVEN_NUMBER_EVENT_CONSUMING_INTEGRATION_POINT")
        EventPublisher<String> oddEventIdsPublisher = factory.createEventPublisher("COMPLEX_TEST_ODD_NUMBER_PRODUCING_INTEGRATION_POINT")
        EventPublisher<String> evenEventIdsPublisher = factory.createEventPublisher("COMPLEX_TEST_EVEN_NUMBER_PRODUCING_INTEGRATION_POINT")

        and: "a event listener for each subscriber"
        EventListener<String> allEventIdsEventListener = new SimpleBatchEventListener<String>()
        EventListener<String> oddEventIdsEventListener = new SimpleBatchEventListener<String>()
        EventListener<String> evenEventIdsEventListener = new SimpleBatchEventListener<String>()

        and: "registering the eventListeners with the subscribers"
        allEventIdsSubscriber.registerEventListener allEventIdsEventListener
        oddEventIdsSubscriber.registerEventListener oddEventIdsEventListener
        evenEventIdsSubscriber.registerEventListener evenEventIdsEventListener

        when: """sending the same batch of events through the publishers, one of them only configured to send odd numbered event ids and the other,
                    just even numbered event ids"""
        sendBatchOfEvents oddEventIdsPublisher, "oddEventIdsSubscriber"
        sendBatchOfEvents evenEventIdsPublisher, "evenEventIdsSubscriber"

        then: "the all event Ids subscriber should receive all events that the publishers were allowed to send"
        int expectedOutcomeForAllEventIdsSubscriber = 12
        waitUntilAllExpectedEventsArrived allEventIdsEventListener, expectedOutcomeForAllEventIdsSubscriber, MILLISECONDS_TO_WAIT_BETWEEN_RETRIES,
                SECONDS_TO_TIMEOUT
        allEventIdsEventListener.getEvents().size() == expectedOutcomeForAllEventIdsSubscriber

        and: "the odd numbered event ids subscriber should receive only that kind of events"
        int expectedOutcomeForOddNumberedEventIdsSubscriber = 6
        waitUntilAllExpectedEventsArrived oddEventIdsEventListener, expectedOutcomeForOddNumberedEventIdsSubscriber,
                MILLISECONDS_TO_WAIT_BETWEEN_RETRIES, SECONDS_TO_TIMEOUT
        oddEventIdsEventListener.getEvents().size() == expectedOutcomeForOddNumberedEventIdsSubscriber
        oddEventIdsEventListener.getEvents().every { event ->
            (isEventIdOddOrEven(event.split()[0]) == "odd") && event.endsWith("oddEventIdsSubscriber")
        }

        and: "the even numbered event ids subscriber should receive only that kind of events"
        int expectedOutcomeForEvenNumberedEventIdsSubscriber = 6
        waitUntilAllExpectedEventsArrived evenEventIdsEventListener, expectedOutcomeForEvenNumberedEventIdsSubscriber,
                MILLISECONDS_TO_WAIT_BETWEEN_RETRIES, SECONDS_TO_TIMEOUT
        evenEventIdsEventListener.getEvents().size() == expectedOutcomeForEvenNumberedEventIdsSubscriber
        evenEventIdsEventListener.getEvents().every { event ->
            (isEventIdOddOrEven(event.split()[0]) == "even") && event.endsWith("evenEventIdsSubscriber")
        }
    }

    private void sendBatchOfEvents(EventPublisher sender, String senderName) {
        String[] eventIdsToSend = [
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15"
        ]
        eventIdsToSend.each { eventId ->
            sender.sendRecord eventId, eventId + " " + senderName
        }
    }

    private String isEventIdOddOrEven(String number) {
        int numberAsInteger = number.toInteger()
        if (numberAsInteger % 2 == 0) {
            return "even"
        }
        return "odd"
    }

    private void waitUntilAllExpectedEventsArrived(EventCollectionListener eventListener, int numberOfExpectedEvents, long millisecondsToWaitBeforeRetrying,
            int secondsToTimeOut) {
        long millisecondsTranspired = 0
        while (eventListener.getEvents().size() != numberOfExpectedEvents) {
            sleepMilliseconds(millisecondsToWaitBeforeRetrying)
            millisecondsTranspired += millisecondsToWaitBeforeRetrying
            if ( millisecondsTranspired / 1000 > secondsToTimeOut) {
                return
            }
        }
    }

    private void sleepMilliseconds(long millis) {
        Thread.sleep(millis)
    }
}
