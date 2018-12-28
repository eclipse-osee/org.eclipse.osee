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
public class CompositeKeyTripleHashMap<KeyOne, KeyTwo, KeyThree, Value> implements Map<Triplet<KeyOne, KeyTwo, KeyThree>, Value> {

   private final Map<Triplet<KeyOne, KeyTwo, KeyThree>, Value> map;

   private final ThreadLocal<Triplet<KeyOne, KeyTwo, KeyThree>> threadLocalKey =
      new ThreadLocal<Triplet<KeyOne, KeyTwo, KeyThree>>() {

         @Override
         protected Triplet<KeyOne, KeyTwo, KeyThree> initialValue() {
            return new Triplet<>(null, null, null);
         }

      };

   public CompositeKeyTripleHashMap() {
      map = new HashMap<>();
   }

   public CompositeKeyTripleHashMap(Map<Triplet<KeyOne, KeyTwo, KeyThree>, Value> map) {
      this.map = map;
   }

   public CompositeKeyTripleHashMap(int initialCapacity) {
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

   public boolean containsKey(KeyOne a, KeyTwo b, KeyThree c) {
      return map.containsKey(threadLocalKey.get().set(a, b, c));
   }

   @Override
   public boolean containsValue(Object value) {
      return map.containsValue(value);
   }

   @Override
   public Set<Map.Entry<Triplet<KeyOne, KeyTwo, KeyThree>, Value>> entrySet() {
      return map.entrySet();
   }

   @Override
   public Value get(Object key) {
      if (Triplet.class.isInstance(key)) {
         return map.get(key);
      } else {
         throw new IllegalArgumentException(
            String.format("Expected Type [CompositeKey], got type [%s].", key.getClass().getName()));
      }
   }

   public Value get(KeyOne a, KeyTwo b, KeyThree c) {
      return map.get(threadLocalKey.get().set(a, b, c));
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   @Override
   public Set<Triplet<KeyOne, KeyTwo, KeyThree>> keySet() {
      return map.keySet();
   }

   @Override
   public Value put(Triplet<KeyOne, KeyTwo, KeyThree> key, Value value) {
      return map.put(key, value);
   }

   public Value put(KeyOne a, KeyTwo b, KeyThree c, Value value) {
      return map.put(new Triplet<>(a, b, c), value);
   }

   @Override
   public void putAll(Map<? extends Triplet<KeyOne, KeyTwo, KeyThree>, ? extends Value> m) {
      map.putAll(m);
   }

   @Override
   public Value remove(Object key) {
      return map.remove(key);
   }

   public Value remove(KeyOne a, KeyTwo b, KeyThree c) {
      return map.remove(threadLocalKey.get().set(a, b, c));
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