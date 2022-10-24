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
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.eclipse.osee.define.operations.synchronization.UnexpectedGroveThingTypeException;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.forest.denizens.NativeDataType;
import org.eclipse.osee.define.operations.synchronization.forest.denizens.NativeDataTypeKey;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.util.DataConverters;
import org.eclipse.osee.framework.core.data.AttributeTypeArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeBranchId;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeDouble;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeLong;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.EnumBiConsumerMap;
import org.eclipse.osee.framework.jdk.core.util.EnumSupplierMap;
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.AttributeDefinitionBoolean;
import org.eclipse.rmf.reqif10.AttributeDefinitionDate;
import org.eclipse.rmf.reqif10.AttributeDefinitionInteger;
import org.eclipse.rmf.reqif10.AttributeDefinitionReal;
import org.eclipse.rmf.reqif10.AttributeDefinitionString;
import org.eclipse.rmf.reqif10.AttributeDefinitionXHTML;
import org.eclipse.rmf.reqif10.AttributeValueBoolean;
import org.eclipse.rmf.reqif10.AttributeValueDate;
import org.eclipse.rmf.reqif10.AttributeValueInteger;
import org.eclipse.rmf.reqif10.AttributeValueReal;
import org.eclipse.rmf.reqif10.AttributeValueString;
import org.eclipse.rmf.reqif10.AttributeValueXHTML;
import org.eclipse.rmf.reqif10.ReqIF10Factory;

/**
 * Class contains the converter method to create the ReqIF {@link AttributeDefinition} things for native OSEE
 * {@link AttributeTypeToken} things.
 *
 * @author Loren K. Ashley
 */
public class AttributeDefinitionConverter {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private AttributeDefinitionConverter() {
   }

   /**
    * Map of {@link BiConsumer} implementations to set the data type specific attributes of foreign
    * {@link AttributeDefinitionGroveThing} things.
    */

   //@formatter:off
   private static final EnumBiConsumerMap<NativeDataType, GroveThing, AttributeDefinition> reqifAttributeDefinitionDatatypeConverterMap =
      EnumBiConsumerMap.ofEntries
         (
           NativeDataType.class,
           Map.entry( NativeDataType.ARTIFACT_IDENTIFIER, AttributeDefinitionConverter::convertAttributeDefinitionReqIfAttributeDefinitionIntegerForArtifactId ),
           Map.entry( NativeDataType.BRANCH_IDENTIFIER,   AttributeDefinitionConverter::convertAttributeDefinitionReqIfAttributeDefinitionIntegerForBranchId   ),
           Map.entry( NativeDataType.BOOLEAN,             AttributeDefinitionConverter::convertAttributeDefinitionReqIfAttributeDefinitionBoolean              ),
           Map.entry( NativeDataType.DATE,                AttributeDefinitionConverter::convertAttributeDefinitionReqIfAttributeDefinitionDate                 ),
           Map.entry( NativeDataType.DOUBLE,              AttributeDefinitionConverter::convertAttributeDefinitionReqIfAttributeDefinitionDouble               ),
           Map.entry( NativeDataType.INTEGER,             AttributeDefinitionConverter::convertAttributeDefinitionReqIfAttributeDefinitionIntegerForInteger    ),
           Map.entry( NativeDataType.LONG,                AttributeDefinitionConverter::convertAttributeDefinitionReqIfAttributeDefinitionIntegerForLong       ),
           Map.entry( NativeDataType.STRING,              AttributeDefinitionConverter::convertAttributeDefinitionReqIfAttributeDefinitionString               ),
           Map.entry( NativeDataType.STRING_WORD_ML,      AttributeDefinitionConverter::convertAttributeDefinitionReqIfAttributeDefinitionXHTML                ),
           Map.entry( NativeDataType.URI,                 AttributeDefinitionConverter::convertAttributeDefinitionReqIfAttributeDefinitionString               )
         );
   //@formatter:on

   /**
    * Map of {@link Supplier} implementations to create the foreign things that extend the {@link AttributeDefinition}
    * class appropriate for each {@link NativeDataType}.
    */

   //@formatter:off
   private static final EnumSupplierMap<NativeDataType, AttributeDefinition> reqifAttributeDefinitionFactoryMap =
      EnumSupplierMap.ofEntries
         (
           NativeDataType.class,
           Map.entry( NativeDataType.ARTIFACT_IDENTIFIER, ReqIF10Factory.eINSTANCE::createAttributeDefinitionInteger     ),
           Map.entry( NativeDataType.BRANCH_IDENTIFIER,   ReqIF10Factory.eINSTANCE::createAttributeDefinitionInteger     ),
           Map.entry( NativeDataType.BOOLEAN,             ReqIF10Factory.eINSTANCE::createAttributeDefinitionBoolean     ),
           Map.entry( NativeDataType.DATE,                ReqIF10Factory.eINSTANCE::createAttributeDefinitionDate        ),
           Map.entry( NativeDataType.DOUBLE,              ReqIF10Factory.eINSTANCE::createAttributeDefinitionReal        ),
           Map.entry( NativeDataType.ENUMERATED,          ReqIF10Factory.eINSTANCE::createAttributeDefinitionEnumeration ),
           Map.entry( NativeDataType.INPUT_STREAM,        ReqIF10Factory.eINSTANCE::createAttributeDefinitionString      ),
           Map.entry( NativeDataType.INTEGER,             ReqIF10Factory.eINSTANCE::createAttributeDefinitionInteger     ),
           Map.entry( NativeDataType.JAVA_OBJECT,         ReqIF10Factory.eINSTANCE::createAttributeDefinitionString      ),
           Map.entry( NativeDataType.LONG,                ReqIF10Factory.eINSTANCE::createAttributeDefinitionInteger     ),
           Map.entry( NativeDataType.STRING,              ReqIF10Factory.eINSTANCE::createAttributeDefinitionString      ),
           Map.entry( NativeDataType.STRING_WORD_ML,      ReqIF10Factory.eINSTANCE::createAttributeDefinitionXHTML       ),
           Map.entry( NativeDataType.URI,                 ReqIF10Factory.eINSTANCE::createAttributeDefinitionString      )
         );
   //@formatter:on

   /**
    * Converts the native OSEE {@link AttributeTypeToken} into a foreign ReqIF {@link AttributeDefinition} for
    * Synchronization Artifact {@link AttributeDefinitionGroveThing}s.
    *
    * @param groveThing the Synchronization Artifact {@link AttributeDefinitionGroveThing} thing to be converted for
    * ReqIF.
    */

   static void convert(GroveThing groveThing) {

      //@formatter:off
      assert
            Objects.nonNull(groveThing)
         && groveThing.isType( IdentifierType.ATTRIBUTE_DEFINITION )
         : UnexpectedGroveThingTypeException.buildMessage( groveThing, IdentifierType.ATTRIBUTE_DEFINITION );
      //@formatter:on

      var datatypeDefinition = groveThing.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
      var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();

      var reqifAttributeDefinition =
         AttributeDefinitionConverter.reqifAttributeDefinitionFactoryMap.get(nativeDataType);

      // Set common attribute definition attributes

      AttributeDefinitionConverter.convertAttributeDefinitionReqIfAttributeDefinition(groveThing,
         reqifAttributeDefinition);

      // Set attribute definition type specific attributes

      if (AttributeDefinitionConverter.reqifAttributeDefinitionDatatypeConverterMap.containsKey(nativeDataType)) {
         AttributeDefinitionConverter.reqifAttributeDefinitionDatatypeConverterMap.accept(nativeDataType, groveThing,
            reqifAttributeDefinition);
      }

      groveThing.setForeignThing(reqifAttributeDefinition);
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

   private static void convertAttributeDefinitionReqIfAttributeDefinition(GroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {
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
      reqifAttributeDefinition.setIdentifier(attributeDefinitionGroveThing.getIdentifier().toString());
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

   private static void convertAttributeDefinitionReqIfAttributeDefinitionBoolean(GroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeBoolean);

      var nativeAttributeTypeBoolean = (AttributeTypeBoolean) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeBoolean.getBaseAttributeTypeDefaultValue();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionBoolean);

      var reqifAttributeDefinitionBoolean = (AttributeDefinitionBoolean) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
      var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();

      assert nativeDataType.equals(NativeDataType.BOOLEAN);

      var defaultAttributeValue =
         (AttributeValueBoolean) AttributeValueConverter.reqifAttributeValueFactoryMap.get(nativeDataType);

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

   private static void convertAttributeDefinitionReqIfAttributeDefinitionDate(GroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeDate);

      var nativeAttributeTypeDate = (AttributeTypeDate) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeDate.getBaseAttributeTypeDefaultValue();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionDate);

      var reqifAttributeDefinitionDate = (AttributeDefinitionDate) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
      var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();

      assert nativeDataType.equals(NativeDataType.DATE);

      var defaultAttributeValue =
         (AttributeValueDate) AttributeValueConverter.reqifAttributeValueFactoryMap.get(nativeDataType);

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

   private static void convertAttributeDefinitionReqIfAttributeDefinitionDouble(GroveThing attributeDefinitionGroveThing, org.eclipse.rmf.reqif10.AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeDouble);

      var nativeAttributeTypeDouble = (AttributeTypeDouble) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeDouble.getBaseAttributeTypeDefaultValue();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionReal);

      var reqifAttributeDefinitionReal = (AttributeDefinitionReal) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
      var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();

      assert nativeDataType.equals(NativeDataType.DOUBLE);

      var defaultAttributeValue =
         (AttributeValueReal) AttributeValueConverter.reqifAttributeValueFactoryMap.get(nativeDataType);

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

   private static void convertAttributeDefinitionReqIfAttributeDefinitionIntegerForArtifactId(GroveThing attributeDefinitionGroveThing, org.eclipse.rmf.reqif10.AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeArtifactId);

      var nativeAttributeTypeArtifactId = (AttributeTypeArtifactId) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeArtifactId.getBaseAttributeTypeDefaultValue().getId();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionInteger);

      var reqifAttributeDefinitionInteger = (AttributeDefinitionInteger) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
      var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();

      assert nativeDataType.equals(NativeDataType.ARTIFACT_IDENTIFIER);

      var defaultAttributeValue =
         (AttributeValueInteger) AttributeValueConverter.reqifAttributeValueFactoryMap.get(nativeDataType);

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

   private static void convertAttributeDefinitionReqIfAttributeDefinitionIntegerForBranchId(GroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeBranchId);

      var nativeAttributeTypeBranchId = (AttributeTypeBranchId) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeBranchId.getBaseAttributeTypeDefaultValue().getId();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionInteger);

      var reqifAttributeDefinitionInteger = (AttributeDefinitionInteger) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
      var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();

      assert nativeDataType.equals(NativeDataType.ARTIFACT_IDENTIFIER);

      var defaultAttributeValue =
         (AttributeValueInteger) AttributeValueConverter.reqifAttributeValueFactoryMap.get(nativeDataType);

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

   private static void convertAttributeDefinitionReqIfAttributeDefinitionIntegerForInteger(GroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeInteger);

      var nativeAttributeTypeInteger = (AttributeTypeInteger) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeInteger.getBaseAttributeTypeDefaultValue();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionInteger);

      var reqifAttributeDefinitionInteger = (AttributeDefinitionInteger) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
      var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();

      assert nativeDataType.equals(NativeDataType.INTEGER);

      var defaultAttributeValue =
         (AttributeValueInteger) AttributeValueConverter.reqifAttributeValueFactoryMap.get(nativeDataType);

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

   private static void convertAttributeDefinitionReqIfAttributeDefinitionIntegerForLong(GroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeLong);

      var nativeAttributeTypeLong = (AttributeTypeLong) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeLong.getBaseAttributeTypeDefaultValue();

      assert (reqifAttributeDefinition instanceof AttributeDefinitionInteger);

      var reqifAttributeDefinitionInteger = (AttributeDefinitionInteger) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
      var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();

      assert nativeDataType.equals(NativeDataType.LONG);

      var defaultAttributeValue =
         (AttributeValueInteger) AttributeValueConverter.reqifAttributeValueFactoryMap.get(nativeDataType);

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

   private static void convertAttributeDefinitionReqIfAttributeDefinitionString(GroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeString) : "Actual Type: " + nativeAttributeTypeToken.getClass().getName();

      var nativeAttributeTypeString = (AttributeTypeString) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeString.getBaseAttributeTypeDefaultValue();

      if (defaultValue.isEmpty()) {
         return;
      }

      assert (reqifAttributeDefinition instanceof AttributeDefinitionString);

      var reqifAttributeDefinitionString = (AttributeDefinitionString) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
      var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();

      var defaultAttributeValue =
         (AttributeValueString) AttributeValueConverter.reqifAttributeValueFactoryMap.get(nativeDataType);

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

   private static void convertAttributeDefinitionReqIfAttributeDefinitionXHTML(GroveThing attributeDefinitionGroveThing, AttributeDefinition reqifAttributeDefinition) {

      var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

      assert (nativeAttributeTypeToken instanceof AttributeTypeString) : "Actual Type: " + nativeAttributeTypeToken.getClass().getName();

      var nativeAttributeTypeString = (AttributeTypeString) nativeAttributeTypeToken;

      var defaultValue = nativeAttributeTypeString.getBaseAttributeTypeDefaultValue();

      if (defaultValue.isEmpty()) {
         return;
      }

      assert (reqifAttributeDefinition instanceof AttributeDefinitionXHTML);

      var reqifAttributeDefinitionXHTML = (AttributeDefinitionXHTML) reqifAttributeDefinition;

      var datatypeDefinition = attributeDefinitionGroveThing.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
      var nativeDataType = ((NativeDataTypeKey) datatypeDefinition.getNativeThing()).getNativeDataType();

      var defaultAttributeValue =
         (AttributeValueXHTML) AttributeValueConverter.reqifAttributeValueFactoryMap.get(nativeDataType);

      var xhtmlContent = DataConverters.wordMlStringToXhtmlContent(defaultValue);

      defaultAttributeValue.setTheValue(xhtmlContent);

      reqifAttributeDefinitionXHTML.setDefaultValue(defaultAttributeValue);
   }

}

/* EOF */