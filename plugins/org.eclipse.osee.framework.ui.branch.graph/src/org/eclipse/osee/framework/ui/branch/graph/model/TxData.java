/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.branch.graph.model;

import java.util.Date;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeApiService;

/**
 * @author Roberto E. Escobar
 */
public class TxData {

   private final UserId authorId;
   private final Date timeStamp;
   private final String comment;
   private final TransactionDetailsType txType;
   private final BranchId branch;
   private final Long txId;

   public TxData(BranchId branch, UserId authorId, Date timeStamp, String comment, TransactionDetailsType txType, long txId) {
      this.authorId = authorId;
      this.timeStamp = timeStamp;
      this.comment = comment;
      this.txType = txType;
      this.branch = branch;
      this.txId = txId;
   }

   /**
    * @return the author
    */
   public String getAuthor() {
      String authorName = null;
      try {
         UserToken user = OseeApiService.userSvc().getUser(authorId);
         if (user != null) {
            authorName = user.getName();
         }
      } catch (OseeCoreException ex) {
         authorName = "Unknown";
      }
      return authorName;
   }

   /**
    * @return the timeStamp
    */
   public Date getTimeStamp() {
      return timeStamp;
   }

   /**
    * @return the comment
    */
   public String getComment() {
      return comment;
   }

   /**
    * @return the txType
    */
   public TransactionDetailsType getTxType() {
      return txType;
   }

   /**
    * @return the branch
    */
   public BranchId getBranch() {
      return branch;
   }

   /**
    * @return the txId
    */
   public Long getTxId() {
      return txId;
   }

   @Override
   public int hashCode() {
      return getTxId().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof TxData) {
         TxData other = (TxData) obj;
         return other.getTxId().longValue() == getTxId().longValue();
      }
      return false;
   }

   @Override
   public String toString() {
      return String.format("Tx:[%s] Author:[%s] Branch:[%s] Comment:[%s]", getTxId(), getAuthor(), getBranch(),
         getComment());
   }

   protected static TxData createTxData(TransactionRecord txId) {
      return new TxData(txId.getBranch(), txId.getAuthor(), txId.getTimeStamp(), txId.getComment(), txId.getTxType(),
         txId.getId());
   }
}