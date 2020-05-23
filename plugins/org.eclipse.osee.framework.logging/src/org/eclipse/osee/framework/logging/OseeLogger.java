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

package org.eclipse.osee.framework.logging;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeLogger {

   private final List<ILoggerListener> listeners;
   private final Map<String, Level> levelMap;
   private final Level defaultLevel;

   public OseeLogger() {
      levelMap = new ConcurrentHashMap<>();
      listeners = new CopyOnWriteArrayList<>();
      listeners.add(new ConsoleLogger());

      defaultLevel = OseeProperties.getOseeLogDefault();

      for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
         if (entry.getKey().toString().startsWith("osee.log.")) {
            String name = entry.getKey().toString().substring(9);
            String level = entry.getValue().toString();
            try {
               Level lev = Level.parse(level);
               levelMap.put(name, lev);
            } catch (Exception ex) {
               // Do Nothing
            }
         }
      }
   }

   public void log(String loggerName, Level level, String message, Throwable th) {
      if (!shouldLog(loggerName, level)) {
         return;
      }

      for (ILoggerListener logger : listeners) {
         logger.log(loggerName, level, message, th);
      }
   }

   public void format(String loggerName, Level level, String message, Object... objects) {
      format(null, loggerName, level, message, objects);
   }

   public void format(Throwable th, String loggerName, Level level, String message, Object... objects) {
      if (!shouldLog(loggerName, level)) {
         return;
      }
      for (ILoggerListener logger : listeners) {
         String msg = String.format(message, objects);
         logger.log(loggerName, level, msg, th);
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

   private boolean shouldLog(String loggerName, Level level) {
      Level filterLevel = levelMap.get(loggerName);
      if (filterLevel == null) {
         filterLevel = defaultLevel;
         levelMap.put(loggerName, filterLevel);
      }
      if (level.intValue() >= filterLevel.intValue()) {
         return true;
      } else {
         return false;
      }
   }
}
