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

import java.util.Optional;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.rest.SynchronizationArtifactBuilder;
import org.eclipse.osee.synchronization.util.ToMessage;

/**
 * Implementations of this interface are a container used to hold native OSEE things and the corresponding foreign thing
 * derived from it by an {@link SynchronizationArtifactBuilder} implementation.
 * <p>
 * Implementations of {@link GroveThing} have a rank and native rank which should match the rank of the {@link Grove}
 * the {@link GroveThing} objects are stored in. The rank determines the number of keys returned by the methods
 * {@link #getPrimaryKeys()} and {@link #getNativeKeys()}.
 *
 * @author Loren K. Ashley
 */

public interface GroveThing extends ToMessage {

   /**
    * Get the foreign thing saved in this container.
    *
    * @return the foreign thing.
    */

   Object getForeignThing();

   /**
    * Gets the unique {@link Identifier} for the {@link GroveThing}.
    */

   Identifier getIdentifier();

   /**
    * Get the {@link Identifier} objects that identify this {@link GroveThing} for the grove's organizational structure.
    *
    * @return an array of {@link Identifier}s.
    */

   Optional<Object[]> getPrimaryKeys();

   /**
    * Gets a key set derived from the native OSEE thing or things stored in this container. The key set returned by the
    * implementation must uniquely identify the {@link GroveThing}.
    *
    * @return when native keys are available, an {@link Optional} containing an array of the native keys; otherwise, an
    * empty {@link Optional}.
    */

   Optional<Object[]> getNativeKeys();

   /**
    * Get the native OSEE thing of lowest rank saved in this container.
    *
    * @return the native OSEE thing.
    */

   Object getNativeThing();

   /**
    * The native rank is the number of native keys that will be returned by the method {@link #getNativeKeys}.
    *
    * @return the native rank.
    */

   int nativeRank();

   /**
    * The rank is the number of keys that will be returned by the method {@link #getPrimaryKeys}.
    *
    * @return the rank.
    */

   int rank();

   /**
    * Sets the foreign thing saved in this container. Foreign things are created by the converter methods provided by a
    * {@link SynchronizationArtifactBuilder} implementation.
    *
    * @param foreignThing the foreign thing to be saved in the container.
    */

   void setForeignThing(Object foreignThing);

   /**
    * Sets the native OSEE thing or things saved in this container. The highest rank native thing is at the low array
    * index and the lowest rank native thing is at the highest array index.
    *
    * @param nativeThing the native OSEE thing to be saved.
    * @return the {@link GroveThing}.
    */

   GroveThing setNativeThings(Object... nativeThings);
}

/* EOF */
