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

import com.ericsson.aia.ipl.model.IntegrationPoint;

/**
 * IntegrationPoint translator receives a representation of an IntegrationPoint content in some format and translates it to the POJO format.
 *
 * @param <T>
 *            the type of the representation of the IntegrationPoint.
 */
public interface IntegrationPointTranslator<T> {

    /**
     * Translates a IntegrationPoint from some representation to a POJO.
     *
     * @param integrationPointContent
     *            the representation of the IntegrationPoint.
     * @return an IntegrationPoint POJO.
     */
    IntegrationPoint translate(T integrationPointContent);

}
