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
import java.util.Base64;
import java.util.Date;
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
      Response initial = userEndpoint.getPublicCertificate();
      assertNotNull(initial);
      assertEquals(Response.Status.NO_CONTENT.getStatusCode(), initial.getStatus());

      String pem = generateEmailCertificatePem(validityFromNowMinutes(-1), validityFromNowDays(365));

      userEndpoint.uploadPublicCertificate(pem);

      Response get = userEndpoint.getPublicCertificate();
      assertNotNull(get);
      assertEquals(Response.Status.OK.getStatusCode(), get.getStatus());
      assertEquals(MediaType.TEXT_PLAIN, get.getMediaType().toString());

      String returnedPem;
      try {
         returnedPem = get.readEntity(String.class);
      } finally {
         get.close();
      }

      assertNotNull(returnedPem);
      assertTrue(returnedPem.contains("-----BEGIN CERTIFICATE-----"));
      assertTrue(returnedPem.contains("-----END CERTIFICATE-----"));

      String contentDisposition = get.getHeaderString(HttpHeaders.CONTENT_DISPOSITION);
      assertNotNull(contentDisposition);
      assertTrue(contentDisposition.contains("attachment"));
      assertTrue(contentDisposition.contains("public-cert.pem"));

      userEndpoint.deletePublicCertificate();

      Response afterDelete = userEndpoint.getPublicCertificate();
      assertNotNull(afterDelete);
      assertEquals(Response.Status.NO_CONTENT.getStatusCode(), afterDelete.getStatus());
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

   @SuppressWarnings("unused")
   private static X509Certificate parsePem(String pem) throws Exception {
      String cleaned =
         pem.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "").replaceAll("\\s+", "");

      byte[] der = Base64.getDecoder().decode(cleaned);
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(der));
   }
}
