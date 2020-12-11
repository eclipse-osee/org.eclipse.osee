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

/**
 * @author Donald G. Dunne
 */
public interface IAtsNotificationService {

   void sendNotifications(AtsNotificationCollector notifications);

   void sendNotifications(String fromUserEmail, Collection<String> toUserEmails, String subject, String body);

   boolean isNotificationsEnabled();

   void setNotificationsEnabled(boolean enabled);

}
