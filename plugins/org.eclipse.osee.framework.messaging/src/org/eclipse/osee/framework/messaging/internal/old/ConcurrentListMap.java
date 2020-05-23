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

package org.eclipse.osee.framework.messaging.internal.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Andrew M. Finkbeiner
 */
public class ConcurrentListMap<MAP_TYPE, LIST_TYPE> {

   private final Map<MAP_TYPE, List<LIST_TYPE>> data;
   private final List<LIST_TYPE> EMPTY_LIST = new ArrayList<>();

   public ConcurrentListMap() {
      data = new HashMap<>();
   }

   public synchronized boolean add(MAP_TYPE key, LIST_TYPE value) {
      List<LIST_TYPE> values = data.get(key);
      if (values == null) {
         values = new CopyOnWriteArrayList<>();
         data.put(key, values);
      }
      if (values.contains(value)) {
         return false;
      } else {
         values.add(value);
         return true;
      }
   }

   public synchronized List<LIST_TYPE> get(MAP_TYPE key) {
      List<LIST_TYPE> values = data.get(key);
      if (values == null) {
         return EMPTY_LIST;
      } else {
         return values;
      }
   }

   public synchronized boolean remove(MAP_TYPE key, LIST_TYPE value) {
      List<LIST_TYPE> values = data.get(key);
      if (values == null) {
         return false;
      } else {
         return values.remove(value);
      }
   }

   public void clear() {
      data.clear();
   }
}
