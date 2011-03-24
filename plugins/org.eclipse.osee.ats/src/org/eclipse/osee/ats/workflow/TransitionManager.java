/*
 * Created on Nov 17, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AbstractReviewArtifact;
import org.eclipse.osee.ats.artifact.AbstractTaskableArtifact;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.ReviewManager;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.log.LogType;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.editor.stateItem.AtsStateItemManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.TeamState;
import org.eclipse.osee.ats.util.TransitionOption;
import org.eclipse.osee.ats.util.widgets.dialog.SMAStatusDialog;
import org.eclipse.osee.ats.workdef.ReviewBlockType;
import org.eclipse.osee.ats.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.ui.PlatformUI;

public class TransitionManager {

   private final AbstractWorkflowArtifact awa;
   private SMAEditor editor;

   public TransitionManager(AbstractWorkflowArtifact awa) {
      this.awa = awa;
   }

   public void handleTransition(StateDefinition toStateDefinition, SMAEditor editor) {
      this.editor = editor;
      try {

         awa.setInTransition(true);

         if (toStateDefinition == null) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "No Transition State Selected");
            return;
         }
         if (toStateDefinition.isCancelledPage()) {
            handleTransitionToCancelled(awa);
            return;
         }

         // Validate assignees
         if (awa.getStateMgr().getAssignees().contains(UserManager.getUser(SystemUser.OseeSystem)) || awa.getStateMgr().getAssignees().contains(
            UserManager.getUser(SystemUser.Guest)) || awa.getStateMgr().getAssignees().contains(
            UserManager.getUser(SystemUser.UnAssigned))) {
            AWorkbench.popup("Transition Blocked",
               "Can not transition with \"Guest\", \"UnAssigned\" or \"OseeSystem\" user as assignee.");
            return;
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
         if (!awa.getStateDefinition().getOverrideAttributeValidationStates().contains(toStateDefinition) && !isStateTransitionable(
            toStateDefinition, toAssignees)) {
            return;
         }

         // Persist must be done prior and separate from transition
         awa.persist();

         // Perform transition separate from persist of previous changes to state machine artifact
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Transition");
         TransitionManager transitionMgr = new TransitionManager(awa);
         Result result =
            transitionMgr.transition(toStateDefinition, toAssignees, transaction, TransitionOption.Persist);
         transaction.execute();
         if (result.isFalse()) {
            result.popup();
            return;
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      } finally {
         awa.setInTransition(false);
      }
   }

   private boolean isStateTransitionable(StateDefinition toStateDefinition, Collection<User> toAssignees) throws OseeCoreException {
      // Validate XWidgets for transition
      Result result = editor.getWorkFlowTab().getSectionForCurrentState().getPage().isPageComplete();
      if (result.isFalse()) {
         result.popup();
         return false;
      }

      // Loop through this state's tasks to confirm complete
      if (awa instanceof AbstractTaskableArtifact && !awa.isCompletedOrCancelled()) {
         for (TaskArtifact taskArt : ((AbstractTaskableArtifact) awa).getTaskArtifactsFromCurrentState()) {
            if (taskArt.isInWork()) {
               AWorkbench.popup("Transition Blocked",
                  "Task Not Complete\n\nTitle: " + taskArt.getName() + "\n\nHRID: " + taskArt.getHumanReadableId());
               return false;
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
         AWorkbench.popup("Transition Blocked",
            "Actions must be targeted for a Version.\nPlease set \"Target Version\" before transition.");
         return false;
      }

      // Loop through this state's blocking reviews to confirm complete
      if (awa.isTeamWorkflow()) {
         for (AbstractReviewArtifact reviewArt : ReviewManager.getReviewsFromCurrentState((TeamWorkFlowArtifact) awa)) {
            if (reviewArt.getReviewBlockType() == ReviewBlockType.Transition && !reviewArt.isCompletedOrCancelled()) {
               AWorkbench.popup("Transition Blocked", "All Blocking Reviews must be completed before transition.");
               return false;
            }
         }
      }

      // Check extension points for valid transition
      for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
         try {
            result = item.transitioning(awa, awa.getStateMgr().getCurrentState(), toStateDefinition, toAssignees);
            if (result.isFalse()) {
               result.popup();
               return false;
            }
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Exception occurred during transition; Aborting.", ex);
            return false;
         }
      }

      // Ask for metrics for this page (store in state versus task?)
      if (!handlePopulateStateMetrics()) {
         return false;
      }
      return true;
   }

   public int getCreationToNowDateDeltaMinutes() throws OseeCoreException {
      Date createDate =
         awa.getStateStartedData(editor.getWorkFlowTab().getSectionForCurrentState().getPage()).getDate();
      long createDateLong = createDate.getTime();
      Date date = new Date();
      float diff = date.getTime() - createDateLong;
      // System.out.println("diff *" + diff + "*");
      Float min = diff / 60000;
      // System.out.println("min *" + min + "*");
      return min.intValue();
   }

   private boolean handlePopulateStateMetrics() throws OseeCoreException {
      // Don't log metrics for completed / cancelled states
      if (editor.getWorkFlowTab().getSectionForCurrentState().getPage().isCompletedOrCancelledPage()) {
         return true;
      }

      // Page has the ability to override the autofill of the metrics
      if (!editor.getWorkFlowTab().getSectionForCurrentState().getPage().isRequireStateHoursSpentPrompt() && awa.getStateMgr().getHoursSpent() == 0) {
         // First, try to autofill if it's only been < 5 min since creation
         double minSinceCreation = getCreationToNowDateDeltaMinutes();
         // System.out.println("minSinceCreation *" + minSinceCreation + "*");
         double hoursSinceCreation = minSinceCreation / 60.0;
         if (hoursSinceCreation < 0.02) {
            hoursSinceCreation = 0.02;
         }
         // System.out.println("hoursSinceCreation *" + hoursSinceCreation + "*");
         if (minSinceCreation < 5) {
            awa.getStateMgr().updateMetrics(hoursSinceCreation, 100, true);
            return true;
         }
      }

      if (editor.getWorkFlowTab().getSectionForCurrentState().getPage().isRequireStateHoursSpentPrompt()) {
         // Otherwise, open dialog to ask for hours complete
         String msg =
            awa.getStateMgr().getCurrentStateName() + " State\n\n" + AtsUtil.doubleToI18nString(awa.getStateMgr().getHoursSpent()) + " hours already spent on this state.\n" + "Enter the additional number of hours you spent on this state.";
         SMAStatusDialog tsd =
            new SMAStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Enter Hours Spent",
               msg, false, Arrays.asList(awa));
         int result = tsd.open();
         if (result == 0) {
            awa.getStateMgr().updateMetrics(tsd.getHours().getFloat(), 100, true);
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public void handleTransitionToCancelled(AbstractWorkflowArtifact awa) throws OseeCoreException {
      EntryDialog cancelDialog = new EntryDialog("Cancellation Reason", "Enter cancellation reason.");
      if (cancelDialog.open() != 0) {
         return;
      }
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Transition to Cancelled");
      TransitionManager transitionMgr = new TransitionManager(awa);
      Result result =
         transitionMgr.transitionToCancelled(cancelDialog.getEntry(), transaction, TransitionOption.Persist);
      transaction.execute();
      if (result.isFalse()) {
         result.popup();
         return;
      }
      awa.setInTransition(false);
      editor.refreshPages();
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
         OseeLog.log(AtsPlugin.class, Level.SEVERE, errStr);
         return new Result(errStr);
      }
      // Don't transition with existing working branch
      if (toStateDefinition.isCancelledPage() && awa.isTeamWorkflow() && ((TeamWorkFlowArtifact) awa).getBranchMgr().isWorkingBranchInWork()) {
         return new Result("Working Branch exists.  Please delete working branch before cancelling.");
      }

      // Don't transition with uncommitted branch if this is a commit state
      if (AtsWorkDefinitions.isAllowCommitBranch(awa.getStateDefinition()) && awa.isTeamWorkflow() && ((TeamWorkFlowArtifact) awa).getBranchMgr().isWorkingBranchInWork()) {
         return new Result("Working Branch exists.  Please commit or delete working branch before transition.");
      }

      // Check extension points for valid transition
      List<IAtsStateItem> atsStateItems = AtsStateItemManager.getStateItems();
      for (IAtsStateItem item : atsStateItems) {
         Result result = item.transitioning(awa, fromStateDefinition, toState, toAssignees);
         if (result.isFalse()) {
            return result;
         }
      }
      for (IAtsStateItem item : atsStateItems) {
         Result result = item.transitioning(awa, fromStateDefinition, toState, toAssignees);
         if (result.isFalse()) {
            return result;
         }
      }
      return Result.TrueResult;
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
         if (persist) {
            OseeNotificationManager.getInstance().sendNotifications();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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

      AtsNotifyUsers.getInstance().notify(awa, AtsNotifyUsers.NotifyType.Subscribed,
         AtsNotifyUsers.NotifyType.Completed, AtsNotifyUsers.NotifyType.Completed);

      // Persist
      if (persist) {
         awa.persist(transaction);
      }

      awa.transitioned(fromState, toState, toAssignees, true, transaction);

      // Notify extension points of transition
      for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
         item.transitioned(awa, fromState, toState, toAssignees, transaction);
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
