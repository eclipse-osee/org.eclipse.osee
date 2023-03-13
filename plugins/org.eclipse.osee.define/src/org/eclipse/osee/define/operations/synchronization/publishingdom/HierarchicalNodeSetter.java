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

/**
 * Package private interface for the methods to set hierarchy information on hierarchical nodes.
 *
 * @author Loren K. Ashley
 */

interface HierarchicalNodeSetter {

   /**
    * Sets the indicator of the {@link Node}'s hierarchical position.
    *
    * @param hierarchyLevels an array of integers indicating the hierarchical sequence position of each of the
    * {@link Node}'s parents and the {@link Node} itself in the last array index.
    */

   void setHierarchyLevels(int[] hierarchyLevels);

   /**
    * Gets the hierarchical depth of the {@link Node}.
    *
    * @return if the hierarchical level has been set, the hierarchical depth of the {@link Node}; otherwise, -1.
    */

   int[] getHierarchyLevels();

   /**
    * Returns an ordered {@link Stream} of the {@link Node}'s hierarchical children {@link HierarchicalNode} objects.
    *
    * @return a {@link Stream} of {@link HierarchicalNode} objects.
    */

   Stream<HierarchicalNode> streamHierarchicalChildren();

}

/* EOF */