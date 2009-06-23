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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.DepricatedOperator;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;

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
      List<ISearchPrimitive> currentStateCriteria = new LinkedList<ISearchPrimitive>();
      currentStateCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
            "<" + user.getUserId() + ">", DepricatedOperator.CONTAINS));
      if (!activeObjectsOnly) {
         currentStateCriteria.add(new AttributeValueSearch(ATSAttributes.STATE_ATTRIBUTE.getStoreName(),
               "<" + user.getUserId() + ">", DepricatedOperator.CONTAINS));
         currentStateCriteria.add(new AttributeValueSearch(ATSAttributes.LOG_ATTRIBUTE.getStoreName(),
               "userId=\"" + user.getUserId() + "\"", DepricatedOperator.CONTAINS));
      }
      currentStateCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
            "userId>" + user.getUserId() + "</userId", DepricatedOperator.CONTAINS));
      currentStateCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
            "user>" + user.getUserId() + "</user", DepricatedOperator.CONTAINS));
      FromArtifactsSearch currentStateSearch = new FromArtifactsSearch(currentStateCriteria, false);

      if (isCancelled()) return EMPTY_SET;
      List<Artifact> arts = new ArrayList<Artifact>();
      arts.addAll(ArtifactPersistenceManager.getArtifacts(currentStateCriteria, false, AtsPlugin.getAtsBranch()));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.TeamLead_Team));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.TeamMember_Team));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.FavoriteUser_Artifact));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.SubscribedUser_Artifact));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.PrivilegedMember_Team));

      if (isCancelled()) return EMPTY_SET;
      return arts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldUISearchItem#copy()
    */
   @Override
   public WorldUISearchItem copy() {
      return new UserRelatedToAtsObjectSearch(this);
   }

}
