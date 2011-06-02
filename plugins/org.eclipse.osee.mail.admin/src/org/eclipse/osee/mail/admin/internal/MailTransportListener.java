/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.mail.admin.internal;

import java.util.Map;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import org.eclipse.osee.mail.MailConstants;
import org.eclipse.osee.mail.MailEventUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Roberto E. Escobar
 */
public final class MailTransportListener implements TransportListener {

   private final EventAdmin eventAdmin;

   public MailTransportListener(EventAdmin eventAdmin) {
      this.eventAdmin = eventAdmin;
   }

   @Override
   public void messageDelivered(TransportEvent event) {
      Map<String, String> data = MailEventUtil.createTransportEventData(event);
      eventAdmin.postEvent(new Event(MailConstants.MAIL_MESSAGE_DELIVERED, data));
   }

   @Override
   public void messageNotDelivered(TransportEvent event) {
      Map<String, String> data = MailEventUtil.createTransportEventData(event);
      eventAdmin.postEvent(new Event(MailConstants.MAIL_MESSAGE_NOT_DELIVERED, data));
   }

   @Override
   public void messagePartiallyDelivered(TransportEvent event) {
      Map<String, String> data = MailEventUtil.createTransportEventData(event);
      eventAdmin.postEvent(new Event(MailConstants.MAIL_MESSAGE_PARTIALLY_DELIVERED, data));
   }

}