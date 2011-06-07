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
import org.eclipse.osee.ats.artifact.GoalManager;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsCacheManager;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class GoalSearchItem extends WorldUISearchItem {

   private Collection<TeamDefinitionArtifact> teamDefs;
   private boolean showFinished;
   private final Collection<String> teamDefNames;
   private final User userArt;

   public GoalSearchItem(String displayName, Set<String> teamDefNames, boolean showFinished, User userArt) {
      super(displayName, AtsImage.GOAL);
      this.userArt = userArt;
      this.teamDefNames = teamDefNames;
      this.showFinished = showFinished;
   }

   public GoalSearchItem(String displayName, Collection<TeamDefinitionArtifact> teamDefs, boolean showFinished, User userArt) {
      super(displayName, AtsImage.GOAL);
      this.userArt = userArt;
      this.teamDefNames = null;
      this.teamDefs = teamDefs;
      this.showFinished = showFinished;
   }

   public GoalSearchItem(GoalSearchItem goalWorldUISearchItem) {
      super(goalWorldUISearchItem, AtsImage.GOAL);
      this.userArt = null;
      this.teamDefNames = goalWorldUISearchItem.teamDefNames;
      this.teamDefs = goalWorldUISearchItem.teamDefs;
      this.showFinished = goalWorldUISearchItem.showFinished;
   }

   public String getProductSearchName() {
      if (teamDefNames != null && teamDefNames.size() > 0) {
         return String.valueOf(teamDefNames);
      } else if (teamDefs != null && teamDefs.size() > 0) {
         return String.valueOf(Artifacts.getNames(teamDefs));
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

   /**
    * Loads all team definitions if specified by name versus by team definition class
    */
   public void getTeamDefs() {
      if (teamDefNames != null && teamDefs == null) {
         teamDefs = new HashSet<TeamDefinitionArtifact>();
         for (String teamDefName : teamDefNames) {
            TeamDefinitionArtifact aia =
               (TeamDefinitionArtifact) AtsCacheManager.getSoleArtifactByName(AtsArtifactTypes.TeamDefinition,
                  teamDefName);
            if (aia != null) {
               teamDefs.add(aia);
            }
         }
      }
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      getTeamDefs();
      Set<String> teamDefinitionGuids = new HashSet<String>(teamDefs != null ? teamDefs.size() : 0);
      if (teamDefs != null) {
         for (TeamDefinitionArtifact teamDef : teamDefs) {
            teamDefinitionGuids.add(teamDef.getGuid());
         }
      }
      List<AbstractArtifactSearchCriteria> criteria = new ArrayList<AbstractArtifactSearchCriteria>();
      if (!teamDefinitionGuids.isEmpty()) {
         criteria.add(new AttributeCriteria(AtsAttributeTypes.TeamDefinition, teamDefinitionGuids));
      }

      if (!showFinished) {
         TeamWorldSearchItem.addIncludeCompletedCancelledCriteria(criteria, false, false);
      }

      List<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromTypeAnd(AtsArtifactTypes.Goal, AtsUtil.getAtsBranch(), 1000, criteria);

      Set<Artifact> resultGoalArtifacts = new HashSet<Artifact>();
      for (Artifact art : artifacts) {

         for (Artifact goalArt : GoalManager.getGoals(art, true)) {
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
