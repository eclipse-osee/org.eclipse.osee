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
package org.eclipse.osee.framework.branch.management.servlet;

import org.eclipse.osee.framework.branch.management.IBranchCreation;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.server.OseeHttpServiceTracker;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M Finkbeiner
 */
public class InternalBranchServletActivator implements BundleActivator {
   private ServiceTracker resourceManagementTracker;
   private ServiceTracker resourceLocatorManagerTracker;
   private OseeHttpServiceTracker httpBranchManagementTracker;
   private OseeHttpServiceTracker httpBranchExportTracker;
   private ServiceTracker branchCreationTracker;
   private ServiceTracker branchExchangeTracker;
   private static InternalBranchServletActivator instance;

   public void start(BundleContext context) throws Exception {
      instance = this;

      resourceManagementTracker = new ServiceTracker(context, IResourceManager.class.getName(), null);
      resourceManagementTracker.open();

      resourceLocatorManagerTracker = new ServiceTracker(context, IResourceLocatorManager.class.getName(), null);
      resourceLocatorManagerTracker.open();

      branchCreationTracker = new ServiceTracker(context, IBranchCreation.class.getName(), null);
      branchCreationTracker.open();

      branchExchangeTracker = new ServiceTracker(context, IBranchExchange.class.getName(), null);
      branchExchangeTracker.open();

      httpBranchManagementTracker =
            new OseeHttpServiceTracker(context, OseeServerContext.BRANCH_CREATION_CONTEXT, BranchManagerServlet.class);
      httpBranchManagementTracker.open();

      httpBranchExportTracker =
            new OseeHttpServiceTracker(context, OseeServerContext.BRANCH_EXCHANGE_CONTEXT, BranchExchangeServlet.class);
      httpBranchExportTracker.open();

   }

   public void stop(BundleContext context) throws Exception {
      httpBranchManagementTracker.close();
      httpBranchManagementTracker = null;

      httpBranchExportTracker.close();
      httpBranchExportTracker = null;

      branchCreationTracker.close();
      branchCreationTracker = null;

      branchExchangeTracker.close();
      branchExchangeTracker = null;

      resourceManagementTracker.close();
      resourceManagementTracker = null;

      resourceLocatorManagerTracker.close();
      resourceLocatorManagerTracker = null;
      instance = null;
   }

   public static InternalBranchServletActivator getInstance() {
      return instance;
   }

   public IBranchCreation getBranchCreation() {
      return (IBranchCreation) branchCreationTracker.getService();
   }

   public IBranchExchange getBranchExchange() {
      return (IBranchExchange) branchExchangeTracker.getService();
   }

   public IResourceManager getResourceManager() {
      return (IResourceManager) resourceManagementTracker.getService();
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return (IResourceLocatorManager) resourceLocatorManagerTracker.getService();
   }
}
