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
package org.eclipse.osee.framework.skynet.core.transaction;

import java.util.Date;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Jeff C. Phillips
 */
public class TransactionId implements IAdaptable {
   private final int transactionNumber;
   private final Branch branch;
   private final TransactionDetailsType txType;
   private String comment;
   private Date time;
   private int authorArtId;
   private int commitArtId;

   public TransactionId(int transactionNumber, Branch branch, String comment, Date time, int authorArtId, int commitArtId, TransactionDetailsType txType) {
      this.transactionNumber = transactionNumber;
      this.branch = branch;
      this.comment = comment;
      this.time = time;
      this.authorArtId = authorArtId;
      this.commitArtId = commitArtId;
      this.txType = txType;
   }

   public Branch getBranch() {
      return branch;
   }

   public int getTransactionNumber() {
      return transactionNumber;
   }

   public String getComment() {
      return comment;
   }

   public Date getDate() {
      return time;
   }

   public int getAuthorArtId() {
      return authorArtId;
   }

   public int getCommitArtId() {
      return commitArtId;
   }

   public TransactionDetailsType getTxType() {
      return txType;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setTime(Date time) {
      this.time = time;
   }

   public void setAuthorArtId(int authorArtId) {
      this.authorArtId = authorArtId;
   }

   public void setCommitArtId(int commitArtId) {
      this.commitArtId = commitArtId;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof TransactionId) {
         TransactionId other = (TransactionId) obj;
         return other.transactionNumber == transactionNumber;
      }
      return false;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == null) {
         throw new IllegalArgumentException("adapter can not be null");
      }

      if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }

   @Override
   public int hashCode() {
      int result = 17;
      result = 37 * result + transactionNumber;
      return result;
   }

   @Override
   public String toString() {
      return branch + ": " + transactionNumber;
   }
}