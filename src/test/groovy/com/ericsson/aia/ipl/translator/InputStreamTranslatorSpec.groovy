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

import com.ericsson.aia.ipl.model.IntegrationPoint

class InputStreamTranslatorSpec extends spock.lang.Specification {

    @Subject
    InputStreamTranslator inputStreamTranslator

    static final String INTEGRATION_POINT_VALID_FILES_PATH = "src/test/resources/valid/xml/"
    static final String INTEGRATION_POINT_INVALID_FILES_PATH = "src/test/resources/invalid/xml/"


    def setup() {
        inputStreamTranslator = new InputStreamTranslator()
    }

    def "Should correctly translate a valid input stream"() {
        given: "the expected translation result and the valid input stream"
        IntegrationPoint expectedOutcome = createExpectedParsedIntegrationPoint()
        String validXmlFileName = INTEGRATION_POINT_VALID_FILES_PATH + "VALID_INTEGRATION_POINT.xml"
        InputStream inputStream = new FileInputStream(new File(validXmlFileName))

        when: "the input stream is translated"
        IntegrationPoint validIntegrationPoint = inputStreamTranslator.translate(inputStream)

        then: "it must be correct "
        validIntegrationPoint == expectedOutcome
    }

    def "Cannot accept a null input stream"() {
        given: "an null input stream"
        InputStream nullStream = null

        when: "trying to translate that input stream"
        inputStreamTranslator.translate(nullStream)

        then: "an IllegalArgumentException is thrown"
        thrown IllegalArgumentException
    }

    def "Cannot break trying to translate an invalid input stream"() {
        given: "an invalid input stream"
        String invalidXmlFileName = INTEGRATION_POINT_INVALID_FILES_PATH + "INVALID_INTEGRATION_POINT.xml"
        InputStream inputStream = new FileInputStream(new File(invalidXmlFileName))

        when: "trying to translate the invalid input steram"
        IntegrationPoint expectedOutcome = inputStreamTranslator.translate(inputStream)

        then: "an empty IntegrationPoint is returned"
        expectedOutcome == IntegrationPoint.INVALID_INTEGRATION_POINT
    }
}
