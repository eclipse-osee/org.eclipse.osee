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
 
package org.eclipse.osee.synchronization.rest;

import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.util.ToMessage;

/**
 * Stores a collection of map or tree like collections of objects implementing the {@link GroveThing} interface.
 *
 * @author Loren K. Ashley
 */

public interface Grove extends ToMessage {

   /**
    * Adds an association of a {@link GroveThing} implementation and it's key to the {@link Grove}. The key is obtained
    * using the {@link GroveThing} interface. {@link Grove} implementations with complex structures that require
    * additional parameters may throw an {@link UnsupportedOperationException}.
    *
    * @param groveThing the {@link GroveThing} object to be added to the grove.
    * @return the {@link GroveThing} added to the grove.
    * @throws UnsupportedOperationException when the {@link Grove} requires additional parameters to add
    * {@link GroveThing}s.
    * @throws DuplicateGroveEntry when the {@link Grove} already contains an entry with the provided
    * {@link GroveThing}'s key.
    * @throws NullPointerException when the provided {@link GroveThing} is <code>null</code>.
    */

   GroveThing add(GroveThing groveThing);

   /**
    * Predicate to determine if the {@link Grove} contains a {@link GroveThing} with an associated native thing that has
    * the specified native key.
    *
    * @param nativeKey the key of the native thing to the presence of.
    * @return <code>true</code>, when the {@link Grove} contains a native thing with the provided key; otherwise,
    * <code>false</code>.
    */

   public boolean contains(Long nativeKey);

   /**
    * Obtains a converter method from the {@link SynchronizationArtifactBuilder} for the implementation class of the
    * {@link GroveThing}s stored in this {@link Grove}. The converter method is then applied to each {@link GroveThing}
    * in the {@link Grove}.
    *
    * @param synchronizationArtifactBuilder the implementation of the {@link SynchronizationArtifactBuilder} interface
    * to obtain the converter methods from.
    */

   void createForeignThings(SynchronizationArtifactBuilder synchronizationArtifactBuilder);

   /**
    * Get the {@link GroveThing} object stored in the grove identified by the key.
    *
    * @param groveThingKey the key who's associated value is returned
    * @return the {@link GroveThing} object associated with the <code>groveThingKey</code>; otherwise,
    * <code>null</code>.
    */

   GroveThing get(Identifier groveThingKey);

   /**
    * Get the {@link GroveThing} with an associated native thing that has the provided unique identifier.
    *
    * @param nativeKey a unique identifier for the native thing
    * @return when the map contains a {@link GroveThing} associated with the provided native key, an {@link Optional}
    * containing the associated {@link GroveThing}; otherwise, and empty {@link Optional}.
    */

   public Optional<GroveThing> getByNativeKey(Long nativeKey);

   /**
    * Gets the {@link IdentifierType} associated with the class implementing the {@link GroveThing}s saved in the
    * {@link Grove}.
    *
    * @return the {@link IdentifierType} associated with the implementation class of the {@link GroveThing}s.
    */

   IdentifierType getType();

   /**
    * Returns an unordered {@link Stream} of the {@link GroveThing} objects stored in the grove.
    *
    * @return an unordered {@link Stream} of the objects stored in the grove.
    */

   Stream<GroveThing> stream();
}

/* EOF */
