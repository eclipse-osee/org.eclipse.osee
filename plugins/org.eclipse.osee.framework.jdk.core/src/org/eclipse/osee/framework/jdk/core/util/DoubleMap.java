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

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A two level map structure where the primary key selects a secondary map and the secondary key is used as the key with
 * the secondary map.
 *
 * @param <Kp> the type of primary map keys
 * @param <Ks> the type of the secondary map keys
 * @param <V> the type of the map values
 * @author Loren K. Ashley
 */

public interface DoubleMap<Kp, Ks, V> {

   /**
    * Predicate to determine if the map contains a value or values associated with the primary key.
    *
    * @param primaryKey
    * @return <code>true</code> when the map contains a value of values associated with the provided primary key;
    * otherwise, <code>false</code>.
    */

   boolean containsKey(Kp primaryKey);

   /**
    * Predicate to determine if the map contains an association for the primary and secondary key pair.
    *
    * @param primaryKey the key used to select the secondary map.
    * @param secondaryKey the key used to select the value from the secondary map.
    * @return <code>true</code> when the map contains an association for the provided key pair; otherwise,
    * <code>false</code>.
    */

   boolean containsKey(Kp primaryKey, Ks secondaryKey);

   /**
    * Returns the secondary mappings for the primary key.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @return when the primary key maps to a set of secondary mappings, an {@link Optional} containing a {@link Map}
    * with the secondary mappings; otherwise, an empty {@link Optional}.
    */

   Optional<Map<Ks, V>> get(Kp primaryKey);

   /**
    * Returns the value which is mapped to the primary and secondary keys.
    *
    * @param primaryKey the key used to select the secondary map.
    * @param secondaryKey the key used to select the value from the secondary map.
    * @return when the key pair maps to a value, an {@link Optional} with the selected value; otherwise, an empty
    * {@link Optional}.
    */

   Optional<V> get(Kp primaryKey, Ks secondaryKey);

   /**
    * Returns a {@link Set} view of the primary map keys. The returned {@link Set} is backed by the primary map. Changes
    * in the primary map will be reflected in the returned {@link Set}. If a primary key is removed from the returned
    * set all associations with the primary key will be removed from the {@link DoubleMap}. Attempting to add a key to
    * the returned set will result in an {@link UnsupportedOperationException}.
    *
    * @return a {@link Set} view of the map's primary keys.
    */

   Set<Kp> keySet();

   /**
    * Returns a {@link Set} view of the secondary map keys associated with the primary key. The returned {@link Set} is
    * backed by the secondary map. Changes to the secondary map will be reflected in the returned {@link Set}. If a
    * secondary key is removed from the returned set the association with the secondary key will be removed from the
    * {@link DoubleMap}. Attempting to add a key to the returned set will result in an
    * {@link UnsupportedOperationException}. Removal of a key from a secondary map {@link Set} can leave the
    * {@link DoubleMap} with a primary key that is associated with an empty {@link Set}. The {@link DoubleMap}
    * implementation is not required to detect this condition and remove the empty association with the primary key.
    *
    * @param primaryKey the primary key used to select the secondary mappings.
    * @return when the primary key maps to a set of secondary mappings, an {@link Optional} with a {@link Set} view of
    * the secondary mappings associated with the primary key; otherwise, an empty {@link Optional}.
    */

   Optional<Set<Ks>> keySet(Kp primaryKey);

   /**
    * Associates the provide <code>value</code> with the primary and secondary keys. If a secondary map is not
    * associated with the primary key, the secondary map will be created and associated with the primary key.
    *
    * @param primaryKey the key used to select the secondary map.
    * @param secondaryKey the key used to associate the value with in the secondary map.
    * @param value the value to be associated with the key pair.
    * @return when the key pair maps to a value, an {@link Optional} with the previous value; otherwise, an empty
    * {@link Optional}.
    */

   Optional<V> put(Kp primaryKey, Ks secondaryKey, V value);

   /**
    * Returns the number of mapped values within the map.
    *
    * @return the number of mapped values within the map.
    */

   int size();

   /**
    * Returns the number of mappings associated with the primary key in the map.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @return when the primary key maps to a set of secondary mappings, a {@link Optional} with the number of secondary
    * mappings associated with the primary key; otherwise, an empty {@link Optional}.
    */

   Optional<Integer> size(Kp primaryKey);

   /**
    * Returns a {@link Collection} view of the values contained in the map.
    *
    * @return a collection view of the values contained in the map.
    */

   Collection<V> values();

   /**
    * Returns a {@link Collection} view of the value contained in the secondary mappings associated with the primary
    * key.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @return when the primary key maps to a set of secondary mappings, an {@link Optional} with a {@link Collection}
    * view of the values contained in the secondary mappings associated with the primary key; otherwise an empty
    * {@link Optional}.
    */

   Optional<Collection<V>> values(Kp primaryKey);
}

/* EOF */
