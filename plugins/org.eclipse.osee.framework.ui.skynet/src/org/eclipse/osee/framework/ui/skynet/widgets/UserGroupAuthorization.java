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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.access.UserGroupImpl;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class UserGroupAuthorization {

   public UserGroupAuthorization() {
      // Util class
   }

   public static XResultData hasUserGroupAuthorization(IUserGroupArtifactToken userGroup, String label,
      XResultData rd) {
      if (userGroup.getId() > 0) {
         Artifact userGroupArt =
            ArtifactQuery.getArtifactOrNull(userGroup, CoreBranches.COMMON, DeletionFlag.EXCLUDE_DELETED);
         IUserGroup group = null;
         if (userGroupArt != null) {
            group = new UserGroupImpl(userGroupArt);
         }
         if (group != null && group.getId() > 0) {
            if (!group.isMember(OseeApiService.user())) {
               StringBuilder sb = new StringBuilder();
               sb.append(String.format("You are not authorized to sign [%s].", label));
               sb.append("\n\nAuthorized Users Are:\n-----------------------------\n");
               for (UserToken member : group.getMembers()) {
                  sb.append(member.getName());
                  sb.append("\n");
               }
               rd.error(sb.toString());
            }
         } else {
            rd.errorf("Authorization User Group %s Not Found", userGroup.toStringWithId());
         }
      }
      return rd;
   }

}
