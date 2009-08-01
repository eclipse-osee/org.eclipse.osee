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
package org.eclipse.osee.framework.search.engine.internal;

import java.net.URL;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProviderManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   private static Activator instance;

   private ServiceTracker attributeTaggerProviderTracker;
   private ServiceTracker resourceManagementTracker;
   private ServiceTracker resourceLocatorManagerTracker;
   private BundleContext context;

   public void start(BundleContext context) throws Exception {
      instance = this;
      this.context = context;

      resourceLocatorManagerTracker = new ServiceTracker(context, IResourceLocatorManager.class.getName(), null);
      resourceLocatorManagerTracker.open();

      resourceManagementTracker = new ServiceTracker(context, IResourceManager.class.getName(), null);
      resourceManagementTracker.open();

      attributeTaggerProviderTracker =
            new ServiceTracker(context, IAttributeTaggerProviderManager.class.getName(), null);
      attributeTaggerProviderTracker.open();
   }

   public void stop(BundleContext context) throws Exception {
      attributeTaggerProviderTracker.close();
      attributeTaggerProviderTracker = null;

      resourceManagementTracker.close();
      resourceManagementTracker = null;

      resourceLocatorManagerTracker.close();
      resourceLocatorManagerTracker = null;

      instance = null;
      context = null;
   }

   public static IResourceManager getResourceManager() {
      return (IResourceManager) instance.resourceManagementTracker.getService();
   }

   public static IResourceLocatorManager getResourceLocatorManager() {
      return (IResourceLocatorManager) instance.resourceLocatorManagerTracker.getService();
   }

   public static IAttributeTaggerProviderManager getTaggerManager() {
      return (IAttributeTaggerProviderManager) instance.attributeTaggerProviderTracker.getService();
   }

   public static URL getResource(String path) {
      return instance.context.getBundle().getResource(path);
   }
}
