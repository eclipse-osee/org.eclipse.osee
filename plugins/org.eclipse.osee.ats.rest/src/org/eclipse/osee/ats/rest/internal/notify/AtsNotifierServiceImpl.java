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
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.rest.util.IAtsNotifierServer;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.mail.api.MailMessage;
import org.eclipse.osee.mail.api.MailService;
import org.eclipse.osee.mail.api.MailStatus;

/**
 * @author Donald G. Dunne
 */
public class AtsNotifierServiceImpl implements IAtsNotifierServer {

   private Log logger;
   private MailService mailService;
   private IAtsUserService userService;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setMailService(MailService mailService) {
      this.mailService = mailService;
   }

   public void setUserService(IAtsUserService userService) {
      this.userService = userService;
   }

   /**
    * Send notifications
    */
   @Override
   public void sendNotifications(String fromUserEmail, String testingUserEmail, String subject, String body, Collection<? extends AtsNotificationEvent> notificationEvents) {
      SendNotificationEvents job = new SendNotificationEvents(logger, mailService, fromUserEmail, testingUserEmail,
         subject, body, notificationEvents, userService);
      job.run();
   }

   @Override
   public List<MailStatus> sendNotifications(String fromUserEmail, Collection<String> toUserEmails, String subject, String htmlBody) {
      MailMessage msg = MailMessage.newBuilder() //
         .from(fromUserEmail) //
         .recipients(toUserEmails) //
         .subject(subject) //
         .addHtml(htmlBody)//
         .build();
      List<MailStatus> status = mailService.sendMessages(msg);
      return status;
   }

}
