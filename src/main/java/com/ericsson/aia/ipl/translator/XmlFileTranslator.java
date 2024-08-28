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

package com.ericsson.aia.ipl.translator;

import static com.ericsson.aia.ipl.util.ValidationUtils.validateIntegrationPointFileName;

import java.io.File;

import javax.xml.bind.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.model.IntegrationPoint;

/**
 * This class is responsible for parsing a xml file in an IntegrationPoint pojo.
 */
public class XmlFileTranslator implements IntegrationPointTranslator<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlFileTranslator.class);

    /**
     * Receives a file name, retrieves the file associated with it and parse it to generate an IntegrationPoint pojo.
     *
     * @param fileName
     *            the file name of the xml file to be read.
     * @return an IntegrationPoint pojo. In case of any problem in the parsing, an empty IntegrationPoint (IntegrationPoint.INVALID_INTEGRATION_POINT)
     *         will be returned.
     */
    @Override
    public IntegrationPoint translate(final String fileName) {
        LOGGER.debug("Received filename '{}' to be parsed.", fileName);
        validateIntegrationPointFileName(fileName);
        try {
            final File xmlFile = new File(fileName);
            final JAXBContext jaxbContext = JAXBContext.newInstance(IntegrationPoint.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (IntegrationPoint) jaxbUnmarshaller.unmarshal(xmlFile);
        } catch (final JAXBException exception) {
            LOGGER.error("Exception thrown while trying to parse the file '{}'. Logging the exception and keeping running.", fileName, exception);
        }
        return IntegrationPoint.INVALID_INTEGRATION_POINT;
    }

}
