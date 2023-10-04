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

import static org.eclipse.osee.ats.api.workflow.transition.TransitionOption.OverrideIdeTransitionCheck;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewDefectManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.task.IAtsTaskService;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.review.UserRoleManager;
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

   private Date transitionOnDate;
   private final IAtsUserService userService;
   private final IAtsReviewService reviewService;
   private final IAtsWorkItemService workItemService;
   private final IAtsTaskService taskService;
   private final IAtsWorkDefinitionService workDefService;
   private final IAttributeResolver attrResolver;
   private final Map<IAtsWorkItem, String> workItemFromStateMap;
   private final AtsApi atsApi;
   private final TransitionData transData;
   private IAtsChangeSet changes;
   private final TransitionResults results = new TransitionResults();

   public TransitionManager(TransitionData transData) {
      this.transData = transData;
      this.atsApi = AtsApiService.get();
      this.userService = atsApi.getUserService();
      this.reviewService = atsApi.getReviewService();
      this.workItemService = atsApi.getWorkItemService();
      this.workDefService = atsApi.getWorkDefinitionService();
      this.attrResolver = atsApi.getAttributeResolver();
      this.taskService = atsApi.getTaskService();
      this.workItemFromStateMap = new HashMap<>();
      results.setDebug(transData.isDebug());
      if (atsApi.isIde() && !transData.getHasTransitionOptions(OverrideIdeTransitionCheck) && !AtsUtil.isInTest()) {
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

      handleTransitionValidation(results);
      if (results.isCancelled() || !results.isEmpty()) {
         return results;
      }

      handleTransition(results);
      results.getTimeRd().addTimeMapToResultData();
      return results;
   }

   public TransitionResults handleAllAndPersist() {
      TransitionResults results = handleAll();
      if (results.isEmpty()) {
         if (getChangeSet() != null) {
            logTimeStart("30 - ChangeSet.execute");
            TransactionId transactionId = getChangeSet().execute();
            results.setTransaction(transactionId);
            logTimeSpent("30 - ChangeSet.execute");
         }

         if (atsApi.getEventService() != null) {
            logTimeStart("35 - ChangeSet.execute");
            atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_TRANSITIONED,
               transData.getWorkItems(), results.getTransaction());
            logTimeSpent("35 - ChangeSet.execute");
         }
      } else {
         if (atsApi.getEventService() != null) {
            logTimeStart("35 - ChangeSet.execute");
            atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED,
               transData.getWorkItems(), TransactionToken.SENTINEL);
            logTimeSpent("35 - ChangeSet.execute");
         }
      }
      return results;
   }

   private void loadWorkItems() {
      logTimeStart("01 - loadWorkItems");
      if (transData.getWorkItems().isEmpty()) {
         for (ArtifactToken art : atsApi.getQueryService().getArtifacts(Collections.castAll(transData.getWorkItemIds()),
            atsApi.getAtsBranch())) {
            transData.getWorkItems().add(atsApi.getWorkItemService().getWorkItem(art));
         }
      }
      logTimeSpent("01 - loadWorkItems");
   }

   /**
    * Validate AbstractWorkflowArtifact for transition including checking widget validation, rules, assignment, etc.
    *
    * @return TransitionResults.results errors if failed
    */
   public TransitionResults handleTransitionValidation(TransitionResults results) {
      loadWorkItems();
      logTimeStart("05 - handleTransitionValidation");
      boolean overrideAssigneeCheck = isOverrideAssigneeCheck();
      try {
         if (transData.getWorkItems().isEmpty()) {
            results.addResult(TransitionResult.NO_WORKFLOWS_PROVIDED_FOR_TRANSITION);
            return results;
         }
         if (transData.getToStateName() == null) {
            results.addResult(TransitionResult.TO_STATE_CANT_BE_NULL);
            return results;
         }
         if (!overrideAssigneeCheck && transData.isSystemUser()) {
            results.addResult(TransitionResult.CAN_NOT_TRANSITION_AS_SYSTEM_USER);
            return results;
         }
      } catch (OseeCoreException ex) {
         results.addResult(
            new TransitionResult(String.format("Exception while validating transition [%s]", transData.getName()), ex));
      }
      for (IAtsWorkItem workItem : transData.getWorkItems()) {
         try {
            if (getChangeSet() != null) {
               getChangeSet().add(workItem);
            }

            // Validate not Blocked or OnHold
            validateNotBlockedOrHold(workItem);
            if (results.isErrors()) {
               continue;
            }

            // Validate toState valid
            StateDefinition fromStateDef = workItem.getStateDefinition();
            if (fromStateDef == null) {
               OseeLog.log(TransitionManager.class, Level.SEVERE,
                  String.format("from state for workItem %s is null", workItem.getName()));
            } else {
               logTimeStart("05.1 - Validate toState valid");
               StateDefinition toStateDef = workItem.getWorkDefinition().getStateByName(transData.getToStateName());
               if (toStateDef == null) {
                  results.addResult(workItem,
                     new TransitionResult(
                        String.format("Transition-To State [%s] does not exist for Work Definition [%s]",
                           transData.getToStateName(), workItem.getWorkDefinition().getName())));
                  continue;
               } else {
                  AtsApiService.get().getWorkItemService().validateUserGroupTransition(workItem, toStateDef, results);
               }
               logTimeSpent("05.1 - Validate toState valid");

               // Ignore transitions to the same state
               if (!fromStateDef.equals(toStateDef)) {
                  // Validate transition from fromState and toState
                  List<StateDefinition> toStatesWithReturnStates = workItemService.getAllToStates(workItem);
                  if (!transData.isOverrideTransitionValidityCheck() && !toStatesWithReturnStates.contains(
                     toStateDef) && !fromStateDef.isCompletedOrCancelled()) {
                     String errStr =
                        String.format("Work Definition [%s] is not configured to transition from \"[%s]\" to \"[%s]\"",
                           fromStateDef.getWorkDefinition().getName(), fromStateDef.getName(), toStateDef.getName());
                     OseeLog.log(TransitionManager.class, Level.SEVERE, errStr);
                     results.addResult(workItem, new TransitionResult(errStr));
                     continue;
                  }

                  // Validate Editable
                  logTimeStart("05.2 - Validate Editable");
                  boolean isEditable = AtsApiService.get().getAtsAccessService().isWorkflowEditable(workItem);
                  boolean currentlyUnAssignedOrCompletedOrCancelled =
                     workItem.isCompletedOrCancelled() || transData.isToAssigneesEmptyOrUnassigned();
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
                  logTimeSpent("05.2 - Validate Editable");

                  // Validate Working Branch
                  logTimeStart("05.3 - Validate Working Branch");
                  if (!isOverrideWorkingBranchCheck()) {
                     isWorkingBranchTransitionable(results, workItem, toStateDef);
                     if (results.isCancelled()) {
                        continue;
                     }
                  }
                  logTimeSpent("05.3 - Validate Working Branch");

                  // Validate Assignees (UnAssigned ok cause will be resolve to current user upon transition
                  if (!overrideAssigneeCheck && !toStateDef.isCancelled() && transData.isSystemUserAssingee(workItem)) {
                     results.addResult(workItem, TransitionResult.CAN_NOT_TRANSITION_WITH_SYSTEM_USER_ASSIGNED);
                     continue;
                  }

                  // Validate state, widgets, rules unless OverrideAttributeValidation is set or transitioning to cancel
                  isStateTransitionable(results, workItem, toStateDef, fromStateDef);
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
            results.addResult(workItem, new TransitionResult(
               String.format("Exception while validating transition [%s]", transData.getName()), ex));
         }
      }
      logTimeSpent("05 - handleTransitionValidation");
      return results;
   }

   private void validateNotBlockedOrHold(IAtsWorkItem workItem) {
      boolean isBlocked = workItem.getAtsApi().getWorkItemService().isBlocked(workItem);
      if (isBlocked) {
         String reason = workItem.getAtsApi().getAttributeResolver().getSoleAttributeValue(workItem,
            AtsAttributeTypes.BlockedReason, "unknown");
         results.addResult(new TransitionResult("Can not transition a Blocked Workflow.\nBlock Reason: [%s]", reason));
      }
      boolean isHold = workItem.getAtsApi().getWorkItemService().isOnHold(workItem);
      if (isHold) {
         String reason = workItem.getAtsApi().getAttributeResolver().getSoleAttributeValue(workItem,
            AtsAttributeTypes.HoldReason, "");
         results.addResult(new TransitionResult("Can not transition a Workflow on Hold.\nHold Reason: [%s]", reason));
      }
   }

   public void isTransitionValidForExtensions(TransitionResults results, IAtsWorkItem workItem,
      StateDefinition fromStateDef, StateDefinition toStateDef) {
      logTimeStart("05.5 - isTransitionValidForExtensions");
      for (IAtsTransitionHook listener : getTransitionHooks()) {
         try {
            logTimeStart("05.51 - transitioning - " + listener.getClass().getSimpleName());
            listener.transitioning(results, workItem, fromStateDef, toStateDef, getToAssignees(workItem, toStateDef),
               transData.getTransitionUser(), atsApi);
            logTimeSpent("05.51 - transitioning - " + listener.getClass().getSimpleName());
            if (results.isCancelled() || !results.isEmpty()) {
               continue;
            }
         } catch (OseeCoreException ex) {
            results.addResult(workItem,
               new TransitionResult(String.format("Exception [%s] while validating transition extensions 1 [%s]",
                  ex.getMessage(), transData.getName()), ex));
         }

      }

      logTimeSpent("05.5 - isTransitionValidForExtensions");
   }

   /**
    * Process transition and persist changes to given skynet transaction
    */
   public void handleTransition(TransitionResults results) {
      logTimeStart("20 - handleTransition");
      try {
         IAtsChangeSet changes = getChangeSet();
         if (changes != null) {
            changes.addExecuteListener(this);
         }
         for (IAtsWorkItem workItem : transData.getWorkItems()) {
            try {

               StateDefinition fromState = workItem.getStateDefinition();
               StateDefinition toState = workItem.getWorkDefinition().getStateByName(transData.getToStateName());

               //Ignore transitions to the same state
               if (!fromState.equals(toState)) {
                  Date transitionDate = getTransitionOnDate();
                  AtsUser transitionUser = getTransitionAsUser();

                  // Log transition
                  if (fromState.isCancelled()) {
                     logWorkflowUnCancelledEvent(workItem, toState, changes, transitionUser);
                  } else if (fromState.isCompleted()) {
                     logWorkflowUnCompletedEvent(workItem, toState, changes, transitionUser);
                  }

                  if (toState.isCancelled()) {
                     logWorkflowCancelledEvent(workItem, fromState, toState, transitionDate, transitionUser, changes,
                        attrResolver);
                  } else if (toState.isCompleted()) {
                     logWorkflowCompletedEvent(workItem, fromState, toState, transitionDate, transitionUser, changes);
                  } else {
                     updatePercentComplete(workItem, toState, changes);
                     logStateCompletedEvent(workItem, workItem.getCurrentStateName(), transitionDate, transitionUser);
                  }
                  logStateStartedEvent(workItem, toState, transitionDate, transitionUser);

                  /**
                   * Get transition to assignees; Use specified or get default current user. See AtsWorkflowLinks.md for
                   * design.
                   */
                  Set<AtsUser> toStateAssigees = new HashSet<>();
                  // If toAssignees is valid, use those
                  if (!transData.isToAssigneesEmptyOrUnassigned()) {
                     toStateAssigees.addAll(transData.getToAssignees());
                  }
                  // Else, get toAssignees or current user if none specified
                  else {
                     toStateAssigees.addAll(getToAssignees(workItem, toState));
                  }

                  if (changes != null) {
                     changes.updateForTransition(workItem, toState, toStateAssigees);
                  }

                  // Create validation review if in correct state and TeamWorkflow
                  if (reviewService.isValidationReviewRequired(workItem) && workItem.isTeamWorkflow()) {
                     IAtsDecisionReview review = reviewService.createValidateReview((IAtsTeamWorkflow) workItem, false,
                        transitionDate, transitionUser, changes);
                     if (review != null && changes != null) {
                        changes.add(review);
                     }
                  }

                  // Create tasks from CreateTasksDefinition(s); call to service persists itself
                  if (workItem.isTeamWorkflow()) {
                     CreateTasksRuleRunner taskRunner = new CreateTasksRuleRunner((IAtsTeamWorkflow) workItem,
                        workItem.getWorkDefinition().getCreateTasksDefs(), atsApi);
                     XResultData result = taskRunner.run();
                     if (result.isErrors()) {
                        results.addResult(new TransitionResult(result.toString()));
                     } else if (!result.getIds().isEmpty()) {
                        // reload team wfs?
                     }
                  }

                  // Notify extension points of transition
                  for (IAtsTransitionHook listener : getTransitionHooks()) {
                     logTimeStart("20.1 - hooks transitioned " + listener.getClass().getSimpleName());
                     listener.transitioned(workItem, fromState, toState, toStateAssigees, transData.getTransitionUser(),
                        changes, atsApi);
                     logTimeSpent("20.1 - hooks transitioned " + listener.getClass().getSimpleName());
                  }
                  // Notify any state transition listeners
                  for (IAtsTransitionHook listener : toState.getTransitionListeners()) {
                     logTimeStart("20.2 - state hook transitioned " + listener.getClass().getSimpleName());
                     listener.transitioned(workItem, fromState, toState, toStateAssigees, transData.getTransitionUser(),
                        changes, atsApi);
                     logTimeSpent("20.2 - state hook transitioned " + listener.getClass().getSimpleName());
                  }
                  if (toState.isCompletedOrCancelled()) {
                     workItemService.clearImplementersCache(workItem);
                  }
                  if (changes != null) {
                     changes.add(workItem);
                     changes.addWorkItemNotificationEvent(
                        AtsNotificationEventFactory.getWorkItemNotificationEvent(AtsCoreUsers.SYSTEM_USER, workItem,
                           AtsNotifyType.Subscribed, AtsNotifyType.Completed, AtsNotifyType.Cancelled));
                  }

                  workItemFromStateMap.put(workItem, fromState.getName());
               }
            } catch (Exception ex) {
               results.addResult(workItem,
                  new TransitionResult(String.format("Exception while transitioning [%s]", transData.getName()), ex));
            }
            results.getWorkItemIds().add(
               ArtifactToken.valueOf(workItem.getId(), workItem.getName(), atsApi.getAtsBranch()));
         }

      } catch (Exception ex) {
         results.addResult(
            new TransitionResult(String.format("Exception while transitioning [%s]", transData.getName()), ex));
      }
      logTimeSpent("20 - handleTransition");
   }

   private void isWorkingBranchTransitionable(TransitionResults results, IAtsWorkItem workItem,
      StateDefinition toStateDef) {
      logTimeStart("05.32 - isWorkingBranchTransitionable");
      if (workItem.isTeamWorkflow()) {
         if (transData.isWorkingBranchInWork((IAtsTeamWorkflow) workItem, atsApi)) {
            if (toStateDef.getName().equals(TeamState.Cancelled.getName())) {
               results.addResult(workItem, TransitionResult.DELETE_WORKING_BRANCH_BEFORE_CANCEL);
            } else if (transData.isBranchInCommit((IAtsTeamWorkflow) workItem, atsApi)) {
               results.addResult(workItem, TransitionResult.WORKING_BRANCH_BEING_COMMITTED);
            } else if (!toStateDef.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch.name())) {
               results.addResult(workItem, TransitionResult.WORKING_BRANCH_EXISTS);
            }
         }
      }
      logTimeSpent("05.32 - isWorkingBranchTransitionable");
   }

   /**
    * @return true if toState is visited and is toState is earlier than current state
    */
   private boolean isOverrideAttributeValidationState(IAtsWorkItem workItem, StateDefinition toStateDef) {
      logTimeStart("05.31 - isWorkingBranchTransitionable");
      Collection<String> visitedStateNames = workItem.getLog().getVisitedStateNames();
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
      logTimeSpent("05.31 - isWorkingBranchTransitionable");
      return false;
   }

   private void isStateTransitionable(TransitionResults results, IAtsWorkItem workItem, StateDefinition toStateDef,
      StateDefinition fromStateDef) {
      logTimeStart("05.4 - isStateTransitionable");
      boolean isOverrideAttributeValidationState =
         transData.isOverrideTransitionValidityCheck() || isOverrideAttributeValidationState(workItem, toStateDef);
      if (toStateDef.isCancelled()) {
         validateTaskCompletion(workItem, toStateDef, taskService);
         validateReviewsCancelled(results, workItem, toStateDef);
      } else if (!toStateDef.isCancelled() && !isOverrideAttributeValidationState) {

         validatePeerDefects(results, workItem, toStateDef);

         // Validate XWidgets for transition
         logTimeStart("05.41 - validateWidgetTransition");
         Collection<WidgetResult> widgetResults = workItemService.validateWidgetTransition(workItem, toStateDef);
         for (WidgetResult widgetResult : widgetResults) {
            if (!widgetResult.isSuccess()) {
               results.addResult(workItem, widgetResult);
            }
         }
         logTimeSpent("05.41 - validateWidgetTransition");

         validateTaskCompletion(workItem, toStateDef, taskService);

         // Don't transition without targeted version if so configured
         boolean teamDefRequiresTargetedVersion =
            workDefService.teamDefHasRule(workItem, RuleDefinitionOption.RequireTargetedVersion);
         boolean pageRequiresTargetedVersion =
            workItem.getStateDefinition().hasRule(RuleDefinitionOption.RequireTargetedVersion.name());

         // Only check this if TeamWorkflow, not for reviews
         if (workItem.isTeamWorkflow() && (teamDefRequiresTargetedVersion || pageRequiresTargetedVersion) && //
            !atsApi.getVersionService().hasTargetedVersion(workItem) && //
            !toStateDef.isCancelled()) {
            results.addResult(workItem, TransitionResult.MUST_BE_TARGETED_FOR_VERSION);
         }

         /**
          * Ensure assignee is selected if required in current state, else current assignees will be used, else current
          * user will be used when transition; Nothing to validate here. See AtsWorkflowLinks.md for design.
          */
         boolean requireAssignee = workItem.getWorkDefinition().getOptions().contains(WorkDefOption.RequireAssignees);
         if (requireAssignee && fromStateDef.isWorking() && transData.isToAssigneesEmptyOrUnassigned()) {
            results.addResult(workItem, TransitionResult.MUST_HAVE_ASSIGNEE);
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
      logTimeSpent("05.4 - isStateTransitionable");
   }

   private void validatePeerDefects(TransitionResults results, IAtsWorkItem workItem, StateDefinition toStateDef) {
      logTimeStart("05.42 - validatePeerDefects");
      if (workItem.isPeerReview() && toStateDef.isCompleted()) {
         IAtsPeerToPeerReview review = (IAtsPeerToPeerReview) workItem;
         IAtsPeerReviewDefectManager defectMgr = review.getDefectManager();

         for (ReviewDefectItem item : defectMgr.getDefectItems()) {
            if (!item.isClosed()) {
               results.addResult(workItem, TransitionResult.REVIEW_DEFECTS_NOT_CLOSED);
               break;
            }
         }

         for (UserRole role : review.getRoleManager().getUserRoles()) {
            if (!role.isCompleted()) {
               results.addResult(workItem, TransitionResult.REVIEW_ROLES_NOT_COMPLETED);
               break;
            }
         }
      }
      logTimeSpent("05.42 - validatePeerDefects");
   }

   private void validateReviewsCancelled(TransitionResults results, IAtsWorkItem workItem, StateDefinition toStateDef) {
      logTimeStart("05.43 - validateReviewsCancelled");
      if (workItem.isTeamWorkflow() && toStateDef.isCancelled()) {
         for (IAtsAbstractReview review : reviewService.getReviewsFromCurrentState((IAtsTeamWorkflow) workItem)) {
            ReviewBlockType reviewBlockType = reviewService.getReviewBlockType(review);
            boolean completedOrCancelled = review.getCurrentStateType().isCompletedOrCancelled();
            if (reviewBlockType == ReviewBlockType.Transition && !completedOrCancelled) {
               results.addResult(workItem, TransitionResult.CANCEL_REVIEWS_BEFORE_CANCEL);
               break;
            }
         }
      }
      logTimeSpent("05.43 - validateReviewsCancelled");
   }

   private void validateTaskCompletion(IAtsWorkItem workItem, StateDefinition toStateDef, IAtsTaskService taskService) {
      logTimeStart("05.44 - validateTaskCompletion");
      validateTaskCompletion(results, workItem, toStateDef, taskService);
      logTimeSpent("05.44 - validateTaskCompletion");
   }

   public static void validateTaskCompletion(TransitionResults results, IAtsWorkItem workItem,
      StateDefinition toStateDef, IAtsTaskService taskService) {
      if (!workItem.isTeamWorkflow()) {
         return;
      }
      // Loop through this state's tasks to confirm complete
      boolean checkTasksCompletedForState = true;
      // Don't check for task completion if transition to working state and AllowTransitionWithoutTaskCompletion rule is set
      if (workItem.getStateDefinition().hasRule(
         RuleDefinitionOption.AllowTransitionWithoutTaskCompletion.name()) && toStateDef.getStateType().isWorking()) {
         checkTasksCompletedForState = false;
      }
      if (checkTasksCompletedForState && workItem.getCurrentStateType().isWorking()) {
         Set<IAtsTask> tasksToCheck = new HashSet<>();
         // If transitioning to completed/cancelled, all tasks must be completed/cancelled
         if (toStateDef.getStateType().isCompletedOrCancelled()) {
            tasksToCheck.addAll(taskService.getTask(workItem));
         }
         // Else, just check current state tasks
         else {
            tasksToCheck.addAll(taskService.getTasks(workItem, workItem.getStateDefinition()));
         }
         for (IAtsTask task : tasksToCheck) {
            if (task.getCurrentStateType().isWorking()) {
               results.addResult(workItem, TransitionResult.TASKS_NOT_COMPLETED);
               break;
            }
         }
      }
   }

   private void logWorkflowCancelledEvent(IAtsWorkItem workItem, StateDefinition fromState, StateDefinition toState,
      Date cancelDate, AtsUser cancelBy, IAtsChangeSet changes, IAttributeResolver attrResolver) {

      logWorkflowCancelledEvent(workItem, fromState, toState, cancelDate, transData.getCancellationReason(),
         transData.getCancellationReasonAttrType(), transData.getCancellationReasonDetails(), cancelBy, changes,
         attrResolver);

   }

   public static void logWorkflowCancelledEvent(IAtsWorkItem workItem, StateDefinition fromState,
      StateDefinition toState, Date cancelDate, String cancelReason, AttributeTypeToken cancelReasonAttrType,
      String cancelReasonDetails, AtsUser cancelBy, IAtsChangeSet changes, IAttributeResolver attrResolver) {

      workItem.getLog().addLog(LogType.StateCancelled, fromState.getName(), cancelReason, cancelDate,
         cancelBy.getUserId());

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
      attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CancelledFromState, fromState.getName(), changes);

      AtsApiService.get().getWorkItemService().getStateMgr(workItem).createOrUpdateState(toState.getName(),
         java.util.Collections.emptyList());

      // Mirror changes in StateManager WorkStates for legacy features
      validateUpdatePercentComplete(workItem, toState, changes);
   }

   private void logWorkflowUnCancelledEvent(IAtsWorkItem workItem, StateDefinition toState, IAtsChangeSet changes,
      AtsUser transitionUser) {

      attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledBy, changes);
      attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledDate, changes);
      changes.deleteAttributes(workItem, AtsAttributeTypes.CancelledReason);
      changes.deleteAttributes(workItem, AtsAttributeTypes.CancelledReasonEnum);
      changes.deleteAttributes(workItem.getStoreObject(), AtsAttributeTypes.CancelledReasonDetails);
      attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CancelledFromState, changes);

      // Mirror changes in StateManager WorkStates for legacy features
      AtsApiService.get().getWorkItemService().getStateMgr(workItem).createOrUpdateState(toState.getName(),
         Arrays.asList(transitionUser));

      validateUpdatePercentComplete(workItem, toState, changes);
   }

   private void logWorkflowCompletedEvent(IAtsWorkItem workItem, StateDefinition fromState, StateDefinition toState,
      Date cancelDate, AtsUser cancelBy, IAtsChangeSet changes) {

      workItem.getLog().addLog(LogType.StateComplete, fromState.getName(), "", cancelDate, cancelBy.getUserId());
      attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedBy, cancelBy.getUserId(), changes);
      attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedDate, cancelDate, changes);
      attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CompletedFromState, fromState.getName(), changes);

      // Mirror changes in StateManager WorkStates for legacy features
      AtsApiService.get().getWorkItemService().getStateMgr(workItem).createOrUpdateState(toState.getName(),
         java.util.Collections.emptyList());

      validateUpdatePercentComplete(workItem, toState, changes);
   }

   private void logWorkflowUnCompletedEvent(IAtsWorkItem workItem, StateDefinition toState, IAtsChangeSet changes,
      AtsUser transitionUser) {

      if (attrResolver.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedBy, changes);
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedDate, changes);
         attrResolver.deleteSoleAttribute(workItem, AtsAttributeTypes.CompletedFromState, changes);
      }

      // Mirror changes in StateManager WorkStates for legacy features
      AtsApiService.get().getWorkItemService().getStateMgr(workItem).createOrUpdateState(toState.getName(),
         java.util.Collections.emptyList());

      validateUpdatePercentComplete(workItem, toState, changes);
   }

   private void updatePercentComplete(IAtsWorkItem workItem, StateDefinition toState, IAtsChangeSet changes) {
      Integer percent = AtsApiService.get().getWorkItemMetricsService().getPercentComplete(workItem);
      if (percent == null) {
         percent = 0;
      }
      if (toState.isWorking()) {
         Integer recPercent = toState.getRecommendedPercentComplete();
         if (recPercent != null && recPercent > 0) {
            AtsApiService.get().getWorkItemMetricsService().setPercentComplete(workItem, recPercent, changes);
         }
         changes.add(workItem);
      }
   }

   private static void validateUpdatePercentComplete(IAtsWorkItem workItem, StateDefinition toState,
      IAtsChangeSet changes) {
      Integer percent = AtsApiService.get().getWorkItemMetricsService().getPercentComplete(workItem);
      if (percent == null) {
         percent = 0;
      }
      if (toState.isCompletedOrCancelled() && percent != 100) {
         AtsApiService.get().getWorkItemMetricsService().setPercentComplete(workItem, 100, changes);
         changes.add(workItem);
      } else if (toState.isWorking() && percent == 100) {
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
      AtsUser user = transData.getTransitionUser();
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
   public Set<AtsUser> getToAssignees(IAtsWorkItem workItem, StateDefinition toState) {
      Set<AtsUser> toAssignees = new HashSet<>();
      if (toState.isWorking()) {
         Collection<AtsUser> requestedAssignees = getToAssignees(workItem);
         if (requestedAssignees != null) {
            for (AtsUser user : requestedAssignees) {
               toAssignees.add(user);
            }
         }

         toAssignees.addAll(getPeerReviewRolesAssignees(workItem, toState));

         if (toAssignees.contains(AtsCoreUsers.UNASSIGNED_USER)) {
            toAssignees.remove(AtsCoreUsers.UNASSIGNED_USER);
            toAssignees.add(getTransitionAsUser());
         }
         if (toAssignees.isEmpty()) {
            if (transData.isSystemUser()) {
               toAssignees.add(AtsCoreUsers.UNASSIGNED_USER);
            } else {
               toAssignees.add(getTransitionAsUser());
            }
         }
      }
      return toAssignees;
   }

   private Collection<AtsUser> getPeerReviewRolesAssignees(IAtsWorkItem workItem, StateDefinition toState) {
      if (workItem.isPeerReview() && toState.isState(PeerToPeerReviewState.Review)) {
         // Set Assignees to all user roles users
         Set<AtsUser> assignees = new HashSet<>();
         IAtsPeerToPeerReview peerRev = (IAtsPeerToPeerReview) workItem;
         for (UserRole uRole : peerRev.getRoleManager().getUserRoles()) {
            if (!uRole.isCompleted()) {
               assignees.add(UserRoleManager.getUser(uRole, atsApi));
            }
         }
         return assignees;
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public void changesStored(IAtsChangeSet changes) {
      // Notify extension points of transitionAndPersist
      for (IAtsTransitionHook listener : getTransitionHooks()) {
         logTimeStart("25.0 - transitionPersisted " + listener.getClass().getSimpleName());

         // Run forground tasks
         listener.transitionPersisted(transData.getWorkItems(), workItemFromStateMap, transData.getToStateName(),
            transData.getTransitionUser(), atsApi);

         // Kickoff background tasks
         if (listener.isBackgroundTask(transData.getWorkItems(), workItemFromStateMap, transData.getToStateName(),
            transData.getTransitionUser(), atsApi)) {
            Thread backgroundTask = new Thread(listener.getClass().getSimpleName()) {

               @Override
               public void run() {
                  listener.transitionPersistedBackground(transData.getWorkItems(), workItemFromStateMap,
                     transData.getToStateName(), transData.getTransitionUser(), atsApi);
               }

            };
            backgroundTask.run();
         }
         logTimeSpent("25.0 - transitionPersisted " + listener.getClass().getSimpleName());
      }
   }

   public Collection<IAtsTransitionHook> getTransitionHooks() {
      try {
         List<IAtsTransitionHook> hooks = new ArrayList<>();
         hooks.addAll(workItemService.getTransitionHooks());
         hooks.addAll(transData.getTransitionHooks());
         return hooks;
      } catch (OseeCoreException ex) {
         OseeLog.log(TransitionManager.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
   }

   public AtsApi getServices() {
      return atsApi;
   }

   public Collection<TransitionOption> getTransitionOptions() {
      return transData.getTransitionOptions();
   }

   public boolean isOverrideAssigneeCheck() {
      return getTransitionOptions().contains(TransitionOption.OverrideAssigneeCheck);
   }

   public boolean isOverrideWorkingBranchCheck() {
      return transData.getTransitionOptions().contains(TransitionOption.OverrideWorkingBranchCheck);
   }

   public IAtsChangeSet getChangeSet() {
      if (changes == null) {
         if (transData.getChanges() != null) {
            changes = transData.getChanges();
         } else {
            AtsUser transitionUser = getTransitionUser();
            changes = atsApi.createChangeSet(getName(), transitionUser);
         }
      }
      return changes;
   }

   public String getName() {
      return transData.getName();
   }

   public AtsUser getTransitionUser() {
      AtsUser user = transData.getTransitionUser();
      if (user == null) {
         user = atsApi.getUserService().getCurrentUser();
      }
      return user;
   }

   public void setTransitionUser(AtsUser user) {
      transData.setTransitionUser(user);
   }

   public Collection<AtsUser> getToAssignees(IAtsWorkItem workItem) {
      return transData.getToAssignees();
   }

   public void addTransitionOption(TransitionOption transitionOption) {
      transData.getTransitionOptions().add(transitionOption);
   }

   public void removeTransitionOption(TransitionOption transitionOption) {
      transData.getTransitionOptions().remove(transitionOption);
   }

   private void logTimeSpent(String key) {
      if (transData.isDebug()) {
         results.getTimeRd().logTimeSpent(key);
      }
   }

   private void logTimeStart(String key) {
      if (transData.isDebug()) {
         results.getTimeRd().logTimeStart(key);
      }
   }
}
