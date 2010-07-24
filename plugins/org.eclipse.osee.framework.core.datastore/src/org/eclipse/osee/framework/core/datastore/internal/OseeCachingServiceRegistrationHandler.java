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

import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.core.server.IApplicationServerLookupProvider;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.translation.IDataTranslationServiceProvider;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class OseeCachingServiceRegistrationHandler extends AbstractTrackingHandler {

   private final static Class<?>[] SERVICE_DEPENDENCIES =
         new Class<?>[] {IOseeDatabaseService.class, IOseeModelFactoryService.class, IDataTranslationService.class,
               IApplicationServerLookup.class, IApplicationServerManager.class};

   private ServiceRegistration factoryRegistration;
   private ServiceRegistration cachingServiceRegistration;

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IOseeCachingServiceFactory factory = createCachingFactoryService(services);
      IOseeCachingService cachingService = factory.createCachingService();

      factoryRegistration = context.registerService(IOseeCachingServiceFactory.class.getName(), factory, null);
      cachingServiceRegistration = context.registerService(IOseeCachingService.class.getName(), cachingService, null);
   }

   @Override
   public void onDeActivate() {
      if (factoryRegistration != null) {
         factoryRegistration.unregister();
      }
      if (cachingServiceRegistration != null) {
         cachingServiceRegistration.unregister();
      }
   }

   private IOseeCachingServiceFactory createCachingFactoryService(Map<Class<?>, Object> services) {
      final IOseeDatabaseService dbService = getService(IOseeDatabaseService.class, services);
      final IOseeModelFactoryService modelService = getService(IOseeModelFactoryService.class, services);
      final IDataTranslationService translationService = getService(IDataTranslationService.class, services);
      final IApplicationServerLookup lookupService = getService(IApplicationServerLookup.class, services);
      final IApplicationServerManager appManager = getService(IApplicationServerManager.class, services);
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

      IDataTranslationServiceProvider txProvider = new IDataTranslationServiceProvider() {

         @Override
         public IDataTranslationService getTranslationService() throws OseeCoreException {
            return translationService;
         }
      };

      IApplicationServerLookupProvider lookupProvider = new IApplicationServerLookupProvider() {

         @Override
         public IApplicationServerLookup getApplicationServerLookupService() throws OseeCoreException {
            return lookupService;
         }
      };
      return new ServerOseeCachingServiceFactory(dbProvider, modelProvider, txProvider, lookupProvider, appManager);
   }
}
