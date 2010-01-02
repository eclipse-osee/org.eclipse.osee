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
package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationEvent;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class AtsNotifyUsers implements IFrameworkTransactionEventListener {

   private static boolean testing = false; // Email goes to current user (set OseeNotifyUsersJob.testing also)
   public static enum NotifyType {
      Subscribed, Cancelled, Completed, Assigned, Originator, Reviewed
   };

   private static AtsNotifyUsers instance = new AtsNotifyUsers();

   public static AtsNotifyUsers getInstance() {
      return instance;
   }

   private AtsNotifyUsers() {
      if (DbUtil.isDbInit()) return;
      OseeLog.log(AtsPlugin.class, Level.INFO, "Starting ATS Notification Handler");
      OseeEventManager.addListener(this);
   }

   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   public static void notify(StateMachineArtifact sma, NotifyType... notifyTypes) throws OseeCoreException {
      notify(sma, null, notifyTypes);
   }

   public static void notify(StateMachineArtifact sma, Collection<User> notifyUsers, NotifyType... notifyTypes) throws OseeCoreException {
      if (testing) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE,
               "AtsNotifyUsers: testing is enabled....turn off for production.");
      }
      if (!testing && (!AtsUtil.isEmailEnabled() || !AtsUtil.isProductionDb() || sma.getName().startsWith("tt "))) {
         return;
      }
      List<NotifyType> types = Collections.getAggregate(notifyTypes);

      if (types.contains(NotifyType.Originator)) {
         User originator = sma.getOriginator();
         if (testing) {
            originator = UserManager.getUser();
         }
         if (!EmailUtil.isEmailValid(originator)) {
            OseeLog.log(AtsPlugin.class, OseeLevel.INFO, String.format("Email [%s] invalid for user [%s]",
                  originator.getEmail(), originator.getName()));
         } else if (!UserManager.getUser().equals(originator)) OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
               Arrays.asList(originator),
               getIdString(sma),
               NotifyType.Originator.name(),
               "You have been set as the originator of \"" + sma.getArtifactTypeName() + "\" titled \"" + sma.getName() + "\""));
      }
      if (types.contains(NotifyType.Assigned)) {
         Collection<User> assignees = notifyUsers != null ? notifyUsers : sma.getStateMgr().getAssignees();
         assignees.remove(UserManager.getUser());
         if (testing) {
            assignees.clear();
            assignees.add(UserManager.getUser());
         }
         assignees = EmailUtil.getValidEmailUsers(assignees);
         if (testing || assignees.size() > 0) {
            OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                  assignees,
                  getIdString(sma),
                  NotifyType.Assigned.name(),
                  "You have been set as an assignee for \"" + sma.getArtifactTypeName() + "\" titled \"" + sma.getName() + "\""));
         }
      }
      if (types.contains(NotifyType.Subscribed)) {
         Collection<User> subscribed = sma.getSubscribed();
         if (testing) {
            subscribed.clear();
            subscribed.add(UserManager.getUser());
         }
         subscribed = EmailUtil.getValidEmailUsers(subscribed);
         if (subscribed.size() > 0) {
            OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                  subscribed,
                  getIdString(sma),
                  NotifyType.Subscribed.name(),
                  sma.getArtifactTypeName() + " titled \"" + sma.getName() + "\" transitioned to \"" + sma.getStateMgr().getCurrentStateName() + "\" and you subscribed for notification."));
         }
      }
      if (types.contains(NotifyType.Cancelled) || types.contains(NotifyType.Completed)) {
         if (((sma.isTeamWorkflow()) || (sma instanceof ReviewSMArtifact)) && (sma.isCompleted() || sma.isCancelled())) {
            User originator = sma.getOriginator();
            if (testing) {
               originator = UserManager.getUser();
            }
            if (!EmailUtil.isEmailValid(originator)) {
               OseeLog.log(AtsPlugin.class, OseeLevel.INFO, String.format("Email [%s] invalid for user [%s]",
                     originator.getEmail(), originator.getName()));
            } else if (!UserManager.getUser().equals(originator)) {
               if (sma.isCompleted()) {
                  OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(Arrays.asList(originator),
                        getIdString(sma), NotifyType.Completed.name(),
                        "\"" + sma.getArtifactTypeName() + "\" titled \"" + sma.getName() + "\" is Completed"));
               }
               if (sma.isCancelled()) {
                  LogItem cancelledItem = sma.getLog().getStateEvent(LogType.StateCancelled);
                  OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                        Arrays.asList(originator),
                        getIdString(sma),
                        NotifyType.Cancelled.name(),
                        String.format(
                              sma.getArtifactTypeName() + " titled \"" + sma.getName() + "\" was cancelled from the \"%s\" state on \"%s\".<br>Reason: \"%s\"",
                              cancelledItem.getState(), cancelledItem.getDate(XDate.MMDDYYHHMM), cancelledItem.getMsg())));
               }
            }
         }
      }
      if (types.contains(NotifyType.Reviewed)) {
         if (sma instanceof ReviewSMArtifact) {
            if (((ReviewSMArtifact) sma).getUserRoleManager() != null) {
               Collection<User> authorModerator =
                     ((ReviewSMArtifact) sma).getUserRoleManager().getRoleUsersAuthorModerator();
               if (testing) {
                  authorModerator.clear();
                  authorModerator.add(UserManager.getUser());
               }
               authorModerator = EmailUtil.getValidEmailUsers(authorModerator);
               if (authorModerator.size() > 0) {
                  for (UserRole role : ((ReviewSMArtifact) sma).getUserRoleManager().getRoleUsersReviewComplete()) {
                     OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                           authorModerator,
                           getIdString(sma),
                           NotifyType.Reviewed.name(),
                           "\"" + sma.getArtifactTypeName() + "\" titled \"" + sma.getName() + "\" has been Reviewed by " + role.getUser().getName()));
                  }
               }
            }
         }
      }
   }

   private static String getIdString(StateMachineArtifact sma) {
      try {
         String legacyPcrId = sma.getWorldViewLegacyPCR();
         if (!legacyPcrId.equals("")) {
            return "HRID: " + sma.getHumanReadableId() + " / LegacyId: " + legacyPcrId;
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return "HRID: " + sma.getHumanReadableId();
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (DbUtil.isDbInit()) return;
      // Only process notifications if this client is sender
      if (sender.isRemote()) return;
      if (transData.branchId != AtsUtil.getAtsBranch().getId()) return;
      boolean notificationAdded = false;
      try {
         // Handle notifications for subscription by TeamDefinition and ActionableItem
         for (Artifact art : transData.cacheAddedArtifacts) {
            if (art instanceof TeamWorkFlowArtifact) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;

               // Handle Team Definitions
               Collection<User> subscribedUsers =
                     Collections.castAll(teamArt.getTeamDefinition().getRelatedArtifacts(
                           AtsRelationTypes.SubscribedUser_User));
               if (subscribedUsers.size() > 0) {
                  notificationAdded = true;
                  OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                        subscribedUsers,
                        getIdString(teamArt),
                        "Workflow Creation",
                        "You have subscribed for email notification for Team \"" + teamArt.getTeamName() + "\"; New Team Workflow created with title \"" + teamArt.getName() + "\""));
               }

               // Handle Actionable Items
               for (ActionableItemArtifact aia : teamArt.getActionableItemsDam().getActionableItems()) {
                  subscribedUsers = Collections.castAll(aia.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User));
                  if (subscribedUsers.size() > 0) {
                     notificationAdded = true;
                     OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                           subscribedUsers,
                           getIdString(teamArt),
                           "Workflow Creation",
                           "You have subscribed for email notification for Actionable Item \"" + teamArt.getTeamName() + "\"; New Team Workflow created with title \"" + teamArt.getName() + "\""));
                  }
               }
            }
         }
      } finally {
         if (notificationAdded) {
            OseeNotificationManager.sendNotifications();
         }
      }
   }
}
