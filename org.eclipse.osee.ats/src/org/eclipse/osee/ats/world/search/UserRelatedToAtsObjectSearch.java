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

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.DepricatedOperator;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

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

   @Override
   protected Collection<Artifact> searchIt(User user) throws OseeCoreException, SQLException {
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

      List<ISearchPrimitive> smaArtifactTypeCriteria = new LinkedList<ISearchPrimitive>();
      for (String artifactTypeName : StateMachineArtifact.getAllSMATypeNames()) {
         smaArtifactTypeCriteria.add(new ArtifactTypeSearch(artifactTypeName, DepricatedOperator.EQUAL));
      }
      FromArtifactsSearch smaArtifactTypeSearch = new FromArtifactsSearch(smaArtifactTypeCriteria, false);

      List<ISearchPrimitive> smaCriteria = new LinkedList<ISearchPrimitive>();
      smaCriteria.add(smaArtifactTypeSearch);
      smaCriteria.add(currentStateSearch);

      if (isCancelled()) return EMPTY_SET;
      Collection<Artifact> arts =
            ArtifactPersistenceManager.getArtifacts(smaCriteria, true, BranchPersistenceManager.getAtsBranch());

      arts.addAll(user.getRelatedArtifacts(AtsRelation.TeamLead_Team));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.TeamMember_Team));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.FavoriteUser_Artifact));
      arts.addAll(user.getRelatedArtifacts(AtsRelation.SubscribedUser_Artifact));

      if (isCancelled()) return EMPTY_SET;
      return arts;
   }
}
