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
package org.eclipse.osee.framework.ui.plugin;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class OseePluginUiActivator extends OseeUiActivator {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(OseePluginUiActivator.class);

   // The plug-in ID
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.ui.plugin";

   // The shared instance
   private static OseePluginUiActivator plugin;

   /**
    * The constructor
    */
   public OseePluginUiActivator() {
      plugin = this;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      super.start(context);
      OseeLog.registerLoggerListener(new EclipseErrorLogLogger());

      if (PlatformUI.isWorkbenchRunning()) {
         IWorkbench workbench = PlatformUI.getWorkbench();
         workbench.addWorkbenchListener(new IWorkbenchListener() {

            @Override
            public void postShutdown(IWorkbench workbench) {
            }

            @Override
            public boolean preShutdown(IWorkbench workbench, boolean forced) {
               try {
                  if (Lib.isWindows()) {
                     String clearCache = OseeInfo.getValue("clear_cache");
                     if (Boolean.parseBoolean(clearCache)) {
                        Location location = Platform.getInstallLocation();
                        URL url = FileLocator.toFileURL(location.getURL());
                        File file = new File(url.getFile());
                        File cache =
                              new File(new File(new File(file, "p2"), "org.eclipse.equinox.p2.metadata.repository"),
                                    "cache");
                        File[] files = cache.listFiles();
                        for (File toDelete : files) {
                           toDelete.delete();
                        }
                     }
                  }
               } catch (Throwable th) {

               }
               return true;
            }

         });
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      plugin = null;
      super.stop(context);
   }

   /**
    * Returns the shared instance
    * 
    * @return the shared instance
    */
   public static OseePluginUiActivator getInstance() {
      return plugin;
   }

   /**
    * @return the logger
    */
   public static Logger getLogger() {
      return logger;
   }
}