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
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.ui.plugin.workspace.SafeWorkspaceAccess;
import org.eclipse.osee.framework.ui.workspacebundleloader.internal.Activator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
public class SafeWorkspaceTracker extends ServiceTracker implements IJarChangeListener<WorkspaceStarterNature>, WorkspaceLoader, IWorkbenchListener {

   private JarChangeResourceListener<WorkspaceStarterNature> workspaceListener;
   private SafeWorkspaceAccess service;
   private final WorkspaceBundleLoadCoordinator bundleCoordinator;

   public SafeWorkspaceTracker(BundleContext context) {
      super(context, SafeWorkspaceAccess.class.getName(), null);
      bundleCoordinator = new WorkspaceBundleLoadCoordinator(new File(OseeData.getPath().toFile(), "loadedbundles"));
      context.registerService(WorkspaceLoader.class.getName(), this, null);
      PlatformUI.getWorkbench().addWorkbenchListener(this);
   }

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
         workspaceListener =
            new JarChangeResourceListener<WorkspaceStarterNature>(WorkspaceStarterNature.NATURE_ID,
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
      SubMonitor master = SubMonitor.convert(monitor, 100);
      bundleCoordinator.updateBundles(master.newChild(10));
      bundleCoordinator.installLatestBundles(master.newChild(90));
   }

   @Override
   public void unloadBundles() {
      bundleCoordinator.uninstallBundles();
   }

   @Override
   public boolean preShutdown(IWorkbench workbench, boolean forced) {
      JobComplete jobComplete = new JobComplete();
      IOperation operation = new PrecompileShutdown("Closing Precompiled Libraries", Activator.BUNDLE_ID);
      Operations.executeAsJob(operation, false, Job.INTERACTIVE, jobComplete);
      for (int i = 0; i < 100 && !jobComplete.isDone(); i++) {//wait up to 10 seconds or until job completion
         yieldDisplay100ms();
      }
      return true;
   }

   private static class JobComplete extends JobChangeAdapter {
      private volatile boolean done = false;

      public boolean isDone() {
         return done;
      }

      @Override
      public void done(IJobChangeEvent event) {
         done = true;
      }
   }

   private class PrecompileShutdown extends AbstractOperation {
      public PrecompileShutdown(String operationName, String pluginId) {
         super(operationName, pluginId);
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         bundleCoordinator.uninstallBundles();
      }
   }

   @Override
   public void postShutdown(IWorkbench workbench) {
   }

   /**
    * This method gives UI updates that happen from bundle unloading to happen before the workbench shuts down, view
    * shutdown and extension point registry based UI updates. If we don't do this we get unexpected errors in logView
    * and other places.
    */
   private void yieldDisplay100ms() {
      while (Display.getCurrent().readAndDispatch()) {
      }
      try {
         Thread.sleep(50);
      } catch (InterruptedException e) {
      }
      while (Display.getCurrent().readAndDispatch()) {
      }
      try {
         Thread.sleep(50);
      } catch (InterruptedException e) {
      }
      while (Display.getCurrent().readAndDispatch()) {
      }
   }
}
