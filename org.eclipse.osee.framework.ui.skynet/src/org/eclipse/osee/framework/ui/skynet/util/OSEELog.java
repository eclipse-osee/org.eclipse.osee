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
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class OSEELog {

   public static void logException(Class<?> clazz, String str, Exception ex, boolean popup) {
      Logger logger = ConfigUtil.getConfigFactory().getLogger(clazz);
      if (popup && PlatformUI.isWorkbenchRunning()) {
         if (ex == null)
            AWorkbench.popup("ERROR", str);
         else
            AWorkbench.popup("ERROR", (str == null ? "" : str + "\n\n") + ex.getLocalizedMessage());
      }
      if (ex != null)
         if (str == null || str.equals(""))
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         else
            logger.log(Level.SEVERE, str, ex);
      else
         logger.log(Level.SEVERE, str);
   }

   public static void logSevere(Class<?> clazz, String str, boolean popup) {
      logException(clazz, str, new IllegalStateException(str), popup);
   }

   public static void logInfo(Class<?> clazz, String str, boolean popup) {
      Logger logger = ConfigUtil.getConfigFactory().getLogger(clazz);
      if (popup) AWorkbench.popup("Info", str);
      logger.log(Level.INFO, str);
   }

   public static void logWarning(Class<?> clazz, String str, boolean popup) {
      logWarning(clazz, str, null, popup);
   }

   public static void logWarning(Class<?> clazz, Exception ex, boolean popup) {
      logWarning(clazz, null, ex, popup);
   }

   public static void logWarning(Class<?> clazz, String str, Exception ex, boolean popup) {
      Logger logger = ConfigUtil.getConfigFactory().getLogger(clazz);
      if (popup) AWorkbench.popup("Warning", str);
      if (ex != null)
         if (str == null || str.equals(""))
            logger.log(Level.WARNING, ex.getLocalizedMessage(), ex);
         else
            logger.log(Level.WARNING, str, ex);
      else
         logger.log(Level.WARNING, str);
   }

   public static void logException(Class<?> clazz, Exception ex, boolean popup) {
      logException(clazz, null, ex, popup);
   }
}