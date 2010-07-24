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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.internal.session.SessionManagerTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class ServerActivator implements BundleActivator {

   private ServiceTracker applicationManagerTracker;
   private ServiceTracker sessionServiceTracker;

   private static List<ServiceRegistration> services;
   private static ServerActivator instance;
   private ApplicationServerManager serverManager;

   private ServiceDependencyTracker serviceDependencyTracker;

   @Override
   public void start(BundleContext context) throws Exception {
      instance = this;

      serviceDependencyTracker = new ServiceDependencyTracker(context, new SessionManagerTrackingHandler());
      serviceDependencyTracker.open();

      services = new ArrayList<ServiceRegistration>();

      services.add(context.registerService(IDatabaseInfoProvider.class.getName(), new ServerDatabaseProvider(), null));

      serverManager = new ApplicationServerManager();
      services.add(context.registerService(IApplicationServerManager.class.getName(), serverManager, null));

      applicationManagerTracker = new ServiceTracker(context, IApplicationServerManager.class.getName(), null);
      applicationManagerTracker.open();

      sessionServiceTracker = new ServiceTracker(context, ISessionManager.class.getName(), null);
      sessionServiceTracker.open();

   }

   @Override
   public void stop(BundleContext context) throws Exception {
      Lib.close(serviceDependencyTracker);

      if (applicationManagerTracker != null) {
         IApplicationServerManager manager = getApplicationServerManager();
         if (manager != null) {
            manager.setServletRequestsAllowed(false);
         }
      }

      if (sessionServiceTracker != null) {
         sessionServiceTracker.close();
         sessionServiceTracker = null;
      }

      if (applicationManagerTracker != null) {
         getApplicationServerManager().shutdown();
         applicationManagerTracker.close();
         applicationManagerTracker = null;
      }

      if (sessionServiceTracker != null) {
         sessionServiceTracker.close();
         sessionServiceTracker = null;
      }

      for (ServiceRegistration service : services) {
         service.unregister();
      }
      services.clear();
      instance = null;
   }

   public static ISessionManager getSessionManager() {
      if (instance != null && instance.sessionServiceTracker != null) {
         return (ISessionManager) instance.sessionServiceTracker.getService();
      }
      return null;
   }

   public static IApplicationServerManager getApplicationServerManager() {
      return instance != null ? instance.serverManager : null;
   }
}
