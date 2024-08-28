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


import spock.lang.Subject
import spock.lang.Unroll

import com.ericsson.aia.ipl.exception.InvalidIntegrationPointException
import com.ericsson.aia.ipl.model.IntegrationPoint


@Unroll
class FileSystemIntegrationPointFinderSpec extends spock.lang.Specification {

    @Subject
    FileSystemFinder fileSystemFinder = new FileSystemFinder()

    static final String LOCAL_URI = "local://"
    static final String INTEGRATION_POINT_VALID_FILES_URL = LOCAL_URI + "src/test/resources/valid/json/"
    static final String INTEGRATION_POINT_INVALID_FILES_URL = LOCAL_URI + "src/test/resources/invalid/json/"

    def "The IntegrationPoint named '#integrationPointName' was succesfully located in file system and translated to an IntegrationPoint"() {
        given: "a directory with valid IntegrationPoints"
        String integrationPointDirectory = INTEGRATION_POINT_VALID_FILES_URL

        when: "asking to load a IntegrationPoint from that directory"
        IntegrationPoint integrationPoint = fileSystemFinder.find integrationPointDirectory, integrationPointName

        then: "the IntegrationPoint should be correctly loaded"
        integrationPoint != null

        where: "some of their IntegrationPoints are"
        integrationPointName << [
            "EVENT_CONSUMING_INTEGRATION_POINT",
            "EVENT_PRODUCING_INTEGRATION_POINT",
            "WELL_FORMED_INTEGRATION_POINT"
        ]
    }

    def "A malformed IntegrationPoint will not be translated into an IntegrationPoint and it will cause a failure"() {
        given: "a directory and an invalid IntegrationPoint name that is inside it"
        String integrationPointDirectory = INTEGRATION_POINT_INVALID_FILES_URL
        String invalidIntegrationPoint = "MALFORMED_INTEGRATION_POINT"

        when: "asking to load the invalid IntegrationPoint"
        fileSystemFinder.find integrationPointDirectory, invalidIntegrationPoint

        then: "an InvalidIntegrationPointException will be thrown"
        thrown InvalidIntegrationPointException
    }
}
