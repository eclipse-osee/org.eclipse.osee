/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Return all ATS Objects that a user is related to through logs, review roles, defects and etc.
 * 
 * @author Donald G. Dunne
 */
public class UserRelatedToAtsObjectSearch extends UserSearchItem {

   private final boolean activeObjectsOnly;

   public UserRelatedToAtsObjectSearch(String name, User user, boolean activeObjectsOnly, LoadView loadView) {
      super(name, user);
      this.activeObjectsOnly = activeObjectsOnly;
      setLoadView(loadView);
   }

   public UserRelatedToAtsObjectSearch(UserRelatedToAtsObjectSearch userRelatedToAtsObjectSearch) {
      super(userRelatedToAtsObjectSearch);
      this.activeObjectsOnly = userRelatedToAtsObjectSearch.activeObjectsOnly;
   }

   @Override
   protected Collection<Artifact> searchIt(User user) throws OseeCoreException {
      // SMA having user as portion of current state attribute (Team WorkFlow and Task)

      if (isCancelled()) return EMPTY_SET;

      List<Artifact> arts = new ArrayList<Artifact>();
      if (activeObjectsOnly) {
         arts.addAll(ArtifactQuery.getArtifactListFromAttributeKeywords(AtsUtil.getAtsBranch(), user.getUserId(),
               false, false, false, ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName()));
      } else {
         arts.addAll(ArtifactQuery.getArtifactListFromAttributeKeywords(AtsUtil.getAtsBranch(), user.getUserId(),
               false, false, false, ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
               ATSAttributes.STATE_ATTRIBUTE.getStoreName(), ATSAttributes.LOG_ATTRIBUTE.getStoreName()));
      }
      arts.addAll(user.getRelatedArtifacts(AtsRelation.TeamLead_Team));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.TeamMember_Team));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.FavoriteUser_Artifact));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.SubscribedUser_Artifact));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.PrivilegedMember_Team));

      if (isCancelled()) return EMPTY_SET;
      return arts;
   }

   @Override
   public WorldUISearchItem copy() {
      return new UserRelatedToAtsObjectSearch(this);
   }

}
