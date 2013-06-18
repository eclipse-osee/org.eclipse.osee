/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.mail;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test unit for {@link MailServiceConfig}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class MailServiceConfigTest {
   private static final String SPECIAL_CHARS = "~`!@#$%^&*()_-+={}|[]:\";'<>?,./";

   private static final String DEFAULT_HOST = "";
   private static final String DEFAULT_USER = "";
   private static final String DEFAULT_PASSWD = "";
   private static final int DEFAULT_PORT = 25;
   private static final String DEFAULT_TRANSPORT = "smtp";
   private static final boolean DEFAULT_DEBUG = false;
   private static final boolean DEFAULT_AUTHENTICATION = false;
   private static final String DEFAULT_SYSTEM_EMAIL = "";
   private static final boolean DEFAULT_MAIL_STATS = false;

   private final MailServiceConfig config = new MailServiceConfig();

   private final String userName;
   private final String password;
   private final String host;
   private final int port;
   private final String transport;
   private final String systemAdminEmailAddress;
   private final boolean debug;
   private final boolean authenticationRequired;
   private final boolean mailStatsEnabled;

   public MailServiceConfigTest(String userName, String password, String host, int port, String transport, String systemAdminEmailAddress, boolean debug, boolean authenticationRequired, boolean mailStatsEnabled) {
      super();
      this.userName = userName;
      this.password = password;
      this.host = host;
      this.port = port;
      this.transport = transport;
      this.systemAdminEmailAddress = systemAdminEmailAddress;
      this.debug = debug;
      this.authenticationRequired = authenticationRequired;
      this.mailStatsEnabled = mailStatsEnabled;
   }

   @org.junit.Test
   public void testSetGetHost() {
      Assert.assertEquals(DEFAULT_HOST, config.getHost());
      config.setHost(host);
      Assert.assertEquals(host, config.getHost());
   }

   @org.junit.Test
   public void testMailMsgIntAccessors() {
      Assert.assertEquals(DEFAULT_PORT, config.getPort());
      config.setPort(port);
      Assert.assertEquals(port, config.getPort());
   }

   @org.junit.Test
   public void testSetGetUserName() {
      Assert.assertEquals(DEFAULT_USER, config.getUserName());
      config.setUserName(userName);
      Assert.assertEquals(userName, config.getUserName());
   }

   @org.junit.Test
   public void testSetGetPassword() {
      Assert.assertEquals(DEFAULT_PASSWD, config.getPassword());
      config.setPassword(password);
      Assert.assertEquals(password, config.getPassword());
   }

   @org.junit.Test
   public void testSetGetSystemAdminEmailAddress() {
      Assert.assertEquals(DEFAULT_SYSTEM_EMAIL, config.getSystemAdminEmailAddress());
      config.setSystemAdminEmailAddress(systemAdminEmailAddress);
      Assert.assertEquals(systemAdminEmailAddress, config.getSystemAdminEmailAddress());
   }

   @org.junit.Test
   public void testSetGetTransport() {
      Assert.assertEquals(DEFAULT_TRANSPORT, config.getTransport());
      config.setTransport(transport);
      Assert.assertEquals(transport, config.getTransport());
   }

   @org.junit.Test
   public void testSetIsDebug() {
      Assert.assertEquals(DEFAULT_DEBUG, config.isDebug());
      config.setDebug(debug);
      Assert.assertEquals(debug, config.isDebug());
   }

   @org.junit.Test
   public void testSetIsAuthenticationRequired() {
      Assert.assertEquals(DEFAULT_AUTHENTICATION, config.isAuthenticationRequired());
      config.setAuthenticationRequired(authenticationRequired);
      Assert.assertEquals(authenticationRequired, config.isAuthenticationRequired());
   }

   @org.junit.Test
   public void testSetIsMailStats() {
      Assert.assertEquals(DEFAULT_MAIL_STATS, config.isMailStatsEnabled());
      config.setMailStatsEnabled(mailStatsEnabled);
      Assert.assertEquals(mailStatsEnabled, config.isMailStatsEnabled());
   }

   @org.junit.Test
   public void testSetTo() {
      MailServiceConfig config1 = new MailServiceConfig();
      Assert.assertEquals(DEFAULT_HOST, config.getHost());
      Assert.assertEquals(DEFAULT_PORT, config.getPort());
      Assert.assertEquals(DEFAULT_USER, config.getUserName());
      Assert.assertEquals(DEFAULT_PASSWD, config.getPassword());
      Assert.assertEquals(DEFAULT_SYSTEM_EMAIL, config.getSystemAdminEmailAddress());
      Assert.assertEquals(DEFAULT_TRANSPORT, config.getTransport());
      Assert.assertEquals(DEFAULT_DEBUG, config.isDebug());
      Assert.assertEquals(DEFAULT_AUTHENTICATION, config.isAuthenticationRequired());
      Assert.assertEquals(DEFAULT_MAIL_STATS, config.isMailStatsEnabled());

      MailServiceConfig config2 = new MailServiceConfig();
      config2.setHost(host);
      config2.setPort(port);
      config2.setUserName(userName);
      config2.setPassword(password);
      config2.setSystemAdminEmailAddress(systemAdminEmailAddress);
      config2.setTransport(transport);
      config2.setDebug(debug);
      config2.setAuthenticationRequired(authenticationRequired);
      config2.setMailStatsEnabled(mailStatsEnabled);

      Assert.assertEquals(host, config2.getHost());
      Assert.assertEquals(port, config2.getPort());
      Assert.assertEquals(userName, config2.getUserName());
      Assert.assertEquals(password, config2.getPassword());
      Assert.assertEquals(systemAdminEmailAddress, config2.getSystemAdminEmailAddress());
      Assert.assertEquals(transport, config2.getTransport());
      Assert.assertEquals(debug, config2.isDebug());
      Assert.assertEquals(authenticationRequired, config2.isAuthenticationRequired());
      Assert.assertEquals(mailStatsEnabled, config2.isMailStatsEnabled());

      config1.setTo(config2);

      Assert.assertEquals(host, config1.getHost());
      Assert.assertEquals(port, config1.getPort());
      Assert.assertEquals(userName, config1.getUserName());
      Assert.assertEquals(password, config1.getPassword());
      Assert.assertEquals(systemAdminEmailAddress, config1.getSystemAdminEmailAddress());
      Assert.assertEquals(transport, config1.getTransport());
      Assert.assertEquals(debug, config1.isDebug());
      Assert.assertEquals(authenticationRequired, config1.isAuthenticationRequired());
      Assert.assertEquals(mailStatsEnabled, config1.isMailStatsEnabled());
   }

   private static void addTest(Collection<Object[]> data, String userName, String password, String host, int port, String transport, String systemAdminEmailAddress, boolean debug, boolean authenticationRequired, boolean mailStatsEnabled) {
      data.add(new Object[] {
         userName,
         password,
         host,
         port,
         transport,
         systemAdminEmailAddress,
         debug,
         authenticationRequired,
         mailStatsEnabled});
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();

      addTest(data, SPECIAL_CHARS, "pword", "123.9.1.2", 0, "smpts", "joe.schmoe@somedomain.com", true, true, true);
      addTest(data, "u1", SPECIAL_CHARS, "such.and.such.somewhere.com", Integer.MAX_VALUE, "tx1", "", false, true, true);
      addTest(data, "u2", "p1", SPECIAL_CHARS, Integer.MIN_VALUE, "tx2", "sys1@xyz.com", true, false, true);
      addTest(data, "u3", "p2", "host1", 8000, SPECIAL_CHARS, "sys2@xyz.com", true, true, false);
      addTest(data, "u4", "p3", "host2", 9999, "tx3", SPECIAL_CHARS, false, false, false);
      addTest(data, null, null, null, 9999, null, null, false, true, false);
      return data;
   }
}
