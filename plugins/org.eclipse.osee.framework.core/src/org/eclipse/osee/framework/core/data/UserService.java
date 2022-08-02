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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;

/**
 * @author Donald G. Dunne
 */
public interface UserService {

   IUserGroup getUserGroup(IUserGroupArtifactToken userGroup);

   String getLoginKey();

   /**
    * @return the user groups that the current user belongs to
    */
   Collection<IUserGroupArtifactToken> getMyUserGroups();

   boolean isInUserGroup(IUserGroupArtifactToken... userGroups);

   /**
    * Checks for existence of user group, then if member
    */
   boolean isUserMember(IUserGroupArtifactToken userGroup, Long id);

   boolean isUserMember(IUserGroupArtifactToken userGroup, ArtifactId user);

   boolean isBeforeUserCreation();

   void setBeforeUserCreation(boolean beforeUserCreation);

   Collection<UserToken> getUsers(IUserGroupArtifactToken userGroup);

   IUserGroup getUserGroup(ArtifactToken userGroupArt);

   UserToken getUser();

   UserToken getUser(Long accountId);

   default UserToken getUserIfLoaded() {
      return UserToken.SENTINEL;
   }

   default UserToken getUserIfLoaded(Long accountId) {
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

   /**
    * Determines if the current thread's user is in at least one of the given groups. Otherwise throws
    * OseeAccessDeniedException. In order to require multiple roles (rather than at least one) call this method once for
    * each such role.
    */
   default void requireRole(IUserGroupArtifactToken... userGroups) throws OseeAccessDeniedException {
      UserToken user = getUser();
      Collection<IUserGroupArtifactToken> roles = user.getRoles();

      for (IUserGroupArtifactToken userGroup : userGroups) {
         if (roles.contains(userGroup)) {
            return;
         }
      }
      throw new OseeAccessDeniedException("User %s is not in any of the user groups %s", user.toStringWithId(),
         Arrays.deepToString(userGroups));
   }

   default void clearCaches() {
      // For extensions
   }

   /**
    * @return the UserToken for the user whose org.eclipse.osee.framework.core.enums.CoreAttributeTypes.UserId attribute
    * equals the userId parameter
    */
   UserToken getUserByUserId(String userId);

   IUserGroup getUserGroupOrNull(IUserGroupArtifactToken userGroup);
}