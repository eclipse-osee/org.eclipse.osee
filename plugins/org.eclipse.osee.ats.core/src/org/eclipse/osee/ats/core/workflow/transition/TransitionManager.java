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
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.task.IAtsTaskService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.workflow.WorkflowManagerCore;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class TransitionManager implements IAtsTransitionManager, IExecuteListener {

   private final ITransitionHelper helper;
   private String completedCancellationReason = null;
   private Date transitionOnDate;
   private final IAtsUserService userService;
   private final IAtsReviewService reviewService;
   private final IAtsWorkItemService workItemService;
   private final IAtsTaskService taskService;
   private final IAtsWorkDefinitionService workDefService;
   private final IAttributeResolver attrResolver;
   private final Map<IAtsWorkItem, String> workItemFromStateMap;

   public TransitionManager(ITransitionHelper helper) {
      this.helper = helper;
      this.userService = helper.getServices().getUserService();
      this.reviewService = helper.getServices().getReviewService();
      this.workItemService = helper.getServices().getWorkItemService();
      this.workDefService = helper.getServices().getWorkDefService();
      this.attrResolver = helper.getServices().getAttributeResolver();
      this.taskService = helper.getServices().getTaskService();
      this.workItemFromStateMap = new HashMap<>();
   }

   @Override
   public TransitionResults handleAll() {
      TransitionResults results = new TransitionResults();
      handleWorkflowReload(results);
      if (results.isCancelled() || !results.isEmpty()) {
         return results;
      }
      handleTransitionValidation(results);
      if (results.isCancelled() || !results.isEmpty()) {
         return results;
      }
      handleTransitionUi(results);
      if (results.isCancelled() || !results.isEmpty()) {
         return results;
      }
      handleTransition(results);
      return results;
   }

   private void handleWorkflowReload(TransitionResults results) {
      if (helper.isReload()) {
         helper.handleWorkflowReload(results);
      }
   }

   /**
    * Validate AbstractWorkflowArtifact for transition including checking widget validation, rules, assignment, etc.
    *
    * @return Result.isFalse if failure
    */
   @Override
   public void handleTransitionValidation(TransitionResults results) {
      boolean overrideAssigneeCheck = helper.isOverrideAssigneeCheck();
      try {
         if (helper.getWorkItems().isEmpty()) {
            results.addResult(TransitionResult.NO_WORKFLOWS_PROVIDED_FOR_TRANSITION);
            return;
         }
         if (helper.getToStateName() == null) {
            results.addResult(TransitionResult.TO_STATE_CANT_BE_NULL);
            return;
         }
         if (!overrideAssigneeCheck && helper.isSystemUser()) {
            results.addResult(TransitionResult.CAN_NOT_TRANSITION_AS_SYSTEM_USER);
            return;
         }
      } catch (OseeCoreException ex) {
         results.addResult(
            new TransitionResult(String.format("Exception while validating transition [%s]", helper.getName()), ex));
      }
      for (IAtsWorkItem workItem : helper.getWorkItems()) {
         try {
            helper.getChangeSet().add(workItem);
            // Validate toState valid
            IAtsStateDefinition fromStateDef = workItem.getStateDefinition();
            IAtsStateDefinition toStateDef = workItem.getWorkDefinition().getStateByName(helper.getToStateName());
            if (toStateDef == null) {
               results.addResult(workItem,
                  new TransitionResult(String.format("Transition-To State [%s] does not exist for Work Definition [%s]",
                     helper.getToStateName(), workItem.getWorkDefinition().getName())));
               continue;
            }

            //Ignore transitions to the same state
            if (!fromStateDef.equals(toStateDef)) {
               // Validate transition from fromState and toState
               if (!helper.isOverrideTransitionValidityCheck() && !fromStateDef.getToStates().contains(
                  toStateDef) && !fromStateDef.getStateType().isCompletedOrCancelledState()) {
                  String errStr =
                     String.format("Work Definition [%s] is not configured to transition from \"[%s]\" to \"[%s]\"",
                        fromStateDef.getWorkDefinition().getName(), fromStateDef.getName(), toStateDef.getName());
                  OseeLog.log(TransitionManager.class, Level.SEVERE, errStr);
                  results.addResult(workItem, new TransitionResult(errStr));
                  continue;
               }

               // Validate Editable
               boolean stateIsEditable = WorkflowManagerCore.isEditable(workItem, workItem.getStateDefinition(),
                  helper.isPrivilegedEditEnabled(), helper.getTransitionUser(),
                  userService.isAtsAdmin(helper.getTransitionUser()));
               boolean currentlyUnAssignedOrCompletedOrCancelled =
                  workItem.isCompletedOrCancelled() || workItem.getStateMgr().getAssignees().contains(
                     AtsCoreUsers.UNASSIGNED_USER);
               workItem.getStateMgr().validateNoBootstrapUser();
               // Allow anyone to transition any task to completed/cancelled/working if parent is working
               if (workItem.isTask() && workItem.getParentTeamWorkflow().getStateMgr().getStateType().isCompletedOrCancelled()) {
                  results.addResult(workItem, TransitionResult.TASK_CANT_TRANSITION_IF_PARENT_COMPLETED);
                  continue;
               }
               // Else, only allow transition if...
               else if (!workItem.isTask() && !stateIsEditable && !currentlyUnAssignedOrCompletedOrCancelled && !overrideAssigneeCheck) {
                  results.addResult(workItem, TransitionResult.MUST_BE_ASSIGNED);
                  continue;
               }

               // Validate Working Branch
               isWorkingBranchTransitionable(results, workItem, toStateDef);
               if (results.isCancelled()) {
                  continue;
               }

               // Validate Assignees (UnAssigned ok cause will be resolve to current user upon transition
               if (!overrideAssigneeCheck && !toStateDef.getStateType().isCancelledState() && helper.isSystemUserAssingee(
                  workItem)) {
                  results.addResult(workItem, TransitionResult.CAN_NOT_TRANSITION_WITH_SYSTEM_USER_ASSIGNED);
                  continue;
               }

               // Validate state, widgets, rules unless OverrideAttributeValidation is set or transitioning to cancel
               isStateTransitionable(results, workItem, toStateDef);
               if (results.isCancelled()) {
                  continue;
               }

               // Validate transition with extensions
               isTransitionValidForExtensions(results, workItem, fromStateDef, toStateDef);
               if (results.isCancelled()) {
                  continue;
               }

               if (helper.isExecuteChanges()) {
                  helper.getChangeSet().execute();
               }
            }
         } catch (OseeCoreException ex) {
            results.addResult(workItem,
               new TransitionResult(String.format("Exception while validating transition [%s]", helper.getName()), ex));
         }
      }
   }

   @Override
   public void isTransitionValidForExtensions(TransitionResults results, IAtsWorkItem workItem, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef) {
      // Check extension points for valid transition
      for (ITransitionListener listener : helper.getTransitionListeners()) {
         try {
            listener.transitioning(results, workItem, fromStateDef, toStateDef, getToAssignees(workItem, toStateDef));
            if (results.isCancelled() || !results.isEmpty()) {
               continue;
            }
         } catch (OseeCoreException ex) {
            results.addResult(workItem,
               new TransitionResult(String.format("Exception [%s] while validating transition extensions 1 [%s]",
                  ex.getMessage(), helper.getName()), ex));
         }

      }

      // Check again in case first check made changes that would now keep transition from happening
      if (results.isEmpty()) {
         for (ITransitionListener listener : helper.getTransitionListeners()) {
            try {
               listener.transitioning(results, workItem, fromStateDef, toStateDef,
                  getToAssignees(workItem, toStateDef));
               if (results.isCancelled() || !results.isEmpty()) {
                  continue;
               }
            } catch (OseeCoreException ex) {
               results.addResult(workItem,
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
   @Override
   public void handleTransitionUi(TransitionResults results) {
      Result result = helper.getCompleteOrCancellationReason();
      if (result.isCancelled()) {
         results.setCancelled(true);
         return;
      }
      if (result.isTrue()) {
         completedCancellationReason = result.getText();
      }
      result = helper.handleExtraHoursSpent(helper.getChangeSet());
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
   @Override
   public void handleTransition(TransitionResults results) {
      try {
         helper.getChangeSet().addExecuteListener(this);
         for (IAtsWorkItem workItem : helper.getWorkItems()) {
            try {
               IAtsStateDefinition fromState = workItem.getStateDefinition();
               IAtsStateDefinition toState = workItem.getWorkDefinition().getStateByName(helper.getToStateName());

               //Ignore transitions to the same state
               if (!fromState.equals(toState)) {
                  Date transitionDate = getTransitionOnDate();
                  IAtsUser transitionUser = getTransitionAsUser();
                  // Log transition
                  if (fromState.getStateType().isCancelledState()) {
                     logWorkflowUnCancelledEvent(workItem, toState, helper.getChangeSet(), attrResolver);
                  } else if (fromState.getStateType().isCompletedState()) {
                     logWorkflowUnCompletedEvent(workItem, toState, helper.getChangeSet(), attrResolver);
                  }
                  if (toState.getStateType().isCancelledState()) {
                     logWorkflowCancelledEvent(workItem, fromState, toState, completedCancellationReason,
                        transitionDate, transitionUser, helper.getChangeSet(), attrResolver);
                  } else if (toState.getStateType().isCompletedState()) {
                     logWorkflowCompletedEvent(workItem, fromState, toState, completedCancellationReason,
                        transitionDate, transitionUser, helper.getChangeSet());
                  } else {
                     logStateCompletedEvent(workItem, workItem.getStateMgr().getCurrentStateName(),
                        completedCancellationReason, transitionDate, transitionUser);
                  }
                  logStateStartedEvent(workItem, toState, transitionDate, transitionUser);

                  // Get transition to assignees, do some checking to ensure someone is assigneed and UnAssigned
                  List<? extends IAtsUser> updatedAssigees = getToAssignees(workItem, toState);

                  workItem.getStateMgr().transitionHelper(updatedAssigees, fromState, toState,
                     completedCancellationReason);

                  // Create validation review if in correct state and TeamWorkflow
                  if (reviewService.isValidationReviewRequired(workItem) && workItem.isTeamWorkflow()) {
                     IAtsDecisionReview review = reviewService.createValidateReview((IAtsTeamWorkflow) workItem, false,
                        transitionDate, transitionUser, helper.getChangeSet());
                     if (review != null) {
                        helper.getChangeSet().add(review);
                     }
                  }

                  // Notify extension points of transition
                  for (ITransitionListener listener : helper.getTransitionListeners()) {
                     listener.transitioned(workItem, fromState, toState, updatedAssigees, helper.getChangeSet());
                  }
                  if (toState.getStateType().isCompletedOrCancelledState()) {
                     workItemService.clearImplementersCache(workItem);
                  }
                  helper.getChangeSet().add(workItem);

                  workItemFromStateMap.put(workItem, fromState.getName());
               }

            } catch (Exception ex) {
               results.addResult(workItem,
                  new TransitionResult(String.format("Exception while transitioning [%s]", helper.getName()), ex));
            }
         }
         if (results.isEmpty() && helper.isExecuteChanges()) {
            helper.getChangeSet().execute();
         }
      } catch (Exception ex) {
         results.addResult(
            new TransitionResult(String.format("Exception while transitioning [%s]", helper.getName()), ex));
      }

   }

   private void isWorkingBranchTransitionable(TransitionResults results, IAtsWorkItem workItem, IAtsStateDefinition toStateDef) throws OseeCoreException {
      if (workItem.isTeamWorkflow() && helper.isWorkingBranchInWork((IAtsTeamWorkflow) workItem)) {
         if (toStateDef.getName().equals(TeamState.Cancelled.getName())) {
            results.addResult(workItem, TransitionResult.DELETE_WORKING_BRANCH_BEFORE_CANCEL);
         }
         if (helper.isBranchInCommit((IAtsTeamWorkflow) workItem)) {
            results.addResult(workItem, TransitionResult.WORKING_BRANCH_BEING_COMMITTED);
         }
         if (!toStateDef.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch.name())) {
            results.addResult(workItem, TransitionResult.WORKING_BRANCH_EXISTS);
         }
      }
   }

   private void isStateTransitionable(TransitionResults results, IAtsWorkItem workItem, IAtsStateDefinition toStateDef) throws OseeCoreException {
      boolean isOverrideAttributeValidationState =
         helper.isOverrideTransitionValidityCheck() || workItem.getStateDefinition().getOverrideAttributeValidationStates().contains(
            toStateDef);
      if (toStateDef.getStateType().isCancelledState()) {
         validateTaskCompletion(results, workItem, toStateDef, taskService);
         validateReviewsCancelled(results, workItem, toStateDef);
      } else if (!toStateDef.getStateType().isCancelledState() && !isOverrideAttributeValidationState) {

         // Validate XWidgets for transition
         Collection<WidgetResult> widgetResults = workItemService.validateWidgetTransition(workItem, toStateDef);
         for (WidgetResult widgetResult : widgetResults) {
            if (!widgetResult.isValid()) {
               results.addResult(workItem, widgetResult);
            }
         }

         validateTaskCompletion(results, workItem, toStateDef, taskService);

         // Don't transition without targeted version if so configured
         boolean teamDefRequiresTargetedVersion =
            workDefService.teamDefHasRule(workItem, RuleDefinitionOption.RequireTargetedVersion);
         boolean pageRequiresTargetedVersion =
            workItem.getStateDefinition().hasRule(RuleDefinitionOption.RequireTargetedVersion.name());

         // Only check this if TeamWorkflow, not for reviews
         if (workItem.isTeamWorkflow() && (teamDefRequiresTargetedVersion || pageRequiresTargetedVersion) && //
            !helper.getServices().getVersionService().hasTargetedVersion(workItem) && //
            !toStateDef.getStateType().isCancelledState()) {
            results.addResult(workItem, TransitionResult.MUST_BE_TARGETED_FOR_VERSION);
         }

         // Loop through this state's blocking reviews to confirm complete
         if (workItem.isTeamWorkflow()) {
            for (IAtsAbstractReview review : reviewService.getReviewsFromCurrentState((IAtsTeamWorkflow) workItem)) {
               if (reviewService.getReviewBlockType(
                  review) == ReviewBlockType.Transition && !review.getStateMgr().getStateType().isCompletedOrCancelled()) {
                  results.addResult(workItem, TransitionResult.COMPLETE_BLOCKING_REVIEWS);
               }
            }
         }
      }
   }

   private void validateReviewsCancelled(TransitionResults results, IAtsWorkItem workItem, IAtsStateDefinition toStateDef) throws OseeCoreException {
      if (workItem.isTeamWorkflow() && toStateDef.getStateType().isCancelledState()) {
         for (IAtsAbstractReview review : reviewService.getReviewsFromCurrentState((IAtsTeamWorkflow) workItem)) {
            ReviewBlockType reviewBlockType = reviewService.getReviewBlockType(review);
            boolean completedOrCancelled = review.getStateMgr().getStateType().isCompletedOrCancelled();
            if (reviewBlockType == ReviewBlockType.Transition && !completedOrCancelled) {
               results.addResult(workItem, TransitionResult.CANCEL_REVIEWS_BEFORE_CANCEL);
               break;
            }
         }
      }
   }

   public static void validateTaskCompletion(TransitionResults results, IAtsWorkItem workItem, IAtsStateDefinition toStateDef, IAtsTaskService taskService) throws OseeCoreException {
      if (!workItem.isTeamWorkflow()) {
         return;
      }
      // Loop through this state's tasks to confirm complete
      boolean checkTasksCompletedForState = true;
      // Don't check for task completion if transition to working state and AllowTransitionWithoutTaskCompletion rule is set
      if (workItem.getStateDefinition().hasRule(
         RuleDefinitionOption.AllowTransitionWithoutTaskCompletion.name()) && toStateDef.getStateType().isWorkingState()) {
         checkTasksCompletedForState = false;
      }
      if (checkTasksCompletedForState && workItem.getStateMgr().getStateType().isInWork()) {
         Set<IAtsTask> tasksToCheck = new HashSet<>();
         // If transitioning to completed/cancelled, all tasks must be completed/cancelled
         if (toStateDef.getStateType().isCompletedOrCancelledState()) {
            tasksToCheck.addAll(taskService.getTask(workItem));
         }
         // Else, just check current state tasks
         else {
            tasksToCheck.addAll(taskService.getTasks(workItem, workItem.getStateDefinition()));
         }
         for (IAtsTask task : tasksToCheck) {
            if (task.getStateMgr().getStateType().isInWork()) {
               results.addResult(workItem, TransitionResult.TASKS_NOT_COMPLETED);
               break;
            }
         }
      }
   }

   public static void logWorkflowCancelledEvent(IAtsWorkItem workItem, IAtsStateDefinition fromState, IAtsStateDefinition toState, String reason, Date cancelDate, IAtsUser cancelBy, IAtsChangeSet changes, IAttributeResolver attrResolver) throws OseeCoreException {
      workItem.getLog().addLog(LogType.StateCancelled, fromState.getName(), reason, cancelDate, cancelBy.getUserId());
      if (attrResolver.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledBy, cancelBy.getUserId(), changes);
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledDate, cancelDate, changes);
         if (Strings.isValid(reason)) {
            attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledReason, reason, changes);
         }
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledFromState, fromState.getName(),
            changes);
      }
      validateUpdatePercentComplete(workItem, toState, changes);
   }

   public static void logWorkflowUnCancelledEvent(IAtsWorkItem workItem, IAtsStateDefinition toState, IAtsChangeSet changes, IAttributeResolver attrResolver) throws OseeCoreException {
      if (attrResolver.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledBy, changes);
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledDate, changes);
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledReason, changes);
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledFromState, changes);
      }
      validateUpdatePercentComplete(workItem, toState, changes);
   }

   private void logWorkflowCompletedEvent(IAtsWorkItem workItem, IAtsStateDefinition fromState, IAtsStateDefinition toState, String reason, Date cancelDate, IAtsUser cancelBy, IAtsChangeSet changes) throws OseeCoreException {
      workItem.getLog().addLog(LogType.StateComplete, fromState.getName(), Strings.isValid(reason) ? reason : "",
         cancelDate, cancelBy.getUserId());
      if (attrResolver.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedBy, cancelBy.getUserId(), changes);
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedDate, cancelDate, changes);
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedFromState, fromState.getName(),
            changes);
      }
      validateUpdatePercentComplete(workItem, toState, changes);
   }

   public static void logWorkflowUnCompletedEvent(IAtsWorkItem workItem, IAtsStateDefinition toState, IAtsChangeSet changes, IAttributeResolver attrResolver) throws OseeCoreException {
      if (attrResolver.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedBy, changes);
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedDate, changes);
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedFromState, changes);
      }
      validateUpdatePercentComplete(workItem, toState, changes);
   }

   private static void validateUpdatePercentComplete(IAtsWorkItem workItem, IAtsStateDefinition toState, IAtsChangeSet changes) {
      IAtsStateManager stateMgr = workItem.getStateMgr();
      Integer percent = stateMgr.getPercentCompleteValue();
      if (percent == null) {
         percent = 0;
      }
      if (toState.getStateType().isCompletedOrCancelledState() && percent != 100) {
         stateMgr.setPercentCompleteValue(100);
         changes.add(workItem);
      } else if (toState.getStateType().isWorkingState() && percent == 100) {
         stateMgr.setPercentCompleteValue(0);
         changes.add(workItem);
      }
   }

   private void logStateCompletedEvent(IAtsWorkItem workItem, String fromStateName, String reason, Date date, IAtsUser user) throws OseeCoreException {
      workItem.getLog().addLog(LogType.StateComplete, fromStateName, Strings.isValid(reason) ? reason : "", date,
         user.getUserId());
   }

   public static void logStateStartedEvent(IAtsWorkItem workItem, IStateToken state, Date date, IAtsUser user) throws OseeCoreException {
      workItem.getLog().addLog(LogType.StateEntered, state.getName(), "", date, user.getUserId());
   }

   /**
    * Allow transition date to be used in log to be overridden for importing Actions from other systems and other
    * programatic transitions.
    */
   @Override
   public IAtsUser getTransitionAsUser() throws OseeCoreException {
      IAtsUser user = helper.getTransitionUser();
      if (user == null) {
         user = userService.getCurrentUser();
      }
      return user;
   }

   /**
    * Allow transition date to be used in log to be overridden for importing Actions from other systems and other
    * programatic transitions.
    */
   @Override
   public Date getTransitionOnDate() {
      if (transitionOnDate == null) {
         return new Date();
      }
      return transitionOnDate;
   }

   @Override
   public void setTransitionOnDate(Date transitionOnDate) {
      this.transitionOnDate = transitionOnDate;
   }

   /**
    * Get transition to assignees. Verify that UnAssigned is not selected with another assignee. Ensure an assignee is
    * entered, else use current user or UnAssigneed if current user is SystemUser.
    */
   @Override
   public List<? extends IAtsUser> getToAssignees(IAtsWorkItem workItem, IAtsStateDefinition toState) throws OseeCoreException {
      List<IAtsUser> toAssignees = new ArrayList<>();
      if (toState.getStateType().isWorkingState()) {
         Collection<? extends IAtsUser> requestedAssignees = helper.getToAssignees(workItem);
         if (requestedAssignees != null) {
            for (IAtsUser user : requestedAssignees) {
               toAssignees.add(user);
            }
         }
         if (toAssignees.contains(AtsCoreUsers.UNASSIGNED_USER)) {
            toAssignees.remove(AtsCoreUsers.UNASSIGNED_USER);
            toAssignees.add(getTransitionAsUser());
         }
         if (toAssignees.isEmpty()) {
            if (helper.isSystemUser()) {
               toAssignees.add(AtsCoreUsers.UNASSIGNED_USER);
            } else {
               toAssignees.add(getTransitionAsUser());
            }
         }
      }
      return toAssignees;
   }

   @Override
   public TransitionResults handleAllAndPersist() {
      TransitionResults result = handleAll();
      if (result.isEmpty()) {
         helper.getChangeSet().execute();
      }
      return result;
   }

   @Override
   public void changesStored(IAtsChangeSet changes) {
      // Notify extension points of transitionAndPersist
      for (ITransitionListener listener : helper.getTransitionListeners()) {
         listener.transitionPersisted(helper.getWorkItems(), workItemFromStateMap, helper.getToStateName());
      }
   }

}
