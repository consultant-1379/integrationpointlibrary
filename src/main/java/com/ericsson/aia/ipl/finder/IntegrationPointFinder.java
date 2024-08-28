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

package com.ericsson.aia.ipl.finder;

import com.ericsson.aia.ipl.model.IntegrationPoint;

/**
 * The IntegrationPointFinder is responsible for locating the IntegrationPoint from its source, as described by the URI, and convert it to a POJO.
 */
public interface IntegrationPointFinder {

    /**
     * Find the solicited IntegrationPoint.
     *
     * @param uri
     *            the URI representing the source of the IntegrationPoints.
     * @param name
     *            the name of the IntegrationPoint.
     * @return the IntegrationPoint in a POJO format.
     */
    IntegrationPoint find(String uri, String name);
}
