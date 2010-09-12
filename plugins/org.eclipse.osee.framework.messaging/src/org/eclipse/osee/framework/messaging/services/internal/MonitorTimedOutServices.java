/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.messaging.services.ServiceNotification;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

/**
 * @author Andrew M. Finkbeiner
 */
class MonitorTimedOutServices implements Runnable {

   private final CompositeKeyHashMap<String, String, Map<String, ServiceHealthPlusTimeout>> map;
   private final CompositeKeyHashMap<String, String, List<ServiceNotification>> callbacks;

   public MonitorTimedOutServices(CompositeKeyHashMap<String, String, Map<String, ServiceHealthPlusTimeout>> map, CompositeKeyHashMap<String, String, List<ServiceNotification>> callbacks) {
      this.map = map;
      this.callbacks = callbacks;
   }

   @Override
   public void run() {
      List<ThreeItems> toRemove = new ArrayList<ThreeItems>();
      long currentSystemTime = System.currentTimeMillis();
      Set<Pair<String, String>> keySet = map.keySet();
      for (Pair<String, String> pair : keySet) {
         Map<String, ServiceHealthPlusTimeout> items = map.get(pair.getFirst(), pair.getSecond());
         for (Entry<String, ServiceHealthPlusTimeout> key : items.entrySet()) {
            if (key.getValue().isTimedOut(currentSystemTime)) {
               List<ServiceNotification> list = callbacks.get(pair.getFirst(), pair.getSecond());
               if (isServiceReallyGone(list, key.getValue().getServiceHealth())) {
                  toRemove.add(new ThreeItems(pair.getFirst(), pair.getSecond(), key.getKey()));
                  for (ServiceNotification notify : list) {
                     notify.onServiceGone(key.getValue().getServiceHealth());
                  }
               }
            }
         }
      }
      for (ThreeItems item : toRemove) {
         Map<String, ServiceHealthPlusTimeout> innerMap = map.get(item.first, item.second);
         innerMap.remove(item.key);
         System.out.println(item.key);
         if (innerMap.isEmpty()) {
            map.remove(item.first, item.second);
            System.out.println("removed " + item.first + item.second);
         }
      }
   }

   private boolean isServiceReallyGone(List<ServiceNotification> list, ServiceHealth serviceHealth) {
      for (ServiceNotification notify : list) {
         if (!notify.isServiceGone(serviceHealth)) {
            return false;
         }
      }
      return true;
   }

   private static class ThreeItems {

      String first;
      String second;
      String key;

      ThreeItems(String first, String second, String key) {
         this.first = first;
         this.second = second;
         this.key = key;
      }
   }

}
