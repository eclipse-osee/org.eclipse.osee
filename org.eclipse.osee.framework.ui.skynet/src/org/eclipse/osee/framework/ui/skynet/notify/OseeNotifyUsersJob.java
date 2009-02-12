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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.mail.MessagingException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail.BodyType;

/**
 * @author Donald G. Dunne
 */
public class OseeNotifyUsersJob extends Job {
   private final boolean testing = false; // Email goes to current user
   private final Collection<? extends OseeNotificationEvent> notificationEvents;

   public OseeNotifyUsersJob(Collection<? extends OseeNotificationEvent> notificationEvents) throws OseeCoreException {
      super("Notifying Users");
      this.notificationEvents = notificationEvents;
      if (testing) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE,
               "OseeNotifyUsersJob: testing is enabled....turn off for production.");
      }
   }

   @Override
   public IStatus run(IProgressMonitor monitor) {
      try {
         Set<User> users = new HashSet<User>();
         for (OseeNotificationEvent notificationEvent : notificationEvents) {
            users.addAll(notificationEvent.getUsers());
         }
         XResultData resultData = new XResultData();
         if (testing) {
            resultData.log("Testing Results Report for Osee Notification; Email to current user.<br>");
            users.clear();
            users.addAll(Arrays.asList(UserManager.getUser()));
         }
         for (User user : users) {
            List<OseeNotificationEvent> notifyEvents = new ArrayList<OseeNotificationEvent>();
            for (OseeNotificationEvent notificationEvent : notificationEvents) {
               if (testing || notificationEvent.getUsers().contains(user)) {
                  notifyEvents.add(notificationEvent);
               }
            }
            notifyUser(user, notifyEvents, resultData);
         }
         monitor.done();
         return Status.OK_STATUS;
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      }
   }

   private String notificationEventsToHtml(List<OseeNotificationEvent> notificationEvents) {
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Reason", "Description", "Id"}));
      for (OseeNotificationEvent notificationEvent : notificationEvents) {
         sb.append(AHTML.addRowMultiColumnTable(new String[] {notificationEvent.getType(),
               notificationEvent.getDescription(), notificationEvent.getId()}));
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString().replaceAll("\n", "");
   }

   private void notifyUser(User user, List<OseeNotificationEvent> notificationEvents, XResultData resultData) throws MessagingException, OseeCoreException {
      if (user == UserManager.getUser(SystemUser.OseeSystem) || user == UserManager.getUser(SystemUser.UnAssigned) || user == UserManager.getUser(SystemUser.Guest)) {
         // do nothing
         return;
      }
      String html = notificationEventsToHtml(notificationEvents);
      if (user.getEmail() == null || user.getEmail().equals("")) {
         // do nothing
         return;
      } else {
         OseeEmail emailMessage =
               new OseeEmail(Arrays.asList(user.getEmail()), UserManager.getUser().getEmail(),
                     UserManager.getUser().getEmail(), getNotificationEmailSubject(notificationEvents), html,
                     BodyType.Html);
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
