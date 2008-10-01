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

import org.eclipse.osee.framework.jdk.core.util.OseeApplicationServerContext;
import org.eclipse.osee.framework.resource.common.osgi.OseeHttpServiceTracker;
import org.eclipse.osee.framework.session.management.ISessionManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class SessionManagementServletActivator implements BundleActivator {

   private static SessionManagementServletActivator instance;
   private OseeHttpServiceTracker httpServiceTracker;
   private ServiceTracker serviceTracker;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      serviceTracker = new ServiceTracker(context, ISessionManager.class.getName(), null);
      serviceTracker.open();

      httpServiceTracker =
            new OseeHttpServiceTracker(context, OseeApplicationServerContext.SESSION_CONTEXT,
                  SessionManagementServlet.class);
      httpServiceTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      httpServiceTracker.close();
      httpServiceTracker = null;

      serviceTracker.close();
      serviceTracker = null;

      instance = null;
   }

   public static SessionManagementServletActivator getInstance() {
      return instance;
   }

   public ISessionManager getSessionManager() {
      return (ISessionManager) serviceTracker.getService();
   }

}
