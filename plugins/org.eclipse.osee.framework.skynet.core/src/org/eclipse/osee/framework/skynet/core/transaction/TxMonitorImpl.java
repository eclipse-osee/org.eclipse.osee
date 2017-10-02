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

import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * Cases:
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Roberto E. Escobar
 */
public final class TxMonitorImpl<K> implements TxMonitor<K> {

   public static enum TxState {
      CREATED,
      MODIFIED,
      RUNNING,
      ENDED,
      ENDED_WITH_EXCEPTION;
   }

   public static interface MonitoredTx {

      String getUuid();

      TxState getTxState();

      boolean containsItem(Object object);

      void rollback() ;
   }

   private final TxMonitorCache<K> cache;

   public TxMonitorImpl(TxMonitorCache<K> cache) {
      this.cache = cache;
   }

   @Override
   public void checkForComodification(K key, MonitoredTx tx, Object object)  {
      for (MonitoredTx otherTx : cache.getTxs(key)) {
         if (!otherTx.equals(tx)) {
            if (otherTx.containsItem(object)) {
               throw new OseeStateException("Co-modification error for object [%s] -\ntx1:[%s]\ntx2:[%s]", object, tx,
                  otherTx);
            }
         }
      }
   }

   @Override
   public void createTx(K key, MonitoredTx tx)  {
      if (cache.contains(key, tx.getUuid())) {
         throw new OseeStateException("key:[%s] tx:[%s] - transaction was already in the cache", key, tx);
      }
      if (tx.getTxState() != TxState.CREATED) {
         throw new OseeStateException("key:[%s] tx:[%s] state:[%s] - was not in CREATED state ", key, tx,
            tx.getTxState());
      }
      cache.putTx(key, tx);
   }

   @Override
   public void beginTx(K key, MonitoredTx tx)  {
      checkIsInCache(key, tx);

      TxState state = tx.getTxState();
      if (state != TxState.CREATED && state != TxState.MODIFIED) {
         throw new OseeStateException("key:[%s] tx:[%s] state:[%s] - was not in CREATED or MODIFIED_STATE state", key,
            tx, state);
      }
   }

   @Override
   public void endTx(K key, MonitoredTx tx)  {
      checkIsInCache(key, tx);

      TxState state = tx.getTxState();
      if (state != TxState.ENDED && state != TxState.ENDED_WITH_EXCEPTION) {
         throw new OseeStateException("key:[%s] tx:[%s] state:[%s] - was not in ENDED or ENDED_WITH_EXCEPTION state",
            key, tx, state);
      }
      cache.removeTx(key, tx.getUuid());
   }

   @Override
   public void rollbackTx(K key, MonitoredTx tx)  {
      checkIsInCache(key, tx);
      tx.rollback();
   }

   private void checkIsInCache(K key, MonitoredTx tx)  {
      if (!cache.contains(key, tx.getUuid())) {
         throw new OseeStateException("key:[%s] tx:[%s] - has not been added to monitor", key, tx);
      }
   }

   @Override
   public void cancel(K key, MonitoredTx tx) {
      cache.removeTx(key, tx.getUuid());
   }

}
