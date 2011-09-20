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

import org.eclipse.osee.cluster.Transaction;

/**
 * @author Roberto E. Escobar
 */
public class TransactionProxy implements Transaction {

   private final com.hazelcast.core.Transaction txn;

   /**
    * Isolation is always <tt>READ_COMMITTED</tt> . If you are in a transaction, you can read the data in your
    * transaction and the data that is already committed and if not in a transaction, you can only read the committed
    * data. Implementation is different for queue and map/set. For queue operations (offer,poll), offered and/or polled
    * objects are copied to the next member in order to safely commit/rollback. For map/set, impl first acquires the
    * locks for the write operations (put, remove) and holds the differences (what is added/removed/updated) locally for
    * each transaction. When transaction is set to commit, impl will release the locks and apply the differences. When
    * rolling back, impl will simply releases the locks and discard the differences. Transaction instance is attached to
    * the current thread and each operation checks if the current thread holds a transaction, if so, operation will be
    * transaction aware. When transaction is committed, rolled back or timed out, it will be detached from the thread
    * holding it.
    */
   public TransactionProxy(com.hazelcast.core.Transaction txn) {
      super();
      this.txn = txn;
   }

   @Override
   public void begin() throws IllegalStateException {
      txn.begin();
   }

   @Override
   public void commit() throws IllegalStateException {
      txn.commit();
   }

   @Override
   public void rollback() throws IllegalStateException {
      txn.rollback();
   }

   @Override
   public TransactionStatus getStatus() {
      TransactionStatus toReturn;
      int txStatus = txn.getStatus();
      switch (txStatus) {
         case com.hazelcast.core.Transaction.TXN_STATUS_NO_TXN:
            toReturn = TransactionStatus.NO_TXN;
            break;
         case com.hazelcast.core.Transaction.TXN_STATUS_ACTIVE:
            toReturn = TransactionStatus.ACTIVE;
            break;
         case com.hazelcast.core.Transaction.TXN_STATUS_PREPARED:
            toReturn = TransactionStatus.PREPARED;
            break;
         case com.hazelcast.core.Transaction.TXN_STATUS_COMMITTED:
            toReturn = TransactionStatus.COMMITTED;
            break;
         case com.hazelcast.core.Transaction.TXN_STATUS_ROLLED_BACK:
            toReturn = TransactionStatus.ROLLED_BACK;
            break;
         case com.hazelcast.core.Transaction.TXN_STATUS_PREPARING:
            toReturn = TransactionStatus.PREPARING;
            break;
         case com.hazelcast.core.Transaction.TXN_STATUS_COMMITTING:
            toReturn = TransactionStatus.COMMITTING;
            break;
         case com.hazelcast.core.Transaction.TXN_STATUS_ROLLING_BACK:
            toReturn = TransactionStatus.ROLLING_BACK;
            break;
         default:
            toReturn = TransactionStatus.UNKNOWN;
            break;
      }
      return toReturn;
   }

   protected com.hazelcast.core.Transaction getProxyObject() {
      return txn;
   }
}
