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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.artifact.GoalManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;

/**
 * @author Donald G. Dunne
 */
public class GoalSearchItem extends WorldUISearchItem {

   private final Collection<IAtsTeamDefinition> teamDefs;
   private boolean showFinished;
   private final User userArt;

   public GoalSearchItem(String displayName, Collection<IAtsTeamDefinition> teamDefs, boolean showFinished, User userArt) {
      super(displayName, AtsImage.GOAL);
      this.userArt = userArt;
      this.teamDefs = teamDefs;
      this.showFinished = showFinished;
   }

   public GoalSearchItem(GoalSearchItem goalWorldUISearchItem) {
      super(goalWorldUISearchItem, AtsImage.GOAL);
      this.userArt = null;
      this.teamDefs = goalWorldUISearchItem.teamDefs;
      this.showFinished = goalWorldUISearchItem.showFinished;
   }

   public String getProductSearchName() {
      if (teamDefs != null && teamDefs.size() > 0) {
         return String.valueOf(TeamDefinitions.getNames(teamDefs));
      }
      return "";
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      String prodName = getProductSearchName();
      if (Strings.isValid(prodName)) {
         return String.format("%s - %s", super.getSelectedName(searchType), prodName);
      }
      return super.getSelectedName(searchType);
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      Set<String> teamDefinitionGuids = new HashSet<>(teamDefs != null ? teamDefs.size() : 0);
      if (teamDefs != null) {
         for (IAtsTeamDefinition teamDef : teamDefs) {
            teamDefinitionGuids.add(AtsUtilCore.getGuid(teamDef));
         }
      }
      List<ArtifactSearchCriteria> criteria = new ArrayList<>();
      if (!teamDefinitionGuids.isEmpty()) {
         criteria.add(new AttributeCriteria(AtsAttributeTypes.TeamDefinition, teamDefinitionGuids));
      }

      if (!showFinished) {
         TeamWorldSearchItem.addIncludeCompletedCancelledCriteria(criteria, false, false);
      }

      List<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromTypeAnd(AtsArtifactTypes.Goal, AtsUtilCore.getAtsBranch(), 1000, criteria);

      Set<Artifact> resultGoalArtifacts = new HashSet<>();
      for (Artifact art : artifacts) {

         for (Artifact goalArt : new GoalManager().getCollectors(art, true)) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) goalArt;
            // don't include if userArt specified and userArt not assignee
            if (userArt != null && !awa.getStateMgr().getAssignees().contains(userArt)) {
               continue;
            }
            if (!showFinished && awa.isCompletedOrCancelled()) {
               continue;
            }
            resultGoalArtifacts.add(goalArt);
         }
      }
      return resultGoalArtifacts;

   }

   public void setShowFinished(boolean showFinished) {
      this.showFinished = showFinished;
   }

   @Override
   public WorldUISearchItem copy() {
      return new GoalSearchItem(this);
   }

}
