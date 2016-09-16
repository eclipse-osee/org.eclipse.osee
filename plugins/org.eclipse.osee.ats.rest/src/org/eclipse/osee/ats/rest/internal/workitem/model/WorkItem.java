/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem.model;

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
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class WorkItem extends AtsObject implements IAtsWorkItem {

   protected final ArtifactReadable artifact;
   private IAtsStateManager stateMgr;
   private IAtsLog atsLog;
   private IWorkDefinitionMatch match;
   private final IAtsServer atsServer;
   private final Log logger;

   public WorkItem(Log logger, IAtsServer atsServer, ArtifactReadable artifact) {
      super(artifact.getName(), artifact.getId());
      this.logger = logger;
      this.atsServer = atsServer;
      this.artifact = artifact;
   }

   protected IAtsServer getAtsServer() {
      return atsServer;
   }

   @Override
   public String getDescription() {
      try {
         return artifact.getSoleAttributeAsString(AtsAttributeTypes.Description, "");
      } catch (OseeCoreException ex) {
         logger.error(ex, "Error getting description for artifact[%s]", artifact);
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
         return artifact.getSoleAttributeAsString(AtsAttributeTypes.AtsId, String.valueOf(getId()));
      } catch (OseeCoreException ex) {
         return null;
      }
   }

   @Override
   public void setAtsId(String atsId, IAtsChangeSet changes) throws OseeCoreException {
      throw new UnsupportedOperationException("Not implemented");
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow() throws OseeCoreException {
      ArtifactReadable teamArt = null;
      if (isTeamWorkflow()) {
         teamArt = artifact;
      } else if (isReview()) {
         ResultSet<ArtifactReadable> results = artifact.getRelated(AtsRelationTypes.TeamWorkflowToReview_Team);
         if (!results.isEmpty()) {
            teamArt = results.iterator().next();
         }
      } else if (isTask()) {
         ResultSet<ArtifactReadable> results = artifact.getRelated(AtsRelationTypes.TeamWfToTask_TeamWf);
         if (!results.isEmpty()) {
            teamArt = results.iterator().next();
         }
      }
      return atsServer.getWorkItemFactory().getTeamWf(teamArt);
   }

   @Override
   public IAtsAction getParentAction() {
      ArtifactReadable actionArt = null;
      IAtsTeamWorkflow teamWf = getParentTeamWorkflow();
      if (teamWf != null) {
         ResultSet<ArtifactReadable> results =
            ((ArtifactReadable) teamWf.getStoreObject()).getRelated(AtsRelationTypes.ActionToWorkflow_Action);
         if (!results.isEmpty()) {
            actionArt = results.iterator().next();
         }
      }
      return atsServer.getWorkItemFactory().getAction(actionArt);
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
            stateMgr = getAtsServer().getStateFactory().getStateManager(this, true);
         } catch (OseeCoreException ex) {
            logger.error(ex, "Error getting stateManager for artifact[%s]", artifact);
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
            atsLog = getAtsServer().getLogFactory().getLogLoaded(this, atsServer.getAttributeResolver());
         } catch (OseeCoreException ex) {
            logger.error(ex, "Error getting Log for artifact[%s]", artifact);
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
            logger.error("Error getting work definition for artifact[%s] - using match [%s]", artifact, match);
            return null;
         }
      }
      return match.getWorkDefinition();
   }

   public IWorkDefinitionMatch getWorkDefinitionMatch() {
      if (match == null) {
         try {
            match = getAtsServer().getWorkDefAdmin().getWorkDefinition(this);
         } catch (Exception ex) {
            logger.error("Error getting work definition match for artifact[%s]: Exception %s", artifact,
               ex.getLocalizedMessage());
         }
      }
      return match;
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
      String userId = artifact.getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null);
      return atsServer.getUserService().getUserById(userId);
   }

   @Override
   public Date getCreatedDate() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CreatedDate, null);
   }

   @Override
   public IAtsUser getCompletedBy() {
      String userId = artifact.getSoleAttributeValue(AtsAttributeTypes.CompletedBy, null);
      return atsServer.getUserService().getUserById(userId);
   }

   @Override
   public IAtsUser getCancelledBy() {
      String userId = artifact.getSoleAttributeValue(AtsAttributeTypes.CancelledBy, null);
      return atsServer.getUserService().getUserById(userId);
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
   public boolean isInWork() {
      return getStateMgr().getStateType().isInWork();
   }

   @Override
   public boolean isCompleted() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(StateType.Completed.name());
   }

   @Override
   public boolean isCompletedOrCancelled() {
      return isCompleted() || isCancelled();
   }

   @Override
   public boolean isCancelled() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(StateType.Cancelled.name());
   }

   @Override
   public List<IAtsUser> getImplementers() throws OseeCoreException {
      return atsServer.getImplementerService().getImplementers(this);
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
