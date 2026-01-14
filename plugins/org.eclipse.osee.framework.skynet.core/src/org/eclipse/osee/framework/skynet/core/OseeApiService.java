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
import org.eclipse.osee.framework.core.client.IdeServerEndpoints;
import org.eclipse.osee.framework.core.client.IdeServerEndpointsImpl;
import org.eclipse.osee.framework.core.data.BranchService;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.access.BranchServiceImpl;
import org.eclipse.osee.framework.skynet.core.access.UserServiceImpl;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.utility.KeyValueServiceImpl;
import org.eclipse.osee.orcs.utility.KeyValueService;

/**
 * New API Utility class. NOTHING IN THIS SHOULD TALK DIRECTLY TO THE DB. Other services should be moved here as theya
 * are removed from db access and only use server REST calls.
 *
 * @author Donald G. Dunne
 */
public class OseeApiService {

   private static OseeApi oseeApi;
   private static UserService userService;
   private static BranchService branchService;
   private static KeyValueService keyValueService;
   private static IdeServerEndpoints serverEndpoints;

   private OseeApiService() {
      // for Jax-Rs
   }

   public static OseeApi get() {
      if (oseeApi == null) {
         oseeApi = ServiceUtil.getOseeApi();
      }
      return oseeApi;
   }

   /**
    * IDE client entry point to server endpoints. In most cases, other client services (available here and in OseeApi)
    * should be used and they should call endpoints as necessary. They should also handle reloading artifacts, events
    * and etc that are needed in calling endpoints.
    */
   public static IdeServerEndpoints serverEnpoints() {
      if (serverEndpoints == null) {
         serverEndpoints = new IdeServerEndpointsImpl();
      }
      return serverEndpoints;
   }

   public static KeyValueService keyValueSvc() {
      if (keyValueService == null) {
         keyValueService = KeyValueServiceImpl.getInstance();
      }
      return keyValueService;
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