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

package org.eclipse.osee.synchronization.rest.forest.morphology;

/**
 * {@link GroveThings} implementing this interface can be added to a {@link StoreRank3} store as a hierarchical root.
 * Only rank 1 {@link GroveThing} implementations should implement this interface.
 *
 * @author Loren K. Ashley
 */
public interface HierarchyRootGroveThing extends GroveThing {

   /**
    * Assertion method to validate the rank of the implementing {@link GroveThing}.
    */

   public default void validate() {
      assert (this.rank() == 1);
   }
}

/* EOF */