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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Donald G. Dunne
 */
public class OSEELog {

   public static void logException(Class<?> clazz, String message, Exception ex, boolean popup) {
      AWorkbench.popup("ERROR", message);
      OseeLog.log(clazz, Level.SEVERE, message, ex);
   }

   public static void logSevere(Class<?> clazz, String message, boolean popup) {
      AWorkbench.popup("ERROR", message);
      OseeLog.log(clazz, Level.SEVERE, message);
   }

   public static void logException(Class<?> clazz, Exception ex, boolean popup) {
      String message = ex.getLocalizedMessage() == null ? ex.getClass().getName() : ex.getLocalizedMessage();
      logException(clazz, message, ex, popup);
   }
}