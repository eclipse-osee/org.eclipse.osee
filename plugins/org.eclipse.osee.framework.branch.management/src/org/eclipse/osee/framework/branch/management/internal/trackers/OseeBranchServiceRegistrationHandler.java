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
import org.eclipse.osee.framework.branch.management.internal.OseeBranchService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.services.IOseeBranchService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class OseeBranchServiceRegistrationHandler extends AbstractTrackingHandler {

   private final static Class<?>[] SERVICE_DEPENDENCIES =
         new Class<?>[] {IOseeDatabaseService.class, IOseeModelFactoryService.class, IOseeCachingService.class};

   private ServiceRegistration serviceRegistration;

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IOseeBranchService branchService = createBranchService(services);
      serviceRegistration = context.registerService(IOseeBranchService.class.getName(), branchService, null);
   }

   @Override
   public void onDeActivate() {
      if (serviceRegistration != null) {
         serviceRegistration.unregister();
      }
   }

   private IOseeBranchService createBranchService(Map<Class<?>, Object> services) {
      final IOseeDatabaseService dbService = getService(IOseeDatabaseService.class, services);
      final IOseeModelFactoryService modelService = getService(IOseeModelFactoryService.class, services);
      final IOseeCachingService cachingService = getService(IOseeCachingService.class, services);

      IOseeDatabaseServiceProvider dbProvider = new IOseeDatabaseServiceProvider() {
         @Override
         public IOseeDatabaseService getOseeDatabaseService() throws OseeDataStoreException {
            return dbService;
         }
      };

      IOseeModelFactoryServiceProvider modelProvider = new IOseeModelFactoryServiceProvider() {

         @Override
         public IOseeModelFactoryService getOseeFactoryService() throws OseeCoreException {
            return modelService;
         }
      };

      IOseeCachingServiceProvider cachingProvider = new IOseeCachingServiceProvider() {

         @Override
         public IOseeCachingService getOseeCachingService() throws OseeCoreException {
            return cachingService;
         }
      };

      return new OseeBranchService(dbProvider, cachingProvider, modelProvider);
   }
}
