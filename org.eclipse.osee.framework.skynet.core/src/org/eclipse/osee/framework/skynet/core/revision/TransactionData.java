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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
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
   private int branchId;

   public TransactionData(String comment, Timestamp timeStamp, int authorId, int transactionId, int associatedArtId, int branchId, int commitArtId) {
      super();
      this.comment = comment == null ? "" : comment;
      this.timeStamp = timeStamp;
      this.transactionNumber = transactionId;
      this.associatedArtId = associatedArtId;
      this.commitArtId = commitArtId;
      this.branchId = branchId;

      try {
         User user = SkynetAuthentication.getInstance().getUserByArtId(authorId);
         name = user == null ? "" : user.getDescriptiveName();
      } catch (IllegalStateException ex) {
         name = "Could not resolve artId: " + (authorId);
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
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
   public int getBranchId() {
      return branchId;
   }

   public TransactionId getTransactionId() throws SQLException {
      return TransactionIdManager.getInstance().getPossiblyEditableTransactionIfFromCache(transactionNumber);
   }

   /**
    * @return the commitArtId
    */
   public int getCommitArtId() {
      return commitArtId;
   }
}
