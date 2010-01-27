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
package org.eclipse.osee.ote.ui.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.plugin.core.IWorkbenchUserService;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.OseeFormActivator;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.ui.IOteConsoleService;
import org.eclipse.osee.ote.ui.OteRemoteConsole;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class TestCoreGuiPlugin extends OseeFormActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.ote.ui";

   private static TestCoreGuiPlugin pluginInstance;

   private ServiceRegistration oteConsoleServiceRegistration;
   private ServiceTracker oteClientServiceTracker;
   private ServiceTracker workbenchUserServiceTracker;

   private OteRemoteConsole remoteConsole;
   private IOteConsoleService oteConsoleService;

   public TestCoreGuiPlugin() {
      super();
      pluginInstance = this;
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);

      oteConsoleService = new OteConsoleServiceImpl();
      oteConsoleServiceRegistration =
            context.registerService(IOteConsoleService.class.getName(), oteConsoleService, null);

      workbenchUserServiceTracker = new ServiceTracker(context, IWorkbenchUserService.class.getName(), null);
      workbenchUserServiceTracker.open();

      oteClientServiceTracker = new ServiceTracker(context, IOteClientService.class.getName(), null);
      oteClientServiceTracker.open();

      if (System.getProperty("NO_OTE_REMOTE_CONSOLE") == null) {
    	  remoteConsole = new OteRemoteConsole();
    	  getOteClientService().addConnectionListener(remoteConsole);
      }
      
      if (System.getProperty("NO_OTE_ARTIFACT_BULK_LOAD") == null) {
    	  startOTEArtifactBulkLoad();
      }
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      getOteClientService().removeConnectionListener(remoteConsole);
      if (oteClientServiceTracker != null) {
         oteClientServiceTracker.close();
      }
      if (workbenchUserServiceTracker != null) {
         workbenchUserServiceTracker.close();
      }
      if (oteConsoleServiceRegistration != null) {
         oteConsoleServiceRegistration.unregister();
      }
      pluginInstance = null;
      remoteConsole.close();
      super.stop(context);
   }

   private void startOTEArtifactBulkLoad() {
      Jobs.runInJob("OTE Persistance Bulk Load", new IExceptionableRunnable() {
         @Override
         public IStatus run(IProgressMonitor monitor) throws Exception {
            // Attempt to obtain current workbench User - if service is available
            getWorkbenchUserService().getUser();
            return Status.OK_STATUS;
         }
      }, TestCoreGuiPlugin.class, "org.eclipse.osee.ote.ui", false);
   }

   private IWorkbenchUserService getWorkbenchUserService() throws OseeCoreException {
      IWorkbenchUserService service = null;
      try {
         service = (IWorkbenchUserService) workbenchUserServiceTracker.waitForService(3000);
      } catch (InterruptedException ex) {
         throw new OseeWrappedException(ex);
      }
      return service;
   }

   public static TestCoreGuiPlugin getDefault() {
      return pluginInstance;
   }

   @Override
   protected String getPluginName() {
      return PLUGIN_ID;
   }

   public IOteConsoleService getOteConsoleService() {
      return oteConsoleService;
   }

   public IOteClientService getOteClientService() {
      return (IOteClientService) oteClientServiceTracker.getService();
   }

}