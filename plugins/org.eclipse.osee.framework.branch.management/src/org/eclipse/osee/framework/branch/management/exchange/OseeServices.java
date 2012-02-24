/****************u***************************************************************
 * Copyright (c) 2ll010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.exchange;

import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.branch.management.exchange.resource.ExchangeProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.SystemPreferences;

public class OseeServices {

   private Log logger;
   private SystemPreferences systemPreferences;
   private IResourceManager resourceService;
   private IResourceLocatorManager locatorService;
   private IOseeCachingService cachingService;
   private IOseeModelingService modelService;
   private IOseeDatabaseService databaseService;
   private IdentityService identityService;
   private ExecutorAdmin executorAdmin;
   private volatile boolean isReady;

   public OseeServices() {
      super();
   }

   public boolean isReady() {
      return isReady;
   }

   public synchronized void setIsReady(boolean isReady) {
      this.isReady = isReady;
   }

   public Log getLogger() {
      return logger;
   }

   public String getExchangeBasePath() throws OseeCoreException {
      return ExchangeProvider.getExchangeDataPath(systemPreferences);
   }

   public IResourceManager getResourceManager() {
      return resourceService;
   }

   public IResourceLocatorManager getResourceLocatorManager() {
      return locatorService;
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

   public IdentityService getIdentityService() {
      return identityService;
   }

   public ExecutorAdmin getExecutorAdmin() {
      return executorAdmin;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setResourceService(IResourceManager resourceService) {
      this.resourceService = resourceService;
   }

   public void setLocatorService(IResourceLocatorManager locatorService) {
      this.locatorService = locatorService;
   }

   public void setCachingService(IOseeCachingService cachingService) {
      this.cachingService = cachingService;
   }

   public void setModelService(IOseeModelingService modelService) {
      this.modelService = modelService;
   }

   public void setDatabaseService(IOseeDatabaseService databaseService) {
      this.databaseService = databaseService;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void setSystemPreferences(SystemPreferences systemPreferences) {
      this.systemPreferences = systemPreferences;
   }

   public void clear() {
      setIsReady(false);
      setLogger(null);
      setSystemPreferences(null);
      setResourceService(null);
      setLocatorService(null);
      setCachingService(null);
      setModelService(null);
      setDatabaseService(null);
      setIdentityService(null);
      setExecutorAdmin(null);
   }

}
