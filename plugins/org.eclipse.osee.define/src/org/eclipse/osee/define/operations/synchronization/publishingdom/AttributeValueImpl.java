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
 * An implementation of the {@link Node} interface for a {@link Node} that represents an attribute value
 * (Synchronization Artifact Attribute Value).
 *
 * @author Loren K. Ashley
 */

class AttributeValueImpl extends AbstractNode implements AttributeValue {

   /**
    * String representation of the attribute value.
    */

   private String value;

   /**
    * Creates a new unattached {@link AttributeValueImpl} {@link Node}.
    *
    * @param documentMap the {@link DocumentMap} this DOM node belongs to.
    * @param identifier the {@link Identifier} for the {@link Node}.
    * @param value string representation of the attribute value.
    */

   AttributeValueImpl(DocumentMap documentMap, Identifier identifier, String value) {

      super(documentMap, identifier);

      //@formatter:off
      assert identifier.isType( IdentifierType.ATTRIBUTE_VALUE )
             : "AttributeValueImpl::new, parameter \"identifier\" is not of the type \"IdentifierType.ATTRIBUTE_DEFINITION\".";

      assert   Objects.nonNull( value )
             : "AttributeValueImpl::new, parameter \"value\" cannot be null.";
      //@formatter:on

      this.value = value;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getValue() {
      return this.value;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setValue(String value) {
      this.value = Objects.requireNonNull(value, "AttributeValueImpl::setValue, parameter \"value\" cannot be null.");
   }

}

/* EOF */
