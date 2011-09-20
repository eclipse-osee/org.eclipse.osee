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
package org.eclipse.osee.mail.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.activation.DataSource;
import javax.mail.event.TransportListener;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.mail.MailConstants;
import org.eclipse.osee.mail.MailMessage;
import org.eclipse.osee.mail.MailService;
import org.eclipse.osee.mail.MailServiceConfig;
import org.eclipse.osee.mail.MailUtils;
import org.eclipse.osee.mail.SendMailStatus;

/**
 * @author Roberto E. Escobar
 */
public class MailServiceImpl implements MailService {

   private MailServiceConfig config;
   private MailMessageFactory factory;
   private EventService eventService;
   private TransportListener[] listeners;

   public void setEventService(EventService eventService) {
      this.eventService = eventService;
   }

   public EventService getEventService() {
      return eventService;
   }

   @Override
   public MailServiceConfig getConfiguration() {
      return config;
   }

   @Override
   public void setConfiguration(MailServiceConfig config) {
      this.config.setTo(config);
   }

   public synchronized void start(Map<String, ?> props) {
      config = new MailServiceConfig();
      factory = new MailMessageFactory(config);
      getEventService().postEvent(MailConstants.REGISTRATION_EVENT, props);

      listeners = new TransportListener[] {new MailTransportListener(eventService)};
   }

   public synchronized void stop(Map<String, ?> props) {
      getEventService().postEvent(MailConstants.DEREGISTRATION_EVENT, props);
      listeners = null;
   }

   @Override
   public List<Callable<SendMailStatus>> createSendCalls(long waitForStatus, TimeUnit timeUnit, MailMessage... emails) {
      List<Callable<SendMailStatus>> callables = new ArrayList<Callable<SendMailStatus>>();
      for (MailMessage mail : emails) {
         callables.add(new SendMailCallable(factory, mail, waitForStatus, timeUnit, listeners));
      }
      return callables;
   }

   @Override
   public MailMessage createSystemTestMessage(int testNumber) {
      MailServiceConfig config = getConfiguration();
      MailMessage message = new MailMessage();
      message.setId(UUID.randomUUID().toString());
      message.setFrom(config.getSystemAdminEmailAddress());
      message.setSubject(String.format("Test email #%s", testNumber));
      message.getRecipients().add(config.getSystemAdminEmailAddress());

      String plainText = String.format("This is test email %s sent from org.eclipse.osee.mail.admin", testNumber);
      String htmlData = String.format("<html><body><h4>%s</h4></body></html>", plainText);
      DataSource source;
      try {
         source = MailUtils.createAlternativeDataSource("TestEmail", htmlData, plainText);
      } catch (Exception ex) {
         source = MailUtils.createFromString("TestEmail", plainText);
      }
      message.addAttachment(source);
      return message;
   }
}
