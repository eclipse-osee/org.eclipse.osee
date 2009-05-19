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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * Describes information associated with a transaction.
 * 
 * @author Jeff C. Phillips
 */
public class TransactionId implements Serializable, IAdaptable {
   private static final long serialVersionUID = 7295589339029402964L;
   private int transactionNumber;
   private final Branch branch;
   private final String comment;
   private final Date time;
   private final int authorArtId;
   private final int commitArtId;
   private final TransactionDetailsType txType;

   /**
    * @param transactionNumber
    * @param branch
    * @param comment
    */
   public TransactionId(int transactionNumber, Branch branch, String comment, Date time, int authorArtId, int commitArtId, TransactionDetailsType txType) {
      this.transactionNumber = transactionNumber;
      this.branch = branch;
      this.comment = comment;
      this.time = time;
      this.authorArtId = authorArtId;
      this.commitArtId = commitArtId;
      this.txType = txType;
   }

   /**
    * @return Returns the branch.
    */
   public Branch getBranch() {
      return branch;
   }

   /**
    * @return Returns the branch id.
    */
   public int getBranchId() {
      return branch.getBranchId();
   }

   /**
    * @return Returns the transactionId.
    */
   public int getTransactionNumber() {
      return transactionNumber;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return branch + ": " + transactionNumber;
   }

   /**
    * @return Returns the comment.
    */
   public String getComment() {
      return comment;
   }

   /**
    * Only store the transaction number since the transaction Id manager controls these.
    * 
    * @param stream
    * @throws IOException
    */
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.writeInt(transactionNumber);
   }

   /**
    * Initialize as a dumb object for portraying the transactionNumber
    * 
    * @param stream
    * @throws IOException
    */
   private void readObject(ObjectInputStream stream) throws IOException {
      transactionNumber = stream.readInt();
   }

   /**
    * @return The TransactionId associated with the transactionNumber
    * @throws OseeDataStoreException
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    */
   private Object readResolve() throws OseeCoreException {
      return TransactionIdManager.getTransactionId(transactionNumber);
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof TransactionId) {
         TransactionId other = ((TransactionId) obj);
         return other.transactionNumber == transactionNumber;
      }

      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      int result = 17;
      result = 37 * result + transactionNumber;
      return result;
   }

   /**
    * @return the time
    */
   public Date getTime() {
      return time;
   }

   /**
    * @return the authorArtId
    */
   public int getAuthorArtId() {
      return authorArtId;
   }

   /**
    * @return the commitArtId
    */
   public int getCommitArtId() {
      return commitArtId;
   }

   /**
    * @return the txType
    */
   public TransactionDetailsType getTxType() {
      return txType;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }
}