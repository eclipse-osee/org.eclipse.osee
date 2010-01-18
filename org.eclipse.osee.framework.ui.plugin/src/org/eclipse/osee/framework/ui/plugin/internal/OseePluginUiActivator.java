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
package org.eclipse.osee.framework.ui.plugin.internal;

import java.io.File;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.IActionReportingService;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * a The activator class controls the plug-in life cycle
 */
public class OseePluginUiActivator extends OseeUiActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.ui.plugin";

   private static OseePluginUiActivator plugin;
   private ServiceTracker tracker;

   public OseePluginUiActivator() {
      plugin = this;
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);

      tracker = new ServiceTracker(context, IActionReportingService.class.getName(), null);
      tracker.open();

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

                        Lib.deleteContents(new File(new File(file, "configuration"), "org.eclipse.osgi"));
                     }
                  }
               } catch (Throwable th) {

               }
               return true;
            }

         });
      }
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      if (tracker != null) {
         tracker.close();
      }
      tracker = null;
      plugin = null;
      context = null;
   }

   public static OseePluginUiActivator getInstance() {
      return plugin;
   }

   public IActionReportingService getActionReportingService() throws OseeCoreException {
      try {
         return (IActionReportingService) tracker.waitForService(3000);
      } catch (InterruptedException ex) {
         throw new OseeWrappedException(ex);
      }
   }
}