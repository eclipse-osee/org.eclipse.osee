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

package org.eclipse.osee.ats.rest.internal.notify;

import java.io.File;
import java.util.Collection;
import java.util.Properties;
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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringDataSource;

/**
 * @author Michael A. Winston
 * @author Donald G. Dunne
 */
public class OseeEmail extends MimeMessage {
   protected static final String emailType = "mail.smtp.host";
   protected static final String HTMLHead = "<html><body>\n";
   protected static final String HTMLEnd = "</body></html>\n";

   public static final String plainText = "text/plain";
   public static final String HTMLText = "text/html";

   private String body = null;
   private String bodyType = null;
   private final Multipart mainMessage;
   private final AtsApi atsApi;
   public static enum BodyType {
      Html,
      Text
   };

   public OseeEmail(AtsApi atsApi) {
      super(getSession(atsApi));
      this.atsApi = atsApi;
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
   public OseeEmail(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, BodyType bodyType, AtsApi atsApi) {
      this(atsApi);
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
         System.err.println(Lib.exceptionToString(ex));
      }
   }

   /**
    * Constructs an AEmail with the given arguments
    *
    * @param subject - the subject of the message
    * @param body - the text/html of the body
    * @param bodyType - Html or Text
    */
   //   public OseeEmail(String toAddress, String subject, String body, BodyType bodyType) {
   //      this(Arrays.asList(toAddress), UserManager.getUser().getEmail(), UserManager.getUser().getEmail(), subject, body,
   //         bodyType);
   //   }

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

   /**
    * Sends the message.
    */
   public void send() {
      new SendThread(this).start();
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
            System.err.println(results.toString());
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
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
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
            System.err.println(results.toString());
         }
      } finally {
         Thread.currentThread().setContextClassLoader(original);
      }
      return results;
   }

   /**
    * Gets the current session
    *
    * @return the Current SMTP Session
    */
   private static Session getSession(AtsApi atsApi) {
      ArtifactToken artifact = atsApi.getQueryService().getArtifact(CoreArtifactTokens.GlobalPreferences);
      System.err.println(String.format("getSession art %s", artifact.toStringWithId()));
      Properties props = System.getProperties();
      String mailServer =
         atsApi.getAttributeResolver().getSoleAttributeValue(artifact, CoreAttributeTypes.DefaultMailServer, "");
      System.err.println(String.format("getSession mailServer %s", mailServer));
      props.put(emailType, mailServer);
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

   //   public static void emailHtml(Collection<String> emails, String subject, String htmlBody) {
   //      OseeEmail emailMessage = new OseeEmail(emails, UserManager.getUser().getEmail(), UserManager.getUser().getEmail(),
   //         subject, htmlBody, BodyType.Html);
   //      emailMessage.send();
   //   }

}
