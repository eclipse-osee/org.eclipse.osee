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
import java.util.Map;
import java.util.Set;

/**
 * A Map of keys to multiple values. Collections of values are stored in the Map. The type of Collection can be
 * specified at construction, if desired. All Collections returned by methods are backed by the HashCollection, so
 * changes to the HashCollection are reflected in the Collection, and vice-versa. However, modifications to the
 * Collection outside of this class are generally discouraged because removal of the last item would then not guarantee
 * removal of the key. The mapping also contains a "plus" object. This object can store additional information about the
 * key. At construction a class of type IPlusProvider must be provided. This provider will generate instances of this
 * "plus" object whenever a new key is added to the Map. When all of the items in the Collection are removed, the entire
 * key is removed from the table - therefore, the "plus" object is only available as long as the Collection for a given
 * key is not empty.
 * 
 * @author David Diepenbrock
 */
public class HashCollectionPlus<K, V, O> {

   private boolean isSynchronized;
   @SuppressWarnings("unchecked")
   private Class<? extends Collection> collectionType;
   private IPlusProvider<O> plusProvider;

   private Map<K, ObjectPair<Collection<V>, O>> map;
   @SuppressWarnings("unchecked")
   public static final Class<? extends Collection> DEFAULT_COLLECTION_TYPE = ArrayList.class;

   /********************************************************************************************************************
    * Constructors
    *******************************************************************************************************************/

   /**
    * @param isSynchronized - If true, the Map & Collection will both be synchronized using the
    *           Collections.synchronizedMap & Collections.synchronizedCollection. otherwise, this class will not be
    *           synchronzied and therefore not threadsafe.
    * @param collectionType The type of collection to use to as the values within the HashMap.
    * @param initialCapacity
    * @param plusProvider
    * @see HashMap#HashMap(int, float)
    */
   @SuppressWarnings("unchecked")
   public HashCollectionPlus(boolean isSynchronized, Class<? extends Collection> collectionType, int initialCapacity, float loadFactor, IPlusProvider<O> plusProvider) {

      if (isSynchronized)
         map = Collections.synchronizedMap(new HashMap<K, ObjectPair<Collection<V>, O>>(initialCapacity, loadFactor));
      else
         map = new HashMap<K, ObjectPair<Collection<V>, O>>(initialCapacity, loadFactor);

      this.isSynchronized = isSynchronized;
      this.collectionType = collectionType;
      this.plusProvider = plusProvider;
   }

   /**
    * @param isSynchronized - If true, the Map & Collection will both be synchronized using the
    *           Collections.synchronizedMap & Collections.synchronizedCollection. otherwise, this class will not be
    *           synchronzied and therefore not threadsafe.
    * @param collectionType - The type of collection to use to as the values within the HashMap.
    * @param initialCapacity
    * @param plusProvider
    * @see HashMap#HashMap(int)
    */
   @SuppressWarnings("unchecked")
   public HashCollectionPlus(boolean isSynchronized, Class<? extends Collection> collectionType, int initialCapacity, IPlusProvider<O> plusProvider) {
      if (isSynchronized)
         map = Collections.synchronizedMap(new HashMap<K, ObjectPair<Collection<V>, O>>(initialCapacity));
      else
         map = new HashMap<K, ObjectPair<Collection<V>, O>>(initialCapacity);

      this.isSynchronized = isSynchronized;
      this.collectionType = collectionType;
      this.plusProvider = plusProvider;
   }

   /**
    * @param isSynchronized - If true, the Map & Collection will both be synchronized using the
    *           Collections.synchronizedMap & Collections.synchronizedCollection. otherwise, this class will not be
    *           synchronzied and therefore not threadsafe.
    * @param collectionType - The type of collection to use to as the values within the HashMap.
    * @param plusProvider
    * @see HashMap#HashMap()
    */
   @SuppressWarnings("unchecked")
   public HashCollectionPlus(boolean isSynchronized, Class<? extends Collection> collectionType, IPlusProvider<O> plusProvider) {
      if (isSynchronized)
         map = Collections.synchronizedMap(new HashMap<K, ObjectPair<Collection<V>, O>>());
      else
         map = new HashMap<K, ObjectPair<Collection<V>, O>>();

      this.isSynchronized = isSynchronized;
      this.collectionType = collectionType;
      this.plusProvider = plusProvider;
   }

   /**
    * Creates an unsynchronized HashCollectionPlus using a default Collection type (ArrayList)
    * 
    * @see HashMap#HashMap(int, float)
    */
   public HashCollectionPlus(int initialCapacity, float loadFactor, IPlusProvider<O> plusProvider) {
      this(false, DEFAULT_COLLECTION_TYPE, initialCapacity, loadFactor, plusProvider);
   }

   /**
    * Creates an unsynchronized HashCollectionPlus using a default Collection type (ArrayList)
    * 
    * @see HashMap#HashMap(int)
    */
   public HashCollectionPlus(int initialCapacity, IPlusProvider<O> plusProvider) {
      this(false, DEFAULT_COLLECTION_TYPE, initialCapacity, plusProvider);
   }

   /**
    * Creates an unsynchronized HashCollectionPlus using a default Collection type (ArrayList)
    * 
    * @see HashMap#HashMap()
    */
   public HashCollectionPlus(IPlusProvider<O> plusProvider) {
      this(false, DEFAULT_COLLECTION_TYPE, plusProvider);
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
      ObjectPair<Collection<V>, O> objectPair = map.get(key);
      if (objectPair == null) {
         try {
            Collection<V> items;
            if (isSynchronized)
               items = Collections.synchronizedCollection(collectionType.newInstance());
            else
               items = collectionType.newInstance();

            objectPair = new ObjectPair(items, plusProvider.newObject());
            map.put(key, objectPair);
         } catch (InstantiationException ex) {
            ex.printStackTrace();
            return null;
         } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
         }
      }
      objectPair.object1.add(value);
      return objectPair.object1;
   }

   /**
    * Adds all of the items in the Collection values to the collection for the specified key.
    * 
    * @param key The key to add the values to
    * @param values The values to be added
    * @return The collection for the key, containing all values.
    */
   public Collection<V> put(K key, Collection<V> values) {
      Collection<V> items = null;

      for (V value : values) {
         if (items == null)
            items = this.put(key, value);
         else
            items.add(value);
      }
      return items;
   }

   /**
    * @param key The key whose collection we will remove value from.
    * @param value The value to be removed
    * @return true iff the value was removed from the collection for key.
    */
   public boolean removeValue(K key, V value) {
      ObjectPair<Collection<V>, O> objectPair = map.get(key);

      if (objectPair != null) {
         Collection<V> items = objectPair.object1;
         if (items != null) {
            if (items.remove(value)) {
               if (items.isEmpty()) map.remove(key);
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Returns the Collection of items for this key, or null if the key does not exist.
    * 
    * @param key
    * @return Return value collection reference
    */
   public Collection<V> getValues(K key) {
      ObjectPair<Collection<V>, O> objectPair = map.get(key);
      if (objectPair != null) return map.get(key).object1;
      return null;
   }

   /**
    * Returns the "plus" object associated with the key, or null if the key does not exist.
    * 
    * @param key
    * @return Return object reference
    */
   public O getPlusObject(K key) {
      ObjectPair<Collection<V>, O> objectPair = map.get(key);
      if (objectPair != null) return map.get(key).object2;
      return null;
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

}
