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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ryan D. Brooks
 */
public class DoubleKeyHashMap<KeyOne, KeyTwo, Value> {

   private final HashMap<KeyOne, HashMap<KeyTwo, Value>> k1ToHashMap;

   public DoubleKeyHashMap() {
      k1ToHashMap = new HashMap<>();
   }

   public Map<KeyOne, KeyTwo> keySet() {
      Map<KeyOne, KeyTwo> keySet = new HashMap<>();
      for (KeyOne one : k1ToHashMap.keySet()) {
         for (KeyTwo two : k1ToHashMap.get(one).keySet()) {
            keySet.put(one, two);
         }
      }
      return keySet;

   }

   public Collection<Value> get(KeyOne k1) {
      HashMap<KeyTwo, Value> o = k1ToHashMap.get(k1);
      if (o == null) {
         return null;
      }
      return o.values();
   }

   public Value get(KeyOne k1, KeyTwo k2) {
      HashMap<KeyTwo, Value> o = k1ToHashMap.get(k1);
      if (o != null) {
         return o.get(k2);
      }
      return null;
   }

   public Value put(KeyOne k1, KeyTwo k2, Value v) {
      Value returnV = null;
      HashMap<KeyTwo, Value> o = k1ToHashMap.get(k1);
      if (o != null) {
         returnV = o.put(k2, v);
      } else {
         o = new HashMap<>(20);
         returnV = o.put(k2, v);
         k1ToHashMap.put(k1, o);
      }
      return returnV;
   }

   public Value remove(KeyOne k1, KeyTwo k2) {
      Value value = null;
      HashMap<KeyTwo, Value> o = k1ToHashMap.get(k1);
      if (o != null) {
         value = o.remove(k2);
         if (o.isEmpty()) {
            k1ToHashMap.remove(k1);
         }
      }
      return value;
   }

   @Override
   public String toString() {
      return k1ToHashMap.toString();
   }

   public int size() {
      return k1ToHashMap.size();
   }

   /**
    * The collection provided by this method is not backed by this DoubleKeyHashMap, and thusly any modifications to
    * Collection will not modify the map, and future modifications to the map will not modify the Collection.
    * 
    * @return Return value collection
    */
   public Collection<Value> allValues() {
      Collection<Value> values = new HashSet<>();
      for (HashMap<KeyTwo, Value> map : k1ToHashMap.values()) {
         values.addAll(map.values());
      }
      return values;
   }

   public Collection<Value> allValues(KeyOne key) {
      HashMap<KeyTwo, Value> map = k1ToHashMap.get(key);
      if (map != null) {
         return new HashSet<Value>(map.values());
      }
      return new HashSet<Value>();
   }

   public Map<KeyTwo, Value> getSubHash(KeyOne k1) {
      return k1ToHashMap.get(k1);
   }

   public boolean containsKey(KeyOne k1, KeyTwo k2) {
      return k1ToHashMap.containsKey(k1) && k1ToHashMap.get(k1).containsKey(k2);
   }

   public void clear() {
      k1ToHashMap.clear();
   }

   public boolean isEmpty() {
      return k1ToHashMap.isEmpty();
   }

   public Set<KeyOne> getKeySetOne() {
      return k1ToHashMap.keySet();
   }

   public Collection<? extends Map<KeyTwo, Value>> getInnerMaps() {
      return k1ToHashMap.values();
   }
}
