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
package org.eclipse.osee.framework.messaging.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Andrew M. Finkbeiner
 */
public class ConcurrentListMap<MAP_TYPE, LIST_TYPE> {

   private Map<MAP_TYPE, List<LIST_TYPE>> data;
   private List<LIST_TYPE> EMPTY_LIST = new ArrayList<LIST_TYPE>();

   public ConcurrentListMap() {
      data = new HashMap<MAP_TYPE, List<LIST_TYPE>>();
   }

   public synchronized boolean add(MAP_TYPE key, LIST_TYPE value) {
      List<LIST_TYPE> values = data.get(key);
      if (values == null) {
         values = new CopyOnWriteArrayList<LIST_TYPE>();
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
