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
package org.eclipse.osee.framework.skynet.core.access;

import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroupService;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class UserGroupService implements IUserGroupService {

   private static UserGroupService userGroupService;

   public static IUserGroup getOseeAdmin() {
      return get(CoreArtifactTokens.OseeAdmin);
   }

   public static IUserGroup getOseeAccessAdmin() {
      return get(CoreArtifactTokens.OseeAccessAdmin);
   }

   private static IUserGroup get(IUserGroupArtifactToken userGroupArtToken) {
      if (userGroupService == null) {
         userGroupService = new UserGroupService();
      }
      return userGroupService.getUserGroup(userGroupArtToken);
   }

   @Override
   public IUserGroup getUserGroup(IUserGroupArtifactToken userGroup) {
      Artifact userGroupArt = null;
      if (userGroup instanceof Artifact) {
         userGroupArt = (Artifact) userGroup;
      }
      if (userGroupArt == null) {
         userGroupArt = ArtifactQuery.getArtifactFromId(userGroup, CoreBranches.COMMON);
      }
      if (userGroupArt != null) {
         return new UserGroupImpl(userGroupArt);
      } else {
         throw new OseeArgumentException("parameter must be artifact");
      }
   }

}
