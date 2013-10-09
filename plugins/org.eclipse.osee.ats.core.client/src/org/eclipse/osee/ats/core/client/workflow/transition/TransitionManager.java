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
package org.eclipse.osee.ats.core.client.workflow.transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.review.ValidateReviewManager;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.validator.AtsXWidgetValidateManagerClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.workflow.WorkflowManagerCore;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResult;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class TransitionManager {

   private final ITransitionHelper helper;
   private String completedCancellationReason = null;
   private SkynetTransaction transaction;
   private IAtsUser transitionAsUser;
   private Date transitionOnDate;
   private final Map<AbstractWorkflowArtifact, List<IAtsUser>> awasToAssignees =
      new HashMap<AbstractWorkflowArtifact, List<IAtsUser>>();

   public TransitionManager(ITransitionHelper helper) {
      this(helper, null);
   }

   public TransitionManager(ITransitionHelper helper, SkynetTransaction transaction) {
      this.helper = helper;
      this.transaction = transaction;
   }

   public TransitionResults handleAll() {
      TransitionResults results = new TransitionResults();

      handleTransitionValidation(results);
      if (results.isCancelled() || !results.isEmpty()) {
         return results;
      }
      handleTransitionUi(results);
      if (results.isCancelled() || !results.isEmpty()) {
         return results;
      }
      try {
         for (AbstractWorkflowArtifact awa : helper.getAwas()) {
            awa.setInTransition(true);
         }
         handleTransition(results);
      } finally {
         for (AbstractWorkflowArtifact awa : helper.getAwas()) {
            awa.setInTransition(false);
         }
      }
      return results;
   }

   /**
    * Validate AbstractWorkflowArtifact for transition including checking widget validation, rules, assignment, etc.
    * 
    * @return Result.isFalse if failure
    */
   public void handleTransitionValidation(TransitionResults results) {
      try {
         if (helper.getAwas().isEmpty()) {
            results.addResult(TransitionResult.NO_WORKFLOWS_PROVIDED_FOR_TRANSITION);
            return;
         }
         if (helper.getToStateName() == null) {
            results.addResult(TransitionResult.TO_STATE_CANT_BE_NULL);
            return;
         }
         if (helper.isSystemUser()) {
            results.addResult(TransitionResult.CAN_NOT_TRANSITION_AS_SYSTEM_USER);
            return;
         }
      } catch (OseeCoreException ex) {
         results.addResult(new TransitionResult(String.format("Exception while validating transition [%s]",
            helper.getName()), ex));
      }
      for (AbstractWorkflowArtifact awa : helper.getAwas()) {
         try {
            // Validate toState valid
            IAtsStateDefinition fromStateDef = awa.getStateDefinition();
            IAtsStateDefinition toStateDef = awa.getStateDefinitionByName(helper.getToStateName());
            if (toStateDef == null) {
               results.addResult(
                  awa,
                  new TransitionResult(String.format(
                     "Transition-To State [%s] does not exist for Work Definition [%s]", helper.getToStateName(),
                     awa.getWorkDefinition().getName())));
               continue;
            }

            //Ignore transitions to the same state
            if (!fromStateDef.equals(toStateDef)) {
               // Validate transition from fromState and toState
               if (!helper.isOverrideTransitionValidityCheck() && !fromStateDef.getToStates().contains(toStateDef) && !fromStateDef.getStateType().isCompletedOrCancelledState()) {
                  String errStr =
                     String.format("Work Definition [%s] is not configured to transition from \"[%s]\" to \"[%s]\"",
                        toStateDef.getName(), fromStateDef.getName(), toStateDef.getName());
                  OseeLog.log(Activator.class, Level.SEVERE, errStr);
                  results.addResult(awa, new TransitionResult(errStr));
                  continue;
               }

               // Validate Editable
               boolean stateIsEditable =
                  WorkflowManagerCore.isEditable(awa, awa.getStateDefinition(), helper.isPrivilegedEditEnabled());
               boolean currentlyUnAssigned = awa.getStateMgr().getAssignees().contains(AtsCoreUsers.UNASSIGNED_USER);
               awa.getStateMgr().validateNoBootstrapUser();
               boolean overrideAssigneeCheck = helper.isOverrideAssigneeCheck();
               // Allow anyone to transition any task to completed/cancelled/working if parent is working
               if (awa.isTask() && awa.getParentTeamWorkflow().isCompletedOrCancelled()) {
                  results.addResult(awa, TransitionResult.TASK_CANT_TRANSITION_IF_PARENT_COMPLETED);
                  continue;

               }
               // Else, only allow transition if...
               else if (!awa.isTask() && !stateIsEditable && !currentlyUnAssigned && !overrideAssigneeCheck) {
                  results.addResult(awa, TransitionResult.MUST_BE_ASSIGNED);
                  continue;
               }

               // Validate Working Branch
               isWorkingBranchTransitionable(results, awa, toStateDef);
               if (results.isCancelled()) {
                  continue;
               }

               // Validate Assignees (UnAssigned ok cause will be resolve to current user upon transition
               if (!overrideAssigneeCheck && !toStateDef.getStateType().isCancelledState() && helper.isSystemUserAssingee(awa)) {
                  results.addResult(awa, TransitionResult.CAN_NOT_TRANSITION_WITH_SYSTEM_USER_ASSIGNED);
                  continue;
               }

               // Validate state, widgets, rules unless OverrideAttributeValidation is set or transitioning to cancel
               isStateTransitionable(results, awa, toStateDef);
               if (results.isCancelled()) {
                  continue;
               }

               // Validate transition with extensions
               isTransitionValidForExtensions(results, awa, fromStateDef, toStateDef);
               if (results.isCancelled()) {
                  continue;
               }
            }
         } catch (OseeCoreException ex) {
            results.addResult(awa,
               new TransitionResult(String.format("Exception while validating transition [%s]", helper.getName()), ex));
         }
      }
   }

   public void isTransitionValidForExtensions(TransitionResults results, AbstractWorkflowArtifact awa, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef) {
      // Check extension points for valid transition
      for (ITransitionListener listener : TransitionListeners.getListeners()) {
         try {
            listener.transitioning(results, awa, fromStateDef, toStateDef, getToAssignees(awa, toStateDef));
            if (results.isCancelled() || !results.isEmpty()) {
               continue;
            }
         } catch (OseeCoreException ex) {
            results.addResult(
               awa,
               new TransitionResult(String.format("Exception [%s] while validating transition extensions 1 [%s]",
                  ex.getMessage(), helper.getName()), ex));
         }

      }

      // clear cache, so can be re-computed if above extension calls changed assignees
      awasToAssignees.remove(awa);

      // Check again in case first check made changes that would now keep transition from happening
      if (results.isEmpty()) {
         for (ITransitionListener listener : TransitionListeners.getListeners()) {
            try {
               listener.transitioning(results, awa, fromStateDef, toStateDef, getToAssignees(awa, toStateDef));
               if (results.isCancelled() || !results.isEmpty()) {
                  continue;
               }
            } catch (OseeCoreException ex) {
               results.addResult(
                  awa,
                  new TransitionResult(String.format("Exception [%s] while validating transition extensions 2 [%s]",
                     ex.getMessage(), helper.getName()), ex));
            }

         }
      }
   }

   /**
    * Request extra information if transition requires hours spent prompt, cancellation reason, etc.
    * 
    * @return Result.isFalse if failure or Result.isCancelled if canceled
    */
   public void handleTransitionUi(TransitionResults results) {
      Result result = helper.getCompleteOrCancellationReason();
      if (result.isCancelled()) {
         results.setCancelled(true);
         return;
      }
      if (result.isTrue()) {
         completedCancellationReason = result.getText();
      }
      result = helper.handleExtraHoursSpent();
      if (result.isCancelled()) {
         results.setCancelled(true);
      } else if (result.isFalse()) {
         results.addResult(new TransitionResult(result.getText()));
      }
   }

   /**
    * Process transition and persist changes to given skynet transaction
    * 
    * @return Result.isFalse if failure
    */
   public void handleTransition(TransitionResults results) {
      try {
         if (transaction == null) {
            transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), helper.getName());
         }
         for (AbstractWorkflowArtifact awa : helper.getAwas()) {
            try {
               IAtsStateDefinition fromState = awa.getStateDefinition();
               IAtsStateDefinition toState = awa.getStateDefinitionByName(helper.getToStateName());

               //Ignore transitions to the same state
               if (!fromState.equals(toState)) {
                  Date transitionDate = getTransitionOnDate();
                  IAtsUser transitionUser = getTransitionAsUser();
                  // Log transition
                  if (fromState.getStateType().isCancelledState()) {
                     logWorkflowUnCancelledEvent(awa, toState);
                  } else if (fromState.getStateType().isCompletedState()) {
                     logWorkflowUnCompletedEvent(awa, toState);
                  }
                  if (toState.getStateType().isCancelledState()) {
                     logWorkflowCancelledEvent(awa, fromState, toState, completedCancellationReason, transitionDate,
                        transitionUser);
                  } else if (toState.getStateType().isCompletedState()) {
                     logWorkflowCompletedEvent(awa, fromState, toState, completedCancellationReason, transitionDate,
                        transitionUser);
                  } else {
                     logStateCompletedEvent(awa, awa.getStateMgr().getCurrentStateName(), completedCancellationReason,
                        transitionDate, transitionUser);
                  }
                  logStateStartedEvent(awa, toState, transitionDate, transitionUser);

                  // Get transition to assignees, do some checking to ensure someone is assigneed and UnAssigned
                  List<? extends IAtsUser> updatedAssigees = getToAssignees(awa, toState);

                  awa.getStateMgr().transitionHelper(updatedAssigees, fromState, toState, completedCancellationReason);

                  if (awa.isValidationRequired() && awa.isTeamWorkflow()) {
                     ValidateReviewManager.createValidateReview((TeamWorkFlowArtifact) awa, false, transitionDate,
                        transitionUser, transaction);
                  }

                  // Persist
                  awa.persist(transaction);

                  awa.transitioned(fromState, toState, updatedAssigees, transaction);

                  // Notify extension points of transition
                  for (ITransitionListener listener : TransitionListeners.getListeners()) {
                     listener.transitioned(awa, fromState, toState, updatedAssigees, transaction);
                  }
                  if (toState.getStateType().isCompletedOrCancelledState()) {
                     awa.clearImplementersCache();
                  }
               }
            } catch (Exception ex) {
               results.addResult(awa,
                  new TransitionResult(String.format("Exception while transitioning [%s]", helper.getName()), ex));
            }
         }
      } catch (Exception ex) {
         results.addResult(new TransitionResult(String.format("Exception while transitioning [%s]", helper.getName()),
            ex));
      }

   }

   private void isWorkingBranchTransitionable(TransitionResults results, AbstractWorkflowArtifact awa, IAtsStateDefinition toStateDef) throws OseeCoreException {
      if (awa.isTeamWorkflow() && helper.isWorkingBranchInWork((TeamWorkFlowArtifact) awa)) {
         if (toStateDef.getName().equals(TeamState.Cancelled.getName())) {
            results.addResult(awa, TransitionResult.DELETE_WORKING_BRANCH_BEFORE_CANCEL);
         }
         if (helper.isBranchInCommit((TeamWorkFlowArtifact) awa)) {
            results.addResult(awa, TransitionResult.WORKING_BRANCH_BEING_COMMITTED);
         }
         if (!toStateDef.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch.name())) {
            results.addResult(awa, TransitionResult.WORKING_BRANCH_EXISTS);
         }
      }
   }

   private void isStateTransitionable(TransitionResults results, AbstractWorkflowArtifact awa, IAtsStateDefinition toStateDef) throws OseeCoreException {
      boolean isOverrideAttributeValidationState =
         helper.isOverrideTransitionValidityCheck() || awa.getStateDefinition().getOverrideAttributeValidationStates().contains(
            toStateDef);
      if (toStateDef.getStateType().isCancelledState()) {
         validateTaskCompletion(results, awa, toStateDef);
         validateReviewsCancelled(results, awa, toStateDef);
      } else if (!toStateDef.getStateType().isCancelledState() && !isOverrideAttributeValidationState) {

         // Validate XWidgets for transition
         Collection<WidgetResult> widgetResults =
            AtsXWidgetValidateManagerClient.instance.validateTransition(awa, toStateDef);
         for (WidgetResult widgetResult : widgetResults) {
            if (!widgetResult.isValid()) {
               results.addResult(awa, widgetResult);
            }
         }

         validateTaskCompletion(results, awa, toStateDef);

         // Don't transition without targeted version if so configured
         boolean teamDefRequiresTargetedVersion = awa.teamDefHasRule(RuleDefinitionOption.RequireTargetedVersion);
         boolean pageRequiresTargetedVersion =
            awa.getStateDefinition().hasRule(RuleDefinitionOption.RequireTargetedVersion.name());

         // Only check this if TeamWorkflow, not for reviews
         if (awa.isOfType(AtsArtifactTypes.TeamWorkflow) && (teamDefRequiresTargetedVersion || pageRequiresTargetedVersion) && //
         !AtsVersionService.get().hasTargetedVersion(awa) && //
         !toStateDef.getStateType().isCancelledState()) {
            results.addResult(awa, TransitionResult.MUST_BE_TARGETED_FOR_VERSION);
         }

         // Loop through this state's blocking reviews to confirm complete
         if (awa.isTeamWorkflow()) {
            for (AbstractReviewArtifact reviewArt : ReviewManager.getReviewsFromCurrentState((TeamWorkFlowArtifact) awa)) {
               if (reviewArt.getReviewBlockType() == ReviewBlockType.Transition && !reviewArt.isCompletedOrCancelled()) {
                  results.addResult(awa, TransitionResult.COMPLETE_BLOCKING_REVIEWS);
               }
            }
         }
      }
   }

   private void validateReviewsCancelled(TransitionResults results, AbstractWorkflowArtifact awa, IAtsStateDefinition toStateDef) throws OseeCoreException {
      if (awa instanceof TeamWorkFlowArtifact && toStateDef.getStateType().isCancelledState()) {
         for (AbstractReviewArtifact reviewArt : ReviewManager.getReviewsFromCurrentState((TeamWorkFlowArtifact) awa)) {
            if (reviewArt.getReviewBlockType() == ReviewBlockType.Transition && !reviewArt.isCompletedOrCancelled()) {
               results.addResult(awa, TransitionResult.CANCEL_REVIEWS_BEFORE_CANCEL);
               break;
            }
         }
      }
   }

   private void validateTaskCompletion(TransitionResults results, AbstractWorkflowArtifact awa, IAtsStateDefinition toStateDef) throws OseeCoreException {
      // Loop through this state's tasks to confirm complete
      boolean checkTasksCompletedForState = true;
      // Don't check for task completion if transition to working state and AllowTransitionWithoutTaskCompletion rule is set
      if (awa.getStateDefinition().hasRule(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion.name()) && toStateDef.getStateType().isWorkingState()) {
         checkTasksCompletedForState = false;
      }
      if (checkTasksCompletedForState && awa instanceof AbstractTaskableArtifact && !awa.isCompletedOrCancelled()) {
         Set<TaskArtifact> tasksToCheck = new HashSet<TaskArtifact>();
         // If transitioning to completed/cancelled, all tasks must be completed/cancelled
         if (toStateDef.getStateType().isCompletedOrCancelledState()) {
            tasksToCheck.addAll(((AbstractTaskableArtifact) awa).getTaskArtifacts());
         }
         // Else, just check current state tasks
         else {
            tasksToCheck.addAll(((AbstractTaskableArtifact) awa).getTaskArtifactsFromCurrentState());
         }
         for (TaskArtifact taskArt : tasksToCheck) {
            if (taskArt.isInWork()) {
               results.addResult(awa, TransitionResult.TASKS_NOT_COMPLETED);
               break;
            }
         }
      }
   }

   public static void logWorkflowCancelledEvent(AbstractWorkflowArtifact awa, IAtsStateDefinition fromState, IAtsStateDefinition toState, String reason, Date cancelDate, IAtsUser cancelBy) throws OseeCoreException {
      awa.getLog().addLog(LogType.StateCancelled, fromState.getName(), reason, cancelDate, cancelBy);
      if (awa.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         awa.setSoleAttributeValue(AtsAttributeTypes.CancelledBy, cancelBy.getUserId());
         awa.setSoleAttributeValue(AtsAttributeTypes.CancelledDate, cancelDate);
         awa.setSoleAttributeValue(AtsAttributeTypes.CancelledReason, reason);
         awa.setSoleAttributeValue(AtsAttributeTypes.CancelledFromState, fromState.getName());
      }
      validateUpdatePercentCompleteAttribute(awa, toState);
   }

   public static void logWorkflowUnCancelledEvent(AbstractWorkflowArtifact awa, IAtsStateDefinition toState) throws OseeCoreException {
      if (awa.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         awa.deleteSoleAttribute(AtsAttributeTypes.CancelledBy);
         awa.deleteSoleAttribute(AtsAttributeTypes.CancelledDate);
         awa.deleteSoleAttribute(AtsAttributeTypes.CancelledReason);
         awa.deleteSoleAttribute(AtsAttributeTypes.CancelledFromState);
      }
      validateUpdatePercentCompleteAttribute(awa, toState);
   }

   private void logWorkflowCompletedEvent(AbstractWorkflowArtifact awa, IAtsStateDefinition fromState, IAtsStateDefinition toState, String reason, Date cancelDate, IAtsUser cancelBy) throws OseeCoreException {
      awa.getLog().addLog(LogType.StateComplete, fromState.getName(), Strings.isValid(reason) ? reason : "",
         cancelDate, cancelBy);
      if (awa.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedBy, cancelBy.getUserId());
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedDate, cancelDate);
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedFromState, fromState.getName());
      }
      validateUpdatePercentCompleteAttribute(awa, toState);
   }

   public static void logWorkflowUnCompletedEvent(AbstractWorkflowArtifact awa, IAtsStateDefinition toState) throws OseeCoreException {
      if (awa.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         awa.deleteSoleAttribute(AtsAttributeTypes.CompletedBy);
         awa.deleteSoleAttribute(AtsAttributeTypes.CompletedDate);
         awa.deleteSoleAttribute(AtsAttributeTypes.CompletedFromState);
      }
      validateUpdatePercentCompleteAttribute(awa, toState);
   }

   private static void validateUpdatePercentCompleteAttribute(AbstractWorkflowArtifact awa, IAtsStateDefinition toState) throws OseeCoreException {
      Integer percent = awa.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
      if (toState.getStateType().isCompletedOrCancelledState() && percent != 100) {
         awa.setSoleAttributeValue(AtsAttributeTypes.PercentComplete, 100);
      } else if (toState.getStateType().isWorkingState() && percent == 100) {
         awa.setSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
      }
   }

   private void logStateCompletedEvent(AbstractWorkflowArtifact awa, String fromStateName, String reason, Date date, IAtsUser user) throws OseeCoreException {
      awa.getLog().addLog(LogType.StateComplete, fromStateName, Strings.isValid(reason) ? reason : "", date, user);
   }

   public static void logStateStartedEvent(AbstractWorkflowArtifact awa, IStateToken state, Date date, IAtsUser user) throws OseeCoreException {
      awa.getLog().addLog(LogType.StateEntered, state.getName(), "", date, user);
   }

   public SkynetTransaction getTransaction() {
      return transaction;
   }

   /**
    * Add listener for notification. This is not the recommended method of listening, use
    * org.eclipse.osee.ats.core.AtsTransitionListener extension point.
    */
   public static void addListener(ITransitionListener listener) {
      TransitionListeners.addListener(listener);
   }

   public static void removeListener(ITransitionListener listener) {
      TransitionListeners.removeListener(listener);
   }

   /**
    * Allow transition date to be used in log to be overridden for importing Actions from other systems and other
    * programatic transitions.
    */
   public IAtsUser getTransitionAsUser() throws OseeCoreException {
      if (transitionAsUser == null) {
         return AtsClientService.get().getUserAdmin().getCurrentUser();
      }
      return transitionAsUser;
   }

   public void setTransitionAsUser(IAtsUser transitionAsUser) {
      this.transitionAsUser = transitionAsUser;
   }

   /**
    * Allow transition date to be used in log to be overridden for importing Actions from other systems and other
    * programatic transitions.
    */
   public Date getTransitionOnDate() {
      if (transitionOnDate == null) {
         return new Date();
      }
      return transitionOnDate;
   }

   public void setTransitionOnDate(Date transitionOnDate) {
      this.transitionOnDate = transitionOnDate;
   }

   /**
    * Get transition to assignees. Verify that UnAssigned is not selected with another assignee. Ensure an assignee is
    * entered, else use current user or UnAssigneed if current user is SystemUser.
    */
   public List<? extends IAtsUser> getToAssignees(AbstractWorkflowArtifact awa, IAtsStateDefinition toState) throws OseeCoreException {
      List<IAtsUser> assignees = awasToAssignees.get(awa);
      if (assignees != null && toState.getStateType().isCompletedOrCancelledState()) {
         assignees.clear();
      }
      if (assignees == null) {
         assignees = new ArrayList<IAtsUser>();
         awasToAssignees.put(awa, assignees);
         if (!toState.getStateType().isCompletedOrCancelledState()) {
            Collection<? extends IAtsUser> requestedAssignees = helper.getToAssignees(awa);
            if (requestedAssignees != null) {
               for (IAtsUser user : requestedAssignees) {
                  assignees.add(user);
               }
            }
            if (assignees.contains(AtsCoreUsers.UNASSIGNED_USER)) {
               assignees.remove(AtsCoreUsers.UNASSIGNED_USER);
               assignees.add(AtsClientService.get().getUserAdmin().getCurrentUser());
            }
            if (assignees.isEmpty()) {
               if (helper.isSystemUser()) {
                  assignees.add(AtsCoreUsers.UNASSIGNED_USER);
               } else {
                  assignees.add(AtsClientService.get().getUserAdmin().getCurrentUser());
               }
            }
         }
      }
      return assignees;
   }

}
