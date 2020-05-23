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

package org.eclipse.osee.account.rest.model;

/**
 * @author Roberto E. Escobar
 */
public final class AccountContexts {

   private AccountContexts() {
      // Constants Class
   }

   public static final String ACCOUNTS = "accounts";
   public static final String ACCOUNT_PREFERENCES = "preferences";
   public static final String ACCOUNT_ACTIVE = "active";

   public static final String ACCOUNT_ID_PARAM = "account-id";
   public static final String ACCOUNT_ID_TEMPLATE = "{" + ACCOUNT_ID_PARAM + "}";

   public static final String ACCOUNT_USERNAME = "username";
   public static final String ACCOUNT_USERNAME_TEMPLATE = "{" + ACCOUNT_USERNAME + "}";

   public static final String ACCOUNT_LOGIN = "login";
   public static final String ACCOUNT_SESSSIONS = "sessions";

   public static final String ACCOUNT_LOGOUT = "logout";

}
