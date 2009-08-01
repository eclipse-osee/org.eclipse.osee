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
package org.eclipse.osee.framework.manager.servlet;

import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeHttpServiceTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Donald G. Dunne
 */
public class InternalSystemManagerServletActivator implements BundleActivator {
   private OseeHttpServiceTracker httpBranchManagementTracker;
   private ServiceTracker managerTracker;
   private static InternalSystemManagerServletActivator instance;

   public void start(BundleContext context) throws Exception {
      instance = this;

      managerTracker = new ServiceTracker(context, ISessionManager.class.getName(), null);
      managerTracker.open();

      httpBranchManagementTracker =
            new OseeHttpServiceTracker(context, OseeServerContext.MANAGER_CONTEXT, SystemManagerServlet.class);
      httpBranchManagementTracker.open();

   }

   public void stop(BundleContext context) throws Exception {
      if (httpBranchManagementTracker != null) {
         httpBranchManagementTracker.close();
         httpBranchManagementTracker = null;
      }

      if (managerTracker != null) {
         managerTracker.close();
         managerTracker = null;
      }

      instance = null;
   }

   public static InternalSystemManagerServletActivator getInstance() {
      return instance;
   }

   public static ISessionManager getSessionManager() {
      return (ISessionManager) instance.managerTracker.getService();
   }

}
