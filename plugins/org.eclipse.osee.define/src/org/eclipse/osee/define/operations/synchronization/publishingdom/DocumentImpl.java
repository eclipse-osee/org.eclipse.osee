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
 * An implementation of the {@link Document} interface for DOM nodes that represents a document (Synchronization
 * Artifact Specification).
 *
 * @author Loren K. Ashley
 */

class DocumentImpl extends AbstractHierarchicalNode implements Document {

   /**
    * The {@link DocumentImpl} name.
    */

   private final String name;

   /**
    * A description of the {@link Node} typeDescription.
    */

   private final String typeDescription;

   /**
    * Creates a new unattached {@link DocumentImpl} {@link Node}.
    *
    * @param documentMap the {@link DocumentMap} this DOM node belongs to.
    * @param identifier the {@link Identifier} for the {@link Node}.
    * @param name the {@link DocumentImpl} name.
    * @param typeDescription a description of the {@link Node} typeDescription.
    */

   DocumentImpl(DocumentMap documentMap, Identifier identifier, String name, String typeDescription) {

      super(documentMap, identifier);

      //@formatter:off
      assert   identifier.isType( IdentifierType.SPECIFICATION )
             : "DocumentImpl::new, parameter \"identifier\" is not of the typeDescription \"IdentifierType.SPECIFICATION\".";

      assert   Objects.nonNull( name )
             : "DocumentImpl::new, parameter \"name\" cannot be null.";

      assert   Objects.nonNull( typeDescription )
             : "DocumentImpl::new, parameter \"typeDescription\" cannot be null.";
      //@formatter:on

      this.name = name;
      this.typeDescription = typeDescription;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return this.name;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getTypeDescription() {
      return this.typeDescription;
   }

}

/* EOF */