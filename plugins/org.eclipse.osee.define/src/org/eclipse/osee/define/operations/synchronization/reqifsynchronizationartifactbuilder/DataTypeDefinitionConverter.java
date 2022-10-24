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

import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.eclipse.osee.define.operations.synchronization.UnexpectedGroveThingTypeException;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.forest.denizens.NativeDataType;
import org.eclipse.osee.define.operations.synchronization.forest.denizens.NativeDataTypeKey;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.framework.jdk.core.util.EnumBiConsumerMap;
import org.eclipse.osee.framework.jdk.core.util.EnumSupplierMap;
import org.eclipse.rmf.reqif10.DatatypeDefinition;
import org.eclipse.rmf.reqif10.DatatypeDefinitionInteger;
import org.eclipse.rmf.reqif10.DatatypeDefinitionReal;
import org.eclipse.rmf.reqif10.DatatypeDefinitionString;
import org.eclipse.rmf.reqif10.ReqIF10Factory;

/**
 * Class contains the converter method to create the ReqIF {@link DatatypeDefinition} things for each member of the
 * {@link NativeDataType} enumeration.
 *
 * @author Loren K. Ashley
 */

class DataTypeDefinitionConverter {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private DataTypeDefinitionConverter() {
   }

   /**
    * Interface used to mark classes as allowable entries in the {@link #reqifDatatypeDefinitionRecordMap}.
    */

   private interface ReqifDatatypeRecord {

      /*
       * Marks classes as allowable entries in the reqifDatatypeDefinitionRecordMap.
       */
   };

   /**
    * Record used to hold Double specific ReqIF Data Type Definition attribute values.
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
    * Record used to hold Integer specific ReqIF Data Type Definition attribute values.
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
    * Record used to hold String specific ReqIF Data Type Definition attribute values.
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
    * Map of {@link Supplier} implementations to create the foreign things that extend the {@link DatatypeDefinition}
    * class appropriate for each {@link NativeDataType}.
    */

   //@formatter:off
   private static final EnumSupplierMap<NativeDataType, DatatypeDefinition> reqifDatatypeDefinitionFactoryMap =
      EnumSupplierMap.ofEntries
         (
           NativeDataType.class,
           Map.entry( NativeDataType.ARTIFACT_IDENTIFIER, ReqIF10Factory.eINSTANCE::createDatatypeDefinitionInteger     ),
           Map.entry( NativeDataType.BRANCH_IDENTIFIER,   ReqIF10Factory.eINSTANCE::createDatatypeDefinitionInteger     ),
           Map.entry( NativeDataType.BOOLEAN,             ReqIF10Factory.eINSTANCE::createDatatypeDefinitionBoolean     ),
           Map.entry( NativeDataType.DATE,                ReqIF10Factory.eINSTANCE::createDatatypeDefinitionDate        ),
           Map.entry( NativeDataType.DOUBLE,              ReqIF10Factory.eINSTANCE::createDatatypeDefinitionReal        ),
           Map.entry( NativeDataType.ENUMERATED,          ReqIF10Factory.eINSTANCE::createDatatypeDefinitionEnumeration ),
           Map.entry( NativeDataType.INPUT_STREAM,        ReqIF10Factory.eINSTANCE::createDatatypeDefinitionString      ),
           Map.entry( NativeDataType.INTEGER,             ReqIF10Factory.eINSTANCE::createDatatypeDefinitionInteger     ),
           Map.entry( NativeDataType.JAVA_OBJECT,         ReqIF10Factory.eINSTANCE::createDatatypeDefinitionString      ),
           Map.entry( NativeDataType.LONG,                ReqIF10Factory.eINSTANCE::createDatatypeDefinitionInteger     ),
           Map.entry( NativeDataType.STRING,              ReqIF10Factory.eINSTANCE::createDatatypeDefinitionString      ),
           Map.entry( NativeDataType.STRING_WORD_ML,      ReqIF10Factory.eINSTANCE::createDatatypeDefinitionXHTML       ),
           Map.entry( NativeDataType.URI,                 ReqIF10Factory.eINSTANCE::createDatatypeDefinitionString      )
         );
   //@formatter:on

   /**
    * Map of {@link BiConsumer} implementations to set the data type specific attributes of foreign
    * {@link DatatypeDefinition} things.
    */

   //@formatter:off
   private static final EnumBiConsumerMap<NativeDataType, GroveThing, DatatypeDefinition> reqifDatatypeDefinitionDatatypeConverterMap =
      EnumBiConsumerMap.ofEntries
         (
            NativeDataType.class,
            Map.entry( NativeDataType.ARTIFACT_IDENTIFIER, DataTypeDefinitionConverter::convertDataTypeDefinitionReqIfDatatypeDefinitionInteger    ),
            Map.entry( NativeDataType.BRANCH_IDENTIFIER,   DataTypeDefinitionConverter::convertDataTypeDefinitionReqIfDatatypeDefinitionInteger    ),
            Map.entry( NativeDataType.DOUBLE,              DataTypeDefinitionConverter::convertDataTypeDefinitionReqIfDatatypeDefinitionReal       ),
            Map.entry( NativeDataType.INPUT_STREAM,        DataTypeDefinitionConverter::convertDataTypeDefinitionReqIfDatatypeDefinitionString     ),
            Map.entry( NativeDataType.INTEGER,             DataTypeDefinitionConverter::convertDataTypeDefinitionReqIfDatatypeDefinitionInteger    ),
            Map.entry( NativeDataType.JAVA_OBJECT,         DataTypeDefinitionConverter::convertDataTypeDefinitionReqIfDatatypeDefinitionString     ),
            Map.entry( NativeDataType.LONG,                DataTypeDefinitionConverter::convertDataTypeDefinitionReqIfDatatypeDefinitionInteger    ),
            Map.entry( NativeDataType.STRING,              DataTypeDefinitionConverter::convertDataTypeDefinitionReqIfDatatypeDefinitionString     ),
            Map.entry( NativeDataType.URI,                 DataTypeDefinitionConverter::convertDataTypeDefinitionReqIfDatatypeDefinitionString     )
         );
   //@formatter:on

   /**
    * Map of {@link ReqifDatatypeRecords} with data type specific constants for each {@link NativeDataType}.
    */

   private static final EnumMap<NativeDataType, ReqifDatatypeRecord> reqifDatatypeDefinitionRecordMap =
      new EnumMap<>(NativeDataType.class);

   static {
      //@formatter:off
      DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.put(NativeDataType.ARTIFACT_IDENTIFIER, new ReqifDatatypeIntegerRecord(Long.MAX_VALUE, Long.MIN_VALUE) );
      DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.put(NativeDataType.BRANCH_IDENTIFIER,   new ReqifDatatypeIntegerRecord(Long.MAX_VALUE, Long.MIN_VALUE) );
      DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.put(NativeDataType.DOUBLE,              new ReqifDatatypeDoubleRecord( 64L, Double.MAX_VALUE, Double.MIN_VALUE ) );
      DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.put(NativeDataType.INPUT_STREAM,        new ReqifDatatypeStringRecord( 1048576L ) );
      DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.put(NativeDataType.INTEGER,             new ReqifDatatypeIntegerRecord( Long.valueOf( Integer.MAX_VALUE ), Long.valueOf( Integer.MIN_VALUE )) );
      DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.put(NativeDataType.JAVA_OBJECT,         new ReqifDatatypeStringRecord( 1024L ) );
      DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.put(NativeDataType.LONG,                new ReqifDatatypeIntegerRecord(Long.MAX_VALUE, Long.MIN_VALUE) );
      DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.put(NativeDataType.STRING,              new ReqifDatatypeStringRecord( 8192L ) );
      DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.put(NativeDataType.URI,                 new ReqifDatatypeStringRecord( 2048L ) );
      //@formatter:on
   }

   /**
    * Converter method for {@link DataTypeDefinitionGroveThing} things. This method creates the foreign ReqIF
    * {@link DatatypeDefinition} from the native {@link NativeDataType}.
    *
    * @param groveThing the {@link DataTypeDefinitionGroveThing} thing to be converted.
    */

   static void convert(GroveThing groveThing) {

      //@formatter:off
      assert
            Objects.nonNull(groveThing)
         && groveThing.isType( IdentifierType.DATA_TYPE_DEFINITION )
         : UnexpectedGroveThingTypeException.buildMessage( groveThing, IdentifierType.DATA_TYPE_DEFINITION );
      //@formatter:on

      var nativeDataType = ((NativeDataTypeKey) groveThing.getNativeThing()).getNativeDataType();

      var reqifDatatypeDefinition = DataTypeDefinitionConverter.reqifDatatypeDefinitionFactoryMap.get(nativeDataType);

      // Set common data type definition attributes

      DataTypeDefinitionConverter.convertDataTypeDefinitionReqIfDatatypeDefinition(groveThing, reqifDatatypeDefinition);

      // Set data type specific attributes

      if (DataTypeDefinitionConverter.reqifDatatypeDefinitionDatatypeConverterMap.containsKey(nativeDataType)) {
         DataTypeDefinitionConverter.reqifDatatypeDefinitionDatatypeConverterMap.accept(nativeDataType, groveThing,
            reqifDatatypeDefinition);
      }

      groveThing.setForeignThing(reqifDatatypeDefinition);

   }

   /**
    * Set common attributes for {@link DatatypeDefinition} things.
    *
    * @param dataTypeDefinitionGroveThing the {@link DataTypeDefinitionGroveThing} thing to get attribute values from.
    * @param reqifDatatypeDefinition the {@link DatatypeDefinition} to have attributes set.
    */

   private static void convertDataTypeDefinitionReqIfDatatypeDefinition(GroveThing dataTypeDefinitionGroveThing, DatatypeDefinition reqifDatatypeDefinition) {
      var nativeDataTypeKey = (NativeDataTypeKey) dataTypeDefinitionGroveThing.getNativeThing();
      var nativeDataType = nativeDataTypeKey.getNativeDataType();

      reqifDatatypeDefinition.setDesc(nativeDataType.toString());
      reqifDatatypeDefinition.setIdentifier(dataTypeDefinitionGroveThing.getIdentifier().toString());
      reqifDatatypeDefinition.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifDatatypeDefinition.setLongName(nativeDataTypeKey.name());

   }

   /**
    * Set data type integer specific attributes for {@link DatatypeDefinitionInteger} things.
    *
    * @param dataTypeDefinitionGroveThing the {@link DataTypeDefinitionGroveThing} thing to get attribute values from.
    * @param reqifDatatypeDefinition the {@link DatatypeDefinition} to have attributes set.
    */

   private static void convertDataTypeDefinitionReqIfDatatypeDefinitionInteger(GroveThing dataTypeDefinitionGroveThing, DatatypeDefinition reqifDatatypeDefinition) {

      assert (reqifDatatypeDefinition instanceof DatatypeDefinitionInteger);

      var reqifDatatypeDefinitionInteger = (DatatypeDefinitionInteger) reqifDatatypeDefinition;
      var nativeDataType = ((NativeDataTypeKey) dataTypeDefinitionGroveThing.getNativeThing()).getNativeDataType();

      assert DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.containsKey(nativeDataType);

      var reqifDatatypeRecord = DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.get(nativeDataType);

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

   private static void convertDataTypeDefinitionReqIfDatatypeDefinitionReal(GroveThing dataTypeDefinitionGroveThing, DatatypeDefinition reqifDatatypeDefinition) {

      assert (reqifDatatypeDefinition instanceof DatatypeDefinitionReal);

      var reqifDatatypeDefinitionReal = (DatatypeDefinitionReal) reqifDatatypeDefinition;
      var nativeDataType = ((NativeDataTypeKey) dataTypeDefinitionGroveThing.getNativeThing()).getNativeDataType();

      assert DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.containsKey(nativeDataType);

      var reqifDatatypeRecord = DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.get(nativeDataType);

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

   private static void convertDataTypeDefinitionReqIfDatatypeDefinitionString(GroveThing dataTypeDefinitionGroveThing, DatatypeDefinition reqifDatatypeDefinition) {

      assert (reqifDatatypeDefinition instanceof DatatypeDefinitionString);

      var reqifDatatypeDefinitionString = (DatatypeDefinitionString) reqifDatatypeDefinition;
      var nativeDataType = ((NativeDataTypeKey) dataTypeDefinitionGroveThing.getNativeThing()).getNativeDataType();

      assert DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.containsKey(nativeDataType);

      var reqifDatatypeRecord = DataTypeDefinitionConverter.reqifDatatypeDefinitionRecordMap.get(nativeDataType);

      assert (reqifDatatypeRecord instanceof ReqifDatatypeStringRecord);

      var reqifDatatypeStringRecord = (ReqifDatatypeStringRecord) reqifDatatypeRecord;

      reqifDatatypeDefinitionString.setMaxLength(BigInteger.valueOf(reqifDatatypeStringRecord.maxLength));
   }

}

/* EOF */
