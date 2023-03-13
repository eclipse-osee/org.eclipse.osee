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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * An interface for multi-rank (level) maps. To accommodate a variable number of keys, the interface uses a varargs
 * parameter for the map keys. This precludes the use of generic type parameters for the map keys.
 *
 * @author Loren K. Ashley
 * @param <V> the type of value stored in the map.
 */

public interface RankMap<V> {

   /**
    * A map entry (full key set / value pair) representing an association in the map. {@link RankMap.Entry} objects are
    * not modifiable by users of the map. The {@link RankMap.Entry} objects are backed by the map and changes to the map
    * affecting the association represented by the {@link RankMap.Entry} will be reflected in the {@link RankMap.Entry}
    * object.
    *
    * @param <V> the type of value stored in the map.
    */

   interface Entry<V> {

      /**
       * {@link RankMap.Entry} objects are equal when both entries belong to the same map, contain the same key set, and
       * contain the same value.
       *
       * @apiNote The {@link #equals} method should not throw an exception when the entry has been removed from the
       * backing map. If this entry has been removed from the backing map, had been stored in another map as a value,
       * and that other map under goes a reallocation an exception could be thrown. This could result in unpredictable
       * program behavior.
       */

      @Override
      boolean equals(Object other);

      /**
       * Returns the key with the specified rank.
       *
       * @param rank of the key to obtain.
       * @return the key of the specified rank.
       * @throws IllegalStateException when the entry has been removed from the backing map.
       */

      Object getKey(int keyRank);

      /**
       * Returns an array containing the full key set contained in the {@link RankMap.Entry}. The primary key (rank 0)
       * is located in the low index (0) of the array. The secondary key (rank 1) is located in the second position
       * (index 1) of the array and so on.
       *
       * @return the full key set.
       * @throws IllegalStateException when the entry has been removed from the backing map.
       */

      Object[] getKeyArray();

      /**
       * Returns the value associated with the full key set contained in the {@link RankMap.Entry}.
       *
       * @return the associated value.
       * @throws IllegalStateException when the entry has been removed from the backing map.
       */

      V getValue();

      /**
       * Returns the hash code for the map entry calculated from the full key set and the associated value.
       *
       * @return the hash code value.
       * @apiNote The {@link #equals} method should not throw an exception when the entry has been removed from the
       * backing map. If this entry has been removed from the backing map, had been stored in another map as a value,
       * and that other map under goes a reallocation an exception could be thrown. This could result in unpredictable
       * program behavior.
       */

      @Override
      int hashCode();

      /**
       * Returns the rank of the map the {@link RankMap.Entry} object is a member of. The rank is the number of keys
       * used by the store and hence, the number of keys contained in the {@link RankMap.Entry} object.
       *
       * @return the rank of the map.
       * @throws IllegalStateException when the entry has been removed from the backing map.
       */

      int rank();

      /**
       * Returns a {@link Stream} of the keys contained in the {@link RankMap.Entry} object.
       *
       * @return a {@link Stream} of the contained keys.
       * @throws IllegalStateException when the entry has been removed from the backing map.
       */

      Stream<Object> streamKeys();
   }

   /**
    * An extension of the {@link Set} interface used to provide a set view of the associations in the map.
    *
    * @param <V> the type of value stored in the map.
    */

   interface EntrySet<V> extends Set<Entry<V>> {

      /**
       * Predicate used to determine if the provided full or partial key set has an association in the map.
       *
       * @param keys the full or partial key set to test for an association.
       * @return <code>true</code>, when there is an association in the map for the provided full or partial key set;
       * otherwise, <code>false</code>.
       */

      boolean containsKeys(Object... keys);
   }

   /**
    * Associates the provided value with the specified full key set in the map. If the map currently contains an
    * association for the full key set, the currently associated value is replaced with the provided value. The replaced
    * value is returned.
    *
    * @param value the value to be added to the map.
    * @param keys the full key set to associate the value with.
    * @return if the map currently contains a mapping for the full key set, an {@link Optional} with the replaced value;
    * otherwise, an empty {@link Optional}.
    * @throws RankMapTooManyKeysException when the number of provided keys exceeds the rank of the map.
    * @throws RankMapInsufficientKeysException when the number of provided keys is less than the rank of the map.
    * @throws RankMapNullValueException when the provided value is <code>null</code>.
    */

   Optional<V> associate(V value, Object... keys);

   /**
    * Associates the provided value with the specified full key set in the map. If the map currently contains a mapping
    * for the full key set, an {@link RankMapDuplicateEntryException} is thrown.
    *
    * @param value the value to be added to the map.
    * @param keys the full key set to associate the value with.
    * @return if the map currently contains a mapping for the full key set, an {@link Optional} with the replaced value;
    * otherwise, an empty {@link Optional}.
    * @throws RankMapTooManyKeysException when the number of provided keys exceeds the rank of the map.
    * @throws RankMapInsufficientKeysException when the number of provided keys is less than the rank of the map.
    * @throws RankMapNullValueException when the provided value is <code>null</code>.
    * @throws RankMapDuplicateEntryException when the map already contains an entry associated with the full key set.
    */

   void associateThrowOnDuplicate(V value, Object... keys);

   /**
    * Predicate to determine if the map contains a value or values associated with the specified full or partial key
    * set.
    *
    * @param keys the map keys to check
    * @return <code>true</code> when the map contains at least one association for the specified full or partial key
    * set; otherwise, <code>false</code>.
    * @throws RankMapTooManyKeysException when the number of provided keys exceeds the rank of the map.
    * @throws RankMapInsufficientKeysException when the provided key array is <code>null</code> or empty.
    */

   boolean containsKeys(Object... keys);

   /**
    * Predicate to determine if the map contains a value or values associated with the specified full or partial key
    * set. Unlike the {@link #containsKeys} method, when to many keys or an insufficient number of keys is provided the
    * method will return <code>false</code>.
    *
    * @param keys the map keys to check
    * @return <code>true</code> when the map contains at least one association for the specified full or partial key
    * set; otherwise, <code>false</code>.
    */

   boolean containsKeysNoExceptions(Object... keys);

   /**
    * Gets an {@link EntrySet} of all the {@link Entry} objects in the {@link RankMap}. The set and entries are backed
    * by the map. Changes to the map or the map's entries will be reflected in the returned {@link EntrySet} and
    * {@link Entry} objects.
    *
    * @return a {@link Set} view of the associations in the map.
    */

   RankMap.EntrySet<V> entrySet();

   /**
    * Gets the value associated with the specified full key set from the map.
    *
    * @param keys the full key set to get associated value of.
    * @return when the map contains an association for the full key set, an {@link Optional} containing the associated
    * value; otherwise; an empty {@link Optional}.
    * @throws RankMapTooManyKeysException when the number of provided keys exceeds the rank of the map.
    * @throws RankMapInsufficientKeysException when the number of provided keys is less than the rank of the map.
    */

   Optional<V> get(Object... keys);

   /**
    * Gets the {@link Entry} for the association specified by a full key set from the map. The returned {@link Entry} is
    * backed by the map and any changes to made to the map for the association will be reflected in the {@link Entry}.
    *
    * @param keys the full key set to get the map association of.
    * @return when the map contains an association for the full key set, an {@link Optional} containing the map
    * {@link Entry} for the association; otherwise, an empty {@link Optional}.
    * @throws RankMapTooManyKeysException when the number of provided keys exceeds the rank of the map.
    * @throws RankMapInsufficientKeysException when the number of provided keys is less than the rank of the map.
    */

   Optional<RankMap.Entry<V>> getEntry(Object... keys);

   /**
    * Gets the {@link Entry} for the association specified by a full key set from the map. The returned {@link Entry} is
    * backed by the map and any changes to made to the map for the association will be reflected in the {@link Entry}.
    * Unlike the {@link #getEntry} method, when too many or an insufficient number of keys is provided the method will
    * return an empty {@link Optional}.
    *
    * @param keys the full key set to get the map association of.
    * @return when the map contains an association for the full key set, an {@link Optional} containing the map
    * {@link Entry} for the association; otherwise, an empty {@link Optional}.
    */

   Optional<RankMap.Entry<V>> getEntryNoExceptions(Object... keys);

   /**
    * Gets the value associated with the specified full key set from the map. Unlike the {@link #get} method, when too
    * many or an insufficient number of keys is provided the method will return an empty {@link Optional}.
    *
    * @param keys the full key set to get associated value of.
    * @return when the map contains an association for the full key set, an {@link Optional} containing the associated
    * value; otherwise; an empty {@link Optional}.
    */

   Optional<V> getNoExceptions(Object... keys);

   /**
    * Gets an identification string for the map. The identifier is not guaranteed to be unique.
    *
    * @return the map's identification string.
    */

   String identifier();

   /**
    * Removes the association for the provided full key set from the map. If the map currently contains an association
    * for the full key set, the currently associated value is returned.
    *
    * @param keys the full key set whose association is to be removed from the map.
    * @return an {@link Optional} with the currently associated value; otherwise, an empty {@link Optional}.
    * @throws RankMapTooManyKeysException when the number of provided keys exceeds the rank of the map.
    * @throws RankMapInsufficientKeysException when the number of provided keys is less than the rank of the map.
    */

   Optional<V> remove(Object... keys);

   /**
    * Removes the association for the provided full key set from the map. If the map currently contains an association
    * for the full key set, the currently associated value is returned. Unlike the method {@link #remove}, when to many
    * or an insufficient number of keys is provided the method will return an empty {@link Optional}.
    *
    * @param keys the full key set whose association is to be removed from the map.
    * @return an {@link Optional} with the currently associated value; otherwise, an empty {@link Optional}.
    */

   Optional<V> removeNoException(Object... keys);

   /**
    * Returns the rank of the map. The rank is the number of keys used by the store.
    *
    * @return the rank of the map.
    */

   int rank();

   /**
    * Determines the number of values in the map.
    *
    * @return the number of values in the map.
    */

   int size();

   /**
    * Determines the number of values in the sub-map specified by the <code>keys</code>. When no keys are specified the
    * size of the primary map is determined. When a full set of keys are specified, one is returned when the map
    * contains an entry for the specified keys; otherwise, zero is returned.
    *
    * @param keys an array of keys. The number of keys specified should be less than or equal to the rank of the map.
    * This parameter may be <code>null</code> or an empty array.
    * @return the number of entries in at the level specified by the keys.
    */

   int size(Object... keys);

   /**
    * Returns an unordered {@link Stream} of the values stored in the map under the provided keys. When no keys are
    * provided the {@link Stream} will contain all of the values within the map.
    *
    * @param keys an array of keys. The number of keys specified should be less than or equal to the rank of the grove.
    * This parameter may be <code>null</code> or an empty array.
    * @return a unordered {@link Stream} of the objects stored in the map under the specified keys.
    * @throws RankMapTooManyKeysException when the number of provided keys exceeds the rank of the map.
    */

   Stream<V> stream(Object... keys);

   /**
    * Returns an unordered {@link Stream} of the entries stored in the map under the provided keys. When no keys are
    * provide all {@link Entry} objects in the map are contained in the {@link Stream}. The streamed {@link Entry}
    * objects are backed by the map and changes to the map will be reflected in the streamed {@link Entry} objects.
    *
    * @param keys an array of keys. The number of keys specified should be less than or equal to the rank of the grove.
    * This parameter may be <code>null</code> or an empty array.
    * @return a unordered {@link Stream} of the {@link Entry} objects stored in the map under the specified keys.
    * @throws RankMapTooManyKeysException when the number of provided keys exceeds the rank of the map.
    */

   Stream<RankMap.Entry<V>> streamEntries(Object... keys);

   /**
    * Returns an unordered {@link Stream} of the keys in the sub-map specified by the provided keys and the keys in all
    * the sub-maps contained in and below the selected sub-map. The returned {@link Stream} may contain keys from
    * different levels with different types. A key that is present in more than one sub-map will be present in the
    * {@link Stream} once for each occurrence of the key in a sub-map. Use the {@link Stream#distinct} method to remove
    * duplicate occurrences of keys from the stream if desired. If there isn't a sub-map associated with the provided
    * keys an empty {@link Stream} is returned.
    *
    * @param keys an array of keys. The number of keys specified must be less than the rank of the grove. The
    * <code>keys</code> parameter may be <code>null</code> or an empty array.
    * @return a unordered {@link Stream} of the keys from the selected sub-maps.
    * @throws RankMapTooManyKeysException when the number of keys specified is greater than or equal to the rank - 1 of
    * the map.
    */

   Stream<Object> streamKeysAtAndBelow(Object... keys);

   /**
    * Returns an unordered {@link Stream} of the keys in the sub-map specified by the provided keys. If there isn't a
    * sub-map associated with the provided keys an empty {@link Stream} is returned.
    *
    * @param keys an array of keys. The number of keys specified should be less than or equal to the rank of the grove.
    * This parameter may be <code>null</code> or an empty array.
    * @return a unordered {@link Stream} of the keys from the selected sub-map.
    * @throws RankMapTooManyKeysException when the number of keys specified is greater than or equal to the rank - 1 of
    * the map.
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
    * @throws RankMapTooManyKeysException when the number of provided keys exceeds the rank of the map.
    */

   Stream<Object[]> streamKeySets(Object... keys);

}

/* EOF */
