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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Map of keys to multiple values. Collections of values are stored in the Map. The type of Collection can be
 * specified at construction, if desired. All Collections returned by methods are backed by the , so changes to the are
 * reflected in the Collection, and vice-versa. However, modifications to the Collection outside of this class are
 * generally discouraged because removal of the last item would then not guarantee removal of the key.
 *
 * @author Donald G. Dunne
 */
@SuppressWarnings("rawtypes")
public class HashCollection<K, V> {

   private boolean isSynchronized;
   private Class<? extends Collection> collectionType;

   private Map<K, Collection<V>> map;
   public static final Class<? extends Collection> DEFAULT_COLLECTION_TYPE = ArrayList.class;

   /********************************************************************************************************************
    * Constructors
    *******************************************************************************************************************/

   /**
    * @param isSynchronized - If true, the Map & Collection will both be synchronized using the
    * Collections.synchronizedMap & Collections.synchronizedCollection. otherwise, this class will not be synchronzied
    * and therefore not threadsafe.
    * @param collectionType The type of collection to use to as the values within the HashMap.
    * @see HashMap#HashMap(int, float)
    */
   public HashCollection(boolean isSynchronized, Class<? extends Collection> collectionType, int initialCapacity, float loadFactor) {

      if (isSynchronized) {
         map = new ConcurrentHashMap<>(initialCapacity, loadFactor);
      } else {
         map = new HashMap<>(initialCapacity, loadFactor);
      }

      this.isSynchronized = isSynchronized;
      this.collectionType = collectionType;
   }

   /**
    * @param isSynchronized - If true, the Map & Collection will both be synchronized using the
    * Collections.synchronizedMap & Collections.synchronizedCollection. otherwise, this class will not be synchronzied
    * and therefore not threadsafe.
    * @param collectionType - The type of collection to use to as the values within the HashMap.
    * @see HashMap#HashMap(int)
    */
   public HashCollection(boolean isSynchronized, Class<? extends Collection> collectionType, int initialCapacity) {
      if (isSynchronized) {
         map = new ConcurrentHashMap<>(initialCapacity);
      } else {
         map = new HashMap<>(initialCapacity);
      }

      this.isSynchronized = isSynchronized;
      this.collectionType = collectionType;
   }

   /**
    * @param isSynchronized - If true, the Map & Collection will both be synchronized using the
    * Collections.synchronizedMap & Collections.synchronizedCollection. otherwise, this class will not be synchronzied
    * and therefore not threadsafe.
    * @param collectionType - The type of collection to use to as the values within the HashMap.
    * @see HashMap#HashMap()
    */
   public HashCollection(boolean isSynchronized, Class<? extends Collection> collectionType) {
      if (isSynchronized) {
         map = new ConcurrentHashMap<>();
      } else {
         map = new HashMap<>();
      }
      this.isSynchronized = isSynchronized;
      this.collectionType = collectionType;
   }

   /**
    * Creates an unsynchronized Plus using a default Collection type (ArrayList)
    *
    * @see HashMap#HashMap(int, float)
    */
   public HashCollection(int initialCapacity, float loadFactor) {
      this(false, DEFAULT_COLLECTION_TYPE, initialCapacity, loadFactor);
   }

   /**
    * Creates an unsynchronized Plus using a default Collection type (ArrayList)
    *
    * @see HashMap#HashMap(int)
    */
   public HashCollection(int initialCapacity) {
      this(false, DEFAULT_COLLECTION_TYPE, initialCapacity);
   }

   public HashCollection(boolean isSynchronized) {
      this(isSynchronized, DEFAULT_COLLECTION_TYPE);
   }

   public HashCollection() {
      this(false, DEFAULT_COLLECTION_TYPE, 0);
   }

   /********************************************************************************************************************
    * Methods
    *******************************************************************************************************************/

   /**
    * Adds the value to the collection specified by the key. If there is not a collection for the given key, a new
    * collection is created and added to the hash.
    *
    * @param key The key whose collection we will add value to.
    * @param value The value to be added.
    * @return the collection containing value and all other items associated with the key.
    */
   @SuppressWarnings("unchecked")
   public Collection<V> put(K key, V value) {
      Collection<V> collection = map.get(key);
      if (collection == null) {
         try {
            if (isSynchronized) {
               collection = Collections.synchronizedCollection(collectionType.newInstance());
            } else {
               collection = collectionType.newInstance();
            }
            map.put(key, collection);
         } catch (InstantiationException ex) {
            ex.printStackTrace();
            return null;
         } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
         }
      }
      collection.add(value);
      return collection;
   }

   /**
    * Adds all of the items in the Collection values to the collection for the specified key.
    *
    * @param key The key to add the values to
    * @param values The values to be added. Null or empty values will insert empty list in map
    * @return The collection for the key, containing all values.
    */
   public Collection<V> put(K key, Collection<V> values) {
      Collection<V> items = null;
      if (values == null || values.isEmpty()) {
         if (!map.containsKey(key)) {
            map.put(key, new LinkedList<>());
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

   /**
    * @param key The key whose collection we will remove value from.
    * @param value The value to be removed
    * @return true iff the value was removed from the collection for key.
    */
   public boolean removeValue(K key, V value) {
      Collection<V> collection = map.get(key);
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

   public Collection<V> removeValues(K key) {
      Collection<V> objectPair = map.remove(key);
      if (objectPair == null) {
         return Collections.emptyList();
      }
      return objectPair;
   }

   /**
    * Returns the Collection of items for this key, or null if the key does not exist.
    *
    * @return Return value collection reference
    */
   public Collection<V> getValues(K key) {
      return map.get(key);
   }

   /**
    * Returns the Collection all items
    *
    * @return Return value collection reference
    */
   public List<V> getValues() {
      List<V> values = new ArrayList<>();
      for (Collection<V> objectPair : map.values()) {
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

   public Set<Entry<K, Collection<V>>> entrySet() {
      return map.entrySet();
   }

   /**
    * The total number of key-value combinations
    */
   public int size() {
      int size = 0;
      Set<K> keySet = keySet();

      synchronized (map) {
         for (K key : keySet) {
            size += getValues(key).size();
         }
      }
      return size;
   }

   /**
    * @return whether the map contains this value
    */
   public boolean containsValue(Object value) {
      for (Collection<V> collection : map.values()) {
         if (collection != null) {
            for (V tempValue : collection) {
               if (value.equals(tempValue)) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   @Override
   public String toString() {
      return map.toString();
   }
}
