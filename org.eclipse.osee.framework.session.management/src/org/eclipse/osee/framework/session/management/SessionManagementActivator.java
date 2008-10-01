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
package org.eclipse.osee.framework.session.management;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class SessionManagementActivator implements BundleActivator {

   private static SessionManagementActivator instance = null;
   private ServiceTracker serviceTracker;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      serviceTracker = new ServiceTracker(context, IAuthenticationManager.class.getName(), null);
      serviceTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      serviceTracker.close();
      serviceTracker = null;

      instance = null;
   }

   public static IAuthenticationManager getAuthenticationManager() {
      return (IAuthenticationManager) instance.serviceTracker.getService();
   }
}
