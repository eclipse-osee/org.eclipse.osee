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

package org.eclipse.osee.ats.ide.world.search;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Return all ATS Objects that a user is related to through logs, review roles, defects and etc.
 *
 * @author Donald G. Dunne
 */
public class UserRelatedToAtsObjectSearch extends UserSearchItem {

   private final boolean activeObjectsOnly;

   public UserRelatedToAtsObjectSearch(String name, AtsUser user, boolean activeObjectsOnly, LoadView loadView) {
      super(name, user);
      this.activeObjectsOnly = activeObjectsOnly;
      setLoadView(loadView);
      setActive(Active.Both);
   }

   public UserRelatedToAtsObjectSearch(UserRelatedToAtsObjectSearch userRelatedToAtsObjectSearch) {
      super(userRelatedToAtsObjectSearch);
      this.activeObjectsOnly = userRelatedToAtsObjectSearch.activeObjectsOnly;
      setActive(Active.Both);
   }

   @Override
   protected Collection<Artifact> searchIt(AtsUser atsUser) {
      // SMA having user as portion of current state attribute (Team WorkFlow and Task)

      if (isCancelled()) {
         return EMPTY_SET;
      }

      AtsApi atsApi = AtsApiService.get();
      List<ArtifactToken> arts = new ArrayList<>();
      arts.addAll(atsApi.getQueryService().getArtifactsFromAttributeKeywords(atsApi.getAtsBranch(),
         atsUser.getArtifactId().getIdString(), false, EXCLUDE_DELETED, false, AtsAttributeTypes.CurrentStateAssignee));
      if (!activeObjectsOnly) {
         arts.addAll(ArtifactQuery.getArtifactListFromAttributeKeywords(AtsApiService.get().getAtsBranch(),
            user.getUserId(), false, EXCLUDE_DELETED, false, AtsAttributeTypes.Log));
      }

      arts.addAll(
         AtsApiService.get().getRelationResolver().getRelated((IAtsObject) user, AtsRelationTypes.TeamLead_Team));
      arts.addAll(
         AtsApiService.get().getRelationResolver().getRelated((IAtsObject) user, AtsRelationTypes.TeamMember_Team));
      arts.addAll(AtsApiService.get().getRelationResolver().getRelated((IAtsObject) user,
         AtsRelationTypes.FavoriteUser_Artifact));
      arts.addAll(AtsApiService.get().getRelationResolver().getRelated((IAtsObject) user,
         AtsRelationTypes.SubscribedUser_Artifact));

      if (isCancelled()) {
         return EMPTY_SET;
      }
      return Collections.castAll(arts);
   }

   @Override
   public WorldUISearchItem copy() {
      return new UserRelatedToAtsObjectSearch(this);
   }

}
