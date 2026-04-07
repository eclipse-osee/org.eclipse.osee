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
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
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
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.operator.OutputEncryptor;
import org.eclipse.osee.framework.core.data.EmailRecipientInfo;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringDataSource;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Michael A. Winston
 * @author Donald G. Dunne
 */
public abstract class OseeEmail extends MimeMessage implements IOseeEmail {
   public static final String EMAIL_BODY_REDACTED_FOR_ABRIDGED_EMAIL =
      "<email body redacted for abridged email; see primary email account>";
   public static final String DEFAULT_MAIL_SERVER_NOT_CONFIGURED = "Default Mail Server is not configured";
   protected static final String emailType = "mail.smtp.host";
   protected static final String HTMLHead = "<html><body>\n";
   protected static final String HTMLEnd = "</body></html>\n";

   public static final String plainText = "text/plain";
   public static final String HTMLText = "text/html";

   protected static String defaultMailServer;
   private String body = null;
   private BodyType bodyType = null;
   private final Multipart mainMessage;
   private String fromAddress;
   private String replyToAddress;

   /**
    * Enables recipient certificate lookup and encrypted send behavior. Recipients with valid certificates receive the
    * encrypted intended content. Recipients without a valid certificate receive a sanitized fallback message.
    */
   private boolean encryptionEnabled = true;

   /**
    * Subject used for the sanitized fallback email sent to recipients that do not have a valid public certificate on
    * file.
    */
   private static final String MISSING_CERTIFICATE_SUBJECT =
      "Action Required: Upload a Valid Email Encryption Certificate For OSEE";

   /**
    * Base body used for the sanitized fallback email sent to recipients that do not have a valid public certificate on
    * file. Upload URL and organization specific instructions are appended automatically when available.
    */
   private static final String MISSING_CERTIFICATE_BODY =
      "We were unable to deliver the full contents of a secure OSEE email because your public email encryption certificate is missing or invalid.\n\n";

   /**
    * Information pointing users to a certificate upload location.
    */
   private static final String MISSING_CERTIFICATE_UPLOAD_INFO =
      "To continue receiving secure OSEE emails, please upload a new public certificate using the link below:\n";

   /**
    * Fallback message when neither a dynamic upload URL nor an override URL is available.
    */
   private static final String MISSING_DYNAMIC_AND_OVERRIDE_WEB_URL =
      "This server instance does not have either an EmailCertificateUploadOverrideLink or the osee application server web URI configured. At least one of these must be specified to provide an upload location.\n";

   public static enum BodyType {
      Html,
      Text;
   };

   // Support for non-classified "abridged" emails.  Should be generic information only
   private String bodyAbridged = EMAIL_BODY_REDACTED_FOR_ABRIDGED_EMAIL;
   private Collection<String> emailAddressesAbridged;

   public OseeEmail() {
      super(getSession());
      mainMessage = new MimeMultipart();
      emailAddressesAbridged = Collections.emptyList();
   }

   /**
    * Constructs an AEmail with the given arguments
    *
    * @param toAddresses - a list of valid addresses to send the message TO
    * @param fromAddress - the sender of the message
    * @param replyToAddress - a valid address of who the message should reply to
    * @param subject - the subject of the message
    * @param emailAddressesAbridged addresses to send abridged email with same subject and no body
    * @param bodyAbridged generic email body with no classified or proprietary data
    * @param textBody - the plain text of the body
    */
   public OseeEmail(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, //
      BodyType bodyType, Collection<String> emailAddressesAbridged, String bodyAbridged) {
      this();
      this.fromAddress = fromAddress;
      this.replyToAddress = replyToAddress;
      this.emailAddressesAbridged = emailAddressesAbridged != null ? emailAddressesAbridged : Collections.emptyList();
      this.bodyAbridged = bodyAbridged;
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

   public OseeEmail(String fromEmail, String toAddress, String subject, String body, BodyType bodyType, String emailAddressAbridged, String bodyAbridged) {
      this(Arrays.asList(toAddress), fromEmail, fromEmail, subject, body, bodyType,
         Strings.isValid(emailAddressAbridged) ? Collections.singleton(emailAddressAbridged) : Collections.emptyList(),
         bodyAbridged);
   }

   /**
    * Adds a single address to the recipient list
    *
    * @param addresses - a valid address to send the message TO
    */
   @Override
   public void addRecipients(String addresses) throws MessagingException {
      addRecipients(Message.RecipientType.TO, addresses);
   }

   /**
    * Adds a list of addresses to the recipient list
    *
    * @param addresses - a list of valid addresses to send the message TO
    */
   @Override
   public void addRecipients(String[] addresses) throws MessagingException {
      addRecipients(Message.RecipientType.TO, addresses);
   }

   /**
    * Adds a list of addresses to the corresponding recipient list
    *
    * @param type - specifies which field the address should be put in
    * @param addresses - a list of valid addresses to send the message
    */
   @Override
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
   @Override
   public void setRecipients(String addresses) throws MessagingException {
      setRecipients(Message.RecipientType.TO, addresses);
   }

   /**
    * Sets a list of addresses to the recipient list
    *
    * @param addresses - a list of valid addresses to send the message TO
    */
   @Override
   public void setRecipients(String[] addresses) throws MessagingException {
      setRecipients(Message.RecipientType.TO, addresses);
   }

   /**
    * Sets a list of addresses to the corresponding recipient list
    *
    * @param type - specifies which field the address should be put in
    * @param addresses - a list of valid addresses to send the message
    */
   @Override
   public void setRecipients(Message.RecipientType type, String[] addresses) throws MessagingException {
      if (addresses != null) {

         InternetAddress newAddresses[] = new InternetAddress[addresses.length];

         for (int i = 0; i < addresses.length; i++) {
            if (!addresses[i].isEmpty()) {
               newAddresses[i] = new InternetAddress(addresses[i]);
            }
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
   @Override
   public void setFrom(String address) throws AddressException, MessagingException {
      setFrom(new InternetAddress(address));
   }

   /**
    * Sets the address to reply to (if different than the from addresss)
    */
   @Override
   public void setReplyTo(String address) throws MessagingException {
      InternetAddress replyAddresses[] = new InternetAddress[1];
      replyAddresses[0] = new InternetAddress(address);
      setReplyTo(replyAddresses);
   }

   /**
    * Gets the current Body Type of the message. NULL if one is not selected yet.
    */
   @Override
   public BodyType getBodyType() {
      return bodyType;
   }

   /**
    * Sets the text in the body of the message.
    */
   @Override
   public void setBody(String text) {
      body = text;
      bodyType = BodyType.Text;
   }

   /**
    * Adds text to the body if the Body Type is "plain". If the body doesn't exist yet, then calls setBody.
    */
   @Override
   public void addBody(String text) {
      if (bodyType == null) {
         setBody(text);
      } else if (bodyType.equals(BodyType.Text)) {
         body += text;
      }
   }

   /**
    * Sets the text in the body of the HTML message. This will already add the &lthtml&gt&ltbody&gt and
    * &lt/body&gt&lt/html&gt tags.
    */
   @Override
   public void setHTMLBody(String htmlText) {
      bodyType = BodyType.Html;
      body = HTMLHead + htmlText;
   }

   /**
    * Adds text to the HTML body if the Body Type is "html". If the body doesn't exist yet, then calls setHTMLBody.
    *
    * @param htmlText - the text to add to the HTML body
    */
   @Override
   public void addHTMLBody(String htmlText) {
      if (bodyType == null) {
         setHTMLBody(htmlText);
      } else if (bodyType == BodyType.Html) {
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

   @Override
   public void send(XResultData rd) {
      if (Strings.isValid(defaultMailServer) && !"disabled".equals(defaultMailServer)) {
         send();
      } else {
         rd.errorf(OseeEmail.DEFAULT_MAIL_SERVER_NOT_CONFIGURED);
      }
   }

   @Override
   public void send() {
      if (Strings.isValid(defaultMailServer) && !"disabled".equals(defaultMailServer)) {
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

         if (email.hasAbridged()) {
            OseeEmail aEmail = createAbridgedEmail(email);
            aEmail.sendLocalThread();
         }
      }
   }

   /**
    * Sends the email on the current thread. If encryption is enabled, recipients are partitioned into those with valid
    * public certificates and those without. Intended content is sent encrypted to the valid set and a sanitized email
    * is sent to the invalid or missing set.
    */
   @Override
   public XResultData sendLocalThread() {
      XResultData results = new XResultData();

      // Do not send emails from sandbox.
      if (System.getProperty("sandbox", "false").equals("true")) {
         return results;
      }

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
         normalizeBody();

         if (encryptionEnabled) {
            sendWithEncryptionWorkflow();
         } else {
            sendStandardMessage(getAllRecipientsByType(), getSubject(), body, bodyType, true);
         }
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

   /**
    * Performs the encrypted-email workflow:
    * <ul>
    * <li>looks up recipient certificate information</li>
    * <li>validates and parses the stored certificate for each recipient</li>
    * <li>sends the intended content encrypted to recipients with a valid certificate</li>
    * <li>sends a sanitized fallback message to recipients without a valid certificate</li>
    * </ul>
    */
   private void sendWithEncryptionWorkflow() throws Exception {
      Map<Message.RecipientType, List<InternetAddress>> recipientsByType = getAllRecipientsByType();
      List<String> allEmails = flattenEmails(recipientsByType);

      if (allEmails.isEmpty()) {
         return;
      }

      Map<String, EmailRecipientInfo> infoByEmail = new HashMap<>();
      for (EmailRecipientInfo info : getRecipientInfo(allEmails)) {
         if (info != null && Strings.isValid(info.getEmail())) {
            infoByEmail.put(info.getEmail().toLowerCase(), info);
         }
      }

      Map<String, X509Certificate> validCertsByEmail = new HashMap<>();
      List<String> invalidOrMissingEmails = new ArrayList<>();

      for (String email : allEmails) {
         EmailRecipientInfo info = infoByEmail.get(email.toLowerCase());
         if (info == null || !Strings.isValid(info.getPublicCertificate())) {
            invalidOrMissingEmails.add(email);
            continue;
         }

         try {
            X509Certificate cert = EmailCertificateValidator.parseAndCheckBasicValidity(info.getPublicCertificate());
            EmailCertificateValidator.checkSuitableForEmail(cert);
            validCertsByEmail.put(email.toLowerCase(), cert);
         } catch (Exception ex) {
            invalidOrMissingEmails.add(email);
         }
      }

      Map<Message.RecipientType, List<InternetAddress>> validRecipientsByType =
         filterRecipientsByType(recipientsByType, validCertsByEmail.keySet());
      Map<Message.RecipientType, List<InternetAddress>> invalidRecipientsByType =
         filterRecipientsByType(recipientsByType, toLowerCaseCollection(invalidOrMissingEmails));

      if (!flattenEmails(validRecipientsByType).isEmpty()) {
         sendEncryptedMessage(validRecipientsByType, validCertsByEmail);
      }

      if (!flattenEmails(invalidRecipientsByType).isEmpty()) {
         sendSanitizedMessage(invalidRecipientsByType);
      }
   }

   /**
    * Sends the intended email content encrypted to the provided recipients using their public certificates.
    */
   private void sendEncryptedMessage(Map<Message.RecipientType, List<InternetAddress>> recipientsByType,
      Map<String, X509Certificate> certsByEmail) throws Exception {
      MimeMessage encryptedMsg = new MimeMessage(getSession());
      applyEnvelope(encryptedMsg, recipientsByType, getSubject());

      MimeBodyPart clearPart = new MimeBodyPart();
      clearPart.setContent(buildMultipartForSend(body, bodyType, true));

      installBouncyCastle();

      SMIMEEnvelopedGenerator envGen = new SMIMEEnvelopedGenerator();
      for (String email : flattenEmails(recipientsByType)) {
         X509Certificate cert = certsByEmail.get(email.toLowerCase());
         if (cert != null) {
            envGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(cert).setProvider("BC"));
         }
      }

      OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC).setProvider("BC").build();

      MimeBodyPart encryptedPart = envGen.generate(clearPart, encryptor);
      encryptedMsg.setContent(encryptedPart.getContent(), encryptedPart.getContentType());
      encryptedMsg.saveChanges();
      Transport.send(encryptedMsg);
   }

   /**
    * Sends the sanitized fallback message to recipients that are missing a valid public certificate.
    */
   private void sendSanitizedMessage(Map<Message.RecipientType, List<InternetAddress>> recipientsByType)
      throws Exception {
      StringBuilder sanitized = new StringBuilder();
      sanitized.append(MISSING_CERTIFICATE_BODY);

      String uploadLink = getCertificateUploadLink();
      String uploadOverrideLink = getCertificateUploadOverrideLink();

      String chosenUploadInfo;
      if (Strings.isValid(uploadOverrideLink)) {
         chosenUploadInfo = uploadOverrideLink;
      } else if (Strings.isValid(uploadLink)) {
         chosenUploadInfo = uploadLink;
      } else {
         chosenUploadInfo = MISSING_DYNAMIC_AND_OVERRIDE_WEB_URL;
      }

      sanitized.append(MISSING_CERTIFICATE_UPLOAD_INFO).append(chosenUploadInfo).append("\n\n");

      String orgSpecificInfo = getOrganizationSpecificCertificateInstructions();
      if (Strings.isValid(orgSpecificInfo)) {
         sanitized.append(orgSpecificInfo);
         if (!orgSpecificInfo.endsWith("\n")) {
            sanitized.append("\n");
         }
      }

      sendStandardMessage(recipientsByType, MISSING_CERTIFICATE_SUBJECT, sanitized.toString(), BodyType.Text, false);
   }

   /**
    * Sends a standard, non-encrypted email to the provided recipients.
    */
   private void sendStandardMessage(Map<Message.RecipientType, List<InternetAddress>> recipientsByType, String subject,
      String msgBody, BodyType msgBodyType, boolean includeAttachments) throws Exception {
      MimeMessage msg = new MimeMessage(getSession());
      applyEnvelope(msg, recipientsByType, subject);
      msg.setContent(buildMultipartForSend(msgBody, msgBodyType, includeAttachments));
      msg.saveChanges();
      Transport.send(msg);
   }

   /**
    * Builds the message multipart to send. The first part is always the body content. Existing attachments are added
    * when requested.
    */
   private Multipart buildMultipartForSend(String msgBody, BodyType msgBodyType, boolean includeAttachments)
      throws Exception {
      MimeMultipart multipart = new MimeMultipart();

      MimeBodyPart bodyPart = new MimeBodyPart();
      bodyPart.setContent(msgBody, msgBodyType == BodyType.Text ? plainText : HTMLText);
      multipart.addBodyPart(bodyPart, 0);

      if (includeAttachments) {
         for (int i = 0; i < mainMessage.getCount(); i++) {
            multipart.addBodyPart(mainMessage.getBodyPart(i));
         }
      }

      return multipart;
   }

   /**
    * Applies From, Reply-To, Subject, and recipients to the provided message.
    */
   private void applyEnvelope(MimeMessage msg, Map<Message.RecipientType, List<InternetAddress>> recipientsByType,
      String subject) throws MessagingException {
      if (Strings.isValid(fromAddress)) {
         msg.setFrom(new InternetAddress(fromAddress));
      }
      if (Strings.isValid(replyToAddress)) {
         msg.setReplyTo(new Address[] {new InternetAddress(replyToAddress)});
      }
      for (Map.Entry<Message.RecipientType, List<InternetAddress>> entry : recipientsByType.entrySet()) {
         if (!entry.getValue().isEmpty()) {
            msg.setRecipients(entry.getKey(), entry.getValue().toArray(new InternetAddress[0]));
         }
      }
      msg.setSubject(subject);
   }

   /**
    * Normalizes the stored body values prior to send.
    */
   private void normalizeBody() {
      if (bodyType == null) {
         bodyType = BodyType.Text;
         body = "";
      } else if (bodyType.equals(BodyType.Html)) {
         if (body == null) {
            body = HTMLHead + HTMLEnd;
         } else if (!body.endsWith(HTMLEnd)) {
            body += HTMLEnd;
         }
      }
   }

   /**
    * Installs the Bouncy Castle provider if needed for S/MIME encryption.
    */
   private void installBouncyCastle() {
      if (Security.getProvider("BC") == null) {
         Security.addProvider(new BouncyCastleProvider());
      }
   }

   /**
    * Returns all current recipients grouped by recipient type.
    */
   private Map<Message.RecipientType, List<InternetAddress>> getAllRecipientsByType() throws MessagingException {
      Map<Message.RecipientType, List<InternetAddress>> map = new HashMap<>();
      map.put(Message.RecipientType.TO, getInternetAddresses(Message.RecipientType.TO));
      map.put(Message.RecipientType.CC, getInternetAddresses(Message.RecipientType.CC));
      map.put(Message.RecipientType.BCC, getInternetAddresses(Message.RecipientType.BCC));
      return map;
   }

   /**
    * Returns all recipients of the requested recipient type as InternetAddress instances.
    */
   private List<InternetAddress> getInternetAddresses(Message.RecipientType type) throws MessagingException {
      Address[] addresses = getRecipients(type);
      List<InternetAddress> result = new ArrayList<>();
      if (addresses != null) {
         for (Address address : addresses) {
            if (address instanceof InternetAddress) {
               result.add((InternetAddress) address);
            }
         }
      }
      return result;
   }

   /**
    * Flattens recipient groups to a list of email addresses.
    */
   private List<String> flattenEmails(Map<Message.RecipientType, List<InternetAddress>> recipientsByType) {
      List<String> emails = new ArrayList<>();
      for (List<InternetAddress> addresses : recipientsByType.values()) {
         for (InternetAddress address : addresses) {
            emails.add(address.getAddress());
         }
      }
      return emails;
   }

   /**
    * Filters grouped recipients to only those whose lowercase email address is contained in the allowed set.
    */
   private Map<Message.RecipientType, List<InternetAddress>> filterRecipientsByType(
      Map<Message.RecipientType, List<InternetAddress>> recipientsByType, Collection<String> allowedEmailsLower) {
      Map<Message.RecipientType, List<InternetAddress>> filtered = new HashMap<>();
      for (Map.Entry<Message.RecipientType, List<InternetAddress>> entry : recipientsByType.entrySet()) {
         List<InternetAddress> addresses = new ArrayList<>();
         for (InternetAddress address : entry.getValue()) {
            if (allowedEmailsLower.contains(address.getAddress().toLowerCase())) {
               addresses.add(address);
            }
         }
         filtered.put(entry.getKey(), addresses);
      }
      return filtered;
   }

   /**
    * Returns a lowercase copy of the provided collection values.
    */
   private Collection<String> toLowerCaseCollection(Collection<String> values) {
      List<String> lowered = new ArrayList<>();
      for (String value : values) {
         lowered.add(value.toLowerCase());
      }
      return lowered;
   }

   /**
    * Hook for subclasses to resolve recipient information, including any stored public certificate for the supplied
    * email addresses.
    */
   protected List<EmailRecipientInfo> getRecipientInfo(Collection<String> emailAddresses) {
      return Collections.emptyList();
   }

   /**
    * Hook for subclasses to provide the certificate upload URL included in the sanitized fallback email.
    */
   protected String getCertificateUploadLink() {
      return "";
   }

   /**
    * Hook for subclasses to provide the certificate upload override URL included in the sanitized fallback email.
    */
   protected String getCertificateUploadOverrideLink() {
      return "";
   }

   /**
    * Hook for subclasses to provide organization specific supplemental certificate instructions included in the
    * sanitized fallback email.
    */
   protected String getOrganizationSpecificCertificateInstructions() {
      return "";
   }

   protected abstract OseeEmail createAbridgedEmail(OseeEmail email);

   public boolean hasAbridged() {
      return emailAddressesAbridged != null && !emailAddressesAbridged.isEmpty();
   }

   abstract public void setClassLoader();

   /**
    * Gets the current session
    *
    * @return the Current SMTP Session
    */
   private static Session getSession() {
      Properties props = System.getProperties();
      if (defaultMailServer != null) {
         props.put(emailType, defaultMailServer);
      }
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

   @Override
   public void addAttachment(File file) throws MessagingException {
      addAttachment(new FileDataSource(file), file.getName());
   }

   @Override
   public void addAttachment(String contents, String attachmentName) throws MessagingException {
      addAttachment(new StringDataSource(contents, attachmentName), attachmentName);
   }

   public static String getDefaultMailServer() {
      return defaultMailServer;
   }

   public static void setDefaultMailServer(String defaultMailServer) {
      OseeEmail.defaultMailServer = defaultMailServer;
   }

   public String getFromAddress() {
      return fromAddress;
   }

   public String getReplyToAddress() {
      return replyToAddress;
   }

   public Collection<String> getEmailAddressesAbridged() {
      return emailAddressesAbridged;
   }

   public String getBodyAbridged() {
      return bodyAbridged;
   }

   public void setBodyAbridged(String bodyAbridged) {
      this.bodyAbridged = bodyAbridged;
   }

   public boolean isEncryptionEnabled() {
      return encryptionEnabled;
   }

   public void setEncryptionEnabled(boolean encryptionEnabled) {
      this.encryptionEnabled = encryptionEnabled;
   }

   public String getMissingCertificateSubject() {
      return MISSING_CERTIFICATE_SUBJECT;
   }

   public String getMissingCertificateBody() {
      return MISSING_CERTIFICATE_BODY;
   }

   public String getMissingCertificateUploadInfo() {
      return MISSING_CERTIFICATE_UPLOAD_INFO;
   }

}
