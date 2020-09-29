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

package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserGroupArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class CoreUserGroups {

   public static final IUserGroupArtifactToken Everyone = UserGroupArtifactToken.valueOf(48656L, "Everyone");
   public static final IUserGroupArtifactToken OseeAccessAdmin =
      UserGroupArtifactToken.valueOf(8033605L, "Osee Access Admin");
   public static final IUserGroupArtifactToken OseeAdmin = UserGroupArtifactToken.valueOf(52247L, "OseeAdmin");
   public static final IUserGroupArtifactToken OseeDeveloper =
      UserGroupArtifactToken.valueOf(464565465L, "Osee Developer");
   public static final IUserGroupArtifactToken OseeSupport = UserGroupArtifactToken.valueOf(10865894L, "Osee Support");
   public static IUserGroupArtifactToken AgileUser = UserGroupArtifactToken.valueOf(10635635L, "Agile User");
   public static IUserGroupArtifactToken EarnedValueUser =
      UserGroupArtifactToken.valueOf(10635662L, "Earner Value User");
   public static IUserGroupArtifactToken DefaultArtifactEditor =
      UserGroupArtifactToken.valueOf(10862351L, "Deault Artifact Editor");
}
