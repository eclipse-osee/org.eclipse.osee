/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.translation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.Triplet;

/**
 * @author Roberto E. Escobar
 */
public final class TranslationUtil {

   private TranslationUtil() {
   }

   public static void loadArrayMap(Map<Integer, String[]> map, PropertyStore store, Enum<?> key) {
      storeToArrayMap(map, store.getPropertyStore(key.name()));
   }

   public static void loadMap(Map<Integer, Integer> map, PropertyStore store, Enum<?> key) {
      storeToMap(map, store.getPropertyStore(key.name()));
   }

   public static void putMap(PropertyStore store, Enum<?> key, Map<Integer, Integer> map) {
      store.put(key.name(), mapToStore(map));
   }

   public static void putArrayMap(PropertyStore store, Enum<?> key, Map<Integer, String[]> map) {
      store.put(key.name(), arrayMapToStore(map));
   }

   public static void loadTripletList(List<Triplet<Integer, Integer, Integer>> data, PropertyStore store, Enum<?> key) {
      storeToTripletList(data, store.getPropertyStore(key.name()));
   }

   public static void putTripletList(PropertyStore store, Enum<?> key, List<Triplet<Integer, Integer, Integer>> list) {
      store.put(key.name(), tripletListToStore(list));
   }

   private static PropertyStore arrayMapToStore(Map<Integer, String[]> map) {
      PropertyStore innerStore = new PropertyStore();
      for (Entry<Integer, String[]> entry : map.entrySet()) {
         innerStore.put(String.valueOf(entry.getKey()), entry.getValue());
      }
      return innerStore;
   }

   private static PropertyStore intArrayMapToStore(Map<Integer, Integer[]> map) {
      PropertyStore innerStore = new PropertyStore();
      for (Entry<Integer, Integer[]> entry : map.entrySet()) {
         Integer[] values = entry.getValue();
         String[] data = new String[values.length];
         for (int index = 0; index < values.length; index++) {
            data[index] = String.valueOf(values[index]);
         }
         innerStore.put(String.valueOf(entry.getKey()), data);
      }
      return innerStore;
   }

   private static PropertyStore mapToStore(Map<Integer, Integer> map) {
      PropertyStore innerStore = new PropertyStore();
      for (Entry<Integer, Integer> entry : map.entrySet()) {
         innerStore.put(String.valueOf(entry.getKey()), entry.getValue());
      }
      return innerStore;
   }

   private static void storeToMap(Map<Integer, Integer> map, PropertyStore innerStore) {
      for (String strkey : innerStore.keySet()) {
         Integer key = Integer.valueOf(strkey);
         Integer value = innerStore.getInt(strkey);
         map.put(key, value);
      }
   }

   private static void storeToArrayMap(Map<Integer, String[]> map, PropertyStore innerStore) {
      for (String strkey : innerStore.arrayKeySet()) {
         Integer key = Integer.valueOf(strkey);
         String[] value = innerStore.getArray(strkey);
         map.put(key, value);
      }
   }

   private static void storeToIntArrayMap(Map<Integer, Integer[]> map, PropertyStore innerStore) {
      for (String strkey : innerStore.arrayKeySet()) {
         Integer key = Integer.valueOf(strkey);
         String[] value = innerStore.getArray(strkey);
         Integer[] intValues = new Integer[value.length];
         for (int index = 0; index < value.length; index++) {
            intValues[index] = Integer.valueOf(value[index]);
         }
         map.put(key, intValues);
      }
   }

   private static void storeToTripletList(List<Triplet<Integer, Integer, Integer>> data, PropertyStore innerStore) {
      for (String strKey : innerStore.arrayKeySet()) {
         String[] value = innerStore.getArray(strKey);
         Integer first = Integer.valueOf(value[0]);
         Integer second = Integer.valueOf(value[1]);
         Integer third = Integer.valueOf(value[2]);
         data.add(new Triplet<Integer, Integer, Integer>(first, second, third));
      }
   }

   private static PropertyStore tripletListToStore(List<Triplet<Integer, Integer, Integer>> list) {
      PropertyStore innerStore = new PropertyStore();
      int index = 0;
      for (Triplet<Integer, Integer, Integer> entry : list) {
         String[] data = new String[3];
         data[0] = String.valueOf(entry.getFirst());
         data[1] = String.valueOf(entry.getSecond());
         data[2] = String.valueOf(entry.getThird());
         innerStore.put(String.valueOf(index), data);
         index++;
      }
      return innerStore;
   }

   public static void loadIntArrayMap(Map<Integer, Integer[]> map, PropertyStore store, Enum<?> key) {
      storeToIntArrayMap(map, store.getPropertyStore(key.name()));
   }

   public static void putIntArrayMap(PropertyStore store, Enum<?> key, Map<Integer, Integer[]> map) {
      store.put(key.name(), intArrayMapToStore(map));
   }

   public static String createKey(Enum<?> prefix, int index) {
      return prefix.name() + "_" + index;
   }
}
