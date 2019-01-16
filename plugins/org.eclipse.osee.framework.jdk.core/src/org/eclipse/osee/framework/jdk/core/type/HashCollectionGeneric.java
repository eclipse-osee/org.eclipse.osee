/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A Map of keys to multiple values. Collections of values are stored in the Map. The type of Collection can be
 * specified at construction, if desired. All Collections returned by methods are backed by the , so changes to the are
 * reflected in the Collection, and vice-versa. However, modifications to the Collection outside of this class are
 * generally discouraged because removal of the last item would then not guarantee removal of the key.
 *
 * @author Donald G. Dunne
 */
abstract class HashCollectionGeneric<K, V, C extends Collection<V>> implements Iterable<Map.Entry<K, C>> {
   private final Map<K, C> map;
   private Supplier<C> collectionSupplier;
   private final boolean isSynchronized;

   /**
    * @param isSynchronized - If true, the Map & Collection will both be synchronized using the
    * Collections.synchronizedMap & Collections.synchronizedCollection. otherwise, this class will not be synchronzied
    * and therefore not threadsafe.
    * @param collectionType The type of collection to use to as the values within the HashMap.
    * @see HashMap#HashMap(int, float)
    */
   public HashCollectionGeneric(boolean isSynchronized, int initialCapacity, float loadFactor) {
      this.isSynchronized = isSynchronized;
      if (isSynchronized) {
         map = new ConcurrentHashMap<>(initialCapacity, loadFactor);
      } else {
         map = new HashMap<>(initialCapacity, loadFactor);
      }
   }

   /**
    * Adds the value to the collection specified by the key. If there is not a collection for the given key, a new
    * collection is created and added to the hash.
    *
    * @return the collection containing value and all other items associated with the key.
    */

   public C put(K key, V value) {
      C collection = map.get(key);
      if (collection == null) {
         collection = createAndPutCollection(key);
      }
      collection.add(value);
      return collection;
   }

   private C createAndPutCollection(K key) {
      C collection = collectionSupplier.get();
      map.put(key, collection);
      return collection;
   }

   /**
    * Adds all of the items in the Collection values to the collection for the specified key.
    *
    * @param values The values to be added. Null or empty values will insert empty list in map
    * @return The collection for the key, containing all values.
    */
   public C put(K key, C values) {
      C items = null;
      if (values == null || values.isEmpty()) {
         if (!map.containsKey(key)) {
            createAndPutCollection(key);
         }
      } else {
         for (V value : values) {
            if (items == null) {
               items = this.put(key, value);
            } else {
               items.add(value);
            }
         }
      }
      return items;
   }

   public boolean removeValue(K key, V value) {
      C collection = map.get(key);
      if (collection != null) {
         if (collection.remove(value)) {
            if (collection.isEmpty()) {
               map.remove(key);
            }
            return true;
         }
      }
      return false;
   }

   public C removeValues(K key) {
      return map.remove(key);
   }

   /**
    * Returns the Collection of items for this key, or null if the key does not exist. The returned collection must be
    * synchronized on manually when used concurrently. Alternatively, forEachValue() can be used instead of this method
    * without manual synchronization (because forEachValue performs the synchronization).
    *
    * @return Return value collection reference
    */
   public C getValues(K key) {
      return map.get(key);
   }

   public Collection<V> safeGetValues(K key) {
      Collection<V> values = new ArrayList<>();
      forEachValue(key, values::add);
      return values;
   }

   public void forEachValue(K key, Consumer<V> consumer) {
      C collection = map.get(key);
      if (collection == null) {
         return;
      }
      if (isSynchronized) {
         synchronized (collection) {
            collection.forEach(consumer);
         }
      } else {
         collection.forEach(consumer);
      }
   }

   public void forEachValue(BiConsumer<K, V> consumer) {
      for (K key : map.keySet()) {
         C collection = map.get(key);
         if (collection != null) {
            if (isSynchronized) {
               synchronized (collection) {
                  for (V value : collection) {
                     consumer.accept(key, value);
                  }
               }
            } else {
               for (V value : collection) {
                  consumer.accept(key, value);
               }
            }
         }
      }
   }

   public List<V> getValues() {
      List<V> values = new ArrayList<>();
      for (C objectPair : map.values()) {
         if (objectPair != null) {
            values.addAll(objectPair);
         }
      }
      return values;
   }

   public Set<K> keySet() {
      return map.keySet();
   }

   public void clear() {
      map.clear();
   }

   public boolean containsKey(K key) {
      return map.containsKey(key);
   }

   public boolean isEmpty() {
      return map.isEmpty();
   }

   public Set<Entry<K, C>> entrySet() {
      return map.entrySet();
   }

   /**
    * The total number of key-value combinations
    */
   public int size() {
      int size = 0;

      if (isSynchronized) {
         synchronized (map) {
            for (K key : keySet()) {
               size += getValues(key).size(); //since we are iterating over existing keys, getValues(key) cannot be null
            }
         }
      } else {
         for (K key : keySet()) {
            size += getValues(key).size();
         }
      }
      return size;
   }

   /**
    * The total number of values in one key
    */
   public int sizeByKey(K key) {
      int size = 0;
      if (isSynchronized) {
         synchronized (map) {
            C values = map.get(key);
            if (values != null) {
               size = values.size();
            }
         }
      } else {
         C values = map.get(key);
         if (values != null) {
            size = values.size();
         }
      }
      return size;
   }

   public boolean isSynchronized() {
      return isSynchronized;
   }

   /**
    * @return whether the map contains this value
    */
   public boolean containsValue(Object value) {
      for (Collection<V> collection : map.values()) {
         if (collection.contains(value)) {
            return true;
         }
      }
      return false;
   }

   public void setCollectionSupplier(Supplier<C> collectionSupplier) {
      this.collectionSupplier = collectionSupplier;
   }

   @Override
   public Iterator<Map.Entry<K, C>> iterator() {
      return map.entrySet().iterator();
   }

   @Override
   public String toString() {
      return map.toString();
   }
}
