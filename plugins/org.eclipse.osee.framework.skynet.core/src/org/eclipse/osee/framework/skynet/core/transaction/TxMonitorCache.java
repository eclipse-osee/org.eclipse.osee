/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.transaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.skynet.core.transaction.TxMonitorImpl.MonitoredTx;

/**
 * @author Roberto E. Escobar
 */
public final class TxMonitorCache<K> {

   private final Map<K, Map<String, WeakReference<MonitoredTx>>> txMap =
      new ConcurrentHashMap<>();

   public boolean contains(K key) {
      return txMap.containsKey(key);
   }

   public boolean contains(K key, String uuid) {
      return getTx(key, uuid) != null;
   }

   public void putTx(K key, MonitoredTx tx) {
      Map<String, WeakReference<MonitoredTx>> data = txMap.get(key);
      if (data == null) {
         data = new ConcurrentHashMap<>();
         txMap.put(key, data);
      }
      data.put(tx.getUuid(), new WeakReference<>(tx));
      manageReferences();
   }

   public void removeTx(K key, String uuid) {
      Map<String, WeakReference<MonitoredTx>> data = txMap.get(key);
      if (data != null) {
         data.remove(uuid);
      }
      manageReferences();
   }

   private void manageReferences() {
      Set<K> toRemove = new HashSet<>();
      for (Entry<K, Map<String, WeakReference<MonitoredTx>>> entry : txMap.entrySet()) {
         Map<?, WeakReference<MonitoredTx>> data = entry.getValue();
         manageReferences(data);
         if (data.isEmpty()) {
            toRemove.add(entry.getKey());
         }
      }
      for (Object item : toRemove) {
         txMap.remove(item);
      }
   }

   private void manageReferences(Map<?, WeakReference<MonitoredTx>> data) {
      Set<Object> toRemove = new HashSet<>();
      for (Entry<?, WeakReference<MonitoredTx>> entry : data.entrySet()) {
         WeakReference<?> reference = entry.getValue();
         if (reference == null || reference.get() == null) {
            toRemove.add(entry.getKey());
         }
      }
      for (Object item : toRemove) {
         data.remove(item);
      }
   }

   public MonitoredTx getTx(K key, String uuid) {
      MonitoredTx toReturn = null;
      Map<String, WeakReference<MonitoredTx>> data = txMap.get(key);
      if (data != null) {
         WeakReference<MonitoredTx> reference = data.get(uuid);
         if (reference != null) {
            toReturn = reference.get();
         }
         if (toReturn == null) {
            data.remove(uuid);
         }
         if (data.isEmpty()) {
            txMap.remove(key);
         }
      }
      return toReturn;
   }

   public List<MonitoredTx> getTxs(K key) {
      List<MonitoredTx> txs = new ArrayList<>();
      Map<String, WeakReference<MonitoredTx>> data = txMap.get(key);
      if (data != null) {
         Set<String> toRemove = new HashSet<>();
         for (Entry<String, WeakReference<MonitoredTx>> entry : data.entrySet()) {
            WeakReference<MonitoredTx> reference = entry.getValue();
            if (reference != null) {
               MonitoredTx tx = reference.get();
               if (tx != null) {
                  txs.add(tx);
               } else {
                  toRemove.add(entry.getKey());
               }
            } else {
               toRemove.add(entry.getKey());
            }
         }
         for (Object item : toRemove) {
            data.remove(item);
         }
         if (data.isEmpty()) {
            txMap.remove(key);
         }
      }
      return txs;
   }

}