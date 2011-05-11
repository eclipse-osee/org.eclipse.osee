/*
 * Created on Nov 17, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.review.ReviewManager;
import org.eclipse.osee.ats.core.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.ATSAttributes;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.WorkflowManagerCore;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetOption;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.log.LogType;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.WorkPageType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.validation.OseeXWidgetValidateManager;

public class TransitionManager {

   private final AbstractWorkflowArtifact awa;
   private StateDefinition toStateDefinition;
   private final boolean priviledgedEditEnabled;
   private String cancellationReason;

   public TransitionManager(AbstractWorkflowArtifact awa) {
      this(awa, false);
   }

   public TransitionManager(AbstractWorkflowArtifact awa, boolean priviledgedEditEnabled) {
      this.awa = awa;
      this.priviledgedEditEnabled = priviledgedEditEnabled;
   }

   public Result handleTransition(StateDefinition toStateDefinition, String cancellationReason) {
      this.toStateDefinition = toStateDefinition;
      this.cancellationReason = cancellationReason;
      try {

         if (!WorkflowManagerCore.isEditable(awa, awa.getStateDefinition(), priviledgedEditEnabled) && !awa.getStateMgr().getAssignees().contains(
            UserManager.getUser(SystemUser.UnAssigned))) {
            return new Result(
               "You must be assigned to transition this workflow.\nContact Assignee or Select Priviledged Edit for Authorized Overriders.");
         }

         Result result = isWorkingBranchTransitionable();
         if (result.isFalse()) {
            return result;
         }

         if (toStateDefinition == null) {
            return new Result("No Transition State Selected");
         }
         if (toStateDefinition.isCancelledPage()) {
            return handleTransitionToCancelled(awa);
         }

         // Validate assignees
         if (awa.getStateMgr().getAssignees().contains(UserManager.getUser(SystemUser.OseeSystem)) || awa.getStateMgr().getAssignees().contains(
            UserManager.getUser(SystemUser.Guest)) || awa.getStateMgr().getAssignees().contains(
            UserManager.getUser(SystemUser.UnAssigned))) {
            return new Result("Can not transition with \"Guest\", \"UnAssigned\" or \"OseeSystem\" user as assignee.");
         }

         awa.setInTransition(true);

         // As a convenience, if assignee is UnAssigned and user selects to transition, make user current assignee
         if (awa.getStateMgr().getAssignees().contains(UserManager.getUser(SystemUser.UnAssigned))) {
            awa.getStateMgr().removeAssignee(UserManager.getUser(SystemUser.UnAssigned));
            awa.getStateMgr().addAssignee(UserManager.getUser());
         }

         // Get transition to assignees
         Collection<User> toAssignees;
         if (toStateDefinition.isCancelledPage() || toStateDefinition.isCompletedPage()) {
            toAssignees = new HashSet<User>();
         } else {
            toAssignees = awa.getTransitionAssignees();
            if (toAssignees.isEmpty()) {
               toAssignees.add(UserManager.getUser());
            }
         }

         // If overrideAttributeValidation state, don't require page/tasks to be complete
         boolean isOverrideAttributeValidationState =
            awa.getStateDefinition().getOverrideAttributeValidationStates().contains(toStateDefinition);
         if (!isOverrideAttributeValidationState) {
            result = isStateTransitionable(awa.getStateDefinition(), toStateDefinition, toAssignees);
            if (result.isFalse()) {
               return result;
            }
         }

         // Persist must be done prior and separate from transition
         awa.persist();

         // Perform transition separate from persist of previous changes to state machine artifact
         SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), "ATS Transition");
         result = transition(toStateDefinition, toAssignees, transaction, TransitionOption.Persist);
         transaction.execute();
         if (result.isFalse()) {
            return result;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      } finally {
         awa.setInTransition(false);
      }
      return Result.TrueResult;
   }

   private Result isWorkingBranchTransitionable() throws OseeCoreException {
      if (awa.isTeamWorkflow() && AtsBranchManagerCore.isWorkingBranchInWork(((TeamWorkFlowArtifact) awa))) {

         if (toStateDefinition.getPageName().equals(TeamState.Cancelled.getPageName())) {
            new Result("Working Branch exists.\n\nPlease delete working branch before transition to cancel.");
         }
         if (AtsBranchManagerCore.isBranchInCommit(((TeamWorkFlowArtifact) awa))) {
            new Result("Working Branch is being Committed.\n\nPlease wait till commit completes to transition.");
         }
         if (!isAllowTransitionWithWorkingBranch(toStateDefinition)) {
            new Result("Working Branch exists.\n\nPlease commit or delete working branch before transition.");
         }
      }
      return Result.TrueResult;
   }

   public static boolean isAllowTransitionWithWorkingBranch(StateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch);
   }

   /**
    * Return collection of problems with state widgets and artifact model storage
    */
   private Collection<ValidResult> isStateValid(AbstractWorkflowArtifact awa, StateDefinition stateDef) throws OseeCoreException {
      List<ValidResult> results = new ArrayList<ValidResult>();
      for (WidgetDefinition widgetDef : stateDef.getWidgets()) {
         ValidResult result = isWidgetValid(awa, widgetDef);
         if (result != null) {
            results.add(result);
         }
      }
      return results;
   }

   private static enum ValidType {
      RequiredForCompletion,
      RequiredForTransition;
   }

   private static class ValidResult {
      public ValidType type;
      public WidgetDefinition widgetDef;

      public ValidResult(ValidType type, WidgetDefinition widgetDef) {
         super();
         this.type = type;
         this.widgetDef = widgetDef;
      }
   }

   /**
    * Return result of validity between widget and artifact model storage
    */
   public static ValidResult isWidgetValid(AbstractWorkflowArtifact awa, WidgetDefinition widgetDef) throws OseeCoreException {
      // validate first with providers of validation
      if (Strings.isValid(widgetDef.getXWidgetName())) {
         OseeXWidgetValidateManager.instance.validate(awa, widgetDef.getXWidgetName(), widgetDef.getName());
      }

      // else fallback on attribute validation if this is an artifact stored widget
      if (Strings.isValid(widgetDef.getAtrributeName())) {
         if (widgetDef.getOptions().contains(WidgetOption.REQUIRED_FOR_COMPLETION)) {
            if (awa.getAttributesToStringList(AttributeTypeManager.getType(widgetDef.getAtrributeName())).isEmpty()) {
               return new ValidResult(ValidType.RequiredForCompletion, widgetDef);
            }
         } else if (widgetDef.getOptions().contains(WidgetOption.REQUIRED_FOR_TRANSITION)) {
            if (awa.getAttributesToStringList(AttributeTypeManager.getType(widgetDef.getAtrributeName())).isEmpty()) {
               return new ValidResult(ValidType.RequiredForTransition, widgetDef);
            }
         }
      }
      return null;
   }

   private Result isStateTransitionable(StateDefinition fromStateDefinition, StateDefinition toStateDefinition, Collection<User> toAssignees) throws OseeCoreException {
      // Validate XWidgets for transition
      Collection<ValidResult> stateValid = isStateValid(awa, fromStateDefinition);
      StringBuffer sb = new StringBuffer();
      for (ValidResult validResult : stateValid) {
         // Stop transition if any errors exist
         if (validResult.type == ValidType.RequiredForTransition) {
            sb.append(String.format("[%s] required for transition\n", validResult.widgetDef.getName()));
         }
         // Only stop transition on warning if transitioning to completed state and REQUIRED_FOR_COMPLETION
         else if (validResult.type == ValidType.RequiredForCompletion) {
            boolean reqForCompletion =
               validResult.widgetDef.getOptions().contains(WidgetOption.REQUIRED_FOR_COMPLETION);
            if (reqForCompletion && toStateDefinition.getWorkPageType() == WorkPageType.Completed) {
               sb.append(String.format("[%s] required for completion\n", validResult.widgetDef.getName()));
            }
         }
      }
      if (!sb.toString().isEmpty()) {
         return new Result(sb.toString());
      }

      // Loop through this state's tasks to confirm complete
      if (awa instanceof AbstractTaskableArtifact && !awa.isCompletedOrCancelled()) {
         for (TaskArtifact taskArt : ((AbstractTaskableArtifact) awa).getTaskArtifactsFromCurrentState()) {
            if (taskArt.isInWork()) {
               return new Result(
                  "Transition Blocked: Task Not Complete\n\nTitle: " + taskArt.getName() + "\n\nHRID: " + taskArt.getHumanReadableId());
            }
         }
      }

      // Don't transition without targeted version if so configured
      boolean teamDefRequiresTargetedVersion = awa.teamDefHasRule(RuleDefinitionOption.RequireTargetedVersion);
      boolean pageRequiresTargetedVersion =
         awa.getStateDefinition().hasRule(RuleDefinitionOption.RequireTargetedVersion);

      // Only check this if TeamWorkflow, not for reviews
      if (awa.isOfType(AtsArtifactTypes.TeamWorkflow) && (teamDefRequiresTargetedVersion || pageRequiresTargetedVersion) && //
      awa.getTargetedVersion() == null && //
      !toStateDefinition.isCancelledPage()) {
         return new Result(
            "Transition Blocked: Actions must be targeted for a Version.\nPlease set \"Target Version\" before transition.");
      }

      // Loop through this state's blocking reviews to confirm complete
      if (awa.isTeamWorkflow()) {
         for (AbstractReviewArtifact reviewArt : ReviewManager.getReviewsFromCurrentState((TeamWorkFlowArtifact) awa)) {
            if (reviewArt.getReviewBlockType() == ReviewBlockType.Transition && !reviewArt.isCompletedOrCancelled()) {
               return new Result("Transition Blocked: All Blocking Reviews must be completed before transition.");
            }
         }
      }

      // Check extension points for valid transition
      for (ITransitionListener listener : TransitionListeners.getListeners()) {
         try {
            Result result =
               listener.transitioning(awa, awa.getStateMgr().getCurrentState(), toStateDefinition, toAssignees);
            if (result.isFalse()) {
               return result;
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Exception occurred during transition; Aborting.", ex);
            return new Result(String.format("Exception occurred during transition; Aborting. [%s]",
               ex.getLocalizedMessage()));
         }
      }

      return Result.TrueResult;
   }

   private Result handleTransitionToCancelled(AbstractWorkflowArtifact awa) throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), "ATS Transition to Cancelled");
      TransitionManager transitionMgr = new TransitionManager(awa);
      Result result = transitionMgr.transitionToCancelled(cancellationReason, transaction, TransitionOption.Persist);
      if (result.isFalse()) {
         return result;
      }
      awa.setInTransition(false);
      transaction.execute();
      return Result.TrueResult;
   }

   public Result isTransitionValid(final IWorkPage toState, final Collection<User> toAssignees, TransitionOption... transitionOption) throws OseeCoreException {
      boolean overrideTransitionCheck =
         org.eclipse.osee.framework.jdk.core.util.Collections.getAggregate(transitionOption).contains(
            TransitionOption.OverrideTransitionValidityCheck);
      boolean overrideAssigneeCheck =
         org.eclipse.osee.framework.jdk.core.util.Collections.getAggregate(transitionOption).contains(
            TransitionOption.OverrideAssigneeCheck);
      // Validate assignees
      if (!overrideAssigneeCheck && (awa.getStateMgr().getAssignees().contains(
         UserManager.getUser(SystemUser.OseeSystem)) || awa.getStateMgr().getAssignees().contains(
         UserManager.getUser(SystemUser.Guest)) || awa.getStateMgr().getAssignees().contains(
         UserManager.getUser(SystemUser.UnAssigned)))) {
         return new Result("Can not transition with \"Guest\", \"UnAssigned\" or \"OseeSystem\" user as assignee.");
      }

      // Validate toState name
      final StateDefinition fromStateDefinition = awa.getStateDefinition();
      final StateDefinition toStateDefinition = awa.getStateDefinitionByName(toState.getPageName());
      if (toStateDefinition == null) {
         return new Result(String.format("Transition-To State [%s] does not exist for Work Definition [%s]",
            toState.getPageName(), awa.getWorkDefinition().getName()));
      }

      // Validate transition from fromPage to toPage
      if (!overrideTransitionCheck && !fromStateDefinition.getToStates().contains(toStateDefinition) && !fromStateDefinition.isCompletedOrCancelledPage()) {
         String errStr =
            String.format("Work Definition [%s] is not configured to transition from \"[%s]\" to \"[%s]\"",
               toStateDefinition.getName(), fromStateDefinition.getPageName(), toState.getPageName());
         OseeLog.log(Activator.class, Level.SEVERE, errStr);
         return new Result(errStr);
      }
      // Don't transition with existing working branch
      if (toStateDefinition.isCancelledPage() && awa.isTeamWorkflow() && AtsBranchManagerCore.isWorkingBranchInWork(((TeamWorkFlowArtifact) awa))) {
         return new Result("Working Branch exists.  Please delete working branch before cancelling.");
      }

      // Don't transition with uncommitted branch if this is a commit state
      if (isAllowCommitBranch(awa.getStateDefinition()) && awa.isTeamWorkflow() && AtsBranchManagerCore.isWorkingBranchInWork(((TeamWorkFlowArtifact) awa))) {
         return new Result("Working Branch exists.  Please commit or delete working branch before transition.");
      }

      // Check extension points for valid transition
      for (ITransitionListener listener : TransitionListeners.getListeners()) {
         Result result = listener.transitioning(awa, fromStateDefinition, toState, toAssignees);
         if (result.isFalse()) {
            return result;
         }
      }
      // Check again in case first check made changes that would now keep transition from happening
      for (ITransitionListener listener : TransitionListeners.getListeners()) {
         Result result = listener.transitioning(awa, fromStateDefinition, toState, toAssignees);
         if (result.isFalse()) {
            return result;
         }
      }
      return Result.TrueResult;
   }

   public static boolean isAllowCommitBranch(StateDefinition stateDefinition) {
      return stateDefinition.hasRule(ATSAttributes.COMMIT_MANAGER_WIDGET.getWorkItemId());
   }

   public Result transition(IWorkPage toState, User toAssignee, SkynetTransaction transaction, TransitionOption... transitionOption) {
      List<User> users = new ArrayList<User>();
      if (toAssignee != null && !toState.getWorkPageType().isCompletedOrCancelledPage()) {
         users.add(toAssignee);
      }
      return transition(toState, users, transaction, transitionOption);
   }

   public Result transition(IWorkPage toState, Collection<User> toAssignees, SkynetTransaction transaction, TransitionOption... transitionOption) {
      return transition(toState, toAssignees, (String) null, transaction, transitionOption);
   }

   private Result transition(final IWorkPage toState, final Collection<User> toAssignees, final String completeOrCancelReason, SkynetTransaction transaction, TransitionOption... transitionOption) {
      try {
         final boolean persist =
            org.eclipse.osee.framework.jdk.core.util.Collections.getAggregate(transitionOption).contains(
               TransitionOption.Persist);

         Result result = isTransitionValid(toState, toAssignees, transitionOption);
         if (result.isFalse()) {
            return result;
         }

         final StateDefinition fromStateDefinition = awa.getStateDefinition();
         final StateDefinition toStateDefinition = awa.getStateDefinitionByName(toState.getPageName());

         transitionHelper(toAssignees, persist, fromStateDefinition, toStateDefinition, completeOrCancelReason,
            transaction);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result("Transaction failed " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   private void transitionHelper(Collection<User> toAssignees, boolean persist, StateDefinition fromState, StateDefinition toState, String completeOrCancelReason, SkynetTransaction transaction) throws OseeCoreException {
      Date transitionDate = new Date();
      User transitionUser = UserManager.getUser();
      // Log transition
      if (toState.isCancelledPage()) {
         logWorkflowCancelledEvent(awa.getStateMgr().getCurrentStateName(), completeOrCancelReason, transitionDate,
            transitionUser);
      } else if (toState.isCompletedPage()) {
         logWorkflowCompletedEvent(awa.getStateMgr().getCurrentStateName(), completeOrCancelReason, transitionDate,
            transitionUser);
      } else {
         logStateCompletedEvent(awa.getStateMgr().getCurrentStateName(), completeOrCancelReason, transitionDate,
            transitionUser);
      }
      if (fromState.isCancelledPage()) {
         logWorkflowUnCancelledEvent();
      } else if (fromState.isCompletedPage()) {
         logWorkflowUnCompletedEvent();
      }
      logStateStartedEvent(toState, transitionDate, transitionUser);

      awa.getStateMgr().transitionHelper(toAssignees, fromState, toState, completeOrCancelReason);

      if (awa.isValidationRequired() && awa.isTeamWorkflow()) {
         ReviewManager.createValidateReview((TeamWorkFlowArtifact) awa, false, transitionDate, transitionUser,
            transaction);
      }

      // Persist
      if (persist) {
         awa.persist(transaction);
      }

      awa.transitioned(fromState, toState, toAssignees, true, transaction);

      // Notify extension points of transition
      for (ITransitionListener listener : TransitionListeners.getListeners()) {
         listener.transitioned(awa, fromState, toState, toAssignees, transaction);
      }
   }

   public Result transitionToCancelled(String reason, SkynetTransaction transaction, TransitionOption... transitionOption) {
      Result result =
         transition(TeamState.Cancelled, Arrays.asList(new User[] {}), reason, transaction, transitionOption);
      return result;
   }

   public Result transitionToCompleted(String reason, SkynetTransaction transaction, TransitionOption... transitionOption) {
      Result result =
         transition(TeamState.Completed, Arrays.asList(new User[] {}), reason, transaction, transitionOption);
      return result;
   }

   public void logWorkflowCancelledEvent(String fromStateName, String reason, Date cancelDate, User cancelBy) throws OseeCoreException {
      awa.getLog().addLog(LogType.StateCancelled, fromStateName, reason, cancelDate, cancelBy);
      if (awa.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         awa.setSoleAttributeValue(AtsAttributeTypes.CancelledBy, cancelBy.getUserId());
         awa.setSoleAttributeValue(AtsAttributeTypes.CancelledDate, cancelDate);
         awa.setSoleAttributeValue(AtsAttributeTypes.CancelledReason, reason);
         awa.setSoleAttributeValue(AtsAttributeTypes.CancelledFromState, fromStateName);
      }
   }

   public void logWorkflowUnCancelledEvent() throws OseeCoreException {
      if (awa.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         awa.deleteSoleAttribute(AtsAttributeTypes.CancelledBy);
         awa.deleteSoleAttribute(AtsAttributeTypes.CancelledDate);
         awa.deleteSoleAttribute(AtsAttributeTypes.CancelledReason);
         awa.deleteSoleAttribute(AtsAttributeTypes.CancelledFromState);
      }
   }

   public void logWorkflowCompletedEvent(String fromStateName, String reason, Date cancelDate, User cancelBy) throws OseeCoreException {
      awa.getLog().addLog(LogType.StateComplete, fromStateName, Strings.isValid(reason) ? reason : "", cancelDate,
         cancelBy);
      if (awa.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedBy, cancelBy.getUserId());
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedDate, cancelDate);
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedFromState, fromStateName);
      }
   }

   public void logWorkflowUnCompletedEvent() throws OseeCoreException {
      if (awa.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         awa.deleteSoleAttribute(AtsAttributeTypes.CompletedBy);
         awa.deleteSoleAttribute(AtsAttributeTypes.CompletedDate);
         awa.deleteSoleAttribute(AtsAttributeTypes.CompletedFromState);
      }
   }

   public void logStateCompletedEvent(String fromStateName, String reason, Date date, User user) throws OseeCoreException {
      awa.getLog().addLog(LogType.StateComplete, fromStateName, Strings.isValid(reason) ? reason : "");
   }

   public void logStateStartedEvent(IWorkPage state, Date date, User user) throws OseeCoreException {
      awa.getLog().addLog(LogType.StateEntered, state.getPageName(), "");
   }

}
