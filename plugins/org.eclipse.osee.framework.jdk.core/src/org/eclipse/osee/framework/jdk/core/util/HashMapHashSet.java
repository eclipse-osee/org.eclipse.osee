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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of the {@link MapCollection} interface using a {@link HashMap} for the primary {@link Map} and
 * {@link HashSet} for the {@link Collection} implementation.
 *
 * @author Loren K. Ashley
 * @param <K> the type of keys used by the map.
 * @param <V> the type of value saved in the {@link HashSet} collections associated with the map keys.
 */

public class HashMapHashSet<K, V> extends AbstractMapCollection<K, V, Set<V>> implements MapSet<K, V> {

   /**
    * Creates the primary {@link HashMap} with a default capacity and load factor. New {@link HashSet} collections
    * created by this object are also created with the default capacity and load factor for {@link HashSet} objects.
    */

   public HashMapHashSet() {
      super(HashMap::new, HashSet::new);
   }

   /**
    * Creates the primary {@link HashMap} with an initial capacity of <code>mapInitialCapacity</code> and the default
    * load factor. New {@link HashSet} collections created by this object are created with an initial capacity of
    * <code>collectionInitialCapacity</code> and the default load factor for {@link HashSet} objects.
    *
    * @param mapInitialCapacity the initial capacity of the primary {@link HashMap}.
    * @param collectionInitialCapacity the initial capacity of the {@link HashSet} collections created by this object.
    */

   public HashMapHashSet(int mapInitialCapacity, int collectionInitialCapacity) {
      super(() -> new HashMap<>(mapInitialCapacity), () -> new HashSet<>(collectionInitialCapacity));
   }

   /**
    * Creates the primary {@link HashMap} with an initial capacity of <code>mapInitialCapacity</code> and a load factor
    * of <code>mapLoadFactory</code>. New {@link HashSet} collections created by this object are created with an initial
    * capacity of <code>collectionInitialCapacity</code> and a load factor of <code>collectionLoadFactor</code>.
    *
    * @param mapInitialCapacity the initial capacity of the primary {@link HashMap}.
    * @param mapLoadFactor the load factor for the primary {@link HashMap}.
    * @param collectionInitialCapacity the initial capacity of the {@link HashSet} collections created by this object.
    * @param collectionLoadFactor the load factor for the {@link HashSet} collections created by this object.
    */

   public HashMapHashSet(int mapInitialCapacity, float mapLoadFactor, int collectionInitialCapacity, float collectionLoadFactor) {
      super(() -> new HashMap<>(mapInitialCapacity, mapLoadFactor),
         () -> new HashSet<>(collectionInitialCapacity, collectionLoadFactor));
   }

   /**
    * Creates the primary {@link HashMap} with an initial capacity that matches the number of collections in the
    * provided <code>mapCollection</code> and the default load factor. New {@link HashSet} collections created by this
    * object are created with the default initial capacity and load factor for {@link HashSet} objects. A new
    * {@link HashSet} is created and associated with each key from the <code>mapCollection</code> and stored in the
    * primary {@link HashMap}. The values in each collection in <code>mapCollection</code> are copied to the
    * corresponding collection in this object. So the collections in this object are independent from the collections in
    * <code>mapCollection</code>. Changes to collections in <code>mapCollection</code> will not be reflected in this
    * object.
    *
    * @param mapCollection the {@link MapCollection} to copy the key and value references from.
    */

   public HashMapHashSet(MapCollection<K, V, Set<V>> mapCollection) {
      super(() -> new HashMap<>(mapCollection.size()), HashSet::new);
      //@formatter:off
      mapCollection
         .streamEntries()
         .forEach( mapCollection::putEntry );
      //@formatter:on
   }

}

/* EOF */
