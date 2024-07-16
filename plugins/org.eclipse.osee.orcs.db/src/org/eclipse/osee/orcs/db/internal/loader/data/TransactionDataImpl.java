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

package org.eclipse.osee.orcs.db.internal.loader.data;

import java.util.Date;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.orcs.core.ds.TxOrcsData;

/**
 * @author Roberto E. Escobar
 */
public class TransactionDataImpl extends BaseId implements TxOrcsData {

   private UserToken authorId = UserToken.SENTINEL;
   private BranchId branch = BranchId.SENTINEL;
   private String comment = RelationalConstants.DEFAULT_COMMENT;
   private ArtifactId commitId = ArtifactId.SENTINEL;
   private TransactionDetailsType type = TransactionDetailsType.INVALID;
   private Date date;
   private Long buildId = 0L;

   public TransactionDataImpl(Long id) {
      super(id);
   }

   @Override
   public UserToken getAuthor() {
      return authorId;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   @Override
   public String getComment() {
      return comment;
   }

   @Override
   public ArtifactId getCommitArt() {
      return commitId;
   }

   @Override
   public Date getDate() {
      return date;
   }

   @Override
   public TransactionDetailsType getTxType() {
      return type;
   }

   @Override
   public void setAuthor(UserToken author) {
      this.authorId = author;
   }

   @Override
   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   @Override
   public void setComment(String comment) {
      this.comment = comment;
   }

   @Override
   public void setCommitArt(ArtifactId commitArt) {
      this.commitId = commitArt;
   }

   @Override
   public void setDate(Date date) {
      this.date = date;
   }

   @Override
   public void setTxType(TransactionDetailsType type) {
      this.type = type;
   }

   @Override
   public String toString() {
      return "TransactionDataImpl [id=" + getId() + ", authorId=" + authorId + ", branch=" + branch + ", comment=" + comment + ", commitId=" + commitId + ", date=" + date + ", type=" + type + "]";
   }

   @Override
   public Long getBuildId() {
      return buildId;
   }

   @Override
   public void setBuildId(Long buildId) {
      this.buildId = buildId;
   }

}