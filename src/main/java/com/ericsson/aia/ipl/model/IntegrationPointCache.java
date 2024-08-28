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

package com.ericsson.aia.ipl.model;

import static com.ericsson.aia.ipl.util.Utils.standardizeName;
import static com.ericsson.aia.ipl.util.ValidationUtils.validateIntegrationPoint;
import static com.ericsson.aia.ipl.util.ValidationUtils.validateIntegrationPointName;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IntegrationPointCache holds a map of all IntegrationPoints already used in the system.
 */
public class IntegrationPointCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationPointCache.class);
    private static Map<String, IntegrationPoint> integrationPointMap = new HashMap<>();

    private IntegrationPointCache() {}

    /**
     * Puts IntegrationPoint in cache.
     * Note: as the cache is name-based, an IntegrationPoint should have an unique name, regardless of the source. For example, if there is a file
     * called AN_INTEGRATION_POINT.json, there shouldn't be another one called AN_INTEGRATION_POINT.xml. In this case, the last IntegrationPoint
     * read will overwrite the previous one.
     *
     * @param integrationPointName
     *            the IntegrationPoint name
     * @param integrationPoint
     *            the IntegrationPoint
     */
    public static synchronized void put(final String integrationPointName, final IntegrationPoint integrationPoint) {
        validateParameters(integrationPointName, integrationPoint);
        final String sanitizedIntegrationPointName = standardizeName(integrationPointName);
        integrationPointMap.put(sanitizedIntegrationPointName, integrationPoint);
        LOGGER.info("Added or updated the IntegrationPoint '{}' to the cache", sanitizedIntegrationPointName);
    }

    private static void validateParameters(final String integrationPointName, final IntegrationPoint integrationPoint) {
        validateIntegrationPointName(integrationPointName);
        validateIntegrationPoint(integrationPoint);
    }

    /**
     * Clear the cache.
     */
    public static void clear() {
        integrationPointMap.clear();
        LOGGER.info("Cleared IntegrationPoint cache.");
    }

    /**
     * Checks if the cache is empty.
     *
     * @return true, if integrationPointMap is empty.
     */
    public static boolean isEmpty() {
        return integrationPointMap.isEmpty();
    }

    /**
     * Gets an IntegrationPoint using its name as the cache key.
     *
     * @param integrationPointName
     *            the IntegrationPoint name.
     * @return the IntegrationPoint
     */
    public static IntegrationPoint get(final String integrationPointName) {
        validateIntegrationPointName(integrationPointName);
        final IntegrationPoint integrationPoint = integrationPointMap.get(standardizeName(integrationPointName));
        return integrationPoint;
    }

}
