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
import java.util.logging.Logger;

public class NonEclipseConfigurationFactory extends BaseConfigurationFactory {

   private Handler handler;

   public NonEclipseConfigurationFactory() {
      handler = new OseeConsoleHandler();
   }

   public void reportError(Throwable th) {
      th.printStackTrace();
   }

   public void reportStatus(String message) {
      System.out.println("Status: " + message);
   }

   public OSEEConfig getOseeConfig() {
      return OSEEConfig.getInstance();
   }

   public void reportError(Throwable th, String title, String message, String className) {
      System.out.println(title + " (" + className + ")");
      System.out.println(message);
      th.printStackTrace();
   }

   public Logger getLogger(Class<?> classname) {
      Logger logger = Logger.getLogger(classname.getName());
      if (logger.getHandlers().length < 1) {
         logger.setUseParentHandlers(false);
         logger.addHandler(handler);
      }
      return logger;
   }
}
