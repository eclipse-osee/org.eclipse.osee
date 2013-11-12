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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.core.config.AtsVersionService;
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
public class TransitionManager {

   private final ITransitionHelper helper;
   private String completedCancellationReason = null;
   private IAtsUser transitionAsUser;
   private Date transitionOnDate;

   public TransitionManager(ITransitionHelper helper) {
      this.helper = helper;
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
      handleTransition(results);
      return results;
   }

   /**
    * Validate AbstractWorkflowArtifact for transition including checking widget validation, rules, assignment, etc.
    * 
    * @return Result.isFalse if failure
    */
   public void handleTransitionValidation(TransitionResults results) {
      try {
         if (helper.getWorkItems().isEmpty()) {
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
      for (IAtsWorkItem workItem : helper.getWorkItems()) {
         try {
            helper.getChangeSet().add(workItem);
            // Validate toState valid
            IAtsStateDefinition fromStateDef = workItem.getStateDefinition();
            IAtsStateDefinition toStateDef = workItem.getWorkDefinition().getStateByName(helper.getToStateName());
            if (toStateDef == null) {
               results.addResult(
                  workItem,
                  new TransitionResult(String.format(
                     "Transition-To State [%s] does not exist for Work Definition [%s]", helper.getToStateName(),
                     workItem.getWorkDefinition().getName())));
               continue;
            }

            //Ignore transitions to the same state
            if (!fromStateDef.equals(toStateDef)) {
               // Validate transition from fromState and toState
               if (!helper.isOverrideTransitionValidityCheck() && !fromStateDef.getToStates().contains(toStateDef) && !fromStateDef.getStateType().isCompletedOrCancelledState()) {
                  String errStr =
                     String.format("Work Definition [%s] is not configured to transition from \"[%s]\" to \"[%s]\"",
                        toStateDef.getName(), fromStateDef.getName(), toStateDef.getName());
                  OseeLog.log(AtsCore.class, Level.SEVERE, errStr);
                  results.addResult(workItem, new TransitionResult(errStr));
                  continue;
               }

               // Validate Editable
               boolean stateIsEditable =
                  WorkflowManagerCore.isEditable(workItem, workItem.getStateDefinition(),
                     helper.isPrivilegedEditEnabled());
               boolean currentlyUnAssigned =
                  workItem.getStateMgr().getAssignees().contains(AtsCoreUsers.UNASSIGNED_USER);
               workItem.getStateMgr().validateNoBootstrapUser();
               boolean overrideAssigneeCheck = helper.isOverrideAssigneeCheck();
               // Allow anyone to transition any task to completed/cancelled/working if parent is working
               if (workItem.isTask() && workItem.getParentTeamWorkflow().getWorkData().isCompletedOrCancelled()) {
                  results.addResult(workItem, TransitionResult.TASK_CANT_TRANSITION_IF_PARENT_COMPLETED);
                  continue;
               }
               // Else, only allow transition if...
               else if (!workItem.isTask() && !stateIsEditable && !currentlyUnAssigned && !overrideAssigneeCheck) {
                  results.addResult(workItem, TransitionResult.MUST_BE_ASSIGNED);
                  continue;
               }

               // Validate Working Branch
               isWorkingBranchTransitionable(results, workItem, toStateDef);
               if (results.isCancelled()) {
                  continue;
               }

               // Validate Assignees (UnAssigned ok cause will be resolve to current user upon transition
               if (!overrideAssigneeCheck && !toStateDef.getStateType().isCancelledState() && helper.isSystemUserAssingee(workItem)) {
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

   public void isTransitionValidForExtensions(TransitionResults results, IAtsWorkItem workItem, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef) {
      // Check extension points for valid transition
      for (ITransitionListener listener : helper.getTransitionListeners()) {
         try {
            listener.transitioning(results, workItem, fromStateDef, toStateDef, getToAssignees(workItem, toStateDef));
            if (results.isCancelled() || !results.isEmpty()) {
               continue;
            }
         } catch (OseeCoreException ex) {
            results.addResult(
               workItem,
               new TransitionResult(String.format("Exception [%s] while validating transition extensions 1 [%s]",
                  ex.getMessage(), helper.getName()), ex));
         }

      }

      // Check again in case first check made changes that would now keep transition from happening
      if (results.isEmpty()) {
         for (ITransitionListener listener : helper.getTransitionListeners()) {
            try {
               listener.transitioning(results, workItem, fromStateDef, toStateDef, getToAssignees(workItem, toStateDef));
               if (results.isCancelled() || !results.isEmpty()) {
                  continue;
               }
            } catch (OseeCoreException ex) {
               results.addResult(
                  workItem,
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
   public void handleTransition(TransitionResults results) {
      try {
         for (IAtsWorkItem workItem : helper.getWorkItems()) {
            try {
               IAtsStateDefinition fromState = workItem.getStateDefinition();
               IAtsStateDefinition toState =
                  workItem.getWorkDefinition().getStateDefinitionByName(helper.getToStateName());

               //Ignore transitions to the same state
               if (!fromState.equals(toState)) {
                  Date transitionDate = getTransitionOnDate();
                  IAtsUser transitionUser = getTransitionAsUser();
                  // Log transition
                  if (fromState.getStateType().isCancelledState()) {
                     logWorkflowUnCancelledEvent(workItem, toState);
                  } else if (fromState.getStateType().isCompletedState()) {
                     logWorkflowUnCompletedEvent(workItem, toState);
                  }
                  if (toState.getStateType().isCancelledState()) {
                     logWorkflowCancelledEvent(workItem, fromState, toState, completedCancellationReason,
                        transitionDate, transitionUser);
                  } else if (toState.getStateType().isCompletedState()) {
                     logWorkflowCompletedEvent(workItem, fromState, toState, completedCancellationReason,
                        transitionDate, transitionUser);
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
                  if (AtsCore.getReviewService().isValidationReviewRequired(workItem) && workItem.isTeamWorkflow()) {
                     IAtsDecisionReview review =
                        AtsCore.getReviewService().createValidateReview((IAtsTeamWorkflow) workItem, false,
                           transitionDate, transitionUser, helper.getChangeSet());
                     if (review != null) {
                        helper.getChangeSet().add(review);
                     }
                  }

                  AtsCore.getWorkItemService().transitioned(workItem, fromState, toState, updatedAssigees,
                     helper.getChangeSet());

                  // Notify extension points of transition
                  for (ITransitionListener listener : helper.getTransitionListeners()) {
                     listener.transitioned(workItem, fromState, toState, updatedAssigees, helper.getChangeSet());
                  }
                  if (toState.getStateType().isCompletedOrCancelledState()) {
                     AtsCore.getWorkItemService().clearImplementersCache(workItem);
                  }
                  helper.getChangeSet().add(workItem);
               }

            } catch (Exception ex) {
               results.addResult(workItem,
                  new TransitionResult(String.format("Exception while transitioning [%s]", helper.getName()), ex));
            }
         }
         if (results.isEmpty()) {
            helper.getChangeSet().execute();
         }
      } catch (Exception ex) {
         results.addResult(new TransitionResult(String.format("Exception while transitioning [%s]", helper.getName()),
            ex));
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
         validateTaskCompletion(results, workItem, toStateDef);
         validateReviewsCancelled(results, workItem, toStateDef);
      } else if (!toStateDef.getStateType().isCancelledState() && !isOverrideAttributeValidationState) {

         // Validate XWidgets for transition
         Collection<WidgetResult> widgetResults =
            AtsCore.getWorkItemService().validateWidgetTransition(workItem, toStateDef);
         for (WidgetResult widgetResult : widgetResults) {
            if (!widgetResult.isValid()) {
               results.addResult(workItem, widgetResult);
            }
         }

         validateTaskCompletion(results, workItem, toStateDef);

         // Don't transition without targeted version if so configured
         boolean teamDefRequiresTargetedVersion =
            AtsCore.getWorkDefService().teamDefHasRule(workItem, RuleDefinitionOption.RequireTargetedVersion);
         boolean pageRequiresTargetedVersion =
            workItem.getStateDefinition().hasRule(RuleDefinitionOption.RequireTargetedVersion.name());

         // Only check this if TeamWorkflow, not for reviews
         if (workItem.isTeamWorkflow() && (teamDefRequiresTargetedVersion || pageRequiresTargetedVersion) && //
         !AtsVersionService.get().hasTargetedVersion(workItem) && //
         !toStateDef.getStateType().isCancelledState()) {
            results.addResult(workItem, TransitionResult.MUST_BE_TARGETED_FOR_VERSION);
         }

         // Loop through this state's blocking reviews to confirm complete
         if (workItem.isTeamWorkflow()) {
            for (IAtsAbstractReview review : AtsCore.getReviewService().getReviewsFromCurrentState(
               (IAtsTeamWorkflow) workItem)) {
               if (AtsCore.getReviewService().getReviewBlockType(review) == ReviewBlockType.Transition && !AtsCore.getWorkItemService().getWorkData(
                  review).isCompletedOrCancelled()) {
                  results.addResult(workItem, TransitionResult.COMPLETE_BLOCKING_REVIEWS);
               }
            }
         }
      }
   }

   private void validateReviewsCancelled(TransitionResults results, IAtsWorkItem workItem, IAtsStateDefinition toStateDef) throws OseeCoreException {
      if (workItem.isTeamWorkflow() && toStateDef.getStateType().isCancelledState()) {
         for (IAtsAbstractReview review : AtsCore.getReviewService().getReviewsFromCurrentState(
            (IAtsTeamWorkflow) workItem)) {
            if (AtsCore.getReviewService().getReviewBlockType(review) == ReviewBlockType.Transition && !AtsCore.getWorkItemService().getWorkData(
               workItem).isCompletedOrCancelled()) {
               results.addResult(workItem, TransitionResult.CANCEL_REVIEWS_BEFORE_CANCEL);
               break;
            }
         }
      }
   }

   private void validateTaskCompletion(TransitionResults results, IAtsWorkItem workItem, IAtsStateDefinition toStateDef) throws OseeCoreException {
      // Loop through this state's tasks to confirm complete
      boolean checkTasksCompletedForState = true;
      // Don't check for task completion if transition to working state and AllowTransitionWithoutTaskCompletion rule is set
      if (workItem.getStateDefinition().hasRule(RuleDefinitionOption.AllowTransitionWithoutTaskCompletion.name()) && toStateDef.getStateType().isWorkingState()) {
         checkTasksCompletedForState = false;
      }
      if (checkTasksCompletedForState && workItem.isTeamWorkflow() && !AtsCore.getWorkItemService().getWorkData(
         workItem).isCompletedOrCancelled()) {
         Set<IAtsTask> tasksToCheck = new HashSet<IAtsTask>();
         // If transitioning to completed/cancelled, all tasks must be completed/cancelled
         if (toStateDef.getStateType().isCompletedOrCancelledState()) {
            tasksToCheck.addAll(AtsCore.getWorkItemService().getTaskArtifacts(workItem));
         }
         // Else, just check current state tasks
         else {
            tasksToCheck.addAll(AtsCore.getWorkItemService().getTaskArtifacts(workItem));
         }
         for (IAtsTask taskArt : tasksToCheck) {
            if (AtsCore.getWorkItemService().getWorkData(taskArt).isInWork()) {
               results.addResult(workItem, TransitionResult.TASKS_NOT_COMPLETED);
               break;
            }
         }
      }
   }

   public static void logWorkflowCancelledEvent(IAtsWorkItem workItem, IAtsStateDefinition fromState, IAtsStateDefinition toState, String reason, Date cancelDate, IAtsUser cancelBy) throws OseeCoreException {
      workItem.getLog().addLog(LogType.StateCancelled, fromState.getName(), reason, cancelDate, cancelBy.getUserId());
      if (AtsCore.getAttrResolver().isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         AtsCore.getAttrResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledBy, cancelBy.getUserId());
         AtsCore.getAttrResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledDate, cancelDate);
         AtsCore.getAttrResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledReason, reason);
         AtsCore.getAttrResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledFromState,
            fromState.getName());
      }
      validateUpdatePercentComplete(workItem, toState);
      AtsCore.getLogFactory().writeToStore(workItem);
   }

   public static void logWorkflowUnCancelledEvent(IAtsWorkItem workItem, IAtsStateDefinition toState) throws OseeCoreException {
      if (AtsCore.getAttrResolver().isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         AtsCore.getAttrResolver().deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledBy);
         AtsCore.getAttrResolver().deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledDate);
         AtsCore.getAttrResolver().deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledReason);
         AtsCore.getAttrResolver().deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledFromState);
      }
      validateUpdatePercentComplete(workItem, toState);
      AtsCore.getLogFactory().writeToStore(workItem);
   }

   private void logWorkflowCompletedEvent(IAtsWorkItem workItem, IAtsStateDefinition fromState, IAtsStateDefinition toState, String reason, Date cancelDate, IAtsUser cancelBy) throws OseeCoreException {
      workItem.getLog().addLog(LogType.StateComplete, fromState.getName(), Strings.isValid(reason) ? reason : "",
         cancelDate, cancelBy.getUserId());
      if (AtsCore.getAttrResolver().isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         AtsCore.getAttrResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedBy, cancelBy.getUserId());
         AtsCore.getAttrResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedDate, cancelDate);
         AtsCore.getAttrResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedFromState,
            fromState.getName());
      }
      validateUpdatePercentComplete(workItem, toState);
      AtsCore.getLogFactory().writeToStore(workItem);
   }

   public static void logWorkflowUnCompletedEvent(IAtsWorkItem workItem, IAtsStateDefinition toState) throws OseeCoreException {
      if (AtsCore.getAttrResolver().isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         AtsCore.getAttrResolver().deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedBy);
         AtsCore.getAttrResolver().deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedDate);
         AtsCore.getAttrResolver().deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedFromState);
      }
      validateUpdatePercentComplete(workItem, toState);
      AtsCore.getLogFactory().writeToStore(workItem);
   }

   private static void validateUpdatePercentComplete(IAtsWorkItem workItem, IAtsStateDefinition toState) {
      Integer percent = workItem.getStateMgr().getPercentCompleteValue();
      if (percent == null) {
         percent = 0;
      }
      if (toState.getStateType().isCompletedOrCancelledState() && percent != 100) {
         workItem.getStateMgr().setPercentCompleteValue(100);
      } else if (toState.getStateType().isWorkingState() && percent == 100) {
         workItem.getStateMgr().setPercentCompleteValue(0);
      }
   }

   private void logStateCompletedEvent(IAtsWorkItem workItem, String fromStateName, String reason, Date date, IAtsUser user) throws OseeCoreException {
      workItem.getLog().addLog(LogType.StateComplete, fromStateName, Strings.isValid(reason) ? reason : "", date,
         user.getUserId());
      AtsCore.getLogFactory().writeToStore(workItem);
   }

   public static void logStateStartedEvent(IAtsWorkItem workItem, IStateToken state, Date date, IAtsUser user) throws OseeCoreException {
      workItem.getLog().addLog(LogType.StateEntered, state.getName(), "", date, user.getUserId());
      AtsCore.getLogFactory().writeToStore(workItem);
   }

   /**
    * Allow transition date to be used in log to be overridden for importing Actions from other systems and other
    * programatic transitions.
    */
   public IAtsUser getTransitionAsUser() throws OseeCoreException {
      if (transitionAsUser == null) {
         return AtsCore.getUserService().getCurrentUser();
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
   public List<? extends IAtsUser> getToAssignees(IAtsWorkItem workItem, IAtsStateDefinition toState) throws OseeCoreException {
      List<IAtsUser> assignees = new ArrayList<IAtsUser>();
      if (toState.getStateType().isWorkingState()) {
         Collection<? extends IAtsUser> requestedAssignees = helper.getToAssignees(workItem);
         if (requestedAssignees != null) {
            for (IAtsUser user : requestedAssignees) {
               assignees.add(user);
            }
         }
         if (assignees.contains(AtsCoreUsers.UNASSIGNED_USER)) {
            assignees.remove(AtsCoreUsers.UNASSIGNED_USER);
            assignees.add(AtsCore.getUserService().getCurrentUser());
         }
         if (assignees.isEmpty()) {
            if (helper.isSystemUser()) {
               assignees.add(AtsCoreUsers.UNASSIGNED_USER);
            } else {
               assignees.add(AtsCore.getUserService().getCurrentUser());
            }
         }
      }
      return assignees;
   }

   public TransitionResults handleAllAndPersist() {
      TransitionResults result = handleAll();
      if (result.isEmpty()) {
         helper.getChangeSet().execute();
      }
      return result;
   }

}
