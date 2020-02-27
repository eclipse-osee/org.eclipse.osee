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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.core.notify.AbstractAtsNotificationService;
import org.eclipse.osee.ats.rest.internal.notify.email.IAtsEmailService;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationServiceImpl extends AbstractAtsNotificationService {

   private AtsNotificationEventProcessor notificationEventProcessor;
   private boolean loggedNotificationDisabled = false;
   private static final List<IAtsEmailService> notifiers = new CopyOnWriteArrayList<>();

   public AtsNotificationServiceImpl() {
      // for jax-rs
   }

   public AtsNotificationServiceImpl(AtsApi atsApi) {
      super(atsApi);
   }

   public void addMailService(IAtsEmailService notifier) {
      notifiers.add(notifier);
   }

   @Override
   public void sendNotifications(String fromUserEmail, Collection<String> toUserEmails, String subject, String body) {
      if (isNotificationsEnabled()) {
         Thread thread = new Thread("ATS Emailer") {

            @Override
            public void run() {
               for (IAtsEmailService emailService : notifiers) {
                  emailService.sendNotifications(fromUserEmail, toUserEmails, subject, body);
               }
            }

         };
         thread.start();
      }
   }

   @Override
   public void sendNotifications(AtsNotificationCollector notifications) {
      if (isNotificationsEnabled()) {
         if (notifiers.isEmpty() || !atsApi.getStoreService().isProductionDb()) {
            if (!loggedNotificationDisabled) {
               atsApi.getLogger().info("Osee Notification Disabled");
               loggedNotificationDisabled = true;
            }
         } else {
            if (notifications.isIncludeCancelHyperlink() && !atsApi.getWorkItemService().isCancelHyperlinkConfigured()) {
               throw new OseeArgumentException("Cancel Hyperlink URl not configured");
            }
            WorkItemNotificationProcessor workItemNotificationProcessor = new WorkItemNotificationProcessor(
               atsApi.getLogger(), atsApi, atsApi.getUserService(), atsApi.getAttributeResolver());
            Thread thread = new Thread("ATS Notification Sender") {

               @Override
               public void run() {
                  super.run();
                  notificationEventProcessor = new AtsNotificationEventProcessor(workItemNotificationProcessor,
                     atsApi.getUserService(), atsApi.getConfigValue("NoReplyEmail"));
                  notificationEventProcessor.sendNotifications(notifications, notifiers);
               }

            };
            thread.start();
         }
      }

   }

}
