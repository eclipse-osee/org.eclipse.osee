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
package org.eclipse.osee.framework.branch.management.exchange;

import java.io.File;
import java.util.List;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.branch.management.exchange.handler.StandardOseeDbExportDataProvider;
import org.eclipse.osee.framework.branch.management.exchange.resource.ExchangeLocatorProvider;
import org.eclipse.osee.framework.branch.management.exchange.transform.ExchangeDataProcessor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class BranchExchange implements IBranchExchange {

   private final OseeServices oseeServices;

   public BranchExchange(OseeServices oseeServices) {
      this.oseeServices = oseeServices;
   }

   @Override
   public IResourceLocator exportBranch(String exportName, Options options, List<Integer> branchIds) throws OseeCoreException {
      ExportController controller = new ExportController(oseeServices, exportName, options, branchIds);
      controller.execute();
      return oseeServices.getResourceLocatorManager().generateResourceLocator(ExchangeLocatorProvider.PROTOCOL, "",
         controller.getExchangeFileName());
   }

   @Override
   public void importBranch(IResourceLocator exportDataLocator, Options options, List<Integer> branchIds) throws OseeCoreException {
      IOseeExchangeDataProvider exportDataProvider = createExportDataProvider(exportDataLocator);
      ImportController importController = new ImportController(oseeServices, exportDataProvider, options, branchIds);
      importController.execute();
   }

   @Override
   public IResourceLocator checkIntegrity(IResourceLocator fileToCheck) throws OseeCoreException {
      IOseeExchangeDataProvider exportDataProvider = createExportDataProvider(fileToCheck);
      ExchangeDataProcessor processor = new ExchangeDataProcessor(exportDataProvider);
      ExchangeIntegrity exchangeIntegrityCheck = new ExchangeIntegrity(oseeServices, exportDataProvider, processor);
      exchangeIntegrityCheck.execute();
      return oseeServices.getResourceLocatorManager().generateResourceLocator(ExchangeLocatorProvider.PROTOCOL, "",
         exchangeIntegrityCheck.getExchangeCheckFileName());
   }

   private IOseeExchangeDataProvider createExportDataProvider(IResourceLocator exportDataLocator) throws OseeCoreException {
      Pair<Boolean, File> result =
         ExchangeUtil.getTempExchangeFile(exportDataLocator, oseeServices.getResourceManager());
      return new StandardOseeDbExportDataProvider(result.getSecond(), result.getFirst());
   }
}
