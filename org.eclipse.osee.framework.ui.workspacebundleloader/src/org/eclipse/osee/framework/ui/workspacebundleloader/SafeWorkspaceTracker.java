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

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.workspace.SafeWorkspaceAccess;
import org.eclipse.osee.framework.ui.workspacebundleloader.internal.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
public class SafeWorkspaceTracker extends ServiceTracker implements IJarChangeListener<WorkspaceStarterNature> {

   private Map<String, Bundle> installedBundles;
   private Map<String, Bundle> runningBundles;
   private Collection<Bundle> stoppedBundles;
   private JarChangeResourceListener workspaceListener;
   private SafeWorkspaceAccess service;

   /**
    * @param context
    * @param filter
    * @param customizer
    */
   public SafeWorkspaceTracker(BundleContext context) {
      super(context, SafeWorkspaceAccess.class.getName(), null);
      this.installedBundles = new HashMap<String, Bundle>();
      this.runningBundles = new HashMap<String, Bundle>();
      this.stoppedBundles = new LinkedList<Bundle>();
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
    */
   @Override
   public Object addingService(ServiceReference reference) {
      service = (SafeWorkspaceAccess) context.getService(reference);
      setupWorkspaceBundleLoadingAfterBenchStartup();
      return super.addingService(reference);
   }

   void setupWorkspaceBundleLoadingAfterBenchStartup() {
      IWorkspace workspace = service.getWorkspace();

      this.workspaceListener =
            new JarChangeResourceListener<WorkspaceStarterNature>(WorkspaceStarterNature.NATURE_ID, this);
      try {
         installWorkspacePlugins();
      } catch (CoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
      } catch (BundleException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
      }
      workspace.addResourceChangeListener((JarChangeResourceListener) workspaceListener);
   }

   public synchronized void close() {
      IWorkspace workspace = service.getWorkspace();
      cleanupHandledBundles();
      workspace.removeResourceChangeListener(workspaceListener);
      super.close();
   }

   /**
    * 
    */
   private void cleanupHandledBundles() {
      for (Bundle bundle : installedBundles.values()) {
         try {
            bundle.uninstall();
         } catch (BundleException ex) {
         }
      }
      for (Bundle bundle : runningBundles.values()) {
         try {
            bundle.stop();
            bundle.uninstall();
         } catch (BundleException ex) {
         }
      }
      for (Bundle bundle : stoppedBundles) {
         try {
            bundle.uninstall();
         } catch (BundleException ex) {
         }
      }
   }

   /**
    * @throws CoreException
    * @throws CoreException
    * @throws BundleException
    * @throws BundleException
    */
   private void installWorkspacePlugins() throws CoreException, BundleException {

      for (WorkspaceStarterNature starterNature : WorkspaceStarterNature.getWorkspaceProjects()) {
         for (URL url : starterNature.getBundles()) {
            try {
               handleBundleAdded(url);
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }
      }

      transitionInstalledPlugins();
   }

   /**
    * @param url
    * @throws BundleException
    */
   @Override
   public void handleBundleAdded(URL url) {
      try {
         String urlString = url.toString();
         installedBundles.put(urlString, context.installBundle(urlString));
      } catch (BundleException ex) {
         // TODO
      }
   }

   /**
    * @param stoppedBundles
    * @param url
    * @throws BundleException
    */
   @Override
   public void handleBundleChanged(URL url) {
      try {
         String urlString = url.toString();

         // Check to see if this is the first we've seen this
         if (runningBundles.containsKey(urlString)) {
            Bundle bundle = runningBundles.get(urlString);
            System.out.println("\tUpdating plugin " + bundle.getSymbolicName());

            bundle.update();
         } else {
            handleBundleAdded(url);
         }
      } catch (BundleException ex) {
         // TODO
      }
   }

   /**
    * @param stoppedBundles
    * @param url
    * @throws BundleException
    */
   @Override
   public void handleBundleRemoved(URL url) {
      try {
         String urlString = url.toString();
         if (runningBundles.containsKey(urlString)) {
            Bundle bundle = runningBundles.get(urlString);
            System.out.println("\tStopping plugin " + bundle.getSymbolicName());

            bundle.stop();
            runningBundles.remove(urlString);
            stoppedBundles.add(bundle);
         }
      } catch (BundleException ex) {
         // TODO
      }
   }

   /**
    * @throws BundleException
    */
   private void transitionInstalledPlugins() throws BundleException {
      Iterator<String> iter = installedBundles.keySet().iterator();
      while (iter.hasNext()) {
         String urlString = iter.next();
         Bundle bundle = installedBundles.get(urlString);
         try {
            bundle.start();

            iter.remove();
            runningBundles.put(urlString, bundle);
         } catch (Throwable th) {
            OseeLog.log(Activator.class, Level.SEVERE, th);
         }
      }
   }

   /**
    * @throws BundleException
    */
   private void transitionStoppedBundles() throws BundleException {
      Iterator<Bundle> iter = stoppedBundles.iterator();
      while (iter.hasNext()) {
         Bundle bundle = iter.next();
         try {
            bundle.uninstall();

            iter.remove();
         } catch (Throwable th) {
            OseeLog.log(Activator.class, Level.SEVERE, th);
         }
      }
   }

   @Override
   public void handlePostChange() {
      try {
         transitionInstalledPlugins();
         transitionStoppedBundles();
      } catch (BundleException ex) {
         // TODO
      }
   }

   @Override
   public void handleNatureClosed(WorkspaceStarterNature nature) {
      closeAllPlugins(nature);
   }

   /**
    * @param project
    * @throws CoreException
    * @throws BundleException
    */
   private void closeAllPlugins(WorkspaceStarterNature nature) {
      for (URL url : nature.getBundles()) {
         handleBundleRemoved(url);
      }

      try {
         transitionStoppedBundles();
      } catch (BundleException ex) {
         // TODO
      }
   }
}
