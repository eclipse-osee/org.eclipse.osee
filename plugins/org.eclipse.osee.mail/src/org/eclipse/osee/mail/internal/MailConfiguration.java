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
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_ADMIN_EMAIL;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_HOST;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_PASSWORD;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_PORT;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_STATUS_WAIT_TIME_MILLIS;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_TEST_EMAIL_BODY;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_TEST_EMAIL_SUBJECT;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_TRANSPORT;
import static org.eclipse.osee.mail.internal.MailConstants.MAIL_SERVER_USERNAME;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * <pre>
 *  Gmail Example:
 *  host = "smtp.gmail.com;
 *  transport = "smpts";
 *  requireAuthentication = true;
 * 
 *  Yahoo Example:
 *  host = "smtp.mail.yahoo.com";
 *  transport = "smpts";
 *  requireAuthentication = true;
 * </pre>
 * 
 * @author Roberto E. Escobar
 */
public class MailConfiguration {

   private String username;
   private String password;
   private String host;
   private int port;
   private String transport;
   private String adminEmail;
   private String testEmailSubject;
   private String testEmailBody;
   private long waitTimeInMillis;

   private MailConfiguration() {
      //Builder class
   }

   public String getHost() {
      return host;
   }

   public int getPort() {
      return port;
   }

   public String getTransport() {
      return transport;
   }

   public boolean isAuthenticationRequired() {
      return Strings.isValid(username) && Strings.isValid(password);
   }

   public String getUserName() {
      return username;
   }

   public String getPassword() {
      return password;
   }

   public String getAdminEmail() {
      return adminEmail;
   }

   public String getTestEmailSubject() {
      return testEmailSubject;
   }

   public String getTestEmailBody() {
      return testEmailBody;
   }

   public long getStatusWaitTime() {
      return waitTimeInMillis;
   }

   public MailConfiguration copy() {
      MailConfiguration data = new MailConfiguration();
      data.username = this.username;
      data.password = this.password;
      data.host = this.host;
      data.port = this.port;
      data.transport = this.transport;
      data.adminEmail = this.adminEmail;
      data.testEmailSubject = this.testEmailSubject;
      data.testEmailBody = this.testEmailBody;
      data.waitTimeInMillis = this.waitTimeInMillis;
      return data;
   }

   void setUserName(String username) {
      this.username = username;
   }

   void setPassword(String password) {
      this.password = password;
   }

   void setHost(String host) {
      this.host = host;
   }

   void setPort(int port) {
      this.port = port;
   }

   void setTransport(String transport) {
      this.transport = transport;
   }

   void setAdminEmail(String adminEmail) {
      this.adminEmail = adminEmail;
   }

   void setTestEmailSubject(String testEmailSubject) {
      this.testEmailSubject = testEmailSubject;
   }

   void setTestEmailBody(String testEmailBody) {
      this.testEmailBody = testEmailBody;
   }

   void setStatusWaitTime(long waitTimeInMillis) {
      this.waitTimeInMillis = waitTimeInMillis;
   }

   @Override
   public String toString() {
      return "MailConfiguration [username=" + username + ", password=" + password + ", host=" + host + ", port=" + port + ", transport=" + transport + ", adminEmail=" + adminEmail + ", testEmailSubject=" + testEmailSubject + ", testEmailBody=" + testEmailBody + ", waitTimeInMillis=" + waitTimeInMillis + "]";
   }

   public static MailConfigurationBuilder newBuilder() {
      return new MailConfigurationBuilder();
   }

   public static MailConfigurationBuilder fromProperties(Map<String, Object> props) {
      return newBuilder().properties(props);
   }

   public static MailConfiguration newConfig(Map<String, Object> props) {
      return fromProperties(props).build();
   }

   public static final class MailConfigurationBuilder {
      private final MailConfiguration config = new MailConfiguration();

      public MailConfiguration build() {
         return config.copy();
      }

      public MailConfigurationBuilder properties(Map<String, Object> props) {
         //@formatter:off
         username(get(props, MAIL_SERVER_USERNAME, MailConstants.DEFAULT_MAIL_SERVER_USERNAME));
         password(get(props, MAIL_SERVER_PASSWORD, DEFAULT_MAIL_SERVER_PASSWORD));
         host(get(props, MAIL_SERVER_HOST, DEFAULT_MAIL_SERVER_HOST));
         port(getInt(props, MAIL_SERVER_PORT, DEFAULT_MAIL_SERVER_PORT));
         transport(get(props, MAIL_SERVER_TRANSPORT, DEFAULT_MAIL_SERVER_TRANSPORT));
         adminEmail(get(props, MAIL_SERVER_ADMIN_EMAIL, DEFAULT_MAIL_SERVER_ADMIN_EMAIL));
         testEmailSubject(get(props, MAIL_SERVER_TEST_EMAIL_SUBJECT, DEFAULT_MAIL_SERVER_TEST_EMAIL_SUBJECT));
         testEmailBody(get(props, MAIL_SERVER_TEST_EMAIL_BODY, DEFAULT_MAIL_SERVER_TEST_EMAIL_BODY));
         statusWaitTime(getLong(props, MAIL_SERVER_STATUS_WAIT_TIME_MILLIS, DEFAULT_MAIL_SERVER_STATUS_WAIT_TIME_MILLIS));
         //@formatter:on
         return this;
      }

      public MailConfigurationBuilder username(String userName) {
         config.setUserName(userName);
         return this;
      }

      public MailConfigurationBuilder password(String password) {
         config.setPassword(password);
         return this;
      }

      public MailConfigurationBuilder host(String host) {
         config.setHost(host);
         return this;
      }

      public MailConfigurationBuilder port(int port) {
         config.setPort(port);
         return this;
      }

      public MailConfigurationBuilder transport(String transport) {
         config.setTransport(transport);
         return this;
      }

      public MailConfigurationBuilder adminEmail(String adminEmail) {
         config.setAdminEmail(adminEmail);
         return this;
      }

      public MailConfigurationBuilder testEmailSubject(String testMailSubject) {
         config.setTestEmailSubject(testMailSubject);
         return this;
      }

      public MailConfigurationBuilder testEmailBody(String testMailBody) {
         config.setTestEmailBody(testMailBody);
         return this;
      }

      public MailConfigurationBuilder statusWaitTime(long waitTimeInMillis) {
         config.setStatusWaitTime(waitTimeInMillis);
         return this;
      }

      private long getLong(Map<String, Object> props, String key, long defaultValue) {
         String toReturn = get(props, key, String.valueOf(defaultValue));
         return Strings.isNumeric(toReturn) ? Long.parseLong(toReturn) : -1L;
      }

      private static int getInt(Map<String, Object> props, String key, int defaultValue) {
         String toReturn = get(props, key, String.valueOf(defaultValue));
         return Strings.isNumeric(toReturn) ? Integer.parseInt(toReturn) : -1;
      }

      private static String get(Map<String, Object> props, String key, String defaultValue) {
         String toReturn = defaultValue;
         Object object = props != null ? props.get(key) : null;
         if (object != null) {
            toReturn = String.valueOf(object);
         }
         return toReturn;
      }
   }

}
