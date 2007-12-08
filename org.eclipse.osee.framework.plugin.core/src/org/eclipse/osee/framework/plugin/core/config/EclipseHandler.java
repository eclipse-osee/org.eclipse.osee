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
package org.eclipse.osee.framework.plugin.core.config;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.plugin.core.PluginCoreActivator;

public class EclipseHandler extends Handler {

   @Override
   public void publish(LogRecord record) {

      int level;
      if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
         level = Status.ERROR;
      } else if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
         level = Status.WARNING;
      } else {
         level = Status.INFO;
      }
      try {
         PluginCoreActivator.getInstance().getLog().log(
               new Status(level, PluginCoreActivator.getInstance().getBundle().getSymbolicName(), level,
                     (record.getMessage() == null ? "NO MESSAGE" : record.getMessage()), record.getThrown()));
      } catch (Exception ex) {
         System.out.println(record.getMessage());
      }

   }

   @Override
   public void flush() {
   }

   @Override
   public void close() throws SecurityException {
   }

}
