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
package org.eclipse.osee.framework.resource.common.osgi;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.common.Activator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class OseeHttpServiceTracker extends ServiceTracker {

   private String contextName;
   private Class<? extends OseeHttpServlet> servletClass;

   public OseeHttpServiceTracker(BundleContext context, String contextName, Class<? extends OseeHttpServlet> servletClass) {
      super(context, HttpService.class.getName(), null);
      this.contextName = !contextName.startsWith("/") ? "/" + contextName : contextName;
      this.servletClass = servletClass;
   }

   public Object addingService(ServiceReference reference) {
      HttpService httpService = (HttpService) context.getService(reference);
      try {
         OseeHttpServlet servlet =
               (OseeHttpServlet) this.servletClass.getConstructor(new Class[0]).newInstance(new Object[0]);
         httpService.registerServlet(contextName, servlet, null, null);
         ApplicationServerManager serverManager =
               (ApplicationServerManager) Activator.getInstance().getApplicationServerManager();
         serverManager.register(contextName, servlet);
         System.out.println(String.format("Registered servlet '%s'", contextName));
      } catch (Exception ex) {
         OseeLog.log(this.getClass(), Level.SEVERE, ex);
      }
      return httpService;
   }

   public void removedService(ServiceReference reference, Object service) {
      HttpService httpService = (HttpService) service;
      httpService.unregister(contextName);
      ApplicationServerManager serverManager =
            (ApplicationServerManager) Activator.getInstance().getApplicationServerManager();
      serverManager.unregister(contextName);
      System.out.println(String.format("De-registering servlet '%s'", contextName));
      super.removedService(reference, service);
   }
}
