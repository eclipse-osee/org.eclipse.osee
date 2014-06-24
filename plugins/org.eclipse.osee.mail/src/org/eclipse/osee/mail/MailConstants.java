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

/**
 * @author Roberto E. Escobar
 */
public final class MailConstants {

   private MailConstants() {
      // Constants
   }

   private static final String PREFIX = "org/eclipse/osee/mail/event/";

   public static final String MAIL_MESSAGE_DELIVERED = PREFIX + "MESSAGE_DELIVERED";

   public static final String MAIL_MESSAGE_NOT_DELIVERED = PREFIX + "MESSAGE_NOT_DELIVERED";

   public static final String MAIL_MESSAGE_PARTIALLY_DELIVERED = PREFIX + "MESSAGE_PARTIALLY_DELIVERED";

   public static final String MAIL_INVALID_ADDRESS = "mail.invalid.address";

   public static final String MAIL_VALID_SENT_ADDRESS = "mail.valid.sent.address";

   public static final String MAIL_VALID_UNSENT_ADDRESS = "mail.valid.unsent.address";

   public static final String MAIL_FROM_ADDRESS = "mail.from.address";

   public static final String MAIL_DATE_SENT = "mail.date.sent";

   public static final String MAIL_SUBJECT = "mail.subject";

   public static final String MAIL_UUID = "mail.uuid";

   public static final String MAIL_UUID_HEADER = "OSEE-MAIL-UUID";

   public static final String NAMESPACE = "mail.server";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final String MAIL_SERVER_USERNAME = qualify("username");
   public static final String MAIL_SERVER_PASSWORD = qualify("password");
   public static final String MAIL_SERVER_HOST = qualify("host");
   public static final String MAIL_SERVER_PORT = qualify("port");
   public static final String MAIL_SERVER_TRANSPORT = qualify("transport");
   public static final String MAIL_SERVER_ADMIN_EMAIL = qualify("admin.email");
   public static final String MAIL_SERVER_TEST_EMAIL_SUBJECT = qualify("test.email.subject");
   public static final String MAIL_SERVER_TEST_EMAIL_BODY = qualify("test.email.body");
   public static final String MAIL_SERVER_STATUS_WAIT_TIME_MILLIS = qualify("status.wait.time");

   public static final String DEFAULT_MAIL_SERVER_USERNAME = null;
   public static final String DEFAULT_MAIL_SERVER_PASSWORD = null;
   public static final String DEFAULT_MAIL_SERVER_HOST = null;
   public static final int DEFAULT_MAIL_SERVER_PORT = 25;
   public static final String DEFAULT_MAIL_SERVER_TRANSPORT = "smtp";
   public static final String DEFAULT_MAIL_SERVER_ADMIN_EMAIL = null;
   public static final String DEFAULT_MAIL_SERVER_TEST_EMAIL_SUBJECT = "Test Email";
   public static final String DEFAULT_MAIL_SERVER_TEST_EMAIL_BODY = "This is a test email sent from OSEE Mail Service";
   public static final long DEFAULT_MAIL_SERVER_STATUS_WAIT_TIME_MILLIS = 60L * 1000L;
}
