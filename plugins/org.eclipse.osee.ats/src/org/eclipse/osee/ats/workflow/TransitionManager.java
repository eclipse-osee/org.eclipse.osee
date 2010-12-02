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
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.log.LogType;
import org.eclipse.osee.ats.editor.stateItem.AtsStateItemManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.TeamState;
import org.eclipse.osee.ats.util.TransitionOption;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

public class TransitionManager {

   private final AbstractWorkflowArtifact aba;

   public TransitionManager(AbstractWorkflowArtifact aba) {
      this.aba = aba;
   }

   public Result isTransitionValid(final IWorkPage toState, final Collection<User> toAssignees, TransitionOption... transitionOption) throws OseeCoreException {
      boolean overrideTransitionCheck =
         org.eclipse.osee.framework.jdk.core.util.Collections.getAggregate(transitionOption).contains(
            TransitionOption.OverrideTransitionValidityCheck);
      boolean overrideAssigneeCheck =
         org.eclipse.osee.framework.jdk.core.util.Collections.getAggregate(transitionOption).contains(
            TransitionOption.OverrideAssigneeCheck);
      // Validate assignees
      if (!overrideAssigneeCheck && (aba.getStateMgr().getAssignees().contains(
         UserManager.getUser(SystemUser.OseeSystem)) || aba.getStateMgr().getAssignees().contains(
         UserManager.getUser(SystemUser.Guest)) || aba.getStateMgr().getAssignees().contains(
         UserManager.getUser(SystemUser.UnAssigned)))) {
         return new Result("Can not transition with \"Guest\", \"UnAssigned\" or \"OseeSystem\" user as assignee.");
      }

      // Validate toState name
      final WorkPageDefinition fromWorkPageDefinition = aba.getWorkPageDefinition();
      final WorkPageDefinition toWorkPageDefinition = aba.getWorkPageDefinitionByName(toState.getPageName());
      if (toWorkPageDefinition == null) {
         return new Result("Invalid toState \"" + toState + "\"");
      }

      // Validate transition from fromPage to toPage
      if (!overrideTransitionCheck && !aba.getWorkFlowDefinition().getToPages(fromWorkPageDefinition).contains(
         toWorkPageDefinition)) {
         String errStr =
            "Not configured to transition to \"" + toState + "\" from \"" + fromWorkPageDefinition.getPageName() + "\"";
         OseeLog.log(AtsPlugin.class, Level.SEVERE, errStr);
         return new Result(errStr);
      }
      // Don't transition with existing working branch
      if (toWorkPageDefinition.isCancelledPage() && aba.isTeamWorkflow() && ((TeamWorkFlowArtifact) aba).getBranchMgr().isWorkingBranchInWork()) {
         return new Result("Working Branch exists.  Please delete working branch before cancelling.");
      }

      // Don't transition with uncommitted branch if this is a commit state
      if (AtsWorkDefinitions.isAllowCommitBranch(aba.getWorkPageDefinition()) && aba.isTeamWorkflow() && ((TeamWorkFlowArtifact) aba).getBranchMgr().isWorkingBranchInWork()) {
         return new Result("Working Branch exists.  Please commit or delete working branch before transition.");
      }

      // Check extension points for valid transition
      List<IAtsStateItem> atsStateItems = AtsStateItemManager.getStateItems(fromWorkPageDefinition.getId());
      for (IAtsStateItem item : atsStateItems) {
         Result result = item.transitioning(aba, fromWorkPageDefinition, toState, toAssignees);
         if (result.isFalse()) {
            return result;
         }
      }
      for (IAtsStateItem item : atsStateItems) {
         Result result = item.transitioning(aba, fromWorkPageDefinition, toState, toAssignees);
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

         final WorkPageDefinition fromWorkPageDefinition = aba.getWorkPageDefinition();
         final WorkPageDefinition toWorkPageDefinition = aba.getWorkPageDefinitionByName(toState.getPageName());

         transitionHelper(toAssignees, persist, fromWorkPageDefinition, toWorkPageDefinition, toState,
            completeOrCancelReason, transaction);
         if (persist) {
            OseeNotificationManager.getInstance().sendNotifications();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result("Transaction failed " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   private void transitionHelper(Collection<User> toAssignees, boolean persist, WorkPageDefinition fromPage, WorkPageDefinition toPage, IWorkPage toState, String completeOrCancelReason, SkynetTransaction transaction) throws OseeCoreException {
      Date transitionDate = new Date();
      User transitionUser = UserManager.getUser();
      // Log transition
      if (toPage.isCancelledPage()) {
         logWorkflowCancelledEvent(aba.getStateMgr().getCurrentStateName(), completeOrCancelReason, transitionDate,
            transitionUser);
      } else if (toPage.isCompletedPage()) {
         logWorkflowCompletedEvent(aba.getStateMgr().getCurrentStateName(), completeOrCancelReason, transitionDate,
            transitionUser);
      } else {
         logStateCompletedEvent(aba.getStateMgr().getCurrentStateName(), completeOrCancelReason, transitionDate,
            transitionUser);
      }
      if (fromPage.isCancelledPage()) {
         logWorkflowUnCancelledEvent();
      } else if (fromPage.isCompletedPage()) {
         logWorkflowUnCompletedEvent();
      }
      logStateStartedEvent(toState, transitionDate, transitionUser);

      aba.getStateMgr().transitionHelper(toAssignees, fromPage, toPage, toState, completeOrCancelReason);

      if (aba.isValidationRequired() && aba.isTeamWorkflow()) {
         ReviewManager.createValidateReview((TeamWorkFlowArtifact) aba, false, transitionDate, transitionUser,
            transaction);
      }

      AtsNotifyUsers.getInstance().notify(aba, AtsNotifyUsers.NotifyType.Subscribed,
         AtsNotifyUsers.NotifyType.Completed, AtsNotifyUsers.NotifyType.Completed);

      // Persist
      if (persist) {
         aba.persist(transaction);
      }

      aba.transitioned(fromPage, toPage, toAssignees, true, transaction);

      // Notify extension points of transition
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(fromPage.getId())) {
         item.transitioned(aba, fromPage, toState, toAssignees, transaction);
      }
      for (IAtsStateItem item : AtsStateItemManager.getStateItems(toPage.getId())) {
         item.transitioned(aba, fromPage, toState, toAssignees, transaction);
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
      aba.getLog().addLog(LogType.StateCancelled, fromStateName, reason, cancelDate, cancelBy);
      if (aba.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         aba.setSoleAttributeValue(AtsAttributeTypes.CancelledBy, cancelBy.getUserId());
         aba.setSoleAttributeValue(AtsAttributeTypes.CancelledDate, cancelDate);
         aba.setSoleAttributeValue(AtsAttributeTypes.CancelledReason, reason);
         aba.setSoleAttributeValue(AtsAttributeTypes.CancelledFromState, fromStateName);
      }
   }

   public void logWorkflowUnCancelledEvent() throws OseeCoreException {
      if (aba.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         aba.deleteSoleAttribute(AtsAttributeTypes.CancelledBy);
         aba.deleteSoleAttribute(AtsAttributeTypes.CancelledDate);
         aba.deleteSoleAttribute(AtsAttributeTypes.CancelledReason);
         aba.deleteSoleAttribute(AtsAttributeTypes.CancelledFromState);
      }
   }

   public void logWorkflowCompletedEvent(String fromStateName, String reason, Date cancelDate, User cancelBy) throws OseeCoreException {
      aba.getLog().addLog(LogType.StateComplete, fromStateName, Strings.isValid(reason) ? reason : "", cancelDate,
         cancelBy);
      if (aba.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         aba.setSoleAttributeValue(AtsAttributeTypes.CompletedBy, cancelBy.getUserId());
         aba.setSoleAttributeValue(AtsAttributeTypes.CompletedDate, cancelDate);
         aba.setSoleAttributeValue(AtsAttributeTypes.CompletedFromState, fromStateName);
      }
   }

   public void logWorkflowUnCompletedEvent() throws OseeCoreException {
      if (aba.isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         aba.deleteSoleAttribute(AtsAttributeTypes.CompletedBy);
         aba.deleteSoleAttribute(AtsAttributeTypes.CompletedDate);
         aba.deleteSoleAttribute(AtsAttributeTypes.CompletedFromState);
      }
   }

   public void logStateCompletedEvent(String fromStateName, String reason, Date date, User user) throws OseeCoreException {
      aba.getLog().addLog(LogType.StateComplete, fromStateName, Strings.isValid(reason) ? reason : "");
   }

   public void logStateStartedEvent(IWorkPage state, Date date, User user) throws OseeCoreException {
      aba.getLog().addLog(LogType.StateEntered, state.getPageName(), "");
   }

}
