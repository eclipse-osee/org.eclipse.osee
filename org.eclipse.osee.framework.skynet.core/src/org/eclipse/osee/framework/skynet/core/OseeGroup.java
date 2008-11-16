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
package org.eclipse.osee.framework.skynet.core;

import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;

/**
 * @author Roberto E. Escobar
 */
public class OseeGroup {
   private static final String GROUP_ARTIFACT_TYPE = "User Group";

   private final String groupName;
   private Artifact groupArtifact;
   private boolean isCurrentUserAMember;
   private boolean isCheckAllowed = true;

   public OseeGroup(String groupName) {
      this.groupName = groupName;
      this.isCurrentUserAMember = false;
      this.groupArtifact = null;
      this.groupArtifact = null;
   }

   /**
    * @return Returns the group.
    * @throws OseeCoreException
    */
   public Artifact getGroupArtifact() throws OseeCoreException {
      checkGroupExists();
      return groupArtifact;
   }

   /**
    * This does not persist the newly created relation that is the callers responsibility.
    * 
    * @param user
    */
   public void addMember(User user) throws OseeCoreException {
      getGroupArtifact().addRelation(CoreRelationEnumeration.Users_User, user);
   }

   /**
    * Determines whether the user is a member of this group
    * 
    * @param user to check
    * @return whether the user is a member of this group
    * @throws OseeCoreException
    */
   public boolean isMember(User user) throws OseeCoreException {
      return getGroupArtifact().isRelated(CoreRelationEnumeration.Users_User, user);
   }

   /**
    * Determines whether the current user is a member of this group
    * 
    * @return whether the current user is a member of this group
    * @throws OseeCoreException
    */
   public boolean isCurrentUserMember() throws OseeCoreException {
      if (isCheckAllowed) {
         isCurrentUserAMember = isMember(UserManager.getUser());
         isCheckAllowed = false;
      }
      return isCurrentUserAMember;
   }

   private void checkGroupExists() throws OseeCoreException {
      if (groupArtifact == null) {
         groupArtifact = getOrCreateGroupArtifact(groupName);
      }
   }

   private Artifact getOrCreateGroupArtifact(String groupName) throws OseeCoreException {
      Branch commonBranch = BranchManager.getCommonBranch();
      List<Artifact> artifacts =
            ArtifactQuery.getArtifactsFromTypeAndName(GROUP_ARTIFACT_TYPE, groupName, commonBranch);
      Artifact groupArtifact;

      if (artifacts.isEmpty()) {
         Artifact userGroupsFolder = getOrCreateUserGroupsFolder(commonBranch);
         groupArtifact = ArtifactTypeManager.addArtifact(GROUP_ARTIFACT_TYPE, commonBranch, groupName);
         userGroupsFolder.addChild(groupArtifact);
      } else {
         groupArtifact = artifacts.get(0);
      }
      return groupArtifact;
   }

   private Artifact getOrCreateUserGroupsFolder(Branch branch) throws OseeCoreException {
      Artifact userGroups = null;
      Artifact root = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(branch);
      if (root.hasChild("User Groups")) {
         userGroups = root.getChild("User Groups");
      } else {
         userGroups = ArtifactTypeManager.addArtifact("Folder", branch, "User Groups");
         root.addChild(userGroups);
      }
      return userGroups;
   }
}
