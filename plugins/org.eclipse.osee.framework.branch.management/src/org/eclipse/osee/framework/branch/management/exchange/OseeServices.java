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
package org.eclipse.osee.framework.branch.management.exchange;

import org.eclipse.osee.framework.core.message.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;

public class OseeServices {

   private final IResourceManager resourceService;
   private final IResourceLocatorManager locatorSservice;
   private final IOseeCachingService cachingService;
   private final IOseeModelingService modelService;
   private final IOseeDatabaseService databaseService;

   public OseeServices(IResourceManager resourceService, IResourceLocatorManager locatorSservice, IOseeCachingService cachingService, IOseeModelingService modelService, IOseeDatabaseService databaseService) {
      super();
      this.resourceService = resourceService;
      this.locatorSservice = locatorSservice;
      this.cachingService = cachingService;
      this.modelService = modelService;
      this.databaseService = databaseService;
   }

   public IResourceManager getResourceManager() {
      return resourceService;
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return locatorSservice;
   }

   public IOseeCachingService getCachingService() {
      return cachingService;
   }

   public IOseeModelingService getModelingService() {
      return modelService;
   }

   public IOseeDatabaseService getDatabaseService() {
      return databaseService;
   }
}
