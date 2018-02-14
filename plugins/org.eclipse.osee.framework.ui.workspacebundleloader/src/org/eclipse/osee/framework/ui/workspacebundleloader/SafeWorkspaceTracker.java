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
package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.workspace.SafeWorkspaceAccess;
import org.eclipse.osee.framework.ui.workspacebundleloader.internal.Activator;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
@SuppressWarnings("rawtypes")
public class SafeWorkspaceTracker extends ServiceTracker implements IJarChangeListener<WorkspaceStarterNature>, WorkspaceLoader, IWorkbenchListener {

   private JarChangeResourceListener<WorkspaceStarterNature> workspaceListener;
   private SafeWorkspaceAccess service;
   private final WorkspaceBundleLoadCoordinator bundleCoordinator;

   @SuppressWarnings("unchecked")
   public SafeWorkspaceTracker(BundleContext context) {
      super(context, SafeWorkspaceAccess.class.getName(), null);
      bundleCoordinator = new WorkspaceBundleLoadCoordinator(new File(OseeData.getPath().toFile(), "loadedbundles"));
      context.registerService(WorkspaceLoader.class.getName(), this, null);
      PlatformUI.getWorkbench().addWorkbenchListener(this);
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object addingService(ServiceReference reference) {
      service = (SafeWorkspaceAccess) context.getService(reference);
      setupWorkspaceBundleLoadingAfterBenchStartup();
      return super.addingService(reference);
   }

   void setupWorkspaceBundleLoadingAfterBenchStartup() {
      Jobs.runInJob(new PrecompileStartup("Loading Precompiled Libraries", Activator.BUNDLE_ID), false);
   }

   private class PrecompileStartup extends AbstractOperation {
      public PrecompileStartup(String operationName, String pluginId) {
         super(operationName, pluginId);
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         IWorkspace workspace = service.getWorkspace();
         workspaceListener = new JarChangeResourceListener<WorkspaceStarterNature>(WorkspaceStarterNature.NATURE_ID,
            SafeWorkspaceTracker.this);
         try {
            loadBundles(monitor);
         } catch (CoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         workspace.addResourceChangeListener(workspaceListener);

      }
   }

   @Override
   public synchronized void close() {
      IWorkspace workspace = service.getWorkspace();
      workspace.removeResourceChangeListener(workspaceListener);
      super.close();
   }

   @Override
   public void handleBundleAdded(URL url) {
      String urlString = url.toString();
      bundleCoordinator.addBundleToCheck(urlString);
   }

   @Override
   public void handleBundleChanged(URL url) {
      String urlString = url.toString();
      bundleCoordinator.addBundleToCheck(urlString);
   }

   @Override
   public void handleBundleRemoved(URL url) {
      String urlString = url.toString();
      bundleCoordinator.addBundleToCheck(urlString);
   }

   @Override
   public void handlePostChange() {
      // do nothing
   }

   @Override
   public void handleNatureClosed(WorkspaceStarterNature nature) {
      for (URL url : nature.getBundles()) {
         handleBundleRemoved(url);
      }
   }

   @Override
   public void loadBundles(IProgressMonitor monitor) throws CoreException {
      for (WorkspaceStarterNature starterNature : WorkspaceStarterNature.getWorkspaceProjects()) {
         for (URL url : starterNature.getBundles()) {
            try {
               handleBundleAdded(url);
            } catch (Exception ex) {
               OseeLog.log(SafeWorkspaceTracker.class, Level.INFO, ex);
               ex.printStackTrace();
            }
         }
      }
      bundleCoordinator.updateBundles(monitor);
      bundleCoordinator.installLatestBundles(monitor);
   }

   @Override
   public void unloadBundles() {
      bundleCoordinator.uninstallBundles();
   }

   @Override
   public boolean preShutdown(IWorkbench workbench, boolean forced) {
      try {
         bundleCoordinator.uninstallBundles();
      } catch (Throwable th) {
         th.printStackTrace();
      }
      return true;
   }

   @Override
   public void postShutdown(IWorkbench workbench) {
      // do nothing
   }

}
