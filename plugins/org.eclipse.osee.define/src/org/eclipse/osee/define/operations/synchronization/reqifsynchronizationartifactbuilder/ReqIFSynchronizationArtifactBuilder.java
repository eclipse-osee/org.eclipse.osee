/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.operations.synchronization.reqifsynchronizationartifactbuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.define.operations.synchronization.ForeignThingFamily;
import org.eclipse.osee.define.operations.synchronization.IsSynchronizationArtifactBuilder;
import org.eclipse.osee.define.operations.synchronization.RelationshipTerminal;
import org.eclipse.osee.define.operations.synchronization.SpecObjectSliderForeignThingFamily;
import org.eclipse.osee.define.operations.synchronization.SynchronizationArtifact;
import org.eclipse.osee.define.operations.synchronization.SynchronizationArtifactBuilder;
import org.eclipse.osee.define.operations.synchronization.SynchronizationArtifactParseException;
import org.eclipse.osee.define.operations.synchronization.SynchronizationArtifactSerializationException;
import org.eclipse.osee.define.operations.synchronization.forest.Grove;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.forest.denizens.NativeDataType;
import org.eclipse.osee.define.operations.synchronization.forest.denizens.NativeDataTypeKey;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierTypeGroup;
import org.eclipse.osee.framework.jdk.core.util.EnumBiConsumerMap;
import org.eclipse.osee.framework.jdk.core.util.EnumConsumerMap;
import org.eclipse.osee.framework.jdk.core.util.EnumFunctionMap;
import org.eclipse.osee.framework.jdk.core.util.ParameterArray;
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.AttributeDefinitionBoolean;
import org.eclipse.rmf.reqif10.AttributeDefinitionDate;
import org.eclipse.rmf.reqif10.AttributeDefinitionEnumeration;
import org.eclipse.rmf.reqif10.AttributeDefinitionInteger;
import org.eclipse.rmf.reqif10.AttributeDefinitionReal;
import org.eclipse.rmf.reqif10.AttributeDefinitionString;
import org.eclipse.rmf.reqif10.AttributeDefinitionXHTML;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.AttributeValueBoolean;
import org.eclipse.rmf.reqif10.AttributeValueDate;
import org.eclipse.rmf.reqif10.AttributeValueEnumeration;
import org.eclipse.rmf.reqif10.AttributeValueInteger;
import org.eclipse.rmf.reqif10.AttributeValueReal;
import org.eclipse.rmf.reqif10.AttributeValueString;
import org.eclipse.rmf.reqif10.AttributeValueXHTML;
import org.eclipse.rmf.reqif10.DatatypeDefinition;
import org.eclipse.rmf.reqif10.DatatypeDefinitionBoolean;
import org.eclipse.rmf.reqif10.DatatypeDefinitionDate;
import org.eclipse.rmf.reqif10.DatatypeDefinitionEnumeration;
import org.eclipse.rmf.reqif10.DatatypeDefinitionInteger;
import org.eclipse.rmf.reqif10.DatatypeDefinitionReal;
import org.eclipse.rmf.reqif10.DatatypeDefinitionString;
import org.eclipse.rmf.reqif10.DatatypeDefinitionXHTML;
import org.eclipse.rmf.reqif10.EnumValue;
import org.eclipse.rmf.reqif10.Identifiable;
import org.eclipse.rmf.reqif10.ReqIF;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.ReqIFHeader;
import org.eclipse.rmf.reqif10.SpecElementWithAttributes;
import org.eclipse.rmf.reqif10.SpecHierarchy;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.SpecObjectType;
import org.eclipse.rmf.reqif10.SpecRelation;
import org.eclipse.rmf.reqif10.SpecRelationType;
import org.eclipse.rmf.reqif10.SpecType;
import org.eclipse.rmf.reqif10.Specification;
import org.eclipse.rmf.reqif10.SpecificationType;
import org.eclipse.rmf.reqif10.serialization.ReqIF10ResourceFactoryImpl;
import org.eclipse.rmf.reqif10.serialization.ReqIF10ResourceImpl;
import org.xml.sax.SAXParseException;

/**
 * Implementation of the {@link SynchronizationArtifactBuilder} interface for building a ReqIF Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

@IsSynchronizationArtifactBuilder(artifactType = "reqif")
public class ReqIFSynchronizationArtifactBuilder implements SynchronizationArtifactBuilder {

   /**
    * A {@link Map} of {@link ForeignThingFamily} objects for the foreign Specifications and foreign Spec Objects
    * contained in the imported ReqIF by the Specification or Spec Object foreign identifier. This map is generated from
    * the foreign {@link SpecHierarchy}s contained in each foreign {@link Specification}. This map will not contain any
    * of the foreign Specter Spec Objects.
    */

   Map<String, ForeignThingFamily> specObjectMap;

   /**
    * Map of the {@link Consumer} implementations to be returned by the {@link #getConverter} method.
    */

   //@formatter:off
   private static final EnumConsumerMap<IdentifierType, GroveThing> converterMap =
      EnumConsumerMap.ofEntries
         (
           IdentifierType.class,
           Map.entry( IdentifierType.ATTRIBUTE_DEFINITION, AttributeDefinitionConverter::convert       ),
           Map.entry( IdentifierType.ATTRIBUTE_VALUE,      AttributeValueConverter::convert            ),
           Map.entry( IdentifierType.DATA_TYPE_DEFINITION, DataTypeDefinitionConverter::convert        ),
           Map.entry( IdentifierType.ENUM_VALUE,           EnumValueConverter::convert                 ),
           Map.entry( IdentifierType.HEADER,               HeaderConverter::convert                    ),
           Map.entry( IdentifierType.SPECIFICATION,        SpecElementWithAttributesConverter::convert ),
           Map.entry( IdentifierType.SPECIFICATION_TYPE,   SpecTypeConverter::convert                  ),
           Map.entry( IdentifierType.SPECTER_SPEC_OBJECT,  SpecElementWithAttributesConverter::convert ),
           Map.entry( IdentifierType.SPEC_OBJECT,          SpecElementWithAttributesConverter::convert ),
           Map.entry( IdentifierType.SPEC_OBJECT_TYPE,     SpecTypeConverter::convert                  ),
           Map.entry( IdentifierType.SPEC_RELATION_TYPE,   SpecTypeConverter::convert                  ),
           Map.entry( IdentifierType.SPEC_RELATION,        SpecElementWithAttributesConverter::convert )
         );
   //@formatter:on

   long getAndIncrementAttributeValueCount() {
      return this.attributeValueCount.getAndIncrement();
   }

   /**
    * Map of {@link Function} implementations used to extract the all of the foreign things of the type specified by the
    * map key from the ReqIF DOM. These functions are implemented in a "sunny day" manner and may throw
    * {@link NullPointException}s when the ReqIF DOM does not contain any things of the specified type. The invocation
    * of the functions in this map are wrapped in a try/catch that will return an empty stream if any exceptions occur.
    */

   //@formatter:off
   private static final EnumFunctionMap<IdentifierType,ReqIFSynchronizationArtifactBuilder,Stream<ForeignThingFamily>> foreignThingsStreamSupplierMap =
      EnumFunctionMap.ofEntries
         (
            IdentifierType.class,
            Map.entry( IdentifierType.ATTRIBUTE_DEFINITION, AttributeDefinitionUtils::extract ),
            Map.entry( IdentifierType.ATTRIBUTE_VALUE,      AttributeValueUtils::extract      ),
            Map.entry( IdentifierType.DATA_TYPE_DEFINITION, DataTypeDefinitionUtils::extract  ),
            Map.entry( IdentifierType.ENUM_VALUE,           EnumValueUtils::extract           ),
            Map.entry( IdentifierType.HEADER,               ( builder ) -> Stream.empty()     ),
            Map.entry( IdentifierType.SPECIFICATION,        SpecificationUtils::extract       ),
            Map.entry( IdentifierType.SPECIFICATION_TYPE,   SpecificationTypeUtils::extract   ),
            Map.entry( IdentifierType.SPECTER_SPEC_OBJECT,  SpecterSpecObjectUtils::extract   ),
            Map.entry( IdentifierType.SPEC_OBJECT,          ( builder ) -> builder.specObjectMap.values().stream() ),
            Map.entry( IdentifierType.SPEC_OBJECT_TYPE,     SpecObjectTypeUtils::extract      ),
            Map.entry( IdentifierType.SPEC_RELATION,        SpecRelationUtils::extract        ),
            Map.entry( IdentifierType.SPEC_RELATION_TYPE,   SpecRelationTypeUtils::extract    )
         );
   //@formatter:on

   /**
    * A counter for generating identifiers for {@link AttributeValue} things in the ReqIF DOM. {@link AttributeValue}s
    * are a special case for being the foreign thing in a {@link GroveThing} because {@link AttributeValue} foreign
    * things do not have identifiers in the ReqIF DOM.
    */

   private AtomicLong attributeValueCount;

   /**
    * Time {@link ZoneId} constant for "Zulu".
    */
   private static final ZoneId zoneIdZ = ZoneId.of("Z");

   /**
    * {@link GregorianCalendar} constant for the UNIX epoch January 1, 1970 UTC.
    */

   static final GregorianCalendar lastChangeEpoch =
      GregorianCalendar.from(ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ReqIFSynchronizationArtifactBuilder.zoneIdZ));

   /**
    * Map of {@link Consumer} implementations to set the {@link AttributeDefinition} on an {@link AttributeValue}
    * according to the {@link NativeDataType}.
    */

   //@formatter:off
   private static final EnumBiConsumerMap<NativeDataType, ? super AttributeValue, ? super AttributeDefinition> reqifAttachAttributeDefinitionToAttributeValueMap =
      EnumBiConsumerMap.ofEntries
         (
           NativeDataType.class,
           Map.entry( NativeDataType.ARTIFACT_IDENTIFIER,  ( attributeValue, attributeDefinition ) -> ((AttributeValueInteger)     attributeValue).setDefinition((AttributeDefinitionInteger)     attributeDefinition) ),
           Map.entry( NativeDataType.BRANCH_IDENTIFIER,    ( attributeValue, attributeDefinition ) -> ((AttributeValueInteger)     attributeValue).setDefinition((AttributeDefinitionInteger)     attributeDefinition) ),
           Map.entry( NativeDataType.BOOLEAN,              ( attributeValue, attributeDefinition ) -> ((AttributeValueBoolean)     attributeValue).setDefinition((AttributeDefinitionBoolean)     attributeDefinition) ),
           Map.entry( NativeDataType.DATE,                 ( attributeValue, attributeDefinition ) -> ((AttributeValueDate)        attributeValue).setDefinition((AttributeDefinitionDate)        attributeDefinition) ),
           Map.entry( NativeDataType.DOUBLE,               ( attributeValue, attributeDefinition ) -> ((AttributeValueReal)        attributeValue).setDefinition((AttributeDefinitionReal)        attributeDefinition) ),
           Map.entry( NativeDataType.ENUMERATED,           ( attributeValue, attributeDefinition ) -> ((AttributeValueEnumeration) attributeValue).setDefinition((AttributeDefinitionEnumeration) attributeDefinition) ),
           Map.entry( NativeDataType.INPUT_STREAM,         ( attributeValue, attributeDefinition ) -> ((AttributeValueString)      attributeValue).setDefinition((AttributeDefinitionString)      attributeDefinition) ),
           Map.entry( NativeDataType.INTEGER,              ( attributeValue, attributeDefinition ) -> ((AttributeValueInteger)     attributeValue).setDefinition((AttributeDefinitionInteger)     attributeDefinition) ),
           Map.entry( NativeDataType.JAVA_OBJECT,          ( attributeValue, attributeDefinition ) -> ((AttributeValueString)      attributeValue).setDefinition((AttributeDefinitionString)      attributeDefinition) ),
           Map.entry( NativeDataType.LONG,                 ( attributeValue, attributeDefinition ) -> ((AttributeValueInteger)     attributeValue).setDefinition((AttributeDefinitionInteger)     attributeDefinition) ),
           Map.entry( NativeDataType.STRING,               ( attributeValue, attributeDefinition ) -> ((AttributeValueString)      attributeValue).setDefinition((AttributeDefinitionString)      attributeDefinition) ),
           Map.entry( NativeDataType.STRING_WORD_ML,       ( attributeValue, attributeDefinition ) -> ((AttributeValueXHTML)       attributeValue).setDefinition((AttributeDefinitionXHTML)       attributeDefinition) ),
           Map.entry( NativeDataType.URI,                  ( attributeValue, attributeDefinition ) -> ((AttributeValueString)      attributeValue).setDefinition((AttributeDefinitionString)      attributeDefinition) )
         );
   //@formatter:on

   /**
    * Map of {@link Consumer} implementations to set the {@link DatatypeDefinition} on an {@link AttributeDefinition}
    * according to the {@link NativeDataType}.
    */

   //@formatter:off
   private static final EnumBiConsumerMap<NativeDataType, ? super AttributeDefinition, ? super DatatypeDefinition> reqifAttachDatatypeDefinitionToAttributeDefinitionMap =
      EnumBiConsumerMap.ofEntries
         (
           NativeDataType.class,
           Map.entry( NativeDataType.ARTIFACT_IDENTIFIER, ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionInteger)     attributeDefinition).setType( (DatatypeDefinitionInteger)     datatypeDefinition ) ),
           Map.entry( NativeDataType.BRANCH_IDENTIFIER,   ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionInteger)     attributeDefinition).setType( (DatatypeDefinitionInteger)     datatypeDefinition ) ),
           Map.entry( NativeDataType.BOOLEAN,             ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionBoolean)     attributeDefinition).setType( (DatatypeDefinitionBoolean)     datatypeDefinition ) ),
           Map.entry( NativeDataType.DATE,                ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionDate)        attributeDefinition).setType( (DatatypeDefinitionDate)        datatypeDefinition ) ),
           Map.entry( NativeDataType.DOUBLE,              ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionReal)        attributeDefinition).setType( (DatatypeDefinitionReal)        datatypeDefinition ) ),
           Map.entry( NativeDataType.ENUMERATED,          ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionEnumeration) attributeDefinition).setType( (DatatypeDefinitionEnumeration) datatypeDefinition ) ),
           Map.entry( NativeDataType.INPUT_STREAM,        ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionString)      attributeDefinition).setType( (DatatypeDefinitionString)      datatypeDefinition ) ),
           Map.entry( NativeDataType.INTEGER,             ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionInteger)     attributeDefinition).setType( (DatatypeDefinitionInteger)     datatypeDefinition ) ),
           Map.entry( NativeDataType.JAVA_OBJECT,         ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionString)      attributeDefinition).setType( (DatatypeDefinitionString)      datatypeDefinition ) ),
           Map.entry( NativeDataType.LONG,                ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionInteger)     attributeDefinition).setType( (DatatypeDefinitionInteger)     datatypeDefinition ) ),
           Map.entry( NativeDataType.STRING,              ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionString)      attributeDefinition).setType( (DatatypeDefinitionString)      datatypeDefinition ) ),
           Map.entry( NativeDataType.STRING_WORD_ML,      ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionXHTML)       attributeDefinition).setType( (DatatypeDefinitionXHTML)       datatypeDefinition ) ),
           Map.entry( NativeDataType.URI,                 ( attributeDefinition, datatypeDefinition ) -> ((AttributeDefinitionString)      attributeDefinition).setType( (DatatypeDefinitionString)      datatypeDefinition ) )
         );
   //@formatter:on

   /**
    * The version of the ReqIFSynchronizationArtifactBuilder.
    */

   static final String version = "0.0";

   /**
    * The root of the ReqIF model.
    */

   ReqIF reqIf;

   /**
    * Saves the {@link SynchronizationArtifact} the ReqIF DOM is to be built from.
    */

   private SynchronizationArtifact synchronizationArtifact;

   /**
    * Constructor initializes the ReqIF model.
    */

   public ReqIFSynchronizationArtifactBuilder() {

      this.reqIf = null;
      this.attributeValueCount = null;
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException when the parameter <code>synchronizationArtifact</code> is <code>null</code>.
    */

   @Override
   public void initialize(SynchronizationArtifact synchronizationArtifact) {

      this.synchronizationArtifact = Objects.requireNonNull(synchronizationArtifact,
         "ReqIFSynchronizationArtifactBuilder::initialize, the parameter \"synchronizationArtifact\" cannot be null.");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Consumer<GroveThing>> getConverter(IdentifierType identifierType) {
      return ReqIFSynchronizationArtifactBuilder.converterMap.getFunction(identifierType);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean build() {

      this.reqIf = ReqIF10Factory.eINSTANCE.createReqIF();
      var forest = this.synchronizationArtifact.getForest();

      //@formatter:off
      var attributeValueGrove     = forest.getGrove(IdentifierType.ATTRIBUTE_VALUE);
      var datatypeDefinitionGrove = forest.getGrove(IdentifierType.DATA_TYPE_DEFINITION);
      var headerGrove             = forest.getGrove(IdentifierType.HEADER);
      var specificationGrove      = forest.getGrove(IdentifierType.SPECIFICATION);
      var specObjectGrove         = forest.getGrove(IdentifierType.SPEC_OBJECT);
      var specRelationGrove       = forest.getGrove(IdentifierType.SPEC_RELATION);
      //@formatter:on

      // HeaderGroveThing

      headerGrove.stream().forEach((headerGroveThing) -> {
         this.reqIf.setTheHeader((ReqIFHeader) headerGroveThing.getForeignThing());
      });

      // Content

      var reqifContent = ReqIF10Factory.eINSTANCE.createReqIFContent();

      this.reqIf.setCoreContent(reqifContent);

      //Data Type Definitions

      var reqifDatatypeDefinitionList = reqifContent.getDatatypes();

      datatypeDefinitionGrove.stream().forEach(dataTypeDefinitionGroveThing -> {

         var reqifDatatypeDefinition = (DatatypeDefinition) dataTypeDefinitionGroveThing.getForeignThing();

         if (((NativeDataTypeKey) dataTypeDefinitionGroveThing.getNativeThing()).isEnumerated()) {
            var reqifDatatypeDefinitionEnumeration = (DatatypeDefinitionEnumeration) reqifDatatypeDefinition;
            var reqifSpecifiedValues = reqifDatatypeDefinitionEnumeration.getSpecifiedValues();

            dataTypeDefinitionGroveThing.streamLinks(IdentifierType.ENUM_VALUE).forEach((enumValueGroveThing) -> {

               var reqifEnumValue = (EnumValue) enumValueGroveThing.getForeignThing();

               reqifSpecifiedValues.add(reqifEnumValue);

            });
         }

         reqifDatatypeDefinitionList.add(reqifDatatypeDefinition);

      });

      //Specification Types, Spec Object Types, & Spec Relation Types

      var reqifSpecTypeList = reqifContent.getSpecTypes();

      //@formatter:off
      Arrays.stream
         (
            new IdentifierType[]
            {
               IdentifierType.SPECIFICATION_TYPE,
               IdentifierType.SPEC_OBJECT_TYPE,
               IdentifierType.SPEC_RELATION_TYPE
            }
         )
         .map( forest::getGrove )
         .flatMap( Grove::stream )
         .forEach
             (
                ( groveThing ) ->
                {
                   var reqifSpecType = (SpecType) groveThing.getForeignThing();

                   reqifSpecTypeList.add(reqifSpecType);

                   var reqifAttributeDefinitionList = reqifSpecType.getSpecAttributes();

                   groveThing.streamLinks(IdentifierType.ATTRIBUTE_DEFINITION).forEach
                      (
                         ( attributeDefinition ) ->
                         {
                            reqifAttributeDefinitionList.add((AttributeDefinition) attributeDefinition.getForeignThing());

                            var reqifAttributeDefinition = (AttributeDefinition) attributeDefinition.getForeignThing();

                            var datatypeDefinition = attributeDefinition.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
                            var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();
                            var reqifDatatypeDefinition = (DatatypeDefinition) datatypeDefinition.getForeignThing();

                            ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.accept
                               (
                                 nativeDataType,
                                 reqifAttributeDefinition,
                                 reqifDatatypeDefinition
                               );
                         }
                      );
                }
             );
      //@formatter:on

      //Specifications

      var reqifSpecificationList = reqifContent.getSpecifications();

      specificationGrove.stream().forEach((specificationGroveThing) -> {

         var reqifSpecification = (Specification) specificationGroveThing.getForeignThing();

         var commonObjectType = specificationGroveThing.getLinkScalar(IdentifierType.SPECIFICATION_TYPE).get();
         var reqifSpecificationType = (SpecificationType) commonObjectType.getForeignThing();

         reqifSpecification.setType(reqifSpecificationType);

         reqifSpecificationList.add(reqifSpecification);
      });

      //Spec Objects

      var reqifSpecObjectList = reqifContent.getSpecObjects();

      //@formatter:off
      Arrays.stream
         (
            new IdentifierType[]
            {
               IdentifierType.SPEC_OBJECT,
               IdentifierType.SPECTER_SPEC_OBJECT
            }
         )
         .map( forest::getGrove )
         .flatMap( Grove::stream )
         .filter
            (
               ( groveThing ) ->    groveThing.isType( IdentifierType.SPEC_OBJECT         )
                                 || groveThing.isType( IdentifierType.SPECTER_SPEC_OBJECT )
            )
         .forEach
            (
               ( specObjectGroveThing ) ->
               {
                  var reqifSpecObject     = (SpecObject) specObjectGroveThing.getForeignThing();

                  var commonObjectType    = specObjectGroveThing.getLinkScalar(IdentifierType.SPEC_OBJECT_TYPE).get();
                  var reqifSpecObjectType = (SpecObjectType) commonObjectType.getForeignThing();

                  reqifSpecObject.setType(reqifSpecObjectType);

                  reqifSpecObjectList.add(reqifSpecObject);
               }
            );
      //@formatter:on

      //Spec Relations

      var reqifSpecRelationList = reqifContent.getSpecRelations();

      specRelationGrove.stream().forEach((specRelationGroveThing) -> {
         //@formatter:off
         var reqifSpecRelation         = (SpecRelation) specRelationGroveThing.getForeignThing();

         var commonObjectType          = specRelationGroveThing.getLinkScalar(IdentifierType.SPEC_RELATION_TYPE).get();
         var reqifSpecRelationType     = (SpecRelationType) commonObjectType.getForeignThing();

         reqifSpecRelation.setType( reqifSpecRelationType );

         var sideASpecObjectGroveThing = specRelationGroveThing.getLinkVectorElement(IdentifierTypeGroup.RELATABLE_OBJECT, 0).get();
         var sideBSpecObjectGroveThing = specRelationGroveThing.getLinkVectorElement(IdentifierTypeGroup.RELATABLE_OBJECT, 1).get();

         var reqifSideAThing = sideASpecObjectGroveThing.getForeignThing();
         var reqifSideBThing = sideBSpecObjectGroveThing.getForeignThing();

         if( reqifSideAThing instanceof SpecObject )
         {
            reqifSpecRelation.setSource( (SpecObject) reqifSideAThing );
         }

         if( reqifSideBThing instanceof SpecObject )
         {
            reqifSpecRelation.setTarget( (SpecObject) reqifSideBThing );
         }

         reqifSpecRelationList.add(reqifSpecRelation);
         //@formatter:on
      });

      //Values

      /*
       * Attach values to Specification, Spec Object, and Spec Relation GroveThings. Attach values to attribute
       * definitions.
       */

      //@formatter:off
      attributeValueGrove.stream().forEach
         (
            ( attributeValueGroveThing ) ->
            {
               var reqifAttributeValue = (AttributeValue) attributeValueGroveThing.getForeignThing();

               /*
                * Attach value to SpecificationGroveThing or SpecObjectGroveThing
                */

               var commonObject                   = attributeValueGroveThing.getParent( -1 ).get();
               var reqifSpecElementWithAttributes = (SpecElementWithAttributes) commonObject.getForeignThing();
               var reqifAttributeValueList        = reqifSpecElementWithAttributes.getValues();
               reqifAttributeValueList.add( reqifAttributeValue );

               /*
                * Attach value to its attribute definition
                */

               var attributeDefinition      = attributeValueGroveThing.getLinkScalar( IdentifierType.ATTRIBUTE_DEFINITION ).get();
               var datatypeDefinition       = attributeDefinition.getLinkScalar( IdentifierType.DATA_TYPE_DEFINITION ).get();
               var nativeDataType           = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();
               var reqifAttributeDefinition = (AttributeDefinition) attributeDefinition.getForeignThing();

               ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.accept
                  (
                     nativeDataType,
                     reqifAttributeValue,
                     reqifAttributeDefinition
                  );

            }
         );
      //@formatter:on

      //Build the specification hierarchies
      //@formatter:off

      /*
       * Stream the Specification Identifiers from the Spec Object Grove
       */

      specObjectGrove.streamIdentifiersShallow().forEach
         (
            ( specificationIdentifier ) ->
            {

               /*
                * Get the Specification Grove Thing by identifier
                */

               var specificationGroveThing = specObjectGrove.getByUniquePrimaryKey( specificationIdentifier ).get();

               /*
                * Get the ReqIF Specification and children list
                */

               var reqifSpecification         = (Specification) specificationGroveThing.getForeignThing();
               var reqifSpecificationChildren = reqifSpecification.getChildren();

               assert
                    reqifSpecificationChildren.size() == 0
                  : new StringBuilder( 1024 )
                           .append( "ReqIFSynchronizationArtifactBuilder::build, ReqIF Specification children list is not empty when starting to build hierarchy." ).append( "\n" )
                           .append( "   Specification: " ).append( specificationIdentifier ).append( "\n" )
                           .toString();

               /*
                * Set the ReqIF children list for the specification on the SpecificationGroveThing
                */

               specificationGroveThing.setForeignHierarchy( reqifSpecificationChildren );

               /*
                * Stream the key sets for all Spec Objects under the Specification
                */

               specObjectGrove.streamKeySets( specificationIdentifier )

                  /*
                   * The stream will contain key sets for the specifications as well. Filter out
                   * the specification key sets so only the Spec Object key sets remain.
                   */

                  .filter( ( keySet ) -> keySet[2].getType().equals( IdentifierType.SPEC_OBJECT ) )
                  .forEach
                     (
                        ( keySet ) ->
                        {
                           assert
                                ParameterArray.validateNonNullAndSize( keySet, 3,  3 )
                              : new StringBuilder( 1024 )
                                       .append( "ReqIFSynchronizationArtifactBuilder::build, Spec Object key set failed validation while building hierarchy." ).append( "\n" )
                                       .append( "   Spec Object Key Set:" ).append( Arrays.stream( keySet ).map( Identifier::toString ).collect( Collectors.joining( ", ", "[ ", " ]" ) ) ).append( "\n" )
                                       .toString();

                           var parentGroveThing     = specObjectGrove.getByUniquePrimaryKey( keySet[ 1 ] ).get();
                           var specObjectGroveThing = specObjectGrove.getByUniquePrimaryKey( keySet[ 2 ] ).get();

                           /*
                            * Create a SpecHierarchy and children list for the Spec Object and save
                            * it in the Grove Thing
                            */

                           var reqifSpecObject      = (SpecObject) specObjectGroveThing.getForeignThing();
                           var reqifSpecHierarchy   = ReqIF10Factory.eINSTANCE.createSpecHierarchy();
                           var reqifChildren        = reqifSpecHierarchy.getChildren();

                           reqifSpecHierarchy.setObject( reqifSpecObject );
                           specObjectGroveThing.setForeignHierarchy( reqifChildren );

                           /*
                            * The parent Spec Object must have already been processed. Get the ReqIF
                            * children list for the parent GroveThing. Add the ReqIF Spec Hierarchy
                            * object for the Spec Object to the parent's ReqIF child list.
                            */

                           @SuppressWarnings( "unchecked" )
                           var parentReqifChildren = (EList<SpecHierarchy>) parentGroveThing.getForeignHierarchy();

                           parentReqifChildren.add( reqifSpecHierarchy );

                        }
                     );
            }
         );

      //@formatter:on

      /*
       * Tool Extensions
       */

      var reqifToolExtension = ReqIF10Factory.eINSTANCE.createReqIFToolExtension();

      this.reqIf.getToolExtensions().add(reqifToolExtension);

      return true;
   }

   /**
    * {@inheritDoc}
    *
    * @implNote This implementation of the {@link SynchronizationArtifactBuilder} interface does not hold any resources
    * that need to be released.
    */

   @Override
   public void close() {
      ;
   }

   /**
    * {@inheritDoc}
    *
    * @throws SynchronizationArtifactParseException {@inheritDoc}
    */

   @Override
   public void deserialize(InputStream inputStream) {

      this.attributeValueCount = new AtomicLong();

      var resourceSet = new ResourceSetImpl();

      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("reqif",
         new ReqIF10ResourceFactoryImpl());

      var uri = URI.createFileURI("i.reqif");

      var resource = resourceSet.createResource(uri);

      try {
         resource.load(inputStream, null);
      } catch (IOException e) {
         //Invalid XML will be caught here

         var cause = e.getCause();

         if (cause instanceof SAXParseException) {
            throw new SynchronizationArtifactParseException((SAXParseException) cause);
         }

         throw new RuntimeException("Resource Load Failed", e);
      }

      /*
       * Check for Errors and Warnings
       */

      var errorList = resource.getErrors();
      var warningList = resource.getWarnings();

      if ((errorList.size() > 0) || (warningList.size() > 0)) {
         throw new SynchronizationArtifactParseException(errorList, warningList);
      }

      var eObjectList = resource.getContents();

      if (Objects.isNull(eObjectList)) {
         throw new SynchronizationArtifactParseException("Failed to get ReqIF contents.");
      }

      var rootEObject = eObjectList.get(0);

      if (Objects.isNull(rootEObject)) {
         throw new SynchronizationArtifactParseException("Failed to get root ReqIF object.");
      }

      this.reqIf = (ReqIF) rootEObject;

      /*
       * Index the hierarchical information for Spec Objects
       */

      var specObjects = this.reqIf.getCoreContent().getSpecObjects();

      //@formatter:off
      this.specObjectMap =
         this.reqIf.getCoreContent().getSpecifications().stream()
            .flatMap
               (
                  ( specification ) ->

                     Stream.concat
                        (
                           Stream.of( new SpecObjectSliderForeignThingFamily( specification ) ),
                           SpecObjectSliderForeignThingFamily.start( specification ).flatMap( SpecObjectSliderForeignThingFamily::flatten )
                        )
               )
            .collect
               (
                  Collectors.toMap
                     (
                        ( foreignThingFamily )                       -> ((Identifiable) foreignThingFamily.getChild()).getIdentifier(),
                        ( foreignThingFamily )                       -> (ForeignThingFamily) foreignThingFamily,
                        ( foreignThingFamilyA, foreignThingFamilyB ) -> foreignThingFamilyA,
                        ()                                           -> new LinkedHashMap<String,ForeignThingFamily>( specObjects.size() * 5 )
                     )
               );
       //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<String> getAttributeDefinition(GroveThing attributeValueGroveThing) {
      return AttributeValueUtils.getAttributeDefinitionIdentifier(
         (AttributeValue) attributeValueGroveThing.getForeignThing());
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<String> getAttributeDefinitions(GroveThing specTypeGroveThing) {
      return ((SpecType) specTypeGroveThing.getForeignThing()).getSpecAttributes().stream().map(
         AttributeDefinition::getIdentifier);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<String> getDatatypeDefinition(GroveThing attributeDefinitionGroveThing) {
      return AttributeDefinitionUtils.getDatatypeDefinitionIdentifier(
         (AttributeDefinition) attributeDefinitionGroveThing.getForeignThing());
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<String> getEnumValues(GroveThing datatypeDefinitionGroveThing) {
      var datatypeDefinition = (DatatypeDefinition) datatypeDefinitionGroveThing.getForeignThing();

      if (!(datatypeDefinition instanceof DatatypeDefinitionEnumeration)) {
         return Stream.empty();
      }

      return ((DatatypeDefinitionEnumeration) datatypeDefinition).getSpecifiedValues().stream().map(
         Identifiable::getIdentifier);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<ForeignThingFamily> getForeignThings(IdentifierType identifierType) {
      try {
         return ReqIFSynchronizationArtifactBuilder.foreignThingsStreamSupplierMap.apply(identifierType, this);
      } catch (Exception e) {
         /*
          * An exception may occur when the ReqIF DOM is incomplete or does not contain any things of the requested
          * type. Eat the exception and return an empty stream.
          */
         return Stream.empty();
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<String> getSpecificationType(GroveThing specificationGroveThing) {
      return Optional.ofNullable(((Specification) specificationGroveThing.getForeignThing()).getType().getIdentifier());
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<ForeignThingFamily> getSpecObject(GroveThing specRelationGroveThing, RelationshipTerminal relationshipTerminal) {
      try {
         var specRelation = (SpecRelation) specRelationGroveThing.getForeignThing();
         //@formatter:off
         var specObjectIdentifierString =
                  relationshipTerminal.equals( RelationshipTerminal.SOURCE )
                     ? specRelation.getSource().getIdentifier()
                     : specRelation.getTarget().getIdentifier();

         return Optional.ofNullable( this.specObjectMap.get( specObjectIdentifierString ) );
         //@formatter:on
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<String> getSpecObjectType(GroveThing specObjectGroveThing) {
      return Optional.ofNullable(((SpecObject) specObjectGroveThing.getForeignThing()).getType().getIdentifier());
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<String> getSpecRelationType(GroveThing specRelationGroveThing) {
      return Optional.ofNullable(((SpecRelation) specRelationGroveThing.getForeignThing()).getType().getIdentifier());
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isEnumerated(GroveThing attributeValueGroveThing) {
      return attributeValueGroveThing.getForeignThing() instanceof AttributeValueEnumeration;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public InputStream serialize() {

      ResourceSetImpl resourceSet = new ResourceSetImpl();

      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("reqif",
         new ReqIF10ResourceFactoryImpl() {
            @Override
            public Resource createResource(URI uri) {
               return new ReqIF10ResourceImpl(uri) {
                  @Override
                  protected boolean useUUIDs() {
                     return false;
                  }
               };
            }
         });

      var uri = URI.createFileURI("o.reqif");

      Resource resource = resourceSet.createResource(uri);

      if (Objects.isNull(resource)) {
         throw new SynchronizationArtifactSerializationException("Failed to create ECore Resource for serialization.");
      }

      resource.getContents().add(this.reqIf);

      var outputStream = new ByteArrayOutputStream() {
         byte[] getBuffer() {
            return this.buf;
         }
      };

      try {
         resource.save(outputStream, null);
      } catch (Exception e) {
         throw new SynchronizationArtifactSerializationException("Resource Save Failed", e);
      }

      var inputStream = new ByteArrayInputStream(outputStream.getBuffer(), 0, outputStream.size());
      return inputStream;
   }

}

/* EOF */
