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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.IBranchCreation;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.branch.management.creation.BranchCreation;
import org.eclipse.osee.framework.branch.management.exchange.BranchExchange;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class InternalBranchActivator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.branch.management";

   private enum TrackerId {
      RESOURCE_LOCATOR,
      RESOURCE_MANAGER,
      BRANCH_EXCHANGE,
      MASTER_SERVICE;
   }

   private static InternalBranchActivator instance;
   private ServiceRegistration serviceRegistration;
   private ServiceRegistration exchangeServiceRegistration;

   private final Map<TrackerId, ServiceTracker> mappedTrackers;

   public InternalBranchActivator() {
      this.mappedTrackers = new HashMap<TrackerId, ServiceTracker>();
   }

   public void start(BundleContext context) throws Exception {
      InternalBranchActivator.instance = this;
      serviceRegistration = context.registerService(IBranchCreation.class.getName(), new BranchCreation(), null);

      exchangeServiceRegistration =
            context.registerService(IBranchExchange.class.getName(), new BranchExchange(), null);

      createServiceTracker(context, IResourceLocatorManager.class, TrackerId.RESOURCE_LOCATOR);
      createServiceTracker(context, IResourceManager.class, TrackerId.RESOURCE_MANAGER);
      createServiceTracker(context, IBranchExchange.class, TrackerId.BRANCH_EXCHANGE);
      createServiceTracker(context, IApplicationServerManager.class, TrackerId.MASTER_SERVICE);
   }

   public void stop(BundleContext context) throws Exception {
      exchangeServiceRegistration.unregister();
      exchangeServiceRegistration = null;

      serviceRegistration.unregister();
      serviceRegistration = null;

      for (ServiceTracker tracker : mappedTrackers.values()) {
         tracker.close();
      }
      mappedTrackers.clear();
      instance = null;

      InternalBranchActivator.instance = null;
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, TrackerId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      mappedTrackers.put(trackerId, tracker);
   }

   public static InternalBranchActivator getInstance() {
      return instance;
   }

   public IBranchExchange getBranchExchange() {
      return getTracker(TrackerId.BRANCH_EXCHANGE, IBranchExchange.class);
   }

   public IResourceManager getResourceManager() {
      return getTracker(TrackerId.RESOURCE_MANAGER, IResourceManager.class);
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return getTracker(TrackerId.RESOURCE_LOCATOR, IResourceLocatorManager.class);
   }

   public IApplicationServerManager getApplicationServerManger() {
      return getTracker(TrackerId.MASTER_SERVICE, IApplicationServerManager.class);
   }

   private <T> T getTracker(TrackerId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }
}
