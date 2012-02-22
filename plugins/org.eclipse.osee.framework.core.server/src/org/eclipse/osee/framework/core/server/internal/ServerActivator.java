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

import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ServerActivator implements BundleActivator {

   private ServiceTracker<IApplicationServerManager, IApplicationServerManager> applicationManagerTracker;

   private static ServerActivator instance;

   @Override
   public void start(BundleContext context) throws Exception {
      instance = this;

      applicationManagerTracker =
         new ServiceTracker<IApplicationServerManager, IApplicationServerManager>(context,
            IApplicationServerManager.class.getName(), null);
      applicationManagerTracker.open();
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (applicationManagerTracker != null) {
         applicationManagerTracker.close();
         applicationManagerTracker = null;
      }
      instance = null;
   }

   public static IApplicationServerManager getApplicationServerManager() {
      return instance.applicationManagerTracker != null ? instance.applicationManagerTracker.getService() : null;
   }
}
