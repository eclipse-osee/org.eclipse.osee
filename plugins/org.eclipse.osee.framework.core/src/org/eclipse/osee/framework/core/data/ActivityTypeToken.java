package org.eclipse.osee.framework.core.data;
/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
public interface ActivityTypeToken extends ActivityTypeId {
   ActivityTypeToken SENTINEL = valueOf(Id.SENTINEL, Level.ALL, "org.eclipse.osee.activity.api", "");

   public Level getLogLevel();

   public String getModule();

   public String getMessageFormat();

   public static ActivityTypeToken valueOf(Long id, Level logLevel, String module) {
      return valueOf(id, logLevel, module, "");
   }

   public static ActivityTypeToken valueOf(Long id, Level logLevel, String module, String messageFormat) {
      final class ArtifactTypeIdImpl extends BaseId implements ActivityTypeToken {
         private final Level logLevel;
         private final String module;
         private final String messageFormat;

         public ArtifactTypeIdImpl(Long id, Level logLevel, String module, String messageFormat) {
            super(id);
            this.logLevel = logLevel;
            this.module = module;
            this.messageFormat = messageFormat;
         }

         @Override
         public Level getLogLevel() {
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
      return new ArtifactTypeIdImpl(id, logLevel, module, messageFormat);
   }
}