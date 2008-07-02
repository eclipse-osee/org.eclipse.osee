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
package org.eclipse.osee.framework.search.engine;

import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.search.engine.internal.SearchEngine;
import org.eclipse.osee.framework.search.engine.internal.SearchEngineTagger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   private static Activator instance;
   private ServiceRegistration searchServiceRegistration;
   private ServiceRegistration taggerServiceRegistration;

   private ServiceTracker resourceManagementTracker;
   private ServiceTracker resourceLocatorManagerTracker;
   private BundleContext context;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      Activator.instance = this;
      this.context = context;
      resourceLocatorManagerTracker = new ServiceTracker(context, IResourceLocatorManager.class.getName(), null);
      resourceLocatorManagerTracker.open();

      resourceManagementTracker = new ServiceTracker(context, IResourceManager.class.getName(), null);
      resourceManagementTracker.open();

      searchServiceRegistration = context.registerService(ISearchEngine.class.getName(), new SearchEngine(), null);

      taggerServiceRegistration =
            context.registerService(ISearchTagger.class.getName(), new SearchEngineTagger(), null);
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      searchServiceRegistration.unregister();
      searchServiceRegistration = null;

      taggerServiceRegistration.unregister();
      taggerServiceRegistration = null;

      resourceManagementTracker.close();
      resourceManagementTracker = null;

      resourceLocatorManagerTracker.close();
      resourceLocatorManagerTracker = null;

      instance = null;
      context = null;
   }

   public IResourceManager getResourceManager() {
      return (IResourceManager) resourceManagementTracker.getService();
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return (IResourceLocatorManager) resourceLocatorManagerTracker.getService();
   }

   public static Activator getInstance() {
      return instance;
   }

   public BundleContext getContext() {
      return context;
   }
}
