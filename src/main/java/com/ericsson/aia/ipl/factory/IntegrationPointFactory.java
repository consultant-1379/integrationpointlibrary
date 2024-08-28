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

package com.ericsson.aia.ipl.factory;

import static com.ericsson.aia.ipl.model.Constants.DEFAULT_KAFKA_BROKERS_ADDRESSES_FILE;
import static com.ericsson.aia.ipl.model.Constants.KAFKA_BROKERS_ADDRESSES_FILE_PROPERTY_NAME;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.aia.ipl.finder.IntegrationPointFinder;
import com.ericsson.aia.ipl.model.IntegrationPoint;
import com.ericsson.aia.ipl.model.IntegrationPointCache;
import com.ericsson.aia.ipl.model.UriSchemeType;
import com.ericsson.aia.ipl.modifier.IntegrationPointModifier;
import com.ericsson.aia.ipl.modifier.KafkaBrokersAddressesModifier;

/**
 * A factory for creating IntegrationPoint objects.
 */
public class IntegrationPointFactory {

    private final List<IntegrationPointModifier> modifiers;
    private final String uri;
    private final UriSchemeType uriSchemeType;
    private final IntegrationPointFinder integrationPointFinder;

    /**
     * Instantiates a new IntegrationPointFactory.
     *
     * @param uri
     *            the URI representing the source of the IntegrationPoints.
     */
    public IntegrationPointFactory(final String uri) {
        this(uri, System.getProperty(KAFKA_BROKERS_ADDRESSES_FILE_PROPERTY_NAME, DEFAULT_KAFKA_BROKERS_ADDRESSES_FILE));

    }

    /**
     * Instantiates a new IntegrationPointFactory.
     *
     * @param uri
     *            the URI representing the source of the IntegrationPoints.
     * @param globalPropertiesFile
     *            the path of the file that contains the Kafka brokers addresses.
     */
    public IntegrationPointFactory(final String uri, final String globalPropertiesFile) {
        this.uri = uri;
        uriSchemeType = UriSchemeType.getUriSchemeTypeFromUri(uri);
        integrationPointFinder = uriSchemeType.getIntegrationPointFinder();
        modifiers = defineModifiers(globalPropertiesFile);

    }

    private List<IntegrationPointModifier> defineModifiers(final String globalPropertiesFile) {
        final List<IntegrationPointModifier> modifiersList = new ArrayList<>();
        modifiersList.add(new KafkaBrokersAddressesModifier(globalPropertiesFile));
        return modifiersList;
    }

    /**
     * Creates an IntegrationPoint from its name.
     *
     * @param integrationPointName
     *            the IntegrationPoint name
     * @return the IntegrationPoint
     */
    public IntegrationPoint create(final String integrationPointName) {
        if (IntegrationPointCache.get(integrationPointName) == null) {
            final IntegrationPoint integrationPoint = integrationPointFinder.find(uri, integrationPointName);
            applyModifiers(integrationPoint);
            IntegrationPointCache.put(integrationPointName, integrationPoint);
        }
        return IntegrationPointCache.get(integrationPointName);
    }

    private void applyModifiers(final IntegrationPoint integrationPoint) {
        for (final IntegrationPointModifier modifier : modifiers) {
            modifier.modify(integrationPoint);
        }
    }

}
