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

import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.core.server.internal.ApplicationServerLookup;
import org.eclipse.osee.framework.core.server.internal.ApplicationServerManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class CoreServerActivator implements BundleActivator {

   private ServiceRegistration applicationManagerService;
   private ServiceRegistration applicationLookupService;
   private ServiceTracker applicationManagerTracker;
   private ServiceTracker applicationLookupTracker;
   private static CoreServerActivator instance;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      applicationManagerService =
            context.registerService(IApplicationServerManager.class.getName(), new ApplicationServerManager(), null);

      applicationLookupService =
            context.registerService(IApplicationServerLookup.class.getName(), new ApplicationServerLookup(), null);

      applicationManagerTracker = new ServiceTracker(context, IApplicationServerManager.class.getName(), null);
      applicationManagerTracker.open();

      applicationLookupTracker = new ServiceTracker(context, IApplicationServerLookup.class.getName(), null);
      applicationLookupTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {

      if (applicationManagerTracker != null) {
         getApplicationServerManager().shutdown();
         applicationManagerTracker.close();
         applicationManagerTracker = null;
      }

      if (applicationLookupTracker != null) {
         applicationLookupTracker.close();
         applicationLookupTracker = null;
      }

      if (applicationLookupService != null) {
         applicationLookupService.unregister();
         applicationLookupService = null;
      }

      if (applicationManagerService != null) {
         applicationManagerService.unregister();
         applicationManagerService = null;
      }
      instance = null;
   }

   private static CoreServerActivator getInstance() {
      return instance;
   }

   public static ThreadFactory createNewThreadFactory(String name) {
      return getApplicationServerManager().createNewThreadFactory(name, Thread.NORM_PRIORITY);
   }

   public static ThreadFactory createNewThreadFactory(String name, int priority) {
      return getApplicationServerManager().createNewThreadFactory(name, priority);
   }

   public static IApplicationServerManager getApplicationServerManager() {
      return (IApplicationServerManager) getInstance().applicationManagerTracker.getService();
   }

   public static IApplicationServerLookup getApplicationServerLookup() {
      return (IApplicationServerLookup) getInstance().applicationLookupTracker.getService();
   }
}
