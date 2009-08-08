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
package org.eclipse.osee.framework.core.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.core.server.internal.ApplicationServerManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class CoreServerActivator implements BundleActivator {

   private ServiceTracker applicationManagerTracker;
   private ServiceTracker applicationLookupTracker;
   private ServiceTracker authenticationServiceTracker;
   private ServiceTracker sessionServiceTracker;
   private ServiceTracker scheduledServerTracker;

   private static List<ServiceRegistration> services;
   private static CoreServerActivator instance;

   public void start(BundleContext context) throws Exception {
      instance = this;
      services = new ArrayList<ServiceRegistration>();

      services.add(context.registerService(IApplicationServerManager.class.getName(), new ApplicationServerManager(),
            null));

      applicationManagerTracker = new ServiceTracker(context, IApplicationServerManager.class.getName(), null);
      applicationManagerTracker.open();

      applicationLookupTracker = new ServiceTracker(context, IApplicationServerLookup.class.getName(), null);
      applicationLookupTracker.open();

      authenticationServiceTracker = new ServiceTracker(context, IAuthenticationManager.class.getName(), null);
      authenticationServiceTracker.open();

      sessionServiceTracker = new ServiceTracker(context, ISessionManager.class.getName(), null);
      sessionServiceTracker.open();

      scheduledServerTracker = new ServiceTracker(context, IServerTaskScheduler.class.getName(), null);
      scheduledServerTracker.open();
   }

   public void stop(BundleContext context) throws Exception {
      if (applicationManagerTracker != null) {
         IApplicationServerManager manager = getApplicationServerManager();
         if (manager != null) {
            manager.setServletRequestsAllowed(false);
         }
      }

      if (sessionServiceTracker != null) {
         ISessionManager sessionManager = getSessionManager();
         if (sessionManager != null) {
            sessionManager.shutdown();
         }
      }
      if (applicationManagerTracker != null) {
         getApplicationServerManager().shutdown();
         applicationManagerTracker.close();
         applicationManagerTracker = null;
      }

      if (applicationLookupTracker != null) {
         applicationLookupTracker.close();
         applicationLookupTracker = null;
      }

      if (authenticationServiceTracker != null) {
         authenticationServiceTracker.close();
         authenticationServiceTracker = null;
      }

      if (sessionServiceTracker != null) {
         sessionServiceTracker.close();
         sessionServiceTracker = null;
      }

      if (scheduledServerTracker != null) {
         scheduledServerTracker.close();
         scheduledServerTracker = null;
      }

      for (ServiceRegistration service : services) {
         service.unregister();
      }
      services.clear();
      instance = null;
   }

   public static IAuthenticationManager getAuthenticationManager() {
      if (instance.authenticationServiceTracker != null) {
         return (IAuthenticationManager) instance.authenticationServiceTracker.getService();
      }
      return null;
   }

   public static ISessionManager getSessionManager() {
      if (instance != null && instance.sessionServiceTracker != null) {
         return (ISessionManager) instance.sessionServiceTracker.getService();
      }
      return null;
   }

   public static ThreadFactory createNewThreadFactory(String name) {
      return getApplicationServerManager().createNewThreadFactory(name, Thread.NORM_PRIORITY);
   }

   public static ThreadFactory createNewThreadFactory(String name, int priority) {
      return getApplicationServerManager().createNewThreadFactory(name, priority);
   }

   public static IApplicationServerManager getApplicationServerManager() {
      if (instance != null && instance.applicationManagerTracker != null) {
         return (IApplicationServerManager) instance.applicationManagerTracker.getService();
      }
      return null;
   }

   public static IApplicationServerLookup getApplicationServerLookup() {
      if (instance != null && instance.applicationLookupTracker != null) {
         return (IApplicationServerLookup) instance.applicationLookupTracker.getService();
      }
      return null;
   }
}
