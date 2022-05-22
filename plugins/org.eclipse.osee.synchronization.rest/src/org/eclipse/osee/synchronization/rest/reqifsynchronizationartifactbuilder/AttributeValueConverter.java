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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.EnumBiConsumerMap;
import org.eclipse.osee.framework.jdk.core.util.EnumSupplierMap;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.UnexpectedGroveThingTypeException;
import org.eclipse.osee.synchronization.rest.forest.GroveThing;
import org.eclipse.osee.synchronization.rest.forest.denizens.NativeDataType;
import org.eclipse.osee.synchronization.rest.forest.denizens.NativeDataTypeKey;
import org.eclipse.osee.synchronization.util.DataConverters;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.AttributeValueBoolean;
import org.eclipse.rmf.reqif10.AttributeValueDate;
import org.eclipse.rmf.reqif10.AttributeValueEnumeration;
import org.eclipse.rmf.reqif10.AttributeValueInteger;
import org.eclipse.rmf.reqif10.AttributeValueReal;
import org.eclipse.rmf.reqif10.AttributeValueString;
import org.eclipse.rmf.reqif10.AttributeValueXHTML;
import org.eclipse.rmf.reqif10.EnumValue;
import org.eclipse.rmf.reqif10.ReqIF10Factory;

/**
 * Class contains the converter method to create the ReqIF {@link AttributeValue} things from native OSEE attribute
 * values.
 *
 * @author Loren K. Ashley
 */

public class AttributeValueConverter {

   /**
    * Map of {@link Supplier} implementations to create the foreign things that extend the {@link AttributeValue} class
    * appropriate for each {@link NativeDataType}.
    */

   //@formatter:off
   static final EnumSupplierMap<NativeDataType, AttributeValue> reqifAttributeValueFactoryMap =
      EnumSupplierMap.ofEntries
         (
            NativeDataType.class,
            Map.entry( NativeDataType.ARTIFACT_IDENTIFIER, ReqIF10Factory.eINSTANCE::createAttributeValueInteger     ),
            Map.entry( NativeDataType.BRANCH_IDENTIFIER,   ReqIF10Factory.eINSTANCE::createAttributeValueInteger     ),
            Map.entry( NativeDataType.BOOLEAN,             ReqIF10Factory.eINSTANCE::createAttributeValueBoolean     ),
            Map.entry( NativeDataType.DATE,                ReqIF10Factory.eINSTANCE::createAttributeValueDate        ),
            Map.entry( NativeDataType.DOUBLE,              ReqIF10Factory.eINSTANCE::createAttributeValueReal        ),
            Map.entry( NativeDataType.ENUMERATED,          ReqIF10Factory.eINSTANCE::createAttributeValueEnumeration ),
            Map.entry( NativeDataType.INPUT_STREAM,        ReqIF10Factory.eINSTANCE::createAttributeValueString      ),
            Map.entry( NativeDataType.INTEGER,             ReqIF10Factory.eINSTANCE::createAttributeValueInteger     ),
            Map.entry( NativeDataType.JAVA_OBJECT,         ReqIF10Factory.eINSTANCE::createAttributeValueString      ),
            Map.entry( NativeDataType.LONG,                ReqIF10Factory.eINSTANCE::createAttributeValueInteger     ),
            Map.entry( NativeDataType.STRING,              ReqIF10Factory.eINSTANCE::createAttributeValueString      ),
            Map.entry( NativeDataType.STRING_WORD_ML,      ReqIF10Factory.eINSTANCE::createAttributeValueXHTML       ),
            Map.entry( NativeDataType.URI,                 ReqIF10Factory.eINSTANCE::createAttributeValueString      )
         );
   //@formatter:on

   /**
    * Map of {@link BiConsumer} implementations to set the value on an {@link AttributeValue} according to the
    * {@link NativeDataType}.
    */

   //@formatter:off
   private static final EnumBiConsumerMap<NativeDataType, Object, AttributeValue> reqifAttributeValueSetterMap =
      EnumBiConsumerMap.ofEntries
         (
           NativeDataType.class,
           Map.entry( NativeDataType.ARTIFACT_IDENTIFIER, ( value, attributeValue ) -> ((AttributeValueInteger)     attributeValue).setTheValue( DataConverters.idToBigInteger( (Id) value ) ) ),
           Map.entry( NativeDataType.BRANCH_IDENTIFIER,   ( value, attributeValue ) -> ((AttributeValueInteger)     attributeValue).setTheValue( DataConverters.idToBigInteger( (Id) value ) ) ),
           Map.entry( NativeDataType.BOOLEAN,             ( value, attributeValue ) -> ((AttributeValueBoolean)     attributeValue).setTheValue( (Boolean) value ) ),
           Map.entry( NativeDataType.DATE,                ( value, attributeValue ) -> ((AttributeValueDate)        attributeValue).setTheValue( DataConverters.dateToGregorianCalendar( (Date) value ) ) ),
           Map.entry( NativeDataType.DOUBLE,              ( value, attributeValue ) -> ((AttributeValueReal)        attributeValue).setTheValue( (Double) value ) ),
           Map.entry( NativeDataType.ENUMERATED,          ( value, attributeValue ) ->
                                                          {
                                                            var attributeValueEnumerationEList = ((AttributeValueEnumeration) attributeValue).getValues();
                                                            @SuppressWarnings("unchecked")
                                                            var enumValueGroveThingList = (List<GroveThing>) value;
                                                            enumValueGroveThingList.stream().map( ( enumValueGroveThing ) -> (EnumValue) enumValueGroveThing.getForeignThing() ).forEach( attributeValueEnumerationEList::add );
                                                          } ),
           Map.entry( NativeDataType.INTEGER,             ( value, attributeValue ) -> ((AttributeValueInteger)     attributeValue).setTheValue( DataConverters.integerToBigInteger( (Integer) value ) ) ),
           Map.entry( NativeDataType.LONG,                ( value, attributeValue ) -> ((AttributeValueInteger)     attributeValue).setTheValue( DataConverters.longToBigInteger( (Long) value ) ) ),
           Map.entry( NativeDataType.STRING,              ( value, attributeValue ) -> ((AttributeValueString)      attributeValue).setTheValue( (String) value ) ),
           Map.entry( NativeDataType.STRING_WORD_ML,      ( value, attributeValue ) -> ((AttributeValueXHTML)       attributeValue).setTheValue( DataConverters.wordMlStringToXhtmlContent( (String) value ) ) ),
           Map.entry( NativeDataType.URI,                 ( value, attributeValue ) -> ((AttributeValueString)      attributeValue).setTheValue( (String) value ) )
         );
   //@formatter:on

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private AttributeValueConverter() {
   }

   /**
    * Converts the native OSEE attribute value into a foreign ReqIF {@link AttributeValue} for Synchronization Artifact
    * {@link AttributeValueGroveThing}s.
    *
    * @param groveThing the Synchronization Artifact {@link AttributeValueGroveThing} to be converted to a ReqIF value.
    */

   static void convert(GroveThing groveThing) {

      //@formatter:off
      assert
            Objects.nonNull(groveThing)
         && groveThing.isType( IdentifierType.ATTRIBUTE_VALUE )
         : UnexpectedGroveThingTypeException.buildMessage( groveThing, IdentifierType.ATTRIBUTE_VALUE );
      //@formatter:on

      try {
         var nativeAttributeValue = groveThing.getNativeThing();
         var attributeDefinitionGroveThing = groveThing.getLinkScalar(IdentifierType.ATTRIBUTE_DEFINITION).get();
         var datatypeDefinitionGroveThing =
            attributeDefinitionGroveThing.getLinkScalar(IdentifierType.DATA_TYPE_DEFINITION).get();
         var nativeDataType = ((NativeDataTypeKey) datatypeDefinitionGroveThing.getNativeThing()).getNativeDataType();

         var reqifAttributeValue = AttributeValueConverter.reqifAttributeValueFactoryMap.get(nativeDataType);

         if (AttributeValueConverter.reqifAttributeValueSetterMap.containsKey(nativeDataType)) {
            AttributeValueConverter.reqifAttributeValueSetterMap.accept(nativeDataType, nativeAttributeValue,
               reqifAttributeValue);
         }

         groveThing.setForeignThing(reqifAttributeValue);
      } catch (Exception e) {
         var message = new StringBuilder(1024);

         //@formatter:off
         message
            .append( "\n" )
            .append( "Failed to convert attribute value." ).append( "\n" )
            .append( "   Identifier: " ).append( groveThing.getIdentifier() ).append( "\n" )
            ;
         //@formatter:on

         groveThing.toMessage(1, message);

         throw new RuntimeException(message.toString(), e);
      }
   }

}

/* EOF */
