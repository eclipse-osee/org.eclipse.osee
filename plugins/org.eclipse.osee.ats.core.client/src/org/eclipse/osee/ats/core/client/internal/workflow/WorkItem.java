/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.workflow;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IWorkDefinitionMatch;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G Dunne
 */
public class WorkItem extends AtsObject implements IAtsWorkItem {

   protected final Artifact artifact;
   private IAtsStateManager stateMgr;
   private IAtsLog atsLog;
   private IWorkDefinitionMatch match;
   private final IAtsClient atsClient;

   public WorkItem(IAtsClient atsClient, Artifact artifact) {
      super(artifact.getName(), artifact.getArtId());
      this.atsClient = atsClient;
      this.artifact = artifact;
   }

   protected IAtsClient getAtsClient() {
      return atsClient;
   }

   @Override
   public String getDescription() {
      try {
         return artifact.getSoleAttributeValue(AtsAttributeTypes.Description, "");
      } catch (OseeCoreException ex) {
         OseeLog.logf(Activator.class, OseeLevel.SEVERE_POPUP, ex, "Error getting description for artifact[%s]",
            artifact);
         return "exception: " + ex.getLocalizedMessage();
      }
   }

   @Override
   public List<IAtsUser> getAssignees() throws OseeCoreException {
      return getStateMgr().getAssignees();
   }

   @Override
   public String getAtsId() {
      try {
         return artifact.getSoleAttributeValue(AtsAttributeTypes.AtsId, String.valueOf(getId()));
      } catch (OseeCoreException ex) {
         return null;
      }
   }

   @Override
   public void setAtsId(String atsId, IAtsChangeSet changes) throws OseeCoreException {
      throw new OseeStateException("Not implemented");
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow() throws OseeCoreException {
      Artifact teamArt = null;
      if (isTeamWorkflow()) {
         teamArt = artifact;
      } else if (isReview()) {
         List<Artifact> results = artifact.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowToReview_Team);
         if (!results.isEmpty()) {
            teamArt = results.iterator().next();
         }
      } else if (isTask()) {
         List<Artifact> results = artifact.getRelatedArtifacts(AtsRelationTypes.TeamWfToTask_TeamWf);
         if (!results.isEmpty()) {
            teamArt = results.iterator().next();
         }
      }
      return atsClient.getWorkItemFactory().getTeamWf(teamArt);
   }

   @Override
   public IAtsAction getParentAction() {
      Artifact actionArt = null;
      if (artifact instanceof ActionArtifact) {
         actionArt = artifact;
      } else if (artifact instanceof AbstractWorkflowArtifact) {
         actionArt = ((AbstractWorkflowArtifact) artifact).getParentActionArtifact();
      }
      if (actionArt != null) {
         return AtsClientService.get().getWorkItemFactory().getAction(actionArt);
      }
      return null;
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
         try {
            stateMgr = getAtsClient().getStateFactory().getStateManager(this, true);
         } catch (OseeCoreException ex) {
            OseeLog.logf(Activator.class, OseeLevel.SEVERE_POPUP, ex, "Error getting stateManager for artifact[%s]",
               artifact);
         }
      }
      return stateMgr;
   }

   @Override
   public void setStateManager(IAtsStateManager stateMgr) {
      this.stateMgr = stateMgr;
   }

   @Override
   public IAtsLog getLog() {
      if (atsLog == null) {
         try {
            atsLog = getAtsClient().getLogFactory().getLogLoaded(this, atsClient.getAttributeResolver());
         } catch (OseeCoreException ex) {
            OseeLog.logf(Activator.class, OseeLevel.SEVERE_POPUP, ex, "Error getting Log for artifact[%s]", artifact);
         }
      }
      return atsLog;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition() {
      if (match == null) {
         match = getWorkDefinitionMatch();
         if (match == null) {
            return null;
         }
         if (!match.isMatched()) {
            OseeLog.logf(Activator.class, OseeLevel.SEVERE_POPUP,
               "Error getting work definition for artifact[%s] - using match [%s]", artifact, match);
            return null;
         }
      }
      return match.getWorkDefinition();
   }

   public IWorkDefinitionMatch getWorkDefinitionMatch() {
      if (match == null) {
         try {
            match = getAtsClient().getWorkDefinitionAdmin().getWorkDefinition(this);
         } catch (Exception ex) {
            OseeLog.logf(Activator.class, OseeLevel.SEVERE_POPUP,
               "Error getting work definition match for artifact[%s]", artifact);
         }
      }
      return match;
   }

   @Override
   public IAtsStateDefinition getStateDefinition() {
      if (getStateMgr().getCurrentStateName() == null) {
         return null;
      }
      return getWorkDefinition().getStateByName(getStateMgr().getCurrentStateName());
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
      String userId = artifact.getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null);
      return atsClient.getUserService().getUserById(userId);
   }

   @Override
   public Date getCreatedDate() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CreatedDate, null);
   }

   @Override
   public IAtsUser getCompletedBy() {
      String userId = artifact.getSoleAttributeValue(AtsAttributeTypes.CompletedBy, null);
      return atsClient.getUserService().getUserById(userId);
   }

   @Override
   public IAtsUser getCancelledBy() {
      String userId = artifact.getSoleAttributeValue(AtsAttributeTypes.CancelledBy, null);
      return atsClient.getUserService().getUserById(userId);
   }

   @Override
   public String getCompletedFromState() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CompletedFromState, null);
   }

   @Override
   public String getCancelledFromState() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CancelledFromState, null);
   }

   @Override
   public String getArtifactTypeName() {
      return artifact.getArtifactType().getName();
   }

   @Override
   public Date getCompletedDate() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CompletedDate, null);
   }

   @Override
   public Date getCancelledDate() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
   }

   @Override
   public String getCancelledReason() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CancelledReason, null);
   }

   @Override
   public int compareTo(Named other) {
      return artifact.compareTo(other);
   }

   @Override
   public boolean isInWork() {
      return getStateMgr().getStateType().isInWork();
   }

   @Override
   public boolean isCompleted() {
      return getStateMgr().getStateType().isCompleted();
   }

   @Override
   public boolean isCompletedOrCancelled() {
      return isCompleted() || isCancelled();
   }

   @Override
   public boolean isCancelled() {
      return getStateMgr().getStateType().isCancelled();
   }

   @Override
   public List<IAtsUser> getImplementers() throws OseeCoreException {
      return atsClient.getImplementerService().getImplementers(this);
   }

   @Override
   public boolean isDecisionReview() {
      return this instanceof IAtsDecisionReview;
   }

   @Override
   public boolean isPeerReview() {
      return this instanceof IAtsPeerToPeerReview;
   }

}
