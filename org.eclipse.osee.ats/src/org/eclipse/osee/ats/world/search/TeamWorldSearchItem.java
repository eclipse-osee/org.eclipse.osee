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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;

/**
 * @author Donald G. Dunne
 */
public class TeamWorldSearchItem extends WorldUISearchItem {

   public enum ReleasedOption {
      Released, UnReleased, Both
   };
   private Collection<TeamDefinitionArtifact> teamDefs;
   private final boolean recurseChildren;
   private boolean showFinished;
   private boolean showAction;
   private final Collection<String> teamDefNames;
   private final ChangeType changeType;
   private final VersionArtifact versionArt;
   private final User userArt;
   private final ReleasedOption releasedOption;

   public TeamWorldSearchItem(String displayName, String[] teamDefNames, boolean showFinished, boolean showAction, boolean recurseChildren, ChangeType changeType, VersionArtifact versionArt, User userArt, ReleasedOption releasedOption) {
      super(displayName, AtsImage.TEAM_WORKFLOW);
      this.versionArt = versionArt;
      this.userArt = userArt;
      if (teamDefNames != null) {
         this.teamDefNames = Arrays.asList(teamDefNames);
      } else {
         this.teamDefNames = null;
      }
      this.showFinished = showFinished;
      this.releasedOption = releasedOption;
      this.showAction = showAction;
      this.recurseChildren = recurseChildren;
      this.changeType = changeType;
   }

   public TeamWorldSearchItem(String displayName, Collection<TeamDefinitionArtifact> teamDefs, boolean showFinished, boolean showAction, boolean recurseChildren, VersionArtifact versionArt, User userArt, ReleasedOption releasedOption) {
      super(displayName, AtsImage.TEAM_WORKFLOW);
      this.versionArt = versionArt;
      this.userArt = userArt;
      this.recurseChildren = recurseChildren;
      this.releasedOption = releasedOption;
      this.teamDefNames = null;
      this.teamDefs = teamDefs;
      this.showFinished = showFinished;
      this.showAction = showAction;
      this.changeType = null;
   }

   public TeamWorldSearchItem(TeamWorldSearchItem teamWorldUISearchItem) {
      super(teamWorldUISearchItem, AtsImage.TEAM_WORKFLOW);
      this.versionArt = null;
      this.userArt = null;
      this.releasedOption = null;
      this.recurseChildren = teamWorldUISearchItem.recurseChildren;
      this.teamDefNames = teamWorldUISearchItem.teamDefNames;
      this.teamDefs = teamWorldUISearchItem.teamDefs;
      this.showFinished = teamWorldUISearchItem.showFinished;
      this.showAction = teamWorldUISearchItem.showAction;
      this.changeType = teamWorldUISearchItem.changeType;
   }

   public Collection<String> getProductSearchName() {
      if (teamDefNames != null)
         return teamDefNames;
      else if (teamDefs != null) return Artifacts.artNames(teamDefs);
      return new ArrayList<String>();
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return String.format("%s - %s", super.getSelectedName(searchType), getProductSearchName());
   }

   /**
    * Loads all team definitions if specified by name versus by team definition class
    * 
    * @throws IllegalArgumentException
    */
   public void getTeamDefs() throws OseeCoreException {
      if (teamDefNames != null && teamDefs == null) {
         teamDefs = new HashSet<TeamDefinitionArtifact>();
         for (String teamDefName : teamDefNames) {
            TeamDefinitionArtifact aia =
                  (TeamDefinitionArtifact) AtsCacheManager.getSoleArtifactByName(
                        ArtifactTypeManager.getType(TeamDefinitionArtifact.ARTIFACT_NAME), teamDefName);
            if (aia != null) {
               teamDefs.add(aia);
            }
         }
      }
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      getTeamDefs();
      Set<String> teamDefinitionGuids = new HashSet<String>(teamDefs.size());
      for (TeamDefinitionArtifact teamDef : teamDefs) {
         if (recurseChildren) {
            for (TeamDefinitionArtifact childTeamDef : TeamDefinitionArtifact.getTeamsFromItemAndChildren(teamDef)) {
               teamDefinitionGuids.add(childTeamDef.getGuid());
            }
         } else {
            teamDefinitionGuids.add(teamDef.getGuid());
         }
      }
      List<AbstractArtifactSearchCriteria> criteria = new ArrayList<AbstractArtifactSearchCriteria>();
      criteria.add(new AttributeCriteria(ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName(),
            teamDefinitionGuids));

      if (!showFinished) {
         List<String> cancelOrComplete = new ArrayList<String>(2);
         cancelOrComplete.add(DefaultTeamState.Cancelled.name() + ";;;");
         cancelOrComplete.add(DefaultTeamState.Completed.name() + ";;;");
         criteria.add(new AttributeCriteria(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(), cancelOrComplete,
               Operator.NOT_EQUAL));
      }
      if (changeType != null) {
         criteria.add(new AttributeCriteria(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), changeType.name()));
      }

      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromCriteria(AtsUtil.getAtsBranch(), 1000, criteria);

      Set<Artifact> resultSet = new HashSet<Artifact>();
      for (Artifact art : artifacts) {
         StateMachineArtifact sma = (StateMachineArtifact) art;
         // don't include if userArt specified and userArt not assignee
         if (userArt != null && !sma.getSmaMgr().getStateMgr().getAssignees().contains(userArt)) {
            continue;
         }
         // don't include if version specified and workflow's not targeted for version
         if (versionArt != null) {
            TeamWorkFlowArtifact team = sma.getParentTeamWorkflow();
            if (team != null && (team.getWorldViewTargetedVersion() == null || !team.getWorldViewTargetedVersion().equals(
                  versionArt))) {
               continue;
            }
         }
         // don't include if release option doesn't match relese state of targeted version
         if (releasedOption != ReleasedOption.Both) {
            TeamWorkFlowArtifact team = sma.getParentTeamWorkflow();
            if (team != null) {
               // skip if released is desired and version artifact is not set
               VersionArtifact setVerArt = team.getWorldViewTargetedVersion();
               if (setVerArt == null && releasedOption == ReleasedOption.Released) {
                  continue;
               }
               // skip of version release is opposite of desired
               if (setVerArt != null) {
                  if (releasedOption == ReleasedOption.Released && !setVerArt.isReleased()) {
                     continue;
                  } else if (releasedOption == ReleasedOption.UnReleased && setVerArt.isReleased()) {
                     continue;
                  }
               }
            }
         }
         resultSet.add(art);
      }
      if (showAction) {
         return RelationManager.getRelatedArtifacts(resultSet, 1, AtsRelationTypes.ActionToWorkflow_Action);
      } else {
         return resultSet;
      }

   }

   /**
    * @param showAction The showAction to set.
    */
   public void setShowAction(boolean showAction) {
      this.showAction = showAction;
   }

   /**
    * @param showFinished The showFinished to set.
    */
   public void setShowFinished(boolean showFinished) {
      this.showFinished = showFinished;
   }

   @Override
   public WorldUISearchItem copy() {
      return new TeamWorldSearchItem(this);
   }

}
