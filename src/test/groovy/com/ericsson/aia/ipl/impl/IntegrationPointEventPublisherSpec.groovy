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
package com.ericsson.aia.ipl.impl

import static org.junit.Assert.*

import com.ericsson.aia.ipl.exception.InvalidIntegrationPointException
import com.ericsson.aia.ipl.model.IntegrationPoint
import com.ericsson.aia.ipl.test.util.TestUtils


class IntegrationPointEventPublisherSpec extends spock.lang.Specification {

    def "An IntegrationPointEventPublisher must receive a non-null IntegrationPoint as parameter"() {
        given: "a null IntegrationPoint"
        IntegrationPoint integrationPoint = null

        when: "trying to construct an IntegrationPointEventPublisher"
        new IntegrationPointEventPublisher(integrationPoint)

        then: "an InvalidIntegrationPointException is thrown"
        thrown InvalidIntegrationPointException
    }

    def "An IntegrationPointEventPublisher must not receive an IntegrationPoint without destinations as parameter"() {
        given: "an IntegrationPoint without destinations"
        IntegrationPoint integrationPoint = new IntegrationPoint()

        when: "trying to construct an IntegrationPointEventPublisher"
        new IntegrationPointEventPublisher(integrationPoint)

        then: "an InvalidIntegrationPointException is thrown"
        thrown InvalidIntegrationPointException
    }

    def "An IntegrationPointEventPublisher canEventBePublished must return true if sent validEventId"() {
        given: "an IntegrationPoint with destinations, validEventId "
        IntegrationPoint integrationPoint = TestUtils.createExpectedParsedIntegrationPoint()
        IntegrationPointEventPublisher eventPublisher = new IntegrationPointEventPublisher(integrationPoint)
        String validEventId = 3

        when: "trying to construct an IntegrationPointEventPublisher"
        boolean result = eventPublisher.canEventBePublished(validEventId)

        then: "result should be equals true"
        result == true
    }

    def "An IntegrationPointEventPublisher canEventBePublished must return false if sent invalidEventId"() {
        given: "an IntegrationPoint with destinations, validEventId "
        IntegrationPoint integrationPoint = TestUtils.createExpectedParsedIntegrationPoint()
        IntegrationPointEventPublisher eventPublisher = new IntegrationPointEventPublisher(integrationPoint)
        String invalidEventId = 98

        when: "trying to construct an IntegrationPointEventPublisher"
        boolean result = eventPublisher.canEventBePublished(invalidEventId)

        then: "result should be equals false"
        result == false
    }
}
