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
package org.eclipse.osee.framework.core.datastore.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.core.datastore";

   private final List<ServiceDependencyTracker> services;

   public Activator() {
      this.services = new ArrayList<ServiceDependencyTracker>();
   }

   @Override
   public void start(BundleContext context) throws Exception {
      createService(context, new OseeCachingServiceRegistrationHandler());
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      for (ServiceDependencyTracker service : services) {
         service.close();
      }
      services.clear();
   }

   private void createService(BundleContext context, AbstractTrackingHandler handler) {
      ServiceDependencyTracker service = new ServiceDependencyTracker(context, handler);
      services.add(service);
      service.open();
   }
}
