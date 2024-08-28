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

import com.ericsson.aia.ipl.model.IntegrationPoint

@Unroll
class XmlTranslatorSpec extends spock.lang.Specification {

    @Subject
    XmlFileTranslator xmlFileTranslator

    static final String INTEGRATION_POINT_VALID_FILES_PATH = "src/test/resources/valid/xml/"
    static final String INTEGRATION_POINT_INVALID_FILES_PATH = "src/test/resources/invalid/xml/"

    def setup() {
        xmlFileTranslator = new XmlFileTranslator()
    }

    def "Should correctly translate a valid XML file"() {
        given: "the expected translate result and the valid XML file name"
        IntegrationPoint expectedOutcome = createExpectedParsedIntegrationPoint()
        String validXmlFileName = INTEGRATION_POINT_VALID_FILES_PATH + "VALID_INTEGRATION_POINT.xml"

        when: "the valid XML file is translated"
        IntegrationPoint validIntegrationPoint = xmlFileTranslator.translate(validXmlFileName)

        then: "it must be correct "
        validIntegrationPoint == expectedOutcome
    }

    def """An invalid XML file name cannot be accepted as a parameter to locate IntegrationPoint definitions: '#invalidFileNameParameter' was
            correctly refused."""() {
        given: "an invalid XML file name"
        String invalidFileName = invalidFileNameParameter

        when: "trying to translate the IntegrationPoint that corresponds to the invalid file name"
        xmlFileTranslator.translate(invalidFileName)

        then: "an IllegalArgumentException is thrown"
        thrown IllegalArgumentException

        where: "these are the invalid parameters"
        invalidFileNameParameter << [null, ""]
    }

    def "An invalid XML file cannot be translated: #invalidXmlFileNameParameter was correctly refused."() {
        given: "the invalid XML file name"
        String invalidXmlFileName = INTEGRATION_POINT_INVALID_FILES_PATH + invalidXmlFileNameParameter

        when: "trying to translate the invalid XML file"
        IntegrationPoint expectedOutcome = xmlFileTranslator.translate(invalidXmlFileName)

        then: "an empty IntegrationPoint is returned"
        expectedOutcome == IntegrationPoint.INVALID_INTEGRATION_POINT

        where: "these are the invalid parameters"
        invalidXmlFileNameParameter << ["INVALID_INTEGRATION_POINT.xml", "NON_EXISTING_INTEGRATION_POINT.xml"]
    }
}
