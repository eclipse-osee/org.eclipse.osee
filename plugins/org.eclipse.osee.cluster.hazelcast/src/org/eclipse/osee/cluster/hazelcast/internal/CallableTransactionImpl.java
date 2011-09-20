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
package org.eclipse.osee.cluster.hazelcast.internal;

import java.util.concurrent.Callable;
import org.eclipse.osee.cluster.Transaction;
import org.eclipse.osee.cluster.TransactionWork;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class CallableTransactionImpl<T> implements Callable<T> {

   private final Log logger;
   private final Transaction txn;
   private final TransactionWork<T> work;

   public CallableTransactionImpl(Log logger, Transaction txn, TransactionWork<T> work) {
      super();
      this.logger = logger;
      this.txn = txn;
      this.work = work;
   }

   @Override
   public T call() throws Exception {
      Throwable saveThrowable = null;
      T result = null;
      try {
         txn.begin();
         logger.debug("Start Transaction: [%s]", work.getName());
         result = work.doWork();
         txn.commit();
         logger.debug("End Transaction: [%s]", work.getName());
      } catch (Throwable throwable) {
         saveThrowable = throwable;
         try {
            txn.rollback();
         } finally {
            work.handleException(throwable);
         }
      } finally {
         try {
            work.handleTxFinally();
         } catch (Throwable ex) {
            logger.error(ex, "Error in Transaction: [%s] ", work.getName());
            if (saveThrowable != null) {
               throw new Exception(ex);
            }
         }
      }

      if (saveThrowable != null) {
         throw new Exception(saveThrowable);
      }

      return result;
   }

}
