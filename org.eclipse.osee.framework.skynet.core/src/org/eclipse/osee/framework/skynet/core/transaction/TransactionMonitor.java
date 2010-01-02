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
package org.eclipse.osee.framework.skynet.core.transaction;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class TransactionMonitor {

   private String lastComment;
   private enum TxState {
      CREATED, RUNNING, ENDED;
   }

   private final Map<Object, TxOperation> txMap;

   public TransactionMonitor() {
      this.txMap = new WeakHashMap<Object, TxOperation>();
   }

   public synchronized void reportTxCreation(final DbTransaction transaction, Object key, String comment) {
      TxOperation currentTx = txMap.get(key);
      if (currentTx != null) {
         // This log is to support debugging the case where osee transactions are nested and should
         // use the same transaction.
         // This case may happens legitimately if an exception occurs outside this API before transaction.execute() is called,
         // so it is only notification that this is occurring.
         OseeLog.log(Activator.class, Level.SEVERE, String.format(
               "New transaction [%s] created over Last transaction [%s]", comment, lastComment), currentTx.getError());
      }
      lastComment = comment;
      txMap.put(key, new TxOperation(transaction));
   }

   public synchronized void reportTxStart(final DbTransaction transaction, Object key) throws OseeWrappedException, OseeStateException {
      TxOperation currentTx = txMap.get(key);
      if (currentTx == null) {
         throw new OseeStateException(
               "reportTxStart called for key: " + key + " but reportTxCreation had not been called.");
      } else if (currentTx.getState() != TxState.CREATED) {
         throw new OseeWrappedException(currentTx.getError());
      }

      if (currentTx.getTransaction().equals(transaction)) {
         currentTx.setState(TxState.RUNNING);
      } else {
         throw new OseeStateException(
               "reportTxStart called for key: " + key + " but was called for incorrect transaction");
      }
   }

   public synchronized void reportTxEnd(final DbTransaction transaction, Object key) throws OseeWrappedException, OseeStateException {
      TxOperation currentTx = txMap.get(key);
      if (currentTx == null) {
         throw new OseeStateException(
               "reportTxEnd called for key: " + key + " but reportTxCreation had not been called.");
      } else if (currentTx.getState() != TxState.RUNNING) {
         // This is a valid case -- can add a log to detect when a reportTxEnd is called before a transaction has a chance to run 
      }

      if (currentTx.getTransaction().equals(transaction)) {
         txMap.put(key, null);
      } else {
         throw new OseeWrappedException(currentTx.getError());
      }
   }

   private static final class TxOperation {
      private final DbTransaction tx;
      private final Throwable throwable;
      private TxState txState;

      public TxOperation(DbTransaction tx) {
         this.tx = tx;
         this.txState = TxState.CREATED;
         // Not null for stack trace purposes;
         this.throwable = new Exception();
      }

      public DbTransaction getTransaction() {
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
