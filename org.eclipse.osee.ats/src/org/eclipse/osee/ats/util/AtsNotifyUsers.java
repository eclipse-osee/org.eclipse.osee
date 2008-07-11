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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationEvent;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class AtsNotifyUsers {
   public static enum NotifyType {
      Subscribed, Cancelled, Completed, Assigned, Originator
   };

   public static void notify(StateMachineArtifact sma, NotifyType... notifyTypes) throws IllegalArgumentException, SQLException {
      notify(sma, null, notifyTypes);
   }

   public static void notify(StateMachineArtifact sma, Collection<User> notifyUsers, NotifyType... notifyTypes) throws IllegalArgumentException, SQLException {
      if (!AtsPlugin.isEmailEnabled() || !AtsPlugin.isProductionDb() || sma.getDescriptiveName().startsWith("tt ")) {
         return;
      }
      List<NotifyType> types = Collections.getAggregate(notifyTypes);

      SMAManager smaMgr = sma.getSmaMgr();
      if (types.contains(NotifyType.Originator)) {
         User originator = smaMgr.getOriginator();
         if (!SkynetAuthentication.getUser().equals(originator)) OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
               Arrays.asList(originator),
               sma.getHumanReadableId(),
               NotifyType.Originator.name(),
               "You have been set as the originator of \"" + sma.getArtifactTypeName() + "\" titled \"" + sma.getDescriptiveName() + "\""));
      }
      if (types.contains(NotifyType.Assigned)) {
         Collection<User> assignees = notifyUsers != null ? notifyUsers : smaMgr.getStateMgr().getAssignees();
         assignees.remove(SkynetAuthentication.getUser());
         if (assignees.size() > 0) {
            OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                  assignees,
                  sma.getHumanReadableId(),
                  NotifyType.Assigned.name(),
                  "You have been set as an assignee for \"" + sma.getArtifactTypeName() + "\" titled \"" + sma.getDescriptiveName() + "\""));
         }
      }
      if (types.contains(NotifyType.Subscribed)) {
         Collection<User> subscribed = sma.getSubscribed();
         if (subscribed.size() > 0) {
            OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                  subscribed,
                  sma.getHumanReadableId(),
                  NotifyType.Subscribed.name(),
                  sma.getArtifactTypeName() + " titled \"" + sma.getDescriptiveName() + "\" transitioned to \"" + sma.getSmaMgr().getStateMgr().getCurrentStateName() + "\" and you subscribed for notification."));
         }
      }
      if (types.contains(NotifyType.Cancelled) || types.contains(NotifyType.Completed)) {
         if (((sma instanceof TeamWorkFlowArtifact) || (sma instanceof ReviewSMArtifact)) && (smaMgr.isCompleted() || smaMgr.isCancelled())) {
            User originator = smaMgr.getOriginator();
            if (!SkynetAuthentication.getUser().equals(originator)) {
               if (smaMgr.isCompleted()) {
                  OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                        Arrays.asList(originator),
                        sma.getHumanReadableId(),
                        NotifyType.Completed.name(),
                        "\"" + sma.getArtifactTypeName() + "\" titled \"" + sma.getDescriptiveName() + "\" is Completed"));
               }
               if (smaMgr.isCancelled()) {
                  LogItem cancelledItem = smaMgr.getLog().getStateEvent(LogType.StateCancelled);
                  OseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(
                        Arrays.asList(originator),
                        sma.getHumanReadableId(),
                        NotifyType.Cancelled.name(),
                        String.format(
                              sma.getArtifactTypeName() + " titled \"" + sma.getDescriptiveName() + "\" was cancelled from the \"%s\" state on \"%s\".<br>Reason: \"%s\"<br><br>",
                              cancelledItem.getState(), cancelledItem.getDate(XDate.MMDDYYHHMM), cancelledItem.getMsg())));
               }
            }
         }
      }
   }
}
