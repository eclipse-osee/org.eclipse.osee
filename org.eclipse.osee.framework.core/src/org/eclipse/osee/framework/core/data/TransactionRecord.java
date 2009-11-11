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
package org.eclipse.osee.framework.core.data;

import java.util.Date;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;

/**
 * @author Jeff C. Phillips
 */
public class TransactionRecord implements IAdaptable {
   private final int transactionNumber;
   private final Branch branch;
   private final TransactionDetailsType txType;

   private String comment;
   private Date time;
   private int authorArtId;
   private int commitArtId;

   public TransactionRecord(int transactionNumber, Branch branch, String comment, Date time, int authorArtId, int commitArtId, TransactionDetailsType txType) {
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

   public int getId() {
      return transactionNumber;
   }

   public String getComment() {
      return comment;
   }

   public Date getDate() {
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

   public void setTime(Date time) {
      this.time = time;
   }

   public void setAuthor(int authorArtId) {
      this.authorArtId = authorArtId;
   }

   public void setCommit(int commitArtId) {
      this.commitArtId = commitArtId;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof TransactionRecord) {
         TransactionRecord other = (TransactionRecord) obj;
         return other.transactionNumber == transactionNumber;
      }
      return false;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (getClass().isAssignableFrom(adapter)) {
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