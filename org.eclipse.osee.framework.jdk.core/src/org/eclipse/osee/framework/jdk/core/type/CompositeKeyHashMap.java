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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A hash map implementation that uses composite keys. This class is not thread safe.
 * 
 * @author Ken J. Aguilar
 * @param <KeyOne>
 * @param <KeyTwo>
 * @param <Value>
 */
public class CompositeKeyHashMap<KeyOne, KeyTwo, Value> implements Map<CompositeKey<KeyOne, KeyTwo>, Value> {
   private final HashCollection<KeyOne, KeyTwo> signleKeyMap = new HashCollection<KeyOne, KeyTwo>();
   private final Map<CompositeKey<KeyOne, KeyTwo>, Value> map;

   private final ThreadLocal<CompositeKey<KeyOne, KeyTwo>> threadLocalKey =
         new ThreadLocal<CompositeKey<KeyOne, KeyTwo>>() {

            @Override
            protected CompositeKey<KeyOne, KeyTwo> initialValue() {
               return new CompositeKey<KeyOne, KeyTwo>();
            }

         };

   public CompositeKeyHashMap() {
      this(50);
   }

   public CompositeKeyHashMap(Map<CompositeKey<KeyOne, KeyTwo>, Value> map) {
      this.map = map;
   }

   public CompositeKeyHashMap(int initialCapacity) {
      map = new HashMap<CompositeKey<KeyOne, KeyTwo>, Value>(initialCapacity);
   }

   public void clear() {
      map.clear();
      signleKeyMap.clear();
   }

   /* (non-Javadoc)
    * @see java.util.Map#containsKey(java.lang.Object)
    */
   /**
    * Use this method to determine if any puts(keyOne, anything) have occurred
    * 
    * @param keyOne
    * @return whether the map contains the key keyOne
    */
   public boolean containsKey(Object key1) {
      return signleKeyMap.containsKey((KeyOne) key1);
   }

   /**
    * @param key1
    * @param key2
    * @return whether the map contains the compound key <keyOne, keyTwo>
    */
   public boolean containsKey(KeyOne key1, KeyTwo key2) {
      return map.containsKey(threadLocalKey.get().setKeys(key1, key2));
   }

   /**
    * determines if at least one of the compound keys are mapped to this value
    * 
    * @param value
    * @return whether the map contains this value
    */
   public boolean containsValue(Object value) {
      return signleKeyMap.containsValue(value);
   }

   public Set<Map.Entry<CompositeKey<KeyOne, KeyTwo>, Value>> entrySet() {
      return map.entrySet();
   }

   public Value get(Object key) {
      throw new UnsupportedOperationException("use getValues() instead");
   }

   public List<Value> getValues(KeyOne key1) {
      Collection<KeyTwo> key2s = signleKeyMap.getValues(key1);
      if (key2s == null) {
         return Collections.emptyList();
      }
      ArrayList<Value> values = new ArrayList<Value>(key2s.size());
      for (KeyTwo key2 : key2s) {
         values.add(get(key1, key2));
      }
      return values;
   }

   public Value get(KeyOne key1, KeyTwo key2) {
      return map.get(threadLocalKey.get().setKeys(key1, key2));
   }

   public boolean isEmpty() {
      return map.isEmpty();
   }

   public Set<CompositeKey<KeyOne, KeyTwo>> keySet() {
      return map.keySet();
   }

   public Value put(CompositeKey<KeyOne, KeyTwo> key, Value value) {
      signleKeyMap.put(key.getKey1(), key.getKey2());
      return map.put(key, value);
   }

   public Value put(KeyOne key1, KeyTwo key2, Value value) {
      signleKeyMap.put(key1, key2);
      return map.put(new CompositeKey<KeyOne, KeyTwo>(key1, key2), value);
   }

   public void putAll(Map<? extends CompositeKey<KeyOne, KeyTwo>, ? extends Value> copyMap) {
      map.putAll(copyMap);

      for (CompositeKey<KeyOne, KeyTwo> key : copyMap.keySet()) {
         signleKeyMap.put(key.getKey1(), key.getKey2());
      }
   }

   /* (non-Javadoc)
    * @see java.util.Map#remove(java.lang.Object)
    */
   public Value remove(Object key) {
      throw new UnsupportedOperationException("use removeValues() instead");
   }

   /**
    * @param key1
    * @return the previous value associated with key, or null if there was no mapping for key.
    */
   public Collection<Value> removeValues(KeyOne key1) {
      Collection<KeyTwo> key2s = signleKeyMap.getValues(key1);
      if (key2s == null) {
         return null;
      }
      ArrayList<Value> values = new ArrayList<Value>(key2s.size());
      for (KeyTwo key2 : key2s) {
         values.add(map.remove(threadLocalKey.get().setKeys(key1, key2)));
      }
      signleKeyMap.removeValues(key1);
      return values;
   }

   public Value remove(KeyOne key1, KeyTwo key2) {
      Value value = map.remove(threadLocalKey.get().setKeys(key1, key2));
      signleKeyMap.removeValue(key1, key2);
      return value;
   }

   public int size() {
      return map.size();
   }

   public Collection<Value> values() {
      return map.values();
   }
}