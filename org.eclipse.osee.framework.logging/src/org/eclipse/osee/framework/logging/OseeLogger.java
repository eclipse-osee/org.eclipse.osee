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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeLogger {

   private List<ILoggerListener> listeners;
   private Map<String, Level> levelMap;

   public OseeLogger() {
      levelMap = new ConcurrentHashMap<String, Level>();
      listeners = new CopyOnWriteArrayList<ILoggerListener>();
      listeners.add(new ConsoleLogger());
   }

   public void log(String loggerName, String bundleId, Level level, String message) {
      log(loggerName, bundleId, level, message, null);
   }

   public void log(String loggerName, String bundleId, Level level, String message, Throwable th) {

      Level lvl = levelMap.get(loggerName);

      if (lvl != null && level.intValue() < lvl.intValue()) return;

      for (ILoggerListener logger : listeners) {
         if (shouldLog(logger, loggerName, bundleId, level)) {
            logger.log(loggerName, bundleId, level, message, th);
         }
      }
   }

   public void format(String loggerName, String bundleId, Level level, String message, Object... objects) {
      format(null, loggerName, bundleId, level, message, objects);
   }

   public void format(Throwable th, String loggerName, String bundleId, Level level, String message, Object... objects) {
      Level lvl = levelMap.get(loggerName);
      if (lvl != null && level.intValue() < lvl.intValue()) return;

      for (ILoggerListener logger : listeners) {
         if (shouldLog(logger, loggerName, bundleId, level)) {
            String msg = String.format(message, objects);
            logger.log(loggerName, bundleId, level, msg, th);
         }
      }
   }

   public void registerLoggerListener(ILoggerListener listener) {
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
   }

   public void unregisterLoggerListener(ILoggerListener listener) {
      listeners.remove(listener);
   }

   public void setLevel(String loggerName, Level level) {
      levelMap.put(loggerName, level);
   }

   private boolean shouldLog(ILoggerListener logger, String loggerName, String bundleId, Level level) {
      ILoggerFilter filter = logger.getFilter();
      boolean levelMatch = true, nameMatch = true, bundleIdMatch = true;
      if (filter != null) {
         Level filterLevel = filter.getLoggerLevel();
         Pattern bundleIdMatcher = filter.bundleId();
         Pattern nameMatcher = filter.name();
         if (filterLevel != null && level != null) {
            levelMatch = level.intValue() >= filterLevel.intValue();
         }
         if (bundleIdMatcher != null && bundleId != null) {
            bundleIdMatch = bundleIdMatcher.matcher(bundleId).matches();
         }
         if (nameMatcher != null && loggerName != null) {
            nameMatch = nameMatcher.matcher(loggerName).matches();
         }
      }
      return levelMatch && bundleIdMatch && nameMatch;
   }
}
