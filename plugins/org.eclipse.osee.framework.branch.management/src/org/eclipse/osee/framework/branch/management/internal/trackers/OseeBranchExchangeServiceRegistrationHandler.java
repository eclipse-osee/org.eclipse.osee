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
package org.eclipse.osee.framework.branch.management.internal.trackers;

import java.util.Map;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.branch.management.exchange.BranchExchange;
import org.eclipse.osee.framework.branch.management.exchange.OseeServices;
import org.eclipse.osee.framework.core.message.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class OseeBranchExchangeServiceRegistrationHandler extends AbstractTrackingHandler {

   private final static Class<?>[] SERVICE_DEPENDENCIES =
         new Class<?>[] {IResourceManager.class, IResourceLocatorManager.class, IOseeModelingService.class,
               IOseeCachingService.class, IOseeDatabaseService.class};

   private ServiceRegistration serviceRegistration;

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IBranchExchange branchExchangeService = createBranchExchangeService(services);
      serviceRegistration = context.registerService(IBranchExchange.class.getName(), branchExchangeService, null);
   }

   @Override
   public void onDeActivate() {
      if (serviceRegistration != null) {
         serviceRegistration.unregister();
      }
   }

   private IBranchExchange createBranchExchangeService(Map<Class<?>, Object> services) {
      IResourceManager resourceManager = getService(IResourceManager.class, services);
      IResourceLocatorManager resourceLocatorManager = getService(IResourceLocatorManager.class, services);
      IOseeModelingService modelingService = getService(IOseeModelingService.class, services);
      IOseeCachingService cachingService = getService(IOseeCachingService.class, services);
      IOseeDatabaseService databaseService = getService(IOseeDatabaseService.class, services);
      OseeServices oseeServices =
            new OseeServices(resourceManager, resourceLocatorManager, cachingService, modelingService, databaseService);
      return new BranchExchange(oseeServices);
   }
}
