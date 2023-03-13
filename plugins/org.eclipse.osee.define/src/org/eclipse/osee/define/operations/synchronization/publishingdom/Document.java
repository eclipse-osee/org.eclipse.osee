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

/**
 * The interface for DOM nodes that represents a document (Synchronization Artifact Specification).
 *
 * @author Loren K. Ashley
 */

public interface Document extends HierarchicalNode {

   /**
    * Gets the {@link Document} name.
    *
    * @return the name.
    */

   String getName();

   /**
    * Gets the {@link Document} type description.
    *
    * @return the type description.
    */

   String getTypeDescription();

}

/* EOF */
