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
package org.eclipse.osee.framework.types.bridge.internal;

import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class Activator extends OseeUiActivator implements IOseeModelFactoryServiceProvider {

   // The plug-in ID
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.types.bridge";

   // The shared instance
   private static Activator plugin;

   private ServiceTracker cacheServiceTracker;
   private ServiceTracker factoryTracker;

   /**
    * The constructor
    */
   public Activator() {
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
    */
   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      plugin = this;

      factoryTracker = new ServiceTracker(context, IOseeModelFactoryService.class.getName(), null);
      factoryTracker.open();

      cacheServiceTracker = new ServiceTracker(context, IOseeCachingService.class.getName(), null);
      cacheServiceTracker.open();

   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
    */
   @Override
   public void stop(BundleContext context) throws Exception {
      plugin = null;
      cacheServiceTracker.close();
      factoryTracker.close();
      super.stop(context);
   }

   /**
    * Returns the shared instance
    * 
    * @return the shared instance
    */
   public static Activator getDefault() {
      return plugin;
   }

   public IOseeCachingService getOseeCacheService() {
      return (IOseeCachingService) cacheServiceTracker.getService();
   }

   public IOseeModelFactoryService getOseeFactoryService() {
      return (IOseeModelFactoryService) factoryTracker.getService();
   }
}
