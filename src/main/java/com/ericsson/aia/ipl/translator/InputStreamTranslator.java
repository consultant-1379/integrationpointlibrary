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

import static com.ericsson.aia.ipl.util.ValidationUtils.validateInputStream;

import java.io.InputStream;

import javax.xml.bind.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.model.IntegrationPoint;

/**
 * This class is responsible for parsing an input stream to an IntegrationPoint POJO.
 */
public class InputStreamTranslator implements IntegrationPointTranslator<InputStream> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputStreamTranslator.class);

    /**
     * Receives an input stream and converts it to an IntegrationPoint.
     *
     * @param inputStream
     *            the inputStream to be read.
     * @return an IntegrationPoint pojo. In case of any problem in the parsing, an empty IntegrationPoint (IntegrationPoint.INVALID_INTEGRATION_POINT)
     *         will be returned.
     */
    @Override
    public IntegrationPoint translate(final InputStream inputStream) {
        validateInputStream(inputStream);
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(IntegrationPoint.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (IntegrationPoint) jaxbUnmarshaller.unmarshal(inputStream);
        } catch (final JAXBException exception) {
            LOGGER.error("Exception thrown while trying to convert input stream to IntegrationPoint.", exception);
        }
        return IntegrationPoint.INVALID_INTEGRATION_POINT;
    }
}
