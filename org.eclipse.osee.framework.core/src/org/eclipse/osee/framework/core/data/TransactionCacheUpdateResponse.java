/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Roberto E. Escobar
 */
public class TransactionCacheUpdateResponse {

   private final List<TxRow> rows;
   private final Map<Integer, Integer> txToBranchId;

   public TransactionCacheUpdateResponse(List<TxRow> rows, Map<Integer, Integer> txToBranchId) {
      this.rows = rows;
      this.txToBranchId = txToBranchId;
   }

   public List<TxRow> getTxRows() {
      return rows;
   }

   public Map<Integer, Integer> getTxToBranchId() {
      return txToBranchId;
   }

   public static final class TxRow {
      private final int txId;
      private final TransactionDetailsType txType;
      private final String comment;
      private final Date timeStamp;
      private final int authorArtId;
      private final int commitArtId;

      protected TxRow(int txId, TransactionDetailsType txType, String comment, Date timeStamp, int authorArtId, int commitArtId) {
         super();
         this.txId = txId;
         this.txType = txType;
         this.comment = comment;
         this.timeStamp = timeStamp;
         this.authorArtId = authorArtId;
         this.commitArtId = commitArtId;
      }

      public int getId() {
         return txId;
      }

      public TransactionDetailsType getTxType() {
         return txType;
      }

      public String getComment() {
         return comment;
      }

      public Date getTimeStamp() {
         return timeStamp;
      }

      public int getAuthorArtId() {
         return authorArtId;
      }

      public int getCommitArtId() {
         return commitArtId;
      }

      public String[] toArray() {
         return new String[] {String.valueOf(getId()), getTxType().name(), getComment(),
               String.valueOf(getTimeStamp().getTime()), String.valueOf(getAuthorArtId()),
               String.valueOf(getCommitArtId())};
      }

      public static TxRow fromArray(String[] data) {
         int txId = Integer.valueOf(data[0]);
         TransactionDetailsType txType = TransactionDetailsType.valueOf(data[1]);
         String comment = data[2];
         Date timeStamp = new Date(Long.valueOf(data[3]));
         int authorArtId = Integer.valueOf(data[4]);
         int commitArtId = Integer.valueOf(data[5]);
         return new TxRow(txId, txType, comment, timeStamp, authorArtId, commitArtId);
      }
   }

   public static TransactionCacheUpdateResponse fromCache(Collection<TransactionRecord> types) throws OseeCoreException {
      List<TxRow> rows = new ArrayList<TxRow>();
      Map<Integer, Integer> txToBranchId = new HashMap<Integer, Integer>();
      for (TransactionRecord tx : types) {
         rows.add(new TxRow(tx.getId(), tx.getTxType(), tx.getComment(), tx.getTimeStamp(), tx.getAuthor(),
               tx.getCommit()));
         txToBranchId.put(tx.getId(), tx.getBranchId());
      }
      return new TransactionCacheUpdateResponse(rows, txToBranchId);
   }
}
