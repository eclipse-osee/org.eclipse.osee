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
package org.eclipse.osee.framework.search.engine.servlet;

import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
   private static Activator instance;

   private HttpServiceTracker httpTracker;
   private ServiceTracker searchServiceTracker;
   private ServiceTracker taggerServiceTracker;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      searchServiceTracker = new ServiceTracker(context, ISearchEngine.class.getName(), null);
      searchServiceTracker.open();

      taggerServiceTracker = new ServiceTracker(context, ISearchEngineTagger.class.getName(), null);
      taggerServiceTracker.open();

      httpTracker = new HttpServiceTracker(context);
      httpTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      httpTracker.close();
      httpTracker = null;

      searchServiceTracker.close();
      searchServiceTracker = null;

      taggerServiceTracker.close();
      taggerServiceTracker = null;

      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

   public ISearchEngine getSearchEngine() {
      return (ISearchEngine) searchServiceTracker.getService();
   }

   public ISearchEngineTagger getSearchTagger() {
      return (ISearchEngineTagger) taggerServiceTracker.getService();
   }

   private class HttpServiceTracker extends ServiceTracker {
      public HttpServiceTracker(BundleContext context) {
         super(context, HttpService.class.getName(), null);
      }

      public Object addingService(ServiceReference reference) {
         HttpService httpService = (HttpService) context.getService(reference);
         try {
            httpService.registerServlet("/search", new SearchEngineServlet(), null, null);
            System.out.println("Registered servlet '/search'");
         } catch (Exception ex) {
         }
         return httpService;
      }

      public void removedService(ServiceReference reference, Object service) {
         HttpService httpService = (HttpService) service;
         httpService.unregister("/search");
         System.out.println("De-registering servlet '/search'");
         super.removedService(reference, service);
      }
   }

}
