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

package org.eclipse.osee.synchronization.rest.reqifsynchronizationartifactbuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.framework.jdk.core.util.EnumBiConsumerMap;
import org.eclipse.osee.framework.jdk.core.util.EnumConsumerMap;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.IsSynchronizationArtifactBuilder;
import org.eclipse.osee.synchronization.rest.SynchronizationArtifact;
import org.eclipse.osee.synchronization.rest.SynchronizationArtifactBuilder;
import org.eclipse.osee.synchronization.rest.forest.AttributeValueGrove;
import org.eclipse.osee.synchronization.rest.forest.AttributeValueGroveThing;
import org.eclipse.osee.synchronization.rest.forest.CommonObjectTypeGroveThing;
import org.eclipse.osee.synchronization.rest.forest.DataTypeDefinitionGrove;
import org.eclipse.osee.synchronization.rest.forest.DataTypeDefinitionGroveThing;
import org.eclipse.osee.synchronization.rest.forest.HeaderGrove;
import org.eclipse.osee.synchronization.rest.forest.SpecObjectGrove;
import org.eclipse.osee.synchronization.rest.forest.SpecObjectGroveThing;
import org.eclipse.osee.synchronization.rest.forest.SpecObjectTypeGrove;
import org.eclipse.osee.synchronization.rest.forest.SpecTypeGrove;
import org.eclipse.osee.synchronization.rest.forest.SpecificationGrove;
import org.eclipse.osee.synchronization.rest.forest.SpecificationGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.osee.synchronization.rest.nativedatatype.NativeDataType;
import org.eclipse.osee.synchronization.rest.nativedatatype.NativeDataTypeKey;
import org.eclipse.osee.synchronization.util.ParameterArray;
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
import org.eclipse.rmf.reqif10.ReqIF;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.ReqIFHeader;
import org.eclipse.rmf.reqif10.SpecElementWithAttributes;
import org.eclipse.rmf.reqif10.SpecHierarchy;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.SpecObjectType;
import org.eclipse.rmf.reqif10.SpecType;
import org.eclipse.rmf.reqif10.Specification;
import org.eclipse.rmf.reqif10.SpecificationType;
import org.eclipse.rmf.reqif10.serialization.ReqIF10ResourceFactoryImpl;
import org.eclipse.rmf.reqif10.serialization.ReqIF10ResourceImpl;

/**
 * Implementation of the {@link SynchronizationArtifactBuilder} interface for building a ReqIF Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

@IsSynchronizationArtifactBuilder(artifactType = "reqif")
public class ReqIFSynchronizationArtifactBuilder implements SynchronizationArtifactBuilder {

   /**
    * Map of the {@link Consumer} implementations to be returned by the {@link #getConverter} method.
    */

   //@formatter:off
   private static final EnumConsumerMap<IdentifierType, GroveThing> converterMap =
      EnumConsumerMap.ofEntries
         (
           IdentifierType.class,
           Map.entry( IdentifierType.HEADER,               HeaderConverter::convert              ),
           Map.entry( IdentifierType.DATA_TYPE_DEFINITION, DataTypeDefinitionConverter::convert  ),
           Map.entry( IdentifierType.ENUM_VALUE,           EnumValueConverter::convert           ),
           Map.entry( IdentifierType.SPECIFICATION_TYPE,   SpecTypeConverter::convert            ),
           Map.entry( IdentifierType.ATTRIBUTE_DEFINITION, AttributeDefinitionConverter::convert ),
           Map.entry( IdentifierType.SPEC_OBJECT_TYPE,     SpecObjectTypeConverter::convert      ),
           Map.entry( IdentifierType.SPECIFICATION,        SpecificationConverter::convert       ),
           Map.entry( IdentifierType.SPEC_OBJECT,          SpecObjectConverter::convert          ),
           Map.entry( IdentifierType.ATTRIBUTE_VALUE,      AttributeValueConverter::convert      )
         );
   //@formatter:on

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
    * Constructor initializes the ReqIF model.
    */

   public ReqIFSynchronizationArtifactBuilder() {

      this.reqIf = ReqIF10Factory.eINSTANCE.createReqIF();
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
   public boolean build(SynchronizationArtifact synchronizationArtifact) {

      var forest = synchronizationArtifact.getForest();

      //@formatter:off
      var headerGrove             = (HeaderGrove)             forest.getGrove(IdentifierType.HEADER);
      var datatypeDefinitionGrove = (DataTypeDefinitionGrove) forest.getGrove(IdentifierType.DATA_TYPE_DEFINITION);
      var specTypeGrove           = (SpecTypeGrove)           forest.getGrove(IdentifierType.SPECIFICATION_TYPE);
      var specObjectTypeGrove     = (SpecObjectTypeGrove)     forest.getGrove(IdentifierType.SPEC_OBJECT_TYPE);
      var specificationGrove      = (SpecificationGrove)      forest.getGrove(IdentifierType.SPECIFICATION);
      var specObjectGrove         = (SpecObjectGrove)         forest.getGrove(IdentifierType.SPEC_OBJECT);
      var attributeValueGrove     = (AttributeValueGrove)     forest.getGrove(IdentifierType.ATTRIBUTE_VALUE);
      //@formatter:on

      // HeaderGroveThing

      headerGrove.streamDeep().forEach(header -> {
         this.reqIf.setTheHeader((ReqIFHeader) header.getForeignThing());
      });

      // Content

      var reqifContent = ReqIF10Factory.eINSTANCE.createReqIFContent();

      this.reqIf.setCoreContent(reqifContent);

      //Data Type Definitions

      var reqifDatatypeDefinitionList = reqifContent.getDatatypes();

      datatypeDefinitionGrove.streamDeep().forEach(groveThing -> {

         var dataTypeDefinitionGroveThing = (DataTypeDefinitionGroveThing) groveThing;
         var reqifDatatypeDefinition = (DatatypeDefinition) dataTypeDefinitionGroveThing.getForeignThing();

         if (((NativeDataTypeKey) dataTypeDefinitionGroveThing.getNativeThing()).isEnumerated()) {
            var reqifDatatypeDefinitionEnumeration = (DatatypeDefinitionEnumeration) reqifDatatypeDefinition;
            var reqifSpecifiedValues = reqifDatatypeDefinitionEnumeration.getSpecifiedValues();

            dataTypeDefinitionGroveThing.streamEnumValueGroveThings().forEach((enumValueGroveThing) -> {

               var reqifEnumValue = (EnumValue) enumValueGroveThing.getForeignThing();

               reqifSpecifiedValues.add(reqifEnumValue);

            });
         }

         reqifDatatypeDefinitionList.add(reqifDatatypeDefinition);

      });

      //Spec Types & Spec Object Types

      var reqifSpecTypeList = reqifContent.getSpecTypes();

      Stream.concat(specTypeGrove.streamDeep(), specObjectTypeGrove.streamDeep()).forEach(groveThing -> {

         var commonObjectType = (CommonObjectTypeGroveThing) groveThing;
         var reqifSpecType = (SpecType) groveThing.getForeignThing();

         reqifSpecTypeList.add(reqifSpecType);

         var reqifAttributeDefinitionList = reqifSpecType.getSpecAttributes();

         commonObjectType.streamAttributeDefinitions().forEach(attributeDefinition -> {

            reqifAttributeDefinitionList.add((AttributeDefinition) attributeDefinition.getForeignThing());

            var reqifAttributeDefinition = (AttributeDefinition) attributeDefinition.getForeignThing();

            var datatypeDefinition = attributeDefinition.getDataTypeDefinition();
            var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();
            var reqifDatatypeDefinition = (DatatypeDefinition) datatypeDefinition.getForeignThing();

            ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.accept(
               nativeDataType, reqifAttributeDefinition, reqifDatatypeDefinition);

         });

      });

      //Specifications

      var reqifSpecificationList = reqifContent.getSpecifications();

      specificationGrove.streamDeep().forEach(groveThing -> {

         var specification = (SpecificationGroveThing) groveThing;
         var reqifSpecification = (Specification) groveThing.getForeignThing();

         var commonObjectType = specification.getCommonObjectType();
         var reqifSpecificationType = (SpecificationType) commonObjectType.getForeignThing();

         reqifSpecification.setType(reqifSpecificationType);

         reqifSpecificationList.add(reqifSpecification);
      });

      //Spec Objects

      var reqifSpecObjectList = reqifContent.getSpecObjects();

      specObjectGrove.streamDeep().forEach(groveThing -> {

         if (groveThing instanceof SpecificationGroveThing) {
            return;
         }

         var specObject = (SpecObjectGroveThing) groveThing;
         var reqifSpecObject = (SpecObject) groveThing.getForeignThing();

         var commonObjectType = specObject.getCommonObjectType();
         var reqifSpecObjectType = (SpecObjectType) commonObjectType.getForeignThing();

         reqifSpecObject.setType(reqifSpecObjectType);

         reqifSpecObjectList.add(reqifSpecObject);
      });

      //Values

      /*
       * Attach values to SpecificationGroveThing and SpecObjectGroveThing things. Attach values to attribute
       * definitions.
       */

      //@formatter:off
      attributeValueGrove.streamDeep().forEach
         (
            ( groveThing ) ->
            {

               var attributeValueGroveThing = (AttributeValueGroveThing) groveThing;
               var reqifAttributeValue      = (AttributeValue) attributeValueGroveThing.getForeignThing();

               /*
                * Attach value to SpecificationGroveThing or SpecObjectGroveThing
                */

               var commonObject                   = attributeValueGroveThing.getParent();
               var reqifSpecElementWithAttributes = (SpecElementWithAttributes) commonObject.getForeignThing();
               var reqifAttributeValueList        = reqifSpecElementWithAttributes.getValues();
               reqifAttributeValueList.add( reqifAttributeValue );

               /*
                * Attach value to its attribute definition
                */

               var attributeDefinition      = attributeValueGroveThing.getAttributeDefinition();
               var datatypeDefinition       = attributeDefinition.getDataTypeDefinition();
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
      specObjectGrove.streamIdentifiersShallow().forEach
         (
            ( specificationIdentifier ) ->
            {

               var specificationGroveThing = specObjectGrove.getSpecification( specificationIdentifier ).get();

               //SpecificationGroveThing
               var reqifSpecification         = (Specification) specificationGroveThing.getForeignThing();
               var reqifSpecificationChildreny = reqifSpecification.getChildren();

               specificationGroveThing.setForeignHierarchy( reqifSpecificationChildreny );

               specObjectGrove.streamKeySetsShallow( specificationIdentifier )
                  .forEach
                     (
                        ( keySet ) ->
                        {
                           assert ParameterArray.validateNonNullAndSize( keySet, 3,  3);

                           var parentIdentifier     = keySet[1];
                           var specObjectIdentifier = keySet[2];

                           //Spec Object
                           var specObject          = specObjectGrove.getSpecObject( specificationIdentifier, specObjectIdentifier ).get();
                           var reqifSpecObject     = (SpecObject) specObject.getForeignThing();
                           var reqifSpecHierarchy  = ReqIF10Factory.eINSTANCE.createSpecHierarchy();
                           var reqifChildren       = reqifSpecHierarchy.getChildren();

                           reqifSpecHierarchy.setObject( reqifSpecObject );
                           specObject.setForeignHierarchy( reqifChildren );

                           var parentSpecObject    = specificationIdentifier.equals( parentIdentifier )
                                                        ? specObjectGrove.getSpecification( specificationIdentifier ).get()
                                                        : specObjectGrove.getSpecObject( specificationIdentifier, parentIdentifier ).get();

                           @SuppressWarnings( "unchecked" )
                           var parentReqifChildren = (EList<SpecHierarchy>) parentSpecObject.getForeignHierarchy();

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

      return false;
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
      resource.getContents().add(this.reqIf);

      var outputStream = new ByteArrayOutputStream() {
         byte[] getBuffer() {
            return this.buf;
         }
      };

      try {
         resource.save(outputStream, null);
      } catch (Exception e) {
         throw new RuntimeException("Resource Save Failed", e);
      }

      var inputStream = new ByteArrayInputStream(outputStream.getBuffer(), 0, outputStream.size());
      return inputStream;
   }

}

/* EOF */
