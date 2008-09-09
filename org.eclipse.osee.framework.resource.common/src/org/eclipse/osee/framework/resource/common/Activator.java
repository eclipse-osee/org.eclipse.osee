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
package org.eclipse.osee.framework.resource.common;

import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.resource.common.osgi.ApplicationServerManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   private ServiceRegistration applicationManagerService;
   private ServiceTracker applicationManagerTracker;
   private static Activator instance;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      applicationManagerService =
            context.registerService(IApplicationServerManager.class.getName(), new ApplicationServerManager(), null);

      applicationManagerTracker = new ServiceTracker(context, IApplicationServerManager.class.getName(), null);
      applicationManagerTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      applicationManagerService.unregister();
      applicationManagerService = null;

      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

   public ThreadFactory createNewThreadFactory(String name) {
      return getApplicationServerManager().createNewThreadFactory(name, Thread.NORM_PRIORITY);
   }

   public ThreadFactory createNewThreadFactory(String name, int priority) {
      return getApplicationServerManager().createNewThreadFactory(name, priority);
   }

   public IApplicationServerManager getApplicationServerManager() {
      return (IApplicationServerManager) applicationManagerTracker.getService();
   }
}
