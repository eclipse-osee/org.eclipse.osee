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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.BRANCH_TABLE;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkRenameBranchEvent;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.LocalRenameBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;

/**
 * @author Robert A. Fisher
 */
public class Branch implements Comparable<Branch>, IAdaptable {
   public static enum BranchType {
      STANDARD(0), ROOT(1), BASELINE(2), MERGE(3);
      private final int value;

      BranchType(int value) {
         this.value = value;
      }

      public final int getValue() {
         return value;
      }

      public static BranchType getBranchType(int value) {
         for (BranchType type : values())
            if (type.getValue() == value) return type;
         return null;
      }
   };
   private static final String UPDATE_BRANCH_SHORT_NAME =
         "UPDATE " + BRANCH_TABLE + " SET short_name = ? WHERE branch_id = ?";
   private final int branchId;
   private Branch parentBranch;
   private final int parentBranchId;
   private String branchName;
   private String branchShortName;
   private boolean archived;
   private final int authorId;
   private int associatedArtifactId;
   private Artifact associatedArtifact;
   private final Timestamp creationDate;
   private final String creationComment;
   private final BranchType branchType;
   public static final int NULL_PARENT_BRANCH_ID = -1;
   public static final String COMMON_BRANCH_CONFIG_ID = "Common";
   private final Exception birthPlace = new Exception();

   public Branch(String branchShortName, String branchName, int branchId, int parentBranchId, boolean archived, int authorId, Timestamp creationDate, String creationComment, int associatedArtifactId, BranchType branchType) {

      this.branchShortName = StringFormat.truncate(branchShortName != null ? branchShortName : branchName, 25);
      this.branchId = branchId;
      this.branchName = branchName;
      this.parentBranch = null;
      this.parentBranchId = parentBranchId;
      this.archived = archived;
      this.authorId = authorId;
      this.creationDate = creationDate;
      this.creationComment = creationComment;
      this.associatedArtifactId = associatedArtifactId;
      this.associatedArtifact = null;
      this.branchType = branchType;

      this.birthPlace.getStackTrace();
      BranchPersistenceManager.getInstance().cache(this);
   }

   public Branch(String branchShortName, String branchName, int branchId, int parentBranchId, boolean archived, int authorId, Timestamp creationDate, String creationComment, Artifact associatedArtifact, BranchType branchType) {
      this(branchShortName, branchName, branchId, parentBranchId, archived, authorId, creationDate, creationComment,
            associatedArtifact.getArtId(), branchType);
      this.associatedArtifact = associatedArtifact;
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

   public String getBranchShortestName() {
      if (branchShortName != null)
         return branchShortName;
      else
         return branchName;
   }

   /**
    * Sets the branch short name to the given value
    * 
    * @param persist if persist, store the change to the data-store
    * @throws SQLException
    */
   public void setBranchShortName(String branchShortName, boolean persist) throws SQLException {
      if (persist) {
         ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_SHORT_NAME, SQL3DataType.VARCHAR, StringFormat.truncate(
               branchShortName, 25), SQL3DataType.INTEGER, branchId);
      }
      this.branchShortName = branchShortName;
      kickRenameEvents();
   }

   private void kickRenameEvents() {
      SkynetEventManager.getInstance().kick(
            new LocalRenameBranchEvent(this, branchId, branchName, getBranchShortName()));
      RemoteEventManager.kick(new NetworkRenameBranchEvent(branchId, SkynetAuthentication.getUser().getArtId(),
            branchName, getBranchShortName()));
   }

   /**
    * Sets the branch name to the given value and stores this change in the data-store
    * 
    * @param branchName The branchName to set.
    */
   public void rename(String branchName) throws SQLException {
      setBranchName(branchName);
      ConnectionHandler.runPreparedUpdate("UPDATE " + BRANCH_TABLE + " SET branch_name = ? WHERE branch_id = ?",
            SQL3DataType.VARCHAR, branchName, SQL3DataType.INTEGER, branchId);
      kickRenameEvents();
   }

   public void setAssociatedArtifact(Artifact artifact) throws Exception {
      // TODO: this method should allow the artifact to be on any branch, not just common
      if (artifact.getBranch() != BranchPersistenceManager.getCommonBranch()) throw new IllegalArgumentException(
            "Setting associated artifact for branch only valid for common branch artifact.");

      ConnectionHandler.runPreparedUpdate("UPDATE " + BRANCH_TABLE + " SET associated_art_id = ? WHERE branch_id = ?",
            SQL3DataType.INTEGER, artifact.getArtId(), SQL3DataType.INTEGER, branchId);

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

   public Branch getParentBranch() throws SQLException {
      if (parentBranch == null && parentBranchId != NULL_PARENT_BRANCH_ID) {
         try {
            parentBranch = BranchPersistenceManager.getInstance().getBranch(parentBranchId);
         } catch (BranchDoesNotExist ex) {
            OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
         }
      }
      return parentBranch;
   }

   public boolean hasParentBranch() throws SQLException {
      return getParentBranch() != null;
   }

   /**
    * @return the branch that is this oldest ancestor for this branch (which could be itself)
    * @throws SQLException
    */
   public Branch getRootBranch() throws SQLException {
      Branch branchCursor = null;
      for (branchCursor = this; branchCursor.getParentBranch() != null; branchCursor = branchCursor.getParentBranch())
         ;
      return branchCursor;
   }

   public Collection<Branch> getChildBranches() throws SQLException {
      return getChildBranches(false);
   }

   public Collection<Branch> getChildBranches(boolean recurse) throws SQLException {
      Set<Branch> children = new HashSet<Branch>();
      getChildBranches(this, children);
      return children;
   }

   private void getChildBranches(Branch parentBranch, Collection<Branch> children) throws SQLException {
      for (Branch branch : BranchPersistenceManager.getBranches()) {
         if (branch.getParentBranchId() == parentBranch.getBranchId()) {
            children.add(branch);
            getChildBranches(branch, children);
         }
      }
   }

   /**
    * @return Returns the parentBranchId.
    */
   public int getParentBranchId() {
      return parentBranchId;
   }

   public void archive() throws SQLException {
      BranchPersistenceManager.getInstance().archive(this);
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

   public void setArchived() {
      archived = true;
   }

   public boolean hasChanges() throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
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
    */
   public Artifact getAssociatedArtifact() throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      if (associatedArtifact == null && associatedArtifactId > 0) {
         associatedArtifact =
               ArtifactQuery.getArtifactFromId(associatedArtifactId, BranchPersistenceManager.getCommonBranch());
         // TODO: this method must get the artifact based on an art id and use the
         // right branch when doing so (the artifact is not necessarily a user artifact)
      }
      return associatedArtifact;
   }

   /**
    * Efficient way of determining if branch is associated cause it does not load the associated artifact
    * 
    * @param artifact
    * @return
    */
   public boolean isAssociatedToArtifact(Artifact artifact) {
      return artifact.getArtId() == getAssociatedArtifactId();
   }

   public boolean isChangeManaged() {
      return associatedArtifactId != SkynetAuthentication.getNoOneArtifactId();
   }

   // TODO fix this HACK
   public boolean isBaselineBranch() {
      System.out.println("Branch.isBaselineBranch...fix this");
      return !getBranchName().contains("RPCR");
   }

   public boolean isRootBranch() {
      return branchType.equals(BranchType.ROOT);
   }

   //   public boolean isBaselineBranch() {
   //      return branchType.equals(BranchType.BASELINE);
   //   }

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
      String branchName = this.getBranchShortestName();

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

}