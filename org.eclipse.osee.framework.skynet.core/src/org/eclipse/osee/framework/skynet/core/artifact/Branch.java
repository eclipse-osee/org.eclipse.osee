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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.BRANCH_TABLE;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkRenameBranchEvent;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.event.LocalRenameBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.remoteEvent.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;

/**
 * @author Robert A. Fisher
 */
public class Branch implements Comparable<Branch>, IAdaptable {
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();
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
   public static final int NULL_PARENT_BRANCH_ID = -1;
   public static final String COMMON_BRANCH_CONFIG_ID = "Common";
   private final Exception birthPlace = new Exception();

   public Branch(String branchShortName, String branchName, int branchId, int parentBranchId, boolean archived, int authorId, Timestamp creationDate, String creationComment, int associatedArtifactId) {

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

      this.birthPlace.getStackTrace();
      branchManager.cache(this);
   }

   public Branch(String branchShortName, String branchName, int branchId, int parentBranchId, boolean archived, int authorId, Timestamp creationDate, String creationComment, Artifact associatedArtifact) {
      this(branchShortName, branchName, branchId, parentBranchId, archived, authorId, creationDate, creationComment,
            associatedArtifact.getArtId());
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
      RemoteEventManager.getInstance().kick(
            new NetworkRenameBranchEvent(branchId,
                  SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId(), branchName,
                  getBranchShortName()));
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
      if (artifact.getBranch() != branchManager.getCommonBranch()) throw new IllegalArgumentException(
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
         parentBranch = branchManager.getBranch(parentBranchId);
      }
      return parentBranch;
   }

   public boolean hasParentBranch() throws SQLException {
      return getParentBranch() != null;
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
      for (Branch branch : BranchPersistenceManager.getInstance().getBranches()) {
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
      branchManager.archive(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Branch) return ((Branch) obj).branchId == branchId;

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

   public boolean hasChanges() throws IllegalStateException, SQLException {
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
    */
   public Artifact getAssociatedArtifact() throws SQLException {
      if (associatedArtifact == null && associatedArtifactId > 0) {
         associatedArtifact =
               ArtifactPersistenceManager.getInstance().getArtifactFromId(associatedArtifactId,
                     branchManager.getCommonBranch());
         // TODO: this method must get the artifact based on an art id and use the
         // right branch when doing so (the artifact is not necessarily a user artifact)
      }
      return associatedArtifact;
   }

   public boolean isChangeManaged() {
      return associatedArtifactId != skynetAuth.getNoOneArtifactId();
   }

   public boolean isBaselineBranch() {
      System.out.println("Branch.isBaselineBranch...fix this");
      return !getBranchName().contains("RPCR");
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
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }
}