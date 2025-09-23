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
import org.eclipse.osee.framework.core.data.BranchService;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.access.BranchServiceImpl;
import org.eclipse.osee.framework.skynet.core.access.UserServiceImpl;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * @author Donald G. Dunne
 */
public class OseeApiService {

   private static OseeApi oseeApi;
   private static UserService userService;
   private static BranchService branchService;

   private OseeApiService() {
      // for Jax-Rs
   }

   public static OseeApi get() {
      if (oseeApi == null) {
         oseeApi = ServiceUtil.getOseeApi();
      }
      return oseeApi;
   }

   public static UserService userSvc() {
      if (userService == null) {
         userService = UserServiceImpl.getInstance();
      }
      return userService;
   }

   public static UserToken user() {
      return userSvc().getCurrentUser();
   }

   /**
    * @deprecated - userArt will be removed; use user() instead
    */
   @Deprecated
   public static Artifact userArt() {
      return ArtifactQuery.getArtifactFromId(user(), CoreBranches.COMMON);
   }

   public static BranchService branchSvc() {
      if (branchService == null) {
         branchService = BranchServiceImpl.getInstance();
      }
      return branchService;
   }

}