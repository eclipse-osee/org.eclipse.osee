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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import org.eclipse.osee.mail.MailConstants;
import org.eclipse.osee.mail.MailMessage;
import org.eclipse.osee.mail.MailService;
import org.eclipse.osee.mail.MailServiceConfig;
import org.eclipse.osee.mail.MailUtils;
import org.eclipse.osee.mail.SendMailOperation;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Roberto E. Escobar
 */
public class MailServiceImpl implements MailService {
   public static final String PLUGIN_ID = "org.eclipse.osee.mail.admin";

   private MailServiceConfig config;
   private MailMessageFactory factory;
   private EventAdmin eventAdmin;

   public void setEventAdmin(EventAdmin eventAdmin) {
      this.eventAdmin = eventAdmin;
   }

   @Override
   public MailServiceConfig getConfiguration() {
      return config;
   }

   @Override
   public void setConfiguration(MailServiceConfig config) {
      this.config.setTo(config);
   }

   public synchronized void start(Map<String, String> props) {
      config = new MailServiceConfig();
      factory = new MailMessageFactory(config);
      eventAdmin.postEvent(new Event(MailConstants.REGISTRATION_EVENT, props));
   }

   public synchronized void stop(Map<String, String> props) {
      eventAdmin.postEvent(new Event(MailConstants.DEREGISTRATION_EVENT, props));
   }

   @Override
   public List<SendMailOperation> createSendOp(MailMessage... emails) {
      return createOperation(factory, eventAdmin, emails);
   }

   private static List<SendMailOperation> createOperation(MailMessageFactory factory, EventAdmin eventAdmin, MailMessage... emails) {
      List<SendMailOperation> ops = new ArrayList<SendMailOperation>();
      int size = emails.length;
      if (size != 0) {
         if (size == 1) {
            MailMessage mail = emails[0];
            String opName = String.format("Send [%s]", mail.getSubject());
            ops.add(createMailSendOp(factory, eventAdmin, opName, mail));
         } else {
            int count = 0;
            for (MailMessage mail : emails) {
               String opName = String.format("Send [%s of %s] [%s]", ++count, size, mail.getSubject());
               ops.add(createMailSendOp(factory, eventAdmin, opName, mail));
            }
         }
      }
      return ops;
   }

   private static MailSendOperation createMailSendOp(MailMessageFactory handler, EventAdmin eventAdmin, String opName, MailMessage mail) {
      MailSendOperation operation = new MailSendOperation(opName, PLUGIN_ID, handler, mail);
      operation.addListener(new MailTransportListener(eventAdmin));
      return operation;
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
         source = MailUtils.createAlternativeDataSource("TestEmail", plainText, htmlData);
      } catch (MessagingException ex) {
         source = MailUtils.createFromString("TestEmail", plainText);
      }
      message.addAttachment(source);
      return message;
   }
}
