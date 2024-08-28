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

import com.ericsson.aia.ipl.model.IntegrationPoint;

/**
 * The IntegrationPointModifier purpose is to allow an IntegrationPoint to be modified after being created.
 */
public interface IntegrationPointModifier {

    /**
     * Modify the IntegrationPoint.
     *
     * @param integrationPoint
     *            the IntegrationPoint to be modified.
     */
    void modify(IntegrationPoint integrationPoint);

}
