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

import java.util.Map;
import java.util.WeakHashMap;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;

/**
 * @author Roberto E. Escobar
 */
public final class TransactionMonitor {

   private enum TxState {
      CREATED,
      RUNNING,
      ENDED;
   }

   private final Map<Object, TxOperation> txMap;

   public TransactionMonitor() {
      this.txMap = new WeakHashMap<Object, TxOperation>();
   }

   public synchronized void reportTxCreation(final Object transaction, Object key, String comment) throws OseeCoreException {
      TxOperation op = txMap.get(key);
      if (op == null) {
         txMap.put(key, new TxOperation(transaction));
      } else {
         txMap.put(key, null);
         throw new OseeCoreException("Branch: [%s]: New transaction [%s] created over Last transaction [%s]", key,
            comment, op.getTransaction().toString());
      }
   }

   public synchronized void reportTxStart(final Object transaction, Object key) throws OseeCoreException {
      TxOperation op = txMap.get(key);
      if (op == null) {
         throw new OseeStateException("reportTxStart called for key: [%s] but reportTxCreation had not been called.",
            key);
      } else if (op.getState() != TxState.CREATED) {
         OseeExceptions.wrapAndThrow(op.getError());
      }

      if (op.getTransaction().equals(transaction)) {
         op.setState(TxState.RUNNING);
      } else {
         throw new OseeStateException("reportTxStart called for key [%s] but was called for incorrect transaction", key);
      }
   }

   public synchronized void reportTxEnd(final Object transaction, Object key) throws OseeCoreException {
      TxOperation op = txMap.get(key);
      if (op == null) {
         throw new OseeStateException(
            "reportTxEnd called for key: [%s] but reportTxCreation had not been called. Comment [%s]", key,
            transaction.toString());
      } else if (op.getState() != TxState.RUNNING) {
         // This is a valid case -- can add a log to detect when a reportTxEnd is called before a transaction has a chance to run
      }

      if (op.getTransaction().equals(transaction)) {
         txMap.put(key, null);
      } else {
         OseeExceptions.wrapAndThrow(op.getError());
      }
   }

   private static final class TxOperation {
      private final Object tx;
      private final Throwable throwable;
      private TxState txState;

      public TxOperation(Object tx) {
         this.tx = tx;
         this.txState = TxState.CREATED;
         // Not null for stack trace purposes;
         this.throwable = new Exception();
      }

      public Object getTransaction() {
         return tx;
      }

      public TxState getState() {
         return txState;
      }

      public void setState(TxState txState) {
         this.txState = txState;
      }

      public Throwable getError() {
         return throwable;
      }
   }
}
