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
package org.eclipse.osee.ats.core.client.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class TaskArtifact extends AbstractWorkflowArtifact implements IAtsTask, IATSStateMachineArtifact {

   public TaskArtifact(String guid, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(guid, branch, artifactType);
   }

   public boolean isRelatedToParentWorkflowCurrentState() throws OseeCoreException {
      return getSoleAttributeValueAsString(AtsAttributeTypes.RelatedToState, "").equals(
         getParentAWA().getStateMgr().getCurrentStateName());
   }

   public boolean isRelatedToUsed() throws OseeCoreException {
      return Strings.isValid(getSoleAttributeValueAsString(AtsAttributeTypes.RelatedToState, ""));
   }

   @Override
   public String getDescription() {
      try {
         return getSoleAttributeValue(AtsAttributeTypes.Description, "");
      } catch (Exception ex) {
         return "Error: " + ex.getLocalizedMessage();
      }
   }

   public Result parentWorkFlowTransitioned(IAtsStateDefinition fromState, IAtsStateDefinition toState, Collection<? extends IAtsUser> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
      if (toState.getName().equals(TeamState.Cancelled.getName()) && isInWork()) {
         TransitionHelper helper =
            new TransitionHelper("Transition to Cancelled", Arrays.asList(this), TaskStates.Cancelled.getName(), null,
               "Parent Cancelled", TransitionOption.None);
         TransitionManager transitionMgr = new TransitionManager(helper, transaction);
         TransitionResults results = transitionMgr.handleAll();
         if (!results.isEmpty()) {
            return new Result("Transition Error %s", results.toString());
         }
      } else if (fromState.getName().equals(TeamState.Cancelled.getName()) && isCancelled()) {
         Result result =
            TaskManager.transitionToInWork(this, AtsClientService.get().getUserAdmin().getCurrentUser(), 99, 0,
               transaction);
         return result;
      }
      return Result.TrueResult;
   }

   @Override
   public double getManHrsPerDayPreference() throws OseeCoreException {
      return getParentAWA().getManHrsPerDayPreference();
   }

   @Override
   public AbstractWorkflowArtifact getParentAWA() throws OseeCoreException {
      if (parentAwa != null) {
         return parentAwa;
      }
      Collection<AbstractWorkflowArtifact> awas =
         getRelatedArtifacts(AtsRelationTypes.TeamWfToTask_TeamWf, AbstractWorkflowArtifact.class);
      if (awas.isEmpty()) {
         throw new OseeStateException("Task has no parent [%s]", getAtsId());
      }
      parentAwa = awas.iterator().next();
      return parentAwa;
   }

   @Override
   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      if (parentAction != null) {
         return parentAction;
      }
      parentAction = getParentTeamWorkflow().getParentActionArtifact();
      return parentAction;
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      if (parentTeamArt != null) {
         return parentTeamArt;
      }
      AbstractWorkflowArtifact awa = getParentAWA();
      if (awa.isTeamWorkflow()) {
         parentTeamArt = (TeamWorkFlowArtifact) awa;
      }
      return parentTeamArt;
   }

   @Override
   public double getWorldViewWeeklyBenefit() {
      return 0;
   }

   @Override
   public String getWorldViewSWEnhancement() throws OseeCoreException {
      AbstractWorkflowArtifact awa = getParentAWA();
      if (awa != null) {
         return awa.getWorldViewSWEnhancement();
      }
      return "";
   }

   public IAtsLogItem getLogItemWithTypeAsOfDate(LogType logType, Date date) throws OseeCoreException {
      IAtsLogItem retLogItem = null;
      IAtsLog atsLog = getLog();
      List<IAtsLogItem> logItems = atsLog.getLogItems();
      for (IAtsLogItem logItem : logItems) {
         if (logItem.getType().equals(logType)) {
            Date logItemDate = logItem.getDate();
            if (logItemDate.after(date)) {
               break;
            } else {
               retLogItem = logItem;
            }
         }
      }

      return retLogItem;
   }
}
