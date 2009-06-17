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
package org.eclipse.osee.ote.ui;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.OseeFormActivator;
import org.eclipse.osee.framework.ui.plugin.util.OseeConsole;
import org.eclipse.osee.ote.service.IOteClientService;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class TestCoreGuiPlugin extends OseeFormActivator {
   private static TestCoreGuiPlugin pluginInstance; // The shared instance.
   public static final String PLUGIN_ID = "org.eclipse.osee.ote.ui";
   private ServiceTracker oteClientServiceTracker;

   private OseeConsole console = null;
   private OteRemoteConsole remoteConsole = null;

   public TestCoreGuiPlugin() {
      super();
      pluginInstance = this;
   }

   private void ensureConsole() {
      if (console == null) {
         console = new OseeConsole("OTE Console");
         console.popup();
      }
   }

   public OseeConsole getConsole() {
      ensureConsole();
      return console;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      getOteClientService().removeConnectionListener(remoteConsole);
      oteClientServiceTracker.close();
      pluginInstance = null;
      remoteConsole.close();
      super.stop(context);
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      oteClientServiceTracker = new ServiceTracker(context, IOteClientService.class.getName(), null);
      oteClientServiceTracker.open();
      remoteConsole = new OteRemoteConsole();
      getOteClientService().addConnectionListener(remoteConsole);

      startOTEArtifactBulkLoad();
   }

   private void startOTEArtifactBulkLoad() {
      Jobs.runInJob("OTE Persistance Bulk Load", new IExceptionableRunnable() {
         @Override
         public IStatus run(IProgressMonitor monitor) throws Exception {
            UserManager.getUser();
            return Status.OK_STATUS;
         }
      }, TestCoreGuiPlugin.class, "org.eclipse.osee.ote.ui", false);
   }

   /**
    * Returns the shared instance.
    */
   public static TestCoreGuiPlugin getDefault() {
      return pluginInstance;
   }

   public boolean runOnEventInDisplayThread() {
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.framework.jdk.core.util.plugin.OseePlugin#getPluginName
    * ()
    */
   @Override
   protected String getPluginName() {
      return PLUGIN_ID;
   }

   public static void log(Level level, String message) {
      log(level, message, null);
   }

   public static void log(Level level, String message, Throwable t) {
      OseeLog.log(TestCoreGuiPlugin.class, level, message, t);
   }

   public IOteClientService getOteClientService() {
      return (IOteClientService) oteClientServiceTracker.getService();
   }

}