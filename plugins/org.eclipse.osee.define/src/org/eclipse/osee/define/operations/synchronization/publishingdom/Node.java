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

import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;

/**
 * Common interface for all data types in the mark down publishing model.
 *
 * @author Loren K. Ashley
 */

public interface Node {

   /**
    * Get the number of {@link AttributeDefinitionImpl} child {@link Node}s.
    *
    * @return the number of child {@link AttributeDefinitionImpl} {@link Node}s.
    */

   int attributeDefinitionSize();

   /**
    * Gets the {@link Node}'s unique {@link Identifier}.
    *
    * @return the {@link Node} {@link Identifier}.
    */

   Identifier getIdentifier();

   /**
    * Gets the {@link Identifier} of the {@link Node}'s parent.
    *
    * @return for non-root {@link Node}s the {@link Identifier} of the {@link Node}'s parent; otherwise,
    * <code>null</code> for the root {@link Node}.
    */

   Identifier getParentIdentifier();

   /**
    * Gets the {@link IdentifierType} of the {@link Node}'s {@link @Identifier}.
    *
    * @return the {@link IdentifierType} of the {@link Node} {@link Identifier}.
    */

   IdentifierType getType();

   /**
    * Predicate to determine if the {@link Node} has been attached to a {@link DocumentMapImpl}.
    *
    * @return <code>true</code> when the {@link Node} is attached; otherwise, <code>false</code>.
    */

   boolean isAttached();

   /**
    * Predicate to determine if the {@link Node} is a hierarchical node (supports children) or a leaf node (does not
    * support children).
    *
    * @return <code>true</code> when the {@link Node} is hierarchical; otherwise, <code>false</code>.
    */

   boolean isHierarchical();

   /**
    * Predicate to determine if the {@link Node}'s {@link Identifier} is of the specified <code>identifierType</code>.
    *
    * @param identifierType the {@link IdentifierType} to test for.
    * @return <code>true</code>, when the {@link Node}'s {@link Identifier} is of the type specified by
    * <code>identifierType</code>; otherwise, <code>false</code>.
    */

   boolean isType(IdentifierType identifierType);

   /**
    * Returns an ordered {@link Stream} of the {@link Node}'s {@link AttributeDefinitionImpl} children {@link Node}
    * objects.
    *
    * @return a {@link Stream} of {@link AttributeDefinitionImpl} {@link Node} objects.
    */

   Stream<AttributeDefinitionImpl> streamAttributeDefinitionChildren();

}

/* EOF */
