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
 * @author Donald G. Dunne
 */
public class DoubleKeyCountingMap<KeyOne, KeyTwo> {

   HashMap<KeyOne, HashMap<KeyTwo, MutableInteger>> k1ToHashMap;

   public DoubleKeyCountingMap() {
      k1ToHashMap = new HashMap<>();
   }

   public DoubleKeyCountingMap(int initialCapacity) {
      k1ToHashMap = new HashMap<>(initialCapacity);
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

   public Collection<MutableInteger> get(KeyOne k1) {
      HashMap<KeyTwo, MutableInteger> o = k1ToHashMap.get(k1);
      if (o == null) {
         return null;
      }
      return o.values();
   }

   public MutableInteger get(KeyOne k1, KeyTwo k2) {
      HashMap<KeyTwo, MutableInteger> o = k1ToHashMap.get(k1);
      if (o != null) {
         return o.get(k2);
      }
      return null;
   }

   public MutableInteger put(KeyOne k1, KeyTwo k2, int v) {
      MutableInteger returnV = null;
      HashMap<KeyTwo, MutableInteger> o = k1ToHashMap.get(k1);
      if (o != null) {
         returnV = o.put(k2, new MutableInteger(v));
      } else {
         o = new HashMap<>(20);
         returnV = o.put(k2, new MutableInteger(v));
         k1ToHashMap.put(k1, o);
      }
      return returnV;
   }

   public MutableInteger add(KeyOne k1, KeyTwo k2, int v) {
      MutableInteger returnV = null;
      HashMap<KeyTwo, MutableInteger> o = k1ToHashMap.get(k1);
      if (o != null) {
         returnV = o.get(k2);
         if (returnV != null) {
            returnV.getValueAndInc(v);
            o.put(k2, returnV);
         } else {
            o.put(k2, new MutableInteger(v));
         }
      } else {
         o = new HashMap<>(20);
         returnV = o.put(k2, new MutableInteger(v));
         k1ToHashMap.put(k1, o);
      }
      return returnV;
   }

   public MutableInteger remove(KeyOne k1, KeyTwo k2) {
      MutableInteger value = null;
      HashMap<KeyTwo, MutableInteger> o = k1ToHashMap.get(k1);
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

   /**
    * The collection provided by this method is not backed by this DoubleKeyHashMap, and thusly any modifications to
    * Collection will not modify the map, and future modifications to the map will not modify the Collection.
    * 
    * @return Return value collection
    */
   public Collection<MutableInteger> allValues() {
      Collection<MutableInteger> values = new HashSet<>();
      for (HashMap<KeyTwo, MutableInteger> map : k1ToHashMap.values()) {
         values.addAll(map.values());
      }
      return values;
   }

   public Collection<MutableInteger> allValues(KeyOne key) {
      HashMap<KeyTwo, MutableInteger> map = k1ToHashMap.get(key);
      if (map != null) {
         return new HashSet<>(map.values());
      }
      return new HashSet<>();
   }

   public Map<KeyTwo, MutableInteger> getSubHash(KeyOne k1) {
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

   public Collection<? extends Map<KeyTwo, MutableInteger>> getInnerMaps() {
      return k1ToHashMap.values();
   }

   /**
    * Test for DoubleKeyCountingMap
    */
   public static void main(String[] args) {
      DoubleKeyCountingMap<String, String> map = new DoubleKeyCountingMap<>(23);
      map.put("aaa", "now", 4);
      System.out.println("Value should be 4 and is -> " + map.get("aaa", "now"));
      for (int x = 1; x < 3; x++) {
         map.add("aaa", "now", x);
      }
      map.add("bbb", "now", 4);
      map.add("bbb", "now", 1);
      map.add("aaa", "the", 3);
      System.out.println("Value aaa,now should be 7 and is -> " + map.get("aaa", "now"));
      System.out.println("Value bbb,now should be 5 and is -> " + map.get("bbb", "now"));
      System.out.println("Value aaa,the should be 3 and is -> " + map.get("aaa", "the"));

      for (String key1 : map.getKeySetOne()) {
         Map<String, MutableInteger> resolutionToCountMap = map.getSubHash(key1);
         for (String key2 : resolutionToCountMap.keySet()) {
            System.out.println(key1 + "," + key2 + "," + resolutionToCountMap.get(key2).getValue());
         }
      }
   }
}
