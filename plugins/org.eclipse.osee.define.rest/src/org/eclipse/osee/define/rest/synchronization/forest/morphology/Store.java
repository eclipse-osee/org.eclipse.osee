/*********************************************************************
q * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.rest.synchronization.forest.morphology;

import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.osee.define.rest.synchronization.forest.Grove;
import org.eclipse.osee.define.rest.synchronization.forest.GroveThing;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Multi-rank map like data storage interface for {@link Grove} implementations. Keys can be any {@link Object}. When
 * assertions are enabled the keys must conform to the requirements set forth by the key validator methods the store
 * implementation was created with.
 *
 * @author Loren K. Ashley
 */

interface Store extends ToMessage {

   /**
    * Adds a {@link GroveThing} to the store. The {@link GroveThing} is queried for the storage keys.
    *
    * @param groveThing the {@link GroveThing} to be added to the store.
    * @throws DuplicateStoreEntryException when the store already contains an entry associated with the key(s) extracted
    * from the provided {@link GroveThing}.
    */

   void add(GroveThing groveThing);

   /**
    * Predicate to determine if the store contains a {@link GroveThing} with the specified key(s).
    *
    * @param keys the storage keys to check
    * @return <code>true</code> when the store contains an entry for the specified keys; otherwise, <code>false</code>.
    */

   boolean contains(Object... keys);

   /**
    * Gets the {@link GroveThing} associated with the specified key(s).
    *
    * @param keys of the {@link GroveThing} to get from the store.
    * @return when the store contains a {@link GroveThing} associated with the provided key(s), an {@link Optional}
    * containing the {@link GroveThing}; otherwise; an empty {@link Optional}.
    */

   Optional<GroveThing> get(Object... keys);

   /**
    * Gets the {@link StoreType} of the store.
    *
    * @return the {@link StoreType}.
    */

   StoreType getType();

   /**
    * Returns the rank of the store. The rank is the number of keys used by the store.
    *
    * @return the rank of the store.
    */

   int rank();

   /**
    * Determines the number of {@link GroveThing} objects in the store.
    *
    * @return the number of {@link GroveThing} objects in the store.
    */

   int size();

   /**
    * Returns an unordered {@link Stream} of the {@link GroveThing} objects stored in the {@link Grove} under the
    * provided keys. When no keys are provided the {@link Stream} will contain all of the {@link GroveThing} objects
    * within the {@link Store}.
    *
    * @param keys an array of keys. The number of keys specified should be less than or equal to the rank of the grove.
    * This parameter may be <code>null</code> or an empty array.
    * @return a unordered {@link Stream} of the objects stored in the grove under the specified keys.
    */

   Stream<GroveThing> stream(Object... keys);

   /**
    * Returns an unordered {@link Stream} of the keys in the sub-map specified by the provided keys and the keys in all
    * the sub-maps contained in and below the selected sub-map. The returned {@link Stream} may contain keys from
    * different levels with different types. If there isn't a sub-map associated with the provided keys an empty
    * {@link Stream} is returned.
    *
    * @param keys an array of keys. The number of keys specified must be less than the rank of the grove. The
    * <code>keys</code> parameter may be <code>null</code> or an empty array.
    * @return a unordered {@link Stream} of the keys from the selected sub-maps.
    * @throws IllegalArgumentException when the number of keys specified is greater than or equal to the rank of the
    * {@link Store}.
    */

   Stream<Object> streamKeysAtAndBelow(Object... keys);

   /**
    * Returns an unordered {@link Stream} of the keys in the sub-map specified by the provided keys. If there isn't a
    * sub-map associated with the provided keys an empty {@link Stream} is returned.
    *
    * @param keys an array of keys. The number of keys specified should be less than or equal to the rank of the grove.
    * This parameter may be <code>null</code> or an empty array.
    * @return a unordered {@link Stream} of the keys from the selected sub-map.
    * @throws IllegalArgumentException when the number of keys specified is greater than or equal to the rank of the
    * {@link Store}.
    */

   Stream<Object> streamKeysAt(Object... keys);

   /**
    * Returns an unordered {@link Stream} of the key sets for the {@link StoreType} from the {@link GroveThing} objects
    * stored in the {@link Store} under the provided keys. When no keys are provided the {@link Stream} will contain the
    * key sets from all of the {@link GroveThing} objects within the {@link Store}.
    *
    * @param keys an array of keys. The number of keys specified should be less than or equal to the rank of the grove.
    * This parameter may be <code>null</code> or an empty array.
    * @return a unordered {@link Stream} of the key sets for the {@link GroveThing} objects stored in the {@link Store}
    * under the specified keys.
    */

   Stream<Object[]> streamKeySets(Object... keys);

}

/* EOF */
