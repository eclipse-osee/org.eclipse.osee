/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.notify;

import java.util.Collection;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.mail.api.MailService;

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
      SendNotificationEvents job =
         new SendNotificationEvents(logger, mailService, fromUserEmail, testingUserEmail, subject, body,
            notificationEvents, userService);
      job.run();
   }

}
