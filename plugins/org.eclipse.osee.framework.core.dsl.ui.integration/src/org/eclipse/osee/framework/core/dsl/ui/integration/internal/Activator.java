/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.core.dsl.ui.integration";

   private static Activator instance;

   private ServiceTracker cacheServiceTracker;
   private ServiceDependencyTracker dependencyTracker;

   @Override
   public void start(BundleContext context) throws Exception {
      Activator.instance = this;

      dependencyTracker = new ServiceDependencyTracker(context, new ArtifactDataProviderServiceRegHandler());
      dependencyTracker.open();

      cacheServiceTracker = new ServiceTracker(context, IOseeCachingService.class.getName(), null);
      cacheServiceTracker.open(true);
   }

   @Override
   public void stop(BundleContext bundleContext) throws Exception {
      Lib.close(dependencyTracker);

      if (cacheServiceTracker != null) {
         cacheServiceTracker.close();
      }
   }

   public static IOseeCachingService getOseeCacheService() {
      return (IOseeCachingService) Activator.instance.cacheServiceTracker.getService();
   }
}
