/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.access;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.OseeUser;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.AbstractUserGroupImpl;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class UserGroupImpl extends AbstractUserGroupImpl {

   public UserGroupImpl(ArtifactToken userGroupArt) {
      super(userGroupArt);
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return CoreArtifactTypes.UserGroup;
   }

   @Override
   public Artifact getArtifact() {
      checkGroupExists();
      if (groupArtifact instanceof Artifact) {
         return (Artifact) groupArtifact;
      }
      groupArtifact = ArtifactQuery.getArtifactFromId(groupArtifact, COMMON);
      return (Artifact) groupArtifact;
   }

   @Override
   public boolean addMember(UserId user, boolean persist) {
      checkGroupExists();
      Artifact userArt = null;
      if (user instanceof Artifact) {
         userArt = (Artifact) user;
      } else {
         userArt = ArtifactQuery.getArtifactFromId(user, COMMON);
      }
      Artifact group = getArtifact();
      if (!group.isRelated(CoreRelationTypes.Users_User, userArt)) {
         group.addRelation(CoreRelationTypes.Users_User, userArt);
         if (persist) {
            group.persist("Add Member");
         }
         return true;
      }
      return false;
   }

   @Override
   public boolean isMember(UserId userId) {
      checkGroupExists();
      OseeUser user = OseeApiService.user();
      return getArtifact().isRelated(CoreRelationTypes.Users_User, user);
   }

   @Override
   public boolean isMember(Long id) {
      checkGroupExists();
      for (RelationLink rel : getArtifact().getRelations(CoreRelationTypes.Users_User)) {
         if (rel.getArtifactIdB().equals(id)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isCurrentUserMember() {
      checkGroupExists();
      return isMember(OseeApiService.user());
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

   private Artifact getOrCreateUserGroupsFolder(BranchToken branch) {
      SkynetTransaction tx = TransactionManager.createTransaction(CoreBranches.COMMON, "Create UserGroups Folder");
      Artifact usersGroupFolder = ArtifactQuery.checkArtifactFromId(CoreArtifactTokens.UserGroups, branch);
      if (usersGroupFolder == null) {
         Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
         Artifact oseeConfig = ArtifactQuery.checkArtifactFromId(CoreArtifactTokens.OseeConfiguration, branch);
         if (oseeConfig == null) {
            oseeConfig = ArtifactTypeManager.addArtifact(CoreArtifactTokens.OseeConfiguration);
            root.addChild(oseeConfig);
            tx.addArtifact(oseeConfig);
         }
         usersGroupFolder = ArtifactTypeManager.addArtifact(CoreArtifactTokens.UserGroups, branch);
         usersGroupFolder.persist(tx);
         oseeConfig.addChild(usersGroupFolder);
      }
      return usersGroupFolder;
   }

   @Override
   public boolean removeMember(UserId user, boolean persist) {
      checkGroupExists();
      Artifact userArt = null;
      if (user instanceof Artifact) {
         userArt = (Artifact) user;
      } else {
         userArt = ArtifactQuery.getArtifactFromId(user, COMMON);
      }
      Artifact group = getArtifact();
      if (group.isRelated(CoreRelationTypes.Users_User, userArt)) {
         group.deleteRelation(CoreRelationTypes.Users_User, userArt);
         if (persist) {
            group.persist("Remove Member");
         }
         return true;
      }
      return false;
   }

   @Override
   public Collection<UserToken> getMembers() {
      checkGroupExists();
      return Collections.castAll(getArtifact().getRelatedArtifacts(CoreRelationTypes.Users_User));
   }

   @Override
   public BranchToken getBranch() {
      return groupArtifact.getBranch();
   }

}