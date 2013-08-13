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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;
import org.eclipse.osee.framework.skynet.core.utility.OseeNotificationEvent;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail.BodyType;

/**
 * @author Donald G. Dunne
 */
public class OseeNotifyUsersJob extends Job {
   private final boolean testing = false; // Email goes to current user
   private final Collection<? extends OseeNotificationEvent> notificationEvents;
   private final String subject;
   private final String body;

   public OseeNotifyUsersJob(String subject, String body, Collection<? extends OseeNotificationEvent> notificationEvents) {
      super("Notifying Users");
      this.subject = subject;
      this.body = body;
      this.notificationEvents = notificationEvents;
   }

   public OseeNotifyUsersJob(Collection<? extends OseeNotificationEvent> notificationEvents) {
      this(null, null, notificationEvents);
      if (testing) {
         OseeLog.log(Activator.class, Level.SEVERE,
            "OseeNotifyUsersJob: testing is enabled....turn off for production.");
      }
   }

   @Override
   public IStatus run(IProgressMonitor monitor) {
      try {
         Set<User> uniqueUusers = new HashSet<User>();
         for (OseeNotificationEvent notificationEvent : notificationEvents) {
            uniqueUusers.addAll(notificationEvent.getUsers());
         }
         XResultData resultData = new XResultData();
         if (testing) {
            resultData.log("Testing Results Report for Osee Notification; Email to current user.<br>");
            uniqueUusers.clear();
            uniqueUusers.addAll(Arrays.asList(UserManager.getUser()));
         }
         for (User user : EmailUtil.getValidEmailUsers(uniqueUusers)) {
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
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
      }
   }

   private String notificationEventsToHtml(List<OseeNotificationEvent> notificationEvents) {
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Reason", "Description", "Id", "URL"}));
      for (OseeNotificationEvent notificationEvent : notificationEvents) {
         sb.append(AHTML.addRowMultiColumnTable(new String[] {
            notificationEvent.getType(),
            notificationEvent.getDescription(),
            notificationEvent.getId(),
            getHyperlink(notificationEvent)}));
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString().replaceAll("\n", "");
   }

   public static String getHyperlink(OseeNotificationEvent notificationEvent) {
      return Strings.isValid(notificationEvent.getUrl()) ? AHTML.getHyperlink(notificationEvent.getUrl(), "More Info") : "";
   }

   private void notifyUser(User user, List<OseeNotificationEvent> notificationEvents, XResultData resultData) throws OseeCoreException {
      if (user == UserManager.getUser(SystemUser.OseeSystem) || user == UserManager.getUser(SystemUser.UnAssigned) || user == UserManager.getUser(SystemUser.Guest)) {
         // do nothing
         return;
      }
      if (!EmailUtil.isEmailValid(UserManager.getUser())) {
         // do nothing; can't send email from user with invalid email address
         return;
      }
      String html = "";
      if (Strings.isValid(body)) {
         html += "<pre>" + body + "</pre>";
      }
      html += notificationEventsToHtml(notificationEvents);
      String email = user.getEmail();
      if (!Strings.isValid(email)) {
         // do nothing
         return;
      } else {
         OseeEmail emailMessage =
            new OseeEmail(Arrays.asList(email), UserManager.getUser().getEmail(), UserManager.getUser().getEmail(),
               getNotificationEmailSubject(notificationEvents), html, BodyType.Html);
         emailMessage.send();
      }
   }

   private String getNotificationEmailSubject(List<OseeNotificationEvent> notificationEvents) {
      String result = subject;
      if (!Strings.isValid(result)) {
         if (notificationEvents.size() == 1) {
            OseeNotificationEvent event = notificationEvents.iterator().next();
            result =
               Strings.truncate("OSEE Notification" + " - " + event.getType() + " - " + event.getDescription(), 128);
         } else {
            result = "OSEE Notification";
         }
      }
      return result;
   }
}
