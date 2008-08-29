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

import javax.servlet.Servlet;
import org.eclipse.osee.framework.branch.management.IBranchCreation;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M Finkbeiner
 */
public class Activator implements BundleActivator {
   private ServiceTracker resourceManagementTracker;
   private ServiceTracker resourceLocatorManagerTracker;
   private HttpServiceTracker httpBranchManagementTracker;
   private HttpServiceTracker httpBranchExportTracker;
   private ServiceTracker branchCreationTracker;
   private ServiceTracker branchExchangeTracker;
   private static Activator instance;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
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

      httpBranchManagementTracker = new HttpServiceTracker(context, "/branch", BranchManagerServlet.class);
      httpBranchManagementTracker.open();

      httpBranchExportTracker = new HttpServiceTracker(context, "/branch.exchange", BranchExchangeServlet.class);
      httpBranchExportTracker.open();

   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
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

   public static Activator getInstance() {
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

   private class HttpServiceTracker extends ServiceTracker {
      private String contextName;
      private Class<? extends Servlet> servletClass;

      public HttpServiceTracker(BundleContext context, String contextName, Class<? extends Servlet> servletClass) {
         super(context, HttpService.class.getName(), null);
         this.contextName = contextName;
         this.servletClass = servletClass;
      }

      public Object addingService(ServiceReference reference) {
         HttpService httpService = (HttpService) context.getService(reference);
         try {
            Servlet servlet = (Servlet) this.servletClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            httpService.registerServlet(contextName, servlet, null, null);
            System.out.println(String.format("Registered servlet '%s'", contextName));
         } catch (Exception ex) {
         }
         return httpService;
      }

      public void removedService(ServiceReference reference, Object service) {
         HttpService httpService = (HttpService) service;
         httpService.unregister(contextName);
         System.out.println(String.format("De-registering servlet '%s'", contextName));
         super.removedService(reference, service);
      }
   }
}
