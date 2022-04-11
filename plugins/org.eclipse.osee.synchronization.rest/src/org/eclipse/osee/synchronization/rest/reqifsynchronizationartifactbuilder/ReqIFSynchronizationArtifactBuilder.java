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
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeBranchId;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeDouble;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeLong;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.synchronization.rest.AttributeDefinitionGroveThing;
import org.eclipse.osee.synchronization.rest.AttributeValueGrove;
import org.eclipse.osee.synchronization.rest.AttributeValueGroveThing;
import org.eclipse.osee.synchronization.rest.CommonObjectTypeGroveThing;
import org.eclipse.osee.synchronization.rest.DataTypeDefinitionGrove;
import org.eclipse.osee.synchronization.rest.DataTypeDefinitionGroveThing;
import org.eclipse.osee.synchronization.rest.GroveThing;
import org.eclipse.osee.synchronization.rest.HeaderGrove;
import org.eclipse.osee.synchronization.rest.HeaderGroveThing;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.IsSynchronizationArtifactBuilder;
import org.eclipse.osee.synchronization.rest.NativeDataType;
import org.eclipse.osee.synchronization.rest.SpecObjectGrove;
import org.eclipse.osee.synchronization.rest.SpecObjectGroveThing;
import org.eclipse.osee.synchronization.rest.SpecObjectTypeGrove;
import org.eclipse.osee.synchronization.rest.SpecObjectTypeGroveThing;
import org.eclipse.osee.synchronization.rest.SpecTypeGrove;
import org.eclipse.osee.synchronization.rest.SpecTypeGroveThing;
import org.eclipse.osee.synchronization.rest.SpecificationGrove;
import org.eclipse.osee.synchronization.rest.SpecificationGroveThing;
import org.eclipse.osee.synchronization.rest.SynchronizationArtifact;
import org.eclipse.osee.synchronization.rest.SynchronizationArtifactBuilder;
import org.eclipse.osee.synchronization.util.DataConverters;
import org.eclipse.osee.synchronization.util.EnumBiConsumerMap;
import org.eclipse.osee.synchronization.util.EnumConsumerMap;
import org.eclipse.osee.synchronization.util.EnumSupplierMap;
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.AttributeDefinitionBoolean;
import org.eclipse.rmf.reqif10.AttributeDefinitionDate;
import org.eclipse.rmf.reqif10.AttributeDefinitionInteger;
import org.eclipse.rmf.reqif10.AttributeDefinitionReal;
import org.eclipse.rmf.reqif10.AttributeDefinitionString;
import org.eclipse.rmf.reqif10.AttributeDefinitionXHTML;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.AttributeValueBoolean;
import org.eclipse.rmf.reqif10.AttributeValueDate;
import org.eclipse.rmf.reqif10.AttributeValueInteger;
import org.eclipse.rmf.reqif10.AttributeValueReal;
import org.eclipse.rmf.reqif10.AttributeValueString;
import org.eclipse.rmf.reqif10.AttributeValueXHTML;
import org.eclipse.rmf.reqif10.DatatypeDefinition;
import org.eclipse.rmf.reqif10.DatatypeDefinitionBoolean;
import org.eclipse.rmf.reqif10.DatatypeDefinitionDate;
import org.eclipse.rmf.reqif10.DatatypeDefinitionInteger;
import org.eclipse.rmf.reqif10.DatatypeDefinitionReal;
import org.eclipse.rmf.reqif10.DatatypeDefinitionString;
import org.eclipse.rmf.reqif10.DatatypeDefinitionXHTML;
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
    * Interface used to mark classes as allowable entries in the {@link #reqifDatatypeDefinitionRecordMap}.
    */

   private interface ReqifDatatypeRecord {
      /*
       * Marks classes as allowable entries in the reqifDatatypeDefinitionRecordMap.
       */
   };

   /**
    * Record used to hold Double specific ReqIF Datatype Definition attribute values.
    */

   private static class ReqifDatatypeDoubleRecord implements ReqifDatatypeRecord {
      final Long accuracy;
      final Double max;
      final Double min;

      /**
       * Creates a new record and saves the attribute values.
       *
       * @param accuracy the number of used to represent the value.
       * @param max the maximum allowable value.
       * @param min the minimum allowable value.
       */

      ReqifDatatypeDoubleRecord(Long accuracy, Double max, Double min) {
         this.accuracy = accuracy;
         this.max = max;
         this.min = min;
      }
   }

   /**
    * Record used to hold Integer specific ReqIF Datatype Definition attribute values.
    */

   private static class ReqifDatatypeIntegerRecord implements ReqifDatatypeRecord {
      final Long max;
      final Long min;

      /**
       * Creates a new record and saves the attribute values.
       *
       * @param max the maximum allowable value.
       * @param min the minimum allowable value.
       */

      ReqifDatatypeIntegerRecord(Long max, Long min) {
         this.max = max;
         this.min = min;
      }
   }

   /**
    * Record used to hold String specific ReqIF Datatype Definition attribute values.
    */

   private static class ReqifDatatypeStringRecord implements ReqifDatatypeRecord {
      final Long maxLength;

      /**
       * Creates a new record and saves the attribute values.
       *
       * @param maxLength the maximum number of characters allowed in the string.
       */

      ReqifDatatypeStringRecord(Long maxLength) {
         this.maxLength = maxLength;
      }
   }

   /**
    * Map of the {@link Consumer} implementations to be returned by the {@link #getConverter} method.
    */

   private static final EnumConsumerMap<IdentifierType, GroveThing> converterMap =
      new EnumConsumerMap<>(IdentifierType.class);

   /**
    * Time {@link ZoneId} constant for "Zulu".
    */

   private static final ZoneId zoneIdZ = ZoneId.of("Z");

   /**
    * {@link GregorianCalendar} constant for the UNIX epoch January 1, 1970 UTC.
    */

   private static final GregorianCalendar lastChangeEpoch =
      GregorianCalendar.from(ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ReqIFSynchronizationArtifactBuilder.zoneIdZ));

   /**
    * Map of {@link Consumer} implementations to set the {@link AttributeDefinition} on an {@link AttributeValue}
    * according to the {@link NativeDataType}.
    */

   private static final EnumBiConsumerMap<NativeDataType, ? super AttributeValue, ? super AttributeDefinition> reqifAttachAttributeDefinitionToAttributeValueMap =
      new EnumBiConsumerMap<>(NativeDataType.class);

   /**
    * Map of {@link Consumer} implementations to set the {@link DatatypeDefinition} on an {@link AttributeDefinition}
    * according to the {@link NativeDataType}.
    */

   private static final EnumBiConsumerMap<NativeDataType, ? super AttributeDefinition, ? super DatatypeDefinition> reqifAttachDatatypeDefinitionToAttributeDefinitionMap =
      new EnumBiConsumerMap<>(NativeDataType.class);

   /**
    * Map of {@link BiConsumer} implementations to set the data type specific attributes of foreign
    * {@link AttributeDefinitionGroveThing} things.
    */

   private static final EnumBiConsumerMap<NativeDataType, AttributeDefinitionGroveThing, AttributeDefinition> reqifAttributeDefinitionDatatypeConverterMap =
      new EnumBiConsumerMap<>(NativeDataType.class);

   /**
    * Map of {@link Supplier} implementations to create the foreign things that extend the {@link AttributeDefinition}
    * class appropriate for each {@link NativeDataType}.
    */

   private static final EnumSupplierMap<NativeDataType, AttributeDefinition> reqifAttributeDefinitionFactoryMap =
      new EnumSupplierMap<>(NativeDataType.class);

   /**
    * Map of {@link Supplier} implementations to create the foreign things that extend the {@link AttributeValue} class
    * appropriate for each {@link NativeDataType}.
    */

   private static final EnumSupplierMap<NativeDataType, AttributeValue> reqifAttributeValueFactoryMap =
      new EnumSupplierMap<>(NativeDataType.class);

   /**
    * Map of {@link BiConsumer} implementations to set the value on an {@link AttributeValue} according to the
    * {@link NativeDataType}.
    */

   private static final EnumBiConsumerMap<NativeDataType, Object, AttributeValue> reqifAttributeValueSetterMap =
      new EnumBiConsumerMap<>(NativeDataType.class);

   /**
    * Map of {@link Supplier} implementations to create the foreign things that extend the {@link DatatypeDefinition}
    * class appropriate for each {@link NativeDataType}.
    */

   private static final EnumSupplierMap<NativeDataType, DatatypeDefinition> reqifDatatypeDefinitionFactoryMap =
      new EnumSupplierMap<>(NativeDataType.class);

   /**
    * Map of {@link BiConsumer} implementations to set the data type specific attributes of foreign
    * {@link DatatypeDefinition} things.
    */

   private static final EnumBiConsumerMap<NativeDataType, DataTypeDefinitionGroveThing, DatatypeDefinition> reqifDatatypeDefinitionDatatypeConverterMap =
      new EnumBiConsumerMap<>(NativeDataType.class);

   /**
    * Map of {@link ReqifDatatypeRecords} with data type specific constants for each {@link NativeDataType}.
    */

   private static final EnumMap<NativeDataType, ReqifDatatypeRecord> reqifDatatypeDefinitionRecordMap =
      new EnumMap<>(NativeDataType.class);

   /**
    * The version of the ReqIFSynchronizationArtifactBuilder.
    */

   private static final String version = "0.0";

   static {
      //@formatter:off
      ReqIFSynchronizationArtifactBuilder.converterMap.put(IdentifierType.HEADER,               ReqIFSynchronizationArtifactBuilder::convertHeader              );
      ReqIFSynchronizationArtifactBuilder.converterMap.put(IdentifierType.DATA_TYPE_DEFINITION, ReqIFSynchronizationArtifactBuilder::convertDataTypeDefinition  );
      ReqIFSynchronizationArtifactBuilder.converterMap.put(IdentifierType.SPECIFICATION_TYPE,   ReqIFSynchronizationArtifactBuilder::convertSpecType            );
      ReqIFSynchronizationArtifactBuilder.converterMap.put(IdentifierType.ATTRIBUTE_DEFINITION, ReqIFSynchronizationArtifactBuilder::convertAttributeDefinition );
      ReqIFSynchronizationArtifactBuilder.converterMap.put(IdentifierType.SPEC_OBJECT_TYPE,     ReqIFSynchronizationArtifactBuilder::convertSpecObjectType      );
      ReqIFSynchronizationArtifactBuilder.converterMap.put(IdentifierType.SPECIFICATION,        ReqIFSynchronizationArtifactBuilder::convertSpecification       );
      ReqIFSynchronizationArtifactBuilder.converterMap.put(IdentifierType.SPEC_OBJECT,          ReqIFSynchronizationArtifactBuilder::convertSpecObject          );
      ReqIFSynchronizationArtifactBuilder.converterMap.put(IdentifierType.ATTRIBUTE_VALUE,      ReqIFSynchronizationArtifactBuilder::convertAttributeValue      );

      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.ARTIFACT_IDENTIFIER,  ( attributeValue, attributeDefinition ) -> ((AttributeValueInteger) attributeValue).setDefinition((AttributeDefinitionInteger) attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.BRANCH_IDENTIFIER,    ( attributeValue, attributeDefinition ) -> ((AttributeValueInteger) attributeValue).setDefinition((AttributeDefinitionInteger) attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.BOOLEAN,              ( attributeValue, attributeDefinition ) -> ((AttributeValueBoolean) attributeValue).setDefinition((AttributeDefinitionBoolean) attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.DATE,                 ( attributeValue, attributeDefinition ) -> ((AttributeValueDate)    attributeValue).setDefinition((AttributeDefinitionDate)    attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.DOUBLE,               ( attributeValue, attributeDefinition ) -> ((AttributeValueReal)    attributeValue).setDefinition((AttributeDefinitionReal)    attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.ENUMERATED,           ( attributeValue, attributeDefinition ) -> ((AttributeValueString)  attributeValue).setDefinition((AttributeDefinitionString)  attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.INPUT_STREAM,         ( attributeValue, attributeDefinition ) -> ((AttributeValueString)  attributeValue).setDefinition((AttributeDefinitionString)  attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.INTEGER,              ( attributeValue, attributeDefinition ) -> ((AttributeValueInteger) attributeValue).setDefinition((AttributeDefinitionInteger) attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.JAVA_OBJECT,          ( attributeValue, attributeDefinition ) -> ((AttributeValueString)  attributeValue).setDefinition((AttributeDefinitionString)  attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.LONG,                 ( attributeValue, attributeDefinition ) -> ((AttributeValueInteger) attributeValue).setDefinition((AttributeDefinitionInteger) attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.STRING,               ( attributeValue, attributeDefinition ) -> ((AttributeValueString)  attributeValue).setDefinition((AttributeDefinitionString)  attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.STRING_WORD_ML,       ( attributeValue, attributeDefinition ) -> ((AttributeValueXHTML)   attributeValue).setDefinition((AttributeDefinitionXHTML)   attributeDefinition) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.put(NativeDataType.URI,                  ( attributeValue, attributeDefinition ) -> ((AttributeValueString)  attributeValue).setDefinition((AttributeDefinitionString)  attributeDefinition) );

      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.ARTIFACT_IDENTIFIER, (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionInteger) attributeDefinition).setType( (DatatypeDefinitionInteger) datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.BRANCH_IDENTIFIER,   (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionInteger) attributeDefinition).setType( (DatatypeDefinitionInteger) datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.BOOLEAN,             (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionBoolean) attributeDefinition).setType( (DatatypeDefinitionBoolean) datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.DATE,                (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionDate)    attributeDefinition).setType( (DatatypeDefinitionDate)    datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.DOUBLE,              (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionReal)    attributeDefinition).setType( (DatatypeDefinitionReal)    datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.ENUMERATED,          (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionString)  attributeDefinition).setType( (DatatypeDefinitionString)  datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.INPUT_STREAM,        (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionString)  attributeDefinition).setType( (DatatypeDefinitionString)  datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.INTEGER,             (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionInteger) attributeDefinition).setType( (DatatypeDefinitionInteger) datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.JAVA_OBJECT,         (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionString)  attributeDefinition).setType( (DatatypeDefinitionString)  datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.LONG,                (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionInteger) attributeDefinition).setType( (DatatypeDefinitionInteger) datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.STRING,              (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionString)  attributeDefinition).setType( (DatatypeDefinitionString)  datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.STRING_WORD_ML,      (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionXHTML)   attributeDefinition).setType( (DatatypeDefinitionXHTML)   datatypeDefinition ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.put(NativeDataType.URI,                 (attributeDefinition,datatypeDefinition) -> ((AttributeDefinitionString)  attributeDefinition).setType( (DatatypeDefinitionString)  datatypeDefinition ) );

      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.put(NativeDataType.ARTIFACT_IDENTIFIER, ReqIFSynchronizationArtifactBuilder::convertAttributeDefinitionReqIfAttributeDefinitionIntegerForArtifactId );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.put(NativeDataType.BRANCH_IDENTIFIER,   ReqIFSynchronizationArtifactBuilder::convertAttributeDefinitionReqIfAttributeDefinitionIntegerForBranchId   );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.put(NativeDataType.BOOLEAN,             ReqIFSynchronizationArtifactBuilder::convertAttributeDefinitionReqIfAttributeDefinitionBoolean              );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.put(NativeDataType.DATE,                ReqIFSynchronizationArtifactBuilder::convertAttributeDefinitionReqIfAttributeDefinitionDate                 );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.put(NativeDataType.DOUBLE,              ReqIFSynchronizationArtifactBuilder::convertAttributeDefinitionReqIfAttributeDefinitionDouble               );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.put(NativeDataType.INTEGER,             ReqIFSynchronizationArtifactBuilder::convertAttributeDefinitionReqIfAttributeDefinitionIntegerForInteger    );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.put(NativeDataType.LONG,                ReqIFSynchronizationArtifactBuilder::convertAttributeDefinitionReqIfAttributeDefinitionIntegerForLong       );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.put(NativeDataType.STRING,              ReqIFSynchronizationArtifactBuilder::convertAttributeDefinitionReqIfAttributeDefinitionString               );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.put(NativeDataType.STRING_WORD_ML,      ReqIFSynchronizationArtifactBuilder::convertAttributeDefinitionReqIfAttributeDefinitionXHTML                );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.put(NativeDataType.URI,                 ReqIFSynchronizationArtifactBuilder::convertAttributeDefinitionReqIfAttributeDefinitionString               );

      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.ARTIFACT_IDENTIFIER, ReqIF10Factory.eINSTANCE::createAttributeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.BRANCH_IDENTIFIER,   ReqIF10Factory.eINSTANCE::createAttributeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.BOOLEAN,             ReqIF10Factory.eINSTANCE::createAttributeDefinitionBoolean );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.DATE,                ReqIF10Factory.eINSTANCE::createAttributeDefinitionDate    );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.DOUBLE,              ReqIF10Factory.eINSTANCE::createAttributeDefinitionReal    );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.ENUMERATED,          ReqIF10Factory.eINSTANCE::createAttributeDefinitionString  );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.INPUT_STREAM,        ReqIF10Factory.eINSTANCE::createAttributeDefinitionString  );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.INTEGER,             ReqIF10Factory.eINSTANCE::createAttributeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.JAVA_OBJECT,         ReqIF10Factory.eINSTANCE::createAttributeDefinitionString  );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.LONG,                ReqIF10Factory.eINSTANCE::createAttributeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.STRING,              ReqIF10Factory.eINSTANCE::createAttributeDefinitionString  );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.STRING_WORD_ML,      ReqIF10Factory.eINSTANCE::createAttributeDefinitionXHTML   );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.put(NativeDataType.URI,                 ReqIF10Factory.eINSTANCE::createAttributeDefinitionString  );

      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.ARTIFACT_IDENTIFIER, ReqIF10Factory.eINSTANCE::createAttributeValueInteger );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.BRANCH_IDENTIFIER,   ReqIF10Factory.eINSTANCE::createAttributeValueInteger );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.BOOLEAN,             ReqIF10Factory.eINSTANCE::createAttributeValueBoolean );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.DATE,                ReqIF10Factory.eINSTANCE::createAttributeValueDate    );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.DOUBLE,              ReqIF10Factory.eINSTANCE::createAttributeValueReal    );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.ENUMERATED,          ReqIF10Factory.eINSTANCE::createAttributeValueString  );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.INPUT_STREAM,        ReqIF10Factory.eINSTANCE::createAttributeValueString  );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.INTEGER,             ReqIF10Factory.eINSTANCE::createAttributeValueInteger );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.JAVA_OBJECT,         ReqIF10Factory.eINSTANCE::createAttributeValueString  );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.LONG,                ReqIF10Factory.eINSTANCE::createAttributeValueInteger );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.STRING,              ReqIF10Factory.eINSTANCE::createAttributeValueString  );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.STRING_WORD_ML,      ReqIF10Factory.eINSTANCE::createAttributeValueXHTML   );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.put(NativeDataType.URI,                 ReqIF10Factory.eINSTANCE::createAttributeValueString  );

      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.put( NativeDataType.ARTIFACT_IDENTIFIER, ( value, attributeValue ) -> ((AttributeValueInteger) attributeValue).setTheValue( DataConverters.idToBigInteger( (Id) value ) ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.put( NativeDataType.BRANCH_IDENTIFIER,   ( value, attributeValue ) -> ((AttributeValueInteger) attributeValue).setTheValue( DataConverters.idToBigInteger( (Id) value ) ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.put( NativeDataType.BOOLEAN,             ( value, attributeValue ) -> ((AttributeValueBoolean) attributeValue).setTheValue( (Boolean) value ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.put( NativeDataType.DATE,                ( value, attributeValue ) -> ((AttributeValueDate)    attributeValue).setTheValue( DataConverters.dateToGregorianCalendar( (Date) value ) ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.put( NativeDataType.DOUBLE,              ( value, attributeValue ) -> ((AttributeValueReal)    attributeValue).setTheValue( (Double) value ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.put( NativeDataType.INTEGER,             ( value, attributeValue ) -> ((AttributeValueInteger) attributeValue).setTheValue( DataConverters.integerToBigInteger( (Integer) value ) ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.put( NativeDataType.LONG,                ( value, attributeValue ) -> ((AttributeValueInteger) attributeValue).setTheValue( DataConverters.longToBigInteger( (Long) value ) ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.put( NativeDataType.STRING,              ( value, attributeValue ) -> ((AttributeValueString)  attributeValue).setTheValue( (String) value ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.put( NativeDataType.STRING_WORD_ML,      ( value, attributeValue ) -> ((AttributeValueXHTML)   attributeValue).setTheValue( DataConverters.wordMlStringToXhtmlContent( (String) value ) ) );
      ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.put( NativeDataType.URI,                 ( value, attributeValue ) -> ((AttributeValueString)  attributeValue).setTheValue( (String) value ) );

      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.ARTIFACT_IDENTIFIER, ReqIF10Factory.eINSTANCE::createDatatypeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.BRANCH_IDENTIFIER,   ReqIF10Factory.eINSTANCE::createDatatypeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.BOOLEAN,             ReqIF10Factory.eINSTANCE::createDatatypeDefinitionBoolean );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.DATE,                ReqIF10Factory.eINSTANCE::createDatatypeDefinitionDate    );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.DOUBLE,              ReqIF10Factory.eINSTANCE::createDatatypeDefinitionReal    );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.ENUMERATED,          ReqIF10Factory.eINSTANCE::createDatatypeDefinitionString  );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.INPUT_STREAM,        ReqIF10Factory.eINSTANCE::createDatatypeDefinitionString  );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.INTEGER,             ReqIF10Factory.eINSTANCE::createDatatypeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.JAVA_OBJECT,         ReqIF10Factory.eINSTANCE::createDatatypeDefinitionString  );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.LONG,                ReqIF10Factory.eINSTANCE::createDatatypeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.STRING,              ReqIF10Factory.eINSTANCE::createDatatypeDefinitionString  );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.STRING_WORD_ML,      ReqIF10Factory.eINSTANCE::createDatatypeDefinitionXHTML   );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.put(NativeDataType.URI,                 ReqIF10Factory.eINSTANCE::createDatatypeDefinitionString  );

      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionDatatypeConverterMap.put(NativeDataType.ARTIFACT_IDENTIFIER, ReqIFSynchronizationArtifactBuilder::convertDataTypeDefinitionReqIfDatatypeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionDatatypeConverterMap.put(NativeDataType.BRANCH_IDENTIFIER,   ReqIFSynchronizationArtifactBuilder::convertDataTypeDefinitionReqIfDatatypeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionDatatypeConverterMap.put(NativeDataType.DOUBLE,              ReqIFSynchronizationArtifactBuilder::convertDataTypeDefinitionReqIfDatatypeDefinitionReal    );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionDatatypeConverterMap.put(NativeDataType.INPUT_STREAM,        ReqIFSynchronizationArtifactBuilder::convertDataTypeDefinitionReqIfDatatypeDefinitionString  );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionDatatypeConverterMap.put(NativeDataType.INTEGER,             ReqIFSynchronizationArtifactBuilder::convertDataTypeDefinitionReqIfDatatypeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionDatatypeConverterMap.put(NativeDataType.JAVA_OBJECT,         ReqIFSynchronizationArtifactBuilder::convertDataTypeDefinitionReqIfDatatypeDefinitionString  );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionDatatypeConverterMap.put(NativeDataType.LONG,                ReqIFSynchronizationArtifactBuilder::convertDataTypeDefinitionReqIfDatatypeDefinitionInteger );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionDatatypeConverterMap.put(NativeDataType.STRING,              ReqIFSynchronizationArtifactBuilder::convertDataTypeDefinitionReqIfDatatypeDefinitionString  );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionDatatypeConverterMap.put(NativeDataType.URI,                 ReqIFSynchronizationArtifactBuilder::convertDataTypeDefinitionReqIfDatatypeDefinitionString  );

      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.put(NativeDataType.ARTIFACT_IDENTIFIER, new ReqifDatatypeIntegerRecord(Long.MAX_VALUE, Long.MIN_VALUE) );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.put(NativeDataType.BRANCH_IDENTIFIER,   new ReqifDatatypeIntegerRecord(Long.MAX_VALUE, Long.MIN_VALUE) );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.put(NativeDataType.DOUBLE,              new ReqifDatatypeDoubleRecord( 64L, Double.MAX_VALUE, Double.MIN_VALUE ) );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.put(NativeDataType.INPUT_STREAM,        new ReqifDatatypeStringRecord( 1048576L ) );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.put(NativeDataType.INTEGER,             new ReqifDatatypeIntegerRecord( Long.valueOf( Integer.MAX_VALUE ), Long.valueOf( Integer.MIN_VALUE )) );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.put(NativeDataType.JAVA_OBJECT,         new ReqifDatatypeStringRecord( 1024L ) );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.put(NativeDataType.LONG,                new ReqifDatatypeIntegerRecord(Long.MAX_VALUE, Long.MIN_VALUE) );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.put(NativeDataType.STRING,              new ReqifDatatypeStringRecord( 8192L ) );
      ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.put(NativeDataType.URI,                 new ReqifDatatypeStringRecord( 2048L ) );
      //@formatter:on
   }

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

      //Get Groves
      //@formatter:off
      var headerGrove             = (HeaderGrove)             synchronizationArtifact.getGrove(IdentifierType.HEADER);
      var datatypeDefinitionGrove = (DataTypeDefinitionGrove) synchronizationArtifact.getGrove(IdentifierType.DATA_TYPE_DEFINITION);
      var specTypeGrove           = (SpecTypeGrove)           synchronizationArtifact.getGrove(IdentifierType.SPECIFICATION_TYPE);
      var specObjectTypeGrove     = (SpecObjectTypeGrove)     synchronizationArtifact.getGrove(IdentifierType.SPEC_OBJECT_TYPE);
      var specificationGrove      = (SpecificationGrove)      synchronizationArtifact.getGrove(IdentifierType.SPECIFICATION);
      var specObjectGrove         = (SpecObjectGrove)         synchronizationArtifact.getGrove(IdentifierType.SPEC_OBJECT);
      var attributeValueGrove     = (AttributeValueGrove)     synchronizationArtifact.getGrove(IdentifierType.ATTRIBUTE_VALUE);
      //@formatter:on

      // HeaderGroveThing

      headerGrove.stream().forEach(header -> {
         this.reqIf.setTheHeader((ReqIFHeader) header.getForeignThing());
      });

      // Content

      var reqifContent = ReqIF10Factory.eINSTANCE.createReqIFContent();

      this.reqIf.setCoreContent(reqifContent);

      //Data Type Definitions

      var reqifDatatypeDefinitionList = reqifContent.getDatatypes();

      datatypeDefinitionGrove.stream().forEach(datatypeDefinition -> reqifDatatypeDefinitionList.add(
         (DatatypeDefinition) datatypeDefinition.getForeignThing()));

      //Spec Types & Spec Object Types

      var reqifSpecTypeList = reqifContent.getSpecTypes();

      Stream.concat(specTypeGrove.stream(), specObjectTypeGrove.stream()).forEach(groveThing -> {

         var commonObjectType = (CommonObjectTypeGroveThing) groveThing;
         var reqifSpecType = (SpecType) groveThing.getForeignThing();

         reqifSpecTypeList.add(reqifSpecType);

         var reqifAttributeDefinitionList = reqifSpecType.getSpecAttributes();

         commonObjectType.streamAttributeDefinitions().forEach(attributeDefinition -> {

            reqifAttributeDefinitionList.add((AttributeDefinition) attributeDefinition.getForeignThing());

            var reqifAttributeDefinition = (AttributeDefinition) attributeDefinition.getForeignThing();

            var datatypeDefinition = attributeDefinition.getDataTypeDefinition();
            var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();
            var reqifDatatypeDefinition = (DatatypeDefinition) datatypeDefinition.getForeignThing();

            ReqIFSynchronizationArtifactBuilder.reqifAttachDatatypeDefinitionToAttributeDefinitionMap.accept(
               nativeDataType, reqifAttributeDefinition, reqifDatatypeDefinition);

         });

      });

      //Specifications

      var reqifSpecificationList = reqifContent.getSpecifications();

      specificationGrove.stream().forEach(groveThing -> {

         var specification = (SpecificationGroveThing) groveThing;
         var reqifSpecification = (Specification) groveThing.getForeignThing();

         var commonObjectType = specification.getCommonObjectType();
         var reqifSpecificationType = (SpecificationType) commonObjectType.getForeignThing();

         reqifSpecification.setType(reqifSpecificationType);

         reqifSpecificationList.add(reqifSpecification);
      });

      //Spec Objects

      var reqifSpecObjectList = reqifContent.getSpecObjects();

      specObjectGrove.stream().forEach(groveThing -> {

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

      attributeValueGrove.stream().forEach(groveThing -> {

         var attributeValue = (AttributeValueGroveThing) groveThing;
         var reqifAttributeValue = (AttributeValue) attributeValue.getForeignThing();

         /*
          * Attach value to SpecificationGroveThing or SpecObjectGroveThing
          */

         var commonObject = attributeValue.getParent();
         var reqifSpecElementWithAttributes = (SpecElementWithAttributes) commonObject.getForeignThing();
         var reqifAttributeValueList = reqifSpecElementWithAttributes.getValues();
         reqifAttributeValueList.add(reqifAttributeValue);

         /*
          * Attach value to its attribute definition
          */

         var attributeDefinition = attributeValue.getAttributeDefinition();
         var datatypeDefinition = attributeDefinition.getDataTypeDefinition();
         var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();
         var reqifAttributeDefinition = (AttributeDefinition) attributeDefinition.getForeignThing();

         ReqIFSynchronizationArtifactBuilder.reqifAttachAttributeDefinitionToAttributeValueMap.accept(nativeDataType,
            reqifAttributeValue, reqifAttributeDefinition);

      });

      //Build the specification hierarchies
      //@formatter:off
      specObjectGrove.streamRootGroveThingKeys().forEach(specificationIdentifier -> {

         //SpecificationGroveThing
         var specification              = specObjectGrove.getSpecification(specificationIdentifier);
         var reqifSpecification         = (Specification) specification.getForeignThing();
         var specificationReqifChildren = reqifSpecification.getChildren();
         specification.setForeignHierarchy( specificationReqifChildren );

         specObjectGrove.forEachGroveThing( specificationIdentifier, ( parentIdentifier, commonObjectIdentifier ) -> {

               //Spec Object
               var specObject          = specObjectGrove.getSpecObject( specificationIdentifier, commonObjectIdentifier );
               var reqifSpecObject     = (SpecObject) specObject.getForeignThing();
               var reqifSpecHierarchy  = ReqIF10Factory.eINSTANCE.createSpecHierarchy();
               var reqifChildren       = reqifSpecHierarchy.getChildren();

               reqifSpecHierarchy.setObject( reqifSpecObject );
               specObject.setForeignHierarchy( reqifChildren );

               var parentSpecObject    = specificationIdentifier.equals( parentIdentifier)
                                            ? specObjectGrove.getSpecification( specificationIdentifier )
                                            : specObjectGrove.getSpecObject( specificationIdentifier, parentIdentifier );
               var parentReqifChildren = (EList<SpecHierarchy>) parentSpecObject.getForeignHierarchy();

               parentReqifChildren.add( reqifSpecHierarchy );
         });

      });

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

   /**
    * Converts the native OSEE attribute value into a foreign ReqIF {@link AttributeValue} for Synchronization Artifact
    * {@link AttributeValueGroveThing}s.
    *
    * @param groveThing the Synchronization Artifact {@link AttributeValueGroveThing} to be converted to a ReqIF value.
    */

   private static void convertAttributeValue(GroveThing groveThing) {
      assert Objects.nonNull(groveThing) && (groveThing instanceof AttributeValueGroveThing);

      var attributeValue = (AttributeValueGroveThing) groveThing;
      var nativeAttributeValue = groveThing.getNativeThing();
      var attributeDefinition = attributeValue.getAttributeDefinition();
      var datatypeDefinition = attributeDefinition.getDataTypeDefinition();
      var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();

      var reqifAttributeValue = ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.get(nativeDataType);

      if (ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.containsKey(nativeDataType)) {
         ReqIFSynchronizationArtifactBuilder.reqifAttributeValueSetterMap.accept(nativeDataType, nativeAttributeValue,
            reqifAttributeValue);
      }

      attributeValue.setForeignThing(reqifAttributeValue);
   }

   /**
    * Converts the native OSEE {@link AttributeTypeToken} into a foreign ReqIF {@link AttributeDefinition} for
    * Synchronization Artifact {@link AttributeDefinitionGroveThing}s.
    *
    * @param groveThing the Synchronization Artifact {@link AttributeDefinitionGroveThing} thing to be converted for
    * ReqIF.
    */

   private static void convertAttributeDefinition(GroveThing groveThing) {
      assert Objects.nonNull(groveThing) && (groveThing instanceof AttributeDefinitionGroveThing);

      var attributeDefinition = (AttributeDefinitionGroveThing) groveThing;
      var nativeAttributeTypeToken = (AttributeTypeToken) groveThing.getNativeThing();

      var datatypeDefinition = attributeDefinition.getDataTypeDefinition();
      var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();

      var reqifAttributeDefinition =
         ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionFactoryMap.get(nativeDataType);

      // Set common attribute definition attributes

      ReqIFSynchronizationArtifactBuilder.convertAttributeDefinitionReqIfAttributeDefinition(attributeDefinition,
         reqifAttributeDefinition);

      // Set attribute definition type specific attributes

      if (ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.containsKey(
         nativeDataType)) {
         ReqIFSynchronizationArtifactBuilder.reqifAttributeDefinitionDatatypeConverterMap.accept(nativeDataType,
            attributeDefinition, reqifAttributeDefinition);
      }

      attributeDefinition.setForeignThing(reqifAttributeDefinition);
   }

   /**
    * Performs the conversion from a native OSEE {@link AttributeTypeToken} to a foreign ReqIF
    * {@link AttributeDefinition} for the ReqIF attributes that are common to all ReqIF attribute definitions for a
    * Synchronization Artifact {@link AttributeDefinitionGroveThing}.
    *
    * @param attributeDefinitionGroveThing the {@link AttributeDefinitionGroveThing} to convert an attribute definition
    * for.
    * @param reqifAttributeDefinition the foreign {@link AttributeDefinition} to be initialized.
    */

   private static void convertAttributeDefinitionReqIfAttributeDefinition(AttributeDefinitionGroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {
      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      var description = nativeAttributeTypeToken.getDescription();

      if (description == null || description.isEmpty()) {
         //@formatter:off
         description = new StringBuilder( 512 )
                              .append( "OSEE " ).append( nativeAttributeTypeToken.getName() ).append( " Attribute Definition")
                              .toString();
         //@formatter:on

      }

      reqifAttributeDefinition.setDesc(description);
      reqifAttributeDefinition.setIdentifier(attributeDefinitionGroveThing.getGroveThingKey().toString());
      reqifAttributeDefinition.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifAttributeDefinition.setLongName(nativeAttributeTypeToken.getName());
      reqifAttributeDefinition.setEditable(!nativeAttributeTypeToken.notEditable());
   }

   /**
    * Performs the conversion from a native OSEE {@link AttributeTypeBoolean} to a foreign ReqIF
    * {@link AttributeDefinitionBoolean} for a Synchronization Artifact {@link AttributeDefinitionGroveThing}.
    *
    * @param attributeDefinitionGroveThing the {@link AttributeDefinitionGroveThing} to convert an attribute definition
    * for.
    * @param reqifAttributeDefinition the foreign {@link AttributeDefinition} to set the default value of.
    */

   private static void convertAttributeDefinitionReqIfAttributeDefinitionBoolean(AttributeDefinitionGroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeBoolean);

      var nativeAttributeTypeBoolean = (AttributeTypeBoolean) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeBoolean.getBaseAttributeTypeDefaultValue();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionBoolean);

      var reqifAttributeDefinitionBoolean = (AttributeDefinitionBoolean) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getDataTypeDefinition();
      var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();

      assert nativeDataType.equals(NativeDataType.BOOLEAN);

      var defaultAttributeValue =
         (AttributeValueBoolean) ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.get(nativeDataType);

      defaultAttributeValue.setTheValue(defaultValue);

      reqifAttributeDefinitionBoolean.setDefaultValue(defaultAttributeValue);
   }

   /**
    * Performs the conversion from a native OSEE {@link AttributeTypeDate} to a foreign ReqIF
    * {@link AttributeDefinitionDate} for a Synchronization Artifact {@link AttributeDefinitionGroveThing}.
    *
    * @param attributeDefinitionGroveThing the {@link AttributeDefinitionGroveThing} to convert an attribute definition
    * for.
    * @param reqifAttributeDefinition the foreign {@link AttributeDefinition} to set the default value of.
    */

   private static void convertAttributeDefinitionReqIfAttributeDefinitionDate(AttributeDefinitionGroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeDate);

      var nativeAttributeTypeDate = (AttributeTypeDate) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeDate.getBaseAttributeTypeDefaultValue();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionDate);

      var reqifAttributeDefinitionDate = (AttributeDefinitionDate) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getDataTypeDefinition();
      var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();

      assert nativeDataType.equals(NativeDataType.DATE);

      var defaultAttributeValue =
         (AttributeValueDate) ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.get(nativeDataType);

      defaultAttributeValue.setTheValue(DataConverters.dateToGregorianCalendar(defaultValue));

      reqifAttributeDefinitionDate.setDefaultValue(defaultAttributeValue);
   }

   /**
    * Performs the conversion from a native OSEE {@link AttributeTypeDouble} to a foreign ReqIF
    * {@link AttributeDefinitionDouble} for a Synchronization Artifact {@link AttributeDefinitionGroveThing}.
    *
    * @param attributeDefinitionGroveThing the {@link AttributeDefinitionGroveThing} to convert an attribute definition
    * for.
    * @param reqifAttributeDefinition the foreign {@link AttributeDefinition} to set the default value of.
    */

   private static void convertAttributeDefinitionReqIfAttributeDefinitionDouble(AttributeDefinitionGroveThing attributeDefinitionGroveThing, org.eclipse.rmf.reqif10.AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeDouble);

      var nativeAttributeTypeDouble = (AttributeTypeDouble) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeDouble.getBaseAttributeTypeDefaultValue();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionReal);

      var reqifAttributeDefinitionReal = (AttributeDefinitionReal) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getDataTypeDefinition();
      var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();

      assert nativeDataType.equals(NativeDataType.DOUBLE);

      var defaultAttributeValue =
         (AttributeValueReal) ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.get(nativeDataType);

      defaultAttributeValue.setTheValue(defaultValue);

      reqifAttributeDefinitionReal.setDefaultValue(defaultAttributeValue);
   }

   /**
    * Performs the conversion from a native OSEE {@link AttributeTypeArtifactId} to a foreign ReqIF
    * {@link AttributeDefinitionInteger} for a Synchronization Artifact {@link AttributeDefinitionGroveThing}.
    *
    * @param attributeDefinitionGroveThing the {@link AttributeDefinitionGroveThing} to convert an attribute definition
    * for.
    * @param reqifAttributeDefinition the foreign {@link AttributeDefinition} to set the default value of.
    */

   private static void convertAttributeDefinitionReqIfAttributeDefinitionIntegerForArtifactId(AttributeDefinitionGroveThing attributeDefinitionGroveThing, org.eclipse.rmf.reqif10.AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeArtifactId);

      var nativeAttributeTypeArtifactId = (AttributeTypeArtifactId) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeArtifactId.getBaseAttributeTypeDefaultValue().getId();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionInteger);

      var reqifAttributeDefinitionInteger = (AttributeDefinitionInteger) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getDataTypeDefinition();
      var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();

      assert nativeDataType.equals(NativeDataType.ARTIFACT_IDENTIFIER);

      var defaultAttributeValue =
         (AttributeValueInteger) ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.get(nativeDataType);

      defaultAttributeValue.setTheValue(BigInteger.valueOf(defaultValue));

      reqifAttributeDefinitionInteger.setDefaultValue(defaultAttributeValue);
   }

   /**
    * Performs the conversion from a native OSEE {@link AttributeTypeBranchId} to a foreign ReqIF
    * {@link AttributeDefinitionInteger} for a Synchronization Artifact {@link AttributeDefinitionGroveThing}.
    *
    * @param attributeDefinitionGroveThing the {@link AttributeDefinitionGroveThing} to convert an attribute definition
    * for.
    * @param reqifAttributeDefinition the foreign {@link AttributeDefinition} to set the default value of.
    */

   private static void convertAttributeDefinitionReqIfAttributeDefinitionIntegerForBranchId(AttributeDefinitionGroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeBranchId);

      var nativeAttributeTypeBranchId = (AttributeTypeBranchId) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeBranchId.getBaseAttributeTypeDefaultValue().getId();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionInteger);

      var reqifAttributeDefinitionInteger = (AttributeDefinitionInteger) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getDataTypeDefinition();
      var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();

      assert nativeDataType.equals(NativeDataType.ARTIFACT_IDENTIFIER);

      var defaultAttributeValue =
         (AttributeValueInteger) ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.get(nativeDataType);

      defaultAttributeValue.setTheValue(BigInteger.valueOf(defaultValue));

      reqifAttributeDefinitionInteger.setDefaultValue(defaultAttributeValue);
   }

   /**
    * Performs the conversion from a native OSEE {@link AttributeTypeInteger} to a foreign ReqIF
    * {@link AttributeDefinitionInteger} for a Synchronization Artifact {@link AttributeDefinitionGroveThing}.
    *
    * @param attributeDefinitionGroveThing the {@link AttributeDefinitionGroveThing} to convert an attribute definition
    * for.
    * @param reqifAttributeDefinition the foreign {@link AttributeDefinition} to set the default value of.
    */

   private static void convertAttributeDefinitionReqIfAttributeDefinitionIntegerForInteger(AttributeDefinitionGroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeInteger);

      var nativeAttributeTypeInteger = (AttributeTypeInteger) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeInteger.getBaseAttributeTypeDefaultValue();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionInteger);

      var reqifAttributeDefinitionInteger = (AttributeDefinitionInteger) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getDataTypeDefinition();
      var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();

      assert nativeDataType.equals(NativeDataType.INTEGER);

      var defaultAttributeValue =
         (AttributeValueInteger) ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.get(nativeDataType);

      defaultAttributeValue.setTheValue(BigInteger.valueOf(defaultValue));

      reqifAttributeDefinitionInteger.setDefaultValue(defaultAttributeValue);
   }

   /**
    * Performs the conversion from a native OSEE {@link AttributeTypeLong} to a foreign ReqIF
    * {@link AttributeDefinitionInteger} for a Synchronization Artifact {@link AttributeDefinitionGroveThing}.
    *
    * @param attributeDefinitionGroveThing the {@link AttributeDefinitionGroveThing} to convert an attribute definition
    * for.
    * @param reqifAttributeDefinition the foreign {@link AttributeDefinition} to set the default value of.
    */

   private static void convertAttributeDefinitionReqIfAttributeDefinitionIntegerForLong(AttributeDefinitionGroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeLong);

      var nativeAttributeTypeLong = (AttributeTypeLong) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeLong.getBaseAttributeTypeDefaultValue();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionInteger);

      var reqifAttributeDefinitionInteger = (AttributeDefinitionInteger) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getDataTypeDefinition();
      var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();

      assert nativeDataType.equals(NativeDataType.LONG);

      var defaultAttributeValue =
         (AttributeValueInteger) ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.get(nativeDataType);

      defaultAttributeValue.setTheValue(BigInteger.valueOf(defaultValue));

      reqifAttributeDefinitionInteger.setDefaultValue(defaultAttributeValue);
   }

   /**
    * Performs the conversion from a native OSEE {@link AttributeTypeString} to a foreign ReqIF
    * {@link AttributeDefinitionString} for a Synchronization Artifact {@link AttributeDefinitionGroveThing}.
    *
    * @param attributeDefinitionGroveThing the {@link AttributeDefinitionGroveThing} to convert an attribute definition
    * for.
    * @param reqifAttributeDefinition the foreign {@link AttributeDefinition} to set the default value of.
    */

   private static void convertAttributeDefinitionReqIfAttributeDefinitionString(AttributeDefinitionGroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeString) : "Actual Type: " + nativeAttributeTypeToken.getClass().getName();

      var nativeAttributeTypeString = (AttributeTypeString) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeString.getBaseAttributeTypeDefaultValue();

      if (defaultValue.isEmpty()) {
         return;
      }

      assert (reqifAttributeDefinition instanceof AttributeDefinitionString);

      var reqifAttributeDefinitionString = (AttributeDefinitionString) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getDataTypeDefinition();
      var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();

      var defaultAttributeValue =
         (AttributeValueString) ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.get(nativeDataType);

      defaultAttributeValue.setTheValue(defaultValue);

      reqifAttributeDefinitionString.setDefaultValue(defaultAttributeValue);
   }

   /**
    * Performs the conversion from a native OSEE {@link AttributeTypeString} with Word ML content to a foreign ReqIF
    * {@link AttributeDefinitionXHTML} for a Synchronization Artifact {@link AttributeDefinitionGroveThing}.
    *
    * @param attributeDefinitionGroveThing the {@link AttributeDefinitionGroveThing} to convert an attribute definition
    * for.
    * @param reqifAttributeDefinition the foreign {@link AttributeDefinition} to set the default value of.
    */

   private static void convertAttributeDefinitionReqIfAttributeDefinitionXHTML(AttributeDefinitionGroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeString) : "Actual Type: " + nativeAttributeTypeToken.getClass().getName();

      var nativeAttributeTypeString = (AttributeTypeString) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeString.getBaseAttributeTypeDefaultValue();

      if (defaultValue.isEmpty()) {
         return;
      }

      assert (reqifAttributeDefinition instanceof AttributeDefinitionXHTML);

      var reqifAttributeDefinitionXHTML = (AttributeDefinitionXHTML) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getDataTypeDefinition();
      var nativeDataType = (NativeDataType) datatypeDefinition.getNativeThing();

      var defaultAttributeValue =
         (AttributeValueXHTML) ReqIFSynchronizationArtifactBuilder.reqifAttributeValueFactoryMap.get(nativeDataType);

      var xhtmlContent = DataConverters.wordMlStringToXhtmlContent(defaultValue);

      defaultAttributeValue.setTheValue(xhtmlContent);

      reqifAttributeDefinitionXHTML.setDefaultValue(defaultAttributeValue);
   }

   /**
    * Converter method for {@link DataTypeDefinitionGroveThing} things. This method creates the foreign ReqIF
    * {@link DatatypeDefinition} from the native {@link NativeDataType}.
    *
    * @param groveThing the {@link DataTypeDefinitionGroveThing} thing to be converted.
    */

   private static void convertDataTypeDefinition(GroveThing groveThing) {
      assert Objects.nonNull(groveThing) && (groveThing instanceof DataTypeDefinitionGroveThing);

      var dataTypeDefinition = (DataTypeDefinitionGroveThing) groveThing;
      var nativeDataType = (NativeDataType) groveThing.getNativeThing();

      var reqifDatatypeDefinition =
         ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionFactoryMap.get(nativeDataType);

      // Set common data type definition attributes

      ReqIFSynchronizationArtifactBuilder.convertDataTypeDefinitionReqIfDatatypeDefinition(dataTypeDefinition,
         reqifDatatypeDefinition);

      // Set data type specific attributes

      if (ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionDatatypeConverterMap.containsKey(nativeDataType)) {
         ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionDatatypeConverterMap.accept(nativeDataType,
            dataTypeDefinition, reqifDatatypeDefinition);
      }

      dataTypeDefinition.setForeignThing(reqifDatatypeDefinition);

   }

   /**
    * Set common attributes for {@link DatatypeDefinition} things.
    *
    * @param dataTypeDefinitionGroveThing the {@link DataTypeDefinitionGroveThing} thing to get attribute values from.
    * @param reqifDatatypeDefinition the {@link DatatypeDefinition} to have attributes set.
    */

   private static void convertDataTypeDefinitionReqIfDatatypeDefinition(DataTypeDefinitionGroveThing dataTypeDefinitionGroveThing, DatatypeDefinition reqifDatatypeDefinition) {
      var nativeDataType = (NativeDataType) dataTypeDefinitionGroveThing.getNativeThing();

      reqifDatatypeDefinition.setDesc(nativeDataType.toString());
      reqifDatatypeDefinition.setIdentifier(dataTypeDefinitionGroveThing.getGroveThingKey().toString());
      reqifDatatypeDefinition.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifDatatypeDefinition.setLongName(nativeDataType.name());

   }

   /**
    * Set data type integer specific attributes for {@link DatatypeDefinitionInteger} things.
    *
    * @param dataTypeDefinitionGroveThing the {@link DataTypeDefinitionGroveThing} thing to get attribute values from.
    * @param reqifDatatypeDefinition the {@link DatatypeDefinition} to have attributes set.
    */

   private static void convertDataTypeDefinitionReqIfDatatypeDefinitionInteger(DataTypeDefinitionGroveThing dataTypeDefinitionGroveThing, DatatypeDefinition reqifDatatypeDefinition) {

      assert (reqifDatatypeDefinition instanceof DatatypeDefinitionInteger);

      var reqifDatatypeDefinitionInteger = (DatatypeDefinitionInteger) reqifDatatypeDefinition;
      var nativeDataType = (NativeDataType) dataTypeDefinitionGroveThing.getNativeThing();

      assert ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.containsKey(nativeDataType);

      var reqifDatatypeRecord =
         ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.get(nativeDataType);

      assert (reqifDatatypeRecord instanceof ReqifDatatypeIntegerRecord);

      var reqifDatatypeIntegerRecord = (ReqifDatatypeIntegerRecord) reqifDatatypeRecord;

      reqifDatatypeDefinitionInteger.setMax(BigInteger.valueOf(reqifDatatypeIntegerRecord.max));
      reqifDatatypeDefinitionInteger.setMin(BigInteger.valueOf(reqifDatatypeIntegerRecord.min));
   }

   /**
    * Set data type real specific attributes for {@link DatatypeDefinitionReal} things.
    *
    * @param dataTypeDefinitionGroveThing the {@link DataTypeDefinitionGroveThing} thing to get attribute values from.
    * @param reqifDatatypeDefinition the {@link DatatypeDefinition} to have attributes set.
    */

   private static void convertDataTypeDefinitionReqIfDatatypeDefinitionReal(DataTypeDefinitionGroveThing dataTypeDefinitionGroveThing, DatatypeDefinition reqifDatatypeDefinition) {

      assert (reqifDatatypeDefinition instanceof DatatypeDefinitionReal);

      var reqifDatatypeDefinitionReal = (DatatypeDefinitionReal) reqifDatatypeDefinition;
      var nativeDataType = (NativeDataType) dataTypeDefinitionGroveThing.getNativeThing();

      assert ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.containsKey(nativeDataType);

      var reqifDatatypeRecord =
         ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.get(nativeDataType);

      assert (reqifDatatypeRecord instanceof ReqifDatatypeDoubleRecord);

      var reqifDatatypeDoubleRecord = (ReqifDatatypeDoubleRecord) reqifDatatypeRecord;

      reqifDatatypeDefinitionReal.setAccuracy(BigInteger.valueOf(reqifDatatypeDoubleRecord.accuracy));
      reqifDatatypeDefinitionReal.setMax(reqifDatatypeDoubleRecord.max);
      reqifDatatypeDefinitionReal.setMin(reqifDatatypeDoubleRecord.min);
   }

   /**
    * Set data type string specific attributes for {@link DatatypeDefinitionString} things.
    *
    * @param dataTypeDefinitionGroveThing the {@link DataTypeDefinitionGroveThing} thing to get attribute values from.
    * @param reqifDatatypeDefinition the {@link DatatypeDefinition} to have attributes set.
    */

   private static void convertDataTypeDefinitionReqIfDatatypeDefinitionString(DataTypeDefinitionGroveThing dataTypeDefinitionGroveThing, DatatypeDefinition reqifDatatypeDefinition) {

      assert (reqifDatatypeDefinition instanceof DatatypeDefinitionString);

      var reqifDatatypeDefinitionString = (DatatypeDefinitionString) reqifDatatypeDefinition;
      var nativeDataType = (NativeDataType) dataTypeDefinitionGroveThing.getNativeThing();

      assert ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.containsKey(nativeDataType);

      var reqifDatatypeRecord =
         ReqIFSynchronizationArtifactBuilder.reqifDatatypeDefinitionRecordMap.get(nativeDataType);

      assert (reqifDatatypeRecord instanceof ReqifDatatypeStringRecord);

      var reqifDatatypeStringRecord = (ReqifDatatypeStringRecord) reqifDatatypeRecord;

      reqifDatatypeDefinitionString.setMaxLength(BigInteger.valueOf(reqifDatatypeStringRecord.maxLength));
   }

   /**
    * Converter method for {@link HeaderGroveThing}s. This method creates the foreign ReqIF {@link HeaderGroveThing}
    * from the native {@link HeaderGroveThing} thing.
    *
    * @param groveThing the {@link HeaderGroveThing} to be converted.
    */

   private static void convertHeader(GroveThing groveThing) {
      assert Objects.nonNull(groveThing) && (groveThing instanceof HeaderGroveThing);

      var header = (HeaderGroveThing) groveThing;
      var reqifHeader = ReqIF10Factory.eINSTANCE.createReqIFHeader();

      reqifHeader.setComment(header.getComment());
      reqifHeader.setIdentifier(header.getGroveThingKey().toString());
      reqifHeader.setRepositoryId(header.getRepositoryId());
      reqifHeader.setReqIFVersion("1.0.1");
      reqifHeader.setSourceToolId(header.getSourceToolId());
      reqifHeader.setReqIFToolId(
         ReqIFSynchronizationArtifactBuilder.class.getName() + ":" + ReqIFSynchronizationArtifactBuilder.version);
      reqifHeader.setCreationTime(header.getTime());
      reqifHeader.setTitle(header.getTitle());

      header.setForeignThing(reqifHeader);
   }

   /**
    * Converter method for {@link SpecificationGroveThing}s. This method creates the foreign ReqIF {@link Specification}
    * from the native {@link ArtifactReadable}.
    *
    * @param groveThing the {@link SpecificaitionGroveThing} to be converted.
    */

   private static void convertSpecification(GroveThing groveThing) {
      assert Objects.nonNull(groveThing) && (groveThing instanceof SpecificationGroveThing);

      var specification = (SpecificationGroveThing) groveThing;
      var nativeArtifactReadable = (ArtifactReadable) groveThing.getNativeThing();

      var reqifSpecification = ReqIF10Factory.eINSTANCE.createSpecification();

      //@formatter:off
      var description = new StringBuilder( 512 )
                           .append( "OSEE " ).append( nativeArtifactReadable.getName() ).append( " SpecificationGroveThing")
                           .toString();
      //@formatter:on

      reqifSpecification.setDesc(description);
      reqifSpecification.setIdentifier(specification.getGroveThingKey().toString());
      reqifSpecification.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifSpecification.setLongName(nativeArtifactReadable.getName());

      specification.setForeignThing(reqifSpecification);
   }

   /**
    * Converter method for {@link SpecObjectGroveThing}s. This method creates the foreign ReqIF {@link SpecObject} from
    * the native {@link ArtifactReadable}.
    *
    * @param groveThing the {@link SpecObjectGroveThing} to be converted.
    */

   private static void convertSpecObject(GroveThing groveThing) {

      if (groveThing instanceof SpecificationGroveThing) {
         return;
      }

      assert Objects.nonNull(groveThing) && (groveThing instanceof SpecObjectGroveThing);

      var specObject = (SpecObjectGroveThing) groveThing;
      var nativeArtifactReadable = (ArtifactReadable) groveThing.getNativeThing();

      var reqifSpecObject = ReqIF10Factory.eINSTANCE.createSpecObject();

      reqifSpecObject.setDesc(nativeArtifactReadable.getClass().getName());
      reqifSpecObject.setIdentifier(groveThing.getGroveThingKey().toString());
      reqifSpecObject.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifSpecObject.setLongName(nativeArtifactReadable.getName());

      specObject.setForeignThing(reqifSpecObject);
   }

   /**
    * Converter method for {@link SpecObjectTypeGroveThing} things. This method creates the foreign ReqIF
    * {@link SpecObjecType} from the native {@link ArtifactTypeToken}.
    *
    * @param groveThing the {@link SpecObjectTypeGroveThing} thing to be converted.
    */

   private static void convertSpecObjectType(GroveThing groveThing) {
      assert Objects.nonNull(groveThing) && (groveThing instanceof SpecObjectTypeGroveThing);

      var specObjectType = (SpecObjectTypeGroveThing) groveThing;
      var nativeArtifactTypeToken = (ArtifactTypeToken) groveThing.getNativeThing();

      var reqifSpecObjectType = ReqIF10Factory.eINSTANCE.createSpecObjectType();

      //@formatter:off
      var description = new StringBuilder( 512 )
                           .append( "OSEE " ).append( nativeArtifactTypeToken.getName() ).append( " Spec Object Type")
                           .toString();
      //@formatter:on

      reqifSpecObjectType.setDesc(description);
      reqifSpecObjectType.setIdentifier(specObjectType.getGroveThingKey().toString());
      reqifSpecObjectType.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifSpecObjectType.setLongName(nativeArtifactTypeToken.getName());

      specObjectType.setForeignThing(reqifSpecObjectType);
   }

   /**
    * Converter method for {@link SpecTypeGroveThing} things. This method creates the foreign ReqIF {@link SpecType}
    * from the native {@link ArtifactTypeToken}.
    *
    * @param groveThing the {@link SpecTypeGroveThing} thing to be converted.
    */

   private static void convertSpecType(GroveThing groveThing) {
      assert Objects.nonNull(groveThing) && (groveThing instanceof SpecTypeGroveThing);

      var specType = (SpecTypeGroveThing) groveThing;
      var nativeArtifactTypeToken = (ArtifactTypeToken) groveThing.getNativeThing();

      var reqifSpecificationType = ReqIF10Factory.eINSTANCE.createSpecificationType();

      //@formatter:off
      var description = new StringBuilder( 512 )
                           .append( "OSEE " ).append( nativeArtifactTypeToken.getName() ).append( " SpecificationGroveThing Type")
                           .toString();
      //@formatter:on

      reqifSpecificationType.setDesc(description);
      reqifSpecificationType.setIdentifier(specType.getGroveThingKey().toString());
      reqifSpecificationType.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifSpecificationType.setLongName(nativeArtifactTypeToken.getName());

      specType.setForeignThing(reqifSpecificationType);
   }

}

/* EOF */
