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

package org.eclipse.osee.account.rest.internal;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsAuthenticatorConstants {

   private JaxRsAuthenticatorConstants() {
      // Constants
   }

   private static final String NAMESPACE = "jaxrs.authenticator";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final String JAXRS_AUTH__ALLOW_AUTOMATIC_ACCOUNT_CREATION =
      qualify("automatic.account.creation.allowed");

   public static final boolean DEFAULT_JAXRS_AUTH__ALLOW_AUTOMATIC_ACCOUNT_CREATION = true;

}