/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.core.workflow;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class WorkItem extends AtsObject implements IAtsWorkItem {

   protected final ArtifactToken artifact;
   private IAtsLog atsLog;
   protected final AtsApi atsApi;
   protected final Log logger;
   IAtsTeamWorkflow parentTeamWf;
   IAtsAction parentAction;
   private final ArtifactTypeToken artifactType;

   public WorkItem(Log logger, AtsApi atsApi, ArtifactToken artifact, ArtifactTypeToken artifactType) {
      super(artifact.getName(), artifact.getId());
      this.logger = logger;
      this.atsApi = atsApi;
      this.artifact = artifact;
      this.artifactType = artifactType;
      setStoreObject(artifact);
   }

   @Override
   public String getDescription() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.Description, "");
   }

   @Override
   public List<AtsUser> getAssignees() {
      return getStateMgr().getAssignees();
   }

   @Override
   public String getAtsId() {
      try {
         return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.AtsId, getIdString());
      } catch (OseeCoreException ex) {
         return null;
      }
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow() {
      if (this.isTeamWorkflow()) {
         return (IAtsTeamWorkflow) this;
      }
      if (parentTeamWf == null) {
         ArtifactToken teamArt = ArtifactReadable.SENTINEL;
         if (isTeamWorkflow()) {
            teamArt = artifact;
         } else if (isReview()) {
            teamArt = atsApi.getRelationResolver().getRelatedOrSentinel(artifact,
               AtsRelationTypes.TeamWorkflowToReview_TeamWorkflow);
         } else if (isTask()) {
            teamArt =
               atsApi.getRelationResolver().getRelatedOrSentinel(artifact, AtsRelationTypes.TeamWfToTask_TeamWorkflow);
         }
         if (teamArt.isValid()) {
            parentTeamWf = atsApi.getWorkItemService().getTeamWf(teamArt);
         }
      }
      return parentTeamWf;
   }

   @Override
   public IAtsAction getParentAction() {
      if (parentAction == null) {
         ArtifactToken actionArt = null;
         IAtsTeamWorkflow teamWf = getParentTeamWorkflow();
         if (teamWf != null) {
            Collection<ArtifactToken> results = atsApi.getRelationResolver().getRelated(teamWf.getStoreObject(),
               AtsRelationTypes.ActionToWorkflow_Action);
            if (!results.isEmpty()) {
               actionArt = results.iterator().next();
            }
         }
         if (actionArt != null) {
            parentAction = atsApi.getWorkItemService().getAction(actionArt);
         }
      }
      return parentAction;
   }

   @Override
   public boolean isReview() {
      return this instanceof IAtsAbstractReview;
   }

   @Override
   public boolean isGoal() {
      return this instanceof IAtsGoal;
   }

   @Override
   public IAtsStateManager getStateMgr() {
      return atsApi.getStateFactory().getStateManager(this);
   }

   @Override
   public IAtsLog getLog() {
      if (atsLog == null) {
         try {
            atsLog = atsApi.getLogFactory().getLogLoaded(this, atsApi.getAttributeResolver());
         } catch (OseeCoreException ex) {
            logger.error(ex, "Error getting Log for artifact[%s]", artifact);
         }
      }
      return atsLog;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition() {
      return atsApi.getWorkDefinitionService().getWorkDefinition(this);
   }

   @Override
   public IAtsStateDefinition getStateDefinition() {
      String currentStateName = getStateMgr().getCurrentStateName();
      if (currentStateName == null) {
         return null;
      }
      return getWorkDefinition().getStateByName(currentStateName);
   }

   public IAtsStateDefinition getStateDefinitionByName(String name) {
      return getWorkDefinition().getStateByName(name);
   }

   @Override
   public boolean isTask() {
      return this instanceof IAtsTask;
   }

   @Override
   public boolean isTeamWorkflow() {
      return this instanceof IAtsTeamWorkflow;
   }

   @Override
   public ArtifactToken getStoreObject() {
      return atsApi.getQueryService().getArtifact(artifact);
   }

   @Override
   public AtsUser getCreatedBy() {
      String userId = atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CreatedBy, null);
      return atsApi.getUserService().getUserByUserId(userId);
   }

   @Override
   public Date getCreatedDate() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CreatedDate, null);
   }

   @Override
   public AtsUser getCompletedBy() {
      String userId =
         atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CompletedBy, null);
      return atsApi.getUserService().getUserByUserId(userId);
   }

   @Override
   public AtsUser getCancelledBy() {
      String userId =
         atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CancelledBy, null);
      return atsApi.getUserService().getUserByUserId(userId);
   }

   @Override
   public String getCompletedFromState() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CompletedFromState, null);
   }

   @Override
   public String getCancelledFromState() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CancelledFromState, null);
   }

   @Override
   public String getArtifactTypeName() {
      return artifactType.getName();
   }

   @Override
   public Date getCompletedDate() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CompletedDate, null);
   }

   @Override
   public Date getCancelledDate() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CancelledDate, null);
   }

   @Override
   public String getCancelledReason() {
      String ret = "";
      if (artifact instanceof IAtsWorkItem) {
         ret = atsApi.getAttributeResolver().getSoleAttributeValueAsString(artifact, AtsAttributeTypes.CancelledReason,
            "");
         if (Strings.isInValid(ret)) {
            ret = atsApi.getAttributeResolver().getSoleAttributeValueAsString(artifact,
               AtsAttributeTypes.CancelledReasonEnum, "");
         }
      }
      return ret;
   }

   @Override
   public boolean isInWork() {
      return getStateMgr().getStateType().isInWork();
   }

   @Override
   public boolean isCompleted() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(this, AtsAttributeTypes.CurrentStateType,
         StateType.Working.name()).equals(StateType.Completed.name());
   }

   @Override
   public boolean isCompletedOrCancelled() {
      return isCompleted() || isCancelled();
   }

   @Override
   public boolean isCancelled() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(this, AtsAttributeTypes.CurrentStateType,
         StateType.Working.name()).equals(StateType.Cancelled.name());
   }

   @Override
   public List<AtsUser> getImplementers() {
      return atsApi.getImplementerService().getImplementers(this);
   }

   @Override
   public boolean isDecisionReview() {
      return this instanceof IAtsDecisionReview;
   }

   @Override
   public boolean isPeerReview() {
      return this instanceof IAtsPeerToPeerReview;
   }

   @Override
   public void setStateMgr(IAtsStateManager stateMgr) {
      atsApi.getStateFactory().setStateMgr(this, stateMgr);
   }

   @Override
   public void clearCaches() {
      parentAction = null;
      atsApi.getStateFactory().clearStateManager(this);
      atsLog = null;
      atsApi.getWorkDefinitionService().internalClearWorkDefinition(this);
   }

   @Override
   public boolean isInState(IStateToken state) {
      return getStateMgr().getCurrentState().getName().equals(state.getName());
   }

}