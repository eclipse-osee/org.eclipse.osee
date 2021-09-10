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

package org.eclipse.osee.framework.core.data;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;

/**
 * @author Donald G. Dunne
 */
public interface UserService {

   IUserGroup getUserGroup(IUserGroupArtifactToken userGroup);

   Collection<IUserGroupArtifactToken> getMyUserGroups();

   boolean isInUserGroup(IUserGroupArtifactToken... userGroups);

   /**
    * Checks for existence of user group, then if member
    */
   boolean isUserMember(IUserGroupArtifactToken userGroup, Long id);

   boolean isUserMember(IUserGroupArtifactToken userGroup, ArtifactId user);

   Collection<UserToken> getUsers(IUserGroupArtifactToken userGroup);

   IUserGroup getUserGroup(ArtifactToken userGroupArt);

   UserToken getUser();

   default UserToken getUserIfLoaded() {
      return UserToken.SENTINEL;
   }

   default void setUserLoading(boolean loading) {
      // client implementation must override
   }

   /**
    * Must ensure this is only called by legitimate ORCS system
    */
   void setUserForCurrentThread(String loginId);

   TransactionId createUsers(Iterable<UserToken> users, String comment);

   void setUserForCurrentThread(UserId accountId);

   default IUserGroup getOseeAdmin() {
      return getUserGroup(CoreUserGroups.OseeAdmin);
   }

   default IUserGroup getOseeAccessAdmin() {
      return getUserGroup(CoreUserGroups.OseeAccessAdmin);
   }
}