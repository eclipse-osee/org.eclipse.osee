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
package org.eclipse.osee.framework.branch.management.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.branch.management.internal.trackers.OseeBranchExchangeServiceRegistrationHandler;
import org.eclipse.osee.framework.branch.management.internal.trackers.OseeBranchServiceRegistrationHandler;
import org.eclipse.osee.framework.branch.management.internal.trackers.OseeCachingServiceRegistrationHandler;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.branch.management";

   private final List<ServiceDependencyTracker> services;

   public Activator() {
      this.services = new ArrayList<ServiceDependencyTracker>();
   }

   public void start(BundleContext context) throws Exception {
      createService(context, new OseeCachingServiceRegistrationHandler());
      createService(context, new OseeBranchServiceRegistrationHandler());
      createService(context, new OseeBranchExchangeServiceRegistrationHandler());
   }

   private void createService(BundleContext context, AbstractTrackingHandler handler) {
      ServiceDependencyTracker service = new ServiceDependencyTracker(context, handler);
      services.add(service);
      service.open();
   }

   public void stop(BundleContext context) throws Exception {
      for (ServiceDependencyTracker service : services) {
         service.close();
      }
      services.clear();
   }

}