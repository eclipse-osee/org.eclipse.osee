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

package org.eclipse.osee.define.operations.synchronization.publishingdom;

import java.util.Objects;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;

/**
 * An implementation of the {@link AttributeDefinition} interface for a DOM node that represents an attribute definition
 * (Synchronization Artifact Attribute Definition.)
 *
 * @author Loren K. Ashley
 */

class AttributeDefinitionImpl extends AbstractNode implements AttributeDefinition {

   /**
    * A description of the attribute.
    */

   private final String description;

   /**
    * The name of the attribute.
    */

   private final String name;

   /**
    * Creates a new unattached {@link AttributeDefinitionImpl} {@link Node}.
    *
    * @param documentMap the {@link DocumentMap} this DOM node belongs to.
    * @param identifier the {@link Identifier} for the {@link Node}.
    * @param name the {@link AttributeDefinitionImpl} name.
    * @param description a description of the attribute.
    */

   AttributeDefinitionImpl(DocumentMap documentMap, Identifier identifier, String name, String description) {

      super(documentMap, identifier);

      //@formatter:off
      assert identifier.isType( IdentifierType.ATTRIBUTE_DEFINITION )
             : "AttributeDefinitionImpl::new, parameter \"identifier\" is not of the type \"IdentifierType.ATTRIBUTE_DEFINITION\".";

      assert   Objects.nonNull( name )
             : "AttributeDefinitionImpl::new, parameter \"name\" cannot be null.";

      assert   Objects.nonNull( description )
             : "AttributeDefinitionImpl::new, parameter \"description\" cannot be null.";
      //@formatter:on

      this.name = name;
      this.description = description;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDescription() {
      return this.description;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return this.name;
   }

}

/* EOF */