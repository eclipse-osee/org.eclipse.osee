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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.data.TransactionReadable;

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

   public CancellableCallable<Integer> purgeTransactions(OrcsSession session, final Collection<? extends TransactionId> transactions) {
      return new AbstractTxCallable<Integer>("PurgeTransactions", session) {
         @Override
         protected Integer innerCall() throws Exception {
            return txDataStore.purgeTransactions(getSession(), transactions).call();
         }
      };
   }

   public Callable<Void> setTransactionComment(OrcsSession session, final TransactionId transaction, final String comment) {
      return new AbstractTxCallable<Void>("SetTxComment", session) {
         @Override
         protected Void innerCall() throws Exception {
            return txDataStore.setTransactionComment(getSession(), transaction, comment).call();
         }
      };
   }

   public CancellableCallable<TransactionReadable> createTx(final TxData txData) {
      return new AbstractTxCallable<TransactionReadable>("CommitTransaction", txData.getSession()) {

         @Override
         protected TransactionReadable innerCall() throws Exception {
            TransactionReadable transaction = null;
            try {
               txManager.startTx(txData);
               TransactionResult result = doCommit();
               txManager.txCommitSuccess(txData);
               if (result != null) {
                  transaction = result.getTransaction();
               }
            } catch (Exception ex) {
               Exception toThrow = ex;
               try {
                  txManager.rollbackTx(txData);
               } catch (Exception ex2) {
                  toThrow = new OseeCoreException("Exception during rollback and commit", ex);
               } finally {
                  OseeCoreException.wrapAndThrow(toThrow);
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

   public void setTransactionCommitArtifact(OrcsSession session, TransactionId trans, ArtifactToken commitArt) {
      txDataStore.setTransactionCommitArtifact(session, trans, commitArt);
   }

}
