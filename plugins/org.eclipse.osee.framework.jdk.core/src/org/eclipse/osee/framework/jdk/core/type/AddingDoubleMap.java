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
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Donald G. Dunne
 */
public class AddingDoubleMap<K> {
   private final HashMap<K, MutableDouble> addingMap;

   public AddingDoubleMap(int initialCapacity) {
      addingMap = new HashMap<K, MutableDouble>(initialCapacity);
   }

   public AddingDoubleMap() {
      addingMap = new HashMap<K, MutableDouble>();
   }

   public double get(K key) {
      for (Entry<K, MutableDouble> entry : getCounts()) {
         if (entry.getKey().equals(key)) return entry.getValue().getValue();
      }
      return 0;
   }

   public boolean contains(K key) {
      for (Entry<K, MutableDouble> entry : getCounts()) {
         if (entry.getKey().equals(key)) return true;
      }
      return false;
   }

   public void put(K key, double byAmmount) {
      MutableDouble count = addingMap.get(key);
      if (count == null) {
         addingMap.put(key, new MutableDouble(byAmmount));
      } else {
         count.getValueAndInc(byAmmount);
      }
   }

   public void put(Collection<K> keys, double byAmmount) {
      for (K key : keys) {
         put(key, byAmmount);
      }
   }

   public Set<Entry<K, MutableDouble>> getCounts() {
      return addingMap.entrySet();
   }
}
