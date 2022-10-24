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

package org.eclipse.osee.define.operations.synchronization.forest.morphology;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;

/**
 * Enumeration used to indicate if a {@link Store} uses Synchronization Artifact {@link GroveThing} {@link Identifier}
 * keys (primary), or native OSEE values as keys (native).
 *
 * @author Loren K. Ashley
 */

enum StoreType {

   /**
    * Descriptor for {@link Store}s that use native OSEE values as keys.
    */

   NATIVE(GroveThing::getNativeKeys),

   /**
    * Descriptor for {@link Store}s that use the {@link GroveThing} and it's parents Synchronization Artifact
    * {@link Identifier}s as keys.
    */

   PRIMARY(GroveThing::getPrimaryKeys),

   /**
    * Descriptor for hierarchical {@link Store}s that use the {@link GroveThing} and it's parents Synchronization
    * Artifact {@link Identifier}s as keys.
    */

   PRIMARY_HIERARCHY(GroveThing::getPrimaryKeys);

   /**
    * Saves the {@Function} to extract store key(s) from a {@link GroveThing} for the type of store.
    */

   Function<GroveThing, Optional<Object[]>> keySupplier;

   /**
    * Creates a new {@link StoreType} enumeration member with the associated {@link GroveThing} key extraction
    * {@link Function}.
    *
    * @param keySupplier the {@link Function} to extract store key(s) from a {@link GroveThing} for the type of store.
    */

   StoreType(Function<GroveThing, Optional<Object[]>> keySupplier) {
      this.keySupplier = Objects.requireNonNull(keySupplier);
   }

   /**
    * Extracts the proper type of keys from the {@link GroveThing} object for the {@link StoreType}.
    *
    * @param groveThing the {@link GroveThing} to extract store keys from.
    * @return when the {@link GroveThing} contains keys for the {@link StoreType}, an {@link Optional} containing the
    * extracted keys; otherwise, an empty {@link Optional}.
    */

   Optional<Object[]> getKeys(GroveThing groveThing) {
      return this.keySupplier.apply(groveThing);
   }
}

/* EOF */
