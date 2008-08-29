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
package org.eclipse.osee.framework.server.admin;

import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   private static Activator instance;
   private ServiceTracker resourceManagementTracker;
   private ServiceTracker resourceLocatorManagerTracker;
   private ServiceTracker searchTaggerTracker;
   private ServiceTracker searchEngineTracker;
   private ServiceTracker branchExchangeTracker;

   /*
    * (non-Javadoc)
    * 
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;
      resourceManagementTracker = new ServiceTracker(context, IResourceManager.class.getName(), null);
      resourceManagementTracker.open();

      resourceLocatorManagerTracker = new ServiceTracker(context, IResourceLocatorManager.class.getName(), null);
      resourceLocatorManagerTracker.open();

      searchTaggerTracker = new ServiceTracker(context, ISearchEngineTagger.class.getName(), null);
      searchTaggerTracker.open();

      searchEngineTracker = new ServiceTracker(context, ISearchEngine.class.getName(), null);
      searchEngineTracker.open();

      branchExchangeTracker = new ServiceTracker(context, IBranchExchange.class.getName(), null);
      branchExchangeTracker.open();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      resourceManagementTracker.close();
      resourceManagementTracker = null;

      resourceLocatorManagerTracker.close();
      resourceLocatorManagerTracker = null;

      searchTaggerTracker.close();
      searchTaggerTracker = null;

      searchEngineTracker.close();
      searchEngineTracker = null;

      branchExchangeTracker.close();
      branchExchangeTracker = null;
   }

   public IResourceManager getResourceManager() {
      return (IResourceManager) resourceManagementTracker.getService();
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return (IResourceLocatorManager) resourceLocatorManagerTracker.getService();
   }

   public ISearchEngineTagger getSearchTagger() {
      return (ISearchEngineTagger) searchTaggerTracker.getService();
   }

   public ISearchEngine getSearchEngine() {
      return (ISearchEngine) searchEngineTracker.getService();
   }

   public IBranchExchange getBranchExchange() {
      return (IBranchExchange) branchExchangeTracker.getService();
   }

   public static Activator getInstance() {
      return Activator.instance;
   }
}
