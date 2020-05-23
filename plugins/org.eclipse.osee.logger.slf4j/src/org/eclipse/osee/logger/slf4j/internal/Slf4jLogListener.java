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

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author Roberto E. Escobar
 */
public class Slf4jLogListener implements LogListener {
   private static final Logger logger = LoggerFactory.getLogger(Slf4jLogListener.class);
   private static final String[] remapMessages = new String[] {"Bundle Event", "Service Event"};

   @Override
   public void logged(LogEntry entry) {
      try {
         String symbolicName = entry.getBundle().getSymbolicName();
         Logger logger = LoggerFactory.getLogger(symbolicName);
         Marker marker = MarkerFactory.getMarker(symbolicName);
         logLogEntry(logger, entry.getLevel(), marker, entry.getMessage(), entry.getException());
      } catch (Exception ex) {
         logger.error("Error during log listening", ex);
      }
   }

   private void logLogEntry(Logger logger, int level, Marker marker, String message, Throwable throwable) {
      int theLevel = level;
      if (throwable == null || isOSGIMessage(message)) {
         theLevel = LogService.LOG_DEBUG;
      }
      String originalName = Thread.currentThread().getName();
      try {
         Thread.currentThread().setName("Osgi Log Service");
         switch (theLevel) {
            case LogService.LOG_DEBUG:
               logger.debug(marker, message, throwable);
               break;
            case LogService.LOG_ERROR:
               logger.error(marker, message, throwable);
               break;
            case LogService.LOG_WARNING:
               logger.warn(marker, message, throwable);
               break;
            case LogService.LOG_INFO:
               logger.info(marker, message, throwable);
               break;
            default:
               logger.trace(marker, message, throwable);
               break;
         }
      } finally {
         Thread.currentThread().setName(originalName);
      }
   }

   private boolean isOSGIMessage(String message) {
      for (String toCheck : remapMessages) {
         if (message.startsWith(toCheck)) {
            return true;
         }
      }
      return false;
   }
}