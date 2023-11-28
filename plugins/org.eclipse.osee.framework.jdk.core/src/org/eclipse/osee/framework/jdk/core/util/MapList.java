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

import java.util.List;
import java.util.Map;

/**
 * Interface for an object that maps keys to {@link List} collections of values. The interface extends the interface for
 * {@link Map}<code>&lt;K,{@link List}&lt;V&gt;&gt;</code> with additional methods for directly accessing or adding
 * values to the collections contained within the map.
 *
 * @author Loren K. Ashley
 * @param <K> the map key type.
 * @param <V> the type of value saved in the {@link List} collections associated with the map keys.
 */

public interface MapList<K, V> extends MapCollection<K, V, List<V>> {

   /**
    * Creates an immutable map of immutable lists from the <code>entries</code>. The {@link List} implementations in the
    * <code>entries</code> are copied to new immutable {@link List}s. So the collections in the returned {@link MapList}
    * are independent from the collections provided in <code>entries</code>. Changes to the provided collections will
    * not be reflected in the returned {@link MapList}.
    *
    * @param <K> the map key type.
    * @param <V> the type of value saved in the {@link List} collections associated with the map keys.
    * @param entries {@link Map.Entry}s containing the keys and {@link List} collections the map is populated with.
    * @return an immutable {@link MapList} containing the specified mappings.
    */

   @SafeVarargs
   static <K, V> MapList<K, V> ofEntries(Map.Entry<K, List<V>>... entries) {

      if (entries == null) {
         throw new NullPointerException("MapList::ofEntries, parameter \"entries\" is null");
      }

      @SuppressWarnings("unchecked")
      Map.Entry<K, List<V>>[] newEntries = new Map.Entry[entries.length];

      var i = 0;
      for (var entry : entries) {
         var key = entry.getKey();
         var value = entry.getValue();

         newEntries[i++] = Map.entry(key, List.copyOf(value));
      }

      var map = Map.ofEntries(newEntries);

      var mapList = new AbstractImmutableMapList<K, V>(map);

      return mapList;
   }

}

/* EOF */
