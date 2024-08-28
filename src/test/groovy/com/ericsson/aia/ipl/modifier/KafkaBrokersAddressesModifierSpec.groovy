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
package com.ericsson.aia.ipl.modifier

import static com.ericsson.aia.ipl.model.Constants.*
import static com.ericsson.aia.ipl.test.util.TestUtils.createValidIntegrationPoint
import static com.ericsson.aia.ipl.test.util.TestUtils.defineTestValuesForKafkaBrokersAddressesModifier

import spock.lang.Subject
import spock.lang.Unroll

import com.ericsson.aia.ipl.exception.KafkaBrokerAddressesNotResolvedException
import com.ericsson.aia.ipl.model.Constants
import com.ericsson.aia.ipl.model.IntegrationPoint

@Unroll
class KafkaBrokersAddressesModifierSpec  extends spock.lang.Specification {

    static final String KAFKA_BROKER_ADDRESSES_FILE_FOR_TEST = "src/test/resources/global.properties.sample"
    static final String KAFKA_BROKER_ADDRESSES_INVALID_FILE_FOR_TEST = "src/test/resources/invalid.global.properties.sample"
    static final String KAFKA_BROKER_PORT_FOR_TESTS = 59002

    @Subject
    KafkaBrokersAddressesModifier kafkaBrokersAddressesModifier

    def "Should modify the value of bootstrap.servers property in an IntegrationPoint only if there is a placeholder for the new value: #description" () {
        given: """the correct configuration for the Kafka brokers, a KafkaBrokersAddressesModifier and an IntegrationPoint whose bootstrap.servers
                property's value has a placeholder for modification"""
        defineTestValuesForKafkaBrokersAddressesModifier KAFKA_BROKER_ADDRESSES_FILE_FOR_TEST, KAFKA_BROKER_PORT_FOR_TESTS
        kafkaBrokersAddressesModifier = new KafkaBrokersAddressesModifier(KAFKA_BROKER_ADDRESSES_FILE_FOR_TEST)
        IntegrationPoint integrationPoint = integrationPointToBeTested

        when: "modifying it"
        kafkaBrokersAddressesModifier.modify integrationPoint

        then: "the IntegrationPoint bootstrap.servers property value should be the modified one"
        integrationPoint.properties.getProperty(PROPERTY_NAME_FOR_KAFKA_BROKERS_ADDRESSES) == expectedOutcome

        where: "these are the scenarios"
        description |integrationPointToBeTested | expectedOutcome
        "IntegrationPoint has placeholder and should be modified" | createValidIntegrationPoint(PLACEHOLDER_FOR_KAFKA_BROKERS_ADDRESSES_IN_MODELS) | "localhost:59002"
        "IntegrationPoint has not placeholder and should not be modified" | createValidIntegrationPoint("should_not_modify") | "should_not_modify"
    }

    @Unroll
    def "Should throw an #expectedException if the path for the file '#fileName' containing the Kafka brokers addresses is invalid or wrong" () {
        given: """A location for the file containing the Kafka brokers addresses, a KafkaBrokersAddressesModifier and an IntegrationPoint
                whose bootstrap.servers property's value has a placeholder for modification"""
        defineTestValuesForKafkaBrokersAddressesModifier fileName, KAFKA_BROKER_PORT_FOR_TESTS
        kafkaBrokersAddressesModifier = new KafkaBrokersAddressesModifier(fileName)
        IntegrationPoint integrationPoint = createValidIntegrationPoint(PLACEHOLDER_FOR_KAFKA_BROKERS_ADDRESSES_IN_MODELS)

        when: "modifying it"
        kafkaBrokersAddressesModifier.modify integrationPoint

        then: "an exception is thrown"
        thrown expectedException

        where: "the test inputs are"
        fileName                                        || expectedException
        "invalid_file"                                  || KafkaBrokerAddressesNotResolvedException
        KAFKA_BROKER_ADDRESSES_INVALID_FILE_FOR_TEST    || KafkaBrokerAddressesNotResolvedException
    }

}
