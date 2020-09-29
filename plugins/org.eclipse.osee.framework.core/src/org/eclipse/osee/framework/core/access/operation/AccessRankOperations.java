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

import org.eclipse.osee.framework.core.access.object.AccessObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;

/**
 * @author Donald G. Dunne
 */
public class AccessRankOperations {

   private final AccessCache cache;

   public AccessRankOperations(AccessCache cache) {
      this.cache = cache;
   }

   public PermissionEnum acquirePermissionRank(ArtifactToken subject, AccessObject accessObject) {
      PermissionEnum userPermission = cache.accessControlListCache.get(subject.getId(), accessObject);
      if (cache.subjectToGroupCache.containsKey(subject.getId())) {
         for (ArtifactId groupPermissionId : cache.subjectToGroupCache.getValues(subject.getId())) {
            PermissionEnum groupPermission = cache.accessControlListCache.get(groupPermissionId.getId(), accessObject);

            if (groupPermission != null) {
               if (userPermission == null) {
                  userPermission = groupPermission;
               } else if (groupPermission.getRank() > userPermission.getRank()) {
                  userPermission = groupPermission;
               }
            }
         }
      }
      // user does not have entry in the branch access control table for this branch
      if (userPermission == null) {
         // If there are any other access on this branch, it's locked for this user
         if (cache.objectToSubjectCache.containsKey(accessObject)) {
            userPermission = PermissionEnum.DENY;
         } else {
            userPermission = PermissionEnum.FULLACCESS;
         }
      }

      return userPermission;
   }

}
