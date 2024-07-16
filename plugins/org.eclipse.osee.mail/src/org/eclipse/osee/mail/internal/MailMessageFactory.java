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

import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.eclipse.osee.framework.core.util.MailStatus;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.mail.api.MailMessage;

/**
 * @author Roberto E. Escobar
 */
public class MailMessageFactory {

   public static final String MAIL_UUID_HEADER = "OSEE-MAIL-UUID";

   private final Log logger;
   private final AtomicInteger testCounter;

   public MailMessageFactory(Log logger, AtomicInteger testCounter) {
      this.logger = logger;
      this.testCounter = testCounter;
   }

   public Session createSession(String transport, String host, int port, boolean requiresAuthentication) {
      final Properties props = new Properties();

      props.put("mail.transport.protocol", transport);
      props.put("mail." + transport + ".host", host);
      props.put("mail." + transport + ".port", String.valueOf(port));
      props.put("mail." + transport + ".auth", String.valueOf(requiresAuthentication).toLowerCase());

      boolean isDebugEnabled = logger != null && logger.isDebugEnabled();
      props.put("mail.debug", isDebugEnabled);

      Session session = Session.getDefaultInstance(props);
      session.setDebug(isDebugEnabled);
      if (isDebugEnabled) {
         session.setDebugOut(new PrintStream(new ForwardingStream() {
            @Override
            protected void forward(String data) {
               logger.debug(data);
            }
         }));
      }
      return session;
   }

   public Transport createTransport(Session session, String username, String password) throws MessagingException {
      Transport transport = session.getTransport();
      if (!transport.isConnected()) {
         if (Strings.isValid(username) && Strings.isValid(password)) {
            transport.connect(username, password);
         } else {
            transport.connect();
         }
      }
      return transport;
   }

   public MimeMessage createMimeMessage(Session session, MailMessage email, Date sendDate) throws AddressException, MessagingException {
      System.err.println(String.format("MMF session [%s]", session));
      MimeMessage message = new MimeMessage(session);
      String from = email.getFrom();
      System.err.println(String.format("MMF getFrom [%s]", from));
      Address addr = toAddress(from);
      System.err.println(String.format("MMF addr [%s]", addr));
      message.setFrom(addr);
      System.err.println(String.format("MMF subject [%s]", email.getSubject()));
      message.setSubject(email.getSubject(), "UTF-8");
      Collection<String> replyTo = email.getReplyTo();
      System.err.println(String.format("MMF replyTo [%s]", replyTo));
      Address[] replyAddrs = toAddress(replyTo);
      for (Address replyAddr : replyAddrs) {
         System.err.println(String.format("MMF replyAddr  [%s]", replyAddr));
      }
      if (replyAddrs.length > 0) {
         message.setReplyTo(replyAddrs);
      }
      System.err.println(String.format("MMF recipients [%s]", email.getRecipients()));
      message.setRecipients(Message.RecipientType.TO, toAddress(email.getRecipients()));
      System.err.println(String.format("MMF uuid [%s]", email.getId()));
      message.addHeader(MAIL_UUID_HEADER, email.getId());
      System.err.println(String.format("MMF setSentDate"));
      message.setSentDate(sendDate);

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

   public MailMessage createTestMessage(String adminEmail, String subject, String text) {
      String subjectWithCounter = String.format("%s #%s", subject, testCounter.incrementAndGet());
      String html = String.format("<html><body><big>%s</big><p>%s</p></body></html>", subjectWithCounter, text);

      MailMessage message = MailMessage.newBuilder() //
         .from(adminEmail) //
         .recipient(adminEmail) //
         .replyTo(adminEmail) //
         .subject(subjectWithCounter) //
         .addHtml(html) //
         .build();
      return message;
   }

   public MailStatus createMailStatus(TransportEvent event) {
      MailStatus status = new MailStatus();
      status.setVerified(true);
      Message message = event.getMessage();
      String uuid;
      try {
         String[] header = message.getHeader(MAIL_UUID_HEADER);
         uuid = header.length == 0 ? "-1" : header[0];
      } catch (Exception ex) {
         uuid = ex.getMessage();
      }
      status.setUuid(uuid);
      try {
         Date sentDate = message.getSentDate();
         status.setDateSent(sentDate);
      } catch (Exception ex) {
         // Do nothing
      }
      String subject;
      try {
         subject = message.getSubject();
      } catch (Exception ex) {
         subject = ex.getMessage();
      }
      status.setSubject(subject);

      try {
         addAddress(status.getFromAddress(), message.getFrom());
      } catch (Exception ex) {
         //
      }
      addAddress(status.getInvalidAddress(), event.getInvalidAddresses());
      addAddress(status.getSentAddress(), event.getValidSentAddresses());
      addAddress(status.getUnsentAddress(), event.getValidUnsentAddresses());
      return status;
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

   private static void addAddress(Collection<String> data, Address... addresses) {
      if (addresses != null) {
         for (Address address : addresses) {
            data.add(address.toString());
         }
      }
   }

}
