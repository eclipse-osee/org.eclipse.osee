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
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
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
    */
   public Artifact getGroupArtifact() {
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
    */
   public boolean isCurrentUserMember() {
      try {
         if (isCheckAllowed) {
            isCurrentUserAMember = isMember(UserManager.getUser());
            isCheckAllowed = false;
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.INFO, ex);
      }
      return isCurrentUserAMember;
   }

   private void checkGroupExists() {
      if (groupArtifact == null) {
         groupArtifact = getOrCreateGroupArtifact(groupName);
      }
   }

   private Artifact getOrCreateGroupArtifact(String groupName) {
      Artifact groupArtifact = null;
      try {
         Branch commonBranch = BranchManager.getCommonBranch();
         try {
            List<Artifact> artifacts =
                  ArtifactQuery.getArtifactsFromTypeAndName(GROUP_ARTIFACT_TYPE, groupName, commonBranch);
            if (!artifacts.isEmpty()) {
               groupArtifact = artifacts.get(0);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetActivator.class, Level.SEVERE, String.format("Osee group not found [%s]", groupName));
         }

         if (groupArtifact == null) {
            Artifact userGroupsFolder = getOrCreateUserGroupsFolder(commonBranch);
            if (userGroupsFolder != null) {
               groupArtifact = ArtifactTypeManager.addArtifact(GROUP_ARTIFACT_TYPE, commonBranch, groupName);
               userGroupsFolder.addChild(groupArtifact);
               userGroupsFolder.persistAttributesAndRelations();
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
      return groupArtifact;
   }

   private Artifact getOrCreateUserGroupsFolder(Branch branch) {
      Artifact userGroups = null;
      Artifact root = null;
      try {
         root = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(branch);
         if (root != null) {
            userGroups = root.getChild("User Groups");
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, String.format("Unable to find 'User Groups' folder on [%s]",
               branch.getBranchName()));
         if (root != null) {
            try {
               userGroups = ArtifactTypeManager.addArtifact("Folder", branch, "User Groups");
               root.addChild(userGroups);
               root.persistAttributesAndRelations();
            } catch (OseeCoreException ex1) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex1);
            }
         }
      }
      return userGroups;
   }
}
