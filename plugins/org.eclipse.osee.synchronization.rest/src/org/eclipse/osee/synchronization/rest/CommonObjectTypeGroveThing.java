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

package org.eclipse.osee.synchronization.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.util.IndentedString;

/**
 * Class to represent the SpecTypeGroveThing or SpecObjectTypeGroveThing definition for Specifications and SpecObjects in the
 * Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

public class CommonObjectTypeGroveThing extends AbstractGroveThing {

   /**
    * Map used to save an consolidate the Attribute Definitions for the attributes of this SpecificationGroveThing or Spec Object.
    */

   Map<Identifier, AttributeDefinitionGroveThing> attributeDefinitionMap;

   /**
    * Creates a new {@link CommonObjectTypeGroveThing} object. When assertions are enabled an assertion error will be thrown when
    * the identifier is not one of the types:<br>
    * <ul>
    * <li>{@link IdentifierType#SPECIFICATION_TYPE}, or</li>
    * <li>{@link IdentifierType#SPEC_OBJECT_TYPE}.</li>
    * </ul>
    *
    * @param identifier a unique {@link Identifier}.
    */

   CommonObjectTypeGroveThing(Identifier identifier) {
      super(identifier);

      assert ((identifier.getType() == IdentifierType.SPECIFICATION_TYPE) || (identifier.getType() == IdentifierType.SPEC_OBJECT_TYPE));

      this.attributeDefinitionMap = new HashMap<>();
   }

   /**
    * Adds an {@link AttributeDefinitionGroveThing} object representing the definition for an attribute of the
    * SpecificationGroveThing or SpecObjectGroveThing. If an {@link AttributeDefinitionGroveThing} with the same identifier has already been
    * added, the provided {@link AttributeDefinitionGroveThing} will replace the exiting one.
    *
    * @param attributeDefinitionGroveThing the {@link AttributeDefinitionGroveThing} to be added.
    */

   void add(AttributeDefinitionGroveThing attributeDefinitionGroveThing) {
      assert (attributeDefinitionGroveThing != null);
      this.attributeDefinitionMap.put(attributeDefinitionGroveThing.getGroveThingKey(), attributeDefinitionGroveThing);
   }

   /**
    * {@inheritDoc}<br>
    * <br>
    * When assertions are enabled an assertion error will be thrown when the <code>nativeThing</code> is not an instance
    * of {@link ArtifactTypeToken}.
    */

   @Override
   public GroveThing setNativeThing(Object nativeThing) {
      assert nativeThing instanceof ArtifactTypeToken;
      return super.setNativeThing(nativeThing);
   }

   /**
    * @return
    */

   public Stream<AttributeDefinitionGroveThing> streamAttributeDefinitions() {
      return this.attributeDefinitionMap.values().stream();
   }

   /**
    * {@inheritDoc}<br>
    * <br>
    * The generated message is for debugging, there is no contract for the message contents or structure.
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);
      var indent1 = IndentedString.indentString(indent + 1);

      var name = this.getClass().getName();

      //@formatter:off
      var attributeListString =
         this.attributeDefinitionMap.values().stream()
            .map(AttributeDefinitionGroveThing::getGroveThingKey)
            .map(Identifier::toString)
            .collect(Collectors.joining(","))
            ;
      //@formatter:on

      //@formatter:off
      outMessage
         .append( indent0 ).append( name ).append( ":" ).append( "\n" )
         .append( indent1 ).append( "Attributes: " ).append( attributeListString ).append( "\n" )
         ;
      //@formatter:on

      return outMessage;
   }

}

/* EOF */
