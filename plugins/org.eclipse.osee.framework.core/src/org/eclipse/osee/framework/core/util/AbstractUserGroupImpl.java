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

package org.eclipse.osee.framework.core.util;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractUserGroupImpl extends BaseId implements IUserGroup, IUserGroupArtifactToken {

   protected ArtifactToken groupArtifact;

   public AbstractUserGroupImpl(ArtifactToken userGroupArt) {
      super(userGroupArt);
      this.groupArtifact = userGroupArt;
   }

   @Override
   public ArtifactToken getArtifact() {
      checkGroupExists();
      return groupArtifact;
   }

   protected void checkGroupExists() {
      if (groupArtifact == null) {
         groupArtifact = getOrCreateGroupArtifact(groupArtifact);
      }
   }

   @Override
   public String toString() {
      return "UserGroup: " + getArtifact().toStringWithId();
   }

   protected abstract ArtifactToken getOrCreateGroupArtifact(ArtifactToken groupArtifact2);

   @Override
   public Collection<String> getActiveMemberEmails() {
      ArrayList<String> toUserEmailList = new ArrayList<String>();
      for (UserToken userTok : getMembers()) {
         if (userTok.isActive()) {
            String userEmail = userTok.getEmail();
            if (EmailUtil.isEmailValid(userEmail)) {
               toUserEmailList.add(userEmail);
            }
         }
      }
      return toUserEmailList;
   }

}