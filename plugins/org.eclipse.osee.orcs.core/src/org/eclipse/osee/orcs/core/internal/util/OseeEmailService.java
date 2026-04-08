/*********************************************************************
 * Copyright (c) 2026 Boeing
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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.IOseeEmail;
import org.eclipse.osee.framework.core.util.IOseeEmailService;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.core.util.OseeEmail.BodyType;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class OseeEmailService implements IOseeEmailService {

   private final OrcsApi orcsApi;

   public OseeEmailService(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   /**
    * Holder for the email-related global preferences needed to construct and configure an {@link OseeEmailServer}.
    */
   private static class EmailPreferences {
      private final String defaultMailServer;
      private final boolean encryptionEnabled;
      private final String certificateUploadOverrideLink;
      private final String organizationSpecificCertificateInstructions;
      private final String emailCertificateLdapUrl;

      private EmailPreferences(String defaultMailServer, boolean encryptionEnabled,
         String certificateUploadOverrideLink, String organizationSpecificCertificateInstructions,
         String emailCertificateLdapUrl) {
         this.defaultMailServer = defaultMailServer;
         this.encryptionEnabled = encryptionEnabled;
         this.certificateUploadOverrideLink = certificateUploadOverrideLink;
         this.organizationSpecificCertificateInstructions = organizationSpecificCertificateInstructions;
         this.emailCertificateLdapUrl = emailCertificateLdapUrl;
      }
   }

   /**
    * Loads the global preferences needed for email delivery. Missing encryption preference values default to true.
    * Default mail server is also applied prior to constructing the email so the parent MimeMessage session can be
    * created safely.
    */
   private EmailPreferences loadPreferences() {
      ArtifactReadable globalArt = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         CoreArtifactTokens.GlobalPreferences).asArtifactOrSentinel();

      String mailServer = globalArt.getSoleAttributeValue(CoreAttributeTypes.DefaultMailServer, "");
      boolean encryptionEnabled = globalArt.getSoleAttributeValue(CoreAttributeTypes.EmailEncryptionEnabled, true);
      String certificateUploadOverrideLink =
         globalArt.getSoleAttributeValue(CoreAttributeTypes.EmailCertificateUploadOverrideLink, "");
      String organizationSpecificCertificateInstructions =
         globalArt.getSoleAttributeValue(CoreAttributeTypes.EmailCertificateInstructions, "");
      String emailCertificateLdapUrl = globalArt.getSoleAttributeValue(CoreAttributeTypes.EmailCertificateLdapUrl, "");

      OseeEmail.setDefaultMailServer(mailServer);

      return new EmailPreferences(mailServer, encryptionEnabled, certificateUploadOverrideLink,
         organizationSpecificCertificateInstructions, emailCertificateLdapUrl);
   }

   /**
    * Applies previously loaded preferences to the created email instance.
    */
   private void applyPreferences(OseeEmailServer email, EmailPreferences preferences) {
      email.setEncryptionEnabled(preferences.encryptionEnabled);
      email.setCertificateUploadOverrideLink(preferences.certificateUploadOverrideLink);
      email.setOrganizationSpecificCertificateInstructions(preferences.organizationSpecificCertificateInstructions);
      email.setEmailCertificateLdapUrl(preferences.emailCertificateLdapUrl);
   }

   @Override
   public IOseeEmail create(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject,
      String body, BodyType bodyType, Collection<String> emailAddressesAbridged, String bodyAbridged) {
      EmailPreferences preferences = loadPreferences();
      OseeEmailServer email =
         new OseeEmailServer(toAddresses, fromAddress, replyToAddress, subject, body, bodyType, emailAddressesAbridged,
            bodyAbridged);
      applyPreferences(email, preferences);
      return email;
   }

   @Override
   public IOseeEmail create(String fromEmail, String toAddress, String subject, String body, BodyType bodyType,
      String emailAddressAbridged, String bodyAbridged) {
      EmailPreferences preferences = loadPreferences();
      OseeEmailServer email =
         new OseeEmailServer(fromEmail, toAddress, subject, body, bodyType, emailAddressAbridged, bodyAbridged);
      applyPreferences(email, preferences);
      return email;
   }

   @Override
   public IOseeEmail create() {
      EmailPreferences preferences = loadPreferences();
      OseeEmailServer email = new OseeEmailServer();
      applyPreferences(email, preferences);
      return email;
   }

}
