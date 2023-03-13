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

import java.util.Objects;
import org.eclipse.osee.define.operations.synchronization.UnexpectedGroveThingTypeException;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.publishingdom.DocumentMap;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * Class contains the converter method to create the Publishing DOM {@link AttributeDefinition} things for Native OSEE
 * {@link AttributeTypeToken} things.
 *
 * @author Loren K. Ashley
 */

public class AttributeDefinitionConverter {

   /**
    * Save the Publishing DOM {@link DocumentMap} which the factory object for creating {@link AttributeValue} things.
    */

   private final DocumentMap documentMap;

   /**
    * Creates a new {@link AttributeDefinitionConverter} and saves the Publishing DOM.
    *
    * @param documentMap the Publishing DOM.
    */

   public AttributeDefinitionConverter(DocumentMap documentMap) {
      this.documentMap = documentMap;
   }

   /**
    * Converts the Native OSEE {@link AttributeTypeToken} into a foreign Publishing DOM {@link AttributeDefinition} for
    * Synchronization Artifact {@link AttributeDefinitionGroveThing}s.
    *
    * @param groveThing the Synchronization Artifact {@link AttributeDefinitionGroveThing} thing to be converted for the
    * Publishing DOM.
    */

   void convert(GroveThing groveThing) {

      //@formatter:off
      assert
            Objects.nonNull(groveThing)
         && groveThing.isType( IdentifierType.ATTRIBUTE_DEFINITION )
         : UnexpectedGroveThingTypeException.buildMessage( groveThing, IdentifierType.ATTRIBUTE_DEFINITION );

      // Set common attribute definition attributes

      var identifier               = groveThing.getIdentifier();
      var nativeAttributeTypeToken = (AttributeTypeToken) groveThing.getNativeThing();
      var description              = nativeAttributeTypeToken.getDescription();
      var name                     = nativeAttributeTypeToken.getName();

      var attributeDefinition      = this.documentMap.createAttributeDefinition( identifier, name, description );

      groveThing.setForeignThing( attributeDefinition );
      //@formatter:on
   }

}

/* EOF */