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
package org.eclipse.osee.orcs.db.internal.exchange;

import java.io.File;
import java.util.List;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.db.internal.exchange.handler.StandardOseeDbExportDataProvider;
import org.eclipse.osee.orcs.db.internal.exchange.transform.ExchangeDataProcessor;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;

/**
 * @author Roberto E. Escobar
 */
public class BranchExchangeImpl implements IBranchExchange {

   private final OseeServices oseeServices;

   public BranchExchangeImpl() {
      this.oseeServices = new OseeServices();
   }

   public void setLogger(Log logger) {
      oseeServices.setLogger(logger);
   }

   public void setSystemPreferences(SystemPreferences systemPreferences) {
      oseeServices.setSystemPreferences(systemPreferences);
   }

   public void setResourceManager(IResourceManager resourceService) {
      oseeServices.setResourceService(resourceService);
   }

   public void setResourceLocator(IResourceLocatorManager locatorSservice) {
      oseeServices.setLocatorService(locatorSservice);
   }

   public void setModelingService(IOseeCachingService cachingService) {
      oseeServices.setCachingService(cachingService);
   }

   public void setCachingService(IOseeModelingService modelService) {
      oseeServices.setModelService(modelService);
   }

   public void setDatabaseService(IOseeDatabaseService databaseService) {
      oseeServices.setDatabaseService(databaseService);
   }

   public void setIdentityService(IdentityService identityService) {
      oseeServices.setIdentityService(identityService);
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      oseeServices.setExecutorAdmin(executorAdmin);
   }

   public void start() {
      oseeServices.setIsReady(true);
   }

   public void stop() {
      oseeServices.clear();
   }

   private void checkInitialized() throws OseeCoreException {
      if (!oseeServices.isReady()) {
         throw new OseeStateException("BranchExchangeService was not initialized properly");
      }
   }

   @Override
   public IResourceLocator exportBranch(String exportName, PropertyStore options, List<Integer> branchIds) throws OseeCoreException {
      checkInitialized();
      ExportController controller = new ExportController(oseeServices, exportName, options, branchIds);
      controller.handleTxWork();
      return oseeServices.getResourceLocatorManager().generateResourceLocator(ResourceConstants.EXCHANGE_PROTOCOL, "",
         controller.getExchangeFileName());
   }

   @Override
   public void importBranch(IResourceLocator exportDataLocator, PropertyStore options, List<Integer> branchIds, OperationLogger logger) throws OseeCoreException {
      checkInitialized();
      IOseeExchangeDataProvider exportDataProvider = createExportDataProvider(exportDataLocator);
      ImportController importController =
         new ImportController(oseeServices, exportDataProvider, options, branchIds, logger);
      importController.execute();
   }

   @Override
   public IResourceLocator checkIntegrity(IResourceLocator fileToCheck) throws OseeCoreException {
      checkInitialized();
      IOseeExchangeDataProvider exportDataProvider = createExportDataProvider(fileToCheck);
      ExchangeDataProcessor processor = new ExchangeDataProcessor(exportDataProvider);
      ExchangeIntegrity exchangeIntegrityCheck = new ExchangeIntegrity(oseeServices, exportDataProvider, processor);
      exchangeIntegrityCheck.execute();
      return oseeServices.getResourceLocatorManager().generateResourceLocator(ResourceConstants.EXCHANGE_PROTOCOL, "",
         exchangeIntegrityCheck.getExchangeCheckFileName());
   }

   private IOseeExchangeDataProvider createExportDataProvider(IResourceLocator exportDataLocator) throws OseeCoreException {
      checkInitialized();
      Pair<Boolean, File> result =
         ExchangeUtil.getTempExchangeFile(oseeServices.getExchangeBasePath(), oseeServices.getLogger(),
            exportDataLocator, oseeServices.getResourceManager());
      return new StandardOseeDbExportDataProvider(oseeServices.getExchangeBasePath(), oseeServices.getLogger(),
         result.getSecond(), result.getFirst());
   }
}
