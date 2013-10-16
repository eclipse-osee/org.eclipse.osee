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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.artifact.WorkflowManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeType;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public class TeamWorldSearchItem extends WorldUISearchItem {

   public static enum ReleasedOption {
      Released,
      UnReleased,
      Both
   };
   private final Collection<IAtsTeamDefinition> teamDefs;
   private final boolean recurseChildren;
   private boolean includeCompleted;
   private boolean showAction;
   private final ChangeType changeType;
   private final IAtsVersion versionArt;
   private final IAtsUser userArt;
   private final ReleasedOption releasedOption;
   private final boolean includeCancelled;
   private final String stateName;
   // If set attributeType will be searched for value
   private IAttributeType attrValueSearchType;
   private final List<String> attrValueSearchOrValues = new ArrayList<String>();

   public TeamWorldSearchItem(String displayName, Collection<IAtsTeamDefinition> teamDefs, boolean includeCompleted, boolean includeCancelled, boolean showAction, boolean recurseChildren, IAtsVersion versionArt, IAtsUser userArt, ReleasedOption releasedOption, String stateName) {
      super(displayName, AtsImage.TEAM_WORKFLOW);
      this.includeCancelled = includeCancelled;
      this.versionArt = versionArt;
      this.userArt = userArt;
      this.recurseChildren = recurseChildren;
      this.releasedOption = releasedOption;
      this.stateName = stateName;
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
      this.teamDefs = teamWorldUISearchItem.teamDefs;
      this.includeCompleted = teamWorldUISearchItem.includeCompleted;
      this.includeCancelled = teamWorldUISearchItem.includeCancelled;
      this.showAction = teamWorldUISearchItem.showAction;
      this.changeType = teamWorldUISearchItem.changeType;
      this.stateName = teamWorldUISearchItem.stateName;
   }

   public Collection<String> getProductSearchName() {
      if (teamDefs != null) {
         return TeamDefinitions.getNames(teamDefs);
      }
      return new ArrayList<String>();
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return String.format("%s - %s", super.getSelectedName(searchType), getProductSearchName());
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      Set<String> teamDefinitionGuids = new HashSet<String>(teamDefs.size());
      for (IAtsTeamDefinition teamDef : teamDefs) {
         if (recurseChildren) {
            for (IAtsTeamDefinition childTeamDef : TeamDefinitions.getTeamsFromItemAndChildren(teamDef)) {
               teamDefinitionGuids.add(childTeamDef.getGuid());
            }
         } else {
            teamDefinitionGuids.add(teamDef.getGuid());
         }
      }
      List<ArtifactSearchCriteria> criteria = new ArrayList<ArtifactSearchCriteria>();

      if (teamDefinitionGuids.isEmpty()) {
         criteria.add(new AttributeCriteria(AtsAttributeTypes.TeamDefinition));
      } else {
         criteria.add(new AttributeCriteria(AtsAttributeTypes.TeamDefinition, teamDefinitionGuids));
      }

      addIncludeCompletedCancelledCriteria(criteria, includeCompleted, includeCancelled);

      // If set, add attrType and attrValue to search criteria
      if (getAttrValueSearchType() != null && !attrValueSearchOrValues.isEmpty()) {
         criteria.add(new AttributeCriteria(getAttrValueSearchType(), attrValueSearchOrValues, Operator.EQUAL));
      }

      if (changeType != null) {
         criteria.add(new AttributeCriteria(AtsAttributeTypes.ChangeType, changeType.name()));
      }

      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromCriteria(AtsUtil.getAtsBranch(), 1000, criteria);

      Set<Artifact> resultSet = new HashSet<Artifact>();
      for (Artifact art : artifacts) {
         if (art instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) art;

            // don't include if userArt specified and userArt not assignee
            if (userArt != null && !awa.getStateMgr().getAssignees().contains(userArt)) {
               continue;
            }
            // don't include if version specified and workflow's not targeted for version
            if (versionArt != null) {
               TeamWorkFlowArtifact team = awa.getParentTeamWorkflow();
               if (team != null && (!AtsVersionService.get().hasTargetedVersion(team) || !AtsVersionService.get().getTargetedVersion(
                  team).equals(versionArt))) {
                  continue;
               }
            }
            // don't include if release option doesn't match relese state of targeted version
            if (releasedOption != ReleasedOption.Both) {
               TeamWorkFlowArtifact team = awa.getParentTeamWorkflow();
               if (team != null) {
                  // skip if released is desired and version artifact is not set
                  IAtsVersion setVerArt = AtsVersionService.get().getTargetedVersion(team);
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
      }
      if (showAction) {
         return WorkflowManager.filterState(stateName,
            RelationManager.getRelatedArtifacts(resultSet, 1, AtsRelationTypes.ActionToWorkflow_Action));
      } else {
         return WorkflowManager.filterState(stateName, resultSet);
      }

   }

   public static void addIncludeCompletedCancelledCriteria(List<ArtifactSearchCriteria> criteria, boolean includeCompleted, boolean includeCancelled) throws OseeCoreException {
      try {
         if (AttributeTypeManager.getType(AtsAttributeTypes.CurrentStateType) != null) {
            if (!includeCancelled && !includeCompleted) {
               criteria.add(new AttributeCriteria(AtsAttributeTypes.CurrentStateType, StateType.Working.name()));
            } else {
               List<String> cancelOrComplete = new ArrayList<String>(2);
               cancelOrComplete.add(StateType.Working.name());
               if (includeCompleted) {
                  cancelOrComplete.add(StateType.Completed.name());
               }
               if (includeCancelled) {
                  cancelOrComplete.add(StateType.Cancelled.name());
               }
               criteria.add(new AttributeCriteria(AtsAttributeTypes.CurrentStateType, cancelOrComplete));
            }
         }
      } catch (OseeTypeDoesNotExist ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         // Backward compatibility; remove after 0.9.7 release
         List<String> cancelOrComplete = new ArrayList<String>(2);
         if (!includeCancelled) {
            cancelOrComplete.add(TeamState.Cancelled.getName() + ";;;");
         }
         if (!includeCompleted) {
            cancelOrComplete.add(TeamState.Completed.getName() + ";;;");
         }
         if (cancelOrComplete.size() > 0) {
            criteria.add(new AttributeCriteria(AtsAttributeTypes.CurrentState, cancelOrComplete));
         }
      }
   }

   public void setShowAction(boolean showAction) {
      this.showAction = showAction;
   }

   public void setShowFinished(boolean showFinished) {
      this.includeCompleted = showFinished;
   }

   @Override
   public WorldUISearchItem copy() {
      return new TeamWorldSearchItem(this);
   }

   public IAttributeType getAttrValueSearchType() {
      return attrValueSearchType;
   }

   public List<String> getAttrValueSearchOrValues() {
      return attrValueSearchOrValues;
   }

   public void setAttrValueSearchType(IAttributeType attrValueSearchType) {
      this.attrValueSearchType = attrValueSearchType;
   }

}
