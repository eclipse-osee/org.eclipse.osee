/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Interface for an object that maps keys to collections of values. The interface extends the interface for
 * {@link Map}<code>&lt;K,C&gt;</code> with additional methods for directly accessing or adding values to the
 * collections contained within the map.
 *
 * @author Loren K. Ashley
 * @param <K> the map key type.
 * @param <V> the type of value saved in the collections associated with the map keys.
 * @param <C> the type of collection associated with the map keys.
 */

public interface MapCollection<K, V, C extends Collection<V>> extends Map<K, C> {

   /**
    * Determines if the <code>value</code> is contained in any of the {@link Collection}s in the {@link MapCollection}.
    *
    * @param value the value to look for.
    * @return <code>true</code>, when the {@link MapCollection} contains <code>value</code>; otherwise,
    * <code>false</code>.
    */

   boolean containsValueInAnyCollection(Object value);

   /**
    * Performs the <code>action</code> for each key and the values from the {@link Collection} associated with the key
    * for all of the keys in the {@link MapCollection}.
    *
    * @param action the action to be performed on key and value pair.
    */

   void forEachEntry(Consumer<Map.Entry<K, V>> action);

   /**
    * Performs the <code>action</code> for each value in the {@link Collection} associated with <code>key</code> and the
    * key.
    *
    * @param key the key whose associated {@link Collection} that will have it's values processed.
    * @param action the action to be performed on each value.
    */

   void forEachEntry(K key, Consumer<Map.Entry<K, V>> action);

   /**
    * Performs the <code>action</code> for each key and the values from the {@link Collection} associated with the key
    * for all of the keys in the {@link MapCollection}.
    *
    * @param action the action to be performed on key and value pair.
    */

   void forEachValue(BiConsumer<K, V> action);

   /**
    * Performs the <code>action</code> for each value in the {@link Collection} associated with <code>key</code>.
    *
    * @param key the key whose associated {@link Collection} that will have it's values processed.
    * @param action the action to be performed on each value.
    */

   void forEachValue(K key, Consumer<V> action);

   /**
    * Gets the an {@link Optional} containing the {@link Collection} associated with the <code>key</code>.
    *
    * @param key the key whose associated {@link Collection} is to be obtained.
    * @return when a {@link Collection} is associated with the <code>key</code>, an {@link Optional} containing the
    * associated {@link Collection}; otherwise, and empty {@link Optional}.
    */

   Optional<C> getOptional(K key);

   /**
    * Adds the values in the {@link Collection} <code>values</code> to a collection in this object associated with the
    * specified <code>key</code>.
    *
    * @implSpec Implements are expected to create a new {@link Collection} when an existing collection is not associated
    * with the specified <code>key</code> and associated it with the <code>key</code>. This decouples the collection in
    * this object with the <code>values</code> collection.
    * @param key the key whose associated {@link Collection} will be used to store all the values from the provided
    * <code>values</code> {@link Collection}.
    * @param values the collection to copy values from.
    * @return the collection containing the added values.
    */

   C putAll(K key, C values);

   /**
    * Adds the value in the {@link Map#Entry} to the {@link Collection} associated with the key in the
    * {@link Map#Entry}. When the key does not have an associated {@link Collection} an new {@link Collection} is
    * created and associated with the key. The value is added using the method {@link Collection#add(Object)}.
    *
    * @param entry the {@link Map.Entry} containing the key and value to be added.
    * @return the collection containing the added value.
    */

   C putEntry(Map.Entry<K, V> entry);

   /**
    * Adds the <code>value</code> to the {@link Collection} associated with the <code>key</code>. When the
    * <code>key</code> does not have an associated {@link Collection} an new {@link Collection} is created and
    * associated with the <code>key</code>. The <code>value</code> is added using the method
    * {@link Collection#add(Object)}.
    *
    * @param entry the {@link Map.Entry} containing the key and value to be added.
    * @return the collection containing the added value.
    */

   C putValue(K key, V value);

   /**
    * Removes the value in the {@link Map#Entry} from the {@link Collection} associated with the key in the
    * {@link Map#Entry}. If the value is also contained in any {@link Collection}s associated with other keys, the value
    * will not be removed from those {@link Collection}s.
    *
    * @param entry a {@link Map#Entry} contain the key of the {@link Collection} to remove the value contained in the
    * {@Link Map#Entry} from.
    * @return <code>true</code>, when the {@link MapCollection} was modified; otherwise, <code>false</code>.
    */

   boolean removeEntry(Map.Entry<K, V> entry);

   /**
    * Removes the <code>value</code> from the {@link Collection} associated with the <code>key</code>. If the
    * <code>value</code> is also contained in any {@link Collection}s associated with other keys, the <code>value</code>
    * will not be removed from those {@link Collection}s.
    *
    * @param key the key associated with the collection to remove the value from.
    * @param value the value to be removed.
    * @return <code>true</code>, when the {@link MapCollection} was modified; otherwise, <code>false</code>.
    */

   boolean removeValue(K key, V value);

   /**
    * Returns the number of values in the collection associated with the <code>key</code>.
    *
    * @param key the key whose associated {@link Collection} size is to be obtained.
    * @return when <code>key</code> is associated with a {@link Collection}, the number of values in the
    * {@link Collection}; otherwise, zero.
    */

   int size(K key);

   /**
    * Returns the number of values in all of the {@link Collection}s with in the {@link MapCollection}. If a value is
    * contained in more than one {@link Collection}, it will be counted once for each {@link Collection} containing the
    * value.
    *
    * @return the sum of the sizes of all the {@link Collection}s in the {@link MapCollection}.
    */

   int sizeValues();

   /**
    * Provides an unordered {@link Stream} of all the values in the {@link Collection} associated with the
    * <code>key</code>.
    *
    * @param key the key whose associated {@Link Collection} is to be streamed.
    * @return when a {@link Collection} is associated with the <code>key</code>, a {@link Stream} of the values
    * contained in the associated {@link Collection}; otherwise an empty {@link Stream}.
    */

   Stream<V> stream(K key);

   /**
    * Provides an unordered {@link Stream} of the {@link Collection}s in the map.
    *
    * @return an unordered {@link Stream} of the {@link Collection}s in the map.
    */

   Stream<C> streamCollections();

   /**
    * Provides an unordered {@link Stream} of all the values within all the {@link Collection}s in the map. The values
    * are wrapped in an {@link Map.Entry} with the key the {@link Collection} the value came from is associated with.
    *
    * @return a {@link Stream} of {@link Map.Entry}<code>&lt;K,V&gt;</code> objects.
    */

   Stream<Map.Entry<K, V>> streamEntries();
}

/* EOF */
