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
package org.eclipse.osee.ats.api.util;

import static java.util.logging.Level.INFO;
import java.util.logging.Level;
import org.eclipse.osee.activity.api.ActivityType;

/**
 * @author Donald G. Dunne
 */
public enum AtsActivity implements ActivityType {
   ATSNAVIGATEITEM(45L, INFO, "osee.ats.XNavigateItem", "ATS XNavigateItem [%s]");

   private final Long typeId;
   private final Long logLevel;
   private final String module;
   private final String messageFormat;

   AtsActivity(Long typeId, Level logLevel, String module, String messageFormat) {
      this.typeId = typeId;
      this.messageFormat = messageFormat;
      this.logLevel = new Long(logLevel.intValue());
      this.module = module;
   }

   AtsActivity(Long typeId, Level logLevel, String module) {
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
