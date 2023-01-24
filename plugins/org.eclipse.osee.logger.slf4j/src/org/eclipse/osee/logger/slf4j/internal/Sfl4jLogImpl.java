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

import java.util.Objects;
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

   @Override
   public boolean isTraceEnabled() {
      return this.isEnabled(LOG_TRACE);
   }

   @Override
   public void trace(String format, Object... args) {
      this.logHelper(LOG_TRACE, null, format, args);
   }

   @Override
   public void trace(Throwable th, String format, Object... args) {
      this.logHelper(LOG_TRACE, th, format, args);
   }

   @Override
   public void traceNoFormat(Throwable throwable, CharSequence message) {
      this.logHelperNoFormat(LOG_TRACE, throwable, message);
   }

   @Override
   public boolean isDebugEnabled() {
      return this.isEnabled(LOG_DEBUG);
   }

   @Override
   public void debug(String format, Object... args) {
      this.logHelper(LOG_DEBUG, null, format, args);
   }

   @Override
   public void debug(Throwable th, String format, Object... args) {
      logHelper(LOG_DEBUG, th, format, args);
   }

   @Override
   public void debugNoFormat(Throwable throwable, CharSequence message) {
      this.logHelperNoFormat(LOG_DEBUG, throwable, message);
   }

   @Override
   public boolean isInfoEnabled() {
      return this.isEnabled(LOG_INFO);
   }

   @Override
   public void info(String format, Object... args) {
      this.logHelper(LOG_INFO, null, format, args);
   }

   @Override
   public void info(Throwable th, String format, Object... args) {
      this.logHelper(LOG_INFO, th, format, args);
   }

   @Override
   public void infoNoFormat(Throwable throwable, CharSequence message) {
      this.logHelperNoFormat(LOG_INFO, throwable, message);
   }

   @Override
   public boolean isWarnEnabled() {
      return this.isEnabled(LOG_WARNING);
   }

   @Override
   public void warn(String format, Object... args) {
      this.logHelper(LOG_WARNING, null, format, args);
   }

   @Override
   public void warn(Throwable th, String format, Object... args) {
      this.logHelper(LOG_WARNING, th, format, args);
   }

   @Override
   public void warnNoFormat(Throwable throwable, CharSequence message) {
      this.logHelperNoFormat(LOG_WARNING, throwable, message);
   }

   @Override
   public boolean isErrorEnabled() {
      return this.isEnabled(LOG_ERROR);
   }

   @Override
   public void error(String format, Object... args) {
      this.logHelper(LOG_ERROR, null, format, args);
   }

   @Override
   public void error(Throwable th, String format, Object... args) {
      this.logHelper(LOG_ERROR, th, format, args);
   }

   @Override
   public void errorNoFormat(Throwable throwable, CharSequence message) {
      this.logHelperNoFormat(LOG_ERROR, throwable, message);
   }

   private boolean isEnabled(int level) {

      if (Objects.isNull(this.logger)) {
         return false;
      }

      switch (level) {
         case LOG_DEBUG:
            return this.logger.isDebugEnabled();
         case LOG_ERROR:
            return this.logger.isErrorEnabled();
         case LOG_WARNING:
            return this.logger.isWarnEnabled();
         case LOG_INFO:
            return this.logger.isInfoEnabled();
         default:
            return this.logger.isTraceEnabled();
      }
   }

   private void logAtLevel(int level, Throwable throwable, CharSequence message) {

      if (Objects.isNull(this.logger)) {
         return;
      }

      switch (level) {
         case LOG_DEBUG:
            this.logger.debug(this.marker, message.toString(), throwable);
            break;
         case LOG_ERROR:
            this.logger.error(this.marker, message.toString(), throwable);
            break;
         case LOG_WARNING:
            this.logger.warn(this.marker, message.toString(), throwable);
            break;
         case LOG_INFO:
            this.logger.info(this.marker, message.toString(), throwable);
            break;
         default:
            this.logger.trace(this.marker, message.toString(), throwable);
            break;
      }
   }

   private void logHelper(int level, Throwable th, String format, Object... args) {

      if (!this.isEnabled(level)) {
         return;
      }

      String message = safeFormat(format, args);

      if (Objects.isNull(message)) {
         return;
      }

      this.logAtLevel(level, th, message);
   }

   private void logHelperNoFormat(int level, Throwable throwable, CharSequence message) {

      if (!this.isEnabled(level)) {
         return;
      }

      message = Objects.nonNull(message) ? message : "";

      this.logAtLevel(level, throwable, message);
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
