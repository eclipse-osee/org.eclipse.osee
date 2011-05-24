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
package org.eclipse.osee.ats.core.task;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.EstimatedHoursUtil;
import org.eclipse.osee.ats.core.workflow.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.core.workflow.StateManager;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.workflow.transition.TransitionOption;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class TaskArtifact extends AbstractWorkflowArtifact implements IATSStateMachineArtifact {

   public TaskArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public boolean isRelatedToParentWorkflowCurrentState() throws OseeCoreException {
      return getSoleAttributeValueAsString(AtsAttributeTypes.RelatedToState, "").equals(
         getParentAWA().getStateMgr().getCurrentStateName());
   }

   public boolean isUsingTaskResolutionOptions() throws OseeCoreException {
      return getTaskResolutionOptionDefintions().size() > 0;
   }

   public List<TaskResOptionDefinition> getTaskResolutionOptionDefintions() throws OseeCoreException {
      TeamWorkFlowArtifact team = getParentTeamWorkflow();
      if (team == null) {
         return TaskResolutionOptionRule.EMPTY_TASK_RESOLUTION_OPTIONS;
      }
      return TaskResolutionOptionRule.getTaskResolutionOptions(team.getStateDefinition());
   }

   public TaskResOptionDefinition getTaskResolutionOptionDefinition(String optionName) throws OseeCoreException {
      for (TaskResOptionDefinition def : getTaskResolutionOptionDefintions()) {
         if (def.getName().equals(optionName)) {
            return def;
         }
      }
      return null;
   }

   public List<TaskResOptionDefinition> getTaskResolutionOptionDefintions(String stateName) throws OseeCoreException {
      TeamWorkFlowArtifact team = getParentTeamWorkflow();
      if (team == null) {
         return TaskResolutionOptionRule.EMPTY_TASK_RESOLUTION_OPTIONS;
      }
      return TaskResolutionOptionRule.getTaskResolutionOptions(team.getStateDefinitionByName(stateName));
   }

   public TaskResOptionDefinition getTaskResolutionOptionDefinition(String stateName, String optionName) throws OseeCoreException {
      for (TaskResOptionDefinition def : getTaskResolutionOptionDefintions(stateName)) {
         if (def.getName().equals(optionName)) {
            return def;
         }
      }
      return null;
   }

   @Override
   public String getDescription() {
      try {
         return getSoleAttributeValue(AtsAttributeTypes.Description, "");
      } catch (Exception ex) {
         return "Error: " + ex.getLocalizedMessage();
      }
   }

   public Result transitionToCompleted(double additionalHours, SkynetTransaction transaction, TransitionOption... transitionOption) {
      if (isInState(TeamState.Completed)) {
         return Result.TrueResult;
      }
      // Assign current user if unassigned
      try {
         if (getStateMgr().isUnAssigned()) {
            getStateMgr().setAssignee(UserManager.getUser());
         }
         getStateMgr().updateMetrics(additionalHours, 100, true);
         setSoleAttributeValue(AtsAttributeTypes.PercentComplete, 100);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      TransitionManager transitionMgr = new TransitionManager(this);
      Result result = transitionMgr.transition(TaskStates.Completed, (User) null, transaction, transitionOption);
      return result;
   }

   public Result transitionToInWork(IBasicUser toUser, int percentComplete, double additionalHours, SkynetTransaction transaction, TransitionOption... transitionOption) throws OseeCoreException {
      if (isInState(TaskStates.InWork)) {
         return Result.TrueResult;
      }
      TransitionManager transitionMgr = new TransitionManager(this);
      Result result = transitionMgr.transition(TaskStates.InWork, toUser, transaction, transitionOption);
      if (getStateMgr().getPercentComplete() != percentComplete || additionalHours > 0) {
         getStateMgr().updateMetrics(additionalHours, percentComplete, true);
         setSoleAttributeValue(AtsAttributeTypes.PercentComplete, percentComplete);
      }
      if (Collections.getAggregate(transitionOption).contains(TransitionOption.Persist)) {
         saveSMA(transaction);
      }
      return result;
   }

   /**
    * Tasks must transition in/out of completed when percent changes between 100 and <100. This method will handle these
    * cases.
    */
   public Result statusPercentChanged(double additionalHours, int percentComplete, SkynetTransaction transaction) throws OseeCoreException {
      if (percentComplete == 100 && !isCompleted()) {
         Result result = transitionToCompleted(additionalHours, transaction, TransitionOption.None);
         return result;
      } else if (percentComplete != 100 && isCompleted()) {
         Result result =
            transitionToInWork(UserManager.getUser(), percentComplete, additionalHours, transaction,
               TransitionOption.Persist);
         return result;
      }
      // Case where already completed and statusing, just add additional hours to InWork state
      else if (percentComplete == 100 && isCompleted()) {
         if (additionalHours > 0) {
            getStateMgr().updateMetrics(TaskStates.InWork, additionalHours, percentComplete, true);
            setSoleAttributeValue(AtsAttributeTypes.PercentComplete, percentComplete);
         }
      } else {
         getStateMgr().updateMetrics(additionalHours, percentComplete, true);
         setSoleAttributeValue(AtsAttributeTypes.PercentComplete, percentComplete);
      }
      return Result.TrueResult;
   }

   public Result parentWorkFlowTransitioned(StateDefinition fromState, StateDefinition toState, Collection<IBasicUser> toAssignees, boolean persist, SkynetTransaction transaction, TransitionOption... transitionOption) throws OseeCoreException {
      if (toState.getPageName().equals(TeamState.Cancelled.getPageName()) && isInWork()) {
         TransitionManager transitionMgr = new TransitionManager(this);
         Result result = transitionMgr.transitionToCancelled("Parent Cancelled", transaction, transitionOption);
         return result;
      } else if (fromState.getPageName().equals(TeamState.Cancelled.getPageName()) && isCancelled()) {
         Result result = transitionToInWork(UserManager.getUser(), 99, 0, transaction, transitionOption);
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
         getRelatedArtifacts(AtsRelationTypes.SmaToTask_Sma, AbstractWorkflowArtifact.class);
      if (awas.isEmpty()) {
         throw new OseeStateException("Task has no parent [%s]", getHumanReadableId());
      }
      parentAwa = awas.iterator().next();
      return parentAwa;
   }

   @Override
   public Artifact getParentActionArtifact() throws OseeCoreException {
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
   public Collection<IBasicUser> getImplementers() throws OseeCoreException {
      return StateManager.getImplementersByState(this, TaskStates.InWork);
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

   @Override
   public double getRemainHoursFromArtifact() throws OseeCoreException {
      if (isCompleted() || isCancelled()) {
         return 0;
      }
      double est = EstimatedHoursUtil.getEstimatedHours(this);
      if (PercentCompleteTotalUtil.getPercentCompleteTotal(this) == 0) {
         return est;
      }
      double percent = getStateMgr().getPercentComplete(TaskStates.InWork);
      if (percent == 0) {
         return est;
      }
      return est - ((est * percent) / 100.0);
   }

}
