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

package org.eclipse.osee.ats.api.data;

import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserGroupArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class AtsUserGroups {

   public static IUserGroupArtifactToken AtsAddAttrColumns =
      UserGroupArtifactToken.valueOf(10847751L, "ATS Add Attr Columns");
   public static IUserGroupArtifactToken AtsAdmin = UserGroupArtifactToken.valueOf(136750L, "ATS Admin");
   public static IUserGroupArtifactToken AtsTempAdmin = UserGroupArtifactToken.valueOf(5367074L, "ATSTemp Admin");
   public static IUserGroupArtifactToken AtsDeleteWorkflowAdmin =
      UserGroupArtifactToken.valueOf(11200216L, "ATS Delete Workflow Admin");
   public static IUserGroupArtifactToken peerReviewCreationNotify =
      UserGroupArtifactToken.valueOf(8742803L, "Peer Review Creation");
}
