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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.UserNotInDatabase;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * Stores transaction information.
 * 
 * @author Jeff C. Phillips
 */
public class TransactionData implements IAdaptable {
   private final String comment;
   private final Timestamp timeStamp;
   private final int associatedArtId;
   private final int commitArtId;
   private final int transactionNumber;
   private String name;
   private final Branch branch;
   private Artifact artifact;

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
            user = UserManager.getUser(SystemUser.OseeSystem);
            authorId = user.getArtId();
         } else {
            user = UserManager.getUserByArtId(authorId);
         }
         name = user.getDescriptiveName();
      } catch (UserNotInDatabase ex) {
         name = "Could not resolve artId: " + authorId;
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   /**
    * @return the artifact
    */
   public Artifact getArtifact() {
      if (artifact == null) {
         if (getAssociatedArtId() > 0) {
            try {
               artifact =
                     ArtifactPersistenceManager.getInstance().getArtifactFromId(getAssociatedArtId(),
                           getTransactionId());
            } catch (OseeCoreException ex) {
               OseeLog.log(OseeActivator.class, Level.SEVERE, ex);
            }
         }
      }
      return artifact;
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

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      if (adapter.isInstance(this)) {
         return this;
      }

      try {
         if (adapter.isInstance(getArtifact())) {
            return getArtifact();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }
}
