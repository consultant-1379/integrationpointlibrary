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


class IntegrationPointEventSubscriberSpec extends spock.lang.Specification {

    def "An IntegrationPointEventSubscriber must receive a not-null IntegrationPoint as parameter"() {
        given: "a null IntegrationPoint"
        IntegrationPoint integrationPoint = null

        when: "trying to construct an IntegrationPointEventSubscriber"
        new IntegrationPointEventSubscriber(integrationPoint)

        then: "an InvalidIntegrationPointException is thrown"
        thrown InvalidIntegrationPointException
    }

    def "An IntegrationPointEventSubscriber must not receive an IntegrationPoint without destinations as parameter"() {
        given: "an IntegrationPoint without destinations"
        IntegrationPoint integrationPoint = new IntegrationPoint()

        when: "trying to construct an IntegrationPointEventSubscriber"
        new IntegrationPointEventSubscriber(integrationPoint)

        then: "an InvalidIntegrationPointException is thrown"
        thrown InvalidIntegrationPointException
    }

    def "A null EventListener cannot be registered"() {
        given: "A valid IntegrationPoint, an IntegrationPointEventSubscriber and a null EventListener"
        IntegrationPoint integrationPoint = TestUtils.createValidIntegrationPoint()
        IntegrationPointEventSubscriber eventConsumer = new IntegrationPointEventSubscriber(integrationPoint)
        EventListener eventListener = null

        when: "trying to register an Event Listener"
        eventConsumer.registerEventListener eventListener

        then: "an IllegalArgumentException is thrown"
        thrown IllegalArgumentException
    }
}
