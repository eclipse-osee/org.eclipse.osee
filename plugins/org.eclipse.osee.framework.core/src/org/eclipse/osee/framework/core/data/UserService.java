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
import java.util.Objects;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;

/**
 * @author Donald G. Dunne
 */
public interface UserService {

   void clearCaches();

   TransactionId createUsers(Iterable<UserToken> users, String comment);

   String getLoginKey();

   /**
    * Gets a {@link Collection} of {@link IUserGroupArtifactToken} objects for the groups the current thread user
    * belongs to.
    *
    * @return the user groups that the current user belongs to
    */

   Collection<IUserGroupArtifactToken> getMyUserGroups();

   default IUserGroup getOseeAccessAdmin() {
      return getUserGroup(CoreUserGroups.OseeAccessAdmin);
   }

   default IUserGroup getOseeAdmin() {
      return getUserGroup(CoreUserGroups.OseeAdmin);
   }

   UserToken getUser();

   UserToken getUser(Long accountId);

   /**
    * @return the UserToken for the user whose org.eclipse.osee.framework.core.enums.CoreAttributeTypes.UserId attribute
    * equals the userId parameter
    */

   UserToken getUserByUserId(String userId);

   IUserGroup getUserGroup(ArtifactToken userGroupArt);

   IUserGroup getUserGroup(IUserGroupArtifactToken userGroup);

   IUserGroup getUserGroupOrNull(IUserGroupArtifactToken userGroup);

   UserToken getUserIfLoaded();

   UserToken getUserIfLoaded(Long accountId);

   Collection<UserToken> getUsers(IUserGroupArtifactToken userGroup);

   /**
    * Predicate to determine if the user is active and has at least one login identifier.
    *
    * @return <code>true</code>, when the user is active with at least one login identifier; otherwise,
    * <code>false</code>.
    */

   default boolean isActiveLoginUser() {
      return this.isActiveUser() && this.isLoginUser();
   }

   /**
    * Predicate to determine if the user is active.
    *
    * @return <code>true</code>, when the user is active; otherwise, <code>false</code>.
    */

   default boolean isActiveUser() {

      try {

         var userToken = this.getUser();

         if (Objects.isNull(userToken)) {
            return false;
         }

         return userToken.isActive();

      } catch (Exception e) {

         return false;

      }
   }

   boolean isBeforeUserCreation();

   default boolean isInUserGroup(IUserGroupArtifactToken... userGroups) {
      Collection<IUserGroupArtifactToken> myUserGroups = this.getMyUserGroups();
      for (IUserGroupArtifactToken userGrp : userGroups) {
         if (myUserGroups.contains(userGrp)) {
            return true;
         }
      }
      return false;
   }

   /**
    * Predicate to determine if the user has at least one login identifier.
    *
    * @return <code>true</code>, when the user has at least one login identifier; otherwise <code>false</code>.
    */

   default boolean isLoginUser() {

      try {

         var userToken = this.getUser();

         if (Objects.isNull(userToken)) {
            return false;
         }

         var loginIdsList = userToken.getLoginIds();

         if (Objects.isNull(loginIdsList)) {
            return false;
         }

         if (loginIdsList.size() < 1) {
            return false;
         }

         return true;

      } catch (Exception e) {

         return false;

      }
   }

   default boolean isUserMember(IUserGroupArtifactToken userGroup, ArtifactId user) {
      return isUserMember(userGroup, user.getId());
   }

   /**
    * Checks for existence of user group, then if member
    */

   boolean isUserMember(IUserGroupArtifactToken userGroup, Long id);

   /**
    * Determines if the current thread's user is an active user with at least one login id.
    *
    * @throws OseeAccessDeniedException when the current thread's user is not active or does not have a login id.
    */

   default void requireActiveLoginUser() {
      if (!this.isActiveLoginUser()) {
         throw new OseeAccessDeniedException("User %s is not an active login user", this.getUser().toStringWithId());
      }
   }

   /**
    * Determines if the current thread's user is an active user.
    *
    * @throws OseeAccessDeniedException when the current thread's user is not active.
    */

   default void requireActiveUser() {
      if (!this.isActiveUser()) {
         throw new OseeAccessDeniedException("User %s is not an active user", this.getUser().toStringWithId());
      }
   }

   /**
    * Determines if the current thread's user has at least one login id.
    *
    * @throws OseeAccessDeniedException when the current thread's user does not have a login id.
    */

   default void requireLoginUser() {
      if (!this.isLoginUser()) {
         throw new OseeAccessDeniedException("User %s is not a login user", this.getUser().toStringWithId());
      }
   }

   /**
    * Determines if the current thread's user is in at least one of the given groups. Otherwise throws
    * OseeAccessDeniedException. In order to require multiple roles (rather than at least one) call this method once for
    * each such role.
    */

   default void requireRole(IUserGroupArtifactToken... userGroups) throws OseeAccessDeniedException {
      Collection<IUserGroupArtifactToken> roles = this.getMyUserGroups();

      for (IUserGroupArtifactToken userGroup : userGroups) {
         if (roles.contains(userGroup)) {
            return;
         }
      }

      throw new OseeAccessDeniedException("User %s is not in any of the user groups %s",
         this.getUser().toStringWithId(), Arrays.deepToString(userGroups));
   }

   void setBeforeUserCreation(boolean beforeUserCreation);

   /**
    * Must ensure this is only called by legitimate ORCS system
    */

   void setUserForCurrentThread(String loginId);

   void setUserForCurrentThread(UserId accountId);

   void setUserLoading(boolean loading);

}

/* EOF */
