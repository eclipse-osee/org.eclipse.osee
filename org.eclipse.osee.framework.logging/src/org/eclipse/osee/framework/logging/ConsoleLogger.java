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
package org.eclipse.osee.framework.logging;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Andrew M. Finkbeiner
 */
public class ConsoleLogger implements ILoggerListener {

   public Set<Logger> initializedLoggers;
   public static Level filterLevel;

   static {
      String level = System.getProperty("osee.console", "INFO");

      filterLevel = Level.parse(level);

   }

   private ILoggerFilter filter = new ILoggerFilter() {
      public Pattern bundleId() {
         return null;
      }

      public Level getLoggerLevel() {
         return filterLevel;
      }

      public Pattern name() {
         return null;
      }
   };

   public ConsoleLogger() {
      initializedLoggers = new HashSet<Logger>(64);
   }

   public ILoggerFilter getFilter() {
      return filter;
   }

   public void log(String loggerName, String bundleId, Level level, String message, Throwable th) {
      Logger logger = Logger.getLogger(loggerName);
      if (initializedLoggers.add(logger)) {
         logger.setUseParentHandlers(false);
         SimpleOseeHandler handler = new SimpleOseeHandler();
         logger.addHandler(handler);
         logger.setLevel(Level.ALL);
      }
      logger.log(level, message, th);
   }

}
