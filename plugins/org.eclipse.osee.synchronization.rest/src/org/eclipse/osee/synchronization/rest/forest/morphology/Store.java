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

package org.eclipse.osee.synchronization.rest.forest.morphology;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.util.ParameterArray;
import org.eclipse.osee.synchronization.util.ToMessage;

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

   /**
    * Factory for obtaining {@link Store} implementations. {@link Grove} implementations provide access to
    * {@link GroveThing}s with key sets composed of {@link GroveThing} {@link Identifier} objects or with key sets
    * composed of native OSEE keys. {@link Grove} implementations use two {@link Store} implementations. One for the
    * {@link Identifier} key sets and the other for the native OSEE key sets. The {@link StoreType} enumeration member
    * contains one of the following methods for extracting keys:
    * <ul>
    * <li>{@link GroveThing#getPrimaryKeys()}</li>
    * <li>{@link GroveThing#getNativeKeys()}</li>
    * </ul>
    * The superclass of everything the {@link Object} provides the methods <code>hashCode</code> and <code>equals</code>
    * which makes an object of any class suitable for use as a map key. The <code>keyValidators</code> are predicate
    * {@link Function} implementations that are applied to each key when assertions are enabled. The provided predicates
    * can be used to ensure that keys are non-null, of a particular class, or even if other expected properties are
    * present. The key validator function from the same array position as key will be used to check that key.
    *
    * @implNote General map stores of rank 1 ({@link Map}) &amp; 2 ({@link DoupbleMap}) are supported for both primary
    * and native stores. A rank 3 map of hierarchical trees is implemented for primary stores.
    * @param storeType specifies if the store is to use for primary or native keys.
    * @param keyValidators an array of predicate {@link Function}s that will be used to validate keys passed when
    * assertions are enabled.
    * @return an implementation of the {@link Store} interface with a rank matching the number of provided key
    * validators.
    */

   @SafeVarargs
   static Store create(StoreType storeType, Function<Object, Boolean>... keyValidators) {
      //@formatter:off
      assert
            Objects.nonNull(storeType)
         && ParameterArray.validateNonNullAndSize(keyValidators, 1, 3 );
      //@formatter:on

      switch (keyValidators.length) {
         case 1:
            return new StoreRankN(storeType, 1, keyValidators);

         case 2:
            return new StoreRankN(storeType, 2, keyValidators);

         case 3:
            return new StoreRank3(storeType, keyValidators[0], keyValidators[1], keyValidators[2]);

         default:
            //When assertions are enabled, execution should never reach here
            return null;
      }

   }
}

/* EOF */
