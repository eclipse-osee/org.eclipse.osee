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

import java.util.Map;
import java.util.Set;

/**
 * Interface for an object that maps keys to {@link Set} collections of values. The interface extends the interface for
 * {@link Map}<code>&lt;K,{@link Set}&lt;V&gt;&gt;</code> with additional methods for directly accessing or adding
 * values to the collections contained within the map.
 *
 * @author Loren K. Ashley
 * @param <K> the map key type.
 * @param <V> the type of value saved in the {@link Set} collections associated with the map keys.
 */

public interface MapSet<K, V> extends MapCollection<K, V, Set<V>> {

   /**
    * Creates an immutable map of immutable sets from the <code>entries</code>. The {@link Set} implementations in the
    * <code>entries</code> are copied to new immutable {@link Set}s. So the collections in the returned {@link MapSet}
    * are independent from the collections provided in <code>entries</code>. Changes to the provided collections will
    * not be reflected in the returned {@link MapSet}.
    *
    * @param <K> the map key type.
    * @param <V> the type of value saved in the {@link List} collections associated with the map keys.
    * @param entries {@link Map.Entry}s containing the keys and {@link List} collections the map is populated with.
    * @return an immutable {@link MapList} containing the specified mappings.
    */

   @SafeVarargs
   static <K, V> MapSet<K, V> ofEntries(Map.Entry<K, Set<V>>... entries) {

      if (entries == null) {
         throw new NullPointerException("MapList::ofEntries, parameter \"entries\" is null");
      }

      @SuppressWarnings("unchecked")
      Map.Entry<K, Set<V>>[] newEntries = new Map.Entry[entries.length];

      var i = 0;
      for (var entry : entries) {
         var key = entry.getKey();
         var value = entry.getValue();

         newEntries[i++] = Map.entry(key, Set.copyOf(value));
      }

      var map = Map.ofEntries(newEntries);

      var mapSet = new AbstractImmutableMapSet<K, V>(map);

      return mapSet;
   }

}

/* EOF */
