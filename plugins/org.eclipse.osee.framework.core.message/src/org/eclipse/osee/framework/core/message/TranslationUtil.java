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
package org.eclipse.osee.framework.core.message;

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
      //Utility Class
   }

   public static void loadArrayMap(Map<Long, String[]> map, PropertyStore store, Enum<?> key) {
      storeToArrayMap(map, store.getPropertyStore(key.name()));
   }

   public static void loadMap(Map<Long, Integer> map, PropertyStore store, Enum<?> key) {
      storeToMap(map, store.getPropertyStore(key.name()));
   }

   public static void loadMapLong(Map<Long, Long> map, PropertyStore store, Enum<?> key) {
      storeToMapLong(map, store.getPropertyStore(key.name()));
   }

   public static void putMap(PropertyStore store, Enum<?> key, Map<Long, Integer> map) {
      store.put(key.name(), mapToStore(map));
   }

   public static void putMapLong(PropertyStore store, Enum<?> key, Map<Long, Long> map) {
      store.put(key.name(), mapToStoreLong(map));
   }

   public static void putArrayMap(PropertyStore store, Enum<?> key, Map<Long, String[]> map) {
      store.put(key.name(), arrayMapToStore(map));
   }

   public static void loadTripletList(List<Triplet<Long, Long, Long>> data, PropertyStore store, Enum<?> key) {
      storeToStringTripletList(data, store.getPropertyStore(key.name()));
   }

   public static void loadTripletLongList(List<Triplet<Long, Long, Long>> data, PropertyStore store, Enum<?> key) {
      storeToTripletList(data, store.getPropertyStore(key.name()));
   }

   public static void putTripletList(PropertyStore store, Enum<?> key, List<Triplet<Long, Long, Long>> list) {
      store.put(key.name(), tripletListToStore(list));
   }

   public static void putTripletLongList(PropertyStore store, Enum<?> key, List<Triplet<Long, Long, Long>> list) {
      store.put(key.name(), tripletLongListToStore(list));
   }

   private static PropertyStore arrayMapToStore(Map<Long, String[]> map) {
      PropertyStore innerStore = new PropertyStore();
      for (Entry<Long, String[]> entry : map.entrySet()) {
         innerStore.put(String.valueOf(entry.getKey()), entry.getValue());
      }
      return innerStore;
   }

   private static PropertyStore longArrayMapToStore(Map<Long, Long[]> map) {
      PropertyStore innerStore = new PropertyStore();
      for (Entry<Long, Long[]> entry : map.entrySet()) {
         Long[] values = entry.getValue();
         String[] data = new String[values.length];
         for (int index = 0; index < values.length; index++) {
            data[index] = String.valueOf(values[index]);
         }
         innerStore.put(String.valueOf(entry.getKey()), data);
      }
      return innerStore;
   }

   private static PropertyStore mapToStore(Map<Long, Integer> map) {
      PropertyStore innerStore = new PropertyStore();
      for (Entry<Long, Integer> entry : map.entrySet()) {
         innerStore.put(String.valueOf(entry.getKey()), entry.getValue());
      }
      return innerStore;
   }

   private static PropertyStore mapToStoreLong(Map<Long, Long> map) {
      PropertyStore innerStore = new PropertyStore();
      for (Entry<Long, Long> entry : map.entrySet()) {
         innerStore.put(String.valueOf(entry.getKey()), entry.getValue());
      }
      return innerStore;
   }

   private static void storeToMap(Map<Long, Integer> map, PropertyStore innerStore) {
      for (String strkey : innerStore.keySet()) {
         Long key = Long.valueOf(strkey);
         Integer value = innerStore.getInt(strkey);
         map.put(key, value);
      }
   }

   private static void storeToMapLong(Map<Long, Long> map, PropertyStore innerStore) {
      for (String strkey : innerStore.keySet()) {
         Long key = Long.valueOf(strkey);
         Long value = innerStore.getLong(strkey);
         map.put(key, value);
      }
   }

   private static void storeToArrayMap(Map<Long, String[]> map, PropertyStore innerStore) {
      for (String strkey : innerStore.arrayKeySet()) {
         Long key = Long.valueOf(strkey);
         String[] value = innerStore.getArray(strkey);
         map.put(key, value);
      }
   }

   private static void storeToLongArrayMap(Map<Long, Long[]> map, PropertyStore innerStore) {
      for (String strkey : innerStore.arrayKeySet()) {
         Long key = Long.valueOf(strkey);
         String[] value = innerStore.getArray(strkey);
         Long[] intValues = new Long[value.length];
         for (int index = 0; index < value.length; index++) {
            intValues[index] = Long.valueOf(value[index]);
         }
         map.put(key, intValues);
      }
   }

   private static void storeToTripletList(List<Triplet<Long, Long, Long>> data, PropertyStore innerStore) {
      for (String strKey : innerStore.arrayKeySet()) {
         String[] value = innerStore.getArray(strKey);
         data.add(new Triplet<Long, Long, Long>(Long.valueOf(value[0]), Long.valueOf(value[1]), Long.valueOf(value[2])));
      }
   }

   private static void storeToStringTripletList(List<Triplet<Long, Long, Long>> data, PropertyStore innerStore) {
      for (String strKey : innerStore.arrayKeySet()) {
         String[] value = innerStore.getArray(strKey);
         data.add(new Triplet<Long, Long, Long>(Long.valueOf(value[0]), Long.valueOf(value[1]), Long.valueOf(value[2])));
      }
   }

   private static PropertyStore tripletListToStore(List<Triplet<Long, Long, Long>> list) {
      PropertyStore innerStore = new PropertyStore();
      int index = 0;
      for (Triplet<Long, Long, Long> entry : list) {
         innerStore.put(
            String.valueOf(index),
            new String[] {
               String.valueOf(entry.getFirst()),
               String.valueOf(entry.getSecond()),
               String.valueOf(entry.getThird())});
         index++;
      }
      return innerStore;
   }

   private static PropertyStore tripletLongListToStore(List<Triplet<Long, Long, Long>> list) {
      PropertyStore innerStore = new PropertyStore();
      int index = 0;
      for (Triplet<Long, Long, Long> entry : list) {
         innerStore.put(
            String.valueOf(index),
            new String[] {
               String.valueOf(entry.getFirst()),
               String.valueOf(entry.getSecond()),
               String.valueOf(entry.getThird())});
         index++;
      }
      return innerStore;
   }

   public static void loadLongArrayMap(Map<Long, Long[]> map, PropertyStore store, Enum<?> key) {
      storeToLongArrayMap(map, store.getPropertyStore(key.name()));
   }

   public static void putLongArrayMap(PropertyStore store, Enum<?> key, Map<Long, Long[]> map) {
      store.put(key.name(), longArrayMapToStore(map));
   }

   public static String createKey(Enum<?> prefix, int index) {
      return prefix.name() + "_" + index;
   }

}
