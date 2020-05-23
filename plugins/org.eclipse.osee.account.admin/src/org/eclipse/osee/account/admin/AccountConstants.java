/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.account.admin;

/**
 * @author Roberto E. Escobar
 */
public final class AccountConstants {

   private AccountConstants() {
      // Utility class
   }

   public static final String NAMESPACE = "account";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final String NOT_AVAILABLE = "N/A";

   public static final String DEFAULT_USERNAME_VALIDATION_PATTERN = null;
   public static final String DEFAULT_EMAIL_VALIDATION_PATTERN = null;
   public static final String DEFAULT_DISPLAY_NAME_VALIDATION_PATTERN = null;
   public static final String DEFAULT_SUBSCRIPTION_GROUP_NAME_VALIDATION_PATTERN = null;

   public static final String ACCOUNT_USERNAME_VALIDATION_PATTERN = qualify("username.validation.pattern");
   public static final String ACCOUNT_EMAIL_VALIDATION_PATTERN = qualify("email.validation.pattern");
   public static final String ACCOUNT_DISPLAY_NAME_VALIDATION_PATTERN = qualify("display.name.validation.pattern");

   public static final String SUBSCRIPTION_GROUP_NAME_VALIDATION_PATTERN =
      qualify("subscription.group.name.validation.pattern");
}
