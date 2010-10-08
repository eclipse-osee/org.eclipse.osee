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
package org.eclipse.osee.ote.ui.test.manager.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.ui.IOteConsoleService;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class TestManagerPlugin extends OseeUiActivator {
   private static TestManagerPlugin pluginInstance; // The shared instance.
   public static final String PLUGIN_ID = "org.eclipse.osee.ote.ui.test.manager";

   private static String username = null;

   private ServiceTracker oteClientServiceTracker;
   private ServiceTracker oteConsoleServiceTracker;

   /**
    * Returns the shared instance.
    */
   public static TestManagerPlugin getInstance() {
      return pluginInstance;
   }

   public static String getUsername() {
      if (username == null) {
         username = java.lang.System.getProperty("user.name");
         username = username.replaceAll("b", "");
         if (username.length() == 6) {
            username = "0" + username;
         }
      }
      return username;
   }

   /**
    * The constructor.
    */
   public TestManagerPlugin() {
      super(PLUGIN_ID);
      pluginInstance = this;
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      oteClientServiceTracker = new ServiceTracker(context, IOteClientService.class.getName(), null);
      oteClientServiceTracker.open();

      oteConsoleServiceTracker = new ServiceTracker(context, IOteConsoleService.class.getName(), null);
      oteConsoleServiceTracker.open();
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (oteConsoleServiceTracker != null) {
         oteConsoleServiceTracker.close();
      }
      if (oteClientServiceTracker != null) {
         oteClientServiceTracker.close();
      }
      super.stop(context);
   }

   public IOteClientService getOteClientService() {
      return (IOteClientService) oteClientServiceTracker.getService();
   }

   public IOteConsoleService getOteConsoleService() {
      return (IOteConsoleService) oteConsoleServiceTracker.getService();
   }

   public static void log(Level level, String message, Throwable t) {
      OseeLog.log(TestManagerPlugin.class, level, message, t);
   }

   public static void log(Level level, String message) {
      log(level, message, null);
   }
}