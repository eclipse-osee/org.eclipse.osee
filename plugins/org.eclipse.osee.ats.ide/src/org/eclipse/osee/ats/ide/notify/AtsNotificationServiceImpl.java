/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.notify;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.core.notify.AbstractAtsNotificationService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationServiceImpl extends AbstractAtsNotificationService {

   public AtsNotificationServiceImpl(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public synchronized XResultData sendNotifications(final AtsNotificationCollector notifications, XResultData rd) {
      return atsApi.getServerEndpoints().getNotifyEndpoint().sendNotifications(notifications);
   }

   @Override
   public void sendNotifications(String fromUserEmail, Collection<String> toUserEmails, String subject,
      String htmlBody) {
      throw new UnsupportedOperationException(
         "IDE notification service does not create or send email directly. Use the server notification endpoint.");
   }

}
