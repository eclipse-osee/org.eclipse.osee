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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TxMonitorImpl.MonitoredTx;
import org.eclipse.osee.framework.skynet.core.transaction.TxMonitorImpl.TxState;

/**
 * @author Roberto E. Escobar
 */
public abstract class TransactionOperation<K> extends AbstractOperation implements MonitoredTx {

   private final TxMonitor<K> txMonitor;
   private final K key;
   private final String uuid;
   private volatile TxState state;

   protected TransactionOperation(TxMonitor<K> txMonitor, K key, String description) {
      super(description, Activator.PLUGIN_ID);
      this.txMonitor = txMonitor;
      this.key = key;
      this.uuid = GUID.create();
      setTxState(TxState.CREATED);
   }

   protected abstract void clear();

   protected abstract void txWork(IProgressMonitor monitor) throws Exception;

   protected TxMonitor<K> getTxMonitor() {
      return txMonitor;
   }

   @Override
   public String getUuid() {
      return uuid;
   }

   protected K getKey() {
      return key;
   }

   protected void setTxState(TxState state) {
      this.state = state;
   }

   @Override
   public TxState getTxState() {
      return state;
   }

   protected void ensureCanBeAdded(Object object)  {
      txMonitor.checkForComodification(getKey(), this, object);
   }

   @Override
   public abstract boolean containsItem(Object object);

   @Override
   public void rollback()  {
      // Do Nothing
   }

   @Override
   protected final void doWork(IProgressMonitor monitor) throws Exception {
      synchronized (txMonitor) {
         try {
            txMonitor.beginTx(getKey(), this);
            if (getTxState() == TxState.MODIFIED) {
               setTxState(TxState.RUNNING);
               txWork(SubMonitor.convert(monitor));
            }
            setTxState(TxState.ENDED);
         } catch (Exception ex) {
            setTxState(TxState.ENDED_WITH_EXCEPTION);
            txMonitor.rollbackTx(getKey(), this);
            throw ex;
         } finally {
            try {
               txMonitor.endTx(getKey(), this);
            } finally {
               clear();
            }
         }
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (uuid == null ? 0 : uuid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      MonitoredTx other = (MonitoredTx) obj;
      if (uuid == null) {
         if (other.getUuid() != null) {
            return false;
         }
      } else if (!uuid.equals(other.getUuid())) {
         return false;
      }
      return true;
   }
}
