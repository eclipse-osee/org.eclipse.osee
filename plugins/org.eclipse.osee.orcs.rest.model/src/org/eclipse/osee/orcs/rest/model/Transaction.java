/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import java.util.Date;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.ArtifactId;
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
   private ArtifactId author;
   private ArtifactId commitArtId;

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

   public ArtifactId getAuthor() {
      return author;
   }

   public void setAuthor(ArtifactId author) {
      this.author = author;
   }

   public ArtifactId getCommitArt() {
      return commitArtId;
   }

   public void setCommitArt(ArtifactId commitArtId) {
      this.commitArtId = commitArtId;
   }

   @Override
   public String toString() {
      return "Transaction [txId=" + getTxId() + ", branchUuid=" + branchUuid + ", txType=" + txType + ", comment=" + comment + ", timestamp=" + timestamp + ", authorArtId=" + author + ", commitArtId=" + commitArtId + "]";
   }
}