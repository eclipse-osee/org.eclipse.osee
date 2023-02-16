/*********************************************************************
 * Copyright (c) 2013 Boeing
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
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.task.CreateTasksRuleRunner;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * This class should NOT be used on the IDE client except in integration tests. Use
 * AtsApiService.get().getWorkItemServiceClient().transition() instead.
 *
 * @author Donald G. Dunne
 */
public class TransitionManager implements IExecuteListener {

   private final ITransitionHelper helper;
   private Date transitionOnDate;
   private final IAtsUserService userService;
   private final IAtsReviewService reviewService;
   private final IAtsWorkItemService workItemService;
   private final IAtsTaskService taskService;
   private final IAtsWorkDefinitionService workDefService;
   private final IAttributeResolver attrResolver;
   private final Map<IAtsWorkItem, String> workItemFromStateMap;
   private final IAtsStoreService storeService;

   public TransitionManager(ITransitionHelper helper) {
      this(helper, false);
   }

   public TransitionManager(ITransitionHelper helper, boolean overrideClientCheck) {
      this.helper = helper;
      this.userService = helper.getServices().getUserService();
      this.reviewService = helper.getServices().getReviewService();
      this.workItemService = helper.getServices().getWorkItemService();
      this.workDefService = helper.getServices().getWorkDefinitionService();
      this.attrResolver = helper.getServices().getAttributeResolver();
      this.taskService = helper.getServices().getTaskService();
      this.storeService = helper.getServices().getStoreService();
      this.workItemFromStateMap = new HashMap<>();
      if (helper.getServices().isIde() && !overrideClientCheck && !AtsUtil.isInTest()) {
         // Capture stack trace so it's easy to determine where this is being called from
         try {
            throw new OseeArgumentException(
               "TransitionManager should NOT be used on client.  Use AtsApiService.get().getWorkItemServiceClient().transition() instead.");
         } catch (Exception ex) {
            OseeLog.log(TransitionManager.class, Level.WARNING, "Exception: " + Lib.exceptionToString(ex));
         }
      }
   }

   public TransitionResults handleAll() {
      loadWorkItems();

      IAtsWorkItem workItem = helper.getWorkItems().iterator().next();

      TransitionResults results = new TransitionResults();
      if (storeService.isIdeClient() && storeService.isInDb(workItem)) {
         handleWorkflowReload(results);
         if (results.isCancelled() || !results.isEmpty()) {
            return results;
         }
      }

      handleTransitionValidation(results);
      if (results.isCancelled() || !results.isEmpty()) {
         return results;
      }

      handleTransition(results);
      return results;
   }

   private void loadWorkItems() {
      if (helper.getTransData().getWorkItems().isEmpty()) {
         for (ArtifactToken art : helper.getServices().getQueryService().getArtifacts(
            Collections.castAll(helper.getTransData().getWorkItemIds()), helper.getServices().getAtsBranch())) {
            helper.getTransData().getWorkItems().add(helper.getServices().getWorkItemService().getWorkItem(art));
         }
      }
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
   public TransitionResults handleTransitionValidation(TransitionResults results) {
      loadWorkItems();
      boolean overrideAssigneeCheck = helper.isOverrideAssigneeCheck();
      try {
         if (helper.getWorkItems().isEmpty()) {
            results.addResult(TransitionResult.NO_WORKFLOWS_PROVIDED_FOR_TRANSITION);
            return results;
         }
         if (helper.getToStateName() == null) {
            results.addResult(TransitionResult.TO_STATE_CANT_BE_NULL);
            return results;
         }
         if (!overrideAssigneeCheck && helper.isSystemUser()) {
            results.addResult(TransitionResult.CAN_NOT_TRANSITION_AS_SYSTEM_USER);
            return results;
         }
      } catch (OseeCoreException ex) {
         results.addResult(
            new TransitionResult(String.format("Exception while validating transition [%s]", helper.getName()), ex));
      }
      for (IAtsWorkItem workItem : helper.getWorkItems()) {
         try {
            if (helper.getChangeSet() != null) {
               helper.getChangeSet().add(workItem);
            }
            // Validate toState valid
            StateDefinition fromStateDef = workItem.getStateDefinition();
            if (fromStateDef == null) {
               OseeLog.log(TransitionManager.class, Level.SEVERE,
                  String.format("from state for workItem %s is null", workItem.getName()));
            } else {
               StateDefinition toStateDef = workItem.getWorkDefinition().getStateByName(helper.getToStateName());
               if (toStateDef == null) {
                  results.addResult(workItem,
                     new TransitionResult(
                        String.format("Transition-To State [%s] does not exist for Work Definition [%s]",
                           helper.getToStateName(), workItem.getWorkDefinition().getName())));
                  continue;
               }

               // Ignore transitions to the same state
               if (!fromStateDef.equals(toStateDef)) {
                  // Validate transition from fromState and toState
                  List<StateDefinition> toStatesWithReturnStates = workItemService.getAllToStates(workItem);
                  if (!helper.isOverrideTransitionValidityCheck() && !toStatesWithReturnStates.contains(
                     toStateDef) && !fromStateDef.getStateType().isCompletedOrCancelledState()) {
                     String errStr =
                        String.format("Work Definition [%s] is not configured to transition from \"[%s]\" to \"[%s]\"",
                           fromStateDef.getWorkDefinition().getName(), fromStateDef.getName(), toStateDef.getName());
                     OseeLog.log(TransitionManager.class, Level.SEVERE, errStr);
                     results.addResult(workItem, new TransitionResult(errStr));
                     continue;
                  }

                  // Validate Editable
                  boolean isEditable = AtsApiService.get().getAtsAccessService().isWorkflowEditable(workItem);
                  boolean currentlyUnAssignedOrCompletedOrCancelled =
                     workItem.isCompletedOrCancelled() || workItem.getStateMgr().getAssignees().contains(
                        AtsCoreUsers.UNASSIGNED_USER);
                  // Allow anyone to transition any task to completed/cancelled/working if parent is working
                  if (workItem.isTask() && workItem.getParentTeamWorkflow().getCurrentStateType().isCompletedOrCancelled()) {
                     results.addResult(workItem, TransitionResult.TASK_CANT_TRANSITION_IF_PARENT_COMPLETED);
                     continue;
                  }
                  // Else, only allow transition if...
                  else if (!workItem.isTask() && !isEditable && !currentlyUnAssignedOrCompletedOrCancelled && !overrideAssigneeCheck) {
                     results.addResult(workItem, TransitionResult.UNABLE_TO_ASSIGN);
                     continue;
                  }

                  // Validate Working Branch
                  if (!helper.isOverrideWorkingBranchCheck()) {
                     isWorkingBranchTransitionable(results, workItem, toStateDef);
                     if (results.isCancelled()) {
                        continue;
                     }
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
               }
            }
         } catch (OseeCoreException ex) {
            results.addResult(workItem,
               new TransitionResult(String.format("Exception while validating transition [%s]", helper.getName()), ex));
         }
      }
      return results;
   }

   public void isTransitionValidForExtensions(TransitionResults results, IAtsWorkItem workItem, StateDefinition fromStateDef, StateDefinition toStateDef) {
      // Check extension points for valid transition
      for (IAtsTransitionHook listener : helper.getTransitionHooks()) {
         try {
            listener.transitioning(results, workItem, fromStateDef, toStateDef, getToAssignees(workItem, toStateDef),
               helper.getTransitionUser());
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
         for (IAtsTransitionHook listener : helper.getTransitionHooks()) {
            try {
               listener.transitioning(results, workItem, fromStateDef, toStateDef, getToAssignees(workItem, toStateDef),
                  AtsApiService.get().getUserService().getCurrentUser());
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
    * Process transition and persist changes to given skynet transaction
    */
   public void handleTransition(TransitionResults results) {
      try {
         IAtsChangeSet changes = helper.getChangeSet();
         if (changes != null) {
            changes.addExecuteListener(this);
         }
         for (IAtsWorkItem workItem : helper.getWorkItems()) {
            try {

               StateDefinition fromState = workItem.getStateDefinition();
               StateDefinition toState = workItem.getWorkDefinition().getStateByName(helper.getToStateName());

               //Ignore transitions to the same state
               if (!fromState.equals(toState)) {
                  Date transitionDate = getTransitionOnDate();
                  AtsUser transitionUser = getTransitionAsUser();

                  // Log transition
                  if (fromState.getStateType().isCancelledState()) {
                     logWorkflowUnCancelledEvent(workItem, toState, changes, attrResolver);
                  } else if (fromState.getStateType().isCompletedState()) {
                     logWorkflowUnCompletedEvent(workItem, toState, changes, attrResolver);
                  }

                  if (toState.getStateType().isCancelledState()) {
                     logWorkflowCancelledEvent(workItem, fromState, toState, transitionDate, transitionUser, changes,
                        attrResolver);
                  } else if (toState.getStateType().isCompletedState()) {
                     logWorkflowCompletedEvent(workItem, fromState, toState, transitionDate, transitionUser, changes);
                  } else {
                     updatePercentComplete(workItem, toState, changes);
                     logStateCompletedEvent(workItem, workItem.getCurrentStateName(), transitionDate, transitionUser);
                  }
                  logStateStartedEvent(workItem, toState, transitionDate, transitionUser);
                  // Get transition to assignees, do some checking to ensure someone is assigneed and UnAssigned
                  List<? extends AtsUser> updatedAssigees = getToAssignees(workItem, toState);

                  workItem.getStateMgr().transitionHelper(updatedAssigees, fromState, toState);

                  // Create validation review if in correct state and TeamWorkflow
                  if (reviewService.isValidationReviewRequired(workItem) && workItem.isTeamWorkflow()) {
                     IAtsDecisionReview review = reviewService.createValidateReview((IAtsTeamWorkflow) workItem, false,
                        transitionDate, transitionUser, changes);
                     if (review != null) {
                        changes.add(review);
                     }
                  }

                  // Create tasks from CreateTasksDefinition(s); call to service persists itself
                  if (workItem.isTeamWorkflow()) {
                     CreateTasksRuleRunner taskRunner = new CreateTasksRuleRunner((IAtsTeamWorkflow) workItem,
                        workItem.getWorkDefinition().getCreateTasksDefs(), helper.getServices());
                     XResultData result = taskRunner.run();
                     if (result.isErrors()) {
                        results.addResult(new TransitionResult(result.toString()));
                     } else if (!result.getIds().isEmpty()) {
                        // reload team wfs?
                     }
                  }

                  // Notify extension points of transition
                  for (IAtsTransitionHook listener : helper.getTransitionHooks()) {
                     listener.transitioned(workItem, fromState, toState, updatedAssigees, helper.getTransitionUser(),
                        changes);
                  }
                  // Notify any state transition listeners
                  for (IAtsTransitionHook listener : toState.getTransitionListeners()) {
                     listener.transitioned(workItem, fromState, toState, updatedAssigees, helper.getTransitionUser(),
                        changes);
                  }
                  if (toState.getStateType().isCompletedOrCancelledState()) {
                     workItemService.clearImplementersCache(workItem);
                  }
                  changes.add(workItem);

                  workItemFromStateMap.put(workItem, fromState.getName());
               }
            } catch (Exception ex) {
               results.addResult(workItem,
                  new TransitionResult(String.format("Exception while transitioning [%s]", helper.getName()), ex));
            }
            results.getWorkItemIds().add(
               ArtifactToken.valueOf(workItem.getId(), workItem.getName(), helper.getServices().getAtsBranch()));
         }

      } catch (Exception ex) {
         results.addResult(
            new TransitionResult(String.format("Exception while transitioning [%s]", helper.getName()), ex));
      }

   }

   private void isWorkingBranchTransitionable(TransitionResults results, IAtsWorkItem workItem, StateDefinition toStateDef) {
      if (workItem.isTeamWorkflow()) {
         if (helper.isWorkingBranchInWork((IAtsTeamWorkflow) workItem)) {
            if (toStateDef.getName().equals(TeamState.Cancelled.getName())) {
               results.addResult(workItem, TransitionResult.DELETE_WORKING_BRANCH_BEFORE_CANCEL);
            } else if (helper.isBranchInCommit((IAtsTeamWorkflow) workItem)) {
               results.addResult(workItem, TransitionResult.WORKING_BRANCH_BEING_COMMITTED);
            } else if (!toStateDef.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch.name())) {
               results.addResult(workItem, TransitionResult.WORKING_BRANCH_EXISTS);
            }
         }
      }
   }

   /**
    * @return true if toState is visited and is toState is earlier than current state
    */
   private boolean isOverrideAttributeValidationState(IAtsWorkItem workItem, StateDefinition toStateDef) {
      List<String> visitedStateNames = workItem.getStateMgr().getVisitedStateNames();
      if (visitedStateNames.contains(toStateDef.getName())) {
         StateDefinition currState = workItem.getStateDefinition();
         for (StateDefinition stateDef : toStateDef.getWorkDefinition().getStates()) {
            if (stateDef.getName().equals(toStateDef.getName())) {
               if (toStateDef.getOrdinal() < currState.getOrdinal()) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   private void isStateTransitionable(TransitionResults results, IAtsWorkItem workItem, StateDefinition toStateDef) {
      boolean isOverrideAttributeValidationState =
         helper.isOverrideTransitionValidityCheck() || isOverrideAttributeValidationState(workItem, toStateDef);
      if (toStateDef.getStateType().isCancelledState()) {
         validateTaskCompletion(results, workItem, toStateDef, taskService);
         validateReviewsCancelled(results, workItem, toStateDef);
      } else if (!toStateDef.getStateType().isCancelledState() && !isOverrideAttributeValidationState) {

         // Validate XWidgets for transition
         Collection<WidgetResult> widgetResults = workItemService.validateWidgetTransition(workItem, toStateDef);
         for (WidgetResult widgetResult : widgetResults) {
            if (!widgetResult.isSuccess()) {
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
                  review) == ReviewBlockType.Transition && !review.getCurrentStateType().isCompletedOrCancelled()) {
                  results.addResult(workItem, TransitionResult.COMPLETE_BLOCKING_REVIEWS);
               }
            }
         }
      }
   }

   private void validateReviewsCancelled(TransitionResults results, IAtsWorkItem workItem, StateDefinition toStateDef) {
      if (workItem.isTeamWorkflow() && toStateDef.getStateType().isCancelledState()) {
         for (IAtsAbstractReview review : reviewService.getReviewsFromCurrentState((IAtsTeamWorkflow) workItem)) {
            ReviewBlockType reviewBlockType = reviewService.getReviewBlockType(review);
            boolean completedOrCancelled = review.getCurrentStateType().isCompletedOrCancelled();
            if (reviewBlockType == ReviewBlockType.Transition && !completedOrCancelled) {
               results.addResult(workItem, TransitionResult.CANCEL_REVIEWS_BEFORE_CANCEL);
               break;
            }
         }
      }
   }

   public static void validateTaskCompletion(TransitionResults results, IAtsWorkItem workItem, StateDefinition toStateDef, IAtsTaskService taskService) {
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
      if (checkTasksCompletedForState && workItem.getCurrentStateType().isInWork()) {
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
            if (task.getCurrentStateType().isInWork()) {
               results.addResult(workItem, TransitionResult.TASKS_NOT_COMPLETED);
               break;
            }
         }
      }
   }

   private void logWorkflowCancelledEvent(IAtsWorkItem workItem, StateDefinition fromState, StateDefinition toState, Date cancelDate, AtsUser cancelBy, IAtsChangeSet changes, IAttributeResolver attrResolver) {
      logWorkflowCancelledEvent(workItem, fromState, toState, cancelDate, helper.getCancellationReason(),
         helper.getCancellationReasonAttrType(), helper.getCancellationReasonDetails(), cancelBy, changes,
         attrResolver);
   }

   public static void logWorkflowCancelledEvent(IAtsWorkItem workItem, StateDefinition fromState, StateDefinition toState, Date cancelDate, String cancelReason, AttributeTypeToken cancelReasonAttrType, String cancelReasonDetails, AtsUser cancelBy, IAtsChangeSet changes, IAttributeResolver attrResolver) {
      workItem.getLog().addLog(LogType.StateCancelled, fromState.getName(), cancelReason, cancelDate,
         cancelBy.getUserId());
      if (attrResolver.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledBy, cancelBy.getUserId(), changes);
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledDate, cancelDate, changes);
         if (Strings.isValid(cancelReason)) {
            Conditions.assertTrue(cancelReasonAttrType.isValid(), "Cancel Attr Type must be valid");
            attrResolver.setSoleAttributeValue(workItem, cancelReasonAttrType, cancelReason, changes);
         }
         if (Strings.isValid(cancelReasonDetails)) {
            attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledReasonDetails, cancelReasonDetails,
               changes);
         }
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledFromState, fromState.getName(),
            changes);
      }
      validateUpdatePercentComplete(workItem, toState, changes);
   }

   private void logWorkflowUnCancelledEvent(IAtsWorkItem workItem, StateDefinition toState, IAtsChangeSet changes, IAttributeResolver attrResolver) {
      if (attrResolver.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledBy, changes);
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledDate, changes);
         changes.deleteAttributes(workItem, AtsAttributeTypes.CancelledReason);
         changes.deleteAttributes(workItem, AtsAttributeTypes.CancelledReasonEnum);
         changes.deleteAttributes(workItem.getStoreObject(), AtsAttributeTypes.CancelledReasonDetails);
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledFromState, changes);
      }
      validateUpdatePercentComplete(workItem, toState, changes);
   }

   private void logWorkflowCompletedEvent(IAtsWorkItem workItem, StateDefinition fromState, StateDefinition toState, Date cancelDate, AtsUser cancelBy, IAtsChangeSet changes) {
      workItem.getLog().addLog(LogType.StateComplete, fromState.getName(), "", cancelDate, cancelBy.getUserId());
      if (attrResolver.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedBy, cancelBy.getUserId(), changes);
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedDate, cancelDate, changes);
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedFromState, fromState.getName(),
            changes);
      }
      validateUpdatePercentComplete(workItem, toState, changes);
   }

   private void logWorkflowUnCompletedEvent(IAtsWorkItem workItem, StateDefinition toState, IAtsChangeSet changes, IAttributeResolver attrResolver) {
      if (attrResolver.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedBy, changes);
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedDate, changes);
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedFromState, changes);
      }
      validateUpdatePercentComplete(workItem, toState, changes);
   }

   private void updatePercentComplete(IAtsWorkItem workItem, StateDefinition toState, IAtsChangeSet changes) {
      IAtsStateManager stateMgr = workItem.getStateMgr();
      Integer percent = AtsApiService.get().getWorkItemMetricsService().getPercentComplete(workItem);
      if (percent == null) {
         percent = 0;
      }
      if (toState.getStateType().isWorkingState()) {
         Integer recPercent = toState.getRecommendedPercentComplete();
         if (recPercent != null && recPercent > 0) {
            AtsApiService.get().getWorkItemMetricsService().setPercentComplete(workItem, recPercent, changes);
         }
         changes.add(workItem);
      }
   }

   private static void validateUpdatePercentComplete(IAtsWorkItem workItem, StateDefinition toState, IAtsChangeSet changes) {
      Integer percent = AtsApiService.get().getWorkItemMetricsService().getPercentComplete(workItem);
      if (percent == null) {
         percent = 0;
      }
      if (toState.getStateType().isCompletedOrCancelledState() && percent != 100) {
         AtsApiService.get().getWorkItemMetricsService().setPercentComplete(workItem, 100, changes);
         changes.add(workItem);
      } else if (toState.getStateType().isWorkingState() && percent == 100) {
         AtsApiService.get().getWorkItemMetricsService().setPercentComplete(workItem, 0, changes);
         changes.add(workItem);
      }
   }

   private void logStateCompletedEvent(IAtsWorkItem workItem, String fromStateName, Date date, AtsUser user) {
      workItem.getLog().addLog(LogType.StateComplete, fromStateName, "", date, user.getUserId());
   }

   public static void logStateStartedEvent(IAtsWorkItem workItem, IStateToken state, Date date, AtsUser user) {
      workItem.getLog().addLog(LogType.StateEntered, state.getName(), "", date, user.getUserId());
   }

   /**
    * Allow transition date to be used in log to be overridden for importing Actions from other systems and other
    * programatic transitions.
    */
   public AtsUser getTransitionAsUser() {
      AtsUser user = helper.getTransitionUser();
      if (user == null) {
         user = userService.getCurrentUser();
      }
      return user;
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
   public List<? extends AtsUser> getToAssignees(IAtsWorkItem workItem, StateDefinition toState) {
      List<AtsUser> toAssignees = new ArrayList<>();
      if (toState.getStateType().isWorkingState()) {
         Collection<? extends AtsUser> requestedAssignees = helper.getToAssignees(workItem);
         if (requestedAssignees != null) {
            for (AtsUser user : requestedAssignees) {
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

   public TransitionResults handleAllAndPersist() {
      TransitionResults result = handleAll();
      if (result.isEmpty()) {
         if (helper.getChangeSet() != null) {
            TransactionId transactionId = helper.getChangeSet().execute();
            result.setTransaction(transactionId);
         }

         if (helper.getServices().getEventService() != null) {
            helper.getServices().getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_TRANSITIONED,
               helper.getWorkItems(), result.getTransaction());
         }
      } else {
         if (helper.getServices().getEventService() != null) {
            helper.getServices().getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED,
               helper.getWorkItems(), TransactionToken.SENTINEL);
         }
      }
      return result;
   }

   @Override
   public void changesStored(IAtsChangeSet changes) {
      // Notify extension points of transitionAndPersist
      for (IAtsTransitionHook listener : helper.getTransitionHooks()) {
         listener.transitionPersisted(helper.getWorkItems(), workItemFromStateMap, helper.getToStateName(),
            helper.getTransitionUser());
      }
   }

}
