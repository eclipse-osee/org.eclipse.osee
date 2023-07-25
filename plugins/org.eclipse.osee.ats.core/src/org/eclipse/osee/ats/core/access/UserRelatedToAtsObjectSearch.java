/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.core.access;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * Return all ATS Objects that a user is related to through logs, review roles, defects and etc.
 *
 * @author Donald G. Dunne
 */
public class UserRelatedToAtsObjectSearch {

   private final boolean activeObjectsOnly;
   private final AtsUser atsUser;
   private final AtsApi atsApi;

   public UserRelatedToAtsObjectSearch(AtsUser user, boolean activeObjectsOnly, AtsApi atsApi) {
      this.atsUser = user;
      this.activeObjectsOnly = activeObjectsOnly;
      this.atsApi = atsApi;
   }

   public Collection<ArtifactToken> getResults() {
      List<ArtifactToken> arts = new ArrayList<>();

      arts.addAll(atsApi.getQueryService().getArtifactsFromAttributeKeywords(atsApi.getAtsBranch(),
         atsUser.getIdString(), false, EXCLUDE_DELETED, false, AtsAttributeTypes.CurrentStateAssignee));
      if (!activeObjectsOnly) {
         arts.addAll(atsApi.getQueryService().getArtifactsFromAttributeKeywords(atsApi.getAtsBranch(),
            atsUser.getUserId(), false, EXCLUDE_DELETED, false, AtsAttributeTypes.Log));
      }

      arts.addAll(atsApi.getRelationResolver().getRelatedArtifacts(atsUser, AtsRelationTypes.TeamLead_Team));
      arts.addAll(atsApi.getRelationResolver().getRelatedArtifacts(atsUser, AtsRelationTypes.TeamMember_Team));
      arts.addAll(atsApi.getRelationResolver().getRelatedArtifacts(atsUser, AtsRelationTypes.FavoriteUser_Artifact));
      arts.addAll(atsApi.getRelationResolver().getRelatedArtifacts(atsUser, AtsRelationTypes.SubscribedUser_Artifact));

      return arts;
   }

}
