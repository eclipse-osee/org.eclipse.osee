/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
