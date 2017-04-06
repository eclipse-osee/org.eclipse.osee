/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class WorkItem extends AtsObject implements IAtsWorkItem {

   protected final ArtifactToken artifact;
   private IAtsStateManager stateMgr;
   private IAtsLog atsLog;
   protected final IAtsServices services;
   protected final Log logger;
   IAtsTeamWorkflow parentTeamWf;
   IAtsAction parentAction;

   public WorkItem(Log logger, IAtsServices services, ArtifactToken artifact) {
      super(artifact.getName(), artifact.getId());
      this.logger = logger;
      this.services = services;
      this.artifact = artifact;
      setStoreObject(artifact);
   }

   @Override
   public String getDescription() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.Description, "");
   }

   @Override
   public List<IAtsUser> getAssignees() throws OseeCoreException {
      return getStateMgr().getAssignees();
   }

   @Override
   public String getAtsId() {
      try {
         return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.AtsId,
            String.valueOf(getId()));
      } catch (OseeCoreException ex) {
         return null;
      }
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow() throws OseeCoreException {
      if (this.isTeamWorkflow()) {
         return (IAtsTeamWorkflow) this;
      }
      if (parentTeamWf == null) {
         ArtifactId teamArt = null;
         if (isTeamWorkflow()) {
            teamArt = artifact;
         } else if (isReview()) {
            teamArt =
               services.getRelationResolver().getRelatedOrNull(artifact, AtsRelationTypes.TeamWorkflowToReview_Team);
         } else if (isTask()) {
            teamArt = services.getRelationResolver().getRelatedOrNull(artifact, AtsRelationTypes.TeamWfToTask_TeamWf);
         }
         parentTeamWf = services.getWorkItemFactory().getTeamWf(teamArt);
      }
      return parentTeamWf;
   }

   @Override
   public IAtsAction getParentAction() {
      if (parentAction == null) {
         ArtifactToken actionArt = null;
         IAtsTeamWorkflow teamWf = getParentTeamWorkflow();
         if (teamWf != null) {
            Collection<ArtifactToken> results = services.getRelationResolver().getRelated(teamWf.getStoreObject(),
               AtsRelationTypes.ActionToWorkflow_Action);
            if (!results.isEmpty()) {
               actionArt = results.iterator().next();
            }
         }
         parentAction = services.getWorkItemFactory().getAction(actionArt);
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
      if (stateMgr == null) {
         stateMgr = services.getStateFactory().getStateManager(this);
      }
      return stateMgr;
   }

   @Override
   public IAtsLog getLog() {
      if (atsLog == null) {
         try {
            atsLog = services.getLogFactory().getLogLoaded(this, services.getAttributeResolver());
         } catch (OseeCoreException ex) {
            logger.error(ex, "Error getting Log for artifact[%s]", artifact);
         }
      }
      return atsLog;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition() {
      return services.getWorkDefinitionService().getWorkDefinition(this);
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
      return artifact;
   }

   @Override
   public IAtsUser getCreatedBy() {
      String userId =
         services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CreatedBy, null);
      return services.getUserService().getUserById(userId);
   }

   @Override
   public Date getCreatedDate() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CreatedDate, null);
   }

   @Override
   public IAtsUser getCompletedBy() {
      String userId =
         services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CompletedBy, null);
      return services.getUserService().getUserById(userId);
   }

   @Override
   public IAtsUser getCancelledBy() {
      String userId =
         services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CancelledBy, null);
      return services.getUserService().getUserById(userId);
   }

   @Override
   public String getCompletedFromState() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CompletedFromState,
         null);
   }

   @Override
   public String getCancelledFromState() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CancelledFromState,
         null);
   }

   @Override
   public String getArtifactTypeName() {
      return artifact.getArtifactType().getName();
   }

   @Override
   public Date getCompletedDate() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CompletedDate, null);
   }

   @Override
   public Date getCancelledDate() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CancelledDate, null);
   }

   @Override
   public String getCancelledReason() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CancelledReason, null);
   }

   @Override
   public boolean isInWork() {
      return getStateMgr().getStateType().isInWork();
   }

   @Override
   public boolean isCompleted() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CurrentStateType,
         "").equals(StateType.Completed.name());
   }

   @Override
   public boolean isCompletedOrCancelled() {
      return isCompleted() || isCancelled();
   }

   @Override
   public boolean isCancelled() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.CurrentStateType,
         "").equals(StateType.Cancelled.name());
   }

   @Override
   public List<IAtsUser> getImplementers() throws OseeCoreException {
      return services.getImplementerService().getImplementers(this);
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
      this.stateMgr = stateMgr;
   }

   @Override
   public void clearCaches() {
      stateMgr = null;
      atsLog = null;
   }
}