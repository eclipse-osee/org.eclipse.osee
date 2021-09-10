/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access.operation;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public class UserGroupOperations {

   private final AccessCache cache;
   private final IAccessControlService accessService;
   private final UserService userService;

   public UserGroupOperations(AccessCache cache, IAccessControlService accessService, UserService userService) {
      this.cache = cache;
      this.accessService = accessService;
      this.userService = userService;
   }

   public void populateUserGroupList() {
      for (ArtifactToken userGroupArt : accessService.getArtifactListFromType(CoreArtifactTypes.UserGroup, COMMON)) {
         IUserGroup userGroup = userService.getUserGroup(userGroupArt);
         cache.idToUserGroup.put(userGroup.getId(), userGroup);
      }
   }

   public void populateGroupMembersEntry(ArtifactId groupId, ArtifactId groupMember) {
      cache.subjectToGroupCache.put(groupMember.getId(), groupId);
      cache.groupToSubjectsCache.put(groupId.getId(), groupMember);
   }
}