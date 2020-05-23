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

package org.eclipse.osee.jaxrs.server.internal;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsConstants {

   private JaxRsConstants() {
      // Constants class
   }

   public static final String NAMESPACE = "jaxrs.server";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final String DEFAULT_JAXRS_BASE_CONTEXT = "/";

   public static final String JAXRS_BASE_CONTEXT = qualify("base.context");

}
