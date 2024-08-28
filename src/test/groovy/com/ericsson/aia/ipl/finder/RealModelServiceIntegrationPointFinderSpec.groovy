package com.ericsson.aia.ipl.finder

import static com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants.EXT_INTEGRATION_POINT_LIBRARY
import static com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants.GLOBAL_MODEL_NAMESPACE

import spock.lang.Unroll

import com.ericsson.aia.ipl.model.IntegrationPoint
import com.ericsson.aia.ipl.model.IntegrationPointType
import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.providers.custom.model.ModelPattern
import com.ericsson.cds.cdi.support.providers.custom.model.RealModelServiceProvider
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo

class RealModelServiceIntegrationPointFinderSpec extends CdiSpecification{

    @ObjectUnderTest
    ModelServiceFinder modelServiceFinder

    private static final String MODEL_URI = "model://"
    private static filteredModels = [new ModelPattern("ext_integrationpointlibrary", ".*", ".*", ".*")]

    private static RealModelServiceProvider realModelServiceProvider = new RealModelServiceProvider(filteredModels)

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {
        injectionProperties.addInjectionProvider(realModelServiceProvider)
    }

    @Unroll
    def "The IntegrationPoint named '#integrationPointName' was succesfully located in Model Service and translated to an IntegrationPoint"() {

        when: "asking to load a valid IntegrationPoint from model service"
        IntegrationPoint integrationPoint = modelServiceFinder.find MODEL_URI, integrationPointName

        then: "the IntegrationPoint should be correctly loaded"
        integrationPoint != null
        integrationPoint.name == integrationPointName
        integrationPoint.type == type
        integrationPoint.properties.size() == propertiesSize
        integrationPoint.destinations.size() == destinationsSize

        where: "some of their IntegrationPoints are"
        integrationPointName                         | type                            | propertiesSize | destinationsSize
        "EsnLteRanApollo-ForwarderSubscriberDecoded" | IntegrationPointType.SUBSCRIBER | 8              | 1
        "EsnStreamTerminatorPublisherRaw"            | IntegrationPointType.PUBLISHER  | 10             | 1
        "rawKafkaSubscriber"                         | IntegrationPointType.SUBSCRIBER | 6              | 1
        "avroKafkaPublisher"                         | IntegrationPointType.PUBLISHER  | 9              | 1
    }

    @Unroll
    def "The IntegrationPoint named '#integrationPointName' was succesfully located in Model Service and Destination EventId List not Empty"() {

        when: "asking to load a valid IntegrationPoint from model service"
        IntegrationPoint integrationPoint = modelServiceFinder.find MODEL_URI, integrationPointName

        then: "the IntegrationPoint destination eventIds are correctly loaded"
        integrationPoint != null
        integrationPoint.name == integrationPointName
        integrationPoint.destinations[0].events.size() == destinationEventSize

        where: "some of their IntegrationPoints are"
        integrationPointName                         | destinationEventSize
        "EsnLteRanApollo-ForwarderSubscriberDecoded" | 41
        "EsnStreamTerminatorPublisherRaw"            | 85
    }

    @Unroll
    def "The IntegrationPoint named '#integrationPointName' was succesfully located in ModelService with Destination EventIds and EventMap not Empty"() {

        when: "asking to load a valid IntegrationPoint from model service"
        ModelInfo integrationPointModelInfo = new ModelInfo(EXT_INTEGRATION_POINT_LIBRARY, GLOBAL_MODEL_NAMESPACE, integrationPointName);
        IntegrationPoint integrationPoint = modelServiceFinder.find MODEL_URI, integrationPointName

        then: "the IntegrationPoint destination eventIds are correctly loaded"
        integrationPoint != null
        integrationPoint.name == integrationPointName
        integrationPoint.destinations[0].events.size() == destinationEventIdSize
        integrationPoint.eventMap.mapping.size() == eventMappingSize

        where: "some of their IntegrationPoints are"
        integrationPointName                         | destinationEventIdSize | eventMappingSize
        "EsnLteRanApollo-ForwarderSubscriberDecoded" | 41                     | 41
    }

    @Unroll
    def "The IntegrationPoint named '#integrationPointName' was succesfully located in Model Service and Destination property List not Empty" () {

        when: "asking to load a valid IntegrationPoint from model service"
        IntegrationPoint integrationPoint = modelServiceFinder.find MODEL_URI, integrationPointName

        then: "the IntegrationPoint destination properties are correctly loaded"
        integrationPoint != null
        integrationPoint.name == integrationPointName
        integrationPoint.destinations[0].properties.size() == destinationPropertiesSize

        where: "some of their IntegrationPoints are"
        integrationPointName | destinationPropertiesSize
        "rawKafkaSubscriber" | 2
        "avroKafkaPublisher" | 3
    }
}
