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

package org.eclipse.osee.synchronization.rest.forest;

import java.util.Optional;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.HierarchyRootGroveThing;

/**
 * Class to represent a SpecificationGroveThing in the Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

public final class SpecificationGroveThing extends CommonObjectGroveThing implements HierarchyRootGroveThing {

   /**
    * Creates a new {@link SpecificationGroveThing} object with a unique identifier.
    */

   SpecificationGroveThing(GroveThing parent) {
      super(IdentifierType.SPECIFICATION.createIdentifier());
   }

   public Optional<Object[]> getPrimaryHierarchyKeys() {
      var key = this.groveThingKeys[this.groveThingKeys.length - 1];
      return Optional.of(new Object[] {key, key, key});
   }
}

/* EOF */
