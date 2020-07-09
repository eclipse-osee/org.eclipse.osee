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

package org.eclipse.osee.ats.rest.util;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.mail.api.MailStatus;

/**
 * @author Donald G. Dunne
 */
public interface IAtsNotifierServer {

   /**
    * Send notifications
    */
   public void sendNotifications(String fromUserEmail, String testingUserEmail, String subject, String body, Collection<? extends AtsNotificationEvent> notificationEvents);

   public List<MailStatus> sendNotifications(String fromUserEmail, Collection<String> toUserEmails, String subject, String htmlBody);

}
