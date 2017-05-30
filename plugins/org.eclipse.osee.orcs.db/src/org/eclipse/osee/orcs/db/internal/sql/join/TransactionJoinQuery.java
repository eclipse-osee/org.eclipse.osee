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
package org.eclipse.osee.orcs.db.internal.sql.join;

import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.jdbc.SQL3DataType;

/**
 * @author Roberto E. Escobar
 */
public final class TransactionJoinQuery extends AbstractJoinQuery {

   private final class TempTransactionEntry implements IJoinRow {
      private final Long gammaId;
      private final Integer transactionId;
      private final Long branchUuid;

      private TempTransactionEntry(Long gammaId, Integer transactionId, Long branchUuid) {
         this.gammaId = gammaId;
         this.transactionId = transactionId;
         this.branchUuid = branchUuid;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {
            getQueryId(),
            gammaId,
            transactionId,
            branchUuid != null ? branchUuid : SQL3DataType.BIGINT};
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + (branchUuid == null ? 0 : branchUuid.hashCode());
         result = prime * result + (gammaId == null ? 0 : gammaId.hashCode());
         result = prime * result + (transactionId == null ? 0 : transactionId.hashCode());
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
         if (branchUuid == null) {
            if (other.branchUuid != null) {
               return false;
            }
         } else if (!branchUuid.equals(other.branchUuid)) {
            return false;
         }
         if (gammaId == null) {
            if (other.gammaId != null) {
               return false;
            }
         } else if (!gammaId.equals(other.gammaId)) {
            return false;
         }
         if (transactionId == null) {
            if (other.transactionId != null) {
               return false;
            }
         } else if (!transactionId.equals(other.transactionId)) {
            return false;
         }
         return true;
      }

      @Override
      public String toString() {
         return String.format("gamma_id=%s, tx_id=%s, branch_id=%s", gammaId, transactionId, branchUuid);
      }

      private TransactionJoinQuery getOuterType() {
         return TransactionJoinQuery.this;
      }
   }

   protected TransactionJoinQuery(IJoinAccessor joinAccessor, Long expiresIn, int queryId) {
      super(joinAccessor, JoinItem.TRANSACTION, expiresIn, queryId);
   }

   public void add(Long gammaId, Integer transactionId) {
      add(gammaId, transactionId, null);
   }

   public void add(Long gammaId, Integer transactionId, Long branchUuid) {
      entries.add(new TempTransactionEntry(gammaId, transactionId, branchUuid));
   }
}