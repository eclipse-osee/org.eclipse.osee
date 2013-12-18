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

   public static final String AUTHENTICATION_SCHEME_ALLOWED = qualify("scheme.allowed");

}
