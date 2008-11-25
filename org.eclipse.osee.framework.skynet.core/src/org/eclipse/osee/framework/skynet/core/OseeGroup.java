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

import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
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
   private static final String FOLDER_ARTIFACT_TYPE = "Folder";
   private static final String USERS_GROUP_FOLDER_NAME = "User Groups";

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
      Branch branch = BranchManager.getCommonBranch();
      String cacheKey = GROUP_ARTIFACT_TYPE + "." + groupName;
      Artifact groupArtifact = null;
      try {
         groupArtifact = ArtifactCache.getByTextId(cacheKey, branch);
         if (groupArtifact == null) {
            groupArtifact = ArtifactQuery.getArtifactFromTypeAndName(GROUP_ARTIFACT_TYPE, groupName, branch);
            ArtifactCache.putByTextId(cacheKey, groupArtifact);
         }
      } catch (ArtifactDoesNotExist ex) {
         Artifact userGroupsFolder = getOrCreateUserGroupsFolder(branch);
         groupArtifact = ArtifactTypeManager.addArtifact(GROUP_ARTIFACT_TYPE, branch, groupName);
         ArtifactCache.putByTextId(cacheKey, groupArtifact);
         userGroupsFolder.addChild(groupArtifact);
      }
      return groupArtifact;
   }

   private Artifact getOrCreateUserGroupsFolder(Branch branch) throws OseeCoreException {
      String cacheKey = FOLDER_ARTIFACT_TYPE + "." + USERS_GROUP_FOLDER_NAME;
      Artifact usersGroupFolder = null;
      try {
         usersGroupFolder = ArtifactCache.getByTextId(cacheKey, branch);
         if (usersGroupFolder == null) {
            usersGroupFolder =
                  ArtifactQuery.getArtifactFromTypeAndName(FOLDER_ARTIFACT_TYPE, USERS_GROUP_FOLDER_NAME, branch);
            ArtifactCache.putByTextId(cacheKey, usersGroupFolder);
         }
      } catch (ArtifactDoesNotExist ex) {
         Artifact root = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(branch);
         if (root.hasChild(USERS_GROUP_FOLDER_NAME)) {
            usersGroupFolder = root.getChild(USERS_GROUP_FOLDER_NAME);
         } else {
            usersGroupFolder = ArtifactTypeManager.addArtifact(FOLDER_ARTIFACT_TYPE, branch, USERS_GROUP_FOLDER_NAME);
            root.addChild(usersGroupFolder);
         }
      }
      return usersGroupFolder;
   }
}
