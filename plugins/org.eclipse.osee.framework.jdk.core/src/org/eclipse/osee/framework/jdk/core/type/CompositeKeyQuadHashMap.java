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
 */
public class CompositeKeyQuadHashMap<KeyOne, KeyTwo, KeyThree, KeyFour, Value> implements Map<Quad<KeyOne, KeyTwo, KeyThree, KeyFour>, Value> {

   private final Map<Quad<KeyOne, KeyTwo, KeyThree, KeyFour>, Value> map;

   private final ThreadLocal<Quad<KeyOne, KeyTwo, KeyThree, KeyFour>> threadLocalKey =
      new ThreadLocal<Quad<KeyOne, KeyTwo, KeyThree, KeyFour>>() {

         @Override
         protected Quad<KeyOne, KeyTwo, KeyThree, KeyFour> initialValue() {
            return new Quad<>(null, null, null, null);
         }

      };

   public CompositeKeyQuadHashMap() {
      map = new HashMap<>();
   }

   public CompositeKeyQuadHashMap(Map<Quad<KeyOne, KeyTwo, KeyThree, KeyFour>, Value> map) {
      this.map = map;
   }

   public CompositeKeyQuadHashMap(int initialCapacity) {
      map = new HashMap<>(initialCapacity);
   }

   @Override
   public void clear() {
      map.clear();
   }

   @Override
   public boolean containsKey(Object key) {
      return map.containsKey(key);
   }

   public boolean containsKey(KeyOne a, KeyTwo b, KeyThree c, KeyFour d) {
      return map.containsKey(threadLocalKey.get().set(a, b, c, d));
   }

   @Override
   public boolean containsValue(Object value) {
      return map.containsValue(value);
   }

   @Override
   public Set<Map.Entry<Quad<KeyOne, KeyTwo, KeyThree, KeyFour>, Value>> entrySet() {
      return map.entrySet();
   }

   @Override
   public Value get(Object key) {
      if (Quad.class.isInstance(key)) {
         return map.get(key);
      } else {
         throw new IllegalArgumentException(
            String.format("Expected Type [CompositeKey], got type [%s].", key.getClass().getName()));
      }
   }

   public Value get(KeyOne a, KeyTwo b, KeyThree c, KeyFour d) {
      return map.get(threadLocalKey.get().set(a, b, c, d));
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   @Override
   public Set<Quad<KeyOne, KeyTwo, KeyThree, KeyFour>> keySet() {
      return map.keySet();
   }

   @Override
   public Value put(Quad<KeyOne, KeyTwo, KeyThree, KeyFour> key, Value value) {
      return map.put(key, value);
   }

   public Value put(KeyOne a, KeyTwo b, KeyThree c, KeyFour d, Value value) {
      return map.put(new Quad<>(a, b, c, d), value);
   }

   @Override
   public void putAll(Map<? extends Quad<KeyOne, KeyTwo, KeyThree, KeyFour>, ? extends Value> m) {
      map.putAll(m);
   }

   @Override
   public Value remove(Object key) {
      return map.remove(key);
   }

   public Value remove(KeyOne a, KeyTwo b, KeyThree c, KeyFour d) {
      return map.remove(threadLocalKey.get().set(a, b, c, d));
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