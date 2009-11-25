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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public final class TranslationUtil {

   private TranslationUtil() {
   }

   public static Map<Integer, String[]> getArrayMap(PropertyStore store, Enum<?> key) {
      return storeToArrayMap(store.getPropertyStore(key.name()));
   }

   public static Map<Integer, Integer> getMap(PropertyStore store, Enum<?> key) {
      return storeToMap(store.getPropertyStore(key.name()));
   }

   public static void putMap(PropertyStore store, Enum<?> key, Map<Integer, Integer> map) {
      store.put(key.name(), mapToStore(map));
   }

   public static void putArrayMap(PropertyStore store, Enum<?> key, Map<Integer, String[]> map) {
      store.put(key.name(), arrayMapToStore(map));
   }

   private static PropertyStore arrayMapToStore(Map<Integer, String[]> map) {
      PropertyStore innerStore = new PropertyStore();
      for (Entry<Integer, String[]> entry : map.entrySet()) {
         innerStore.put(String.valueOf(entry.getKey()), entry.getValue());
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

   private static Map<Integer, Integer> storeToMap(PropertyStore innerStore) {
      Map<Integer, Integer> map = new HashMap<Integer, Integer>();
      for (String strkey : innerStore.keySet()) {
         Integer key = Integer.valueOf(strkey);
         Integer value = innerStore.getInt(strkey);
         map.put(key, value);
      }
      return map;
   }

   private static Map<Integer, String[]> storeToArrayMap(PropertyStore innerStore) {
      Map<Integer, String[]> map = new HashMap<Integer, String[]>();
      for (String strkey : innerStore.keySet()) {
         Integer key = Integer.valueOf(strkey);
         String[] value = innerStore.getArray(strkey);
         map.put(key, value);
      }
      return map;
   }

}
