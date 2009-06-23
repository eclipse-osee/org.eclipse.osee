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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Robert A. Fisher
 */
public class Branch implements Comparable<Branch>, IAdaptable {
   private static final int SHORT_NAME_LIMIT = 25;
   public static final String COMMON_BRANCH_CONFIG_ID = "Common";
   private final int branchId;
   private final int parentBranchId;
   private TransactionId parentTransactionId;
   private final int parentTransactionIdNumber;
   private String branchName;
   private boolean archived;
   private final int authorId;
   private int associatedArtifactId;
   private Artifact associatedArtifact;
   private final Timestamp creationDate;
   private final String creationComment;
   private BranchType branchType;
   private Branch sourceBranch;
   private Branch destBranch;
   private BranchState branchState;

   public Branch(String branchName, int branchId, int parentBranchId, int parentTransactionIdNumber, boolean archived, int authorId, Timestamp creationDate, String creationComment, int associatedArtifactId, BranchType branchType, BranchState branchState) {
      this.branchId = branchId;
      this.branchName = branchName;
      this.parentBranchId = parentBranchId;
      this.parentTransactionIdNumber = parentTransactionIdNumber;
      this.archived = archived;
      this.authorId = authorId;
      this.creationDate = creationDate;
      this.creationComment = creationComment;
      this.associatedArtifactId = associatedArtifactId;
      this.associatedArtifact = null;
      this.branchType = branchType;
      this.branchState = branchState;
   }

   public BranchState getBranchState() {
      return branchState;
   }

   void setBranchState(BranchState branchState) {
      this.branchState = branchState;
   }

   /**
    * @return Returns the branchId.
    */
   public int getBranchId() {
      // Should we persist the branch automatically here if the branchId is 0 for cases the software
      // has freshly created a branch ?
      return branchId;
   }

   /**
    * @return Returns the branchName.
    */
   public String getBranchName() {
      return branchName;
   }

   /**
    * updates this object's name (but does not affect the datastore)
    * 
    * @param branchName The branchName to set.
    */
   public void setBranchName(String branchName) {
      if (branchName == null) {
         throw new IllegalArgumentException("The branchName parameter can not be null.");
      }
      this.branchName = branchName;
   }

   /**
    * @return Returns the short branch name if provided else returns null.
    */
   public String getBranchShortName() {
      return Strings.isValid(getBranchName()) ? StringFormat.truncate(getBranchName(), SHORT_NAME_LIMIT) : Strings.emptyString();
   }

   private void kickRenameEvents() throws OseeCoreException {
      OseeEventManager.kickBranchEvent(this, BranchEventType.Renamed, branchId);
   }

   /**
    * Sets the branch name to the given value and stores this change in the data-store
    * 
    * @param branchName The branchName to set.
    */
   public void rename(String branchName) throws OseeCoreException {
      setBranchName(branchName);
      ConnectionHandler.runPreparedUpdate("UPDATE osee_branch SET branch_name = ? WHERE branch_id = ?", branchName,
            branchId);
      kickRenameEvents();
   }

   public void setAssociatedArtifact(Artifact artifact) throws OseeCoreException {
      // TODO: this method should allow the artifact to be on any branch, not just common
      if (artifact.getBranch() != BranchManager.getCommonBranch()) {
         throw new OseeArgumentException(
               "Setting associated artifact for branch only valid for common branch artifact.");
      }

      ConnectionHandler.runPreparedUpdate("UPDATE osee_branch SET associated_art_id = ? WHERE branch_id = ?",
            artifact.getArtId(), branchId);

      associatedArtifact = artifact;
      associatedArtifactId = artifact.getArtId();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return getBranchName();
   }

   public Branch getParentBranch() throws OseeCoreException {
      return BranchManager.getBranch(getParentBranchId());
   }

   public boolean hasParentBranch() throws OseeCoreException {
      return !isTopLevelBranch();
   }

   /**
    * @return the top level branch that is an ancestor of this branch (which could be itself)
    */
   public Branch getTopLevelBranch() throws OseeCoreException {
      Branch branchCursor = this;
      while (branchCursor.hasParentBranch()) {
         branchCursor = branchCursor.getParentBranch();
      }
      return branchCursor;
   }

   public Collection<Branch> getChildBranches() throws OseeCoreException {
      return getChildBranches(false);
   }

   public Collection<Branch> getChildBranches(boolean recurse) throws OseeCoreException {
      Set<Branch> children = new HashSet<Branch>();
      getChildBranches(this, children, recurse);
      return children;
   }

   private void getChildBranches(Branch parentBranch, Collection<Branch> children, boolean recurse) throws OseeCoreException {
      for (Branch branch : BranchManager.getNormalBranches()) {
         if (branch.getParentBranchId() == parentBranch.getBranchId()) {
            children.add(branch);
            if (recurse) {
               getChildBranches(branch, children, recurse);
            }
         }
      }
   }

   /**
    * @return Returns all children branches including archived branches
    */
   public Collection<Branch> getDescendants() throws OseeCoreException {
      Set<Branch> children = new HashSet<Branch>();
      getAllChildBranches(this, children, true);

      return children;
   }

   private void getAllChildBranches(Branch parentBranch, Collection<Branch> children, boolean recurse) throws OseeCoreException {
      for (Branch branch : BranchManager.getNormalAllBranches()) {
         if (branch.getParentBranchId() == parentBranch.getBranchId()) {
            children.add(branch);
            if (recurse) {
               getChildBranches(branch, children, recurse);
            }
         }
      }
   }

   /**
    * @return Returns the parentBranchId.
    */
   public int getParentBranchId() {
      return parentBranchId;
   }

   public void archive() throws OseeCoreException {
      BranchManager.archive(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Branch) {
         return ((Branch) obj).branchId == branchId;
      }

      return false;
   }

   @Override
   public int hashCode() {
      return branchId * 13;
   }

   /**
    * @return Returns the archived.
    */
   public boolean isArchived() {
      return archived;
   }

   public void setArchived(boolean archived) {
      this.archived = archived;
   }

   public boolean hasChanges() throws OseeCoreException {
      return RevisionManager.branchHasChanges(this);
   }

   public int getAuthorId() {
      return authorId;
   }

   public String getCreationComment() {
      return creationComment;
   }

   public Date getCreationDate() {
      return creationDate;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(Branch branch) {
      return getBranchName().compareToIgnoreCase(branch.getBranchName());
   }

   /**
    * @return Returns the associatedArtifact.
    * @throws MultipleArtifactsExist
    * @throws ArtifactDoesNotExist
    * @throws OseeDataStoreException
    */
   public Artifact getAssociatedArtifact() throws OseeCoreException {
      if (associatedArtifact == null && associatedArtifactId > 0) {
         associatedArtifact = ArtifactQuery.getArtifactFromId(associatedArtifactId, BranchManager.getCommonBranch());
      }
      return associatedArtifact;
   }

   /**
    * Efficient way of determining if branch is associated cause it does not load the associated artifact
    * 
    * @param artifact
    */
   public boolean isAssociatedToArtifact(Artifact artifact) {
      return artifact.getArtId() == getAssociatedArtifactId();
   }

   public boolean isChangeManaged() {
      try {
         return associatedArtifactId != UserManager.getUser(SystemUser.OseeSystem).getArtId();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return true;
      }
   }

   /*
    * True if baseline or top level branch
    */
   public boolean isBaselineBranch() {
      return branchType.equals(BranchType.BASELINE) || branchType.equals(BranchType.TOP_LEVEL);
   }

   public boolean isSystemRootBranch() {
      return branchType.equals(BranchType.SYSTEM_ROOT);
   }

   public boolean isTopLevelBranch() throws OseeCoreException {
      return getParentBranch() != null && getParentBranch().getBranchType().equals(BranchType.SYSTEM_ROOT);
   }

   public BranchType getBranchType() {
      return branchType;
   }

   protected void setBranchType(int type) {
      branchType = BranchType.getBranchType(type);
   }

   public boolean isMergeBranch() {
      return branchType.equals(BranchType.MERGE);
   }

   public boolean isWorkingBranch() {
      return branchType.equals(BranchType.WORKING);
   }

   /**
    * @return the associatedArtifactId
    */
   public int getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   public String asFolderName() {
      String branchName = this.getBranchShortName();

      // Remove illegal filename characters
      // NOTE: The current program.launch has a tokenizing bug that causes an error if consecutive spaces are in the name
      branchName = branchName.replaceAll("[^A-Za-z0-9]", "_");
      branchName = StringFormat.truncate(branchName, 20).trim();

      return String.format("%s.%s", branchName.toLowerCase(), this.getBranchId());
   }

   public static int getBranchIdFromBranchFolderName(String folderName) throws Exception {
      return Integer.parseInt(Lib.getExtension(folderName));
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) {
         throw new IllegalArgumentException("adapter can not be null");
      }

      if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }

   public void setMergeBranchInfo(Branch sourceBranch, Branch destBranch) {
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
   }

   public boolean isMergeBranchFor(Branch sourceBranch, Branch destBranch) {
      return sourceBranch.equals(this.sourceBranch) && destBranch.equals(this.destBranch);
   }

   /**
    * @param branchTypes
    * @return whether this branch is of one of the specified branch types
    */
   public boolean isOfType(BranchType... branchTypes) {
      for (BranchType branchType : branchTypes) {
         if (this.branchType == branchType) {
            return true;
         }
      }
      return false;
   }

   public boolean matchesState(BranchArchivedState branchState) {
      return branchState == BranchArchivedState.ALL || isArchived() && branchState == BranchArchivedState.ARCHIVED || !isArchived() && branchState == BranchArchivedState.UNARCHIVED;
   }

   public boolean matchesControlled(BranchControlled branchControlled) {
      return branchControlled == BranchControlled.ALL || isChangeManaged() && branchControlled == BranchControlled.CHANGE_MANAGED || !isChangeManaged() && branchControlled == BranchControlled.NOT_CHANGE_MANAGED;
   }

   public void setDeleted() {
      setBranchState(BranchState.DELETED);
      try {
         OseeEventManager.kickBranchEvent(this, BranchEventType.Deleted, getBranchId());
      } catch (Exception ex) {
         // Do Nothing
      }
   }

   /**
    * @return the deleted
    */
   public boolean isDeleted() {
      return getBranchState() == BranchState.DELETED;
   }

   /**
    * @return Returns whether the branch is editable.
    */
   public boolean isEditable() {
      return !isCommitted() && !isRebaselined() && !isArchived() && !isDeleted();
   }

   public boolean isCommitted() {
      return getBranchState() == BranchState.COMMITTED;
   }

   public boolean isRebaselined() {
      return getBranchState() == BranchState.REBASELINED;
   }

   public boolean isRebaselineInProgress() {
      return getBranchState() == BranchState.REBASELINE_IN_PROGRESS;
   }

   /**
    * @return the parentTransactionId
    * @throws OseeCoreException
    */
   public TransactionId getParentTransactionId() throws OseeCoreException {
      if (parentTransactionId == null) {
         parentTransactionId = TransactionIdManager.getTransactionId(parentTransactionIdNumber);
      }
      return parentTransactionId;
   }

   public List<Branch> getBranchHierarchy() throws OseeCoreException {
      List<Branch> ancestors = new LinkedList<Branch>();
      Branch branchCursor = this;
      ancestors.add(branchCursor);
      while (branchCursor.hasParentBranch()) {
         ancestors.add(branchCursor.getParentBranch());
         branchCursor = branchCursor.getParentBranch();
      }
      return ancestors;
   }

   public Collection<Branch> getWorkingBranches() throws OseeCoreException {
      return BranchManager.getWorkingBranches(this);
   }

   public int getParentTransactionNumber() {
      return parentTransactionIdNumber;
   }
}