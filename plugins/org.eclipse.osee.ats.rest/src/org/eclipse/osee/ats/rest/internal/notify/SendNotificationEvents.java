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
package org.eclipse.osee.ats.rest.internal.notify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.core.users.AtsUsersUtility;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.mail.api.MailMessage;
import org.eclipse.osee.mail.api.MailService;
import org.eclipse.osee.mail.api.MailStatus;

/**
 * @author Donald G. Dunne
 */
public class SendNotificationEvents {
   private final Log logger;
   private final Collection<? extends AtsNotificationEvent> notificationEvents;
   private final String subject;
   private final String body;
   private final MailService mailService;
   private final String fromUserEmail;
   private final String testingUserEmail;
   private final IAtsUserService userService;

   protected SendNotificationEvents(Log logger, MailService mailService, String fromUserEmail, String testingUserEmail, String subject, String body, Collection<? extends AtsNotificationEvent> notificationEvents, IAtsUserService userService) {
      this.logger = logger;
      this.mailService = mailService;
      this.fromUserEmail = fromUserEmail;
      this.testingUserEmail = testingUserEmail;
      this.subject = subject;
      this.body = body;
      this.notificationEvents = notificationEvents;
      this.userService = userService;
      if (isTesting()) {
         logger.error("OseeNotifyUsersJob: testing is enabled....turn off for production.");
      }
   }

   public Result run() {
      try {
         Set<IAtsUser> uniqueUusers = new HashSet<>();
         for (AtsNotificationEvent notificationEvent : notificationEvents) {
            uniqueUusers.addAll(AtsUsersUtility.getUsers(notificationEvent.getUserIds(), userService));
         }
         XResultData resultData = new XResultData();
         if (isTesting()) {
            resultData.errorf("Testing Results Report for Osee Notification; Email to user [%s].<br>",
               testingUserEmail);
         }

         // Notify specified OSEE users; one email for all events that user was specified for
         for (IAtsUser user : AtsUsersUtility.getValidEmailUsers(uniqueUusers)) {
            List<AtsNotificationEvent> notifyEvents = new ArrayList<>();
            for (AtsNotificationEvent notificationEvent : notificationEvents) {
               if (isTesting() || AtsUsersUtility.getUsers(notificationEvent.getUserIds(), userService).contains(
                  user)) {
                  notifyEvents.add(notificationEvent);
               }
            }
            notifyUser(user, notifyEvents, resultData);
         }

         // Notify email address; one email for all events that email was specified for
         Set<String> uniqueEmailAddresses = getUniqueEmailAddresses(notificationEvents);
         if (!uniqueEmailAddresses.isEmpty()) {
            for (String email : uniqueEmailAddresses) {
               List<AtsNotificationEvent> notifyEvents = new ArrayList<>();
               for (AtsNotificationEvent notificationEvent : notificationEvents) {
                  if (notificationEvent.getEmailAddresses().contains(email)) {
                     notifyEvents.add(notificationEvent);
                  }
               }
               notifyUser(email, notifyEvents);
            }
         }
         return Result.TrueResult;
      } catch (Exception ex) {
         logger.error(ex, "Error notifying users");
         return new Result("Error notifying users [%s]", ex.getMessage());
      }
   }

   private Set<String> getUniqueEmailAddresses(Collection<? extends AtsNotificationEvent> notificationEvents) {
      Set<String> uniqueEmails = new HashSet<>();
      for (AtsNotificationEvent notificationEvent : notificationEvents) {
         uniqueEmails.addAll(notificationEvent.getEmailAddresses());
      }
      return uniqueEmails;
   }

   private String notificationEventsToHtml(List<AtsNotificationEvent> notificationEvents) {
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      boolean anyCancelable = isAnyCancelable(notificationEvents);
      if (anyCancelable) {
         sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Reason", "Description", "Id", "Cancel"}));
      } else {
         sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Reason", "Description", "Id"}));
      }
      for (AtsNotificationEvent notificationEvent : notificationEvents) {
         if (anyCancelable) {
            sb.append(AHTML.addRowMultiColumnTable(new String[] {
               notificationEvent.getType(),
               notificationEvent.getDescription(),
               getHyperlink(notificationEvent),
               getCancelHyperlink(notificationEvent)}));
         } else {
            sb.append(AHTML.addRowMultiColumnTable(new String[] {
               notificationEvent.getType(),
               notificationEvent.getDescription(),
               getHyperlink(notificationEvent)}));
         }
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString().replaceAll("\n", "");
   }

   private boolean isAnyCancelable(List<AtsNotificationEvent> notificationEvents) {
      for (AtsNotificationEvent notificationEvent : notificationEvents) {
         if (Strings.isValid(notificationEvent.getCancelUrl())) {
            return true;
         }
      }
      return false;
   }

   private String getHyperlink(AtsNotificationEvent notificationEvent) {
      return Strings.isValid(notificationEvent.getUrl()) ? AHTML.getHyperlink(notificationEvent.getUrl(),
         notificationEvent.getId()) : "";
   }

   private String getCancelHyperlink(AtsNotificationEvent notificationEvent) {
      return Strings.isValid(notificationEvent.getCancelUrl()) ? AHTML.getHyperlink(notificationEvent.getCancelUrl(),
         "Cancel") : "";
   }

   private void notifyUser(IAtsUser user, List<AtsNotificationEvent> notificationEvents, XResultData resultData) {
      if (AtsCoreUsers.isAtsCoreUser(user)) {
         // do nothing
         return;
      }
      String email = user.getEmail();
      notifyUser(email, notificationEvents);
   }

   private void notifyUser(String email, List<AtsNotificationEvent> notificationEvents) {
      if (!AtsUsersUtility.isEmailValid(email)) {
         // do nothing; can't send email from user with invalid email address
         return;
      }
      String html = "";
      if (Strings.isValid(body)) {
         html += "<pre>" + body + "</pre>";
      }
      html += notificationEventsToHtml(notificationEvents);
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
