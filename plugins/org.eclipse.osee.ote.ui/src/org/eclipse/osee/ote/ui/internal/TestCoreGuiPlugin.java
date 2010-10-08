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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.plugin.core.IWorkbenchUserService;
import org.eclipse.osee.framework.ui.plugin.workspace.SafeWorkspaceAccess;
import org.eclipse.osee.ote.ui.IOteConsoleService;
import org.eclipse.osee.ote.ui.RemoteConsoleLauncher;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class TestCoreGuiPlugin implements BundleActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.ote.ui";

   private static TestCoreGuiPlugin instance;

   private ServiceRegistration oteConsoleServiceRegistration;
   private ServiceTracker workbenchUserServiceTracker;

   private ServiceTracker workspaceStartTracker;
   private OteConsoleServiceImpl oteConsoleService;
   private BundleContext context;

   @Override
   public void start(final BundleContext context) throws Exception {
      this.context = context;
      instance = this;
      workspaceStartTracker = new ServiceTracker(context, SafeWorkspaceAccess.class.getName(), null) {
         private RemoteConsoleLauncher tracker;

         @Override
         public void removedService(ServiceReference reference, Object service) {
            if (oteConsoleService != null) {
               oteConsoleServiceRegistration.unregister();
               oteConsoleService.close();
               oteConsoleService = null;
            }
            if (tracker != null) {
               tracker.close();
            }
            super.removedService(reference, service);
         }

         @Override
         public void close() {
            if (tracker != null) {
               tracker.close();
            }
            super.close();
         }

         @Override
         public void modifiedService(ServiceReference reference, Object service) {
            // TODO Auto-generated method stub

         }

         @Override
         public Object addingService(ServiceReference reference) {
            oteConsoleService = new OteConsoleServiceImpl();
            oteConsoleServiceRegistration =
               context.registerService(IOteConsoleService.class.getName(), oteConsoleService, null);
            if (System.getProperty("NO_OTE_REMOTE_CONSOLE") == null) {
               tracker = new RemoteConsoleLauncher(oteConsoleService);
               tracker.open(true);
            }
            return super.addingService(reference);
         }
      };
      workspaceStartTracker.open(true);

      workbenchUserServiceTracker = new ServiceTracker(context, IWorkbenchUserService.class.getName(), null);
      workbenchUserServiceTracker.open();

      if (System.getProperty("NO_OTE_ARTIFACT_BULK_LOAD") == null) {
         startOTEArtifactBulkLoad();
      }
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (workbenchUserServiceTracker != null) {
         workbenchUserServiceTracker.close();
      }
      if (oteConsoleServiceRegistration != null) {
         oteConsoleServiceRegistration.unregister();
         oteConsoleService.close();
         oteConsoleService = null;
      }
      workspaceStartTracker.close();
      instance = null;
   }

   private void startOTEArtifactBulkLoad() {
      Operations.executeAsJob(new AbstractOperation("OTE Persistance Bulk Load", PLUGIN_ID) {

         @Override
         protected void doWork(IProgressMonitor monitor) throws Exception {
            if (getWorkbenchUserService() != null) {
               getWorkbenchUserService().getUser();
            }
         }
      }, false);
   }

   private IWorkbenchUserService getWorkbenchUserService() throws OseeCoreException {
      IWorkbenchUserService service = null;
      try {
         service = (IWorkbenchUserService) workbenchUserServiceTracker.waitForService(3000);
      } catch (InterruptedException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return service;
   }

   public static TestCoreGuiPlugin getDefault() {
      return instance;
   }

   public IOteConsoleService getOteConsoleService() {
      return oteConsoleService;
   }

   public static BundleContext getContext() {
      return instance.context;
   }
}