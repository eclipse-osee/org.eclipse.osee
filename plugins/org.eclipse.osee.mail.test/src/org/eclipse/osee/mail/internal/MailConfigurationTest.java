/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.mail.internal;

import static org.eclipse.osee.mail.internal.MailConstants.DEFAULT_MAIL_SERVER_ADMIN_EMAIL;
import static org.eclipse.osee.mail.internal.MailConstants.DEFAULT_MAIL_SERVER_HOST;
import static org.eclipse.osee.mail.internal.MailConstants.DEFAULT_MAIL_SERVER_PASSWORD;
import static org.eclipse.osee.mail.internal.MailConstants.DEFAULT_MAIL_SERVER_PORT;
import static org.eclipse.osee.mail.internal.MailConstants.DEFAULT_MAIL_SERVER_STATUS_WAIT_TIME_MILLIS;
import static org.eclipse.osee.mail.internal.MailConstants.DEFAULT_MAIL_SERVER_TEST_EMAIL_BODY;
import static org.eclipse.osee.mail.internal.MailConstants.DEFAULT_MAIL_SERVER_TEST_EMAIL_SUBJECT;
import static org.eclipse.osee.mail.internal.MailConstants.DEFAULT_MAIL_SERVER_TRANSPORT;
import static org.eclipse.osee.mail.internal.MailConstants.DEFAULT_MAIL_SERVER_USERNAME;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_ADMIN_EMAIL;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_HOST;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_PASSWORD;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_PORT;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_STATUS_WAIT_TIME_MILLIS;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_TEST_EMAIL_BODY;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_TEST_EMAIL_SUBJECT;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_TRANSPORT;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_USERNAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.mail.internal.MailConfiguration.MailConfigurationBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link MailConfiguration}
 * 
 * @author Roberto E. Escobar
 */
public class MailConfigurationTest {

   private static final String USERNAME = "mail-username";
   private static final String PASSWORD = "mail-password";
   private static final String HOST = "mail-server-host";
   private static final int PORT = 21231;
   private static final String TRANSPORT = "other-transport";
   private static final String ADMIN_EMAIL = "admin@admin.com";
   private static final String TEST_EMAIL_SUBJECT = "test-mail-subjet";
   private static final String TEST_EMAIL_BODY = "test mail body";
   private static final long STATUS_WAIT_TIME = 129817307L;

   private MailConfigurationBuilder builder;

   @Before
   public void testSetup() {
      initMocks(this);

      builder = MailConfiguration.newBuilder();
   }

   @Test
   public void testAdminEmail() {
      builder.adminEmail(ADMIN_EMAIL);

      MailConfiguration actual = builder.build();
      assertEquals(ADMIN_EMAIL, actual.getAdminEmail());
   }

   @Test
   public void testHost() {
      builder.host(HOST);

      MailConfiguration actual = builder.build();
      assertEquals(HOST, actual.getHost());
   }

   @Test
   public void testPassword() {
      builder.password(PASSWORD);

      MailConfiguration actual = builder.build();
      assertEquals(PASSWORD, actual.getPassword());
   }

   @Test
   public void testPort() {
      builder.port(PORT);

      MailConfiguration actual = builder.build();
      assertEquals(PORT, actual.getPort());
   }

   @Test
   public void testEmailSubject() {
      builder.testEmailSubject(TEST_EMAIL_SUBJECT);

      MailConfiguration actual = builder.build();
      assertEquals(TEST_EMAIL_SUBJECT, actual.getTestEmailSubject());
   }

   @Test
   public void testEmailBody() {
      builder.testEmailBody(TEST_EMAIL_BODY);

      MailConfiguration actual = builder.build();
      assertEquals(TEST_EMAIL_BODY, actual.getTestEmailBody());
   }

   @Test
   public void testTransport() {
      builder.transport(TRANSPORT);

      MailConfiguration actual = builder.build();
      assertEquals(TRANSPORT, actual.getTransport());
   }

   @Test
   public void testUserName() {
      builder.username(USERNAME);

      MailConfiguration actual = builder.build();
      assertEquals(USERNAME, actual.getUserName());
   }

   @Test
   public void testStatusWaitTime() {
      builder.statusWaitTime(STATUS_WAIT_TIME);

      MailConfiguration actual = builder.build();
      assertEquals(STATUS_WAIT_TIME, actual.getStatusWaitTime());
   }

   @Test
   public void testDefaultProperties() {
      Map<String, Object> properties = new HashMap<>();
      builder.properties(properties);

      MailConfiguration actual = builder.build();

      assertEquals(DEFAULT_MAIL_SERVER_ADMIN_EMAIL, actual.getAdminEmail());
      assertEquals(DEFAULT_MAIL_SERVER_HOST, actual.getHost());
      assertEquals(DEFAULT_MAIL_SERVER_PASSWORD, actual.getPassword());
      assertEquals(DEFAULT_MAIL_SERVER_PORT, actual.getPort());
      assertEquals(DEFAULT_MAIL_SERVER_TEST_EMAIL_BODY, actual.getTestEmailBody());
      assertEquals(DEFAULT_MAIL_SERVER_TEST_EMAIL_SUBJECT, actual.getTestEmailSubject());
      assertEquals(DEFAULT_MAIL_SERVER_TRANSPORT, actual.getTransport());
      assertEquals(DEFAULT_MAIL_SERVER_USERNAME, actual.getUserName());
      assertEquals(DEFAULT_MAIL_SERVER_STATUS_WAIT_TIME_MILLIS, actual.getStatusWaitTime());
      assertEquals(false, actual.isAuthenticationRequired());
   }

   @Test
   public void testConfigProperties() {
      Map<String, Object> properties = new HashMap<>();
      properties.put(MAIL_SERVER_ADMIN_EMAIL, ADMIN_EMAIL);
      properties.put(MAIL_SERVER_HOST, HOST);
      properties.put(MAIL_SERVER_PASSWORD, PASSWORD);
      properties.put(MAIL_SERVER_PORT, PORT);
      properties.put(MAIL_SERVER_TEST_EMAIL_BODY, TEST_EMAIL_BODY);
      properties.put(MAIL_SERVER_TEST_EMAIL_SUBJECT, TEST_EMAIL_SUBJECT);
      properties.put(MAIL_SERVER_TRANSPORT, TRANSPORT);
      properties.put(MAIL_SERVER_USERNAME, USERNAME);
      properties.put(MAIL_SERVER_STATUS_WAIT_TIME_MILLIS, STATUS_WAIT_TIME);
      builder.properties(properties);

      MailConfiguration actual = builder.build();

      assertEquals(ADMIN_EMAIL, actual.getAdminEmail());
      assertEquals(HOST, actual.getHost());
      assertEquals(PASSWORD, actual.getPassword());
      assertEquals(PORT, actual.getPort());
      assertEquals(TEST_EMAIL_BODY, actual.getTestEmailBody());
      assertEquals(TEST_EMAIL_SUBJECT, actual.getTestEmailSubject());
      assertEquals(TRANSPORT, actual.getTransport());
      assertEquals(USERNAME, actual.getUserName());
      assertEquals(STATUS_WAIT_TIME, actual.getStatusWaitTime());
      assertEquals(true, actual.isAuthenticationRequired());
   }

   @Test
   public void testNoChangeAfterBuild() {
      builder.adminEmail(ADMIN_EMAIL);
      builder.host(HOST);
      builder.password(PASSWORD);
      builder.port(PORT);
      builder.testEmailBody(TEST_EMAIL_BODY);
      builder.testEmailSubject(TEST_EMAIL_SUBJECT);
      builder.transport(TRANSPORT);
      builder.username(USERNAME);
      builder.statusWaitTime(STATUS_WAIT_TIME);

      MailConfiguration actual = builder.build();

      assertEquals(ADMIN_EMAIL, actual.getAdminEmail());
      assertEquals(HOST, actual.getHost());
      assertEquals(PASSWORD, actual.getPassword());
      assertEquals(PORT, actual.getPort());
      assertEquals(TEST_EMAIL_BODY, actual.getTestEmailBody());
      assertEquals(TEST_EMAIL_SUBJECT, actual.getTestEmailSubject());
      assertEquals(TRANSPORT, actual.getTransport());
      assertEquals(USERNAME, actual.getUserName());
      assertEquals(STATUS_WAIT_TIME, actual.getStatusWaitTime());
      assertEquals(true, actual.isAuthenticationRequired());

      builder.properties(Collections.<String, Object> emptyMap());
      MailConfiguration config2 = builder.build();

      assertEquals(DEFAULT_MAIL_SERVER_ADMIN_EMAIL, config2.getAdminEmail());
      assertEquals(DEFAULT_MAIL_SERVER_HOST, config2.getHost());
      assertEquals(DEFAULT_MAIL_SERVER_PASSWORD, config2.getPassword());
      assertEquals(DEFAULT_MAIL_SERVER_PORT, config2.getPort());
      assertEquals(DEFAULT_MAIL_SERVER_TEST_EMAIL_BODY, config2.getTestEmailBody());
      assertEquals(DEFAULT_MAIL_SERVER_TEST_EMAIL_SUBJECT, config2.getTestEmailSubject());
      assertEquals(DEFAULT_MAIL_SERVER_TRANSPORT, config2.getTransport());
      assertEquals(DEFAULT_MAIL_SERVER_USERNAME, config2.getUserName());
      assertEquals(DEFAULT_MAIL_SERVER_STATUS_WAIT_TIME_MILLIS, config2.getStatusWaitTime());
      assertEquals(false, config2.isAuthenticationRequired());

      assertEquals(ADMIN_EMAIL, actual.getAdminEmail());
      assertEquals(HOST, actual.getHost());
      assertEquals(PASSWORD, actual.getPassword());
      assertEquals(PORT, actual.getPort());
      assertEquals(TEST_EMAIL_BODY, actual.getTestEmailBody());
      assertEquals(TEST_EMAIL_SUBJECT, actual.getTestEmailSubject());
      assertEquals(TRANSPORT, actual.getTransport());
      assertEquals(USERNAME, actual.getUserName());
      assertEquals(STATUS_WAIT_TIME, actual.getStatusWaitTime());
      assertEquals(true, actual.isAuthenticationRequired());
   }
}
