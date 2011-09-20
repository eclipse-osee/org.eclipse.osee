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

import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.eclipse.osee.mail.MailConstants;
import org.eclipse.osee.mail.MailMessage;
import org.eclipse.osee.mail.MailServiceConfig;

/**
 * @author Roberto E. Escobar
 */
public class MailMessageFactory {

   private final MailServiceConfig config;

   public MailMessageFactory(MailServiceConfig config) {
      this.config = config;
   }

   public Session createSession() {
      final Properties props = System.getProperties();

      String transport = config.getTransport();
      props.put("mail.transport.protocol", transport);
      props.put("mail." + transport + ".host", config.getHost());
      props.put("mail." + transport + ".port", String.valueOf(config.getPort()));
      props.put("mail." + transport + ".auth", String.valueOf(config.isAuthenticationRequired()).toLowerCase());
      props.put("mail.debug", config.isDebug());

      Session session = Session.getDefaultInstance(props);
      session.setDebug(config.isDebug());
      session.setDebugOut(System.out);
      return session;
   }

   public Transport createTransport(Session session) throws MessagingException {
      Transport transport = session.getTransport();
      if (!transport.isConnected()) {
         if (config.isAuthenticationRequired()) {
            transport.connect(config.getUserName(), config.getPassword());
         } else {
            transport.connect();
         }
      }
      return transport;
   }

   public MimeMessage createMimeMessage(Session session, MailMessage email) throws AddressException, MessagingException {
      MimeMessage message = new MimeMessage(session);
      message.setFrom(toAddress(email.getFrom()));
      message.setSubject(email.getSubject(), "UTF-8");
      message.setReplyTo(toAddress(email.getReplyTo()));
      message.setRecipients(Message.RecipientType.TO, toAddress(email.getRecipients()));
      message.addHeader(MailConstants.MAIL_UUID_HEADER, email.getId());
      message.setSentDate(new Date());

      Multipart multiPart = new MimeMultipart();
      for (DataHandler handler : email.getAttachments()) {
         MimeBodyPart part = new MimeBodyPart();
         part.setDataHandler(handler);
         multiPart.addBodyPart(part);
      }
      message.setContent(multiPart);
      message.saveChanges();
      return message;
   }

   private Address[] toAddress(Collection<String> rawAddresses) throws AddressException {
      InternetAddress[] toReturn = new InternetAddress[rawAddresses.size()];
      int index = 0;
      for (String rawAddress : rawAddresses) {
         toReturn[index++] = toAddress(rawAddress);
      }
      return toReturn;
   }

   private InternetAddress toAddress(String rawAddress) throws AddressException {
      return new InternetAddress(rawAddress);
   }

}
