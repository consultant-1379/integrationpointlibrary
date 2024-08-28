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

package com.ericsson.aia.ipl.translator

import static com.ericsson.aia.ipl.test.util.TestUtils.*

import spock.lang.Subject
import spock.lang.Unroll

import com.ericsson.aia.ipl.exception.InvalidIntegrationPointException
import com.ericsson.aia.ipl.model.IntegrationPoint


@Unroll
class JsonTranslatorSpec extends spock.lang.Specification {

    @Subject
    JsonFileTranslator jsonFileTranslator

    static final String INTEGRATION_POINT_VALID_FILES_PATH = "src/test/resources/valid/json/"
    static final String INTEGRATION_POINT_INVALID_FILES_PATH = "src/test/resources/invalid/json/"

    def setup() {
        jsonFileTranslator = new JsonFileTranslator()
    }

    def """An invalid JSON file name cannot be accepted as a parameter to locate IntegrationPoint definitions: '#invalidFileNameParameter' was
            correctly refused."""() {
        given: "an invalid JSON file name"
        String invalidFileName = invalidFileNameParameter

        when: "trying to translate the IntegrationPoint that corresponds to the invalid file name"
        jsonFileTranslator.translate(invalidFileName)

        then: "an IllegalArgumentException is thrown"
        thrown IllegalArgumentException

        where: "these are the invalid parameters"
        invalidFileNameParameter << [null, ""]
    }

    def "An invalid JSON file cannot be translated: #invalidJsonFileNameParameter was correctly refused."() {
        given: "the invalid JSON file name"
        String invalidJsonFileName = INTEGRATION_POINT_INVALID_FILES_PATH + invalidJsonFileNameParameter

        when: "trying to translate the invalid JSON file"
        jsonFileTranslator.translate(invalidJsonFileName)

        then: "an InvalidIntegrationPointException will be thrown"
        thrown InvalidIntegrationPointException

        where: "these are the invalid parameters"
        invalidJsonFileNameParameter << ["MALFORMED_INTEGRATION_POINT.json", "NON_EXISTING_INTEGRATION_POINT.json"]
    }

    def "Should correctly translate a well formed JSON file"() {
        given: "the expected translation result and the well formed JSON file name"
        IntegrationPoint expectedOutcome = createExpectedParsedIntegrationPoint()
        String wellFormedJsonFileName = INTEGRATION_POINT_VALID_FILES_PATH + "WELL_FORMED_INTEGRATION_POINT.json"

        when: "the well formed JSON file is translated"
        IntegrationPoint wellFormedIntegrationPoint = jsonFileTranslator.translate(wellFormedJsonFileName)

        then: "it must be correct "
        wellFormedIntegrationPoint == expectedOutcome
    }
}
