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

package com.ericsson.aia.ipl.util;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.EventListener;
import com.ericsson.aia.ipl.exception.InvalidIntegrationPointException;
import com.ericsson.aia.ipl.exception.InvalidUriException;
import com.ericsson.aia.ipl.model.*;

/**
 * Validation utilities for the Application.
 */
public final class ValidationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtils.class);

    private ValidationUtils() {
    }

    private static boolean isArrayEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Validates EventListener.
     *
     * @param eventListener
     *            the EventListener.
     */
    public static void validateEventListener(@SuppressWarnings("rawtypes") final EventListener eventListener) {
        if (eventListener == null) {
            throw new IllegalArgumentException("EventListener must not be null");
        }
    }

    /**
     * Validates input stream
     *
     * @param inputStream
     *            the input stream
     * @throws IllegalArgumentException
     *             if the input stream is null.
     */
    public static void validateInputStream(final InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
    }

    /**
     * Validates IntegrationPoint.
     *
     * @param integrationPoint
     *            the IntegrationPoint
     * @throws InvalidIntegrationPointException
     *             if the IntegrationPoint is null or empty.
     */
    public static void validateIntegrationPoint(final IntegrationPoint integrationPoint) {
        if (integrationPoint == null) {
            throw new InvalidIntegrationPointException("IntegrationPoint must not be null");
        }
        if (integrationPoint.getType() == null) {
            throw new InvalidIntegrationPointException("IntegrationPoint must have a valid type. The valid types are: "
                    + IntegrationPointType.printValidTypes());
        }
        if (isArrayEmpty(integrationPoint.getDestinations())) {
            throw new InvalidIntegrationPointException("The IntegrationPoint informed has no destinations defined.");
        }
        if (integrationPoint.getProperties().isEmpty()) {
            throw new InvalidIntegrationPointException("The IntegrationPoint informed has no properties defined.");
        }

    }

    /**
     * Validates IntegrationPoint file name.
     *
     * @param integrationPointFileName
     *            the IntegrationPoint file name
     * @throws IllegalArgumentException
     *             if the IntegrationPoint file name is null or empty.
     */
    public static void validateIntegrationPointFileName(final String integrationPointFileName) {
        if (StringUtils.isBlank(integrationPointFileName)) {
            throw new IllegalArgumentException("IntegrationPoint file name must not be null or empty");
        }
    }

    /**
     * Validates IntegrationPoint name.
     *
     * @param integrationPointName
     *            the IntegrationPoint name
     * @throws IllegalArgumentException
     *             if the IntegrationPoint name is null or blank.
     */
    public static void validateIntegrationPointName(final String integrationPointName) {
        if (StringUtils.isBlank(integrationPointName) || integrationPointName.trim().isEmpty()) {
            throw new IllegalArgumentException("IntegrationPoint name must not be null or blank");
        }
    }

    /**
     * Checks to see if an IntegrationPoint`s properties are null.
     *
     * @param integrationPoint
     *            the IntegrationPoint
     * @return true, if the properties are null.
     */
    public static boolean arePropertiesNull(final IntegrationPoint integrationPoint) {
        return integrationPoint.getProperties() == null;
    }

    /**
     * Validate IntegrationPoint uri.
     *
     * @param uri
     *            the IntegrationPoint uri
     * @throws InvalidUriException
     *             if the IntegrationPoint uri is not of correct format.
     */
    public static void validateUri(final String uri) {
        if (!UriSchemeType.isValidUriScheme(uri)) {
            LOGGER.error("URI '{}' is not valid. A valid URI should start with one of these: {}", uri, UriSchemeType.printValidUris());
            throw new InvalidUriException("URI '" + uri + "' is not valid. A valid URI should start with one of these: "
                    + UriSchemeType.printValidUris());
        }
    }

}
