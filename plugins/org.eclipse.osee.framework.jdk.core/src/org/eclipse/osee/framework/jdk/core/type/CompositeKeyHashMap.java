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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A hash map implementation that uses two objects to form a single composite key. The thread safety of this class is
 * determined by the isThreadSafe of its constructors.
 *
 * @author Ken J. Aguilar
 */
public class CompositeKeyHashMap<KeyOne, KeyTwo, Value> implements Map<Pair<KeyOne, KeyTwo>, Value> {
   private final HashCollectionSet<KeyOne, KeyTwo> singleKeyMap;
   private final Map<Pair<KeyOne, KeyTwo>, Value> map;

   private final ThreadLocal<Pair<KeyOne, KeyTwo>> threadLocalKey = new ThreadLocal<Pair<KeyOne, KeyTwo>>() {

      @Override
      protected Pair<KeyOne, KeyTwo> initialValue() {
         return new Pair<KeyOne, KeyTwo>(null, null);
      }

   };

   public CompositeKeyHashMap() {
      this(50, false);
   }

   public CompositeKeyHashMap(int initialCapacity, boolean isThreadSafe) {
      if (isThreadSafe) {
         map = new ConcurrentHashMap<>(initialCapacity);
      } else {
         map = new HashMap<>(initialCapacity);
      }
      singleKeyMap = new HashCollectionSet<>(isThreadSafe, HashSet::new);
   }

   @Override
   public void clear() {
      map.clear();
      singleKeyMap.clear();
   }

   /**
    * Use this method to determine if any puts(keyOne, anything) have occurred
    *
    * @return whether the map contains the key keyOne
    */
   @Override
   @SuppressWarnings("unchecked")
   public boolean containsKey(Object key1) {
      return singleKeyMap.containsKey((KeyOne) key1);
   }

   /**
    * @return whether the map contains the compound key <keyOne, keyTwo>
    */
   public boolean containsKey(KeyOne key1, KeyTwo key2) {
      return map.containsKey(threadLocalKey.get().set(key1, key2));
   }

   /**
    * determines if at least one of the compound keys are mapped to this value
    *
    * @return whether the map contains this value
    */
   @Override
   public boolean containsValue(Object value) {
      return map.containsValue(value);
   }

   @Override
   public Set<Map.Entry<Pair<KeyOne, KeyTwo>, Value>> entrySet() {
      return map.entrySet();
   }

   @SuppressWarnings("unchecked")
   @Override
   public Value get(Object key) {
      List<Value> values = getValues((KeyOne) key);
      if (!values.isEmpty()) {
         return values.iterator().next();
      }
      return null;
   }

   public Map<KeyTwo, Value> getKeyedValues(KeyOne key1) {
      int size = singleKeyMap.sizeByKey(key1);
      if (size == 0) {
         return Collections.emptyMap();
      }

      Map<KeyTwo, Value> values = new HashMap<>(size);
      singleKeyMap.forEachValue(key1, key2 -> values.put(key2, get(key1, key2)));

      return values;
   }

   public List<Value> getValues(KeyOne key1) {
      int size = singleKeyMap.sizeByKey(key1);
      if (size == 0) {
         return Collections.emptyList();
      }
      ArrayList<Value> values = new ArrayList<>(size);
      singleKeyMap.forEachValue(key1, key2 -> values.add(get(key1, key2)));
      return values;
   }

   public Value get(KeyOne key1, KeyTwo key2) {
      return map.get(threadLocalKey.get().set(key1, key2));
   }

   public List<Pair<KeyOne, KeyTwo>> getEnumeratedKeys() {
      List<Pair<KeyOne, KeyTwo>> toReturn = new ArrayList<>();
      for (KeyOne firstKey : singleKeyMap.keySet()) {
         singleKeyMap.forEachValue(firstKey, secondKey -> toReturn.add(new Pair<KeyOne, KeyTwo>(firstKey, secondKey)));
      }
      return toReturn;
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   @Override
   public Set<Pair<KeyOne, KeyTwo>> keySet() {
      return map.keySet();
   }

   @Override
   public Value put(Pair<KeyOne, KeyTwo> key, Value value) {
      put(key.getFirst(), key.getSecond());
      return map.put(key, value);
   }

   public Value put(KeyOne key1, KeyTwo key2, Value value) {
      put(key1, key2);
      return map.put(new Pair<KeyOne, KeyTwo>(key1, key2), value);
   }

   @Override
   public void putAll(Map<? extends Pair<KeyOne, KeyTwo>, ? extends Value> copyMap) {
      map.putAll(copyMap);

      for (Pair<KeyOne, KeyTwo> key : copyMap.keySet()) {
         put(key.getFirst(), key.getSecond());
      }
   }

   private void put(KeyOne key1, KeyTwo key2) {
      if (singleKeyMap.isSynchronized()) {
         synchronized (singleKeyMap) {
            singleKeyMap.put(key1, key2);
         }
      } else {
         singleKeyMap.put(key1, key2);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public Value remove(Object key) {
      Collection<Value> values = removeValues((KeyOne) key);
      if (values != null && !values.isEmpty()) {
         return values.iterator().next();
      }
      return null;
   }

   /**
    * @return the previous value associated with key, or null if there was no mapping for key.
    */
   public Collection<Value> removeValues(KeyOne key1) {
      int size = singleKeyMap.sizeByKey(key1);
      if (size == 0) {
         return null;
      }
      ArrayList<Value> values = new ArrayList<>(size);
      singleKeyMap.forEachValue(key1, key2 -> values.add(map.remove(threadLocalKey.get().set(key1, key2))));
      singleKeyMap.removeValues(key1);
      return values;
   }

   public Value removeAndGet(KeyOne key1, KeyTwo key2) {
      Value value = map.remove(threadLocalKey.get().set(key1, key2));
      singleKeyMap.removeValue(key1, key2);
      return value;
   }

   @Override
   public int size() {
      return map.size();
   }

   @Override
   public Collection<Value> values() {
      return map.values();
   }
}