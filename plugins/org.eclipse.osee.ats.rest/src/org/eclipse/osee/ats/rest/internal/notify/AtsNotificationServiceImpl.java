/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.rest.internal.notify;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsWorkItemNotificationEvent;
import org.eclipse.osee.ats.core.notify.AbstractAtsNotificationService;
import org.eclipse.osee.ats.core.notify.OseeEmailCreator;
import org.eclipse.osee.ats.core.notify.SendNotificationEvents;
import org.eclipse.osee.ats.core.notify.WorkItemNotificationProcessor;
import org.eclipse.osee.framework.core.util.IOseeEmail;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationServiceImpl extends AbstractAtsNotificationService {

   private final OrcsApi orcsApi;
   private WorkItemNotificationProcessor workItemNotificationProcessor;
   private boolean loggedNotificationDisabled = false;

   public AtsNotificationServiceImpl(AtsApi atsApi, OrcsApi orcsApi) {
      super(atsApi);
      this.orcsApi = orcsApi;
   }

   @Override
   public synchronized XResultData sendNotifications(final AtsNotificationCollector notifications, XResultData rd) {

      if (isNotificationsEnabled()) {
         workItemNotificationProcessor = new WorkItemNotificationProcessor(rd);

         for (AtsWorkItemNotificationEvent workItemEvent : notifications.getWorkItemNotificationEvents()) {
            workItemNotificationProcessor.run(notifications, workItemEvent);
         }

         Thread send = new Thread("Send Notifications") {

            @Override
            public void run() {
               if (isNotificationsEnabled()) {
                  if (!atsApi.getStoreService().isProductionDb()) {
                     if (!loggedNotificationDisabled) {
                        atsApi.getLogger().info("Osee Notification Disabled");
                        loggedNotificationDisabled = true;
                     }
                  } else {
                     if (notifications.isIncludeCancelHyperlink() && !atsApi.getWorkItemService().isCancelHyperlinkConfigured()) {
                        throw new OseeArgumentException("Cancel Hyperlink URl not configured");
                     }
                     Thread thread = new Thread("ATS Notification Sender") {

                        @Override
                        public void run() {
                           super.run();
                           String testingUserEmail = "";
                           String fromUserEmail = getFromUserEmail(notifications);

                           sendNotificationEvents(fromUserEmail, testingUserEmail, notifications.getSubject(),
                              notifications.getBody(), notifications.getNotificationEvents(), new XResultData());
                        }

                     };
                     thread.start();
                  }
               }
            }

         };
         send.start();
      }
      return rd;
   }

   @Override
   public void sendNotifications(String fromUserEmail, Collection<String> toUserEmails, String subject,
      String htmlBody) {
      if (isNotificationsEnabled()) {
         Thread thread = new Thread("ATS Emailer") {

            @Override
            public void run() {
               try {
                  IOseeEmail msg = orcsApi.getEmailService().create();
                  msg.setFrom(fromUserEmail);
                  msg.setRecipients(toUserEmails.toArray(new String[toUserEmails.size()]));
                  msg.setSubject(subject);
                  msg.setHTMLBody(htmlBody);
                  msg.send();
               } catch (Exception ex) {
                  System.err.println(Lib.exceptionToString(ex));
               }
            }

         };
         thread.start();
      }
   }

   private void sendNotificationEvents(String fromUserEmail, String testingUserEmail, String subject, String body,
      Collection<? extends AtsNotificationEvent> notificationEvents, XResultData rd) {
      SendNotificationEvents job = new SendNotificationEvents(new OseeEmailCreator() {
         @Override
         public IOseeEmail createOseeEmail() {
            return orcsApi.getEmailService().create();
         }
      }, atsApi, fromUserEmail, testingUserEmail, subject, body, notificationEvents, atsApi.getUserService(), rd);
      job.run();
   }

   private String getFromUserEmail(AtsNotificationCollector notifications) {
      String email = atsApi.getConfigValue("NoReplyEmail");
      for (AtsNotificationEvent event : notifications.getNotificationEvents()) {
         if (EmailUtil.isEmailValid(event.getFromEmailAddress())) {
            email = event.getFromEmailAddress();
            break;
         }
      }
      return email;
   }

}
