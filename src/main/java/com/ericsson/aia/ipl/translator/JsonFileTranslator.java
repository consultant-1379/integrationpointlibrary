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

import static com.ericsson.aia.ipl.util.ValidationUtils.validateIntegrationPoint;
import static com.ericsson.aia.ipl.util.ValidationUtils.validateIntegrationPointFileName;

import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.exception.InvalidIntegrationPointException;
import com.ericsson.aia.ipl.model.IntegrationPoint;
import com.google.gson.Gson;

/**
 * This class is responsible for parsing a JSON file in an IntegrationPoint POJO.
 */
public class JsonFileTranslator implements IntegrationPointTranslator<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileTranslator.class);

    /**
     * Receives a file name, retrieves the file associated with it and translate it to an IntegrationPoint POJO.
     *
     * @param fileName
     *            the file name of the JSON file to be read.
     * @return an IntegrationPoint POJO.
     * @throws InvalidIntegrationPointException
     *             when the file cannot be translated.
     */
    @Override
    public IntegrationPoint translate(final String fileName) {
        LOGGER.debug("Received filename '{}' to translate.", fileName);
        validateIntegrationPointFileName(fileName);
        final IntegrationPoint integrationPoint = translateFromJson(fileName);
        validateIntegrationPoint(integrationPoint);
        return integrationPoint;
    }

    private IntegrationPoint translateFromJson(final String fileName) {
        try {
            return new Gson().fromJson(new FileReader(fileName), IntegrationPoint.class);
        } catch (final Exception exception) {
            LOGGER.error("Exception thrown while trying to translate the file '{}'.", fileName, exception);
            throw new InvalidIntegrationPointException("Exception thrown while trying to translate the file '" + fileName + "'.", exception);
        }
    }

}
