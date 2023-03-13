/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.operations.synchronization.publishingdombuilder;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.eclipse.osee.define.operations.synchronization.UnexpectedGroveThingTypeException;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.forest.denizens.NativeDataType;
import org.eclipse.osee.define.operations.synchronization.forest.denizens.NativeDataTypeKey;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.publishingdom.DocumentMap;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.EnumFunctionMap;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * Class contains the converter method to create the Publishing DOM{@link AttributeValue} things from Native OSEE
 * Attribute Values.
 *
 * @author Loren K. Ashley
 */

public class AttributeValueConverter {

   /**
    * Map of {@link BiConsumer} implementations to set the value on an {@link AttributeValue} according to the
    * {@link NativeDataType}.
    */

   //@formatter:off
   private static final EnumFunctionMap<NativeDataType, Object, String> attributeValueConverterMap =
      EnumFunctionMap.ofEntries
         (
           NativeDataType.class,
           Map.entry( NativeDataType.ARTIFACT_IDENTIFIER, ( value ) ->  ((Id) value).getIdString() ),
           Map.entry( NativeDataType.BRANCH_IDENTIFIER,   ( value ) ->  ((Id) value).getIdString() ),
           Map.entry( NativeDataType.BOOLEAN,             ( value ) ->  ((Boolean) value).toString() ),
           Map.entry( NativeDataType.DATE,                ( value ) ->  ((Date) value).toString() ),
           Map.entry( NativeDataType.DOUBLE,              ( value ) ->  ((Double) value).toString() ),
           Map.entry( NativeDataType.ENUMERATED,          ( value ) ->
                                                          {
                                                            @SuppressWarnings("unchecked")
                                                            var enumValueGroveThingList = (List<GroveThing>) value;
                                                            var attributeValue =
                                                               enumValueGroveThingList.stream()
                                                                  .map( ( enumValueGroveThing ) -> (String) enumValueGroveThing.getForeignThing() )
                                                                  .collect( Collectors.joining( "," ) );
                                                            return attributeValue;
                                                          } ),
           Map.entry( NativeDataType.INTEGER,             ( value ) ->  ((Integer) value).toString() ),
           Map.entry( NativeDataType.LONG,                ( value ) ->  ((Long) value).toString() ),
           Map.entry( NativeDataType.STRING,              ( value ) ->  (String) value ),
           Map.entry( NativeDataType.STRING_WORD_ML,      ( value ) ->  (String) value ),
           Map.entry( NativeDataType.URI,                 ( value ) ->  (String) value )
         );
   //@formatter:on

   /**
    * Save the Publishing DOM {@link DocumentMap} which the factory object for creating {@link AttributeValue} things.
    */

   private final DocumentMap documentMap;

   /**
    * Creates a new {@link AttributeValueConverter} and saves the Publishing DOM.
    *
    * @param documentMap the Publishing DOM.
    */

   public AttributeValueConverter(DocumentMap documentMap) {
      this.documentMap = documentMap;
   }

   /**
    * Converts the Native OSEE Attribute Value into a foreign Publishing DOM {@link AttributeValue} for Synchronization
    * Artifact {@link AttributeValueGroveThing}s.
    *
    * @param groveThing the Synchronization Artifact {@link AttributeValueGroveThing} to be converted to a Publishing
    * DOM {@link AttributeValue}.
    */

   void convert(GroveThing groveThing) {

      //@formatter:off
      assert
            Objects.nonNull(groveThing)
         && groveThing.isType( IdentifierType.ATTRIBUTE_VALUE )
         : UnexpectedGroveThingTypeException.buildMessage( groveThing, IdentifierType.ATTRIBUTE_VALUE );

      try {

         var nativeAttributeValue          = groveThing.getNativeThing();
         var attributeDefinitionGroveThing = groveThing.getLinkScalar( IdentifierType.ATTRIBUTE_DEFINITION ).get();
         var datatypeDefinitionGroveThing  = attributeDefinitionGroveThing.getLinkScalar( IdentifierType.DATA_TYPE_DEFINITION ).get();
         var nativeDataType                = ((NativeDataTypeKey) datatypeDefinitionGroveThing.getNativeThing()).getNativeDataType();
         var identifier                    = groveThing.getIdentifier();
         var valueString                   = AttributeValueConverter.attributeValueConverterMap.containsKey( nativeDataType )
                                                ? AttributeValueConverter.attributeValueConverterMap.apply( nativeDataType, nativeAttributeValue )
                                                : nativeDataType.toString();

         var attributeValue                = this.documentMap.createAttributeValue( identifier, valueString );

         groveThing.setForeignThing( attributeValue );

      } catch (Exception e) {

         var message = new Message();

         message
            .blank()
            .title( "Failed to convert attribute value." )
            .indentInc()
            .segment( "Identifier", groveThing.getIdentifier() )
            .toMessage( groveThing )
            ;

         throw new RuntimeException(message.toString(), e);
      }
      //@formatter:on

   }

}

/* EOF */
