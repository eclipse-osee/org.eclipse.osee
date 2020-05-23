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

package org.eclipse.osee.logger.slf4j.internal;

import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author Roberto E. Escobar
 */
public class Sfl4jLogImpl implements Log {

   private static final int LOG_ERROR = 1;
   private static final int LOG_WARNING = 2;
   private static final int LOG_INFO = 3;
   private static final int LOG_DEBUG = 4;
   private static final int LOG_TRACE = 5;

   private Logger logger;
   private Marker marker;

   public void start(ComponentContext context) {
      if (logger == null) {
         Bundle usingBundle = context.getUsingBundle();
         String symbolicName = null;
         if (usingBundle != null) {
            symbolicName = usingBundle.getSymbolicName();
         } else {
            symbolicName = "Osee Log";
         }
         logger = LoggerFactory.getLogger(symbolicName);
         marker = MarkerFactory.getMarker(symbolicName);
         debug("Logger setup for [%s]", logger.getName());
      }
   }

   public void stop() {
      logger = null;
      marker = null;
   }

   private Logger getLogger() {
      return logger;
   }

   @Override
   public boolean isTraceEnabled() {
      return isEnabled(LOG_TRACE);
   }

   @Override
   public void trace(String format, Object... args) {
      trace(null, format, args);
   }

   @Override
   public void trace(Throwable th, String format, Object... args) {
      logHelper(LOG_TRACE, th, format, args);
   }

   @Override
   public boolean isDebugEnabled() {
      return isEnabled(LOG_DEBUG);
   }

   @Override
   public void debug(String format, Object... args) {
      debug(null, format, args);
   }

   @Override
   public void debug(Throwable th, String format, Object... args) {
      logHelper(LOG_DEBUG, th, format, args);
   }

   @Override
   public boolean isInfoEnabled() {
      return isEnabled(LOG_INFO);
   }

   @Override
   public void info(String format, Object... args) {
      info(null, format, args);
   }

   @Override
   public void info(Throwable th, String format, Object... args) {
      logHelper(LOG_INFO, th, format, args);
   }

   @Override
   public boolean isWarnEnabled() {
      return isEnabled(LOG_WARNING);
   }

   @Override
   public void warn(String format, Object... args) {
      warn(null, format, args);
   }

   @Override
   public void warn(Throwable th, String format, Object... args) {
      logHelper(LOG_WARNING, th, format, args);
   }

   @Override
   public boolean isErrorEnabled() {
      return isEnabled(LOG_ERROR);
   }

   @Override
   public void error(String format, Object... args) {
      error(null, format, args);
   }

   @Override
   public void error(Throwable th, String format, Object... args) {
      logHelper(LOG_ERROR, th, format, args);
   }

   private boolean isEnabled(int level) {
      boolean result = false;
      final Logger logger = getLogger();
      if (logger != null) {
         switch (level) {
            case LOG_DEBUG:
               result = logger.isDebugEnabled();
               break;
            case LOG_ERROR:
               result = logger.isErrorEnabled();
               break;
            case LOG_WARNING:
               result = logger.isWarnEnabled();
               break;
            case LOG_INFO:
               result = logger.isInfoEnabled();
               break;
            default:
               result = logger.isTraceEnabled();
               break;
         }
      }
      return result;
   }

   private void logHelper(int level, Throwable th, String format, Object... args) {
      logHelper(null, level, th, format, args);
   }

   private void logHelper(Object context, int level, Throwable th, String format, Object... args) {
      final Logger logger = getLogger();
      if (isEnabled(level)) {
         String message = safeFormat(format, args);
         if (message != null) {
            switch (level) {
               case LOG_DEBUG:
                  logger.debug(marker, message, th);
                  break;
               case LOG_ERROR:
                  logger.error(marker, message, th);
                  break;
               case LOG_WARNING:
                  logger.warn(marker, message, th);
                  break;
               case LOG_INFO:
                  logger.info(marker, message, th);
                  break;
               default:
                  logger.trace(marker, message, th);
                  break;
            }
         }
      }
   }

   private static String safeFormat(String message, Object... args) {
      String toReturn;
      try {
         toReturn = String.format(message, args);
      } catch (RuntimeException ex) {
         StringBuilder builder = new StringBuilder();
         builder.append("Log message could not be formatted:");
         builder.append(message);
         builder.append(" with the following arguments [");
         builder.append(org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", args));
         builder.append("].  Cause [");
         builder.append(ex.toString());
         builder.append("]");
         toReturn = builder.toString();
      }
      return toReturn;
   }

}
