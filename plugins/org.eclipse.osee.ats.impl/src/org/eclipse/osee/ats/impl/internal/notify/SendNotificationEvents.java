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
package org.eclipse.osee.ats.impl.internal.notify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.core.users.AtsUsersUtility;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.mail.api.MailMessage;
import org.eclipse.osee.mail.api.MailService;
import org.eclipse.osee.mail.api.MailStatus;

/**
 * @author Donald G. Dunne
 */
public class SendNotificationEvents {
   private final Collection<? extends AtsNotificationEvent> notificationEvents;
   private final String subject;
   private final String body;
   private final MailService mailService;
   private final String fromUserEmail;
   private final String testingUserEmail;
   private final IAtsUserService userService;

   protected SendNotificationEvents(MailService mailService, String fromUserEmail, String testingUserEmail, String subject, String body, Collection<? extends AtsNotificationEvent> notificationEvents, IAtsUserService userService) {
      this.mailService = mailService;
      this.fromUserEmail = fromUserEmail;
      this.testingUserEmail = testingUserEmail;
      this.subject = subject;
      this.body = body;
      this.notificationEvents = notificationEvents;
      this.userService = userService;
      if (isTesting()) {
         OseeLog.log(SendNotificationEvents.class, Level.SEVERE,
            "OseeNotifyUsersJob: testing is enabled....turn off for production.");
      }
   }

   public Result run() {
      try {
         Set<IAtsUser> uniqueUusers = new HashSet<IAtsUser>();
         for (AtsNotificationEvent notificationEvent : notificationEvents) {
            uniqueUusers.addAll(AtsUsersUtility.getUsers(notificationEvent.getUserIds(), userService));
         }
         XResultData resultData = new XResultData();
         if (isTesting()) {
            resultData.logErrorWithFormat("Testing Results Report for Osee Notification; Email to user [%s].<br>",
               testingUserEmail);
         }
         for (IAtsUser user : AtsUsersUtility.getValidEmailUsers(uniqueUusers)) {
            List<AtsNotificationEvent> notifyEvents = new ArrayList<AtsNotificationEvent>();
            for (AtsNotificationEvent notificationEvent : notificationEvents) {
               if (isTesting() || AtsUsersUtility.getUsers(notificationEvent.getUserIds(), userService).contains(user)) {
                  notifyEvents.add(notificationEvent);
               }
            }
            notifyUser(user, notifyEvents, resultData);
         }
         return Result.TrueResult;
      } catch (Exception ex) {
         OseeLog.log(SendNotificationEvents.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result("Error notifying users [%s]", ex.getMessage());
      }
   }

   private String notificationEventsToHtml(List<AtsNotificationEvent> notificationEvents) {
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Reason", "Description", "Id", "URL"}));
      for (AtsNotificationEvent notificationEvent : notificationEvents) {
         sb.append(AHTML.addRowMultiColumnTable(new String[] {
            notificationEvent.getType(),
            notificationEvent.getDescription(),
            notificationEvent.getId(),
            getHyperlink(notificationEvent)}));
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString().replaceAll("\n", "");
   }

   private String getHyperlink(AtsNotificationEvent notificationEvent) {
      return Strings.isValid(notificationEvent.getUrl()) ? AHTML.getHyperlink(notificationEvent.getUrl(), "More Info") : "";
   }

   private void notifyUser(IAtsUser user, List<AtsNotificationEvent> notificationEvents, XResultData resultData) throws OseeCoreException {
      if (user.getUserId().equals(SystemUser.OseeSystem.getUserId()) || user.getUserId().equals(
         SystemUser.UnAssigned.getUserId()) || user.getUserId().equals(SystemUser.Guest.getUserId())) {
         // do nothing
         return;
      }
      if (!AtsUsersUtility.isEmailValid(user.getEmail())) {
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

         String useEmail = isTesting() ? testingUserEmail : email;

         MailMessage msg = MailMessage.newBuilder() //
         .from(fromUserEmail) //
         .recipients(Arrays.asList(useEmail)) //
         .subject(getNotificationEmailSubject(notificationEvents)) //
         .addHtml(html)//
         .build();

         List<MailStatus> sendMessages = mailService.sendMessages(msg);
         System.out.println(sendMessages);
      }
   }

   private boolean isTesting() {
      return Strings.isValid(testingUserEmail);
   }

   private String getNotificationEmailSubject(List<AtsNotificationEvent> notificationEvents) {
      String result = subject;
      if (!Strings.isValid(result)) {
         if (notificationEvents.size() == 1) {
            AtsNotificationEvent event = notificationEvents.iterator().next();
            result =
               Strings.truncate("OSEE Notification" + " - " + event.getType() + " - " + event.getDescription(), 128);
         } else {
            result = "OSEE Notification";
         }
      }
      return result;
   }
}
