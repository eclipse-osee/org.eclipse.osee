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

   public static final String REGISTRATION_EVENT = PREFIX + "MAIL_SERVICE_REGISTRATION";

   public static final String DEREGISTRATION_EVENT = PREFIX + "MAIL_SERVICE_DEREGISTRATION";

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
}
