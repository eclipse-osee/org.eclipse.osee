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
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M Finkbeiner
 */
public class Activator implements BundleActivator {

   private HttpServiceTracker httpTracker;
   private ServiceTracker branchCreationTracker;
   private static Activator instance;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      httpTracker = new HttpServiceTracker(context);
      httpTracker.open();

      branchCreationTracker = new ServiceTracker(context, IBranchCreation.class.getName(), null);
      branchCreationTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      httpTracker.close();
      branchCreationTracker.close();
      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

   public IBranchCreation getBranchCreation() {
      return (IBranchCreation) branchCreationTracker.getService();
   }

   private class HttpServiceTracker extends ServiceTracker {
      public HttpServiceTracker(BundleContext context) {
         super(context, HttpService.class.getName(), null);
      }

      public Object addingService(ServiceReference reference) {
         HttpService httpService = (HttpService) context.getService(reference);
         try {
            httpService.registerServlet("/branch", new BranchManagerServlet(), null, null);
            System.out.println("Registered servlet '/branch'");
         } catch (Exception ex) {
         }
         return httpService;
      }

      public void removedService(ServiceReference reference, Object service) {
         HttpService httpService = (HttpService) service;
         httpService.unregister("/branch");
         System.out.println("De-registering servlet '/branch'");
         super.removedService(reference, service);
      }
   }
}
