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
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import javax.mail.event.TransportListener;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.mail.MailConfiguration;
import org.eclipse.osee.mail.MailMessage;
import org.eclipse.osee.mail.MailService;
import org.eclipse.osee.mail.SendMailStatus;

/**
 * @author Roberto E. Escobar
 */
public class MailServiceImpl implements MailService {

   private EventService eventService;
   private Log logger;

   private MailMessageFactory factory;
   private TransportListener[] listeners;
   private volatile MailConfiguration config;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setEventService(EventService eventService) {
      this.eventService = eventService;
   }

   public void start(Map<String, Object> props) {
      logger.trace("Starting [%s]...", getClass().getSimpleName());
      update(props);
      AtomicInteger testCounter = new AtomicInteger();
      factory = new MailMessageFactory(logger, testCounter);
      listeners = new TransportListener[] {new MailTransportListener(eventService)};
   }

   public void stop(Map<String, Object> props) {
      logger.trace("Stopping [%s]...", getClass().getSimpleName());
      listeners = null;
      factory = null;
      config = null;
   }

   public void update(Map<String, Object> props) {
      logger.trace("Configuring [%s]...", getClass().getSimpleName());
      config = MailConfiguration.newConfig(props);
   }

   @Override
   public List<Callable<SendMailStatus>> createSendCalls(MailMessage... emails) {
      final MailConfiguration configCopy = config.copy();
      long waitForStatus = configCopy.getStatusWaitTime();
      List<Callable<SendMailStatus>> callables = new ArrayList<Callable<SendMailStatus>>();
      for (MailMessage mail : emails) {
         callables.add(new SendMailCallable(configCopy, factory, mail, waitForStatus, listeners));
      }
      return callables;
   }

   @Override
   public MailMessage createSystemTestMessage() {
      final MailConfiguration configCopy = config.copy();
      String adminEmail = configCopy.getAdminEmail();
      String subject = configCopy.getTestEmailSubject();
      String body = configCopy.getTestEmailBody();
      return factory.createTestMessage(adminEmail, subject, body);
   }
}
