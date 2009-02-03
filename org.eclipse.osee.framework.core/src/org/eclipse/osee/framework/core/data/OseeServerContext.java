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

   public static final String PROCESS_CONTEXT = "GET.ARTIFACT"; // For backwards compatibility must remain without base context.
   public static final String SEARCH_CONTEXT = asAbsoluteContext("search");
   public static final String BRANCH_CREATION_CONTEXT = asAbsoluteContext("branch");
   public static final String BRANCH_EXCHANGE_CONTEXT = asAbsoluteContext("branch/exchange");
   public static final String SEARCH_TAGGING_CONTEXT = asAbsoluteContext("search/tagger");
   public static final String RESOURCE_CONTEXT = asAbsoluteContext("resource");
   public static final String SESSION_CONTEXT = asAbsoluteContext("session");
   public static final String LOOKUP_CONTEXT = asAbsoluteContext("server/lookup");
   public static final String CLIENT_LOOPBACK_CONTEXT = asAbsoluteContext("client/loopback");
   public static final String ARTIFACT_CONTEXT = asAbsoluteContext("artifact");

   private static final String asAbsoluteContext(String value) {
      return BASE_CONTEXT + "/" + value;
   }

   private OseeServerContext() {
   }
}
