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
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;

/**
 * @author Robert A. Fisher
 */
public class Branch implements Comparable<Branch>, IAdaptable {
   public static final String COMMON_BRANCH_CONFIG_ID = "Common";
   private static final String UPDATE_BRANCH_SHORT_NAME = "UPDATE osee_branch SET short_name = ? WHERE branch_id = ?";
   private final int branchId;
   private final int parentBranchId;
   private final int parentTransactionId;
   private String branchName;
   private String branchShortName;
   private boolean archived;
   private final int authorId;
   private int associatedArtifactId;
   private Artifact associatedArtifact;
   private final Timestamp creationDate;
   private final String creationComment;
   private final BranchType branchType;
   private Branch sourceBranch;
   private Branch destBranch;
   private boolean deleted;

   public Branch(String branchShortName, String branchName, int branchId, int parentBranchId, int parentTransactionId, boolean archived, int authorId, Timestamp creationDate, String creationComment, int associatedArtifactId, BranchType branchType) {
      this.branchShortName = StringFormat.truncate(branchShortName != null ? branchShortName : branchName, 25);
      this.branchId = branchId;
      this.branchName = branchName;
      this.parentBranchId = parentBranchId;
      this.parentTransactionId = parentTransactionId;
      this.archived = archived;
      this.authorId = authorId;
      this.creationDate = creationDate;
      this.creationComment = creationComment;
      this.associatedArtifactId = associatedArtifactId;
      this.associatedArtifact = null;
      this.branchType = branchType;
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
      if (branchName == null) throw new IllegalArgumentException("The branchName parameter can not be null.");
      this.branchName = branchName;
   }

   /**
    * @return Returns the short branch name if provided else returns null.
    */
   public String getBranchShortName() {
      return branchShortName;
   }

   /**
    * Sets the branch short name to the given value
    * 
    * @param persist if persist, store the change to the data-store
    */
   public void setBranchShortName(String branchShortName, boolean persist) throws OseeCoreException {
      if (persist) {
         ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_SHORT_NAME, StringFormat.truncate(branchShortName, 25),
               branchId);
      }
      this.branchShortName = branchShortName;
      kickRenameEvents();
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
      if (artifact.getBranch() != BranchManager.getCommonBranch()) throw new OseeArgumentException(
            "Setting associated artifact for branch only valid for common branch artifact.");

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
      return branchName;
   }

   public Branch getParentBranch() throws OseeCoreException {
      return BranchManager.getBranch(parentBranchId);
   }

   public boolean hasParentBranch() {
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
      return RevisionManager.getInstance().branchHasChanges(this);
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
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
         return true;
      }
   }

   public boolean isBaselineBranch() {
      return branchType.equals(BranchType.BASELINE);
   }

   public boolean isSystemRootBranch() {
      return branchType.equals(BranchType.SYSTEM_ROOT);
   }

   public boolean isTopLevelBranch() {
      return branchType.equals(BranchType.TOP_LEVEL);
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public boolean isMergeBranch() {
      return branchType.equals(BranchType.MERGE);
   }

   /**
    * @return the associatedArtifactId
    */
   public int getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   /**
    * @return Returns the branch short name if provided else a truncated branch name.
    */
   public String getDisplayName() {
      return getBranchShortName() != null && getBranchShortName().length() > 0 ? getBranchShortName() : StringFormat.truncate(
            getBranchName(), 22);
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
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

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

   public boolean matchesState(BranchState branchState) {
      return branchState == BranchState.ALL || (isArchived() && branchState == BranchState.ARCHIVED) || (!isArchived() && branchState == BranchState.ACTIVE);
   }

   public boolean matchesControlled(BranchControlled branchControlled) {
      return branchControlled == BranchControlled.ALL || (isChangeManaged() && branchControlled == BranchControlled.CHANGE_MANAGED) || (!isChangeManaged() && branchControlled == BranchControlled.NOT_CHANGE_MANAGED);
   }

   public void setDeleted() {
      this.deleted = true;
   }

   /**
    * @return the deleted
    */
   public boolean isDeleted() {
      return deleted;
   }

   /**
    * @return the parentTransactionId
    */
   public int getParentTransactionId() {
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
}