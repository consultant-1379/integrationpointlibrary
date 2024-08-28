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
package com.ericsson.aia.ipl.factory

import spock.lang.Unroll

import com.ericsson.aia.ipl.EventPublisher
import com.ericsson.aia.ipl.EventSubscriber
import com.ericsson.aia.ipl.exception.InvalidUriException
import com.ericsson.aia.ipl.model.IntegrationPointCache


@Unroll
class EventServiceFactorySpec extends spock.lang.Specification {

    static final String LOCAL_URI = "local://"
    static final String INTEGRATION_POINT_VALID_FILES_URL = LOCAL_URI + "src/test/resources/valid/json/"
    static final String INTEGRATION_POINT_INVALID_FILES_URL = LOCAL_URI + "src/test/resources/invalid/json/"

    def setup() {
        IntegrationPointCache.clear()
    }

    def "EventServiceFactory cannot accept an invalid URI as parameter: '#invalidUriParameter' was correctly refused."() {
        given: "an invalid URI"
        String uri = invalidUriParameter

        when: "trying to construct a EventServiceFactory"
        new EventServiceFactory(uri)

        then: "an InvalidUriException is thrown"
        thrown InvalidUriException

        where: "these are the invalid parameters"
        invalidUriParameter << [null, "", "not_a_valid_uri"]
    }

    def "EventPublisher cannot be created using an invalid IntegrationPoint name: '#invalidIntegrationPointNameParameter' was correctly refused."() {
        given: "an invalid IntegrationPoint name and a EventServiceFactory"
        String invalidIntegrationPointName = invalidIntegrationPointNameParameter
        EventServiceFactory factory = new EventServiceFactory(INTEGRATION_POINT_VALID_FILES_URL)

        when: "trying to build an EventPublisher"
        factory.createEventPublisher invalidIntegrationPointName

        then: "an IllegalArgumentException is thrown"
        thrown IllegalArgumentException

        where: "these are the invalid parameters"
        invalidIntegrationPointNameParameter << [null, ""]
    }

    def "Only one instance of an EventPublisher can be created for an IntegrationPoint"() {
        given: "a EventServiceFactory, a valid IntegrationPoint name,  and a valid EventPublisher"
        EventServiceFactory factory = new EventServiceFactory(INTEGRATION_POINT_VALID_FILES_URL)
        String integrationPointName = "EVENT_PRODUCING_INTEGRATION_POINT"
        EventPublisher firstCreatedPublisher = factory.createEventPublisher(integrationPointName)

        when: "trying to create another EventPublisher with the same IntegrationPoint name"
        EventPublisher secondCreatedPublisher = factory.createEventPublisher(integrationPointName)

        then: "the same EventPublisher should be returned"
        firstCreatedPublisher == secondCreatedPublisher
    }


    def "EventSubscriber cannot be created using an invalid IntegrationPoint name: '#invalidIntegrationPointNameParameter' was correctly refused."() {
        given: "a EventServiceFactory and an invalid IntegrationPoint name"
        String invalidIntegrationPointName = invalidIntegrationPointNameParameter
        EventServiceFactory factory = new EventServiceFactory(INTEGRATION_POINT_VALID_FILES_URL)

        when: "trying to build an EventSubscriber"
        factory.createEventSubscriber(invalidIntegrationPointName)

        then: "an IllegalArgumentException is thrown"
        thrown IllegalArgumentException

        where: "these are the invalid parameters"
        invalidIntegrationPointNameParameter << [null, ""]
    }

    def "Only one instance of an EventSubscriber can be created for an IntegrationPoint"() {
        given: "an EventServiceFactory, a valid IntegrationPoint name,  and a valid EventSubscriber"
        EventServiceFactory factory = new EventServiceFactory(INTEGRATION_POINT_VALID_FILES_URL)
        String integrationPointName = "EVENT_CONSUMING_INTEGRATION_POINT"
        EventSubscriber firstCreatedSubscriber = factory.createEventSubscriber(integrationPointName)

        when: "trying to create another EventSubscriber with the same IntegrationPoint name"
        EventSubscriber secondCreatedSubscriber = factory.createEventSubscriber(integrationPointName)

        then: "the same EventSubscriber should be returned"
        firstCreatedSubscriber == secondCreatedSubscriber
    }
}
