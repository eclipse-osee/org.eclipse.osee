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
package org.eclipse.osee.framework.ui.skynet.notify;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.AEmail;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;

/**
 * @author Donald G. Dunne
 */
public class OseeNotifyUsersJob extends Job {
   private boolean testing = false;
   private final Collection<? extends OseeNotificationEvent> notificationEvents;

   public OseeNotifyUsersJob(Collection<? extends OseeNotificationEvent> notificationEvents) throws OseeCoreException, SQLException {
      super("Notifying Users");
      this.notificationEvents = notificationEvents;
   }

   public IStatus run(IProgressMonitor monitor) {
      try {
         Set<User> users = new HashSet<User>();
         for (OseeNotificationEvent notificationEvent : notificationEvents) {
            users.addAll(notificationEvent.getUsers());
         }
         XResultData resultData = new XResultData(SkynetGuiPlugin.getLogger());
         if (testing) resultData.log("Testing Results Report for Osee Notification; Email Disabled<br>");
         for (User user : users) {
            List<OseeNotificationEvent> notifyEvents = new ArrayList<OseeNotificationEvent>();
            for (OseeNotificationEvent notificationEvent : notificationEvents) {
               if (notificationEvent.getUsers().contains(user)) {
                  notifyEvents.add(notificationEvent);
               }
            }
            notifyUser(user, notifyEvents, resultData);
         }
         if (testing) resultData.report("Notify Users", Manipulations.NO_POPUP);
         monitor.done();
         return Status.OK_STATUS;
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      }
   }

   private String notificationEventsToHtml(List<OseeNotificationEvent> notificationEvents) {
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Reason", "Description", "ID"}));
      for (OseeNotificationEvent notificationEvent : notificationEvents) {
         sb.append(AHTML.addRowMultiColumnTable(new String[] {notificationEvent.getType(),
               notificationEvent.getDescription(), notificationEvent.getId()}));
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString().replaceAll("\n", "");
   }

   private void notifyUser(User user, List<OseeNotificationEvent> notificationEvents, XResultData resultData) throws MessagingException, OseeCoreException, SQLException {
      if (user == SkynetAuthentication.getUser(UserEnum.NoOne) || user == SkynetAuthentication.getUser(UserEnum.UnAssigned) || user == SkynetAuthentication.getUser(UserEnum.Guest)) {
         // do nothing
         return;
      }
      String html = notificationEventsToHtml(notificationEvents);
      if (testing) {
         resultData.log("To: " + user.getName() + " at " + user.getEmail());
         resultData.addRaw(html + AHTML.newline(2));
      } else if (user.getEmail() == null || user.getEmail().equals("")) {
         // do nothing
         return;
      } else {
         AEmail emailMessage =
               new AEmail(null, SkynetAuthentication.getUser().getEmail(), SkynetAuthentication.getUser().getEmail(),
                     getNotificationEmailSubject(notificationEvents));
         emailMessage.setRecipients(Message.RecipientType.TO, new String[] {user.getEmail()});
         emailMessage.addHTMLBody(html);
         emailMessage.send();
      }
   }

   private String getNotificationEmailSubject(List<OseeNotificationEvent> notificationEvents) {
      if (notificationEvents.size() == 1) {
         OseeNotificationEvent event = notificationEvents.iterator().next();
         return Strings.truncate("OSEE Notification" + " - " + event.getType() + " - " + event.getDescription(), 128);
      }
      return "OSEE Notification";
   }
}
