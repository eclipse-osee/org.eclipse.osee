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

package org.eclipse.osee.ats.rest.internal.notify.email;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.framework.core.util.MailStatus;

/**
 * Provides a No Operation service that does nothing but keeps from giving an error that there are no implementing
 * services. This will be provided whether there is a mail service or not.
 *
 * @author Donald G. Dunne
 */
public class AtsEmailServiceImplNoOp implements IAtsEmailService {

   @Override
   public void sendNotifications(String fromUserEmail, String testingUserEmail, String subject, String body, Collection<? extends AtsNotificationEvent> notificationEvents) {
      // do nothing
   }

   @Override
   public List<MailStatus> sendNotifications(String fromUserEmail, Collection<String> toUserEmails, String subject, String htmlBody) {
      return Collections.emptyList();
   }
}
