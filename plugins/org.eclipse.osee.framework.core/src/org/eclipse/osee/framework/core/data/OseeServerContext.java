/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.data;

/**
 * @author Roberto E. Escobar
 */
public class OseeServerContext {

   private static final String BASE_CONTEXT = "osee";

   public static final String MANAGER_CONTEXT = asAbsoluteContext("manager");
   public static final String CLIENT_LOOPBACK_CONTEXT = asAbsoluteContext("loopback");
   public static final String ARTIFACT_CONTEXT = asAbsoluteContext("artifact");

   private static final String asAbsoluteContext(String value) {
      return BASE_CONTEXT + "/" + value;
   }

   private OseeServerContext() {
      // private constructor
   }
}
