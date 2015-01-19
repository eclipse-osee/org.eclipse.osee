/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.http.jetty;

import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__CONTEXT_PATH;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__CONTEXT_SESSION_INACTIVE_INTERVAL;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTPS_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTPS_HOST;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTPS_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTPS_USE_RANDOM_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_HOST;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_NIO_AUTO_DETECT;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_NIO_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__HTTP_USE_RANDOM_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__MULTIPLE_SLASH_TO_SINGLE;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__OTHER_INFO;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_KEYPASSWORD;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_KEYSTORE;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_KEYSTORETYPE;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_NEEDS_CLIENT_AUTH;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_PASSWORD;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_PROTOCOL;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__SSL_WANTS_CLIENT_AUTH;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY__WORKING_DIRECTORY;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__CONTEXT_PATH;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__CONTEXT_SESSION_INACTIVE_INTERVAL;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTPS_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTPS_HOST;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTPS_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTPS_USE_RANDOM_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_HOST;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_NIO_AUTO_DETECT;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_NIO_ENABLED;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_USE_RANDOM_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__MULTIPLE_SLASH_TO_SINGLE;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__OTHER_INFO;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_KEYPASSWORD;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_KEYSTORE;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_KEYSTORETYPE;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_NEEDS_CLIENT_AUTH;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_PASSWORD;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_PROTOCOL;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__SSL_WANTS_CLIENT_AUTH;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__WORKING_DIRECTORY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.osee.http.jetty.JettyServer.Builder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link JettyServer}
 * 
 * @author Roberto E. Escobar
 */
public class JettyServerBuilderTest {

   private static final String CONTEXT = "mycontext";
   private static final int SESSION_INACTIVE_INTERVAL = 10000;

   private static final boolean HTTP_NIO_AUTO_DETECT = true;
   private static final boolean HTTP_NIO_ENABLED = true;
   private static final boolean HTTP_ENABLED = true;
   private static final String HTTP_HOST = "http-host";
   private static final int HTTP_PORT = 9876;

   private static final boolean HTTPS_ENABLED = true;
   private static final String HTTPS_HOST = "https-host";
   private static final int HTTPS_PORT = 5432;

   private static final String OTHER_INFO = "other-info";
   private static final String SSL_KEYPASSWORD = "ssl-key-password";
   private static final String SSL_KEYSTORE = "ssl-keystore";
   private static final String SSL_KEYSTORETYPE = "ssl-keystore-type";
   private static final String SSL_PASSWORD = "ssl-password";
   private static final String SSL_PROTOCOL = "ssl-protocol";
   private static final boolean SSL_NEEDS_CLIENT_AUTH = true;
   private static final boolean SSL_WANTS_CLIENT_AUTH = true;
   private static final String WORKING_DIRECTORY = "my-working-dir";

   //@formatter:off
   @Mock private JettySessionManagerFactory sessionFactory;
   @Mock private SessionManager sessionManager;
   
   //@formatter:on

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private Builder builder;

   @Before
   public void testSetup() {
      MockitoAnnotations.initMocks(this);

      builder = JettyServer.newBuilder();
      when(sessionFactory.newSessionManager(//
         any(JettyLogger.class), //
         any(Server.class), //
         anyMapOf(String.class, Object.class) //
      )).thenReturn(sessionManager);
   }

   @Test
   public void testDefaults() {
      JettyConfig config = builder;

      assertEquals(DEFAULT_JETTY__CONTEXT_PATH, config.getContextPath());
      assertEquals(DEFAULT_JETTY__CONTEXT_SESSION_INACTIVE_INTERVAL, config.getContextSessioninactiveinterval());
      assertEquals(DEFAULT_JETTY__HTTP_HOST, config.getHttpHost());
      assertEquals(DEFAULT_JETTY__HTTP_PORT, config.getHttpPort());
      assertEquals(DEFAULT_JETTY__HTTPS_HOST, config.getHttpsHost());
      assertEquals(DEFAULT_JETTY__HTTPS_PORT, config.getHttpsPort());
      assertEquals(DEFAULT_JETTY__OTHER_INFO, config.getOtherInfo());
      assertEquals(DEFAULT_JETTY__SSL_KEYPASSWORD, config.getSslKeypassword());
      assertEquals(DEFAULT_JETTY__SSL_KEYSTORE, config.getSslKeystore());
      assertEquals(DEFAULT_JETTY__SSL_KEYSTORETYPE, config.getSslKeystoretype());
      assertEquals(DEFAULT_JETTY__SSL_PASSWORD, config.getSslPassword());
      assertEquals(DEFAULT_JETTY__SSL_PROTOCOL, config.getSslProtocol());
      assertEquals(DEFAULT_JETTY__WORKING_DIRECTORY, config.getWorkingDirectory());

      assertEquals(DEFAULT_JETTY__HTTP_NIO_AUTO_DETECT, config.isAutoDetectNioSupport());
      assertEquals(DEFAULT_JETTY__HTTP_ENABLED, config.isHttpEnabled());
      assertEquals(DEFAULT_JETTY__HTTPS_ENABLED, config.isHttpsEnabled());
      assertEquals(DEFAULT_JETTY__HTTP_NIO_ENABLED, config.isNonBlockinIoEnabled());
      assertEquals(DEFAULT_JETTY__HTTP_USE_RANDOM_PORT, config.isRandomHttpPort());
      assertEquals(DEFAULT_JETTY__HTTPS_USE_RANDOM_PORT, config.isRandomHttpsPort());
      assertEquals(DEFAULT_JETTY__SSL_NEEDS_CLIENT_AUTH, config.isSslNeedClientAuth());
      assertEquals(DEFAULT_JETTY__SSL_WANTS_CLIENT_AUTH, config.isSslWantClientAuth());
      assertEquals(DEFAULT_JETTY__MULTIPLE_SLASH_TO_SINGLE, config.isMultipleSlashToSingle());
      assertTrue(config.getOtherProps().isEmpty());
   }

   @Test
   public void testFields() {
      builder.contextPath(CONTEXT);
      builder.contextSessionInactiveInterval(SESSION_INACTIVE_INTERVAL);
      builder.autoDetectNioSupport(HTTP_NIO_AUTO_DETECT);
      builder.nonBlockinIoEnabled(HTTP_NIO_ENABLED);
      builder.httpEnabled(HTTP_ENABLED);
      builder.httpHost(HTTP_HOST);
      builder.httpPort(HTTP_PORT);
      builder.httpsEnabled(HTTPS_ENABLED);
      builder.httpsHost(HTTPS_HOST);
      builder.httpsPort(HTTPS_PORT);
      builder.otherInfo(OTHER_INFO);
      builder.sslKeypassword(SSL_KEYPASSWORD);
      builder.sslKeystore(SSL_KEYSTORE);
      builder.sslKeystoretype(SSL_KEYSTORETYPE);
      builder.sslNeedClientAuth(SSL_NEEDS_CLIENT_AUTH);
      builder.sslPassword(SSL_PASSWORD);
      builder.sslProtocol(SSL_PROTOCOL);
      builder.sslWantClientAuth(SSL_WANTS_CLIENT_AUTH);
      builder.useRandomHttpPort(false);
      builder.useRandomHttpsPort(false);
      builder.workingDirectory(WORKING_DIRECTORY);
      builder.sessionManagerFactory(sessionFactory);
      builder.extraParams(map("a", 1, "b", 2));
      builder.logging(true);
      builder.replaceMultipleSlashesWithSingle(false);

      JettyServer actual = builder.build();
      JettyConfig config = actual.getConfig();

      assertEquals(CONTEXT, config.getContextPath());
      assertEquals(SESSION_INACTIVE_INTERVAL, config.getContextSessioninactiveinterval());
      assertEquals(HTTP_HOST, config.getHttpHost());
      assertEquals(HTTP_PORT, config.getHttpPort());
      assertEquals(HTTPS_HOST, config.getHttpsHost());
      assertEquals(HTTPS_PORT, config.getHttpsPort());
      assertEquals(OTHER_INFO, config.getOtherInfo());
      assertEquals(SSL_KEYPASSWORD, config.getSslKeypassword());
      assertEquals(SSL_KEYSTORE, config.getSslKeystore());
      assertEquals(SSL_KEYSTORETYPE, config.getSslKeystoretype());
      assertEquals(SSL_PASSWORD, config.getSslPassword());
      assertEquals(SSL_PROTOCOL, config.getSslProtocol());

      assertEquals(HTTP_NIO_AUTO_DETECT, config.isAutoDetectNioSupport());
      assertEquals(HTTP_NIO_ENABLED, config.isNonBlockinIoEnabled());
      assertEquals(HTTP_ENABLED, config.isHttpEnabled());
      assertEquals(HTTPS_ENABLED, config.isHttpsEnabled());
      assertEquals(false, config.isRandomHttpPort());
      assertEquals(false, config.isRandomHttpsPort());
      assertEquals(SSL_NEEDS_CLIENT_AUTH, config.isSslNeedClientAuth());
      assertEquals(SSL_WANTS_CLIENT_AUTH, config.isSslWantClientAuth());
      assertEquals(WORKING_DIRECTORY, config.getWorkingDirectory());
      assertEquals(false, config.isMultipleSlashToSingle());

      verify(sessionManager).setMaxInactiveInterval(config.getContextSessioninactiveinterval());
      assertMapEquals(config.getOtherProps(), "a", 1, "b", 2);

      builder.useRandomHttpPort(true);
      builder.useRandomHttpsPort(true);
      builder.replaceMultipleSlashesWithSingle(true);

      actual = builder.build();
      config = actual.getConfig();

      int httpPort = config.getHttpPort();
      assertTrue("HttpPort- " + httpPort, HTTP_PORT != httpPort && httpPort != DEFAULT_JETTY__HTTP_PORT && httpPort > 0);

      int httpsPort = config.getHttpsPort();
      assertTrue("HttpsPort- " + httpsPort,
         HTTPS_PORT != httpsPort && httpsPort != DEFAULT_JETTY__HTTPS_PORT && httpsPort > 0);
      assertEquals(true, config.isRandomHttpPort());
      assertEquals(true, config.isRandomHttpsPort());
      assertEquals(true, config.isMultipleSlashToSingle());
   }

   @Test
   public void testConfigProperties() {
      Map<String, Object> props = new HashMap<String, Object>();

      add(props, JETTY__HTTP_NIO_ENABLED, HTTP_NIO_ENABLED);
      add(props, JETTY__HTTP_NIO_AUTO_DETECT, HTTP_NIO_AUTO_DETECT);
      add(props, JETTY__HTTP_ENABLED, HTTP_ENABLED);
      add(props, JETTY__HTTP_PORT, HTTP_PORT);
      add(props, JETTY__HTTP_HOST, HTTP_HOST);

      add(props, JETTY__HTTPS_ENABLED, HTTPS_ENABLED);
      add(props, JETTY__HTTPS_HOST, HTTPS_HOST);
      add(props, JETTY__HTTPS_PORT, HTTPS_PORT);

      add(props, JETTY__SSL_PROTOCOL, SSL_PROTOCOL);
      add(props, JETTY__SSL_KEYSTORE, SSL_KEYSTORE);
      add(props, JETTY__SSL_KEYSTORETYPE, SSL_KEYSTORETYPE);
      add(props, JETTY__SSL_PASSWORD, SSL_PASSWORD);
      add(props, JETTY__SSL_KEYPASSWORD, SSL_KEYPASSWORD);
      add(props, JETTY__SSL_NEEDS_CLIENT_AUTH, SSL_NEEDS_CLIENT_AUTH);
      add(props, JETTY__SSL_WANTS_CLIENT_AUTH, SSL_WANTS_CLIENT_AUTH);
      add(props, JETTY__CONTEXT_SESSION_INACTIVE_INTERVAL, SESSION_INACTIVE_INTERVAL);
      add(props, JETTY__CONTEXT_PATH, CONTEXT);
      add(props, JETTY__OTHER_INFO, OTHER_INFO);
      add(props, JETTY__WORKING_DIRECTORY, WORKING_DIRECTORY);
      add(props, JETTY__MULTIPLE_SLASH_TO_SINGLE, false);

      add(props, "a", 1);
      add(props, "b", 2);

      builder.properties(props);

      builder.logging(true);
      builder.sessionManagerFactory(sessionFactory);

      JettyServer actual = builder.build();
      JettyConfig config = actual.getConfig();

      assertEquals(CONTEXT, config.getContextPath());
      assertEquals(SESSION_INACTIVE_INTERVAL, config.getContextSessioninactiveinterval());
      assertEquals(HTTP_HOST, config.getHttpHost());
      assertEquals(HTTP_PORT, config.getHttpPort());
      assertEquals(HTTPS_HOST, config.getHttpsHost());
      assertEquals(HTTPS_PORT, config.getHttpsPort());
      assertEquals(OTHER_INFO, config.getOtherInfo());
      assertEquals(SSL_KEYPASSWORD, config.getSslKeypassword());
      assertEquals(SSL_KEYSTORE, config.getSslKeystore());
      assertEquals(SSL_KEYSTORETYPE, config.getSslKeystoretype());
      assertEquals(SSL_PASSWORD, config.getSslPassword());
      assertEquals(SSL_PROTOCOL, config.getSslProtocol());

      assertEquals(HTTP_NIO_AUTO_DETECT, config.isAutoDetectNioSupport());
      assertEquals(HTTP_NIO_ENABLED, config.isNonBlockinIoEnabled());
      assertEquals(HTTP_ENABLED, config.isHttpEnabled());
      assertEquals(HTTPS_ENABLED, config.isHttpsEnabled());
      assertEquals(false, config.isRandomHttpPort());
      assertEquals(false, config.isRandomHttpsPort());
      assertEquals(SSL_NEEDS_CLIENT_AUTH, config.isSslNeedClientAuth());
      assertEquals(SSL_WANTS_CLIENT_AUTH, config.isSslWantClientAuth());
      assertEquals(WORKING_DIRECTORY, config.getWorkingDirectory());
      assertEquals(false, config.isMultipleSlashToSingle());

      verify(sessionManager).setMaxInactiveInterval(config.getContextSessioninactiveinterval());
      assertMapEquals(config.getOtherProps(), "a", "1", "b", "2");

      props.remove(HTTP_PORT);
      add(props, JETTY__HTTP_USE_RANDOM_PORT, true);

      props.remove(HTTPS_PORT);
      add(props, JETTY__HTTPS_USE_RANDOM_PORT, true);

      add(props, JETTY__MULTIPLE_SLASH_TO_SINGLE, true);

      builder.properties(props);

      actual = builder.build();
      config = actual.getConfig();

      int httpPort = config.getHttpPort();
      assertTrue("HttpPort- " + httpPort, HTTP_PORT != httpPort && httpPort != DEFAULT_JETTY__HTTP_PORT && httpPort > 0);

      int httpsPort = config.getHttpsPort();
      assertTrue("HttpsPort- " + httpsPort,
         HTTPS_PORT != httpsPort && httpsPort != DEFAULT_JETTY__HTTPS_PORT && httpsPort > 0);
      assertEquals(true, config.isRandomHttpPort());
      assertEquals(true, config.isRandomHttpsPort());
      assertEquals(true, config.isMultipleSlashToSingle());
   }

   @Test
   public void testNoChangeAfterBuild() {
      builder.contextPath(CONTEXT);
      builder.contextSessionInactiveInterval(SESSION_INACTIVE_INTERVAL);
      builder.autoDetectNioSupport(HTTP_NIO_AUTO_DETECT);
      builder.nonBlockinIoEnabled(HTTP_NIO_ENABLED);
      builder.httpEnabled(HTTP_ENABLED);
      builder.httpHost(HTTP_HOST);
      builder.httpPort(HTTP_PORT);
      builder.httpsEnabled(HTTPS_ENABLED);
      builder.httpsHost(HTTPS_HOST);
      builder.httpsPort(HTTPS_PORT);
      builder.otherInfo(OTHER_INFO);
      builder.sslKeypassword(SSL_KEYPASSWORD);
      builder.sslKeystore(SSL_KEYSTORE);
      builder.sslKeystoretype(SSL_KEYSTORETYPE);
      builder.sslNeedClientAuth(SSL_NEEDS_CLIENT_AUTH);
      builder.sslPassword(SSL_PASSWORD);
      builder.sslProtocol(SSL_PROTOCOL);
      builder.sslWantClientAuth(SSL_WANTS_CLIENT_AUTH);
      builder.useRandomHttpPort(false);
      builder.useRandomHttpsPort(false);
      builder.workingDirectory(WORKING_DIRECTORY);
      builder.sessionManagerFactory(sessionFactory);
      builder.extraParams(map("a", 1, "b", 2));
      builder.logging(true);
      builder.replaceMultipleSlashesWithSingle(false);

      JettyServer actual = builder.build();
      JettyConfig config = actual.getConfig();

      assertEquals(CONTEXT, config.getContextPath());
      assertEquals(SESSION_INACTIVE_INTERVAL, config.getContextSessioninactiveinterval());
      assertEquals(HTTP_HOST, config.getHttpHost());
      assertEquals(HTTP_PORT, config.getHttpPort());
      assertEquals(HTTPS_HOST, config.getHttpsHost());
      assertEquals(HTTPS_PORT, config.getHttpsPort());
      assertEquals(OTHER_INFO, config.getOtherInfo());
      assertEquals(SSL_KEYPASSWORD, config.getSslKeypassword());
      assertEquals(SSL_KEYSTORE, config.getSslKeystore());
      assertEquals(SSL_KEYSTORETYPE, config.getSslKeystoretype());
      assertEquals(SSL_PASSWORD, config.getSslPassword());
      assertEquals(SSL_PROTOCOL, config.getSslProtocol());

      assertEquals(HTTP_NIO_AUTO_DETECT, config.isAutoDetectNioSupport());
      assertEquals(HTTP_NIO_ENABLED, config.isNonBlockinIoEnabled());
      assertEquals(HTTP_ENABLED, config.isHttpEnabled());
      assertEquals(HTTPS_ENABLED, config.isHttpsEnabled());
      assertEquals(false, config.isRandomHttpPort());
      assertEquals(false, config.isRandomHttpsPort());
      assertEquals(SSL_NEEDS_CLIENT_AUTH, config.isSslNeedClientAuth());
      assertEquals(SSL_WANTS_CLIENT_AUTH, config.isSslWantClientAuth());
      assertEquals(WORKING_DIRECTORY, config.getWorkingDirectory());
      assertEquals(false, config.isMultipleSlashToSingle());

      verify(sessionManager).setMaxInactiveInterval(config.getContextSessioninactiveinterval());
      assertMapEquals(config.getOtherProps(), "a", 1, "b", 2);

      builder.readProperties(Collections.<String, Object> emptyMap());

      assertEquals(CONTEXT, config.getContextPath());
      assertEquals(SESSION_INACTIVE_INTERVAL, config.getContextSessioninactiveinterval());
      assertEquals(HTTP_HOST, config.getHttpHost());
      assertEquals(HTTP_PORT, config.getHttpPort());
      assertEquals(HTTPS_HOST, config.getHttpsHost());
      assertEquals(HTTPS_PORT, config.getHttpsPort());
      assertEquals(OTHER_INFO, config.getOtherInfo());
      assertEquals(SSL_KEYPASSWORD, config.getSslKeypassword());
      assertEquals(SSL_KEYSTORE, config.getSslKeystore());
      assertEquals(SSL_KEYSTORETYPE, config.getSslKeystoretype());
      assertEquals(SSL_PASSWORD, config.getSslPassword());
      assertEquals(SSL_PROTOCOL, config.getSslProtocol());

      assertEquals(HTTP_NIO_AUTO_DETECT, config.isAutoDetectNioSupport());
      assertEquals(HTTP_NIO_ENABLED, config.isNonBlockinIoEnabled());
      assertEquals(HTTP_ENABLED, config.isHttpEnabled());
      assertEquals(HTTPS_ENABLED, config.isHttpsEnabled());
      assertEquals(false, config.isRandomHttpPort());
      assertEquals(false, config.isRandomHttpsPort());
      assertEquals(SSL_NEEDS_CLIENT_AUTH, config.isSslNeedClientAuth());
      assertEquals(SSL_WANTS_CLIENT_AUTH, config.isSslWantClientAuth());
      assertEquals(WORKING_DIRECTORY, config.getWorkingDirectory());
      assertEquals(false, config.isMultipleSlashToSingle());

      verify(sessionManager).setMaxInactiveInterval(config.getContextSessioninactiveinterval());
      assertMapEquals(config.getOtherProps(), "a", 1, "b", 2);
   }

   @Test
   public void testFromConfig() {
      builder.contextPath(CONTEXT);
      builder.contextSessionInactiveInterval(SESSION_INACTIVE_INTERVAL);
      builder.autoDetectNioSupport(HTTP_NIO_AUTO_DETECT);
      builder.nonBlockinIoEnabled(HTTP_NIO_ENABLED);
      builder.httpEnabled(HTTP_ENABLED);
      builder.httpHost(HTTP_HOST);
      builder.httpPort(HTTP_PORT);
      builder.httpsEnabled(HTTPS_ENABLED);
      builder.httpsHost(HTTPS_HOST);
      builder.httpsPort(HTTPS_PORT);
      builder.otherInfo(OTHER_INFO);
      builder.sslKeypassword(SSL_KEYPASSWORD);
      builder.sslKeystore(SSL_KEYSTORE);
      builder.sslKeystoretype(SSL_KEYSTORETYPE);
      builder.sslNeedClientAuth(SSL_NEEDS_CLIENT_AUTH);
      builder.sslPassword(SSL_PASSWORD);
      builder.sslProtocol(SSL_PROTOCOL);
      builder.sslWantClientAuth(SSL_WANTS_CLIENT_AUTH);
      builder.useRandomHttpPort(false);
      builder.useRandomHttpsPort(false);
      builder.workingDirectory(WORKING_DIRECTORY);
      builder.sessionManagerFactory(sessionFactory);
      builder.extraParams(map("a", 1, "b", 2));
      builder.logging(true);
      builder.replaceMultipleSlashesWithSingle(false);

      JettyServer actual = builder.build();
      JettyConfig config1 = actual.getConfig();

      assertEquals(CONTEXT, config1.getContextPath());
      assertEquals(SESSION_INACTIVE_INTERVAL, config1.getContextSessioninactiveinterval());
      assertEquals(HTTP_HOST, config1.getHttpHost());
      assertEquals(HTTP_PORT, config1.getHttpPort());
      assertEquals(HTTPS_HOST, config1.getHttpsHost());
      assertEquals(HTTPS_PORT, config1.getHttpsPort());
      assertEquals(OTHER_INFO, config1.getOtherInfo());
      assertEquals(SSL_KEYPASSWORD, config1.getSslKeypassword());
      assertEquals(SSL_KEYSTORE, config1.getSslKeystore());
      assertEquals(SSL_KEYSTORETYPE, config1.getSslKeystoretype());
      assertEquals(SSL_PASSWORD, config1.getSslPassword());
      assertEquals(SSL_PROTOCOL, config1.getSslProtocol());

      assertEquals(HTTP_NIO_AUTO_DETECT, config1.isAutoDetectNioSupport());
      assertEquals(HTTP_NIO_ENABLED, config1.isNonBlockinIoEnabled());
      assertEquals(HTTP_ENABLED, config1.isHttpEnabled());
      assertEquals(HTTPS_ENABLED, config1.isHttpsEnabled());
      assertEquals(false, config1.isRandomHttpPort());
      assertEquals(false, config1.isRandomHttpsPort());
      assertEquals(SSL_NEEDS_CLIENT_AUTH, config1.isSslNeedClientAuth());
      assertEquals(SSL_WANTS_CLIENT_AUTH, config1.isSslWantClientAuth());
      assertEquals(WORKING_DIRECTORY, config1.getWorkingDirectory());
      assertEquals(false, config1.isMultipleSlashToSingle());

      verify(sessionManager).setMaxInactiveInterval(config1.getContextSessioninactiveinterval());
      assertMapEquals(config1.getOtherProps(), "a", 1, "b", 2);

      JettyServer actual2 = JettyServer.fromConfig(config1);
      JettyConfig config2 = actual2.getConfig();

      assertEquals(CONTEXT, config2.getContextPath());
      assertEquals(SESSION_INACTIVE_INTERVAL, config2.getContextSessioninactiveinterval());
      assertEquals(HTTP_HOST, config2.getHttpHost());
      assertEquals(HTTP_PORT, config2.getHttpPort());
      assertEquals(HTTPS_HOST, config2.getHttpsHost());
      assertEquals(HTTPS_PORT, config2.getHttpsPort());
      assertEquals(OTHER_INFO, config2.getOtherInfo());
      assertEquals(SSL_KEYPASSWORD, config2.getSslKeypassword());
      assertEquals(SSL_KEYSTORE, config2.getSslKeystore());
      assertEquals(SSL_KEYSTORETYPE, config2.getSslKeystoretype());
      assertEquals(SSL_PASSWORD, config2.getSslPassword());
      assertEquals(SSL_PROTOCOL, config2.getSslProtocol());

      assertEquals(HTTP_NIO_AUTO_DETECT, config2.isAutoDetectNioSupport());
      assertEquals(HTTP_NIO_ENABLED, config2.isNonBlockinIoEnabled());
      assertEquals(HTTP_ENABLED, config2.isHttpEnabled());
      assertEquals(HTTPS_ENABLED, config2.isHttpsEnabled());
      assertEquals(false, config2.isRandomHttpPort());
      assertEquals(false, config2.isRandomHttpsPort());
      assertEquals(SSL_NEEDS_CLIENT_AUTH, config2.isSslNeedClientAuth());
      assertEquals(SSL_WANTS_CLIENT_AUTH, config2.isSslWantClientAuth());
      assertEquals(WORKING_DIRECTORY, config2.getWorkingDirectory());
      assertEquals(false, config2.isMultipleSlashToSingle());

      verify(sessionManager).setMaxInactiveInterval(config2.getContextSessioninactiveinterval());
      assertMapEquals(config1.getOtherProps(), "a", 1, "b", 2);
   }

   private static void add(Map<String, Object> props, String key, Object value) {
      props.put(key, String.valueOf(value));
   }

   private static Map<String, Object> map(Object... keyVals) {
      Map<String, Object> data = new HashMap<String, Object>();
      String key = null;
      boolean isKey = true;
      for (Object keyVal : keyVals) {
         if (isKey) {
            key = String.valueOf(keyVal);
            isKey = false;
         } else {
            data.put(key, keyVal);
            isKey = true;
         }
      }
      return data;
   }

   private static void assertMapEquals(Map<String, Object> data, Object... keyVals) {
      if (keyVals.length == 0) {
         assertEquals(true, data.isEmpty());
      } else {
         assertEquals(keyVals.length / 2, data.size());

         String key = null;
         boolean isKey = true;
         for (Object keyVal : keyVals) {
            if (isKey) {
               key = String.valueOf(keyVal);
               isKey = false;
            } else {
               Object actual = data.get(key);
               isKey = true;
               assertEquals(String.format("map error for key[%s] expected[%s] was[%s]", key, keyVal, actual), keyVal,
                  actual);
            }
         }
      }
   }
}
