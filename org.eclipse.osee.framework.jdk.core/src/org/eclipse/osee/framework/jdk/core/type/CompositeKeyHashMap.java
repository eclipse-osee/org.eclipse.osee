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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A hash map implementation that uses composite keys
 * 
 * @author Ken J. Aguilar
 * @param <KeyOne>
 * @param <KeyTwo>
 * @param <Value>
 */
public class CompositeKeyHashMap<KeyOne, KeyTwo, Value> implements Map<CompositeKeyHashMap.CompositeKey<KeyOne, KeyTwo>, Value> {

   private final Map<CompositeKey<KeyOne, KeyTwo>, Value> map;

   public static final class CompositeKey<A, B> {
      private final A key1;
      private final B key2;
      private final int hashcode;

      public CompositeKey(A key1, B key2) {
         super();
         this.key1 = key1;
         this.key2 = key2;
         hashcode = key1.hashCode() ^ key2.hashCode();
      }

      public A getKey1() {
         return key1;
      }

      public B getKey2() {
         return key2;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof CompositeKey) {
            final CompositeKey<A, B> otherKey = (CompositeKey<A, B>) obj;
            return otherKey.key1.equals(key1) && otherKey.key2.equals(key2);
         }
         return false;
      }

      @Override
      public int hashCode() {
         return hashcode;
      }

   }

   public CompositeKeyHashMap() {
      map = new HashMap<CompositeKey<KeyOne, KeyTwo>, Value>();
   }

   public CompositeKeyHashMap(Map<CompositeKey<KeyOne, KeyTwo>, Value> map) {
      this.map = map;
   }

   public CompositeKeyHashMap(int count) {
      map = new HashMap<CompositeKey<KeyOne, KeyTwo>, Value>(count);
   }

   public void clear() {
      map.clear();
   }

   public boolean containsKey(Object key) {
      return map.containsKey(key);
   }

   public boolean containsKey(KeyOne a, KeyTwo b) {
      return map.containsKey(new CompositeKey<KeyOne, KeyTwo>(a, b));
   }

   public boolean containsValue(Object value) {
      return map.containsValue(value);
   }

   public Set<Map.Entry<CompositeKey<KeyOne, KeyTwo>, Value>> entrySet() {
      return map.entrySet();
   }

   public Value get(Object key) {
      return map.get(key);
   }

   public Value get(KeyOne a, KeyTwo b) {
      return map.get(new CompositeKey<KeyOne, KeyTwo>(a, b));
   }

   public boolean isEmpty() {
      return map.isEmpty();
   }

   public Set<CompositeKey<KeyOne, KeyTwo>> keySet() {
      return map.keySet();
   }

   public Value put(CompositeKey<KeyOne, KeyTwo> key, Value value) {
      return map.put(key, value);
   }

   public Value put(KeyOne a, KeyTwo b, Value value) {
      return map.put(new CompositeKey<KeyOne, KeyTwo>(a, b), value);
   }

   public void putAll(Map<? extends CompositeKey<KeyOne, KeyTwo>, ? extends Value> m) {
      map.putAll(m);
   }

   public Value remove(Object key) {
      return map.remove(key);
   }

   public Value remove(KeyOne a, KeyTwo b) {
      return map.remove(new CompositeKey<KeyOne, KeyTwo>(a, b));
   }

   public int size() {
      return map.size();
   }

   public Collection<Value> values() {
      return map.values();
   }

}
