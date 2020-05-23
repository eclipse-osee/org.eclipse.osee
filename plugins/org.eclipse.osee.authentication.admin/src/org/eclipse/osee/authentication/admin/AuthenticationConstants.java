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

package org.eclipse.osee.authentication.admin;

/**
 * @author Roberto E. Escobar
 */
public final class AuthenticationConstants {

   private AuthenticationConstants() {
      // Utility class
   }

   public static final String NAMESPACE = "authentication";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final String NO_AUTHENTICATION = "None";

   public static final String DEFAULT_AUTHENTICATION_SCHEME = NO_AUTHENTICATION;
   public static final String DEFAULT_AUTHENTICATION_SCHEME_ALLOWED_DEFAULT = "";

   public static final String AUTHENTICATION_SCHEME_ALLOWED = qualify("scheme.allowed");
   public static final String AUTHENTICATION_SCHEME_ALLOWED_DEFAULT = qualify("scheme.allowed.default");

}
