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
package org.eclipse.osee.framework.artifact.servlet;

import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.server.OseeHttpServiceTracker;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class Activator implements BundleActivator {

   private static Activator instance;

   private OseeHttpServiceTracker httpTracker;
   private OseeHttpServiceTracker httpTracker1;
   private ServiceTracker resourceManagementTracker;
   private ServiceTracker resourceLocatorManagerTracker;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      resourceLocatorManagerTracker = new ServiceTracker(context, IResourceLocatorManager.class.getName(), null);
      resourceLocatorManagerTracker.open();

      resourceManagementTracker = new ServiceTracker(context, IResourceManager.class.getName(), null);
      resourceManagementTracker.open();

      httpTracker = new OseeHttpServiceTracker(context, OseeServerContext.PROCESS_CONTEXT, ArtifactFileServlet.class);
      httpTracker.open();

      httpTracker1 = new OseeHttpServiceTracker(context, OseeServerContext.ARTIFACT_CONTEXT, ArtifactFileServlet.class);
      httpTracker1.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      if (httpTracker != null) {
         httpTracker.close();
         httpTracker = null;
      }

      if (httpTracker1 != null) {
         httpTracker1.close();
         httpTracker1 = null;
      }

      if (resourceManagementTracker != null) {
         resourceManagementTracker.close();
         resourceManagementTracker = null;
      }

      if (resourceLocatorManagerTracker != null) {
         resourceLocatorManagerTracker.close();
         resourceLocatorManagerTracker = null;
      }
      instance = null;
   }

   public static IResourceManager getResourceManager() {
      return (IResourceManager) instance.resourceManagementTracker.getService();
   }

   public static IResourceLocatorManager getResourceLocatorManager() {
      return (IResourceLocatorManager) instance.resourceLocatorManagerTracker.getService();
   }
}
