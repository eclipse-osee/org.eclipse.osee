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
