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

package org.eclipse.osee.define.rest.synchronization.reqifsynchronizationartifactbuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.osee.define.rest.synchronization.ForeignThingFamily;
import org.eclipse.osee.define.rest.synchronization.SimpleForeignThingFamily;
import org.eclipse.osee.define.rest.synchronization.identifier.IdentifierType;
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.AttributeValueBoolean;
import org.eclipse.rmf.reqif10.AttributeValueDate;
import org.eclipse.rmf.reqif10.AttributeValueEnumeration;
import org.eclipse.rmf.reqif10.AttributeValueInteger;
import org.eclipse.rmf.reqif10.AttributeValueReal;
import org.eclipse.rmf.reqif10.AttributeValueString;
import org.eclipse.rmf.reqif10.AttributeValueXHTML;
import org.eclipse.rmf.reqif10.Identifiable;

/**
 * This class contains various methods for {@link AttributeValue} foreign things.
 *
 * @author Loren K. Ashley
 */

public class AttributeValueUtils {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private AttributeValueUtils() {
   }

   /**
    * Gets the identifier of the {@link AttributeDefinition} referenced by the {@link AttributeValue} sub-class.
    *
    * @param attributeValue the foreign thing extending the {@link AttributeValue} class.
    * @return when the <code>attributeValue</code> implements a known sub-class of {@link AttributeValue}, an
    * {@link Optional} containing the identifier of the referenced {@link AttributeDefinition}; otherwise, an empty
    * {@link Optional}.
    */

   public static Optional<String> getAttributeDefinitionIdentifier(AttributeValue attributeValue) {
      //@formatter:off
      var attributeDefinition =
         (attributeValue instanceof AttributeValueBoolean)
            ? ((AttributeValueBoolean) attributeValue).getDefinition()
            : (attributeValue instanceof AttributeValueDate)
                 ? ((AttributeValueDate) attributeValue).getDefinition()
                 : (attributeValue instanceof AttributeValueEnumeration)
                      ? ((AttributeValueEnumeration) attributeValue).getDefinition()
                      : (attributeValue instanceof AttributeValueInteger)
                           ? ((AttributeValueInteger) attributeValue).getDefinition()
                           : (attributeValue instanceof AttributeValueReal)
                                ? ((AttributeValueReal) attributeValue).getDefinition()
                                : (attributeValue instanceof AttributeValueString)
                                     ? ((AttributeValueString) attributeValue).getDefinition()
                                     : (attributeValue instanceof AttributeValueXHTML)
                                          ? ((AttributeValueXHTML) attributeValue).getDefinition()
                                          : null;

      return
         Objects.nonNull( attributeDefinition )
            ? Optional.ofNullable( attributeDefinition.getIdentifier() )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Creates an unordered {@link Stream} of {@link ForeignThingFamily} objects representing the ReqIF Attribute Values
    * in the ReqIF DOM. The stream will contain all attributes values from all of the ReqIF Specifications, Spec
    * Objects, and Spec Relations.
    *
    * @param builder the {@link ReqIFSynchronizationArtifactBuilder}.
    * @return a {@link Stream} of {@link ForeignThingFamily} objects.
    */

   @SuppressWarnings("unchecked")
   static Stream<ForeignThingFamily> extract(ReqIFSynchronizationArtifactBuilder builder) {
      //@formatter:off
      return
         Arrays.stream
      (
         (Stream<ForeignThingFamily>[])
         new Stream []
         {
            builder.reqIf.getCoreContent().getSpecifications().stream()
               .flatMap
                  (
                     ( specification ) -> specification.getValues().stream()
                                             .map
                                                (
                                                   ( attributeValue ) -> (ForeignThingFamily) new SimpleForeignThingFamily
                                                                                                     (
                                                                                                       attributeValue,
                                                                                                       new String[]
                                                                                                       {
                                                                                                         ((Identifiable) specification).getIdentifier(),
                                                                                                         "AV-" + Long.toString( builder.getAndIncrementAttributeValueCount() )
                                                                                                       },
                                                                                                       new IdentifierType[]
                                                                                                       {
                                                                                                         IdentifierType.SPECIFICATION,
                                                                                                         IdentifierType.ATTRIBUTE_VALUE
                                                                                                       }
                                                                                                     )
                                                )
                  ),

            builder.reqIf.getCoreContent().getSpecObjects().stream()
               .flatMap
                  (
                     ( specObject ) -> builder.specObjectMap.containsKey( specObject.getIdentifier() )
                                          ? specObject.getValues().stream()
                                               .map
                                                  (
                                                    ( attributeValue ) -> (ForeignThingFamily) new SimpleForeignThingFamily
                                                                                                      (
                                                                                                        attributeValue,
                                                                                                        new String[]
                                                                                                        {
                                                                                                          ((Identifiable) specObject).getIdentifier(),
                                                                                                          "AV-" + Long.toString( builder.getAndIncrementAttributeValueCount() )
                                                                                                        },
                                                                                                        new IdentifierType[]
                                                                                                        {
                                                                                                          IdentifierType.SPEC_OBJECT,
                                                                                                          IdentifierType.ATTRIBUTE_VALUE
                                                                                                        }
                                                                                                      )
                                                )
                                          : specObject.getValues().stream()
                                               .map
                                                  (
                                                    ( attributeValue ) -> (ForeignThingFamily) new SimpleForeignThingFamily
                                                                                                      (
                                                                                                        attributeValue,
                                                                                                        new String[]
                                                                                                        {
                                                                                                          ((Identifiable) specObject).getIdentifier(),
                                                                                                          "AV-" + Long.toString( builder.getAndIncrementAttributeValueCount() )
                                                                                                        },
                                                                                                        new IdentifierType[]
                                                                                                        {
                                                                                                          IdentifierType.SPECTER_SPEC_OBJECT,
                                                                                                          IdentifierType.ATTRIBUTE_VALUE
                                                                                                        }
                                                                                                      )
                                                  )
                  ),

            builder.reqIf.getCoreContent().getSpecRelations().stream()
               .flatMap
                  (
                     ( specRelation ) -> specRelation.getValues().stream()
                                            .map
                                               (
                                                  ( attributeValue ) -> (ForeignThingFamily) new SimpleForeignThingFamily
                                                                                                    (
                                                                                                       attributeValue,
                                                                                                       new String[]
                                                                                                       {
                                                                                                         ((Identifiable) specRelation).getIdentifier(),
                                                                                                         "AV-" + Long.toString( builder.getAndIncrementAttributeValueCount() )
                                                                                                       },
                                                                                                       new IdentifierType[]
                                                                                                       {
                                                                                                         IdentifierType.SPEC_RELATION,
                                                                                                         IdentifierType.ATTRIBUTE_VALUE
                                                                                                       }
                                                                                                     )
                                               )
                  )
         }
      )
      .flatMap( ( inStream ) -> inStream );
      //@formatter:off

   }
}

/* EOF */
