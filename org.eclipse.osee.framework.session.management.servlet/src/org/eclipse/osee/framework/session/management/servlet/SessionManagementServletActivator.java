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
package org.eclipse.osee.framework.session.management.servlet;

import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeHttpServiceTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class SessionManagementServletActivator implements BundleActivator {

   private static SessionManagementServletActivator instance;
   private OseeHttpServiceTracker httpServiceTracker;
   private OseeHttpServiceTracker httpServiceTracker1;
   private ServiceTracker serviceTracker;
   private ServiceTracker authenticationServiceTracker;

   public void start(BundleContext context) throws Exception {
      instance = this;

      serviceTracker = new ServiceTracker(context, ISessionManager.class.getName(), null);
      serviceTracker.open();

      authenticationServiceTracker = new ServiceTracker(context, IAuthenticationManager.class.getName(), null);
      authenticationServiceTracker.open();

      httpServiceTracker =
            new OseeHttpServiceTracker(context, OseeServerContext.SESSION_CONTEXT, SessionManagementServlet.class);
      httpServiceTracker.open();

      httpServiceTracker1 =
            new OseeHttpServiceTracker(context, OseeServerContext.CLIENT_LOOPBACK_CONTEXT,
                  SessionClientLoopbackServlet.class);
      httpServiceTracker1.open();
   }

   public void stop(BundleContext context) throws Exception {
      if (httpServiceTracker != null) {
         httpServiceTracker.close();
         httpServiceTracker = null;
      }

      if (httpServiceTracker1 != null) {
         httpServiceTracker1.close();
         httpServiceTracker1 = null;
      }

      if (serviceTracker != null) {
         serviceTracker.close();
         serviceTracker = null;
      }

      if (authenticationServiceTracker != null) {
         authenticationServiceTracker.close();
         authenticationServiceTracker = null;
      }

      instance = null;
   }

   public static ISessionManager getSessionManager() {
      return (ISessionManager) instance.serviceTracker.getService();
   }

   public static IAuthenticationManager getAuthenticationManager() {
      return (IAuthenticationManager) instance.authenticationServiceTracker.getService();
   }

}
