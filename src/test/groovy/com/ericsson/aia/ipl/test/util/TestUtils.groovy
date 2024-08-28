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
package com.ericsson.aia.ipl.test.util

import com.ericsson.aia.ipl.model.*
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelRepoBasedModelMetaInformation


class TestUtils {

    /**
     * This method defines the location of the local modelRepo, to be used in the Model Service tests.
     */
    static void defineTestValueForModelRepoHome(){
        final String modelRepoPath = "target/repo.xml"
        System.setProperty ModelRepoBasedModelMetaInformation.MODEL_REPO_PATH_PROPERTY, modelRepoPath
    }

    /**
     * This method defines the Kafka brokers addresses and port, for test purposes..
     */
    static void defineTestValuesForKafkaBrokersAddressesModifier(String kafkaBrokersAddressesFile, String port) {
        System.setProperty Constants.KAFKA_BROKERS_ADDRESSES_FILE_PROPERTY_NAME, kafkaBrokersAddressesFile
        System.setProperty Constants.KAFKA_BROKERS_PORT_PROPERTY_NAME, port
    }


    /**
     * This method creates a valid IntegrationPoint.
     */
    static IntegrationPoint createValidIntegrationPoint() {
        createValidIntegrationPoint("localhost:59002")
    }

    /**
     * This method creates a valid IntegrationPoint.
     *
     * @param kafkaBrokerAddress
     *                           the address of Kafka Broker to be used in the IntegrationPoint.
     */
    static IntegrationPoint createValidIntegrationPoint(String kafkaBrokerAddress) {
        def integrationPointProperties = [
            new Property(name: "bootstrap.servers", value: kafkaBrokerAddress),
            new Property(name: "key.serializer", value: "org.apache.kafka.common.serialization.StringSerializer"),
            new Property(name: "value.serializer", value: "org.apache.kafka.common.serialization.StringSerializer"),
            new Property(name: "auto.create.topics.enable", value: "true")
        ]

        def expectedIntegrationPoint = new IntegrationPoint(type: "PUBLISHER", properties: integrationPointProperties, destinations: [new Destination(name: "test", properties: [], events: [])])
    }

    /**
     * This method creates the IntegrationPoint that is expected as the result of parsing a XML or JSON file.
     */
    static private IntegrationPoint createExpectedParsedIntegrationPoint() {
        def integrationPointProperties = [
            new Property(name: "bootstrap.servers", value: "localhost:59002"),
            new Property(name: "key.serializer", value: "org.apache.kafka.common.serialization.StringSerializer"),
            new Property(name: "value.serializer", value: "org.apache.kafka.common.serialization.StringSerializer"),
            new Property(name: "auto.create.topics.enable", value: "true")
        ]

        def undecodedDestinationProperties = [
            new Property(name: "partition_count", value: "1"),
            new Property(name: "partition_class", value: "org.ericsson.aia.common.partition.Partitioner")
        ]

        def undecodedDestinationEvents = ["EventId1", "EventId2", "EventId3"]

        def decodedDestinationProperties = [new Property(name: "partition_count", value: "1")]

        def decodedDestinationEvents = ["EventId1", "EventId5", "EventId6", "EventId7"]

        def rawDestinationEvents = ["1", "3", "5", "99"]

        def expectedIntegrationPoint = new IntegrationPoint(type: "PUBLISHER", properties: integrationPointProperties, destinations: [
            new Destination(name: "undecoded", properties: undecodedDestinationProperties, events:undecodedDestinationEvents),
            new Destination(name: "decoded", properties: decodedDestinationProperties, events: decodedDestinationEvents),
            new Destination(name: "raw", properties:decodedDestinationProperties, events: rawDestinationEvents)
        ])
    }
}
