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

package org.eclipse.osee.ats.api.notify;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;

/**
 * @author Donald G. Dunne
 */
public interface IAtsNotificationService {

   void sendNotifications(AtsNotificationCollector notifications);

   void sendNotifications(String fromUserEmail, Collection<String> toUserEmails, String subject, String htmlBody);

   boolean isNotificationsEnabled();

   void setNotificationsEnabled(boolean enabled);

   Collection<AtsUser> getJournalSubscribedUsers(IAtsWorkItem workItem);

   void setJournalSubscribedUsers(IAtsWorkItem workItem, Collection<AtsUser> users);

   void sendNotifications(String fromEmail, String toEmail, String subject, String body,
      Collection<? extends AtsNotificationEvent> notificationEvents);

}
