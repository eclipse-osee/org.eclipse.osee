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

import java.util.Optional;

/**
 * The interface for DOM nodes that represents an object within a document (Synchronization Artifact Spec Object,
 * Specter Spec Object, Spec Relation).
 *
 * @author Loren K. Ashley
 */

public interface DocumentObject extends HierarchicalNode {

   /**
    * Gets the {@link DocumentObject} name.
    *
    * @return the name.
    */

   String getName();

   /**
    * Gets the {@link DocumentObject} type description.
    *
    * @return the type description.
    */

   String getTypeDescription();

   /**
    * Gets the value of the attribute that is referenced by the "Primary Attribute".
    *
    * @return when there is an attribute that is referenced by the "Primary Attribute" with a value, an {@link Optional}
    * containing the referenced attribute's value; otherwise, and empty {@link Optional}.
    */

   Optional<String> getPrimaryAttributeValue();

   /**
    * Gets the value of the "Name" attribute.
    *
    * @return where there is an attribute with the name "Name" and a value, an {@link Optional} containing the "Name"
    * attribute's value; otherwise, an empty {@link Optional}.
    */

   Optional<String> getNameAttributeValue();
}

/* EOF */