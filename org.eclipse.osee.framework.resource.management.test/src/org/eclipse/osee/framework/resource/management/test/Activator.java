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
package org.eclipse.osee.framework.resource.management.test;

import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   ServiceTracker resourceManagementTracker;
   ServiceTracker resourceLocatorManagerTracker;
   private static Activator me;

   public void start(BundleContext context) throws Exception {
      me = this;
      resourceManagementTracker = new ServiceTracker(context, IResourceManager.class.getName(), null);
      resourceManagementTracker.open();
      resourceLocatorManagerTracker = new ServiceTracker(context, IResourceLocatorManager.class.getName(), null);
      resourceLocatorManagerTracker.open();
   }

   public void stop(BundleContext context) throws Exception {
      me = null;
      resourceManagementTracker.close();
      resourceLocatorManagerTracker.close();
   }

   public static Activator getActivator() {
      return me;
   }

   public IResourceManager getResourceManager() {
      return (IResourceManager) resourceManagementTracker.getService();
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return (IResourceLocatorManager) resourceLocatorManagerTracker.getService();
   }

}
