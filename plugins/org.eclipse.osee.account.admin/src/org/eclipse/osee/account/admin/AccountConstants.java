/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

   public static final String DEFAULT_USERNAME_VALIDATION_PATTERN = null;
   public static final String DEFAULT_EMAIL_VALIDATION_PATTERN = null;
   public static final String DEFAULT_DISPLAY_NAME_VALIDATION_PATTERN = null;

   public static final String ACCOUNT_USERNAME_VALIDATION_PATTERN = qualify("username.validation.pattern");
   public static final String ACCOUNT_EMAIL_VALIDATION_PATTERN = qualify("email.validation.pattern");
   public static final String ACCOUNT_DISPLAY_NAME_VALIDATION_PATTERN = qualify("display.name.validation.pattern");

}
