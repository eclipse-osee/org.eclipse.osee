/*******************************************************************************
 * Copyright (c) 2024 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.Date;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

public class TransactionDetails {
   private TransactionId txId;
   private BranchToken branch;
   private Date time;
   private String oseeComment;
   private int txType;
   private ArtifactId commitArtId;
   private Long build_id;
   private ArtifactId author;
   public TransactionDetails() {
      this.txId = TransactionId.SENTINEL;
      this.time = DateUtil.getSentinalDate();
      this.oseeComment = "";
      this.commitArtId = ArtifactId.SENTINEL;
      this.build_id = 0L;
      this.branch = BranchToken.SENTINEL;
      this.author = ArtifactId.SENTINEL;
   }

   public TransactionDetails(TransactionId txId, BranchToken branch, Date time, String oseeComment, int txType, ArtifactId commitArtId, Long build_id, ArtifactId author) {
      this.txId = txId;
      this.time = time;
      this.oseeComment = oseeComment;
      this.commitArtId = commitArtId;
      this.build_id = build_id;
      this.branch = branch;
      this.author = author;
   }

   public BranchToken getBranch() {
      return branch;
   }

   public void setBranchId(BranchToken branch) {
      this.branch = branch;
   }

   public Date getTime() {
      return time;
   }

   public void setTime(Date time) {
      this.time = time;
   }

   public String getOseeComment() {
      return oseeComment;
   }

   public void setOseeComment(String oseeComment) {
      this.oseeComment = oseeComment;
   }

   public int getTxType() {
      return txType;
   }

   public void setTxType(int txType) {
      this.txType = txType;
   }

   public ArtifactId getCommitArtId() {
      return commitArtId;
   }

   public void setCommitArtId(ArtifactId commitArtId) {
      this.commitArtId = commitArtId;
   }

   public Long getBuild_id() {
      return build_id;
   }

   public void setBuild_id(Long build_id) {
      this.build_id = build_id;
   }

   public TransactionId getTxId() {
      return txId;
   }

   public void setTxId(TransactionId txId) {
      this.txId = txId;
   }

   public ArtifactId getAuthor() {
      return author;
   }

   public void setAuthor(ArtifactId author) {
      this.author = author;
   }

}
