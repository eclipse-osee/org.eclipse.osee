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
 * Interface for the hierarchy related methods for DOM nodes that are hierarchical in nature.
 *
 * @author Loren K. Ashley
 */

public interface HierarchicalNode extends Node {

   /**
    * Gets the hierarchical depth of the {@link Node}.
    *
    * @return if the hierarchical level has been set, the hierarchical depth of the {@link Node}; otherwise, -1.
    */

   int getHierarchyLevel();

   /**
    * Gets an array containing the numerical sequence position of the {@link Node} and the {@link Node}'s parents at
    * each level of the hierarchy.
    *
    * @return the hierarchy level array.
    */

   int[] getHierarchyLevels();

   /**
    * Gets a {@link String} representation of the {@link #hierarchyLevels} array.
    *
    * @return the hierarchy level string.
    */

   String getHierarchyLevelString();

   /**
    * Get the number of hierarchical child {@link Node}s.
    *
    * @return the number of child {@link Node}s.
    */

   int hierarchicalSize();

   /**
    * Returns an ordered {@link Stream} of the {@link Node}'s hierarchical children {@link HierarchicalNode} objects.
    *
    * @return a {@link Stream} of {@link HierarchicalNode} objects.
    */

   Stream<HierarchicalNode> streamHierarchicalChildren();

}

/* EOF */