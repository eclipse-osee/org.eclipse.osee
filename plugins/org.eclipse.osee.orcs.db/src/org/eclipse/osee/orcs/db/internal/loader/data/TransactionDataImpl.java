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
package org.eclipse.osee.orcs.db.internal.loader.data;

import java.util.Date;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.orcs.core.ds.TxOrcsData;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public class TransactionDataImpl extends OrcsObjectImpl<Integer> implements TxOrcsData {

   private int authorId = RelationalConstants.ART_ID_SENTINEL;
   private Long branchId = RelationalConstants.BRANCH_SENTINEL;
   private String comment = RelationalConstants.DEFAULT_COMMENT;
   private int commitId = RelationalConstants.ART_ID_SENTINEL;
   private TransactionDetailsType type = TransactionDetailsType.INVALID;
   private Date date;

   public TransactionDataImpl() {
      super();
   }

   @Override
   public int getAuthorId() {
      return authorId;
   }

   @Override
   public Long getBranchId() {
      return branchId;
   }

   @Override
   public String getComment() {
      return comment;
   }

   @Override
   public int getCommit() {
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
   public void setAuthorId(int authorId) {
      this.authorId = authorId;
   }

   @Override
   public void setBranchId(Long branchId) {
      this.branchId = branchId;
   }

   @Override
   public void setComment(String comment) {
      this.comment = comment;
   }

   @Override
   public void setCommit(int commitId) {
      this.commitId = commitId;
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
      return "TransactionDataImpl [id=" + getGuid() + ", authorId=" + authorId + ", branchUuid=" + branchId + ", comment=" + comment + ", commitId=" + commitId + ", date=" + date + ", type=" + type + "]";
   }

   @Override
   public Integer getGuid() {
      return super.getLocalId();
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      for (Identity<?> identity : identities) {
         if (identity.getGuid().equals(this.getGuid())) {
            return true;
         }
      }
      return false;
   }

}
