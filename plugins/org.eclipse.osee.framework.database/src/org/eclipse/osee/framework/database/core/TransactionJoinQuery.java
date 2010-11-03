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
package org.eclipse.osee.framework.database.core;

import org.eclipse.osee.framework.database.core.DatabaseJoinAccessor.JoinItem;

/**
 * @author Roberto E. Escobar
 */
public final class TransactionJoinQuery extends AbstractJoinQuery {

   private final class TempTransactionEntry implements IJoinRow {
      private final long gammaId;
      private final int transactionId;

      private TempTransactionEntry(Long gammaId, Integer transactionId) {
         this.gammaId = gammaId;
         this.transactionId = transactionId;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), getInsertTime(), gammaId, transactionId};
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + (int) (gammaId ^ (gammaId >>> 32));
         result = prime * result + transactionId;
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
         TempTransactionEntry other = (TempTransactionEntry) obj;
         if (!getOuterType().equals(other.getOuterType())) {
            return false;
         }
         if (gammaId != other.gammaId) {
            return false;
         }
         if (transactionId != other.transactionId) {
            return false;
         }
         return true;
      }

      @Override
      public String toString() {
         return String.format("gamma_id=%s, tx_id=%s", gammaId, transactionId);
      }

      private TransactionJoinQuery getOuterType() {
         return TransactionJoinQuery.this;
      }
   }

   protected TransactionJoinQuery(IJoinAccessor joinAccessor, int queryId) {
      super(joinAccessor, JoinItem.TRANSACTION, queryId);
   }

   public void add(Long gammaId, Integer transactionId) {
      entries.add(new TempTransactionEntry(gammaId, transactionId));
   }
}