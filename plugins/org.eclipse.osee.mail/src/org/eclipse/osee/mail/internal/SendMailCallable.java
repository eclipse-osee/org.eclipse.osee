/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.mail.internal;

import java.util.Date;
import java.util.concurrent.Callable;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.MimeMessage;
import org.eclipse.osee.mail.api.MailMessage;
import org.eclipse.osee.mail.api.MailStatus;

/**
 * @author Roberto E. Escobar
 */
public class SendMailCallable implements Callable<MailStatus> {

   private final MailConfiguration config;
   private final MailMessage email;
   private final MailMessageFactory factory;

   public SendMailCallable(MailConfiguration config, MailMessageFactory factory, MailMessage email) {
      this.config = config;
      this.factory = factory;
      this.email = email;
   }

   @Override
   public MailStatus call() throws Exception {
      String transportProtocol = config.getTransport();
      String host = config.getHost();
      int port = config.getPort();
      boolean requiresAuthentication = config.isAuthenticationRequired();
      String username = config.getUserName();
      String password = config.getPassword();
      long waitForStatus = config.getStatusWaitTime();

      Date sendDate = new Date();
      Session session = factory.createSession(transportProtocol, host, port, requiresAuthentication);
      MimeMessage message = factory.createMimeMessage(session, email, sendDate);
      Transport transport = factory.createTransport(session, username, password);

      StatusListener statusListener = new StatusListener();
      transport.addTransportListener(statusListener);
      try {
         message.saveChanges();
         transport.sendMessage(message, message.getAllRecipients());
         synchronized (statusListener) {
            statusListener.wait(waitForStatus);
         }
      } finally {
         try {
            transport.removeTransportListener(statusListener);
         } finally {
            try {
               transport.close();
            } catch (MessagingException ex) {
               // Do nothing;
            }
         }
      }
      MailStatus status = statusListener.getMailStatus();
      if (waitForStatus <= 0 || status == null) {
         status = new MailStatus();
         status.setDateSent(message.getSentDate());
         status.setSubject(message.getSubject());
         status.setUuid(message.getMessageID());
         status.setVerified(false);
      }
      return status;
   }

   private class StatusListener implements TransportListener {

      private MailStatus status;

      public MailStatus getMailStatus() {
         return status;
      }

      @Override
      public void messageDelivered(TransportEvent event) {
         handleEvent(event);
      }

      @Override
      public void messageNotDelivered(TransportEvent event) {
         handleEvent(event);
      }

      @Override
      public void messagePartiallyDelivered(TransportEvent event) {
         handleEvent(event);
      }

      private void handleEvent(TransportEvent event) {
         synchronized (this) {
            try {
               status = factory.createMailStatus(event);
            } finally {
               notify();
            }
         }
      }
   }

}
