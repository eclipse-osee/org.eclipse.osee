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
package org.eclipse.osee.framework.branch.management.internal;

import org.eclipse.osee.framework.branch.management.IBranchCreation;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.branch.management.exchange.BranchExchange;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class InternalBranchActivator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.branch.management";

   private static InternalBranchActivator instance;
   private ServiceRegistration serviceRegistration;
   private ServiceRegistration exchangeServiceRegistration;
   private ServiceTracker resourceManagementTracker;
   private ServiceTracker resourceLocatorManagerTracker;
   private ServiceTracker branchExchangeTracker;
   private ServiceTracker applicationServerManagerTracker;
   private BundleContext context;

   public void start(BundleContext context) throws Exception {
      InternalBranchActivator.instance = this;
      this.context = context;
      serviceRegistration = context.registerService(IBranchCreation.class.getName(), new BranchCreation(), null);

      exchangeServiceRegistration =
            context.registerService(IBranchExchange.class.getName(), new BranchExchange(), null);

      resourceLocatorManagerTracker = new ServiceTracker(context, IResourceLocatorManager.class.getName(), null);
      resourceLocatorManagerTracker.open();

      resourceManagementTracker = new ServiceTracker(context, IResourceManager.class.getName(), null);
      resourceManagementTracker.open();

      branchExchangeTracker = new ServiceTracker(context, IBranchExchange.class.getName(), null);
      branchExchangeTracker.open();

      applicationServerManagerTracker = new ServiceTracker(context, IApplicationServerManager.class.getName(), null);
      applicationServerManagerTracker.open();
   }

   public void stop(BundleContext context) throws Exception {
      exchangeServiceRegistration.unregister();
      exchangeServiceRegistration = null;

      serviceRegistration.unregister();
      serviceRegistration = null;

      resourceManagementTracker.close();
      resourceManagementTracker = null;

      resourceLocatorManagerTracker.close();
      resourceLocatorManagerTracker = null;

      branchExchangeTracker.close();
      branchExchangeTracker = null;

      applicationServerManagerTracker.close();
      applicationServerManagerTracker = null;

      InternalBranchActivator.instance = null;
   }

   public static IBranchExchange getBranchExchange() {
      return (IBranchExchange) instance.branchExchangeTracker.getService();
   }

   public static IResourceManager getResourceManager() {
      return (IResourceManager) instance.resourceManagementTracker.getService();
   }

   public static IResourceLocatorManager getResourceLocatorManager() {
      return (IResourceLocatorManager) instance.resourceLocatorManagerTracker.getService();
   }

   public static IApplicationServerManager getApplicationServerManger() {
      return (IApplicationServerManager) instance.applicationServerManagerTracker.getService();
   }

   public static BundleContext getBundleContext() {
      return instance.context;
   }
}
