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
package org.eclipse.osee.framework.core.model;

import java.util.Date;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Jeff C. Phillips
 */
public class TransactionRecord extends BaseIdentity<Integer> implements TransactionId, Adaptable {
   private final TransactionDetailsType txType;
   private final BranchId branch;
   private String comment;
   private Date time;
   private int authorArtId;
   private int commitArtId;

   public TransactionRecord(int transactionNumber, BranchId branch, String comment, Date time, int authorArtId, int commitArtId, TransactionDetailsType txType) {
      super(transactionNumber);
      this.branch = branch;
      this.comment = Strings.intern(comment);
      this.time = time;
      this.authorArtId = authorArtId;
      this.commitArtId = commitArtId;
      this.txType = txType;
   }

   public TransactionRecord(int transactionNumber) {
      this(transactionNumber, BranchId.SENTINEL, "INVALID", new Date(0), -1, -1, TransactionDetailsType.INVALID);
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   public String getComment() {
      return comment;
   }

   public Date getTimeStamp() {
      return time;
   }

   public int getAuthor() {
      return authorArtId;
   }

   public int getCommit() {
      return commitArtId;
   }

   public TransactionDetailsType getTxType() {
      return txType;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setTimeStamp(Date time) {
      this.time = time;
   }

   public void setAuthor(int authorArtId) {
      this.authorArtId = authorArtId;
   }

   public void setCommit(int commitArtId) {
      this.commitArtId = commitArtId;
   }

   @Override
   public String toString() {
      return "branchId: " + getBranchId() + " txId: " + getGuid();
   }

   public boolean isIdValid() {
      return getId() > 0;
   }
}