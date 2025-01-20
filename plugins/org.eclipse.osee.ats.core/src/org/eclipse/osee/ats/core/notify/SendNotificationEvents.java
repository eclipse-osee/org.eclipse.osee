/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.notify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsConfigKey;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.core.users.AtsUsersUtility;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class SendNotificationEvents {
   private final Collection<? extends AtsNotificationEvent> notificationEvents;
   private final String subject;
   private final String body;
   private final String fromUserEmail;
   private final String testingUserEmail;
   private final OseeEmailCreator oseeEmailCreator;
   private final AtsApi atsApi;
   private final XResultData rd;

   public SendNotificationEvents(OseeEmailCreator oseeEmailCreator, AtsApi atsApi, String fromUserEmail, String //
   testingUserEmail, String subject, String body, Collection<? extends AtsNotificationEvent> notificationEvents, //
      IAtsUserService userService, XResultData rd) {
      this.oseeEmailCreator = oseeEmailCreator;
      this.atsApi = atsApi;
      this.rd = rd;
      this.fromUserEmail = fromUserEmail;
      this.testingUserEmail = testingUserEmail;
      this.subject = subject;
      this.body = body;
      this.notificationEvents = notificationEvents;
      if (isTesting()) {
         //         logger.error("OseeNotifyUsersJob: testing is enabled....turn off for production.");
      }
   }

   public XResultData run() {
      try {
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
               notifyUser(email, notifyEvents, rd);
            }
         }
      } catch (Exception ex) {
         rd.errorf("Error notifying users [%s]", ex.getMessage());
      }
      return rd;
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

   private void notifyUser(String email, List<AtsNotificationEvent> notificationEvents, XResultData rd) {
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
         String useFromEmail =
            Strings.isValid(fromUserEmail) ? fromUserEmail : atsApi.getConfigValue(AtsConfigKey.NoReplyEmail, "");
         if (!EmailUtil.isEmailValid(useFromEmail)) {
            return;
         }

         try {
            OseeEmail oseeEmail = oseeEmailCreator.createOseeEmail();
            oseeEmail.setFrom(useFromEmail);
            oseeEmail.setSubject(getNotificationEmailSubject(notificationEvents));
            oseeEmail.setHTMLBody(html);
            oseeEmail.setRecipients(useEmail);
            oseeEmail.send();
         } catch (Exception ex) {
            System.err.println(Lib.exceptionToString(ex));
         }

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
