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
package org.eclipse.osee.framework.core.server.internal;

import java.util.logging.Level;

import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class InternalHttpServiceTracker extends ServiceTracker {

   private final String contextName;
   private final OseeHttpServlet servlet;

   public InternalHttpServiceTracker(BundleContext context, String contextName, OseeHttpServlet servlet) {
      super(context, HttpService.class.getName(), null);
      this.contextName = !contextName.startsWith("/") ? "/" + contextName : contextName;
      this.servlet = servlet;
   }

   @Override
   public Object addingService(ServiceReference reference) {
      HttpService httpService = (HttpService) context.getService(reference);
      try {
         httpService.registerServlet(contextName, servlet, null, null);
         ApplicationServerManager serverManager =
               (ApplicationServerManager) CoreServerActivator.getApplicationServerManager();
         serverManager.register(contextName, servlet);
         System.out.println(String.format("Registered servlet '%s'", contextName));
      } catch (Exception ex) {
         OseeLog.log(this.getClass(), Level.SEVERE, ex);
      }
      return httpService;
   }

   @Override
   public void removedService(ServiceReference reference, Object service) {
      HttpService httpService = (HttpService) service;
      httpService.unregister(contextName);
      ApplicationServerManager serverManager =
            (ApplicationServerManager) CoreServerActivator.getApplicationServerManager();
      serverManager.unregister(contextName);
      System.out.println(String.format("De-registering servlet '%s'", contextName));
      super.removedService(reference, service);
   }
}
