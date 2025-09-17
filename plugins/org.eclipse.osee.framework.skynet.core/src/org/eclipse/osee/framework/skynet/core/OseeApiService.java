/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.framework.skynet.core;

import org.eclipse.osee.framework.core.OseeApi;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.OseeUser;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserService2;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.skynet.core.access.UserServiceImpl2;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * @author Donald G. Dunne
 */
public class OseeApiService {

   private static OseeApi oseeApi;
   private static UserService2 userService2;
   private static UserService userService; // Legacy, to be removed

   private OseeApiService() {
      // for Jax-Rs
   }

   public static OseeApi get() {
      if (oseeApi == null) {
         oseeApi = ServiceUtil.getOseeApi();
      }
      return oseeApi;
   }

   public static UserService2 userSvc() {
      if (userService2 == null) {
         userService2 = UserServiceImpl2.getInstance();
      }
      return userService2;
   }

   public static UserService userServiceLegacy() {
      if (userService == null) {
         userService = OsgiUtil.getService(UserAdmin.class, OseeClient.class).userService();
      }
      return userService;

   }

   public static OseeUser user() {
      return userSvc().getCurrentUser();
   }

   public static User getUserArt() {
      return (User) userServiceLegacy().getCurrentUser();
   }

}