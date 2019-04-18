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
package org.eclipse.osee.framework.skynet.core.access;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.AbstractUserGroupImpl;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class UserGroupImpl extends AbstractUserGroupImpl {

   public UserGroupImpl(ArtifactToken userGroupArt) {
      super(userGroupArt);
   }

   @Override
   public Artifact getArtifact() {
      checkGroupExists();
      if (groupArtifact instanceof Artifact) {
         return (Artifact) groupArtifact;
      }
      return null;
   }

   @Override
   public void addMember(UserId user) {
      checkGroupExists();
      Conditions.assertTrue(user instanceof Artifact, "User must be artifact");
      getArtifact().addRelation(CoreRelationTypes.Users_User, (Artifact) user);
   }

   @Override
   public boolean isMember(UserId user) {
      checkGroupExists();
      Conditions.assertTrue(user instanceof Artifact, "User must be artifact");
      return getArtifact().isRelated(CoreRelationTypes.Users_User, (Artifact) user);
   }

   @Override
   public boolean isCurrentUserMember() {
      checkGroupExists();
      return isMember(UserManager.getUser());
   }

   @Override
   protected Artifact getOrCreateGroupArtifact(ArtifactToken token) {
      groupArtifact = ArtifactQuery.getArtifactOrNull(token, EXCLUDE_DELETED);
      if (groupArtifact == null) {
         Artifact userGroupsFolder = getOrCreateUserGroupsFolder(COMMON);
         groupArtifact = ArtifactTypeManager.addArtifact(token, COMMON);
         userGroupsFolder.addChild(getArtifact());
      }
      return getArtifact();
   }

   private Artifact getOrCreateUserGroupsFolder(BranchId branch) {
      Artifact usersGroupFolder = ArtifactQuery.checkArtifactFromId(CoreArtifactTokens.UserGroups, branch);
      if (usersGroupFolder == null) {
         Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
         if (root.hasChild(CoreArtifactTokens.UserGroups.getName())) {
            usersGroupFolder = root.getChild(CoreArtifactTokens.UserGroups.getName());
         } else {
            usersGroupFolder = ArtifactTypeManager.addArtifact(CoreArtifactTokens.UserGroups, branch);
            root.addChild(usersGroupFolder);
         }
      }
      return usersGroupFolder;
   }

   @Override
   public void removeMember(UserId user) {
      checkGroupExists();
      Conditions.assertTrue(user instanceof Artifact, "User must be artifact");
      getArtifact().deleteRelation(CoreRelationTypes.Users_User, (Artifact) user);
   }

   @Override
   public Collection<UserToken> getMembers() {
      checkGroupExists();
      return Collections.castAll(getArtifact().getRelatedArtifacts(CoreRelationTypes.Users_User));
   }
}