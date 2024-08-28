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
package com.ericsson.aia.ipl.finder

import static com.ericsson.aia.ipl.test.util.TestUtils.defineTestValueForModelRepoHome

import spock.lang.Subject
import spock.lang.Unroll

import com.ericsson.aia.ipl.exception.IntegrationPointNotFoundException
import com.ericsson.aia.ipl.model.IntegrationPoint


@Unroll
class ModelServiceIntegrationPointFinderSpec extends spock.lang.Specification {

    static final String MODEL_URI = "model://"

    def setupSpec() {
        defineTestValueForModelRepoHome()
    }

    @Subject
    ModelServiceFinder modelServiceFinder = new ModelServiceFinder()

    def "The IntegrationPoint named '#integrationPointName' was succesfully located in Model Service and translated to an IntegrationPoint"() {
        given: "a model URI"

        when: "asking to load a valid IntegrationPoint from model service"
        IntegrationPoint integrationPoint = modelServiceFinder.find MODEL_URI, integrationPointName

        then: "the IntegrationPoint should be correctly loaded"
        integrationPoint != null

        where: "some of their IntegrationPoints are"
        integrationPointName << ["KafkaSubscriber", "KafkaPublisher"]
    }

    def "Should throw an exception if the requested IntegrationPoint has not been deployed in the model repo"() {
        given: "an unknown IntegrationPoint"
        String unknownIntegrationPointName = "UNKNOWN_INTEGRATION_POINT"

        when: "asking to load an IntegrationPoint not deployed in the model repo"
        modelServiceFinder.find MODEL_URI, unknownIntegrationPointName

        then: "an IntegrationPointNotFoundException will be thrown"
        thrown IntegrationPointNotFoundException
    }
}
