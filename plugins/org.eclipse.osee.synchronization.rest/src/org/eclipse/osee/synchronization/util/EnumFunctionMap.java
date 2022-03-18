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

package org.eclipse.osee.synchronization.util;

import java.util.Optional;
import java.util.Set;

/**
 * A limited map type interface for a map with keys that are members of a single enumeration and values that are an
 * implementation of a functional interface.
 *
 * @author Loren K. Ashley
 * @param <K> the enumeration type whose members may be used as keys in this map.
 * @param <F> the mapped functional interfaces.
 */

public interface EnumFunctionMap<K extends Enum<K>, F> {

   /**
    * Predicate to determine if the map contains a key-function mapping for the specified key.
    *
    * @param key the key whose presence in the map is to be tested
    * @return <code>true</code>, when the map contains a key-function mapping for the specified key; otherwise,
    * <code>false</code>.
    * @throws NullPointerException when the specified key is <code>null</code>.
    */

   boolean containsKey(K key);

   /**
    * Returns the function to which the specified key is mapped.
    *
    * @param key the key whose associated function is desired.
    * @return an {@link Optional} containing the function associated with the provided key; otherwise, an empty
    * {@link Optional} is returned when the key is not present in the map.
    */

   public Optional<F> getFunction(K key);

   /**
    * Predicate to determine if the map contains no key-function mappings.
    *
    * @return <code>true</code>, when the map contains no key-function mappings; otherwise, <code>false</code>.
    */

   boolean isEmpty();

   /**
    * Returns a {@link Set} view of the keys contained in this map. The set is backed by the map, so changes to the map
    * are reflected in the set. Implementations are required to return an unmodifiable {@link Set}. The returned
    * {@link Set} should throw an {@link UnsporttedOperationException} if an attempt is made to add or remove items from
    * the {@link Set}. The set's iterator will return the keys in the order in which the enumeration constants are
    * declared.
    *
    * @return a set view of the keys contained in this {@link EnumFunctionMap}.
    */

   Set<K> keySet();

   /**
    * Associates the specified function with the specified key in this map.
    *
    * @param key the key with which the specified function is to be associated.
    * @param function the function to be associated with the specified key.
    * @throws NullPointerException when the specified key or function is <code>null</code>.
    * @throws EnumMapDuplicateEntryException when the map already contains a mapping for the specified key.
    */

   void put(K key, F function);

   /**
    * Returns the number of key-function mappings in the map.
    *
    * @return the number of key-function mappings in the map.
    */

   int size();

}

/* EOF */