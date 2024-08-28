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

import static com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants.*;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.exception.IntegrationPointNotFoundException;
import com.ericsson.aia.ipl.model.IntegrationPoint;
import com.ericsson.aia.ipl.translator.InputStreamTranslator;
import com.ericsson.aia.ipl.translator.IntegrationPointTranslator;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.ModelService;
import com.ericsson.oss.itpf.modeling.modelservice.ModelServiceImpl;
import com.ericsson.oss.itpf.modeling.modelservice.exception.UnknownModelException;

/**
 * This class is responsible for finding the IntegrationPoints contained in models in ModelService.
 */
public class ModelServiceFinder implements IntegrationPointFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelServiceFinder.class);
    private static final IntegrationPointTranslator<InputStream> inputStreamTranslator = new InputStreamTranslator();

    private final ModelService modelService = new ModelServiceImpl();

    @Override
    public IntegrationPoint find(final String uri, final String name) {
        LOGGER.info("URI received: {}", uri);

        try {
            final IntegrationPoint integrationPoint = loadIntegrationPointFromServiceModel(name);
            return integrationPoint;
        } catch (final UnknownModelException e) {
            final String exceptionMessage = "Integration point with the name '" + name + "' could not be found in deployed in model service";
            LOGGER.error(exceptionMessage, e);
            throw new IntegrationPointNotFoundException(exceptionMessage, e);
        }
    }

    private IntegrationPoint loadIntegrationPointFromServiceModel(final String name) {
        LOGGER.info("Loading IntegrationPoint '{}' from model service .", name);
        final ModelInfo integrationPointModelInfo = new ModelInfo(EXT_INTEGRATION_POINT_LIBRARY, GLOBAL_MODEL_NAMESPACE, name);
        final InputStream modelAsInputStream = modelService.getDirectAccess().getAsInputStream(integrationPointModelInfo);
        return inputStreamTranslator.translate(modelAsInputStream);
    }

}
