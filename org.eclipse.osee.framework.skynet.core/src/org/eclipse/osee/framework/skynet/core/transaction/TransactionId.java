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
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;

/**
 * Describes information associated with a transaction.
 * 
 * @author Jeff C. Phillips
 */
public class TransactionId implements Serializable {
   private static final long serialVersionUID = 7295589339029402964L;
   private int transactionNumber;
   private boolean head;
   private Branch branch;
   private String comment;
   // This will only differ from the transactionNumber for TransactionId's that are head
   // and when there is a SkynetTransaction in progress
   private int lastSavedTransactionNumber;

   /**
    * @param transactionNumber
    * @param head
    * @param branch
    * @param comment
    */
   public TransactionId(int transactionNumber, Branch branch, boolean head, String comment) {
      if (branch == null) throw new IllegalArgumentException(
            "Branch can not be null. TransactionNumber = " + transactionNumber + ", Transaction is editable = " + head);

      this.transactionNumber = transactionNumber;
      this.head = head;
      this.branch = branch;
      this.comment = comment;
      this.lastSavedTransactionNumber = transactionNumber;
   }

   //   /**
   //    * Constructor for deserialization
   //    */
   //   private TransactionId() {
   //      this.transactionNumber = 0;
   //      this.head = false;
   //      this.branch = null;
   //      this.comment = null;
   //   }

   /**
    * @return Returns the branch.
    */
   public Branch getBranch() {
      return branch;
   }

   /**
    * @return the head
    */
   public boolean isHead() {
      return head;
   }

   /**
    * @return Returns the head.
    */
   public boolean isEditable() {
      return isHead();
   }

   /**
    * @return Returns the transactionId.
    */
   public int getTransactionNumber() {
      return transactionNumber;
   }

   /**
    * @param transactionNumber The transactionId to set.
    */
   protected void setTransactionNumber(int transactionNumber) {
      this.transactionNumber = transactionNumber;
   }

   /**
    * @param head The head to set.
    */
   public void setHead(boolean head) {
      this.head = head;
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
    * @return The TransactionId associated with the transactionNumber from this dumb object
    * @throws ObjectStreamException
    * @throws SQLException
    * @throws BranchDoesNotExist
    */
   private Object readResolve() throws ObjectStreamException, SQLException, BranchDoesNotExist {
      return TransactionIdManager.getInstance().getNonEditableTransactionId(transactionNumber);
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
         return (other.head == head && other.transactionNumber == transactionNumber) || (other.head && head && other.branch.equals(branch));
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
      if (head) {
         return branch.hashCode();
      }
      int result = 17;
      result = 37 * result + transactionNumber;
      return result;
   }

   /**
    * @return the lastSavedTransactionNumber
    */
   protected int getLastSavedTransactionNumber() {
      return lastSavedTransactionNumber;
   }

   /**
    * @param lastSavedTransactionNumber the lastSavedTransactionNumber to set
    */
   protected void setLastSavedTransactionNumber(int lastSavedTransactionNumber) {
      this.lastSavedTransactionNumber = lastSavedTransactionNumber;
   }
}