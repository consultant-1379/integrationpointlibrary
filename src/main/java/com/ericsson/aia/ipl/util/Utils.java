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

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

import static com.ericsson.aia.ipl.model.Constants.DOT;
import static com.ericsson.aia.ipl.model.Constants.MAXIMUM_VALID_PORT_VALUE;
import static com.ericsson.aia.ipl.model.Constants.UTF_8_ENCODING_TYPE;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;

import com.ericsson.aia.ipl.model.Property;

/**
 * Application utilities.
 */
public class Utils {

    private Utils() {}

    /**
     * Extracts the IntegrationPoint name from its file name.
     *
     * @param fileName
     *            the IntegrationPoint file name
     * @return the IntegrationPoint name
     */
    public static String getIntegrationPointNameFromFileName(final String fileName) {
        if (isBlank(fileName)) {
            return EMPTY;
        }
        return fileName.substring(0, fileName.lastIndexOf(DOT));
    }

    /**
     * Convert an array of properties in a map of properties.
     *
     * @param properties
     *            array of properties to be converted.
     * @return the map of properties equivalent to the array passed as parameter.
     */
    public static Map<String, Object> convertToPropertiesMap(final Property[] properties) {
        final Map<String, Object> propertyMap = new HashMap<String, Object>();
        if (properties == null) {
            return propertyMap;
        }
        for (final Property property : properties) {
            propertyMap.put(property.getName(), property.getValue());
        }
        return propertyMap;
    }

    /**
     * Convert property array to properties.
     *
     * @param propertyArray
     *            the property array
     * @return the properties
     */
    public static Properties toProperties(final Property[] propertyArray) {
        final Properties properties = new Properties();
        if (propertyArray != null && propertyArray.length != 0) {
            for (final Property property : propertyArray) {
                properties.put(property.getName(), property.getValue());
            }
        }
        return properties;
    }

    /**
     * Gets all the properties as a comma separated String.
     *
     * @param properties
     *            the properties
     * @return the properties as a string
     */
    public static String getPropertiesAsString(final Properties properties) {
        final StringBuilder builder = new StringBuilder("");
        final Iterator<Object> iterator = properties.keySet().iterator();
        builder.append("Properties: [");
        while (iterator.hasNext()) {
            final String keyValue = String.valueOf(iterator.next());
            builder.append(keyValue + " : " + properties.getProperty(keyValue));
            if (iterator.hasNext()) {
                builder.append(" , ");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Standardize a name.
     * The method removes spaces and convert all letters to upper case.
     *
     * @param name
     *            the name.
     * @return sanitized name.
     */
    public static String standardizeName(final String name) {
        if (isBlank(name)) {
            return EMPTY;
        }
        return name.trim().toUpperCase();
    }

    /**
     * Read a text file contents.
     *
     * @param fileName
     *            the file name to be read, including full path.
     * @return a List<String> of the lines of the file.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static List<String> readFileContents(final String fileName) throws IOException {
        final File file = new File(fileName);
        return FileUtils.readLines(file, UTF_8_ENCODING_TYPE);
    }

    /**
     * Gets the lines of a text file that start with the prefix parameter.
     *
     * @param fileName
     *            the text file name to search for lines.
     * @param prefix
     *            the case-sensitive prefix to match the beginning of the line.
     * @return List of lines starting with prefix.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static List<String> getFileLinesStartingWith(final String fileName, final String prefix) throws IOException {
        final List<String> fileLines = readFileContents(fileName);
        return searchForLinesStartingWith(prefix, fileLines);
    }

    private static List<String> searchForLinesStartingWith(final String prefix, final List<String> fileLines) {
        final List<String> lines = new ArrayList<>();
        for (final String line : fileLines) {
            if (line.startsWith(prefix)) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * Append a value to the end of all array elements.
     *
     * @param array
     *            the array to be modified.
     * @param value
     *            the value to be added after each element.
     * @return the modified array.
     */
    public static String[] appendValueToArrayElements(final String[] array, final String value) {
        final String[] arrayModified = new String[array.length];
        for (int index = 0; index < array.length; index++) {
            arrayModified[index] = array[index] + value;
        }
        return arrayModified;
    }

    /**
     * Checks if the port value is valid.
     *
     * @param portValue
     *            the port value to be checked.
     * @return true, if the port value is valid.
     */
    public static boolean isValidPortValue(final int portValue) {
        return portValue > 0 && portValue <= MAXIMUM_VALID_PORT_VALUE;
    }

    /**
     * Checks if the port value is valid.
     *
     * @param portValue
     *            the port value to be checked.
     * @return true, if the port value is valid.
     */
    public static boolean isValidPortValue(final String portValue) {
        final int portValueAsInteger = Integer.valueOf(portValue);
        return isValidPortValue(portValueAsInteger);
    }

}
