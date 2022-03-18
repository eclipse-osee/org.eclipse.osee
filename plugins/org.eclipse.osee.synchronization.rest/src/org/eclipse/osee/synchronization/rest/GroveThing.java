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
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.util.ToMessage;

/**
 * Implementations of this interface are a container used to hold a native OSEE thing and the corresponding foreign
 * thing derived from it by an {@link SynchronizationArtifactBuilder} implementation.
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
    * Get the unique {@link Identifier} associated with this {@link GroveThing}.
    *
    * @return a unique {@link Identifier}.
    */

   Identifier getGroveThingKey();

   /**
    * Gets a unique {@link Long} identifier associated with the native thing saved in this container.
    *
    * @return when the native thing implements the {@link Id} interface, an {@link Optional} with the {@link Long}
    * identifier; otherwise, an empty {@link Optional}.
    */

   Optional<Long> getNativeKey();

   /**
    * Get the native OSEE thing saved in this container.
    *
    * @return the native OSEE thing.
    */

   Object getNativeThing();

   /**
    * Sets the foreign thing saved in this container. Foreign things are created by the converter methods provided by a
    * {@link SynchronizationArtifactBuilder} implementation.
    *
    * @param foreignThing the foreign thing to be saved in the container.
    */

   void setForeignThing(Object foreignThing);

   /**
    * Sets the native OSEE thing saved in this container.
    *
    * @param nativeThing the native OSEE thing to be saved.
    * @return the {@link GroveThing}.
    */

   GroveThing setNativeThing(Object nativeThing);
}

/* EOF */
