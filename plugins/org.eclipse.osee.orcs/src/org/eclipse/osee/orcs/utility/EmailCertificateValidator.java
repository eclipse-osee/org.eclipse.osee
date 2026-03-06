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

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

public final class EmailCertificateValidator {

   // OID for ExtendedKeyUsage: email protection
   private static final String EKU_EMAIL_PROTECTION = "1.3.6.1.5.5.7.3.4";

   private EmailCertificateValidator() {
   }

   public static X509Certificate parseAndCheckBasicValidity(String pem) {
      if (pem == null || pem.isBlank()) {
         throw new EmailCertificateValidationException("Certificate is empty");
      }

      try {
         String normalizedPem = stripPemHeaders(pem);
         byte[] derBytes = java.util.Base64.getDecoder().decode(normalizedPem);

         CertificateFactory cf = CertificateFactory.getInstance("X.509");
         X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(derBytes));

         cert.checkValidity(new Date()); // throws CertificateException if expired/not yet valid
         return cert;
      } catch (IllegalArgumentException e) {
         throw new EmailCertificateValidationException("Certificate is not valid Base64 PEM", e);
      } catch (CertificateException e) {
         throw new EmailCertificateValidationException("Invalid X.509 certificate: " + e.getMessage(), e);
      }
   }

   public static void checkSuitableForEmail(X509Certificate cert) {
      try {
         List<String> eku = cert.getExtendedKeyUsage();
         if (eku != null && !eku.contains(EKU_EMAIL_PROTECTION)) {
            throw new EmailCertificateValidationException(
               "Certificate is not intended for email protection (missing EKU emailProtection)");
         }
      } catch (CertificateException e) {
         throw new EmailCertificateValidationException("Certificate extended key usage is invalid for email", e);
      }

      boolean[] keyUsage = cert.getKeyUsage();
      if (keyUsage != null) {
         boolean digitalSignature = keyUsage.length > 0 && keyUsage[0];
         boolean keyEncipherment = keyUsage.length > 2 && keyUsage[2];
         if (!digitalSignature && !keyEncipherment) {
            throw new EmailCertificateValidationException("Certificate key usage is not suitable for email");
         }
      }
   }

   private static String stripPemHeaders(String pem) {
      return pem.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "").replaceAll("\\s+",
         "");
   }
}
