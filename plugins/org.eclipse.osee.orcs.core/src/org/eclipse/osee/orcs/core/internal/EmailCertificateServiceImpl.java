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
package org.eclipse.osee.orcs.core.internal;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.EmailRecipientInfo;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.EmailCertificateValidator;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.internal.util.EmailCertificateLdapLookup;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.utility.EmailCertificateService;

public class EmailCertificateServiceImpl implements EmailCertificateService {

   private final OrcsApi orcsApi;
   private final ExecutorService ldapWritebackExecutor = Executors.newSingleThreadExecutor(runnable -> {
      Thread thread = new Thread(runnable, "EmailCertificateLdapWriteback");
      thread.setDaemon(true);
      return thread;
   });

   public EmailCertificateServiceImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public void setPublicCertificateForCurrentUser(String certificatePem) {
      // 1) Parse + basic validity (structure, dates)
      X509Certificate cert = EmailCertificateValidator.parseAndCheckBasicValidity(certificatePem);

      // 2) Ensure it is intended for email use (key usage / EKU, etc.)
      EmailCertificateValidator.checkSuitableForEmail(cert);

      // 3) Persist the PEM as-is for the current user
      UserToken user = orcsApi.userService().getUser();

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON,
         "Store email public certificate associated with user.");
      tx.setSoleAttributeFromString(user.getArtifactId(), CoreAttributeTypes.EmailPublicCertificate, certificatePem);
      tx.commit();
   }

   @Override
   public String getPublicCertificateForCurrentUser() {
      UserToken user = orcsApi.userService().getUser();
      QueryFactory queryFactory = orcsApi.getQueryFactory();

      return queryFactory.fromBranch(CoreBranches.COMMON).andId(
         user.getArtifactId()).asArtifact().getSoleAttributeAsString(CoreAttributeTypes.EmailPublicCertificate, null);
   }

   @Override
   public void deletePublicCertificateForCurrentUser() {
      UserToken user = orcsApi.userService().getUser();

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON,
         "Delete email public certificate associated with user.");
      tx.deleteSoleAttribute(user.getArtifactId(), CoreAttributeTypes.EmailPublicCertificate);
      tx.commit();
   }

   @Override
   public List<EmailRecipientInfo> getPublicCertificatesByEmailAddresses(Collection<String> emailAddresses) {
      return getPublicCertificatesByEmailAddresses(emailAddresses, "");
   }

   @Override
   public List<EmailRecipientInfo> getPublicCertificatesByEmailAddresses(Collection<String> emailAddresses,
      String emailCertificateLdapUrl) {
      if (emailAddresses == null || emailAddresses.isEmpty()) {
         return Collections.emptyList();
      }

      Map<String, EmailRecipientInfo> resultsByEmailLower = new LinkedHashMap<>();
      for (String email : emailAddresses) {
         if (Strings.isValid(email)) {
            resultsByEmailLower.put(email.toLowerCase(), new EmailRecipientInfo(email, null));
         }
      }

      if (resultsByEmailLower.isEmpty()) {
         return Collections.emptyList();
      }

      QueryFactory queryFactory = orcsApi.getQueryFactory();

      List<ArtifactReadable> emailUsers =
         queryFactory.fromBranch(CoreBranches.COMMON).andTypeEquals(CoreArtifactTypes.User).and(
            CoreAttributeTypes.Email, new ArrayList<>(resultsByEmailLower.keySet()),
            QueryOption.EXACT_MATCH_OPTIONS).asArtifacts();

      //@formatter:off
      emailUsers.stream().forEach(
         art -> {
            String email = art.getSoleAttributeValue(CoreAttributeTypes.Email, "");
            String certificate = art.getSoleAttributeValue(CoreAttributeTypes.EmailPublicCertificate, null);
            if (Strings.isValid(email)) {
               resultsByEmailLower.put(email.toLowerCase(), new EmailRecipientInfo(email, certificate));
            }
         });
      //@formatter:on

      List<String> ldapLookupEmails =
         resultsByEmailLower.values().stream().filter(info -> !hasValidCertificate(info.getPublicCertificate())).map(
            EmailRecipientInfo::getEmail).filter(Strings::isValid).collect(Collectors.toList());

      if (Strings.isValid(emailCertificateLdapUrl) && !ldapLookupEmails.isEmpty()) {
         Map<String, String> ldapCertificatesByEmail =
            EmailCertificateLdapLookup.getNewestEmailEncryptionCerts(emailCertificateLdapUrl, ldapLookupEmails);

         for (Map.Entry<String, String> entry : ldapCertificatesByEmail.entrySet()) {
            String emailLower = entry.getKey().toLowerCase();
            EmailRecipientInfo existing = resultsByEmailLower.get(emailLower);
            if (existing != null && Strings.isValid(entry.getValue())) {
               existing.setPublicCertificate(entry.getValue());
            }
         }

         if (!ldapCertificatesByEmail.isEmpty()) {
            writePublicCertificatesByEmailAddressesAsync(ldapCertificatesByEmail);
         }
      }

      return new ArrayList<>(resultsByEmailLower.values());
   }

   @Override
   public void writePublicCertificatesByEmailAddressesAsync(Map<String, String> certificatesByEmailAddress) {
      if (certificatesByEmailAddress == null || certificatesByEmailAddress.isEmpty()) {
         return;
      }

      Map<String, String> normalizedCertificatesByEmail = new LinkedHashMap<>();
      for (Map.Entry<String, String> entry : certificatesByEmailAddress.entrySet()) {
         if (Strings.isValid(entry.getKey()) && Strings.isValid(entry.getValue())) {
            normalizedCertificatesByEmail.put(entry.getKey().toLowerCase(), entry.getValue());
         }
      }

      if (normalizedCertificatesByEmail.isEmpty()) {
         return;
      }

      ldapWritebackExecutor.submit(() -> writePublicCertificatesByEmailAddresses(normalizedCertificatesByEmail));
   }

   private void writePublicCertificatesByEmailAddresses(Map<String, String> certificatesByEmailAddress) {
      try {
         QueryFactory queryFactory = orcsApi.getQueryFactory();

         List<ArtifactReadable> emailUsers =
            queryFactory.fromBranch(CoreBranches.COMMON).andTypeEquals(CoreArtifactTypes.User).and(
               CoreAttributeTypes.Email, new ArrayList<>(certificatesByEmailAddress.keySet()),
               QueryOption.EXACT_MATCH_OPTIONS).asArtifacts();

         if (emailUsers.isEmpty()) {
            return;
         }

         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON,
            "Write back LDAP email public certificates.");

         for (ArtifactReadable userArt : emailUsers) {
            String email = userArt.getSoleAttributeValue(CoreAttributeTypes.Email, "");
            String certificatePem = certificatesByEmailAddress.get(email.toLowerCase());

            if (!Strings.isValid(certificatePem)) {
               continue;
            }

            try {
               X509Certificate cert = EmailCertificateValidator.parseAndCheckBasicValidity(certificatePem);
               EmailCertificateValidator.checkSuitableForEmail(cert);

               // Delete is done to lessen the likelihood of duplicate attributes being added in high latency scenarios.
               tx.deleteAttributes(userArt.getArtifactId(), CoreAttributeTypes.EmailPublicCertificate);
               tx.setSoleAttributeFromString(userArt.getArtifactId(), CoreAttributeTypes.EmailPublicCertificate,
                  certificatePem);
            } catch (Exception ex) {
               // Skip invalid certificates
            }
         }

         tx.commit();
      } catch (Exception ex) {
         // do nothing
      }
   }

   private boolean hasValidCertificate(String certificatePem) {
      if (!Strings.isValid(certificatePem)) {
         return false;
      }
      try {
         X509Certificate cert = EmailCertificateValidator.parseAndCheckBasicValidity(certificatePem);
         EmailCertificateValidator.checkSuitableForEmail(cert);
         return true;
      } catch (Exception ex) {
         return false;
      }
   }
}
