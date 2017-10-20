/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.activity.api;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Level;

/**
 * @author Ryan D. Brooks
 */
public enum Activity implements ActivityType {
   JAXRS_METHOD_CALL(880479734L, INFO, "org.eclipse.osee.activity.jaxrs"),
   JAXRS_METHOD_CALL_FILTER_ERROR(23133964208285L, SEVERE, "org.eclipse.osee.activity.jaxrs"),
   SRS_TRACE(80349535402L, INFO, "org.eclipse.osee.define.report.SrsTraceReport"),
   MSG_CONTINUATION(29566294587L, INFO, "org.eclipse.osee.activity"),
   IDE(88L, INFO, "osee.ide.client"),
   XNAVIGATEITEM(45L, INFO, "osee.framework.XNavigateItem", "XNavigateItem [%s]"),
   ACCESS_CONTROL_MODIFIED(99L, INFO, "osee.framework.access", "Access Control Modified [%s]"),
   BRANCH_OPERATION(61L, INFO, "org.eclipse.osee.orcs.rest.internal.branch"),
   THREAD_ACTIVITY(777L, INFO, "org.eclipse.osee.activity"),
   PURGE_TRANSACTION(4455L, INFO, "org.eclipse.osee.orcs.rest.purge.transaction");

   private final Long typeId;
   private final Long logLevel;
   private final String module;
   private String messageFormat;

   Activity(Long typeId, Level logLevel, String module, String messageFormat) {
      this.typeId = typeId;
      this.messageFormat = messageFormat;
      this.logLevel = new Long(logLevel.intValue());
      this.module = module;
   }

   Activity(Long typeId, Level logLevel, String module) {
      this(typeId, logLevel, module, "");
   }

   @Override
   public Long getTypeId() {
      return typeId;
   }

   @Override
   public Long getLogLevel() {
      return logLevel;
   }

   @Override
   public String getModule() {
      return module;
   }

   @Override
   public String getMessageFormat() {
      return messageFormat;
   }
}
