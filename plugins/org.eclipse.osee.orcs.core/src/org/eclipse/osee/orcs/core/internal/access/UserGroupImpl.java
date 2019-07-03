/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.AbstractUserGroupImpl;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.RelationReadable;

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
   public boolean addMember(UserId user) {
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
      for (RelationReadable rel : getArtifact().getRelations(CoreRelationTypes.Users_User)) {
         if (rel.getArtIdB() == id.intValue()) {
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
   public boolean removeMember(UserId user) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<UserToken> getMembers() {
      checkGroupExists();
      return Collections.castAll(getArtifact().getRelated(CoreRelationTypes.Users_User).getList());
   }

   @Override
   public BranchId getBranch() {
      return getArtifact().getBranch();
   }
}