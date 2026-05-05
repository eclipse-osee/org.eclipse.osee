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

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.data.EmailRecipientInfo;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.EmailCertificateValidator;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.orcs.rest.model.UserEndpoint;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class UserEndpointTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   private static UserEndpoint userEndpoint;

   @BeforeClass
   public static void testSetup() {
      userEndpoint = ServiceUtil.getOseeClient().getOrcsUserEndpoint();
   }

   @Test
   public void uploadGetDeletePublicCertificate_happyPath() throws Exception {
      try (Response initial = userEndpoint.getPublicCertificate()) {
         assertNotNull(initial);
         assertEquals(Response.Status.NO_CONTENT.getStatusCode(), initial.getStatus());
      }
      String pem = generateEmailCertificatePem(validityFromNowMinutes(-1), validityFromNowDays(365));

      userEndpoint.uploadPublicCertificate(pem);

      try (Response get = userEndpoint.getPublicCertificate()) {
         assertNotNull(get);
         assertEquals(Response.Status.OK.getStatusCode(), get.getStatus());
         assertEquals(MediaType.TEXT_PLAIN, get.getMediaType().toString());

         String returnedPem;
         String contentDisposition;

         returnedPem = get.readEntity(String.class);
         contentDisposition = get.getHeaderString(HttpHeaders.CONTENT_DISPOSITION);

         assertNotNull(returnedPem);
         assertTrue(returnedPem.contains("-----BEGIN CERTIFICATE-----"));
         assertTrue(returnedPem.contains("-----END CERTIFICATE-----"));

         assertNotNull(contentDisposition);
         assertTrue(contentDisposition.contains("attachment"));
         assertTrue(contentDisposition.contains("public-cert.pem"));

         userEndpoint.deletePublicCertificate();
      }
      try (Response afterDelete = userEndpoint.getPublicCertificate()) {
         assertNotNull(afterDelete);
         assertEquals(Response.Status.NO_CONTENT.getStatusCode(), afterDelete.getStatus());
      }
   }

   @Test
   public void getPublicCertificatesByEmailAddresses_happyPath() throws Exception {
      UserToken currentUser = ServiceUtil.getOseeClient().userService().getUser();
      assertNotNull(currentUser);
      assertTrue("Current user email must be valid for this test", EmailUtil.isEmailValid(currentUser.getEmail()));

      String pem = generateEmailCertificatePem(validityFromNowMinutes(-1), validityFromNowDays(365));
      userEndpoint.uploadPublicCertificate(pem);

      try {
         Collection<String> emails = java.util.Collections.singleton(currentUser.getEmail());
         List<EmailRecipientInfo> recipientInfos = userEndpoint.getPublicCertificatesByEmailAddresses(emails);

         assertNotNull(recipientInfos);
         assertTrue("Expected at least one EmailRecipientInfo result", !recipientInfos.isEmpty());

         EmailRecipientInfo matchingInfo = null;
         for (EmailRecipientInfo info : recipientInfos) {
            if (info != null && currentUser.getEmail().equalsIgnoreCase(info.getEmail())) {
               matchingInfo = info;
               break;
            }
         }

         assertNotNull("Expected to find matching EmailRecipientInfo for current user email", matchingInfo);
         assertNotNull("Expected public certificate to be returned for current user",
            matchingInfo.getPublicCertificate());
         assertEquals(normalizePem(pem), normalizePem(matchingInfo.getPublicCertificate()));
      } finally {
         userEndpoint.deletePublicCertificate();
      }
   }

   @Test
   public void getPublicCertificatesByEmailAddresses_multipleUsers() throws Exception {
      Artifact keith = getUserArtifact(DemoUsers.Keith_Johnson);
      Artifact jason = getUserArtifact(DemoUsers.Jason_Stevens);
      Artifact janice = getUserArtifact(DemoUsers.Janice_Michael);

      String keithEmail = keith.getSoleAttributeValue(CoreAttributeTypes.Email, "");
      String jasonEmail = jason.getSoleAttributeValue(CoreAttributeTypes.Email, "");
      String janiceEmail = janice.getSoleAttributeValue(CoreAttributeTypes.Email, "");

      assertTrue(EmailUtil.isEmailValid(keithEmail));
      assertTrue(EmailUtil.isEmailValid(jasonEmail));
      assertTrue(EmailUtil.isEmailValid(janiceEmail));

      String keithPem = generateEmailCertificatePem(validityFromNowMinutes(-1), validityFromNowDays(365));
      String jasonPem = generateEmailCertificatePem(validityFromNowMinutes(-1), validityFromNowDays(365));
      String janicePem = generateEmailCertificatePem(validityFromNowMinutes(-1), validityFromNowDays(365));

      validatePemForStorage(keithPem);
      validatePemForStorage(jasonPem);
      validatePemForStorage(janicePem);

      Map<Artifact, String> expectedByArtifact = new HashMap<>();
      expectedByArtifact.put(keith, keithPem);
      expectedByArtifact.put(jason, jasonPem);
      expectedByArtifact.put(janice, janicePem);

      storeCertificates(expectedByArtifact);

      try {
         Collection<String> emails = Arrays.asList(keithEmail, jasonEmail, janiceEmail);
         List<EmailRecipientInfo> recipientInfos = userEndpoint.getPublicCertificatesByEmailAddresses(emails);

         assertNotNull(recipientInfos);
         assertTrue("Expected at least three EmailRecipientInfo results", recipientInfos.size() >= 3);

         Map<String, String> actualByEmailLower = new HashMap<>();
         for (EmailRecipientInfo info : recipientInfos) {
            if (info != null && info.getEmail() != null) {
               actualByEmailLower.put(info.getEmail().toLowerCase(), normalizePem(info.getPublicCertificate()));
            }
         }

         assertEquals(normalizePem(keithPem), actualByEmailLower.get(keithEmail.toLowerCase()));
         assertEquals(normalizePem(jasonPem), actualByEmailLower.get(jasonEmail.toLowerCase()));
         assertEquals(normalizePem(janicePem), actualByEmailLower.get(janiceEmail.toLowerCase()));
      } finally {
         deleteCertificates(keith, jason, janice);
      }
   }

   @Test
   public void getPublicCertificatesByEmailAddresses_multipleUsers_noCertificates() throws Exception {
      Artifact keith = getUserArtifact(DemoUsers.Keith_Johnson);
      Artifact jason = getUserArtifact(DemoUsers.Jason_Stevens);

      String keithEmail = keith.getSoleAttributeValue(CoreAttributeTypes.Email, "");
      String jasonEmail = jason.getSoleAttributeValue(CoreAttributeTypes.Email, "");

      assertTrue(EmailUtil.isEmailValid(keithEmail));
      assertTrue(EmailUtil.isEmailValid(jasonEmail));

      deleteCertificates(keith, jason);

      Collection<String> emails = Arrays.asList(keithEmail, jasonEmail);
      List<EmailRecipientInfo> recipientInfos = userEndpoint.getPublicCertificatesByEmailAddresses(emails);

      assertNotNull(recipientInfos);
      assertTrue("Expected at least two EmailRecipientInfo results", recipientInfos.size() >= 2);

      Map<String, EmailRecipientInfo> byEmailLower = new HashMap<>();
      for (EmailRecipientInfo info : recipientInfos) {
         if (info != null && info.getEmail() != null) {
            byEmailLower.put(info.getEmail().toLowerCase(), info);
         }
      }

      assertTrue(byEmailLower.containsKey(keithEmail.toLowerCase()));
      assertTrue(byEmailLower.containsKey(jasonEmail.toLowerCase()));

      assertEquals(null, byEmailLower.get(keithEmail.toLowerCase()).getPublicCertificate());
      assertEquals(null, byEmailLower.get(jasonEmail.toLowerCase()).getPublicCertificate());
   }

   @Test
   public void uploadPublicCertificate_rejectsInvalidPem() {
      assertUploadThrows("not a certificate");
   }

   @Test
   public void uploadPublicCertificate_rejectsExpiredCertificate() throws Exception {
      Date notBefore = validityFromNowDays(-10);
      Date notAfter = validityFromNowDays(-1);

      String pem = generateEmailCertificatePem(notBefore, notAfter);
      assertUploadThrows(pem);
   }

   @Test
   public void uploadPublicCertificate_rejectsNotYetValidCertificate() throws Exception {
      Date notBefore = validityFromNowDays(1);
      Date notAfter = validityFromNowDays(365);

      String pem = generateEmailCertificatePem(notBefore, notAfter);
      assertUploadThrows(pem);
   }

   private static void assertUploadThrows(String pem) {
      boolean threw = false;
      try {
         userEndpoint.uploadPublicCertificate(pem);
      } catch (RuntimeException ex) {
         threw = true;
      }
      assertTrue("Expected uploadPublicCertificate to throw", threw);
   }

   private static Artifact getUserArtifact(UserToken user) {
      return ArtifactQuery.getArtifactFromId(user, CoreBranches.COMMON);
   }

   private static void storeCertificates(Map<Artifact, String> certificatesByArtifact) throws Exception {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(CoreBranches.COMMON, UserEndpointTest.class.getSimpleName());

      for (Map.Entry<Artifact, String> entry : certificatesByArtifact.entrySet()) {
         Artifact userArt = entry.getKey();
         String certificatePem = entry.getValue();
         userArt.setSoleAttributeValue(CoreAttributeTypes.EmailPublicCertificate, certificatePem);
         userArt.persist(transaction);
      }

      transaction.execute();
   }

   private static void deleteCertificates(Artifact... users) throws Exception {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(CoreBranches.COMMON, UserEndpointTest.class.getSimpleName());

      for (Artifact userArt : users) {
         userArt.deleteAttributes(CoreAttributeTypes.EmailPublicCertificate);
         userArt.persist(transaction);
      }

      transaction.execute();
   }

   private static void validatePemForStorage(String certificatePem) {
      X509Certificate cert = EmailCertificateValidator.parseAndCheckBasicValidity(certificatePem);
      EmailCertificateValidator.checkSuitableForEmail(cert);
   }

   private static Date validityFromNowDays(long days) {
      return Date.from(Instant.now().plus(days, ChronoUnit.DAYS));
   }

   private static Date validityFromNowMinutes(long minutes) {
      return Date.from(Instant.now().plus(minutes, ChronoUnit.MINUTES));
   }

   private static String generateEmailCertificatePem(Date notBefore, Date notAfter) throws Exception {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(2048);
      KeyPair keyPair = kpg.generateKeyPair();

      X500Name dn = new X500Name("CN=OSEE Test Email Cert");

      BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

      X509v3CertificateBuilder builder =
         new JcaX509v3CertificateBuilder(dn, serial, notBefore, notAfter, dn, keyPair.getPublic());

      builder.addExtension(Extension.keyUsage, true,
         new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

      builder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.id_kp_emailProtection));

      ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());

      X509CertificateHolder holder = builder.build(signer);

      X509Certificate cert = new JcaX509CertificateConverter().getCertificate(holder);

      return toPem(cert);
   }

   private static String toPem(X509Certificate cert) throws Exception {
      byte[] der = cert.getEncoded();
      String base64 = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(der);
      return "-----BEGIN CERTIFICATE-----\n" + base64 + "\n-----END CERTIFICATE-----\n";
   }

   private static String normalizePem(String pem) {
      return pem == null ? null : pem.replace("\r\n", "\n").trim();
   }

   @SuppressWarnings("unused")
   private static X509Certificate parsePem(String pem) throws Exception {
      String cleaned =
         pem.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "").replaceAll("\\s+", "");

      byte[] der = Base64.getDecoder().decode(cleaned);
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(der));
   }
}
