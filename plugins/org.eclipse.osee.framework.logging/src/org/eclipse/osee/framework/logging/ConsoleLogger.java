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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Andrew M. Finkbeiner
 */
public class ConsoleLogger implements ILoggerListener {

   public Set<Logger> initializedLoggers;

   public ConsoleLogger() {
      initializedLoggers = new HashSet<>(64);
   }

   @Override
   public void log(String loggerName, Level level, String message, Throwable th) {
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
