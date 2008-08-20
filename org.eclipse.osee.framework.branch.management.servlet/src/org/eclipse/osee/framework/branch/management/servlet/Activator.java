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
import org.eclipse.osee.framework.branch.management.IBranchExport;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M Finkbeiner
 */
public class Activator implements BundleActivator {

   private HttpServiceTracker httpBranchManagementTracker;
   private HttpServiceTracker httpBranchExportTracker;
   private ServiceTracker branchCreationTracker;
   private ServiceTracker branchExportTracker;

   private static Activator instance;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      httpBranchManagementTracker = new HttpServiceTracker(context, "/branch", BranchManagerServlet.class);
      httpBranchManagementTracker.open();

      httpBranchExportTracker = new HttpServiceTracker(context, "/branch.export", BranchExportServlet.class);
      httpBranchExportTracker.open();

      branchCreationTracker = new ServiceTracker(context, IBranchCreation.class.getName(), null);
      branchCreationTracker.open();

      branchExportTracker = new ServiceTracker(context, IBranchExport.class.getName(), null);
      branchExportTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      httpBranchManagementTracker.close();
      httpBranchExportTracker.close();
      branchCreationTracker.close();
      branchExportTracker.close();
      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

   public IBranchCreation getBranchCreation() {
      return (IBranchCreation) branchCreationTracker.getService();
   }

   public IBranchExport getBranchExport() {
      return (IBranchExport) branchExportTracker.getService();
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
