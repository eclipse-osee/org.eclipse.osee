/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.rest.publishing;

import java.util.Objects;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Class for verifying the current thread user's permissions for access to publishing REST API endpoints.
 *
 * @author Loren K. Ashley
 */

public class PublishingPermissions {

   /**
    * Saves the single instance of the {@link PublishingPermissions} class.
    */

   private static PublishingPermissions instance = null;

   /**
    * Saves a handle to the server's {@link UserService}.
    */

   private final UserService userService;

   /**
    * Creates the single class instance.
    *
    * @param userService a handle to the server's {@link UserService}.
    */

   private PublishingPermissions(UserService userService) {
      this.userService = userService;
   }

   /**
    * If the {@link PublishingPermissions} single instance has not yet been created, it will be created.
    *
    * @param orcsApi A reference to the server's {@link OrcsApi}.
    * @throws NullPointerException when creating the instance and:
    * <ul>
    * <li>the parameter <code>orcsApi</code> is <code>null</code>, or</li>
    * <li>the {@link UserService} obtained from the <code>orcsApi</code> is <code>null</code>.</li>
    * </ul>
    */

   public static void create(OrcsApi orcsApi) {

      if (Objects.nonNull(PublishingPermissions.instance)) {
         return;
      }

      //@formatter:off
      var userService =
         Objects.requireNonNull
            (
               Objects.requireNonNull
                  (
                    orcsApi,
                    "PublishingPermissions::new, parameter \"orcsApi\" is null."
                  ).userService(),
               "PublishingPermissions::new, failed to get the \"UserService\" from the \"orcsApi\"."
            );
      //@formatter:on

      PublishingPermissions.instance = new PublishingPermissions(userService);
   }

   /**
    * Verifies the current thread user is authorized to access a publishing REST API by performing the following checks:
    * <ul>
    * <li>the user is a member of the publishing group,</li>
    * <li>the user is an active user, and</li>
    * <li>the user has at least one login identifier.</li>
    * </ul>
    *
    * @throws UserNoAuthorizedForPublishingException when any of the authorization checks fail.
    */

   public static void verify() {

      if (Objects.isNull(PublishingPermissions.instance)) {
         throw new IllegalStateException();
      }

      //@formatter:off
      int causeFlags =
           ( PublishingPermissions.instance.userService.isInUserGroup(CoreUserGroups.Publishing)
                ? 0
                : UserNotAuthorizedForPublishingException.NOT_IN_PUBLISHING_GROUP )
         + ( PublishingPermissions.instance.userService.isActiveUser()
                ? 0
                : UserNotAuthorizedForPublishingException.NOT_ACTIVE_USER )
         + ( PublishingPermissions.instance.userService.isLoginUser()
                ? 0
                : UserNotAuthorizedForPublishingException.NOT_LOGIN_USER );

      if (causeFlags > 0) {
         throw new UserNotAuthorizedForPublishingException(causeFlags,
            PublishingPermissions.instance.userService.getUser());
      }
   }
}

/* EOF */
