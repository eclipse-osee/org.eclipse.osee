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

package org.eclipse.osee.authorization.admin;

/**
 * Class to collect configuration constants such as keys and default values.
 * 
 * @author Roberto E. Escobar
 */
public final class AuthorizationConstants {

   private AuthorizationConstants() {
      // Constants class
   }

   public static final String NAMESPACE = "authorization";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final String DENY_ALL_AUTHORIZER_SCHEME = "Override - DenyAll";
   public static final String PERMIT_ALL_AUTHORIZER_SCHEME = "Override - PermitAll";
   public static final String NONE_AUTHORIZATION_PROVIDER = "None";
   public static final String OSEE_AUTHORIZATION_PROVIDER = "OSEE";

   public static final AuthorizationOverride DEFAULT_AUTHORIZATION_OVERRIDE = AuthorizationOverride.PERMIT_ALL;
   public static final String DEFAULT_AUTHORIZATION_PROVIDER = NONE_AUTHORIZATION_PROVIDER;

   public static final String AUTHORIZATION_OVERRIDE = qualify("override");
   public static final String AUTHORIZATION_SCHEME_ALLOWED = qualify("scheme.allowed");

   public static final String AUTHORIZATION_SCHEME_DEFAULT = qualify("scheme.default");
   public static final String DEFAULT_AUTHORIZATION_SCHEME_DEFAULT = "";

}
