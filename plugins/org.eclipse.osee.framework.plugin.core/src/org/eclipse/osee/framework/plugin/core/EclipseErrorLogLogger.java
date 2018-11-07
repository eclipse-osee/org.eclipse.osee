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
package org.eclipse.osee.framework.plugin.core;

import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.logging.ILoggerListener;
import org.eclipse.osee.framework.plugin.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class EclipseErrorLogLogger implements ILoggerListener {

   @Override
   public void log(String loggerName, Level level, String message, Throwable th) {
      int statusLevel = 0;
      if (level.intValue() >= Level.SEVERE.intValue()) {
         statusLevel = IStatus.ERROR;
      } else if (level.intValue() >= Level.WARNING.intValue()) {
         statusLevel = IStatus.WARNING;
      } else if (level.intValue() >= Level.INFO.intValue()) {
         statusLevel = IStatus.INFO;
      } else {
         return;
      }
      Activator.getInstance().getLog().log(new Status(statusLevel, loggerName, statusLevel, message, th));
   }

}
