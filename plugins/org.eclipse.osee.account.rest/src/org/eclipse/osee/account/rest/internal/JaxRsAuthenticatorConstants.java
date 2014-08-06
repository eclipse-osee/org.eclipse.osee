/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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