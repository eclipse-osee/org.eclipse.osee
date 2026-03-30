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

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.eclipse.osee.framework.core.util.EmailCertificateValidator;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public final class EmailCertificateLdapLookup {

   private static final int LDAP_FILTER_BATCH_SIZE = 50;
   private static final String CERTIFICATE_ATTRIBUTE = "userCertificate;binary";
   private static final String MAIL_ATTRIBUTE = "mail";

   private EmailCertificateLdapLookup() {
   }

   /**
    * Queries LDAP for the newest valid email encryption certificate for the supplied email addresses. The lookup is
    * performed in batches using a single OR filter per batch for efficiency.
    *
    * @param ldapUrl LDAP provider URL from preferences
    * @param emailAddresses email addresses to query
    * @return map keyed by lowercase email address with a PEM encoded public certificate value
    */
   public static Map<String, String> getNewestEmailEncryptionCerts(String ldapUrl, Collection<String> emailAddresses) {
      if (!Strings.isValid(ldapUrl) || emailAddresses == null || emailAddresses.isEmpty()) {
         return Collections.emptyMap();
      }

      List<String> normalizedEmails = new ArrayList<>();
      for (String email : emailAddresses) {
         if (Strings.isValid(email)) {
            normalizedEmails.add(email);
         }
      }

      if (normalizedEmails.isEmpty()) {
         return Collections.emptyMap();
      }

      Map<String, X509Certificate> newestCertByEmailLower = new HashMap<>();
      DirContext ctx = null;
      try {
         ctx = new InitialDirContext(createEnvironment(ldapUrl));
         CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

         for (int i = 0; i < normalizedEmails.size(); i += LDAP_FILTER_BATCH_SIZE) {
            List<String> batch =
               normalizedEmails.subList(i, Math.min(i + LDAP_FILTER_BATCH_SIZE, normalizedEmails.size()));
            queryBatch(ctx, certFactory, batch, newestCertByEmailLower);
         }

      } catch (Exception ex) {
         return Collections.emptyMap();
      } finally {
         closeContext(ctx);
      }

      Map<String, String> pemByEmailLower = new HashMap<>();
      for (Map.Entry<String, X509Certificate> entry : newestCertByEmailLower.entrySet()) {
         pemByEmailLower.put(entry.getKey(), toPem(entry.getValue()));
      }
      return pemByEmailLower;
   }

   private static Hashtable<String, String> createEnvironment(String ldapUrl) {
      Hashtable<String, String> env = new Hashtable<>();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      env.put(Context.PROVIDER_URL, ldapUrl.trim());
      env.put("com.sun.jndi.ldap.connect.timeout", "5000");
      env.put("com.sun.jndi.ldap.read.timeout", "5000");
      return env;
   }

   private static void queryBatch(DirContext ctx, CertificateFactory certFactory, List<String> batch,
      Map<String, X509Certificate> newestCertByEmailLower) throws Exception {
      SearchControls controls = new SearchControls();
      controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      controls.setReturningAttributes(new String[] {MAIL_ATTRIBUTE, CERTIFICATE_ATTRIBUTE});

      String filter = createBatchMailFilter(batch);
      NamingEnumeration<SearchResult> results = null;
      try {
         results = ctx.search("", filter, controls);

         while (results != null && results.hasMore()) {
            SearchResult result = results.next();
            Attributes attributes = result.getAttributes();
            if (attributes == null) {
               continue;
            }

            List<String> entryEmails = getEmails(attributes.get(MAIL_ATTRIBUTE));
            X509Certificate newestEntryCert =
               getNewestValidEncryptionCertificate(attributes.get(CERTIFICATE_ATTRIBUTE), certFactory);

            if (newestEntryCert == null || entryEmails.isEmpty()) {
               continue;
            }

            for (String email : entryEmails) {
               String emailLower = email.toLowerCase();
               X509Certificate existing = newestCertByEmailLower.get(emailLower);
               if (existing == null || newestEntryCert.getNotBefore().after(existing.getNotBefore())) {
                  newestCertByEmailLower.put(emailLower, newestEntryCert);
               }
            }
         }
      } finally {
         if (results != null) {
            try {
               results.close();
            } catch (NamingException ex) {
               // do nothing
            }
         }
      }
   }

   private static List<String> getEmails(Attribute mailAttribute) throws NamingException {
      if (mailAttribute == null) {
         return Collections.emptyList();
      }

      List<String> emails = new ArrayList<>();
      NamingEnumeration<?> values = null;
      try {
         values = mailAttribute.getAll();
         while (values.hasMore()) {
            Object value = values.next();
            if (value instanceof String && Strings.isValid((String) value)) {
               emails.add((String) value);
            }
         }
      } finally {
         if (values != null) {
            values.close();
         }
      }
      return emails;
   }

   private static X509Certificate getNewestValidEncryptionCertificate(Attribute certificateAttribute,
      CertificateFactory certFactory) throws Exception {
      if (certificateAttribute == null) {
         return null;
      }

      X509Certificate newest = null;
      NamingEnumeration<?> values = null;
      try {
         values = certificateAttribute.getAll();
         while (values.hasMore()) {
            Object value = values.next();
            if (!(value instanceof byte[])) {
               continue;
            }

            try {
               X509Certificate cert =
                  (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream((byte[]) value));
               cert.checkValidity();
               EmailCertificateValidator.checkSuitableForEmail(cert);

               if (newest == null || cert.getNotBefore().after(newest.getNotBefore())) {
                  newest = cert;
               }
            } catch (Exception ex) {
               // ignore invalid certificates and keep looking
            }
         }
      } finally {
         if (values != null) {
            values.close();
         }
      }
      return newest;
   }

   private static String createBatchMailFilter(List<String> emails) {
      if (emails.size() == 1) {
         return "(" + MAIL_ATTRIBUTE + "=" + escapeLdapFilterValue(emails.get(0)) + ")";
      }

      StringBuilder filter = new StringBuilder("(|");
      for (String email : emails) {
         filter.append("(").append(MAIL_ATTRIBUTE).append("=").append(escapeLdapFilterValue(email)).append(")");
      }
      filter.append(")");
      return filter.toString();
   }

   private static String escapeLdapFilterValue(String value) {
      StringBuilder escaped = new StringBuilder();
      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);
         switch (c) {
            case '\\':
               escaped.append("\\5c");
               break;
            case '*':
               escaped.append("\\2a");
               break;
            case '(':
               escaped.append("\\28");
               break;
            case ')':
               escaped.append("\\29");
               break;
            case '\0':
               escaped.append("\\00");
               break;
            default:
               escaped.append(c);
               break;
         }
      }
      return escaped.toString();
   }

   private static String toPem(X509Certificate certificate) {
      try {
         String base64 = Base64.getMimeEncoder(64, new byte[] {'\n'}).encodeToString(certificate.getEncoded());
         return "-----BEGIN CERTIFICATE-----\n" + base64 + "\n-----END CERTIFICATE-----";
      } catch (Exception ex) {
         return "";
      }
   }

   private static void closeContext(DirContext ctx) {
      if (ctx != null) {
         try {
            ctx.close();
         } catch (NamingException ex) {
            // do nothing
         }
      }
   }
}
