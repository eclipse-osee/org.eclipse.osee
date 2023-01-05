/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.core.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
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
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringDataSource;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Michael A. Winston
 * @author Donald G. Dunne
 */
public abstract class OseeEmail extends MimeMessage {
   protected static final String emailType = "mail.smtp.host";
   protected static final String HTMLHead = "<html><body>\n";
   protected static final String HTMLEnd = "</body></html>\n";

   public static final String plainText = "text/plain";
   public static final String HTMLText = "text/html";

   protected static String defaultMailServer;
   private String body = null;
   private String bodyType = null;
   private final Multipart mainMessage;
   public static enum BodyType {
      Html,
      Text
   };

   public OseeEmail() {
      super(getSession());
      mainMessage = new MimeMultipart();
   }

   /**
    * Constructs an AEmail with the given arguments
    *
    * @param toAddresses - a list of valid addresses to send the message TO
    * @param fromAddress - the sender of the message
    * @param replyToAddress - a valid address of who the message should reply to
    * @param subject - the subject of the message
    * @param textBody - the plain text of the body
    */
   public OseeEmail(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, BodyType bodyType) {
      this();
      try {
         setRecipients(toAddresses.toArray(new String[toAddresses.size()]));
         setFrom(fromAddress);
         setSubject(subject);
         setReplyTo(replyToAddress);

         if (bodyType == BodyType.Text) {
            setBody(body);
         } else if (bodyType == BodyType.Html) {
            setHTMLBody(body);
         } else {
            throw new IllegalArgumentException("Unhandled body type " + bodyType);
         }

      } catch (MessagingException ex) {
         OseeLog.log(OseeEmail.class, Level.SEVERE, ex);
      }
   }

   public OseeEmail(String fromEmail, String toAddress, String subject, String body, BodyType bodyType) {
      this(Arrays.asList(toAddress), fromEmail, fromEmail, subject, body, bodyType);
   }

   /**
    * Adds a single address to the recipient list
    *
    * @param addresses - a valid address to send the message TO
    */
   public void addRecipients(String addresses) throws MessagingException {
      addRecipients(Message.RecipientType.TO, addresses);
   }

   /**
    * Adds a list of addresses to the recipient list
    *
    * @param addresses - a list of valid addresses to send the message TO
    */
   public void addRecipients(String[] addresses) throws MessagingException {
      addRecipients(Message.RecipientType.TO, addresses);
   }

   /**
    * Adds a list of addresses to the corresponding recipient list
    *
    * @param type - specifies which field the address should be put in
    * @param addresses - a list of valid addresses to send the message
    */
   public void addRecipients(Message.RecipientType type, String[] addresses) throws MessagingException {
      if (addresses != null) {

         InternetAddress newAddresses[] = new InternetAddress[addresses.length];

         for (int i = 0; i < addresses.length; i++) {
            newAddresses[i] = new InternetAddress(addresses[i]);
         }

         addRecipients(type, newAddresses);
      }
   }

   /**
    * Sets the recipient TO field
    *
    * @param addresses - a valid address to send the message TO
    */
   public void setRecipients(String addresses) throws MessagingException {
      setRecipients(Message.RecipientType.TO, addresses);
   }

   /**
    * Sets a list of addresses to the recipient list
    *
    * @param addresses - a list of valid addresses to send the message TO
    */
   public void setRecipients(String[] addresses) throws MessagingException {
      setRecipients(Message.RecipientType.TO, addresses);
   }

   /**
    * Sets a list of addresses to the corresponding recipient list
    *
    * @param type - specifies which field the address should be put in
    * @param addresses - a list of valid addresses to send the message
    */
   public void setRecipients(Message.RecipientType type, String[] addresses) throws MessagingException {
      if (addresses != null) {

         InternetAddress newAddresses[] = new InternetAddress[addresses.length];

         for (int i = 0; i < addresses.length; i++) {
            newAddresses[i] = new InternetAddress(addresses[i]);
         }

         setRecipients(type, newAddresses);
      }
   }

   /**
    * Sets the from address
    *
    * @param address - the user name the message is from
    */
   // Set all the From Values
   public void setFrom(String address) throws AddressException, MessagingException {
      setFrom(new InternetAddress(address));
   }

   /**
    * Sets the address to reply to (if different than the from addresss)
    *
    * @param address - a valid address to reply to
    */
   public void setReplyTo(String address) throws MessagingException {
      InternetAddress replyAddresses[] = new InternetAddress[1];
      replyAddresses[0] = new InternetAddress(address);
      setReplyTo(replyAddresses);
   }

   /**
    * Gets the current Body Type of the message. NULL if one is not selected yet.
    *
    * @return A String representation of the current Body Type
    */
   public String getBodyType() {
      return bodyType;
   }

   /**
    * Sets the text in the body of the message.
    *
    * @param text - the text to for the body of the message
    */
   public void setBody(String text) {
      body = text;
      bodyType = plainText;
   }

   /**
    * Adds text to the body if the Body Type is "plain". If the body doesn't exist yet, then calls setBody.
    *
    * @param text - the text to add to the body
    */
   public void addBody(String text) {
      if (bodyType == null) {
         setBody(text);
      } else if (bodyType.equals(plainText)) {
         body += text;
      }
   }

   /**
    * Sets the text in the body of the HTML message. This will already add the &lthtml&gt&ltbody&gt and
    * &lt/body&gt&lt/html&gt tags.
    *
    * @param htmlText - the text for the body of the HTML message
    */
   public void setHTMLBody(String htmlText) {
      bodyType = HTMLText;
      body = HTMLHead + htmlText;
   }

   /**
    * Adds text to the HTML body if the Body Type is "html". If the body doesn't exist yet, then calls setHTMLBody.
    *
    * @param htmlText - the text to add to the HTML body
    */
   public void addHTMLBody(String htmlText) {
      if (bodyType == null) {
         setHTMLBody(htmlText);
      } else if (bodyType.equals(HTMLText)) {
         body += htmlText;
      }

   }

   @Override
   public void setSubject(String subject) {
      try {
         super.setSubject(subject);
      } catch (MessagingException ex) {
         // do nothing
      }
   }

   /**
    * Sends the message.
    */
   public void send() {
      if (Strings.isValid(defaultMailServer)) {
         new SendThread(this).start();
      }
   }

   private class SendThread extends Thread {

      private final OseeEmail email;

      public SendThread(OseeEmail email) {
         this.email = email;
      }

      @Override
      public void run() {
         XResultData results = email.sendLocalThread();
         if (results.isFailed()) {
            OseeLog.log(OseeEmail.class, Level.SEVERE, results.toString());
         }
      }
   }

   public XResultData sendLocalThread() {
      XResultData results = new XResultData();
      MimeBodyPart messageBodyPart = new MimeBodyPart();
      ClassLoader original = Thread.currentThread().getContextClassLoader();
      try {

         MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
         mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
         mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
         mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
         mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
         mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
         CommandMap.setDefaultCommandMap(mc);

         // Set class loader so can find the mail handlers
         setClassLoader();
         if (bodyType == null) {
            bodyType = plainText;
            body = "";
         } else if (bodyType.equals(HTMLText)) {
            body += HTMLEnd;
         }
         messageBodyPart.setContent(body, bodyType);
         mainMessage.addBodyPart(messageBodyPart, 0);
         setContent(mainMessage);
         Transport.send(this);
      } catch (Exception ex) {
         results.errorf("Exception sending message (contents below) [%s]", Lib.exceptionToString(ex));
         /**
          * Do not display exception for email address email service does not know since it is normal with user leaves
          */
         if (!results.toString().contains("User unknown")) {
            results.logf("Contents are [%s]", body);
            OseeLog.log(OseeEmail.class, Level.SEVERE, results.toString());
         }
      } finally {
         Thread.currentThread().setContextClassLoader(original);
      }
      return results;
   }

   abstract public void setClassLoader();

   /**
    * Gets the current session
    *
    * @return the Current SMTP Session
    */
   private static Session getSession() {
      Properties props = System.getProperties();
      props.put(emailType, defaultMailServer);
      return Session.getDefaultInstance(props, null);
   }

   /**
    * Adds an attachment to an email
    */
   public void addAttachment(DataSource source, String attachmentName) throws MessagingException {
      MimeBodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setDataHandler(new DataHandler(source));
      messageBodyPart.setFileName(attachmentName);
      mainMessage.addBodyPart(messageBodyPart);
   }

   public void addAttachment(File file) throws MessagingException {
      addAttachment(new FileDataSource(file), file.getName());
   }

   public void addAttachment(String contents, String attachmentName) throws MessagingException {
      addAttachment(new StringDataSource(contents, attachmentName), attachmentName);
   }

   public static String getDefaultMailServer() {
      return defaultMailServer;
   }

   public static void setDefaultMailServer(String defaultMailServer) {
      OseeEmail.defaultMailServer = defaultMailServer;
   }

}
