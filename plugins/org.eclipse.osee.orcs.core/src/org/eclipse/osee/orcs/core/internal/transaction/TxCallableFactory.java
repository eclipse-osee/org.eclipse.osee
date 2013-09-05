/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class TxCallableFactory {

   private final Log logger;
   private final TxDataStore txDataStore;
   private final TxDataManager txManager;

   public TxCallableFactory(Log logger, TxDataStore txDataStore, TxDataManager txManager) {
      super();
      this.logger = logger;
      this.txDataStore = txDataStore;
      this.txManager = txManager;
   }

   public CancellableCallable<String> createUnsubscribeTx(OrcsSession session, final ArtifactReadable userArtifact, final ArtifactReadable groupArtifact) {
      return new AbstractTxCallable<String>("UnsubscribeTx", session) {
         @Override
         protected String innerCall() throws Exception {
            return txDataStore.createUnsubscribeTx(userArtifact, groupArtifact).call();
         }
      };
   }

   public CancellableCallable<Integer> purgeTransactions(OrcsSession session, final Collection<? extends ITransaction> transactions) {
      return new AbstractTxCallable<Integer>("PurgeTransactions", session) {
         @Override
         protected Integer innerCall() throws Exception {
            return txDataStore.purgeTransactions(getSession(), transactions).call();
         }
      };
   }

   public CancellableCallable<TransactionRecord> createTx(final TxData txData) {
      return new AbstractTxCallable<TransactionRecord>("CommitTransaction", txData.getSession()) {

         @Override
         protected TransactionRecord innerCall() throws Exception {
            TransactionRecord transaction = null;
            try {
               txManager.startTx(txData);
               TransactionResult result = doCommit();
               txManager.txCommitSuccess(txData);
               transaction = result.getTransaction();
            } catch (Exception ex) {
               Exception toThrow = ex;
               try {
                  txManager.rollbackTx(txData);
               } catch (Exception ex2) {
                  toThrow = new OseeCoreException("Exception during rollback and commit", ex);
               } finally {
                  OseeExceptions.wrapAndThrow(toThrow);
               }
            } finally {
               txManager.endTx(txData);
            }
            return transaction;
         }

         private TransactionResult doCommit() throws Exception {
            TransactionData changes = txManager.createChangeData(txData);
            Callable<TransactionResult> callable = txDataStore.commitTransaction(getSession(), changes);
            return callable.call();
         }
      };
   }

   private abstract class AbstractTxCallable<T> extends CancellableCallable<T> {

      private final String opName;
      private final OrcsSession session;

      public AbstractTxCallable(String opName, OrcsSession session) {
         super();
         this.opName = opName;
         this.session = session;
      }

      protected OrcsSession getSession() {
         return session;
      }

      @Override
      public final T call() throws Exception {
         long startTime = System.currentTimeMillis();
         long endTime = startTime;
         T result = null;
         try {
            if (logger.isTraceEnabled()) {
               logger.trace("%s [start] ", opName);
            }
            result = innerCall();
         } finally {
            endTime = System.currentTimeMillis() - startTime;
         }
         if (logger.isTraceEnabled()) {
            logger.trace("%s [%s] - completed", opName, Lib.asTimeString(endTime));
         }
         return result;
      }

      protected abstract T innerCall() throws Exception;

   }

}
