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
package org.eclipse.osee.orcs.utility;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.EmailRecipientInfo;

public interface EmailCertificateService {

   String CERTIFICATE_MANAGEMENT_SUB_PATH = "/osee/certificate-management";

   /**
    * Validates and stores the current user's public email certificate in PEM format.
    */
   void setPublicCertificateForCurrentUser(String certificatePem);

   /**
    * Returns the current user's stored public email certificate in PEM format, or null if none exists.
    */
   String getPublicCertificateForCurrentUser();

   /**
    * Deletes the current user's stored public email certificate.
    */
   void deletePublicCertificateForCurrentUser();

   /**
    * Returns recipient information for the supplied email addresses, including any stored public certificates.
    */
   List<EmailRecipientInfo> getPublicCertificatesByEmailAddresses(Collection<String> emailAddresses);

   /**
    * Returns recipient information for the supplied email addresses, including any stored public certificates. If an
    * LDAP URL is provided, LDAP may be queried for recipients whose stored certificate is missing or invalid.
    */
   List<EmailRecipientInfo> getPublicCertificatesByEmailAddresses(Collection<String> emailAddresses,
      String emailCertificateLdapUrl);

   /**
    * Asynchronously writes back public certificates for the supplied email addresses in a single transaction.
    */
   void writePublicCertificatesByEmailAddressesAsync(Map<String, String> certificatesByEmailAddress);
}
