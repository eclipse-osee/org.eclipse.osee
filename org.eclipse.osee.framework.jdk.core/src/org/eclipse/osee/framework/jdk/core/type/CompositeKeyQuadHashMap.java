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
 * A hash map implementation that uses composite keys. This class is not thread safe.
 * 
 * @author Ken J. Aguilar
 * @param <KeyOne>
 * @param <KeyTwo>
 * @param <Value>
 */
public class CompositeKeyQuadHashMap<KeyOne, KeyTwo, KeyThree, KeyFour, Value> implements Map<CompositeKeyQuadHashMap.CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>, Value> {

   private final Map<CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>, Value> map;

   private final ThreadLocal<CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>> threadLocalKey =
         new ThreadLocal<CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>>() {

            @Override
            protected CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour> initialValue() {
               return new CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>();
            }

         };

   public static final class CompositeKey<A, B, C, D> {
      private A key1;
      private B key2;
      private C key3;
      private D key4;

      public CompositeKey() {

      }

      public CompositeKey(A key1, B key2, C key3, D key4) {
         setKeys(key1, key2, key3, key4);
      }

      public A getKey1() {
         return key1;
      }

      public B getKey2() {
         return key2;
      }

      public C getKey3() {
         return key3;
      }

      public D getKey4() {
         return key4;
      }

      public CompositeKey<A, B, C, D> setKeys(A key1, B key2, C key3, D key4) {
         this.key1 = key1;
         this.key2 = key2;
         this.key3 = key3;
         this.key4 = key4;
         return this;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof CompositeKey) {
            final CompositeKey<A, B, C, D> otherKey = (CompositeKey<A, B, C, D>) obj;
            return otherKey.key1.equals(key1) && otherKey.key2.equals(key2) && otherKey.key3.equals(key3) && otherKey.key4.equals(key4);
         }
         return false;
      }

      @Override
      public int hashCode() {
         int hashCode = 11;
         hashCode = hashCode * 31 + key1.hashCode();
         hashCode = hashCode * 31 + key2.hashCode();
         hashCode = hashCode * 31 + key3.hashCode();
         hashCode = hashCode * 31 + key4.hashCode();
         return hashCode;
      }

   }

   public CompositeKeyQuadHashMap() {
      map = new HashMap<CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>, Value>();
   }

   public CompositeKeyQuadHashMap(Map<CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>, Value> map) {
      this.map = map;
   }

   public CompositeKeyQuadHashMap(int initialCapacity) {
      map = new HashMap<CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>, Value>(initialCapacity);
   }

   public void clear() {
      map.clear();
   }

   public boolean containsKey(Object key) {
      return map.containsKey(key);
   }

   public boolean containsKey(KeyOne a, KeyTwo b, KeyThree c, KeyFour d) {
      return map.containsKey(threadLocalKey.get().setKeys(a, b, c, d));
   }

   public boolean containsValue(Object value) {
      return map.containsValue(value);
   }

   public Set<Map.Entry<CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>, Value>> entrySet() {
      return map.entrySet();
   }

   public Value get(Object key) {
      if (CompositeKey.class.isInstance(key)) {
         return map.get(key);
      } else {
         throw new IllegalArgumentException(String.format("Expected Type [CompositeKey], got type [%s].",
               key.getClass().getName()));
      }
   }

   public Value get(KeyOne a, KeyTwo b, KeyThree c, KeyFour d) {
      return map.get(threadLocalKey.get().setKeys(a, b, c, d));
   }

   public boolean isEmpty() {
      return map.isEmpty();
   }

   public Set<CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>> keySet() {
      return map.keySet();
   }

   public Value put(CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour> key, Value value) {
      return map.put(key, value);
   }

   public Value put(KeyOne a, KeyTwo b, KeyThree c, KeyFour d, Value value) {
      return map.put(new CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>(a, b, c, d), value);
   }

   public void putAll(Map<? extends CompositeKey<KeyOne, KeyTwo, KeyThree, KeyFour>, ? extends Value> m) {
      map.putAll(m);
   }

   public Value remove(Object key) {
      return map.remove(key);
   }

   public Value remove(KeyOne a, KeyTwo b, KeyThree c, KeyFour d) {
      return map.remove(threadLocalKey.get().setKeys(a, b, c, d));
   }

   public int size() {
      return map.size();
   }

   public Collection<Value> values() {
      return map.values();
   }
}