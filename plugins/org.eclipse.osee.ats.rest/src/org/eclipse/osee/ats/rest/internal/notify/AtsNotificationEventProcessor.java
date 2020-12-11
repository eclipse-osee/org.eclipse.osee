/*********************************************************************
 * Copyright (c) 2014 Boeing
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

import java.util.List;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsWorkItemNotificationEvent;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.rest.internal.notify.email.IAtsEmailService;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationEventProcessor {

   private final String noReplyEmail;
   private final WorkItemNotificationProcessor workItemNotificationProcessor;
   private final IAtsUserService userService;

   public AtsNotificationEventProcessor(WorkItemNotificationProcessor workItemNotificationProcessor, IAtsUserService userService, String noReplyEmail) {
      this.workItemNotificationProcessor = workItemNotificationProcessor;
      this.userService = userService;
      this.noReplyEmail = noReplyEmail;
   }

   public void sendNotifications(AtsNotificationCollector notifications, List<IAtsEmailService> notifiers) {

      // convert all WorkItem notifications to AtsNotificationEvent
      for (AtsWorkItemNotificationEvent workItemEvent : notifications.getWorkItemNotificationEvents()) {
         workItemNotificationProcessor.run(notifications, workItemEvent);
      }

      String testingUserEmail = ""; // change to email address for testing purposes; all emails will go there
      String fromUserEmail = getFromUserEmail(notifications);

      for (IAtsEmailService notifier : notifiers) {
         notifier.sendNotifications(fromUserEmail, testingUserEmail, notifications.getSubject(),
            notifications.getBody(), notifications.getNotificationEvents());
      }
   }

   private String getFromUserEmail(AtsNotificationCollector notifications) {
      String email = noReplyEmail;
      for (AtsNotificationEvent event : notifications.getNotificationEvents()) {
         if (Strings.isValid(event.getFromUserId())) {
            AtsUser userById = userService.getUserByUserId(event.getFromUserId());
            if (EmailUtil.isEmailValid(userById.getEmail())) {
               email = userById.getEmail();
               break;
            }
         }
      }
      return email;
   }

}
