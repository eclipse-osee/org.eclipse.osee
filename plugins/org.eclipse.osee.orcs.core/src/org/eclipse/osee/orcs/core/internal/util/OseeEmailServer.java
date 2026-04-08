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
package org.eclipse.osee.orcs.core.internal.util;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import javax.mail.MessagingException;
import org.eclipse.osee.framework.core.data.EmailRecipientInfo;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.orcs.utility.EmailCertificateService;

public class OseeEmailServer extends OseeEmail {

   private String certificateUploadOverrideLink = "";
   private String organizationSpecificCertificateInstructions = "";
   private String emailCertificateLdapUrl = "";

   public OseeEmailServer() {
      super();
   }

   OseeEmailServer(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, BodyType bodyType, Collection<String> emailAddressesAbridged, String bodyAbridged) {
      super(toAddresses, fromAddress, replyToAddress, subject, body, bodyType, emailAddressesAbridged, bodyAbridged);
   }

   OseeEmailServer(String fromEmail, String toAddress, String subject, String body, BodyType bodyType, String emailAddressesAbridged, String bodyAbridged) {
      super(fromEmail, toAddress, subject, body, bodyType, emailAddressesAbridged, bodyAbridged);
   }

   @Override
   public void setClassLoader() {
      Thread.currentThread().setContextClassLoader(new ExportClassLoader(ServiceUtil.getPackageAdmin()));
   }

   @Override
   protected OseeEmail createAbridgedEmail(OseeEmail email) {
      String subject = "";
      try {
         subject = email.getSubject();
      } catch (MessagingException ex) {
         OseeLog.log(OseeEmailServer.class, Level.SEVERE, "Failed to retrieve subject while creating abridged email.",
            ex);
      }

      String abridgedSubject = Strings.isValid(subject) ? "[Abridged] " + subject : "OSEE Abridged Email";

      OseeEmailServer abridgedEmail =
         new OseeEmailServer(email.getEmailAddressesAbridged(), email.getFromAddress(), email.getReplyToAddress(),
            abridgedSubject, email.getBodyAbridged(), email.getBodyType(), Collections.emptyList(), "");
      abridgedEmail.setEncryptionEnabled(isEncryptionEnabled());
      abridgedEmail.setCertificateUploadOverrideLink(getCertificateUploadOverrideLink());
      abridgedEmail.setOrganizationSpecificCertificateInstructions(getOrganizationSpecificCertificateInstructions());
      abridgedEmail.setEmailCertificateLdapUrl(getEmailCertificateLdapUrl());
      return abridgedEmail;
   }

   @Override
   protected List<EmailRecipientInfo> getRecipientInfo(Collection<String> emailAddresses) {
      EmailCertificateService service = ServiceUtil.getOrcsApi().getEmailCertificateService();
      return service.getPublicCertificatesByEmailAddresses(emailAddresses, emailCertificateLdapUrl);
   }

   @Override
   protected String getCertificateUploadLink() {
      return OseeServerProperties.getOseeApplicationServerWebUri() //
         .map(base -> URI.create(base).resolve(EmailCertificateService.CERTIFICATE_MANAGEMENT_SUB_PATH).toString()) //
         .orElse("");
   }

   @Override
   protected String getCertificateUploadOverrideLink() {
      return certificateUploadOverrideLink;
   }

   void setCertificateUploadOverrideLink(String certificateUploadOverrideLink) {
      this.certificateUploadOverrideLink = certificateUploadOverrideLink;
   }

   @Override
   protected String getOrganizationSpecificCertificateInstructions() {
      return organizationSpecificCertificateInstructions;
   }

   void setOrganizationSpecificCertificateInstructions(String organizationSpecificCertificateInstructions) {
      this.organizationSpecificCertificateInstructions = organizationSpecificCertificateInstructions;
   }

   String getEmailCertificateLdapUrl() {
      return emailCertificateLdapUrl;
   }

   void setEmailCertificateLdapUrl(String emailCertificateLdapUrl) {
      this.emailCertificateLdapUrl = emailCertificateLdapUrl;
   }

}
