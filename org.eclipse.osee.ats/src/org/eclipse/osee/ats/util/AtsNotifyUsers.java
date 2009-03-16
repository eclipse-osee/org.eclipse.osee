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
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationEvent;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class AtsNotifyUsers {

   private static boolean testing = false; // Email goes to current user (set OseeNotifyUsersJob.testing also)
   public static enum NotifyType {
      Subscribed, Cancelled, Completed, Assigned, Originator, Reviewed
   };

   public static void notify(StateMachineArtifact sma, NotifyType... notifyTypes) throws OseeCoreException {
      notify(sma, null, notifyTypes);
   }

   public static void notify(StateMachineArtifact sma, Collection<User> notifyUsers, NotifyType... notifyTypes) throws OseeCoreException {
      if (testing) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE,
               "AtsNotifyUsers: testing is enabled....turn off for production.");
      }
      if (!testing && (!AtsPlugin.isEmailEnabled() || !AtsPlugin.isProductionDb() || sma.getDescriptiveName().startsWith(
            "tt "))) {
         return;
      }
      List<NotifyType> types = Collections.getAggregate(notifyTypes);

      SMAManager smaMgr = sma.getSmaMgr();
      if (types.contains(NotifyType.Originator)) {
         User originator = smaMgr.getOriginator();
         if (testing) {
            originator = UserManager.getUser();
         }
         if (!UserManager.getUser().equals(originator)) OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
               Arrays.asList(originator),
               getIdString(sma),
               NotifyType.Originator.name(),
               "You have been set as the originator of \"" + sma.getArtifactTypeName() + "\" titled \"" + sma.getDescriptiveName() + "\""));
      }
      if (types.contains(NotifyType.Assigned)) {
         Collection<User> assignees = notifyUsers != null ? notifyUsers : smaMgr.getStateMgr().getAssignees();
         assignees.remove(UserManager.getUser());
         if (testing) {
            assignees.clear();
            assignees.add(UserManager.getUser());
         }
         if (testing || assignees.size() > 0) {
            OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                  assignees,
                  getIdString(sma),
                  NotifyType.Assigned.name(),
                  "You have been set as an assignee for \"" + sma.getArtifactTypeName() + "\" titled \"" + sma.getDescriptiveName() + "\""));
         }
      }
      if (types.contains(NotifyType.Subscribed)) {
         Collection<User> subscribed = sma.getSubscribed();
         if (testing) {
            subscribed.clear();
            subscribed.add(UserManager.getUser());
         }
         if (subscribed.size() > 0) {
            OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                  subscribed,
                  getIdString(sma),
                  NotifyType.Subscribed.name(),
                  sma.getArtifactTypeName() + " titled \"" + sma.getDescriptiveName() + "\" transitioned to \"" + sma.getSmaMgr().getStateMgr().getCurrentStateName() + "\" and you subscribed for notification."));
         }
      }
      if (types.contains(NotifyType.Cancelled) || types.contains(NotifyType.Completed)) {
         if (((sma instanceof TeamWorkFlowArtifact) || (sma instanceof ReviewSMArtifact)) && (smaMgr.isCompleted() || smaMgr.isCancelled())) {
            User originator = smaMgr.getOriginator();
            if (testing) {
               originator = UserManager.getUser();
            }
            if (!UserManager.getUser().equals(originator)) {
               if (smaMgr.isCompleted()) {
                  OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                        Arrays.asList(originator),
                        getIdString(sma),
                        NotifyType.Completed.name(),
                        "\"" + sma.getArtifactTypeName() + "\" titled \"" + sma.getDescriptiveName() + "\" is Completed"));
               }
               if (smaMgr.isCancelled()) {
                  LogItem cancelledItem = smaMgr.getLog().getStateEvent(LogType.StateCancelled);
                  OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                        Arrays.asList(originator),
                        getIdString(sma),
                        NotifyType.Cancelled.name(),
                        String.format(
                              sma.getArtifactTypeName() + " titled \"" + sma.getDescriptiveName() + "\" was cancelled from the \"%s\" state on \"%s\".<br>Reason: \"%s\"",
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
               for (UserRole role : ((ReviewSMArtifact) sma).getUserRoleManager().getRoleUsersReviewComplete()) {
                  OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                        authorModerator,
                        getIdString(sma),
                        NotifyType.Reviewed.name(),
                        "\"" + sma.getArtifactTypeName() + "\" titled \"" + sma.getDescriptiveName() + "\" has been Reviewed by " + role.getUser().getName()));
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
}
