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
package org.eclipse.osee.ats.core.access;

import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupService;

/**
 * @author Donald G. Dunne
 */
public class UserGroupService {

   private static IUserGroupService userGroupService;

   public static IUserGroupService get() {
      return userGroupService;
   }

   public void setUserGroupService(IUserGroupService userGroupService) {
      UserGroupService.userGroupService = userGroupService;
   }

   public static IUserGroup getAtsAdmin() {
      return userGroupService.getUserGroup(AtsArtifactToken.AtsAdmin);
   }

   public static IUserGroup getAtsTemmpAdmin() {
      return userGroupService.getUserGroup(AtsArtifactToken.AtsTempAdmin);
   }

}
