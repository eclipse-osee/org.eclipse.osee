/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Ryan D. Brooks
 */
public class CountingMap<K> {
   private final HashMap<K, MutableInteger> countingMap;

   public CountingMap(int initialCapacity) {
      countingMap = new HashMap<>(initialCapacity);
   }

   public CountingMap() {
      countingMap = new HashMap<>();
   }

   public int get(K key) {
      MutableInteger count = countingMap.get(key);
      if (count == null) {
         return 0;
      }
      return count.getValue();
   }

   public boolean contains(K key) {
      return countingMap.containsKey(key);
   }

   public void put(K key) {
      MutableInteger count = countingMap.get(key);
      if (count == null) {
         countingMap.put(key, new MutableInteger(1));
      } else {
         count.getValueAndInc();
      }
   }

   public void put(K key, int byAmt) {
      MutableInteger count = countingMap.get(key);
      if (count == null) {
         countingMap.put(key, new MutableInteger(byAmt));
      } else {
         count.getValueAndInc(byAmt);
      }
   }

   public void put(Collection<K> keys) {
      for (K key : keys) {
         put(key);
      }
   }

   public Set<Entry<K, MutableInteger>> getCounts() {
      return countingMap.entrySet();
   }

   public void clear() {
      countingMap.clear();
   }

   @Override
   public String toString() {
      return countingMap.toString();
   }
}