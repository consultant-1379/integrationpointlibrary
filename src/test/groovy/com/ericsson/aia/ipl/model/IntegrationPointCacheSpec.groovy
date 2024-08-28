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
package com.ericsson.aia.ipl.model

import spock.lang.Unroll

import com.ericsson.aia.ipl.exception.InvalidIntegrationPointException
import com.ericsson.aia.ipl.test.util.TestUtils


@Unroll
class IntegrationPointCacheSpec extends spock.lang.Specification {

    def setup() {
        IntegrationPointCache.clear()
    }

    def "The cache cannot accept an invalid IntegrationPoint name as parameter: '#invalidIntegrationPointNameParameter' was correctly refused."() {
        given: "an invalid IntegrationPoint name and an IntegrationPoint"
        String invalidIntegrationPointName = invalidIntegrationPointNameParameter
        IntegrationPoint integrationPoint = new IntegrationPoint()

        when: "trying to add an IntegrationPoint in the IntegrationPointCache"
        IntegrationPointCache.put invalidIntegrationPointName, integrationPoint

        then: "an IllegalArgumentException is thrown"
        thrown IllegalArgumentException

        where: "these are the invalid parameters"
        invalidIntegrationPointNameParameter  << [null, ""]
    }

    def "Cannot add a null IntegrationPoint  to IntegrationPointCache"() {
        given: "an IntegrationPoint name and a null IntegrationPoint"
        String integrationPointName = "integrationPointName"
        IntegrationPoint integrationPoint = null

        when: "trying to add an IntegrationPoint in the IntegrationPointCache"
        IntegrationPointCache.put integrationPointName, integrationPoint

        then: "an InvalidIntegrationPointException is thrown"
        thrown InvalidIntegrationPointException
    }

    def "Should add a valid IntegrationPoint to IntegrationPointCache"() {
        given: "an IntegrationPoint name and an IntegrationPoint"
        String integrationPointName = "integrationPointName"
        IntegrationPoint integrationPoint = TestUtils.createValidIntegrationPoint()

        when: "trying to add an IntegrationPoint in the IntegrationPointCache"
        IntegrationPointCache.put integrationPointName, integrationPoint

        then: "the IntegrationPoint should be added to the IntegrationPointCache"
        IntegrationPointCache.get(integrationPointName) == integrationPoint
    }

    def "Should update a valid IntegrationPoint to IntegrationPointCache"() {
        given: "an IntegrationPoint name and two different IntegrationPoints"
        String integrationPointName = "integrationPointName"
        IntegrationPoint originalIntegrationPoint = TestUtils.createValidIntegrationPoint("kafkaBroker1:59002")
        IntegrationPoint updatedIntegrationPoint = TestUtils.createValidIntegrationPoint("kafkaBroker2:59002")

        when: "adding the IntegrationPoint first version in the IntegrationPointCache"
        IntegrationPointCache.put integrationPointName, originalIntegrationPoint

        and: "later adding a new version of the IntegrationPoint with the same name"
        IntegrationPointCache.put integrationPointName, updatedIntegrationPoint

        then: "the IntegrationPointCache should have been correctly updated"
        IntegrationPointCache.get(integrationPointName) != originalIntegrationPoint
        IntegrationPointCache.get(integrationPointName) == updatedIntegrationPoint
    }
}
