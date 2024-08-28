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

package com.ericsson.aia.ipl.modifier;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;

import static com.ericsson.aia.ipl.model.Constants.*;
import static com.ericsson.aia.ipl.model.Property.PROPERTY_NON_EXISTENT;
import static com.ericsson.aia.ipl.util.Utils.appendValueToArrayElements;
import static com.ericsson.aia.ipl.util.Utils.getFileLinesStartingWith;
import static com.ericsson.aia.ipl.util.Utils.isValidPortValue;
import static com.ericsson.aia.ipl.util.ValidationUtils.arePropertiesNull;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.exception.KafkaBrokerAddressesNotResolvedException;
import com.ericsson.aia.ipl.model.IntegrationPoint;
import com.ericsson.aia.ipl.model.Property;

/**
 * KafkaBrokersAddressesModifier contains the logic to replace the "bootstrap.servers" property from the IntegrationPoints that have the specific
 * placeholder for this, "${kafkaBrokers}".
 */
public class KafkaBrokersAddressesModifier implements IntegrationPointModifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaBrokersAddressesModifier.class);

    private static final int KAFKA_BROKERS_ADDRESSES_CONTENTS_INDEX = 1;
    private final String kafkaBrokersAddressesFile;
    private final String kafkaBrokersPort;

    /**
     * Instantiates a new default ModelServiceFinder.
     */
    public KafkaBrokersAddressesModifier() {
        this(System.getProperty(KAFKA_BROKERS_ADDRESSES_FILE_PROPERTY_NAME, DEFAULT_KAFKA_BROKERS_ADDRESSES_FILE));
    }

    /**
     * Instantiates a new default ModelServiceFinder.
     * @param globalPropertiesFile
     *            the path of the file that contains the Kafka brokers addresses.
     */
    public KafkaBrokersAddressesModifier(final String globalPropertiesFile) {
        kafkaBrokersAddressesFile = globalPropertiesFile;
        kafkaBrokersPort = System.getProperty(KAFKA_BROKERS_PORT_PROPERTY_NAME, DEFAULT_KAFKA_BROKERS_PORT);
        validateArguments(kafkaBrokersAddressesFile, kafkaBrokersPort);
    }

    private void validateArguments(final String kafkaBrokersAddressesFile, final String kafkaBrokersPort) {
        if (isBlank(kafkaBrokersAddressesFile)) {
            throw new IllegalArgumentException(
                    "The argument kafkaBrokersAddressesFile received was not valid. Value received was: " + kafkaBrokersAddressesFile);
        }
        if (isBlank(kafkaBrokersAddressesFile) || !isValidPortValue(kafkaBrokersPort)) {
            throw new IllegalArgumentException(
                    "The argument kafkaBrokersPort must represent a valid port number. Value received was: " + kafkaBrokersPort);
        }
    }

    /**
     * Change the value of the property "bootstrap.servers" of the IntegrationPoint, if it has the placeholder "${kafkaBrokers}" as the value.
     *
     * @param integrationPoint
     *            the integration point
     */
    @Override
    public void modify(final IntegrationPoint integrationPoint) {
        if (isModifierApplicable(integrationPoint)) {
            reconfigureBootstrapServersProperty(integrationPoint);
        }
    }

    private boolean isModifierApplicable(final IntegrationPoint integrationPoint) {
        return !arePropertiesNull(integrationPoint) && isBootstrapProperyMarkedForModification(integrationPoint);
    }

    private boolean isBootstrapProperyMarkedForModification(final IntegrationPoint integrationPoint) {
        final Property property = integrationPoint.getProperty(PROPERTY_NAME_FOR_KAFKA_BROKERS_ADDRESSES);
        return property != PROPERTY_NON_EXISTENT && PLACEHOLDER_FOR_KAFKA_BROKERS_ADDRESSES_IN_MODELS.equals(property.getValue());
    }

    private void reconfigureBootstrapServersProperty(final IntegrationPoint integrationPoint) {
        final String bootstrapServersPropertyValue = defineNewValueForBootstrapServersProperty();
        integrationPoint.replacePlaceHolderForValueInProperty(PROPERTY_NAME_FOR_KAFKA_BROKERS_ADDRESSES, bootstrapServersPropertyValue,
                PLACEHOLDER_FOR_KAFKA_BROKERS_ADDRESSES_IN_MODELS);
    }

    private String defineNewValueForBootstrapServersProperty() {
        final String[] kafkaBrokerAddresses = retrieveKafkaBrokerAddresses();
        final String[] kafkaAddressesWithPort = appendValueToArrayElements(kafkaBrokerAddresses, COLON + kafkaBrokersPort);
        final String bootstrapServersPropertyValue = join(kafkaAddressesWithPort, COMMA);
        return bootstrapServersPropertyValue;
    }

    private String[] retrieveKafkaBrokerAddresses() {
        final String kafkaBrokerAddressesLine = getKafkaBrokerAddressesFromFile();
        return kafkaBrokerAddressesLine.split(COMMA);
    }

    private String getKafkaBrokerAddressesFromFile() {
        try {
            final List<String> kafkaLines = getFileLinesStartingWith(kafkaBrokersAddressesFile, KAKFA_LINE_PREFIX_IN_GLOBAL_PROPERTIES_FILE);
            final String kafkaLineContents = extractKafkaLineContent(kafkaLines);
            final String[] kafkaLineContentsSplitted = kafkaLineContents.split(GLOBAL_PROPERTIES_FILE_VALUE_SEPARATOR);
            return kafkaLineContentsSplitted[KAFKA_BROKERS_ADDRESSES_CONTENTS_INDEX];
        } catch (final IOException | IndexOutOfBoundsException exception) {
            final String exceptionMessage = "Could not find the Kafka brokers addresses in the file: '" + kafkaBrokersAddressesFile
                    + "' due to " + exception.getClass().getSimpleName() + ". Models will not be updated with the current Kafka brokers addresses.";
            LOGGER.error(exceptionMessage, exception);
            throw new KafkaBrokerAddressesNotResolvedException(exceptionMessage);
        }
    }

    private String extractKafkaLineContent(final List<String> kafkaLines) {
        final boolean contentIsInvalid = kafkaLines == null || kafkaLines.size() != 1;
        if (contentIsInvalid) {
            final String exceptionMessage =
                    "Either no entry or more than one entry was found for '" + KAKFA_LINE_PREFIX_IN_GLOBAL_PROPERTIES_FILE + "' in the file "
                            + kafkaBrokersAddressesFile
                            + ", when it should have only one occurrence. Models will not be updated with the current Kafka brokers addresses";
            LOGGER.error(exceptionMessage);
            throw new KafkaBrokerAddressesNotResolvedException(exceptionMessage);
        }
        return kafkaLines.get(0);
    }

}
