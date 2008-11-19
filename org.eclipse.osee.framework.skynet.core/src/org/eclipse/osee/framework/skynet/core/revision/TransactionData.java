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
package org.eclipse.osee.framework.skynet.core.revision;

import java.sql.Timestamp;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.UserNotInDatabase;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * Stores transaction information.
 * 
 * @author Jeff C. Phillips
 */
public class TransactionData {
   private String comment;
   private Timestamp timeStamp;
   private int associatedArtId;
   private int commitArtId;
   private int transactionNumber;
   private String name;
   private Branch branch;

   public TransactionData(String comment, Timestamp timeStamp, int authorId, int transactionId, int associatedArtId, Branch branch, int commitArtId) throws OseeCoreException {
      super();
      this.comment = comment == null ? "" : comment;
      this.timeStamp = timeStamp;
      this.transactionNumber = transactionId;
      this.associatedArtId = associatedArtId;
      this.commitArtId = commitArtId;
      this.branch = branch;

      try {
         User user = null;
         if (authorId == 0) {
            user = UserManager.getUser(SystemUser.NoOne);
            authorId = user.getArtId();
         } else {
            user = UserManager.getUserByArtId(authorId);
         }
         name = user.getDescriptiveName();
      } catch (UserNotInDatabase ex) {
         name = "Could not resolve artId: " + authorId;
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
   }

   public String getComment() {
      return comment;
   }

   public Timestamp getTimeStamp() {
      return timeStamp;
   }

   public int getTransactionNumber() {
      return transactionNumber;
   }

   public String getName() {
      return name;
   }

   public int getAssociatedArtId() {
      return associatedArtId;
   }

   /**
    * @return Returns the branchId.
    */
   public Branch getBranch() {
      return branch;
   }

   public TransactionId getTransactionId() throws OseeCoreException {
      return TransactionIdManager.getTransactionId(transactionNumber);
   }

   /**
    * @return the commitArtId
    */
   public int getCommitArtId() {
      return commitArtId;
   }
}
