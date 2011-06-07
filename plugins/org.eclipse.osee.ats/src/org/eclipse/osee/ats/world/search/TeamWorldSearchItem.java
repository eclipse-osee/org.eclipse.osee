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
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.WorkflowManager;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionManager;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.util.AtsCacheManager;
import org.eclipse.osee.ats.core.version.VersionArtifact;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.ChangeType;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.util.WorkPageType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class TeamWorldSearchItem extends WorldUISearchItem {

   public enum ReleasedOption {
      Released,
      UnReleased,
      Both
   };
   private Collection<TeamDefinitionArtifact> teamDefs;
   private final boolean recurseChildren;
   private boolean includeCompleted;
   private boolean showAction;
   private final Collection<String> teamDefNames;
   private final ChangeType changeType;
   private final Artifact versionArt;
   private final User userArt;
   private final ReleasedOption releasedOption;
   private final boolean includeCancelled;
   private final String stateName;

   public TeamWorldSearchItem(String displayName, List<String> teamDefNames, boolean includeCompleted, boolean includeCancelled, boolean showAction, boolean recurseChildren, ChangeType changeType, Artifact versionArt, User userArt, ReleasedOption releasedOption, String stateName) {
      super(displayName, AtsImage.TEAM_WORKFLOW);
      this.includeCancelled = includeCancelled;
      this.versionArt = versionArt;
      this.userArt = userArt;
      this.teamDefNames = teamDefNames;
      this.includeCompleted = includeCompleted;
      this.releasedOption = releasedOption;
      this.showAction = showAction;
      this.recurseChildren = recurseChildren;
      this.changeType = changeType;
      this.stateName = stateName;
   }

   public TeamWorldSearchItem(String displayName, Collection<TeamDefinitionArtifact> teamDefs, boolean includeCompleted, boolean includeCancelled, boolean showAction, boolean recurseChildren, Artifact versionArt, User userArt, ReleasedOption releasedOption, String stateName) {
      super(displayName, AtsImage.TEAM_WORKFLOW);
      this.includeCancelled = includeCancelled;
      this.versionArt = versionArt;
      this.userArt = userArt;
      this.recurseChildren = recurseChildren;
      this.releasedOption = releasedOption;
      this.stateName = stateName;
      this.teamDefNames = null;
      this.teamDefs = teamDefs;
      this.includeCompleted = includeCompleted;
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
      this.includeCompleted = teamWorldUISearchItem.includeCompleted;
      this.includeCancelled = teamWorldUISearchItem.includeCancelled;
      this.showAction = teamWorldUISearchItem.showAction;
      this.changeType = teamWorldUISearchItem.changeType;
      this.stateName = teamWorldUISearchItem.stateName;
   }

   public Collection<String> getProductSearchName() {
      if (teamDefNames != null) {
         return teamDefNames;
      } else if (teamDefs != null) {
         return Artifacts.getNames(teamDefs);
      }
      return new ArrayList<String>();
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return String.format("%s - %s", super.getSelectedName(searchType), getProductSearchName());
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
      Set<String> teamDefinitionGuids = new HashSet<String>(teamDefs.size());
      for (TeamDefinitionArtifact teamDef : teamDefs) {
         if (recurseChildren) {
            for (TeamDefinitionArtifact childTeamDef : TeamDefinitionManager.getTeamsFromItemAndChildren(teamDef)) {
               teamDefinitionGuids.add(childTeamDef.getGuid());
            }
         } else {
            teamDefinitionGuids.add(teamDef.getGuid());
         }
      }
      List<AbstractArtifactSearchCriteria> criteria = new ArrayList<AbstractArtifactSearchCriteria>();
      if (teamDefinitionGuids.isEmpty()) {
         criteria.add(new AttributeCriteria(AtsAttributeTypes.TeamDefinition));
      } else {
         criteria.add(new AttributeCriteria(AtsAttributeTypes.TeamDefinition, teamDefinitionGuids));
      }

      addIncludeCompletedCancelledCriteria(criteria, includeCompleted, includeCancelled);

      if (changeType != null) {
         criteria.add(new AttributeCriteria(AtsAttributeTypes.ChangeType, changeType.name()));
      }

      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromCriteria(AtsUtil.getAtsBranch(), 1000, criteria);

      Set<Artifact> resultSet = new HashSet<Artifact>();
      for (Artifact art : artifacts) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) art;
         // don't include if userArt specified and userArt not assignee
         if (userArt != null && !awa.getStateMgr().getAssignees().contains(userArt)) {
            continue;
         }
         // don't include if version specified and workflow's not targeted for version
         if (versionArt != null) {
            TeamWorkFlowArtifact team = awa.getParentTeamWorkflow();
            if (team != null && (team.getTargetedVersion() == null || !team.getTargetedVersion().equals(versionArt))) {
               continue;
            }
         }
         // don't include if release option doesn't match relese state of targeted version
         if (releasedOption != ReleasedOption.Both) {
            TeamWorkFlowArtifact team = awa.getParentTeamWorkflow();
            if (team != null) {
               // skip if released is desired and version artifact is not set
               VersionArtifact setVerArt = team.getTargetedVersion();
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
         return WorkflowManager.filterState(stateName,
            RelationManager.getRelatedArtifacts(resultSet, 1, AtsRelationTypes.ActionToWorkflow_Action));
      } else {
         return WorkflowManager.filterState(stateName, resultSet);
      }

   }

   public static void addIncludeCompletedCancelledCriteria(List<AbstractArtifactSearchCriteria> criteria, boolean includeCompleted, boolean includeCancelled) throws OseeCoreException {
      try {
         if (AttributeTypeManager.getType(AtsAttributeTypes.CurrentStateType) != null) {
            if (!includeCancelled && !includeCompleted) {
               criteria.add(new AttributeCriteria(AtsAttributeTypes.CurrentStateType, WorkPageType.Working.name()));
            } else {
               List<String> cancelOrComplete = new ArrayList<String>(2);
               cancelOrComplete.add(WorkPageType.Working.name());
               if (includeCompleted) {
                  cancelOrComplete.add(WorkPageType.Completed.name());
               }
               if (includeCancelled) {
                  cancelOrComplete.add(WorkPageType.Cancelled.name());
               }
               criteria.add(new AttributeCriteria(AtsAttributeTypes.CurrentStateType, cancelOrComplete));
            }
         }
      } catch (OseeTypeDoesNotExist ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         // Backward compatibility; remove after 0.9.7 release
         List<String> cancelOrComplete = new ArrayList<String>(2);
         if (!includeCancelled) {
            cancelOrComplete.add(TeamState.Cancelled.getPageName() + ";;;");
         }
         if (!includeCompleted) {
            cancelOrComplete.add(TeamState.Completed.getPageName() + ";;;");
         }
         if (cancelOrComplete.size() > 0) {
            criteria.add(new AttributeCriteria(AtsAttributeTypes.CurrentState, cancelOrComplete));
         }
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
      this.includeCompleted = showFinished;
   }

   @Override
   public WorldUISearchItem copy() {
      return new TeamWorldSearchItem(this);
   }

}
