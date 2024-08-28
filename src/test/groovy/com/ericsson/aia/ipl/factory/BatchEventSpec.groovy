package com.ericsson.aia.ipl.factory

import static com.ericsson.aia.ipl.model.Constants.KAFKA_BROKERS_ADDRESSES_FILE_PROPERTY_NAME

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition

import com.ericsson.aia.ipl.BatchCommitter
import com.ericsson.aia.ipl.EventCollectionListener
import com.ericsson.aia.ipl.EventSubscriber
import com.ericsson.aia.ipl.batch.BatchEvent
import com.ericsson.aia.ipl.batch.BatchEventType
import com.ericsson.aia.ipl.batch.util.BatchConstants
import com.ericsson.aia.ipl.factory.util.DummyKafkaConsumer
import com.ericsson.aia.ipl.factory.util.TestServiceProviderInstanceHelper
import com.ericsson.aia.ipl.impl.KafkaBatchSubscriberWrapper
import com.ericsson.aia.ipl.util.Offsets
import com.ericsson.aia.ipl.util.ServiceProviderInstance
import com.ericsson.aia.ipl.util.ServiceProviderInstanceHelper
import com.ericsson.aia.ipl.impl.ProcessMode
import com.ericsson.aia.ipl.test.util.SimpleBatchEventListener

import org.apache.kafka.clients.consumer.OffsetAndMetadata

import spock.lang.Specification

class BatchEventSpec extends Specification {

    private static final String LOCAL_URI = "local://"
    private static final String INTEGRATION_POINT_FILES_URL = LOCAL_URI + "src/test/resources/valid/batch/"
    private static final String KAFKA_BROKER_ADDRESSES_FILE_FOR_TEST = "src/test/resources/global.properties.sample"
    private static final String TOPIC = "topic"
    private static final int PARTITION = 3
    private EventSubscriber subscriber
    SimpleBatchEventListener<String> simpleBatchEventListener = new SimpleBatchEventListener<String>()
    private TestServiceProviderInstanceHelper providerHelper = new TestServiceProviderInstanceHelper<>()
    private int numOfRecordsPerConsumer = 10


    SimpleBatchEventListener<String> brokenEndBatchEventListener = [
            onEvent : { a -> simpleBatchEventListener.onEvent(a) },
            batchEvent : { a -> throwIfBatchType(a, BatchEventType.END_BATCH) }
    ] as SimpleBatchEventListener

    SimpleBatchEventListener<String> brokenStartBatchEventListener = [
            onEvent : { a -> simpleBatchEventListener.onEvent(a) },
            batchEvent : { a -> throwIfBatchType(a, BatchEventType.START_BATCH) }
    ] as SimpleBatchEventListener

    private void throwIfBatchType(BatchEvent batchEvent, BatchEventType batchEventType) {
        simpleBatchEventListener.batchEvent(batchEvent)
        if (batchEvent.getBatchEventType() == batchEventType) {
            throw new Exception()
        }
    }

    def setup() {
        System.setProperty(KAFKA_BROKERS_ADDRESSES_FILE_PROPERTY_NAME, KAFKA_BROKER_ADDRESSES_FILE_FOR_TEST)
    }

    def "events are processed by the batch subscriber when available to the kafka consumer"() {
        DummyKafkaConsumer<String, String> kafkaConsumer = setupEnv(providerHelper)
        providerHelper.setTimesToPoll((1000..2000).step(40))
        given: "An event service factory set up with a batch subscriber"
            //Only execute one of the two runnables, ignore the other one that consumes event 10
            KafkaBatchSubscriberWrapper kafkaBatchSubscriberWrapper = startEventListener(simpleBatchEventListener, providerHelper).get(0)
        when:"Records are polled by the subscriber"
            kafkaBatchSubscriberWrapper.processBatch()
            List<String> allEvents = getAllEvents(simpleBatchEventListener)
        then:"The events that match the filter are processed"
            allEvents.size() == numOfRecordsPerConsumer
        and:"There are the correct number of commits"
            kafkaConsumer.callsToCommit == 0
            commit(simpleBatchEventListener, 1)
            kafkaBatchSubscriberWrapper.commitIfNecessary()
            kafkaConsumer.callsToCommit == 1
        and:"The start and end batch events are sent"
            simpleBatchEventListener.batchEvents.size() == 2
        and:"The offsets are as expected"
            Offsets offsets = getExpectedOffsets(1, numOfRecordsPerConsumer * 2)
            simpleBatchEventListener.batchEvents.get(1).getAttributes().get(BatchConstants.OFFSETS) == offsets
            kafkaConsumer.committedOffsets == [offsets.getEndOffsets()]
        and:"the number of partitions and assigned partitions are correct"
            simpleBatchEventListener.batchEvents.get(0).getAttributes().get(BatchConstants.NUMBER_OF_PARTITIONS) == 1
            simpleBatchEventListener.batchEvents.get(0).getAttributes().get(BatchConstants.ASSIGNED_PARTITIONS) == new HashSet(Arrays.asList(PARTITION))
    }

    def "events are processed by multiple consumers when available to the kafka consumer"() {
        DummyKafkaConsumer<String, String> kafkaConsumer = setupEnv(providerHelper)
        providerHelper.setTimesToPoll((1000..2000).step(40))
        given: "An event service factory set up with two batch subscribers"
            List<KafkaBatchSubscriberWrapper> kafkaBatchSubscriberWrappers = startEventListener(simpleBatchEventListener, providerHelper)
        when:"Records are polled by both subscribers"
            kafkaBatchSubscriberWrappers.each {
                it.processBatch()
                kafkaConsumer.addRecords(createRecordList())
                providerHelper.setTimesToPoll((1000..2000).step(40))
            }
            List<String> allEvents = getAllEvents(simpleBatchEventListener)
        then:"All events are processed because both consumers are being executed"
            allEvents.size() == numOfRecordsPerConsumer * 2
        and:"There are the correct number of commits"
            kafkaConsumer.callsToCommit == 0
            commit(simpleBatchEventListener, 1)
            for (KafkaBatchSubscriberWrapper kafkaBatchSubscriberWrapper : kafkaBatchSubscriberWrappers) {
                kafkaBatchSubscriberWrapper.commitIfNecessary()
            }
            kafkaConsumer.callsToCommit == 1
            commit(simpleBatchEventListener, 3)
            for (KafkaBatchSubscriberWrapper kafkaBatchSubscriberWrapper : kafkaBatchSubscriberWrappers) {
                kafkaBatchSubscriberWrapper.commitIfNecessary()
            }
            kafkaConsumer.callsToCommit == 2
        and:"The start and end batch events are sent"
            simpleBatchEventListener.batchEvents.size() == 4
        and:"The offsets are as expected"
            simpleBatchEventListener.batchEvents.get(1).getAttributes().get(BatchConstants.OFFSETS) == getExpectedOffsets(1, numOfRecordsPerConsumer * 2)
            simpleBatchEventListener.batchEvents.get(3).getAttributes().get(BatchConstants.OFFSETS) == getExpectedOffsets(1, numOfRecordsPerConsumer * 2)
        and:"the number of partitions and assigned partitions are correct"
            simpleBatchEventListener.batchEvents.get(0).getAttributes().get(BatchConstants.NUMBER_OF_PARTITIONS) == 1
            simpleBatchEventListener.batchEvents.get(0).getAttributes().get(BatchConstants.ASSIGNED_PARTITIONS) == new HashSet(Arrays.asList(PARTITION))
            simpleBatchEventListener.batchEvents.get(2).getAttributes().get(BatchConstants.NUMBER_OF_PARTITIONS) == 1
            simpleBatchEventListener.batchEvents.get(2).getAttributes().get(BatchConstants.ASSIGNED_PARTITIONS) == new HashSet(Arrays.asList(PARTITION))
    }

    def "A cancel batch is sent then partitions are revoked and the batch is restarted"() {
        List<Boolean> revokePartitions = [false, true, true, false]
        DummyKafkaConsumer<String, String> kafkaConsumer = setupEnv(providerHelper)
        given: "An event service factory set up with two batch subscribers"
            providerHelper.setTimesToPoll((1000..2000).step(40))
            List<KafkaBatchSubscriberWrapper> kafkaBatchSubscriberWrappers = startEventListener(simpleBatchEventListener, providerHelper)
        and:"revokePartitions set to true so that a cancel batch is sent first"
            kafkaConsumer.revokePartitions = revokePartitions
        when:"Records are polled by both subscribers"
            kafkaBatchSubscriberWrappers.each {
                it.processBatch()
                kafkaConsumer.addRecords(createRecordList())
                providerHelper.setTimesToPoll((1000..2000).step(40))
            }
            List<String> allEvents = getAllEvents(simpleBatchEventListener)
        then:"Not all events are sent because the batch is cancelled"
            allEvents.size() == 13
        and:"There are the correct number of commits"
            kafkaConsumer.callsToCommit == 0
            commit(simpleBatchEventListener, 3)
            for (KafkaBatchSubscriberWrapper kafkaBatchSubscriberWrapper : kafkaBatchSubscriberWrappers) {
                kafkaBatchSubscriberWrapper.commitIfNecessary()
            }
            kafkaConsumer.callsToCommit == 1
            commit(simpleBatchEventListener, 3)
            for (KafkaBatchSubscriberWrapper kafkaBatchSubscriberWrapper : kafkaBatchSubscriberWrappers) {
                kafkaBatchSubscriberWrapper.commitIfNecessary()
            }
            kafkaConsumer.callsToCommit == 2
        and:"The start and end batch events are sent"
            simpleBatchEventListener.batchEvents.size() == 4
        and:"The offsets are as expected"
            simpleBatchEventListener.batchEvents.get(3).getAttributes().get(BatchConstants.OFFSETS) == getExpectedOffsets(3, 7)
        and:"the number of partitions and assigned partitions are correct"
            simpleBatchEventListener.batchEvents.get(0).getAttributes().get(BatchConstants.NUMBER_OF_PARTITIONS) == 1
            simpleBatchEventListener.batchEvents.get(0).getAttributes().get(BatchConstants.ASSIGNED_PARTITIONS) == new HashSet(Arrays.asList(PARTITION))
            simpleBatchEventListener.batchEvents.get(2).getAttributes().get(BatchConstants.NUMBER_OF_PARTITIONS) == 1
            simpleBatchEventListener.batchEvents.get(2).getAttributes().get(BatchConstants.ASSIGNED_PARTITIONS) == new HashSet(Arrays.asList(PARTITION))
        and:"The cancel batch is sent"
            simpleBatchEventListener.batchEvents.get(1).getBatchEventType() == BatchEventType.CANCEL_BATCH
            simpleBatchEventListener.batchEvents.get(1).getAttributes().get(BatchConstants.TOPIC) == "undecoded-complex"
    }

    private void commit(final SimpleBatchEventListener simpleBatchEventListener, final int index) {
        BatchCommitter batchCommitter = simpleBatchEventListener.batchEvents.get(index).getAttributes().get(BatchConstants.COMMITTER)
        batchCommitter.commitBatchAsync()
    }

    def "if startBatch throws an exception no events are sent but the thread keeps running"() {
        DummyKafkaConsumer<String, String> kafkaConsumer = setupEnv(providerHelper)
        given: "An event service factory set up with two batch subscribers and a list of 'batch end' times to return"
            providerHelper.setTimesToPoll((1000..2000).step(40))
            List<KafkaBatchSubscriberWrapper> kafkaBatchSubscriberWrappers = startEventListener(brokenStartBatchEventListener, providerHelper)
        when:"Records are polled by both subscribers"
            kafkaBatchSubscriberWrappers.each {
                it.processBatch()
                kafkaConsumer.addRecords(createRecordList())
                providerHelper.setTimesToPoll((1000..2000).step(40))
            }
            List<String> allEvents = getAllEvents(simpleBatchEventListener)
        then:"No events are processed"
            allEvents.size() == 0
        and:"There no commits"
            kafkaConsumer.callsToCommit == 0
        and:"Only start batch events are sent"
            simpleBatchEventListener.batchEvents.size() == 2
            simpleBatchEventListener.batchEvents.each { it.batchEventType == BatchEventType.END_BATCH}
        and:"the number of partitions and assigned partitions are correct"
            simpleBatchEventListener.batchEvents.get(0).getAttributes().get(BatchConstants.NUMBER_OF_PARTITIONS) == 1
            simpleBatchEventListener.batchEvents.get(0).getAttributes().get(BatchConstants.ASSIGNED_PARTITIONS) == new HashSet(Arrays.asList(PARTITION))
    }

    def "If an exception is thrown during endBatch it does not kill the thread"() {
        DummyKafkaConsumer<String, String> kafkaConsumer = setupEnv(providerHelper)
        providerHelper.setTimesToPoll((1000..2000).step(40))
        given: "An event service factory set up with a listener that throws an exception at end batch"
            List<KafkaBatchSubscriberWrapper> kafkaBatchSubscriberWrappers = startEventListener(brokenEndBatchEventListener, providerHelper)
        when:"Records are polled by both subscribers"
            kafkaBatchSubscriberWrappers.each {
                it.processBatch()
                kafkaConsumer.addRecords(createRecordList())
                providerHelper.setTimesToPoll((1000..2000).step(40))
            }
            List<String> allEvents = getAllEvents(simpleBatchEventListener)
        then:"All events are processed because both consumers are being executed"
            allEvents.size() == numOfRecordsPerConsumer * 2
        and:"The start and end batch events are sent"
            simpleBatchEventListener.batchEvents.size() == 4
        and:"the number of partitions and assigned partitions are correct"
            simpleBatchEventListener.batchEvents.get(0).getAttributes().get(BatchConstants.NUMBER_OF_PARTITIONS) == 1
            simpleBatchEventListener.batchEvents.get(0).getAttributes().get(BatchConstants.ASSIGNED_PARTITIONS) == new HashSet(Arrays.asList(PARTITION))
    }

    private List<KafkaBatchSubscriberWrapper> startEventListener(final SimpleBatchEventListener simpleBatchEventListener, TestServiceProviderInstanceHelper providerHelper) {
        EventServiceFactory factory = new EventServiceFactory(INTEGRATION_POINT_FILES_URL, ProcessMode.BATCH, 1000L, KAFKA_BROKER_ADDRESSES_FILE_FOR_TEST)
        this.subscriber = factory.createEventSubscriber("EventBatchSpecConsumer")
        this.subscriber.registerEventListener(simpleBatchEventListener)
        return new ArrayList<>(providerHelper.getRunnables())
    }


    private Map<TopicPartition, List<ConsumerRecord<String, String>>> getRecords(String topic, int partition, long offset, String eventId, String value) {
        TopicPartition topicPartition = new TopicPartition(topic, partition)
        Map<TopicPartition, List<ConsumerRecord<String, String>>> records = new HashMap<>()
        final ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(topic, partition, offset, eventId, value)
        List<ConsumerRecord<String, String>> list = new ArrayList<>(Arrays.asList(consumerRecord))
        records.put(topicPartition, list)
        return records
    }

    private KafkaConsumer setupEnv(TestServiceProviderInstanceHelper providerHelper) {
        final DummyKafkaConsumer<String, String> kafkaConsumer = new DummyKafkaConsumer<>(createRecordList())
        providerHelper.setKafkaConsumer(kafkaConsumer)
        ServiceProviderInstance.getInstance().setInstanceProviderHelper(providerHelper)
        return kafkaConsumer
    }

    private ArrayList<Map<TopicPartition, List<ConsumerRecord<String, String>>>> createRecordList() {
        List<Map<TopicPartition, List<ConsumerRecord<String, String>>>> recordList = new ArrayList<>()
        1.step(numOfRecordsPerConsumer * 2, 2) {
            recordList.add(getRecords(TOPIC, PARTITION, it, "4", "value"))
            recordList.add(getRecords(TOPIC, PARTITION, it + 1, "10", "value"))
        }
        recordList
    }

    def cleanupSpec() {
        ServiceProviderInstance.getInstance().setInstanceProviderHelper(new ServiceProviderInstanceHelper())

    }

    def cleanup() {
        providerHelper.setKafkaConsumer(null)
        providerHelper.overrideConsumer = false
    }

    private List<String> getAllRecords(EventCollectionListener eventListener) {
        List<String> records = new ArrayList<>()
        eventListener.getRecords().each {
            try {
                records.addAll(it.getRecords())
            } catch(Exception exception) {
                exception.printStackTrace()
            }

        }
        return records
    }

    private List<String> getAllEvents(EventCollectionListener eventListener) {
        List<String> events = new ArrayList<>()
        eventListener.getEvents().each {
            try {
                events.add(it)
            } catch(Exception exception) {
                exception.printStackTrace()
            }

        }
        return events
    }

    private Offsets getExpectedOffsets(long startOffset, long endOffset) {
        Map<TopicPartition, OffsetAndMetadata> expectedStartOffsets = new HashMap<>()
        Map<TopicPartition, OffsetAndMetadata> expectedEndOffsets = new HashMap<>()
        expectedStartOffsets.put(new TopicPartition(TOPIC, PARTITION), new OffsetAndMetadata(startOffset))
        expectedEndOffsets.put(new TopicPartition(TOPIC, PARTITION), new OffsetAndMetadata(endOffset))
        Offsets offsets = new Offsets()
        offsets.getStartOffsets().putAll(expectedStartOffsets)
        offsets.getEndOffsets().putAll(expectedEndOffsets)
        return offsets
    }
}
