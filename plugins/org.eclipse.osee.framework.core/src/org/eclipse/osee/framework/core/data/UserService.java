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
import org.eclipse.osee.framework.core.ApiKeyApi;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;

/**
 * @author Donald G. Dunne
 */
public interface UserService {

   void clearCaches();

   TransactionId createUsers(Iterable<UserToken> users, String comment);

   TransactionId createUsers(Iterable<UserToken> users, UserToken superUser, String string);

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

   /**
    * This method should only be invoked on the client and never by applications.
    *
    * @return the UserToken if it has already been loaded; otherwise <code>UserToken.SENTINEL</code>
    */
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

   void removeUserFromCurrentThread();

   void setUserLoading(boolean loading);

   default IUserGroup getUserGroupOrSentinel(IUserGroupArtifactToken userGroup) {
      IUserGroup userGroupOrNull = getUserGroupOrNull(userGroup);
      if (userGroupOrNull == null) {
         return IUserGroup.SENTINEL;
      }
      return userGroupOrNull;
   }

   // @formatter:off
   /**
    * Sets the user for current thread using either a login ID or an API key.
    * <p>
    * This method first checks the cache for a user associated with the provided credential.
    * If the user is not found in the cache, it queries the database and updates the cache.
    * </p>
    * <p>
    * This method supports two authentication mechanisms:
    * <ul>
    *   <li>Login ID-based authentication</li>
    *   <li>API key-based authentication</li>
    * </ul>
    * </p>
    * <p>
    * <strong>Note:</strong> When login ID-based authentication is deprecated, the related code should be removed.
    * </p>
    *
    * @param credential The login ID or API key used to identify the user.
    * @param apiKeyApi An instance of ApiKeyApi to retrieve API keys.
    */
   // @formatter:on
   void setUserFromBasic(String credential, ApiKeyApi apiKeyApi);

   default UserId getUserOrSystem() {
      UserId user = getUser();
      if (user.isInvalid()) {
         return SystemUser.OseeSystem;
      }
      return user;
   }

   default UserId getUserOrSystem(Long accountId) {
      UserId user = getUser(accountId);
      if (user.isInvalid()) {
         return SystemUser.OseeSystem;
      }
      return user;
   }

}

/* EOF */
