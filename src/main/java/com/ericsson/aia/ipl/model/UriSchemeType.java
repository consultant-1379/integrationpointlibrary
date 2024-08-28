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

import static org.apache.commons.lang3.StringUtils.isBlank;

import static com.ericsson.aia.ipl.model.Constants.URI_DELIMITER;

import com.ericsson.aia.ipl.finder.FileSystemFinder;
import com.ericsson.aia.ipl.finder.IntegrationPointFinder;
import com.ericsson.aia.ipl.finder.ModelServiceFinder;

/**
 * Enum defining the available URI schemes. By definition, "each URI begins with a scheme name that refers to a specification for assigning
 * identifiers within that scheme." Examples of scheme, in the IPL context: local, model, etc. More information in
 * https://tools.ietf.org/html/rfc3986#section-1.1.1, https://tools.ietf.org/html/rfc3986#section-1.1.2 and
 * https://tools.ietf.org/html/rfc3986#section-3.1.
 */
public enum UriSchemeType {

    /** The local URI, meaning that the IntegrationPoints will be present in the file system. */
    LOCAL("local", new FileSystemFinder()),

    /** The Model Service URI, meaning that the IntegrationPoints will be read from the Model Service. */
    MODEL("model", new ModelServiceFinder());

    private static final String EMPTY_URI = "";
    private IntegrationPointFinder integrationPointFinder;
    private String uriScheme;

    /**
     * Instantiates a new URI type.
     *
     * @param uriScheme
     *            the URI scheme (as "local","modelservice", etc).
     * @param integrationPointFinder
     *            the IntegrationPointFinder associated with the URI.
     */
    UriSchemeType(final String uriScheme, final IntegrationPointFinder integrationPointFinder) {
        this.uriScheme = uriScheme;
        this.integrationPointFinder = integrationPointFinder;
    }

    private static String extractUriScheme(final String fullUri) {
        final int uriDelimiterPosition = fullUri.indexOf(URI_DELIMITER);
        final boolean isUriSchemeAbsent = uriDelimiterPosition < 0;
        if (isUriSchemeAbsent) {
            return checkIfUriRepresentsValidUriScheme(fullUri);
        }
        return fullUri.substring(0, uriDelimiterPosition);
    }

    private static String checkIfUriRepresentsValidUriScheme(final String fullUri) {
        if (checkIfUriSchemeIsValid(fullUri)) {
            return fullUri;
        }
        return EMPTY_URI;
    }

    /**
     * Gets the UriSchemeType from an URI.
     *
     * @param fullUri
     *            the full URI.
     * @return the UriSchemeType associated with the full URI.
     */
    public static UriSchemeType getUriSchemeTypeFromUri(final String fullUri) {
        final String uriScheme = extractUriScheme(fullUri);
        return valueOf(uriScheme.toUpperCase());
    }

    /**
     * Checks if a given URI has a valid scheme.
     *
     * @param fullUri
     *            the full URI.
     * @return true, if is valid URI scheme.
     */
    public static boolean isValidUriScheme(final String fullUri) {
        if (isBlank(fullUri)) {
            return false;
        }
        final String uriScheme = extractUriScheme(fullUri);
        return checkIfUriSchemeIsValid(uriScheme);
    }

    private static boolean checkIfUriSchemeIsValid(final String uriScheme) {
        for (final UriSchemeType uriSchemeType : UriSchemeType.values()) {
            if (uriScheme.equalsIgnoreCase(uriSchemeType.getUriScheme())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a String representation of the valid URIs accepted by the component.
     *
     * @return the string representation.
     */
    public static String printValidUris() {
        final String uriSeparator = ", ";
        String validUris = EMPTY_URI;
        for (final UriSchemeType uriSchemeType : UriSchemeType.values()) {
            validUris += uriSeparator + uriSchemeType.getUriScheme();
        }
        final String formattedValidUris = validUris.substring(uriSeparator.length());
        return formattedValidUris;
    }

    /**
     * Gets the IntegrationPointFinder associated with the URI.
     *
     * @return the IntegrationPointFinder.
     */
    public IntegrationPointFinder getIntegrationPointFinder() {
        return integrationPointFinder;
    }

    /**
     * Gets the URI scheme.
     *
     * @return the URI scheme
     */
    public String getUriScheme() {
        return uriScheme;
    }

}
