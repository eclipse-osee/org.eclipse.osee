/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.orcs.core.internal.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.AbstractUserGroupImpl;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
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
   public ArtifactReadable getArtifact() {
      checkGroupExists();
      if (groupArtifact instanceof ArtifactReadable) {
         return (ArtifactReadable) groupArtifact;
      }
      return null;
   }

   @Override
   public boolean addMember(UserId user, boolean persist) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isMember(UserId user) {
      checkGroupExists();
      Conditions.assertTrue(user instanceof ArtifactReadable, "User must be artifact");
      return getArtifact().areRelated(CoreRelationTypes.Users_User, (ArtifactReadable) user);
   }

   @Override
   public boolean isMember(Long id) {
      checkGroupExists();
      for (IRelationLink rel : getArtifact().getRelations(CoreRelationTypes.Users_User)) {
         if (rel.getArtifactIdB().equals(ArtifactId.valueOf(id))) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isCurrentUserMember() {
      throw new UnsupportedOperationException();
   }

   @Override
   protected ArtifactReadable getOrCreateGroupArtifact(ArtifactToken token) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeMember(UserId user, boolean persist) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<UserToken> getMembers() {
      checkGroupExists();
      List<UserToken> users = new ArrayList<UserToken>();
      for (ArtifactReadable userArt : getArtifact().getRelated(CoreRelationTypes.Users_User).getList()) {
         String name = userArt.getName();
         String email = userArt.getSoleAttributeValue(CoreAttributeTypes.Email, "");
         String userId = userArt.getSoleAttributeValue(CoreAttributeTypes.UserId);
         boolean active = userArt.getSoleAttributeValue(CoreAttributeTypes.Active);
         List<IUserGroupArtifactToken> roles = new ArrayList<IUserGroupArtifactToken>();
         for (ArtifactReadable userGroupArt : userArt.getRelated(CoreRelationTypes.Users_Artifact).getList()) {
            IUserGroupArtifactToken userGroup =
               UserGroupArtifactToken.valueOf(userGroupArt.getId(), userGroupArt.getName());
            roles.add(userGroup);
         }
         UserToken userToken = UserToken.create(userArt.getId(), name, email, userId, active, roles);
         users.add(userToken);
      }
      return users;
   }

   @Override
   public BranchToken getBranch() {
      return getArtifact().getBranch();
   }
}
