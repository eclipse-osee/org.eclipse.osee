/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author Ryan D. Brooks
 */
public final class CoreActivityTypes {
   private static final ArrayList<ActivityTypeToken> types = new ArrayList<>();

   // @formatter:off
   public static final ActivityTypeToken DEFAULT_ROOT = create(1L, INFO, "org.eclipse.osee.activity");
   public static final ActivityTypeToken OSEE_ERROR = create(2L, SEVERE, "org.eclipse.osee.activity");
   public static final ActivityTypeToken JAXRS_METHOD_CALL = create(880479734L, INFO, "org.eclipse.osee.activity.jaxrs");
   public static final ActivityTypeToken JAXRS_METHOD_CALL_FILTER_ERROR = create(23133964208285L, SEVERE, "org.eclipse.osee.activity.jaxrs");
   public static final ActivityTypeToken SRS_TRACE = create(80349535402L, INFO, "org.eclipse.osee.define.SrsTraceReport");
   public static final ActivityTypeToken MSG_CONTINUATION = create(29566294587L, INFO, "org.eclipse.osee.activity");
   public static final ActivityTypeToken IDE = create(88L, INFO, "osee.ide.client");
   public static final ActivityTypeToken XNAVIGATEITEM = create(45L, INFO, "osee.framework.XNavigateItem", "XNavigateItem [%s]");
   public static final ActivityTypeToken ACCESS_CONTROL_MODIFIED = create(99L, INFO, "osee.framework.access", "Access Control Modified [%s]");
   public static final ActivityTypeToken BRANCH_OPERATION = create(61L, INFO, "org.eclipse.osee.orcs.rest.internal.branch");
   public static final ActivityTypeToken THREAD_ACTIVITY = create(777L, INFO, "org.eclipse.osee.activity");
   public static final ActivityTypeToken PURGE_TRANSACTION = create(4455L, INFO, "org.eclipse.osee.orcs.rest.purge.transaction");
   // @formatter:on

   private CoreActivityTypes() {
      // Constants
   }

   private static ActivityTypeToken create(Long id, Level logLevel, String module) {
      return create(id, logLevel, module, "");
   }

   private static ActivityTypeToken create(Long id, Level logLevel, String module, String messageFormat) {
      ActivityTypeToken type = ActivityTypeToken.valueOf(id, logLevel, module, messageFormat);
      types.add(type);
      return type;
   }

   public static ArrayList<ActivityTypeToken> getTypes() {
      return types;
   }
}