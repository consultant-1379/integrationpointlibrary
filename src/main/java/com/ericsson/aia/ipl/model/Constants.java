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

/**
 * Constants.
 */
public class Constants {

    /** Represents the XML_SUFFIX that identifies the IntegrationPoint files in XML format. */
    public static final String XML_SUFFIX = "_INTEGRATION_POINT.XML";

    /** Represents the JSON_SUFFIX that identifies the IntegrationPoint files in JSON format. */
    public static final String JSON_SUFFIX = "_INTEGRATION_POINT.JSON";

    /** Represents the DOT character. */
    public static final String DOT = ".";

    /** Represents the COLON character. */
    public static final String COLON = ":";

    /** Represents the COMMA character. */
    public static final String COMMA = ",";

    /** Represents the URI delimiter. */
    public static final String URI_DELIMITER = "://";

    /**
     * The system property containing the Kafka broker port to be used (normally, DEFAULT_KAFKA_BROKERS_PORT). This system property can
     * be changed to use a custom port for test purposes.
     */
    public static final String KAFKA_BROKERS_PORT_PROPERTY_NAME = "KAFKA_BROKERS_PORT";

    /** The default port to be used with the Kafka brokers. */
    public static final String DEFAULT_KAFKA_BROKERS_PORT = "9092";

    /**
     * The system property containing the Kafka broker addresses file path (normally, DEFAULT_KAFKA_BROKERS_ADDRESSES_FILE). This system property can
     * be changed to point to a custom file for test purposes.
     */
    public static final String KAFKA_BROKERS_ADDRESSES_FILE_PROPERTY_NAME = "KAFKA_BROKERS_ADDRESSES_FILE";

    /** The default file that contains the Kafka broker addresses in the current environment. */
    public static final String DEFAULT_KAFKA_BROKERS_ADDRESSES_FILE = "/ericsson/tor/data/global.properties";

    /** The beginning of the line in global.properties file that contains the Kafka broker addresses. */
    public static final String KAKFA_LINE_PREFIX_IN_GLOBAL_PROPERTIES_FILE = "kafka1=";

    /** The placeholder inside the models that will be replaced by the Kafka broker addresses in the current environment. */
    public static final String PLACEHOLDER_FOR_KAFKA_BROKERS_ADDRESSES_IN_MODELS = "${kafkaBrokers}";

    /** The name of the property that contains the Kafka broker addresses in the IntegrationPoint properties. */
    public static final String PROPERTY_NAME_FOR_KAFKA_BROKERS_ADDRESSES = "bootstrap.servers";

    /** The character that separates the value from the property in the global.properties file. */
    public static final String GLOBAL_PROPERTIES_FILE_VALUE_SEPARATOR = "=";

    /** The maximum valid port value for the Operational System. */
    public static final int MAXIMUM_VALID_PORT_VALUE = 65536;

    /** UTF-8 encoding type. */
    public static final String UTF_8_ENCODING_TYPE = "UTF-8";

    private Constants() {}
}
