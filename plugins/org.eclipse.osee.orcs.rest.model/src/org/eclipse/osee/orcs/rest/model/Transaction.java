/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import static org.eclipse.osee.framework.core.data.TransactionId.SENTINEL;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class Transaction {

   private TransactionId txId = TransactionId.SENTINEL;
   private long branchUuid;
   private TransactionDetailsType txType;
   private String comment;
   private Date timestamp;
   private int authorId;
   private int commitArtId;

   public TransactionId getTxId() {
      return txId;
   }

   public void setTxId(TransactionId txId) {
      this.txId = txId;
   }

   public TransactionDetailsType getTxType() {
      return txType;
   }

   public void setTxType(TransactionDetailsType txType) {
      this.txType = txType;
   }

   public long getBranchUuid() {
      return branchUuid;
   }

   public void setBranchUuid(long branchUuid) {
      this.branchUuid = branchUuid;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public Date getTimeStamp() {
      return timestamp;
   }

   public void setTimeStamp(Date timestamp) {
      this.timestamp = timestamp;
   }

   public int getAuthorId() {
      return authorId;
   }

   public void setAuthorId(int authorId) {
      this.authorId = authorId;
   }

   public int getCommitArtId() {
      return commitArtId;
   }

   public void setCommitArtId(int commitArtId) {
      this.commitArtId = commitArtId;
   }

   @Override
   public String toString() {
      return "Transaction [txId=" + getTxId() + ", branchUuid=" + branchUuid + ", txType=" + txType + ", comment=" + comment + ", timestamp=" + timestamp + ", authorArtId=" + authorId + ", commitArtId=" + commitArtId + "]";
   }

}
