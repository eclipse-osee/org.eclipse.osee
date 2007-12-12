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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.CmdLineArgs;
import org.eclipse.osee.framework.jdk.core.util.Lib;

public class HeadlessEclipseConfigurationFactory extends BaseConfigurationFactory {

   private Handler handler;

   public HeadlessEclipseConfigurationFactory() {

      String[] commandLineArgs = Platform.getCommandLineArgs();
      CmdLineArgs cmdLineArgs = new CmdLineArgs(commandLineArgs);
      String appName = cmdLineArgs.get("-application");

      String logDir = System.getProperty("user.home") + File.separator + "OseeLogs" + File.separator;

      createDir(new File(logDir));

      System.out.println("Log files written to: [ " + logDir + " ]");

      String instanceName = null;
      if (appName != null && appName.length() > 0) {
         instanceName = appName + new Date().getTime();
      } else {
         instanceName = "log" + new Date().getTime();
      }

      createDir(new File(logDir + instanceName));

      try {
         handler =
               new FileHandler(logDir + File.separator + instanceName + File.separator + instanceName + ".log",
                     5000000, 1, true);
      } catch (SecurityException ex) {
         ex.printStackTrace();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void reportError(Throwable th) {
      th.printStackTrace();
   }

   public void reportStatus(String message) {
      System.out.println("Status: " + message);
   }

   public OSEEConfig getOseeConfig() {
      return OSEEConfigGUI.getInstance();
   }

   private static void createDir(File file) {
      boolean created = file.mkdirs();

      if (!Lib.isWindows() && created) {
         Lib.chmod777(file);
      }
   }

   public void reportError(Throwable th, String title, String message, String className) {
      System.out.println(title + " (" + className + ")");
      System.out.println(message);
      th.printStackTrace();
   }

   public Logger getLogger(Class<?> classname) {
      Logger logger = Logger.getLogger(classname.getName());
      if (logger.getHandlers().length == 0) {
         logger.setUseParentHandlers(false);
         logger.addHandler(handler);
         logger.addHandler(new OseeConsoleHandler());
      }
      return logger;
   }
}
